package klev.fishing.map.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.repository.TripTrackPointRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import leshy.mushrooms.map.domain.model.GeoPoint

data class CatchMapUiState(
    val isLoading: Boolean = true,
    val catches: List<Catch> = emptyList(),
    val species: List<FishSpecies> = emptyList(),
    /** Фильтр по виду: `null` — все виды. */
    val speciesFilter: Long? = null,
    val tracks: Map<Long, List<GeoPoint>> = emptyMap(),
) {
    val visibleCatches: List<Catch>
        get() = speciesFilter?.let { id -> catches.filter { it.speciesId == id } } ?: catches
}

class CatchMapViewModel(
    private val catches: CatchRepository,
    private val species: SpeciesRepository,
    private val points: TripTrackPointRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CatchMapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(catches.observeAll(), species.observeAll()) { catchList, speciesList ->
                catchList to speciesList
            }.collect { (catchList, speciesList) ->
                // Треки подтягиваются только для рыбалок, на которых что-то отмечено: выезд без
                // улова на общей карте показывать нечем, а линия без причины её только засоряет.
                val tracks = catchList.map { it.tripId }.distinct().associateWith { tripId ->
                    points.getByTrip(tripId).map {
                        GeoPoint(it.lat, it.lon, elevation = null, timestamp = it.timestamp)
                    }
                }
                _uiState.value = CatchMapUiState(
                    isLoading = false,
                    catches = catchList,
                    species = speciesList.filter { it.isActive },
                    speciesFilter = _uiState.value.speciesFilter,
                    tracks = tracks,
                )
            }
        }
    }

    fun setSpeciesFilter(id: Long?) {
        _uiState.value = _uiState.value.copy(speciesFilter = id)
    }
}
