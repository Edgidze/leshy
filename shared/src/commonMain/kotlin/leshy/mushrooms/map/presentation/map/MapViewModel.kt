package leshy.mushrooms.map.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MapFilter
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.MapFilterRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.domain.usecase.DeletePlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.UpdatePlaceMarkUseCase
import leshy.mushrooms.map.domain.util.computeFilterCount
import leshy.mushrooms.map.domain.util.matchesDateAndSeason
import leshy.mushrooms.map.presentation.archive.CategoryCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class RawMapData(
    val walks: List<Walk>,
    val marks: List<FieldMark>,
    val categories: List<Category>,
)

/** Ключ перезагрузки треков — см. комментарий у его collect в [MapViewModel.init]. */
private data class TracksKey(val walkIds: Set<Long>, val visible: Boolean)

class MapViewModel(
    walkRepository: WalkRepository,
    fieldMarkRepository: FieldMarkRepository,
    private val trackPointRepository: TrackPointRepository,
    categoryRepository: CategoryRepository,
    mapFilterRepository: MapFilterRepository,
    private val updatePlaceMark: UpdatePlaceMarkUseCase,
    private val deletePlaceMark: DeletePlaceMarkUseCase,
) : ViewModel() {

    private val _mode = MutableStateFlow(MapMode.MAP)

    /** Заполняется отдельным сборщиком ниже и входит в общий combine готовым значением — см.
     * комментарий у этого сборщика. */
    private val tracks = MutableStateFlow<Map<Long, List<GeoPoint>>>(emptyMap())

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        // Dangling photo/thumbnail paths are repaired once at app startup (App.kt), not
        // per-screen — see RepairPhotoPathsUseCase.
        viewModelScope.launch {
            val rawData = combine(
                walkRepository.observeAll(),
                fieldMarkRepository.observeAll(),
                categoryRepository.observeAll(),
            ) { walks, marks, categories -> RawMapData(walks, marks, categories) }

            combine(rawData, _mode, mapFilterRepository.observeFilter(), tracks) { raw, mode, filter, tracks ->
                buildUiState(raw, mode, filter, tracks)
            }.collect { state -> _uiState.value = state }
        }
        viewModelScope.launch {
            // Ровно тот же приём, что в RecordViewModel (там же и подробное объяснение): треки
            // читаются одноразовым запросом по ключу «набор показываемых прогулок + флаг
            // показа», а не подпиской на track_points. Здесь это важно не меньше — если
            // прогулка пишется в фоне, пока пользователь смотрит «Карту находок», подписка
            // перечитывала бы и перегруппировывала всю таблицу на каждый GPS-фикс.
            //
            // Прореживания, в отличие от «Записи», нет: тут маршруты — само содержимое экрана,
            // а не фоновый контекст, и их можно рассматривать вблизи.
            combine(
                walkRepository.observeAll(),
                mapFilterRepository.observeFilter(),
            ) { walks, filter ->
                TracksKey(
                    walkIds = walks.filter { it.matchesDateAndSeason(filter) }.map { it.id }.toSet(),
                    visible = filter.showPastRoutes,
                )
            }.distinctUntilChanged().collect { key ->
                tracks.value = if (!key.visible || key.walkIds.isEmpty()) {
                    emptyMap()
                } else {
                    withContext(Dispatchers.Default) {
                        trackPointRepository.getPoints(key.walkIds)
                            .groupBy(TrackPoint::walkId) {
                                GeoPoint(it.lat, it.lon, it.elevation, it.timestamp)
                            }
                    }
                }
            }
        }
    }

    fun selectMode(mode: MapMode) {
        _mode.value = mode
    }

    fun updatePlace(mark: FieldMark, name: String, description: String, photoPath: String?) {
        // No manual _uiState splice needed: fieldMarkRepository.observeAll() above is a live Room
        // Flow, so the update re-emits into uiState.placeMarks on its own once it commits.
        viewModelScope.launch { updatePlaceMark(mark, name, description, photoPath) }
    }

    fun deletePlace(mark: FieldMark) {
        viewModelScope.launch { deletePlaceMark(mark) }
    }

    private fun buildUiState(
        raw: RawMapData,
        mode: MapMode,
        filter: MapFilter,
        // Пустая карта, когда показ прошлых маршрутов выключен (так решает сборщик выше) — это
        // не просто скрытие слоя: подгонка камеры в AggregatedFindsMap иначе продолжала бы
        // кадрировать невидимые треки.
        tracks: Map<Long, List<GeoPoint>>,
    ): MapUiState {
        val filteredWalkIds = raw.walks
            .filter { it.matchesDateAndSeason(filter) }
            .map { it.id }
            .toSet()

        val categoryById = raw.categories.associateBy { it.id }
        val mushroomMarks = raw.marks.filter {
            it.walkId in filteredWalkIds && it.type == MarkType.MUSHROOM && categoryById[it.categoryId]?.isActive == true
        }
        val placeMarks = raw.marks.filter { it.walkId in filteredWalkIds && it.type == MarkType.POI }

        val categoryCounts = mushroomMarks
            .groupingBy { it.categoryId }
            .eachCount()
            .mapNotNull { (categoryId, count) -> categoryById[categoryId]?.let { CategoryCount(it, count) } }
            .sortedBy { it.category.order }

        return MapUiState(
            mode = mode,
            tracks = tracks,
            findMarks = mushroomMarks,
            placeMarks = placeMarks,
            categories = raw.categories,
            stats = MapStats(
                walkCount = filteredWalkIds.size,
                totalDistanceMeters = raw.walks.filter { it.id in filteredWalkIds }.sumOf { it.distanceMeters },
                totalMushroomCount = mushroomMarks.size,
                categoryCounts = categoryCounts,
            ),
            filterCount = computeFilterCount(filter, raw.walks, raw.categories),
            // Сюда попадают только настоящие данные — сам факт вызова и означает, что база
            // ответила.
            isLoading = false,
        )
    }
}
