package klev.fishing.map.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ArchiveRow(
    val trip: Trip,
    val catchCount: Int,
    val totalWeightGrams: Int,
)

data class FishArchiveUiState(
    val isLoading: Boolean = true,
    val rows: List<ArchiveRow> = emptyList(),
)

class FishArchiveViewModel(
    private val trips: TripRepository,
    private val catches: CatchRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FishArchiveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(trips.observeAll(), catches.observeAll()) { tripList, catchList ->
                val byTrip: Map<Long, List<Catch>> = catchList.groupBy { it.tripId }
                tripList
                    // Идущая рыбалка живёт на экране записи, а не в архиве: иначе она стоит в
                    // списке законченных без длительности и без погоды.
                    .filter { !it.isActive }
                    .map { trip ->
                        val own = byTrip[trip.id].orEmpty()
                        ArchiveRow(trip, own.size, own.sumOf { it.weightGrams ?: 0 })
                    }
            }.collect { rows ->
                _uiState.value = FishArchiveUiState(isLoading = false, rows = rows)
            }
        }
    }
}
