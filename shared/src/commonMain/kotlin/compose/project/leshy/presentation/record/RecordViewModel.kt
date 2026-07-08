package compose.project.leshy.presentation.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import compose.project.leshy.data.platform.LocationTracker
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.repository.CategoryRepository
import compose.project.leshy.domain.repository.SettingsRepository
import compose.project.leshy.domain.usecase.AddMushroomMarkUseCase
import compose.project.leshy.domain.usecase.AddPhotoMarkUseCase
import compose.project.leshy.domain.usecase.EnsureDefaultCategoriesUseCase
import compose.project.leshy.domain.usecase.FinishWalkUseCase
import compose.project.leshy.domain.usecase.RecordTrackPointUseCase
import compose.project.leshy.domain.usecase.RemoveLastMushroomMarkUseCase
import compose.project.leshy.domain.usecase.StartWalkUseCase
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.string
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TICK_INTERVAL_MILLIS = 1000L

class RecordViewModel(
    private val categoryRepository: CategoryRepository,
    private val locationTracker: LocationTracker,
    private val settingsRepository: SettingsRepository,
    private val ensureDefaultCategories: EnsureDefaultCategoriesUseCase,
    private val startWalk: StartWalkUseCase,
    private val finishWalk: FinishWalkUseCase,
    private val recordTrackPoint: RecordTrackPointUseCase,
    private val addMushroomMark: AddMushroomMarkUseCase,
    private val removeLastMushroomMark: RemoveLastMushroomMarkUseCase,
    private val addPhotoMark: AddPhotoMarkUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private var walkId: Long? = null
    private var lastPersistedPoint: GeoPoint? = null
    private var trackSequence = 0
    private var tickerJob: Job? = null
    private var currentLanguage = AppLanguage.EN

    init {
        viewModelScope.launch { ensureDefaultCategories() }
        viewModelScope.launch {
            categoryRepository.observeActive().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            settingsRepository.observeLanguage().collect { currentLanguage = it }
        }
        viewModelScope.launch {
            locationTracker.track().collect { point ->
                _uiState.update { it.copy(currentLocation = point) }
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
    }

    fun setWalkName(name: String) {
        _uiState.update { it.copy(walkName = name) }
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
        viewModelScope.launch {
            val location = _uiState.value.currentLocation
            finishWalk(currentWalkId, currentTimeMillis(), location?.lat, location?.lon)
            walkId = null
            _uiState.update { state ->
                RecordUiState(categories = state.categories, currentLocation = state.currentLocation)
            }
        }
    }

    fun addMushroom(categoryId: Long) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val mark = addMushroomMark(currentWalkId, categoryId, _uiState.value.currentLocation, currentTimeMillis())
            _uiState.update { state ->
                val counts = state.mushroomCounts.toMutableMap()
                counts[categoryId] = (counts[categoryId] ?: 0) + 1
                state.copy(mushroomCounts = counts, marks = state.marks + mark)
            }
        }
    }

    fun removeMushroom(categoryId: Long) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val removed = removeLastMushroomMark(currentWalkId, categoryId)
            if (removed) {
                _uiState.update { state ->
                    val counts = state.mushroomCounts.toMutableMap()
                    val newCount = (counts[categoryId] ?: 0) - 1
                    if (newCount > 0) counts[categoryId] = newCount else counts.remove(categoryId)
                    state.copy(mushroomCounts = counts)
                }
            }
        }
    }

    fun onPhotoCaptured(photoPath: String) {
        val currentWalkId = walkId ?: return
        viewModelScope.launch {
            val mark = addPhotoMark(currentWalkId, _uiState.value.currentLocation, currentTimeMillis(), photoPath)
            _uiState.update { state -> state.copy(marks = state.marks + mark) }
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                delay(TICK_INTERVAL_MILLIS)
                _uiState.update { it.copy(elapsedMillis = it.elapsedMillis + TICK_INTERVAL_MILLIS) }
            }
        }
    }
}
