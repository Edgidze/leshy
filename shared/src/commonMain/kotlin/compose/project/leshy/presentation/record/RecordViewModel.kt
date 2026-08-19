package compose.project.leshy.presentation.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.data.platform.BackgroundRecordingController
import compose.project.leshy.data.platform.LocationTracker
import compose.project.leshy.data.platform.WalkThumbnailRenderer
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.FieldMarkRepository
import compose.project.leshy.domain.repository.MapFilterRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.repository.WalkRepository
import compose.project.leshy.domain.usecase.AddMushroomMarkUseCase
import compose.project.leshy.domain.usecase.AddPlaceMarkUseCase
import compose.project.leshy.domain.usecase.DeletePlaceMarkUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCategoriesUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCollectionsUseCase
import compose.project.leshy.domain.usecase.FinishWalkUseCase
import compose.project.leshy.domain.usecase.MISC_CATEGORY_NAME_KEY
import compose.project.leshy.domain.usecase.RecalculateFilterEligibilityUseCase
import compose.project.leshy.domain.usecase.RecordTrackPointUseCase
import compose.project.leshy.domain.usecase.RemoveLastMushroomMarkUseCase
import compose.project.leshy.domain.usecase.RenameWalkUseCase
import compose.project.leshy.domain.usecase.StartWalkUseCase
import compose.project.leshy.domain.usecase.UpdatePlaceMarkUseCase
import compose.project.leshy.domain.usecase.UpdateWalkThumbnailUseCase
import compose.project.leshy.domain.util.bearingDegrees
import compose.project.leshy.domain.util.computeFilterCount
import compose.project.leshy.domain.util.hasArrived
import compose.project.leshy.domain.util.haversineMeters
import compose.project.leshy.domain.util.matchesDateAndSeason
import compose.project.leshy.domain.util.turnRecommendation
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.string
import compose.project.leshy.presentation.applyRecencyOrder
import compose.project.leshy.presentation.sortCategories
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val TICK_INTERVAL_MILLIS = 1000L
private const val MIN_COURSE_FIX_DISTANCE_METERS = 3.0

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

class RecordViewModel(
    private val categoryRepository: CategoryRepository,
    private val walkRepository: WalkRepository,
    private val fieldMarkRepository: FieldMarkRepository,
    private val mapFilterRepository: MapFilterRepository,
    private val locationTracker: LocationTracker,
    private val backgroundRecordingController: BackgroundRecordingController,
    private val settingsRepository: SettingsRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val ensureDefaultCollections: EnsureDefaultCollectionsUseCase,
    private val recalculateFilterEligibility: RecalculateFilterEligibilityUseCase,
    private val startWalk: StartWalkUseCase,
    private val finishWalk: FinishWalkUseCase,
    private val renameWalk: RenameWalkUseCase,
    private val recordTrackPoint: RecordTrackPointUseCase,
    private val addMushroomMark: AddMushroomMarkUseCase,
    private val removeLastMushroomMark: RemoveLastMushroomMarkUseCase,
    private val addPlaceMark: AddPlaceMarkUseCase,
    private val updatePlaceMark: UpdatePlaceMarkUseCase,
    private val deletePlaceMark: DeletePlaceMarkUseCase,
    private val walkThumbnailRenderer: WalkThumbnailRenderer,
    private val updateWalkThumbnail: UpdateWalkThumbnailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private var walkId: Long? = null
    private var lastPersistedPoint: GeoPoint? = null
    private var trackSequence = 0
    private var tickerJob: Job? = null
    private var currentLanguage = AppLanguage.EN
    private var resetOrderOnWalkFinish = false

    // Most-recently-bumped category ids first — a tile jumps to the front of the feed each time
    // it's added (or picked from search). Not persisted across app restarts; whether it survives
    // past the end of a walk is gated by resetOrderOnWalkFinish (Settings, off by default — see
    // finish()).
    private val categoryOrder = MutableStateFlow<List<Long>>(emptyList())

    private val navigationTargetId = MutableStateFlow<Long?>(null)
    private val courseOverGround = MutableStateFlow<Double?>(null)
    private var courseBaselineFix: GeoPoint? = null

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
                settingsRepository.observeMushroomSortOrder(),
                settingsRepository.observeLanguage(),
                categoryOrder,
            ) { sortOrder, language, order -> Triple(sortOrder, language, order) }
            combine(
                walkRepository.observeAll(),
                fieldMarkRepository.observeAll(),
                categoryRepository.observeAll(),
                mapFilterRepository.observeFilter(),
                sortSettings,
            ) { walks, marks, categories, filter, (sortOrder, language, order) ->
                val defaultOrderCategories = sortCategories(
                    categories.filter { it.nameKey != MISC_CATEGORY_NAME_KEY && it.isActive },
                    sortOrder,
                    language,
                )
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
            settingsRepository.observeLanguage().collect { currentLanguage = it }
        }
        viewModelScope.launch {
            settingsRepository.observeResetMushroomOrderOnWalkFinish().collect { resetOrderOnWalkFinish = it }
        }
        viewModelScope.launch {
            locationTracker.track().collect { point ->
                _uiState.update { it.copy(currentLocation = point) }
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
            backgroundRecordingController.start(currentLanguage)
            _uiState.update {
                it.copy(
                    isRecording = true,
                    isPaused = false,
                    elapsedMillis = 0L,
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
        viewModelScope.launch {
            finishWalk(currentWalkId, currentTimeMillis(), location?.lat, location?.lon)
            walkId = null
            navigationTargetId.value = null
            if (resetOrderOnWalkFinish) categoryOrder.value = emptyList()
            _uiState.update { state ->
                RecordUiState(
                    categories = state.categories,
                    currentLocation = state.currentLocation,
                    historicalFinds = state.historicalFinds,
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

    fun addMushroom(categoryId: Long) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val mark = addMushroomMark(currentWalkId, categoryId, _uiState.value.currentLocation, currentTimeMillis())
            bringCategoryToFront(categoryId)
            _uiState.update { state ->
                val counts = state.mushroomCounts.toMutableMap()
                counts[categoryId] = (counts[categoryId] ?: 0) + 1
                state.copy(mushroomCounts = counts, marks = state.marks + mark)
            }
        }
    }

    /**
     * Bulk-add version of [addMushroom] — logs [count] separate finds at the current (last known)
     * location, exactly as if the + button had been tapped [count] times in a row. Each find is
     * still its own [addMushroomMark] call (own row, own commit) rather than a single use case
     * that writes a combined count, so it stays consistent with the rest of the app treating one
     * [FieldMark] row per find (thumbnail rendering, walk detail stats, etc).
     */
    fun addMushrooms(categoryId: Long, count: Int) {
        if (count <= 0) return
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val location = _uiState.value.currentLocation
            val newMarks = (1..count).map {
                addMushroomMark(currentWalkId, categoryId, location, currentTimeMillis())
            }
            bringCategoryToFront(categoryId)
            _uiState.update { state ->
                val counts = state.mushroomCounts.toMutableMap()
                counts[categoryId] = (counts[categoryId] ?: 0) + count
                state.copy(mushroomCounts = counts, marks = state.marks + newMarks)
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
        _uiState.update { it.copy(scrollToStartSignal = it.scrollToStartSignal + 1) }
    }

    fun removeMushroom(categoryId: Long) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val removed = removeLastMushroomMark(currentWalkId, categoryId)
            if (removed) {
                bringCategoryToFront(categoryId)
                _uiState.update { state ->
                    val counts = state.mushroomCounts.toMutableMap()
                    val newCount = (counts[categoryId] ?: 0) - 1
                    if (newCount > 0) counts[categoryId] = newCount else counts.remove(categoryId)
                    state.copy(mushroomCounts = counts)
                }
            }
        }
    }

    fun addPlace(name: String, description: String, photoPath: String?) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val mark = addPlaceMark(
                currentWalkId,
                _uiState.value.currentLocation,
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

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(TICK_INTERVAL_MILLIS.milliseconds)
                _uiState.update { it.copy(elapsedMillis = it.elapsedMillis + TICK_INTERVAL_MILLIS) }
            }
        }
    }
}
