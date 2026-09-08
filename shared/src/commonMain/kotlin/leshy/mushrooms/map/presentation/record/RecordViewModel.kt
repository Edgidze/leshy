package leshy.mushrooms.map.presentation.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import leshy.mushrooms.map.data.platform.BackgroundRecordingController
import leshy.mushrooms.map.data.platform.LocationTracker
import leshy.mushrooms.map.data.platform.MAX_RECORDING_NOTIFICATION_SPECIES
import leshy.mushrooms.map.data.platform.RecordingCommand
import leshy.mushrooms.map.data.platform.RecordingNotificationSnapshot
import leshy.mushrooms.map.data.platform.RecordingNotificationSpecies
import leshy.mushrooms.map.data.platform.WalkThumbnailRenderer
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MAX_MUSHROOM_FINDS_PER_WALK
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.TrackPoint
import leshy.mushrooms.map.domain.repository.CategoryRepository
import leshy.mushrooms.map.domain.repository.FieldMarkRepository
import leshy.mushrooms.map.domain.repository.MapFilterRepository
import leshy.mushrooms.map.domain.repository.SettingsRepository
import leshy.mushrooms.map.domain.repository.TrackPointRepository
import leshy.mushrooms.map.domain.repository.WalkRepository
import leshy.mushrooms.map.domain.usecase.AddMushroomMarkUseCase
import leshy.mushrooms.map.domain.usecase.AddPlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.CreateOrUpdateUserSpeciesUseCase
import leshy.mushrooms.map.domain.usecase.DeletePlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCategoriesUseCase
import leshy.mushrooms.map.domain.usecase.EnsureDefaultCollectionsUseCase
import leshy.mushrooms.map.domain.usecase.FinishWalkUseCase
import leshy.mushrooms.map.domain.usecase.HealOrphanedWalksUseCase
import leshy.mushrooms.map.domain.usecase.MISC_CATEGORY_NAME_KEY
import leshy.mushrooms.map.domain.usecase.RecalculateFilterEligibilityUseCase
import leshy.mushrooms.map.domain.usecase.RecordTrackPointUseCase
import leshy.mushrooms.map.domain.usecase.RemoveLastMushroomMarkUseCase
import leshy.mushrooms.map.domain.usecase.RenameWalkUseCase
import leshy.mushrooms.map.domain.usecase.StartWalkUseCase
import leshy.mushrooms.map.domain.usecase.UNKNOWN_MUSHROOM_NAME_KEY
import leshy.mushrooms.map.domain.usecase.UpdatePlaceMarkUseCase
import leshy.mushrooms.map.domain.usecase.UpdateWalkThumbnailUseCase
import leshy.mushrooms.map.domain.util.bearingDegrees
import leshy.mushrooms.map.domain.util.computeFilterCount
import leshy.mushrooms.map.domain.util.decimateTrack
import leshy.mushrooms.map.domain.util.hasArrived
import leshy.mushrooms.map.domain.util.haversineMeters
import leshy.mushrooms.map.domain.util.matchesDateAndSeason
import leshy.mushrooms.map.domain.util.turnRecommendation
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.string
import leshy.mushrooms.map.presentation.applyRecencyOrder
import leshy.mushrooms.map.presentation.sortCategories
import leshy.mushrooms.map.ui.util.formatDistanceKm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val TICK_INTERVAL_MILLIS = 1000L
private const val MIN_COURSE_FIX_DISTANCE_METERS = 3.0

/** How long the tile feed must sit idle (no +/-, no manual scroll) before a pending "bring to
 * front" actually reorders the feed — see [RecordViewModel.scheduleFrontBump]. */
private val TILE_REORDER_QUIET_WINDOW = 5.seconds

/** How long [RecordViewModel.flushPendingFrontBumps]'s scroll-to-front should take, so the
 * reorder reads as an observable motion instead of a teleport — see [RecordUiState.scrollToStartDurationMillis]. */
private const val TILE_REORDER_SCROLL_DURATION_MILLIS = 1000

/**
 * Шаг прореживания фоновых линий прошлых маршрутов — обоснование и гарантии см. в
 * [leshy.mushrooms.map.domain.util.decimateTrack]. Экран «Карта находок» прореживания НЕ
 * применяет: там маршруты сами по себе содержимое, а не фоновый контекст.
 */
private const val HISTORICAL_TRACK_STRIDE = 4

/** Ключ перезагрузки фоновых треков — см. комментарий у его collect в [RecordViewModel.init]. */
private data class HistoricalTracksKey(val walkIds: Set<Long>, val visible: Boolean)

private data class RecordFilterState(
    val categories: List<Category>,
    val historicalFinds: List<FieldMark>,
    val historicalPlaces: List<FieldMark>,
    val filterCount: Int,
)

private data class NavigationSourceSnapshot(
    val currentLocation: GeoPoint?,
    val marks: List<FieldMark>,
    val historicalPlaces: List<FieldMark>,
)

@OptIn(ExperimentalCoroutinesApi::class)
class RecordViewModel(
    private val categoryRepository: CategoryRepository,
    private val walkRepository: WalkRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val mapFilterRepository: MapFilterRepository,
    trackPointRepository: TrackPointRepository,
    private val locationTracker: LocationTracker,
    private val backgroundRecordingController: BackgroundRecordingController,
    private val settingsRepository: SettingsRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val startWalk: StartWalkUseCase,
    private val finishWalk: FinishWalkUseCase,
    private val healOrphanedWalks: HealOrphanedWalksUseCase,
    private val renameWalk: RenameWalkUseCase,
    private val recordTrackPoint: RecordTrackPointUseCase,
    private val addMushroomMark: AddMushroomMarkUseCase,
    private val removeLastMushroomMark: RemoveLastMushroomMarkUseCase,
    private val addPlaceMark: AddPlaceMarkUseCase,
    private val updatePlaceMark: UpdatePlaceMarkUseCase,
    private val deletePlaceMark: DeletePlaceMarkUseCase,
    private val walkThumbnailRenderer: WalkThumbnailRenderer,
    private val updateWalkThumbnail: UpdateWalkThumbnailUseCase,
    private val createOrUpdateUserSpecies: CreateOrUpdateUserSpeciesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    /**
     * Elapsed walk time — its own flow rather than a [RecordUiState] field, and that is load-bearing.
     * It changes once a second, while [RecordUiState] travels into `RecordScreenContent` as a single
     * parameter that Compose compares by instance (the class is inferred unstable — it holds
     * `List`/`Map` properties). Folding the tick into it therefore recomposed the entire Record
     * screen every second, rebuilt all four derived marker lists, and made MapLibre re-diff its
     * layers for data that had not changed. Kept separate so the tick can only reach the one `Text`
     * that displays it — see `RecordScreen.kt`'s `ElapsedTimeStat` and the 2026-09-02 section of
     * `.claude/investigations/ios-maplibre-background-watchdog/README.md` for the field measurements
     * (two `wakeups_resource` reports, 159 and 165 thread wakeups/s against a 150/s limit).
     */
    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis.asStateFlow()

    private var walkId: Long? = null
    private var lastPersistedPoint: GeoPoint? = null
    private var trackSequence = 0
    private var tickerJob: Job? = null
    private var currentLanguage = AppLanguage.EN
    private var resetOrderOnWalkFinish = false
    private var freezeOrder = false

    // Most-recently-bumped category ids first — a tile jumps to the front of the feed each time
    // it's added (or picked from search). Not persisted across app restarts; whether it survives
    // past the end of a walk is gated by resetOrderOnWalkFinish (Settings, off by default — see
    // finish()).
    private val categoryOrder = MutableStateFlow<List<Long>>(emptyList())

    // Category ids tapped (+/-) during the current quiet-window countdown, oldest first —
    // flushed into categoryOrder (front-most last-tapped-first) once TILE_REORDER_QUIET_WINDOW
    // passes with no further feed activity. See scheduleFrontBump/notifyTileFeedInteraction.
    private val pendingFrontBumps = mutableListOf<Long>()
    private var frontBumpFlushJob: Job? = null

    // Виды, показанные строками в уведомлении идущей записи, в порядке показа — не более
    // MAX_RECORDING_NOTIFICATION_SPECIES штук. Заполняется началом ленты плиток
    // ([syncNotificationSlots]), дальше живёт заменами на месте ([noteMarked]).
    //
    // Позиции строк здесь НЕ меняются от нажатий — и это главное свойство списка, а не мелочь
    // реализации. Кнопки уведомления жмут с заблокированного экрана, вслепую и часто подряд;
    // список «самый свежий первым» переставлял бы строку прямо из-под пальца, ровно та беда,
    // ради которой на самой ленте плиток заведено окно тишины [TILE_REORDER_QUIET_WINDOW]. На
    // экране от неё спасает то, что плитку видно; на замке — ничто.
    private val notificationSlots = MutableStateFlow<List<Long>>(emptyList())

    // Когда каждый из видов последний раз отмечали в этой прогулке — монотонный счётчик, а не
    // время: сравнивается только порядок. Нужен, когда все слоты заняты, а отметили вид не из
    // них: место уступает наименее свежий, и уступает НА МЕСТЕ, не сдвигая соседей.
    private val slotMarkOrder = mutableMapOf<Long, Long>()
    private var markCounter = 0L

    // Whether the Record screen is currently in front of the user (composed AND resumed). Gates
    // the GPS subscription together with isRecording — see the collector in init().
    private val isRecordScreenResumed = MutableStateFlow(false)

    private val navigationTargetId = MutableStateFlow<Long?>(null)
    private val courseOverGround = MutableStateFlow<Double?>(null)
    private var courseBaselineFix: GeoPoint? = null

    // Runs first, in its own launch rather than queued behind ensureDefaultCategories/
    // ensureDefaultCollections below (those upsert 30+ rows and can be slow right after
    // install/migration) — start() joins this before creating a walk, so it must resolve fast
    // and can't end up racing a real user tap. See start() and androidMain/CLAUDE.md.
    private val startupHealJob: Job = viewModelScope.launch {
        // Self-heal walks a destroyed instance left open (walkId lives only in that instance's
        // memory) — cheap no-op once nothing has endTime == null. Safe here specifically because
        // this VM instance hasn't assigned walkId yet, so nothing open in the DB can be "this"
        // walk — it can only be a walk an earlier, now-gone instance abandoned (process death, or
        // this backStackEntry recreated — either way its viewModelScope/GPS collector is already
        // cancelled, so nothing is still writing to it).
        healOrphanedWalks()
        // The service survives its ViewModel's destruction (only finish() stops it) — anything
        // healOrphanedWalks() just closed left its notification stuck showing "Идёт запись" with
        // no walk behind it. Harmless no-op if nothing was actually running.
        backgroundRecordingController.stop()
    }

    init {
        viewModelScope.launch {
            ensureDefaultCategories()
            // Must run after categories exist — looks categories up by nameKey to seed collection
            // membership.
            ensureDefaultCollections()
            // Self-heal after the v4->v5 migration / any earlier crash mid-recalculation — cheap
            // no-op once isFilterEligible is already in sync with isPicked/finds.
            recalculateFilterEligibility()
        }
        viewModelScope.launch {
            val sortSettings = combine(
                settingsRepository.observeLanguage(),
                categoryOrder,
            ) { language, order -> language to order }
            combine(
                walkRepository.observeAll(),
                fieldMarkRepository.observeAll(),
                categoryRepository.observeAll(),
                mapFilterRepository.observeFilter(),
                sortSettings,
            ) { walks, marks, categories, filter, (language, order) ->
                val sortedCategories = sortCategories(
                    categories.filter { it.nameKey != MISC_CATEGORY_NAME_KEY && it.isActive },
                    language,
                )
                // "Unknown mushroom" defaults to the end of the feed (ahead of AddSpeciesTile),
                // but only as a starting position — applyRecencyOrder below still bumps it to the
                // front like any other species once it's tapped or picked from search.
                val (unknownMushroom, restCategories) = sortedCategories
                    .partition { it.nameKey == UNKNOWN_MUSHROOM_NAME_KEY }
                val defaultOrderCategories = restCategories + unknownMushroom
                val tileCategories = applyRecencyOrder(defaultOrderCategories, order)
                val categoryById = categories.associateBy { it.id }
                val matchingWalkIds = walks.filter { it.matchesDateAndSeason(filter) }.map { it.id }.toSet()
                val historicalFinds = marks.filter {
                    it.walkId in matchingWalkIds && it.type == MarkType.MUSHROOM &&
                        categoryById[it.categoryId]?.isActive == true
                }
                val historicalPlaces = marks.filter { it.walkId in matchingWalkIds && it.type == MarkType.POI }
                RecordFilterState(
                    tileCategories,
                    historicalFinds,
                    historicalPlaces,
                    computeFilterCount(filter, walks, categories),
                )
            }.collect { s ->
                syncNotificationSlots(s.categories)
                _uiState.update {
                    it.copy(
                        categories = s.categories,
                        historicalFinds = s.historicalFinds,
                        historicalPlaces = s.historicalPlaces,
                        filterCount = s.filterCount,
                    )
                }
            }
        }
        viewModelScope.launch {
            // Треки прошлых прогулок живут ОТДЕЛЬНЫМ потоком, а не внутри combine выше, и
            // читаются одноразовым запросом, а не подпиской. Причина — стоимость: подписка на
            // track_points переотдаёт всю таблицу на каждый GPS-фикс (RecordTrackPointUseCase
            // пишет точку, а следом ещё и строку walks), то есть во время записи прогулки все
            // точки всех прошлых прогулок пересоздавались бы объектами и перегруппировывались
            // каждые несколько секунд — ровно тогда, когда подтормаживать нельзя.
            //
            // Ключ перезагрузки — набор id прогулок, чьи треки надо показать, плюс сам флаг
            // показа. Он меняется, когда прогулка завершилась, удалилась или приехала импортом,
            // либо когда сдвинули фильтр, — но НЕ когда в текущую прогулку дописана точка.
            // distinctUntilChanged поверх него и есть то, что развязывает эти два события.
            // Треки завершённых прогулок неизменны по определению, поэтому одного чтения на
            // каждое изменение ключа достаточно.
            combine(
                walkRepository.observeAll(),
                mapFilterRepository.observeFilter(),
            ) { walks, filter ->
                HistoricalTracksKey(
                    // Только завершённые: текущая прогулка уже рисуется живьём и во всю силу из
                    // RecordUiState.trackPoints, вторая линия под ней всё равно отставала бы на
                    // одну запись в Room.
                    walkIds = walks
                        .filter { it.endTime != null && it.matchesDateAndSeason(filter) }
                        .map { it.id }
                        .toSet(),
                    visible = filter.showPastRoutes,
                )
            }.distinctUntilChanged().collect { key ->
                val tracks = if (!key.visible || key.walkIds.isEmpty()) {
                    emptyMap()
                } else {
                    // Dispatchers.Default, а не Main: сам запрос Room уводит с вызывающего
                    // потока сам, а вот группировка десятков тысяч точек — уже нет.
                    // Dispatchers.IO в commonMain недоступен (internal на Kotlin/Native).
                    withContext(Dispatchers.Default) {
                        trackPointRepository.getPoints(key.walkIds)
                            .groupBy(TrackPoint::walkId)
                            .mapValues { (_, points) ->
                                // Прореживание — до превращения в GeoPoint, чтобы лишние объекты
                                // не создавались вовсе.
                                decimateTrack(points, HISTORICAL_TRACK_STRIDE)
                                    .map { GeoPoint(it.lat, it.lon, it.elevation, it.timestamp) }
                            }
                    }
                }
                _uiState.update { it.copy(historicalTracks = tracks) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeLanguage().collect { currentLanguage = it }
        }
        viewModelScope.launch {
            settingsRepository.observeResetMushroomOrderOnWalkFinish().collect { resetOrderOnWalkFinish = it }
        }
        viewModelScope.launch {
            settingsRepository.observeFreezeMushroomOrder().collect { freezeOrder = it }
        }
        viewModelScope.launch {
            // Кнопки «+»/«−» в уведомлении идущей записи (Android; на iOS поток всегда пуст)
            // приходят сюда и идут теми же методами, что и плитки на экране. У находки должен
            // быть ровно один путь в Room независимо от того, откуда её отметили, — вместе с
            // проверкой лимита и барьером по отсутствию GPS-фикса, которые в этих методах уже
            // стоят.
            backgroundRecordingController.commands.collect { command ->
                when (command) {
                    is RecordingCommand.AddMushroom -> addMushroom(command.categoryId)
                    is RecordingCommand.RemoveMushroom -> removeMushroom(command.categoryId)
                }
            }
        }
        viewModelScope.launch {
            // Снимок для уведомления идущей записи пересобирается только когда меняется что-то
            // из него самого. distinctUntilChanged тут обязателен: uiState переиздаётся на
            // каждый GPS-фикс (currentLocation, trackPoints), а из снимка от фикса зависит одно
            // расстояние — и то уже строкой, которая меняется раз в десяток метров, а не раз в
            // секунду.
            //
            // Времени в сравниваемом снимке нет вовсе (elapsedMillis = 0): оно тикает ежесекундно,
            // и попади оно сюда — уведомление перестраивалось бы каждую секунду ради цифры,
            // которую системный `Chronometer` внутри уведомления считает сам (см.
            // [RecordingNotificationSnapshot]). Настоящее значение подставляется уже после
            // дедупликации, в collect, — как точка отсчёта для этого `Chronometer`.
            combine(
                uiState,
                notificationSlots,
                settingsRepository.observeLanguage(),
            ) { state, slots, language ->
                if (!state.isRecording) return@combine null
                RecordingNotificationSnapshot(
                    language = language,
                    elapsedMillis = 0L,
                    isPaused = state.isPaused,
                    distanceText = formatDistanceKm(state.distanceMeters, language),
                    totalFinds = state.mushroomCounts.values.sum(),
                    species = notificationSpecies(state, slots, language),
                )
            }.distinctUntilChanged().collect { snapshot ->
                if (snapshot != null) {
                    backgroundRecordingController.update(snapshot.copy(elapsedMillis = _elapsedMillis.value))
                }
            }
        }
        viewModelScope.launch {
            // GPS is subscribed to only while it is actually needed: the Record screen is in front
            // of the user, or a walk is being recorded (which keeps running in the background, by
            // design, and is what the foreground service exists for). Before this, the collector
            // started in init() and never stopped — this ViewModel is scoped to the Record
            // back-stack entry and survives navigating away with saveState, so the system's
            // "app is using your location" indicator stayed lit on Archive/Map/Settings, with no
            // walk running and nothing consuming the fixes.
            combine(
                isRecordScreenResumed,
                uiState.map { it.isRecording }.distinctUntilChanged(),
            ) { resumed, recording -> resumed || recording }
                .distinctUntilChanged()
                // Re-checked on every gate change rather than once: returning from the system
                // settings resumes this screen, which flips the gate, which is exactly when the
                // answer can have changed.
                .onEach { needed ->
                    _uiState.update { it.copy(locationUnavailable = needed && !locationTracker.isAvailable()) }
                }
                .flatMapLatest { needed -> if (needed) locationTracker.track() else emptyFlow() }
                .collect { point ->
                    _uiState.update { it.copy(currentLocation = point, locationUnavailable = false) }
                    val baseline = courseBaselineFix
                    if (baseline == null) {
                        courseBaselineFix = point
                    } else {
                        val moved = haversineMeters(baseline.lat, baseline.lon, point.lat, point.lon)
                        if (moved >= MIN_COURSE_FIX_DISTANCE_METERS) {
                            courseOverGround.value = bearingDegrees(baseline.lat, baseline.lon, point.lat, point.lon)
                            courseBaselineFix = point
                        }
                        // else: leave both courseBaselineFix and courseOverGround untouched — jitter
                        // accumulates against the same baseline instead of resetting it every fix, so
                        // slow drift while nearly stationary doesn't produce a new noisy bearing.
                    }
                    val currentWalkId = walkId
                    if (currentWalkId != null && _uiState.value.isRecording && !_uiState.value.isPaused) {
                        val delta = recordTrackPoint(currentWalkId, point, trackSequence, lastPersistedPoint)
                        trackSequence += 1
                        lastPersistedPoint = point
                        _uiState.update {
                            it.copy(distanceMeters = it.distanceMeters + delta, trackPoints = it.trackPoints + point)
                        }
                    }
                }
        }
        viewModelScope.launch {
            val navigationSources = uiState
                .map { NavigationSourceSnapshot(it.currentLocation, it.marks, it.historicalPlaces) }
                .distinctUntilChanged()
            combine(navigationSources, navigationTargetId, courseOverGround) { sources, targetId, course ->
                if (targetId == null) return@combine null
                val target = (sources.marks + sources.historicalPlaces)
                    .find { it.id == targetId && it.type == MarkType.POI } ?: return@combine null
                val location = sources.currentLocation ?: return@combine null
                val distance = haversineMeters(location.lat, location.lon, target.lat, target.lon)
                val turn = course?.let {
                    turnRecommendation(it, bearingDegrees(location.lat, location.lon, target.lat, target.lon))
                }
                NavigationOverlayState(
                    targetId = target.id,
                    targetName = target.name.orEmpty(),
                    targetLat = target.lat,
                    targetLon = target.lon,
                    distanceMeters = distance,
                    hasArrived = hasArrived(distance),
                    turnDirection = turn?.direction,
                    turnDegrees = turn?.degrees,
                )
            }.collect { computed -> _uiState.update { it.copy(navigationTarget = computed) } }
        }
    }

    /**
     * Called by [leshy.mushrooms.map.ui.screens.RecordScreen] on ON_RESUME / ON_PAUSE-or-dispose.
     * The screen going away (navigating to another section, or the app going to the background)
     * releases GPS unless a walk is actually being recorded.
     */
    fun onRecordScreenResumed() {
        isRecordScreenResumed.value = true
    }

    fun onRecordScreenPaused() {
        isRecordScreenResumed.value = false
    }

    fun activateNavigationTo(targetId: Long) {
        navigationTargetId.value = targetId
    }

    fun deactivateNavigation() {
        navigationTargetId.value = null
    }

    fun setWalkName(name: String) {
        _uiState.update { it.copy(walkName = name) }
        val currentWalkId = walkId
        if (currentWalkId != null) {
            viewModelScope.launch { renameWalk(currentWalkId, name) }
        }
    }

    fun onStartOrPauseClick() {
        when {
            !_uiState.value.isRecording -> start()
            !_uiState.value.isPaused -> pause()
            else -> resume()
        }
    }

    private fun start() {
        viewModelScope.launch {
            // Must resolve before a new walk can be created — otherwise a fast enough tap here
            // races startupHealJob's query for endTime == null walks and this walk (freshly
            // inserted, no track points yet) gets swept up and closed as if orphaned.
            startupHealJob.join()
            val location = _uiState.value.currentLocation
            val name = _uiState.value.walkName.ifBlank { string(StringKey.DefaultWalkName, currentLanguage) }
            val id = startWalk(
                name = name,
                startTime = currentTimeMillis(),
                startLat = location?.lat ?: 0.0,
                startLon = location?.lon ?: 0.0,
            )
            walkId = id
            trackSequence = 0
            lastPersistedPoint = null
            slotMarkOrder.clear()
            backgroundRecordingController.start(currentLanguage)
            _elapsedMillis.value = 0L
            _uiState.update {
                it.copy(
                    isRecording = true,
                    isPaused = false,
                    distanceMeters = 0.0,
                    mushroomCounts = emptyMap(),
                    trackPoints = emptyList(),
                    marks = emptyList(),
                )
            }
            startTicker()
        }
    }

    private fun pause() {
        tickerJob?.cancel()
        _uiState.update { it.copy(isPaused = true) }
    }

    private fun resume() {
        _uiState.update { it.copy(isPaused = false) }
        startTicker()
    }

    fun finish() {
        val currentWalkId = walkId ?: return
        tickerJob?.cancel()
        backgroundRecordingController.stop()
        // Captured now, before the state reset below wipes trackPoints/marks back to empty.
        val trackPoints = _uiState.value.trackPoints
        val findLocations = _uiState.value.marks
            .filter { it.type == MarkType.MUSHROOM }
            .map { mark -> GeoPoint(mark.lat, mark.lon, null, mark.timestamp) }
        // Last known GPS fix — the thumbnail's fallback anchor when trackPoints has too few
        // points to bound a region on its own (short walks).
        val location = _uiState.value.currentLocation
        frontBumpFlushJob?.cancel()
        // Apply any taps still sitting in the quiet-window countdown right now instead of just
        // discarding them — a walk finished within 5s of the last +/- tap used to lose that tap's
        // reorder outright, even with resetOrderOnWalkFinish off, because the pending bump was
        // cleared here without ever having been applied to categoryOrder. Flushing first still
        // leaves the resetOrderOnWalkFinish branch below the final say — it runs after and wins.
        flushPendingFrontBumps()
        viewModelScope.launch {
            finishWalk(currentWalkId, currentTimeMillis(), location?.lat, location?.lon)
            walkId = null
            navigationTargetId.value = null
            slotMarkOrder.clear()
            if (resetOrderOnWalkFinish) categoryOrder.value = emptyList()
            // Explicit now that elapsed time lives outside RecordUiState: the rebuild below used
            // to zero it implicitly, by virtue of not carrying it over. Without this the header
            // keeps showing the finished walk's time until the next start().
            _elapsedMillis.value = 0L
            _uiState.update { state ->
                RecordUiState(
                    categories = state.categories,
                    currentLocation = state.currentLocation,
                    historicalFinds = state.historicalFinds,
                    historicalTracks = state.historicalTracks,
                    historicalPlaces = state.historicalPlaces,
                    filterCount = state.filterCount,
                    justFinished = true,
                )
            }
        }
        // Independent coroutine: a slow or offline tile fetch must never delay the Archive
        // navigation triggered by justFinished above.
        viewModelScope.launch {
            val thumbnailPath = walkThumbnailRenderer.render(currentWalkId, trackPoints, findLocations, location)
            if (thumbnailPath != null) updateWalkThumbnail(currentWalkId, thumbnailPath)
        }
    }

    fun consumeFinished() {
        _uiState.update { it.copy(justFinished = false) }
    }

    /**
     * Находки, уже отправленные в Room, но ещё не доехавшие до [_uiState]: `categoryId` → сколько
     * записей в полёте.
     *
     * Существует ради лимита [MAX_MUSHROOM_FINDS_PER_WALK]. Проверять его по одному
     * `_uiState.mushroomCounts` нельзя: счётчик там растёт только ПОСЛЕ `addMushroomMark`, то есть
     * после настоящей записи в базу, а [addMushroom] сама не приостанавливается — она стартует
     * корутину и тут же возвращается. Пока запись идёт, следующий вызов видит прежнее число, и
     * пачка вызовов подряд проходит проверку вся целиком.
     *
     * С плитками на экране это почти не проявлялось: у «+» гаснет кнопка, и палец физически не
     * успевает. С кнопками уведомления проявляется в полную силу — [RecordingNotificationBus]
     * буферизует до 32 нажатий, и `collect` разбирает буфер подряд, без единой приостановки между
     * вызовами. Владелец так и получил больше 999 находок по одному виду (репорт 2026-09-08).
     *
     * Правится не блокировкой, а бронью: слот занимается СИНХРОННО, в том же такте, что и
     * проверка, и освобождается там же, где счётчик в [_uiState] вырастает на эту находку. Мапа
     * читается и пишется только из главного потока (`viewModelScope` и `collect` команд — на нём),
     * поэтому синхронизации не требует.
     */
    private val findsInFlight = mutableMapOf<Long, Int>()

    /**
     * Пытается занять [count] слотов под находки вида [categoryId] в пределах лимита. Возвращает,
     * сколько занять получилось (0 — вид уже на лимите). Занятое обязано быть освобождено
     * [releaseFinds] — и в успешном пути, и в любом раннем выходе.
     */
    private fun reserveFinds(categoryId: Long, count: Int): Int {
        val committed = _uiState.value.mushroomCounts[categoryId] ?: 0
        val inFlight = findsInFlight[categoryId] ?: 0
        val free = (MAX_MUSHROOM_FINDS_PER_WALK - committed - inFlight).coerceAtLeast(0)
        val granted = count.coerceAtMost(free)
        if (granted > 0) findsInFlight[categoryId] = inFlight + granted
        return granted
    }

    private fun releaseFinds(categoryId: Long, count: Int) {
        val left = (findsInFlight[categoryId] ?: 0) - count
        if (left > 0) findsInFlight[categoryId] = left else findsInFlight.remove(categoryId)
    }

    fun addMushroom(categoryId: Long) {
        val currentWalkId = walkId ?: return
        if (reserveFinds(categoryId, 1) == 0) return
        // Барьер, а не проверка «на всякий случай»: без фикса находку не к чему привязать, и
        // записать её всё равно куда — значит соврать (см. AddMushroomMarkUseCase). Экран
        // «Записи» ту же проверку делает раньше и показывает сообщение; здесь она стоит второй,
        // потому что ViewModel обязана быть верна сама по себе, а не по договорённости с UI.
        val location = _uiState.value.currentLocation ?: run { releaseFinds(categoryId, 1); return }
        viewModelScope.launch {
            try {
                val mark = addMushroomMark(currentWalkId, categoryId, location, currentTimeMillis())
                scheduleFrontBump(categoryId)
                noteMarked(categoryId)
                _uiState.update { state ->
                    val counts = state.mushroomCounts.toMutableMap()
                    counts[categoryId] = (counts[categoryId] ?: 0) + 1
                    state.copy(mushroomCounts = counts, marks = state.marks + mark)
                }
            } finally {
                // Строго после того, как находка учтена в mushroomCounts: бронь и счётчик — две
                // половины одного числа, и между ними не должно быть такта, в котором лимит виден
                // недобранным.
                releaseFinds(categoryId, 1)
            }
        }
    }

    /**
     * Bulk-add version of [addMushroom] — logs [count] separate finds at the current (last known)
     * location, exactly as if the + button had been tapped [count] times in a row. Each find is
     * still its own [addMushroomMark] call (own row, own commit) rather than a single use case
     * that writes a combined count, so it stays consistent with the rest of the app treating one
     * [FieldMark] row per find (thumbnail rendering, walk detail stats, etc).
     *
     * The `_uiState` update happens once PER find, inside the loop, not once after it — each
     * [addMushroomMark] call is a real suspending Room write, so this lets `mushroomCounts` (and
     * the tile's displayed count) tick up as the batch commits instead of jumping straight from
     * the old value to `old + count` only once every row has landed.
     */
    fun addMushrooms(categoryId: Long, count: Int) {
        if (count <= 0) return
        val currentWalkId = walkId ?: return
        // Та же бронь, что в [addMushroom], и по той же причине: диалог считает лимит по числу,
        // которое видел на момент открытия, а пока его заполняли, вид могли добить кнопками
        // уведомления. Здесь берётся столько, сколько осталось до лимита, — молча, как и «+».
        val granted = reserveFinds(categoryId, count)
        if (granted == 0) return
        // Тот же барьер, что в [addMushroom].
        val location = _uiState.value.currentLocation ?: run { releaseFinds(categoryId, granted); return }
        viewModelScope.launch {
            // Бронь освобождается по одной находке, вместе с ростом счётчика, а не разом в конце:
            // иначе на всю длину пачки (а это granted настоящих записей в базу) вид выглядел бы
            // занятым вдвое и «+» рядом отказывал бы без причины.
            var written = 0
            try {
                repeat(granted) {
                    val mark = addMushroomMark(currentWalkId, categoryId, location, currentTimeMillis())
                    _uiState.update { state ->
                        val counts = state.mushroomCounts.toMutableMap()
                        counts[categoryId] = (counts[categoryId] ?: 0) + 1
                        state.copy(mushroomCounts = counts, marks = state.marks + mark)
                    }
                    releaseFinds(categoryId, 1)
                    written++
                }
                scheduleFrontBump(categoryId)
                noteMarked(categoryId)
            } finally {
                releaseFinds(categoryId, granted - written)
            }
        }
    }

    /**
     * Moves [categoryId]'s tile to the front of the feed without logging a find — used both by
     * [addMushroom] and by the search dialog, where picking a result should surface its tile
     * (per the user description) but not itself count as a find.
     */
    fun bringCategoryToFront(categoryId: Long) {
        categoryOrder.update { current -> listOf(categoryId) + current.filter { it != categoryId } }
        _uiState.update { it.copy(scrollToStartSignal = it.scrollToStartSignal + 1, scrollToStartDurationMillis = null) }
    }

    /**
     * Queues [categoryId] to jump to the front of the feed, but not right away — tapping +/-
     * used to call [bringCategoryToFront] directly, which reordered the tile out from under the
     * user's finger mid-tap. Instead this (re)starts a [TILE_REORDER_QUIET_WINDOW] countdown;
     * every further tap or manual scroll of the feed ([notifyTileFeedInteraction]) restarts it
     * again, and the reorder only actually happens once the feed has sat idle for the full
     * window. Leaving the Record screen doesn't pause the countdown — [viewModelScope] outlives
     * the composable (this ViewModel is scoped to the Record back-stack entry, see
     * `presentation/CLAUDE.md`) — so a species added just before navigating away is already at
     * the front by the time the user comes back.
     *
     * No-op while Settings' "неподвижный порядок грибов" (freeze order) is on — that setting
     * means +/- taps must stop bumping tiles at all, not just delay the bump.
     */
    private fun scheduleFrontBump(categoryId: Long) {
        if (freezeOrder) return
        pendingFrontBumps.remove(categoryId)
        pendingFrontBumps.add(categoryId)
        restartFrontBumpQuietWindow()
    }

    /** Called by the UI when the user manually scrolls the tile feed — counts as activity for
     * [TILE_REORDER_QUIET_WINDOW] just like a +/- tap, so a reorder doesn't happen while the
     * feed is actively being scrolled by hand. A no-op while nothing is pending. */
    fun notifyTileFeedInteraction() {
        if (pendingFrontBumps.isNotEmpty()) restartFrontBumpQuietWindow()
    }

    private fun restartFrontBumpQuietWindow() {
        frontBumpFlushJob?.cancel()
        frontBumpFlushJob = viewModelScope.launch {
            delay(TILE_REORDER_QUIET_WINDOW)
            flushPendingFrontBumps()
        }
    }

    private fun flushPendingFrontBumps() {
        if (pendingFrontBumps.isEmpty()) return
        // Last-tapped ends up frontmost, same order bringCategoryToFront would produce if called
        // once per id in tap order.
        val front = pendingFrontBumps.asReversed().toList()
        pendingFrontBumps.clear()
        categoryOrder.update { current -> front + current.filter { it !in front } }
        _uiState.update {
            it.copy(
                scrollToStartSignal = it.scrollToStartSignal + 1,
                scrollToStartDurationMillis = TILE_REORDER_SCROLL_DURATION_MILLIS,
            )
        }
    }

    fun removeMushroom(categoryId: Long) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val removed = removeLastMushroomMark(currentWalkId, categoryId)
            if (removed != null) {
                scheduleFrontBump(categoryId)
                noteMarked(categoryId)
                _uiState.update { state ->
                    val counts = state.mushroomCounts.toMutableMap()
                    val newCount = (counts[categoryId] ?: 0) - 1
                    if (newCount > 0) counts[categoryId] = newCount else counts.remove(categoryId)
                    state.copy(mushroomCounts = counts, marks = state.marks.filter { it.id != removed.id })
                }
            }
        }
    }

    fun addPlace(name: String, description: String, photoPath: String?) {
        val currentWalkId = walkId ?: return
        // Тот же барьер, что в [addMushroom]. До сюда дело дойти не должно: экран не открывает
        // форму места, пока местоположения нет, — но форма заполняется долго, и фикс может
        // потеряться, пока её заполняют.
        val location = _uiState.value.currentLocation ?: return
        viewModelScope.launch {
            val mark = addPlaceMark(
                currentWalkId,
                location,
                currentTimeMillis(),
                name,
                description,
                photoPath,
            )
            _uiState.update { state -> state.copy(marks = state.marks + mark) }
        }
    }

    fun updatePlace(mark: FieldMark, name: String, description: String, photoPath: String?) {
        viewModelScope.launch {
            val updated = updatePlaceMark(mark, name, description, photoPath)
            _uiState.update { state ->
                state.copy(marks = state.marks.map { if (it.id == updated.id) updated else it })
            }
        }
    }

    fun deletePlace(mark: FieldMark) {
        viewModelScope.launch {
            deletePlaceMark(mark)
            _uiState.update { state -> state.copy(marks = state.marks.filter { it.id != mark.id }) }
        }
    }

    /**
     * Second entry point for creating a user species (`.claude/plans/user-mushrooms.md`, Phase 4)
     * — the rightmost "Добавить гриб" tile on Record's feed opens the same form as the "Грибы"
     * section, but saving here brings the new tile straight to the front of the feed instead of
     * navigating anywhere, so a walk in progress is never interrupted. Подборку тут спрашивают
     * ровно так же, как на «Моих грибах» — тем же вторым диалогом формы: поведение формы не должно
     * зависеть от того, откуда её открыли, а пустое поле там всё равно означает «Другие» и стоит
     * одного нажатия галочки. No find is logged
     * automatically — the user still taps the tile's own "+" to mark it, same as any other tile.
     */
    fun saveNewSpecies(
        name: String,
        scientificNameInput: String?,
        colorHex: String,
        iconPngBytes: ByteArray?,
        collectionName: String,
    ) {
        viewModelScope.launch {
            val saved = createOrUpdateUserSpecies(
                null,
                name,
                scientificNameInput,
                currentLanguage,
                colorHex,
                iconPngBytes,
                collectionName,
            )
            bringCategoryToFront(saved.id)
        }
    }

    /**
     * Держит [notificationSlots] заполненными и живыми: выбрасывает виды, переставшие быть
     * активными (сняты на «Фильтре», удалены из «Моих грибов» посреди прогулки), и добирает
     * недостающее началом ленты плиток [feed].
     *
     * Добор началом ленты — не украшение: без него до первой находки уведомление нечем было бы
     * наполнить, то есть отметить гриб с заблокированного экрана можно было бы только после того,
     * как хотя бы раз отметил его же с разблокированного.
     */
    private fun syncNotificationSlots(feed: List<Category>) {
        val feedIds = feed.mapTo(mutableSetOf()) { it.id }
        notificationSlots.update { current ->
            val alive = current.filter { it in feedIds }
            if (alive.size >= MAX_RECORDING_NOTIFICATION_SPECIES) {
                alive
            } else {
                alive + feed.asSequence()
                    .map { it.id }
                    .filterNot { it in alive }
                    .take(MAX_RECORDING_NOTIFICATION_SPECIES - alive.size)
            }
        }
    }

    /**
     * Отмечает вид как только что тронутый и, если его ещё нет среди строк уведомления, заводит
     * ему там место — заменой наименее свежего слота НА ЕГО ЖЕ ПОЗИЦИИ. Ни одна другая строка при
     * этом не съезжает, см. [notificationSlots].
     */
    private fun noteMarked(categoryId: Long) {
        markCounter += 1
        slotMarkOrder[categoryId] = markCounter
        notificationSlots.update { current ->
            when {
                categoryId in current -> current
                current.size < MAX_RECORDING_NOTIFICATION_SPECIES -> current + categoryId
                else -> {
                    val victim = current.minBy { slotMarkOrder[it] ?: 0L }
                    current.map { if (it == victim) categoryId else it }
                }
            }
        }
    }

    /**
     * Строки «вид — счётчик» для уведомления идущей записи — ровно [notificationSlots], в их
     * порядке.
     *
     * Вид, у которого «−» довёл счётчик до нуля, из списка не выпадает: строка обязана оставаться
     * на месте ровно тогда, когда по соседней промахнуться легче всего.
     */
    private fun notificationSpecies(
        state: RecordUiState,
        slots: List<Long>,
        language: AppLanguage,
    ): List<RecordingNotificationSpecies> =
        slots.mapNotNull { id ->
            val category = state.categories.find { it.id == id } ?: return@mapNotNull null
            RecordingNotificationSpecies(
                categoryId = id,
                name = categoryDisplayName(category, language),
                count = state.mushroomCounts[id] ?: 0,
            )
        }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(TICK_INTERVAL_MILLIS.milliseconds)
                _elapsedMillis.update { it + TICK_INTERVAL_MILLIS }
            }
        }
    }
}
