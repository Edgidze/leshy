package klev.fishing.map.presentation.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.data.weather.WeatherSuggestionSource
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.model.TripWeather
import klev.fishing.map.domain.model.WeatherProvenance
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.repository.TripRepository
import klev.fishing.map.domain.repository.TripTrackPointRepository
import klev.fishing.map.domain.usecase.DeleteTripUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import leshy.mushrooms.map.domain.model.GeoPoint

data class TripDetailUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val track: List<GeoPoint> = emptyList(),
    val catches: List<Catch> = emptyList(),
    val species: Map<Long, FishSpecies> = emptyMap(),
    val pressureUnit: PressureUnit = PressureUnit.HPA,
    /** Недавние приманки — для листа правки улова: он тот же самый, что на экране записи. */
    val recentBaits: List<String> = emptyList(),
    val weatherDraft: TripWeather? = null,
    val weatherSuggesting: Boolean = false,
    val weatherSuggestFailed: Boolean = false,
    val deleted: Boolean = false,
)

class TripDetailViewModel(
    private val trips: TripRepository,
    private val points: TripTrackPointRepository,
    private val catches: CatchRepository,
    private val species: SpeciesRepository,
    private val deleteTrip: DeleteTripUseCase,
    private val fishingSettings: FishingSettingsRepository,
    private val weatherSource: WeatherSuggestionSource,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var tripId: Long = 0

    fun load(id: Long) {
        if (tripId == id) return
        tripId = id
        viewModelScope.launch {
            _uiState.update { it.copy(species = species.getAll().associateBy { s -> s.id }) }
        }
        viewModelScope.launch {
            trips.observeById(id).collect { trip ->
                _uiState.update { it.copy(trip = trip, isLoading = false) }
            }
        }
        viewModelScope.launch {
            points.observeByTrip(id).collect { list ->
                _uiState.update { state ->
                    state.copy(
                        track = list.map { GeoPoint(it.lat, it.lon, elevation = null, timestamp = it.timestamp) },
                    )
                }
            }
        }
        viewModelScope.launch {
            catches.observeByTrip(id).collect { list ->
                _uiState.update { it.copy(catches = list) }
            }
        }
        viewModelScope.launch {
            fishingSettings.observePressureUnit().collect { unit ->
                _uiState.update { it.copy(pressureUnit = unit) }
            }
        }
        viewModelScope.launch {
            catches.observeRecentBaits().collect { baits ->
                _uiState.update { it.copy(recentBaits = baits) }
            }
        }
    }

    fun rename(title: String) {
        viewModelScope.launch { trips.setTitle(tripId, title) }
    }

    fun delete() {
        viewModelScope.launch {
            deleteTrip(tripId)
            _uiState.update { it.copy(deleted = true) }
        }
    }

    fun deleteCatch(id: Long) {
        viewModelScope.launch { catches.delete(id) }
    }

    /**
     * Правка улова уже после рыбалки — вторая половина быстрой записи «в одно касание»: на воде
     * рыба отмечается без цифр, а вес, приманка и исход дописываются вечером, когда есть время.
     */
    fun updateCatch(item: Catch) {
        viewModelScope.launch { catches.update(item) }
    }

    /** Погоду можно вписать и позже — рыбак мог нажать «не сейчас» на экране записи. */
    fun editWeather() {
        val existing = _uiState.value.trip?.weather
        _uiState.update {
            it.copy(
                weatherDraft = existing ?: TripWeather(
                    airTempC = null, waterTempC = null, pressureHpa = null, pressureTrend = null,
                    windSpeedMps = null, windDirection = null, cloudiness = null,
                    precipitation = null, provenance = WeatherProvenance.USER,
                ),
                weatherSuggestFailed = false,
            )
        }
    }

    fun updateWeatherDraft(transform: (TripWeather) -> TripWeather) {
        _uiState.update { state ->
            val draft = state.weatherDraft ?: return@update state
            val provenance = when (draft.provenance) {
                WeatherProvenance.SUGGESTED -> WeatherProvenance.SUGGESTED_EDITED
                else -> draft.provenance
            }
            state.copy(weatherDraft = transform(draft).copy(provenance = provenance))
        }
    }

    fun suggestWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(weatherSuggesting = true, weatherSuggestFailed = false) }
            val trip = trips.getById(tripId)
            val track = points.getByTrip(tripId)
            val here = track.getOrNull(track.size / 2)
            val suggestion = if (here == null || trip == null) {
                null
            } else {
                val end = trip.finishedAt ?: trip.startedAt
                weatherSource.suggest(here.lat, here.lon, trip.startedAt + (end - trip.startedAt) / 2)
            }
            _uiState.update { current ->
                if (suggestion == null) {
                    current.copy(weatherSuggesting = false, weatherSuggestFailed = true)
                } else {
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
        val draft = _uiState.value.weatherDraft
        viewModelScope.launch {
            trips.setWeather(tripId, draft)
            _uiState.update { it.copy(weatherDraft = null) }
        }
    }

    fun dismissWeather() {
        _uiState.update { it.copy(weatherDraft = null, weatherSuggestFailed = false) }
    }
}
