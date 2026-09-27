package klev.fishing.map.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.data.repository.FishingSettingsRepository
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Вид и сколько его взято за эту рыбалку — строка списка показывает их вместо голого числа. */
data class SpeciesCount(val species: FishSpecies, val count: Int)

data class ArchiveRow(
    val trip: Trip,
    val catchCount: Int,
    val totalWeightGrams: Int,
    /**
     * Виды этой рыбалки, от частого к редкому. Именно по ним выезд и вспоминается («тот, где было
     * шесть окуней»), а «Поймано: 6» не отличает одну рыбалку от другой ничем.
     */
    val speciesCounts: List<SpeciesCount> = emptyList(),
    /** Сходы считаются отдельно: они не улов, но и не пустое место — поклёвка была. */
    val lostCount: Int = 0,
)

data class FishArchiveUiState(
    val isLoading: Boolean = true,
    val rows: List<ArchiveRow> = emptyList(),
    /** Единицы давления из настроек: в строке архива стоит давление, и оно обязано быть в тех же
     *  единицах, что везде (в России их обсуждают в мм рт. ст.). */
    val pressureUnit: PressureUnit = PressureUnit.HPA,
)

class FishArchiveViewModel(
    private val trips: TripRepository,
    private val catches: CatchRepository,
    private val species: SpeciesRepository,
    private val fishingSettings: FishingSettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FishArchiveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                trips.observeAll(),
                catches.observeAll(),
                species.observeAll(),
            ) { tripList, catchList, speciesList ->
                val byTrip: Map<Long, List<Catch>> = catchList.groupBy { it.tripId }
                // Все виды, включая снятые с ленты: рыба, пойманная до того, как вид скрыли,
                // осталась в архиве, и показать её нечем, если брать только активные.
                val speciesById = speciesList.associateBy { it.id }
                tripList
                    // Идущая рыбалка живёт на экране записи, а не в архиве: иначе она стоит в
                    // списке законченных без длительности и без погоды.
                    .filter { !it.isActive }
                    .map { trip ->
                        val own = byTrip[trip.id].orEmpty()
                        val kept = own.filter { it.outcome != CatchOutcome.LOST }
                        ArchiveRow(
                            trip = trip,
                            catchCount = own.size,
                            totalWeightGrams = own.sumOf { it.weightGrams ?: 0 },
                            speciesCounts = kept
                                .groupingBy { it.speciesId }
                                .eachCount()
                                .mapNotNull { (id, count) ->
                                    speciesById[id]?.let { SpeciesCount(it, count) }
                                }
                                .sortedByDescending { it.count },
                            lostCount = own.count { it.outcome == CatchOutcome.LOST },
                        )
                    }
            }.collect { rows ->
                _uiState.update { it.copy(isLoading = false, rows = rows) }
            }
        }
        viewModelScope.launch {
            fishingSettings.observePressureUnit().collect { unit ->
                _uiState.update { it.copy(pressureUnit = unit) }
            }
        }
    }
}
