package leshy.mushrooms.map.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.domain.usecase.BackfillWalkThumbnailsUseCase
import leshy.mushrooms.map.domain.usecase.DeleteWalkUseCase
import leshy.mushrooms.map.domain.util.decimateTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

/** См. `ui/components/WalkRouteThumbnail.kt`, `ROUTE_POINT_BUDGET` — число и причина там же. */
private const val ARCHIVE_TRACK_POINT_BUDGET = 400

private data class RawArchiveData(
    val walks: List<Walk>,
    val trackPoints: List<TrackPoint>,
    val marks: List<FieldMark>,
)

class ArchiveViewModel(
    private val walkRepository: WalkRepository,
    private val trackPointRepository: TrackPointRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val backfillWalkThumbnails: BackfillWalkThumbnailsUseCase,
    private val deleteWalk: DeleteWalkUseCase,
) : ViewModel() {

    // UI-only flags, combined with the Room-backed item list in a second combine() below — kept
    // separate so a new DB emission (e.g. after a delete) can't silently reset them. See
    // presentation/CLAUDE.md.
    private val selectedWalkIds = MutableStateFlow<Set<Long>>(emptySet())
    private val showDeleteConfirmation = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(ArchiveUiState())
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    private var thumbnailBackfillJob: Job? = null

    init {
        viewModelScope.launch {
            val itemsFlow = combine(
                walkRepository.observeAll(),
                trackPointRepository.observeAll(),
                fieldMarkRepository.observeAll(),
            ) { walks, trackPoints, marks -> RawArchiveData(walks, trackPoints, marks) }
                .map(::buildItems)
                // Сборка — на фоновом потоке, и это не «на всякий случай». `observeAll()` у точек
                // трека — это ВСЕ точки ВСЕХ прогулок (порядка тысячи на прогулку), а `buildItems`
                // перегруппировывает их целиком на каждую эмиссию. Без `flowOn` всё это считалось
                // в контексте сборщика, то есть на главном потоке.
                //
                // Что превращает это из «дороговато» в зависание: дорисовка снимков
                // (`BackfillWalkThumbnailsUseCase`) пишет `thumbnailPath` ПО ОДНОЙ прогулке, и
                // каждая такая запись заново поднимает `walkRepository.observeAll()` — то есть
                // после импорта архива полная перегруппировка всех точек случается столько раз,
                // сколько приехало прогулок. Репорт владельца 2026-09-26: архив после импорта
                // намертво замирал и несколько раз закончился падением.
                .flowOn(Dispatchers.Default)

            combine(itemsFlow, selectedWalkIds, showDeleteConfirmation) { items, selected, showConfirm ->
                // Drops IDs for walks no longer present (e.g. deleted from another screen) so a
                // stale selection can't linger or reopen the delete button with nothing to delete.
                val validSelected = selected.intersect(items.map { it.walk.id }.toSet())
                ArchiveUiState(
                    items = items,
                    selectedWalkIds = validSelected,
                    showDeleteConfirmation = showConfirm,
                    // Сюда попадают только настоящие данные — сам факт эмиссии и означает, что
                    // база ответила.
                    isLoading = false,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    private fun buildItems(raw: RawArchiveData): List<WalkArchiveItem> {
        val tracksByWalk = raw.trackPoints
            .groupBy(TrackPoint::walkId) { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }
        val findsByWalk = raw.marks
            .filter { it.type == MarkType.MUSHROOM }
            .groupBy(FieldMark::walkId) { GeoPoint(it.lat, it.lon, null, it.timestamp) }

        // raw.walks is already ordered newest-first by the DAO query (ORDER BY startTime DESC).
        return raw.walks.map { walk ->
            val track = tracksByWalk[walk.id].orEmpty()
            WalkArchiveItem(
                walk = walk,
                // Прорежённый трек, а не полный: карточке архива он нужен ровно на силуэт
                // маршрута в 120dp (`WalkRouteThumbnail`), где разницы не видно, — а состояние
                // экрана иначе держит в памяти все точки всех прогулок разом.
                track = decimateTrack(track, stride = trackStrideFor(track.size)),
                findLocations = findsByWalk[walk.id].orEmpty(),
            )
        }
    }

    /** Столько точек на силуэт — тот же потолок, что и у самого `WalkRouteThumbnail`. */
    private fun trackStrideFor(size: Int): Int =
        ceil(size.toFloat() / ARCHIVE_TRACK_POINT_BUDGET).toInt().coerceAtLeast(1)

    /**
     * Дорисовка недостающих снимков карты ([BackfillWalkThumbnailsUseCase]) — на КАЖДОМ входе на
     * экран, а не однажды в `init`.
     *
     * «Архив» — top-level раздел, он открывается через `navigateToTopLevel()`, и его ViewModel
     * переживает переключение разделов (см. `ui/navigation/CLAUDE.md`). Пока проход стоял в
     * `init`, он и выполнялся ровно один раз за жизнь ViewModel: пользователь, заглянувший в
     * архив ДО импорта, возвращался в него после импорта — и приехавшие прогулки не получали
     * снимков уже никогда. Раньше эту дыру затыкал `DataViewModel`, гоняя тот же проход прямо в
     * импорте; оттуда он убран (колёсико импорта не должно ждать сеть — разбор там же), и дыру
     * закрывает вход на экран.
     *
     * Проход независим от потока состояния ниже и список рисовать не задерживает. Повторный вход,
     * пока предыдущий проход ещё идёт, ничего не запускает: очередь и так дойдёт до всех, а второй
     * параллельный проход рисовал бы те же прогулки по второму разу. Неудача (нет сети) не
     * запоминается намеренно — вернулся в архив со связью, и снимки поедут.
     *
     * Битые пути к фото и снимкам чинятся не здесь, а один раз на старте приложения (`App.kt`,
     * `RepairPhotoPathsUseCase`).
     */
    fun onScreenShown() {
        if (thumbnailBackfillJob?.isActive == true) return
        thumbnailBackfillJob = viewModelScope.launch { backfillWalkThumbnails() }
    }

    /** Long-press entry point: opens selection mode (if not already open) and selects this walk. */
    fun selectWalk(walkId: Long) {
        selectedWalkIds.update { it + walkId }
    }

    /** Plain-tap entry point while already in selection mode. */
    fun toggleSelection(walkId: Long) {
        selectedWalkIds.update { current -> if (walkId in current) current - walkId else current + walkId }
    }

    /** Back press, navigating away, or dismissing the delete dialog — all leave selection mode. */
    fun clearSelection() {
        selectedWalkIds.value = emptySet()
    }

    fun onDeleteClick() {
        showDeleteConfirmation.value = true
    }

    fun onDeleteDismiss() {
        showDeleteConfirmation.value = false
        selectedWalkIds.value = emptySet()
    }

    fun onDeleteConfirm() {
        viewModelScope.launch {
            showDeleteConfirmation.value = false
            val idsToDelete = selectedWalkIds.value
            // Через [DeleteWalkUseCase], а не голым `walkRepository.delete`: массовое удаление
            // обязано уносить фотографии и миниатюры так же, как удаление одной прогулки с её
            // экрана, — разбор в KDoc use case'а.
            _uiState.value.items
                .filter { it.walk.id in idsToDelete }
                .forEach { deleteWalk(it.walk) }
            selectedWalkIds.value = emptySet()
        }
    }
}
