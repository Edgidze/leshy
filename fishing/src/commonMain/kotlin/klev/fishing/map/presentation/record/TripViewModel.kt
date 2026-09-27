package klev.fishing.map.presentation.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.data.platform.TripRecordingKeepAlive
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.data.weather.WeatherSuggestionSource
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.LostReason
import klev.fishing.map.domain.model.TripWeather
import klev.fishing.map.domain.model.WeatherProvenance
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.repository.TripRepository
import klev.fishing.map.domain.repository.TripTrackPointRepository
import klev.fishing.map.domain.usecase.AddCatchUseCase
import klev.fishing.map.domain.usecase.EnsureFishSpeciesUseCase
import klev.fishing.map.domain.usecase.FinishTripUseCase
import klev.fishing.map.domain.usecase.RecordTripPointUseCase
import klev.fishing.map.domain.usecase.StartTripUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import leshy.mushrooms.map.data.platform.LocationTracker
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.repository.SettingsRepository

/**
 * Экран «Рыбалка».
 *
 * Два правила проекта соблюдаются здесь буквально:
 *
 * 1. **улов пишется в Room немедленно** ([addCatch]) — процесс может быть убит в любой момент, и
 *    всё отмеченное обязано сохраниться без штатного закрытия рыбалки;
 * 2. **незакрытая рыбалка восстанавливается сама** — активная берётся из базы, а не из памяти,
 *    поэтому убитый процесс не теряет выезд.
 *
 * Погода спрашивается ПОСЛЕ закрытия рыбалки, а не во время: рыбак вспоминает выезд целиком, и
 * требовать погоду на каждую поклёвку значит не получить её вовсе.
 */
class TripViewModel(
    private val trips: TripRepository,
    private val points: TripTrackPointRepository,
    private val catches: CatchRepository,
    private val species: SpeciesRepository,
    private val startTrip: StartTripUseCase,
    private val finishTrip: FinishTripUseCase,
    private val recordPoint: RecordTripPointUseCase,
    private val addCatchUseCase: AddCatchUseCase,
    private val ensureSpecies: EnsureFishSpeciesUseCase,
    private val locationTracker: LocationTracker,
    private val keepAlive: TripRecordingKeepAlive,
    private val settings: SettingsRepository,
    private val fishingSettings: FishingSettingsRepository,
    private val weatherSource: WeatherSuggestionSource,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripUiState())
    val uiState = _uiState.asStateFlow()

    private var trackingJob: Job? = null
    private var tripStreamsJob: Job? = null

    init {
        viewModelScope.launch {
            ensureSpecies()
            _uiState.update { it.copy(locationAvailable = locationTracker.isAvailable()) }
        }
        viewModelScope.launch {
            species.observeAll().collect { list ->
                _uiState.update { state -> state.copy(species = list.filter { it.isActive }, isLoading = false) }
            }
        }
        viewModelScope.launch {
            catches.observeRecentBaits().collect { baits ->
                _uiState.update { it.copy(recentBaits = baits) }
            }
        }
        viewModelScope.launch {
            fishingSettings.observePressureUnit().collect { unit ->
                _uiState.update { it.copy(pressureUnit = unit) }
            }
        }
        viewModelScope.launch {
            fishingSettings.observeMethods().collect { methods ->
                _uiState.update { it.copy(methods = methods) }
            }
        }
        viewModelScope.launch {
            trips.observeActive().collect { active ->
                _uiState.update { it.copy(trip = active) }
                if (active == null) {
                    stopTracking()
                    tripStreamsJob?.cancel()
                    _uiState.update { it.copy(track = emptyList(), catches = emptyList(), distanceMeters = 0.0) }
                } else {
                    observeTripStreams(active.id)
                    startTracking(active.id)
                }
            }
        }
    }

    private fun observeTripStreams(tripId: Long) {
        tripStreamsJob?.cancel()
        tripStreamsJob = viewModelScope.launch {
            launch {
                points.observeByTrip(tripId).collect { list ->
                    _uiState.update { state ->
                        state.copy(
                            track = list.map { GeoPoint(it.lat, it.lon, elevation = null, timestamp = it.timestamp) },
                        )
                    }
                }
            }
            launch {
                catches.observeByTrip(tripId).collect { list ->
                    _uiState.update { it.copy(catches = list) }
                }
            }
            launch {
                trips.observeById(tripId).collect { trip ->
                    if (trip != null) _uiState.update { it.copy(distanceMeters = trip.distanceMeters) }
                }
            }
        }
    }

    private fun startTracking(tripId: Long) {
        if (trackingJob?.isActive == true) return
        trackingJob = viewModelScope.launch {
            keepAlive.start(settings.observeLanguage().first())
            locationTracker.track().collect { fix ->
                _uiState.update { it.copy(currentLocation = fix.point) }
                recordPoint(tripId, fix.point.lat, fix.point.lon, currentTimeMillis())
            }
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        keepAlive.stop()
    }

    fun start(method: FishingMethod) {
        viewModelScope.launch {
            val here = _uiState.value.currentLocation
            startTrip(method, here?.lat, here?.lon, currentTimeMillis())
        }
    }

    fun finish() {
        val tripId = _uiState.value.trip?.id ?: return
        viewModelScope.launch {
            finishTrip(tripId, currentTimeMillis())
            stopTracking()
            // Диалог погоды открывается сразу — здесь и только здесь рыбак ещё помнит выезд.
            _uiState.update {
                it.copy(
                    weatherPromptTripId = tripId,
                    weatherDraft = TripWeather(
                        airTempC = null,
                        waterTempC = null,
                        pressureHpa = null,
                        pressureTrend = null,
                        windSpeedMps = null,
                        windDirection = null,
                        cloudiness = null,
                        precipitation = null,
                        provenance = WeatherProvenance.USER,
                    ),
                    weatherSuggestFailed = false,
                )
            }
        }
    }

    fun setMethod(method: FishingMethod) {
        val tripId = _uiState.value.trip?.id ?: return
        viewModelScope.launch { trips.setMethod(tripId, method) }
    }

    fun setWaterBody(name: String) {
        val tripId = _uiState.value.trip?.id ?: return
        viewModelScope.launch { trips.setWaterBody(tripId, name) }
    }

    /**
     * Улов одним касанием плитки вида: пишется немедленно, без единой цифры, исходом «взял».
     *
     * **Это главное решение рыбацкого экрана записи, и оно от факта, а не от вкуса:** дневник, в
     * котором запись занимает больше времени, чем вытащить рыбу, перестают вести через пару выездов
     * (разбор и источники — `.claude/plans/fishing-ux.md`). Поэтому обязательного здесь нет ничего:
     * вид известен по нажатой плитке, время и координата — у приложения, остальное человек уточнит
     * тогда, когда у него будут сухие руки, — или не уточнит вовсе, и запись всё равно осталась.
     *
     * Правило проекта №1 («каждая находка коммитится немедленно») выполняется буквально: в памяти
     * не задерживается ничего.
     */
    fun quickCatch(speciesId: Long) {
        viewModelScope.launch {
            val id = saveCatch(speciesId = speciesId, outcome = CatchOutcome.KEPT) ?: return@launch
            _uiState.update { it.copy(justSavedCatchId = id) }
        }
    }

    /** Сигнал снэкбара потреблён — иначе он показался бы снова при восстановлении экрана. */
    fun consumeJustSaved() {
        _uiState.update { it.copy(justSavedCatchId = null) }
    }

    /** Улов из формы: вид выбирается в ней самой (в ленте его может не быть — скрыт или свой). */
    fun addCatch(
        speciesId: Long,
        weightGrams: Int?,
        lengthMm: Int?,
        bait: String?,
        outcome: CatchOutcome,
        lostReason: LostReason?,
        note: String?,
    ) {
        viewModelScope.launch {
            saveCatch(
                speciesId = speciesId,
                weightGrams = weightGrams,
                lengthMm = lengthMm,
                bait = bait,
                outcome = outcome,
                lostReason = lostReason,
                note = note,
            )
        }
    }

    /**
     * Координата — текущая, а если фикса нет, последняя точка трека, а если и её нет, нули: запись
     * обязана состояться, даже когда GPS молчит. Нули честно означают «не знаем», как и у грибной
     * находки без фикса.
     */
    private suspend fun saveCatch(
        speciesId: Long,
        weightGrams: Int? = null,
        lengthMm: Int? = null,
        bait: String? = null,
        outcome: CatchOutcome,
        lostReason: LostReason? = null,
        note: String? = null,
    ): Long? {
        val state = _uiState.value
        val trip = state.trip ?: return null
        val point = state.currentLocation
            ?: state.track.lastOrNull()
            ?: GeoPoint(0.0, 0.0, elevation = null, timestamp = 0L)
        return addCatchUseCase(
            Catch(
                id = 0,
                tripId = trip.id,
                speciesId = speciesId,
                lat = point.lat,
                lon = point.lon,
                timestamp = currentTimeMillis(),
                weightGrams = weightGrams,
                lengthMm = lengthMm,
                bait = bait?.trim()?.ifBlank { null },
                outcome = outcome,
                lostReason = if (outcome == CatchOutcome.LOST) lostReason else null,
                photoPath = null,
                note = note?.trim()?.ifBlank { null },
            )
        )
    }

    /** Правка уже записанного улова — то, чем закрывается быстрая запись «в одно касание». */
    fun updateCatch(item: Catch) {
        viewModelScope.launch { catches.update(item) }
    }

    fun deleteCatch(id: Long) {
        viewModelScope.launch { catches.delete(id) }
    }

    // --- Погода ---

    fun updateWeatherDraft(transform: (TripWeather) -> TripWeather) {
        _uiState.update { state ->
            val draft = state.weatherDraft ?: return@update state
            val edited = transform(draft)
            // Правка подсказанного значения меняет провенанс: цифра, подставленная сервисом и
            // тронутая человеком, — уже не мнение модели о узле сетки.
            val provenance = when (draft.provenance) {
                WeatherProvenance.SUGGESTED -> WeatherProvenance.SUGGESTED_EDITED
                else -> draft.provenance
            }
            state.copy(weatherDraft = edited.copy(provenance = provenance))
        }
    }

    /** «Подсказать»: спросить сеть, что было в этом месте в это время, и подставить в поля. */
    fun suggestWeather() {
        val state = _uiState.value
        val tripId = state.weatherPromptTripId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(weatherSuggesting = true, weatherSuggestFailed = false) }
            val trip = trips.getById(tripId)
            val track = points.getByTrip(tripId)
            // Точка — середина трека, а не старт: рыбалка могла начаться на парковке.
            val here = track.getOrNull(track.size / 2)
            val lat = here?.lat ?: trip?.let { 0.0 } ?: 0.0
            val lon = here?.lon ?: 0.0
            val middleTime = trip?.let { t ->
                val end = t.finishedAt ?: currentTimeMillis()
                t.startedAt + (end - t.startedAt) / 2
            } ?: currentTimeMillis()
            val suggestion = if (lat == 0.0 && lon == 0.0) null else weatherSource.suggest(lat, lon, middleTime)
            _uiState.update { current ->
                if (suggestion == null) {
                    current.copy(weatherSuggesting = false, weatherSuggestFailed = true)
                } else {
                    // Введённое человеком не затирается: подсказка заполняет только пустые поля.
                    val draft = current.weatherDraft
                    current.copy(
                        weatherSuggesting = false,
                        weatherDraft = TripWeather(
                            airTempC = draft?.airTempC ?: suggestion.airTempC,
                            waterTempC = draft?.waterTempC ?: suggestion.waterTempC,
                            pressureHpa = draft?.pressureHpa ?: suggestion.pressureHpa,
                            pressureTrend = draft?.pressureTrend ?: suggestion.pressureTrend,
                            windSpeedMps = draft?.windSpeedMps ?: suggestion.windSpeedMps,
                            windDirection = draft?.windDirection ?: suggestion.windDirection,
                            cloudiness = draft?.cloudiness ?: suggestion.cloudiness,
                            precipitation = draft?.precipitation ?: suggestion.precipitation,
                            provenance = WeatherProvenance.SUGGESTED,
                        ),
                    )
                }
            }
        }
    }

    fun saveWeather() {
        val state = _uiState.value
        val tripId = state.weatherPromptTripId ?: return
        val draft = state.weatherDraft
        viewModelScope.launch {
            trips.setWeather(tripId, draft)
            _uiState.update { it.copy(weatherPromptTripId = null, weatherDraft = null) }
        }
    }

    fun dismissWeather() {
        _uiState.update { it.copy(weatherPromptTripId = null, weatherDraft = null, weatherSuggestFailed = false) }
    }

    override fun onCleared() {
        // Сервис НЕ останавливается: рыбалка продолжается, когда экран ушёл. Его снимает только
        // закрытие рыбалки (`finish`) — иначе уход в другое приложение обрывал бы трек, ради
        // которого сервис и существует.
        super.onCleared()
    }
}
