package klev.fishing.map.presentation.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.domain.repository.CatchRepository
import klev.fishing.map.domain.repository.SpeciesRepository
import klev.fishing.map.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Сколько приманок и видов показывать в сводке. Дальше это уже не «что работало», а список всего. */
private const val TOP_ROWS = 6

/** Приманка и что на неё было: взятая рыба и сходы считаются отдельно — сход тоже поклёвка. */
data class BaitStat(val bait: String?, val caught: Int, val lost: Int) {
    val total: Int get() = caught + lost
}

data class SpeciesStat(val species: FishSpecies, val count: Int)

/** Лучший экземпляр — самая тяжёлая взвешенная рыба. Невзвешенные в сравнении не участвуют. */
data class BestCatch(val item: Catch, val species: FishSpecies?)

data class FishSummaryUiState(
    val isLoading: Boolean = true,
    val tripCount: Int = 0,
    val catchCount: Int = 0,
    val lostCount: Int = 0,
    val totalWeightGrams: Int = 0,
    val best: BestCatch? = null,
    val baits: List<BaitStat> = emptyList(),
    val species: List<SpeciesStat> = emptyList(),
    /** Поклёвки по часам суток, 24 числа. Индекс — час местного времени. */
    val byHour: List<Int> = List(24) { 0 },
    /** Рыба, пойманная в выезды с такой тенденцией давления. Выезды без погоды не считаются. */
    val byPressureTrend: Map<PressureTrend, Int> = emptyMap(),
) {
    val hasData: Boolean get() = catchCount > 0 || lostCount > 0
}

/**
 * «Итоги» — то, ради чего дневник вообще ведут.
 *
 * Сами записи ценности не имеют: ценность появляется на разборе — какие приманки, какие часы и
 * какая погода приносят рыбу (это первое, что говорят про дневниковые приложения те, кто ими
 * пользуется; фактура — `.claude/plans/fishing-ux.md`). Поэтому раздел считает ровно эти четыре
 * вопроса и ничего больше.
 *
 * **Считается в памяти, а не запросами в базу.** Записей у одного рыбака за сезон — сотни, и любая
 * агрегация по ним дешевле, чем поддерживать вторую копию тех же правил на SQL. Когда счёт пойдёт
 * на десятки тысяч, это место и станет поводом для запроса.
 */
class FishSummaryViewModel(
    trips: TripRepository,
    catches: CatchRepository,
    species: SpeciesRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FishSummaryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                trips.observeAll(),
                catches.observeAll(),
                species.observeAll(),
            ) { tripList, catchList, speciesList ->
                val speciesById = speciesList.associateBy { it.id }
                val landed = catchList.filter { it.outcome != CatchOutcome.LOST }
                val timeZone = TimeZone.currentSystemDefault()
                val hours = MutableList(24) { 0 }
                catchList.forEach { item ->
                    val hour = Instant.fromEpochMilliseconds(item.timestamp)
                        .toLocalDateTime(timeZone)
                        .hour
                    hours[hour] = hours[hour] + 1
                }
                val trendByTrip = tripList.mapNotNull { trip ->
                    trip.weather?.pressureTrend?.let { trip.id to it }
                }.toMap()

                FishSummaryUiState(
                    isLoading = false,
                    // Считаются только законченные выезды: идущий ещё не про «что работало».
                    tripCount = tripList.count { !it.isActive },
                    catchCount = landed.size,
                    lostCount = catchList.count { it.outcome == CatchOutcome.LOST },
                    totalWeightGrams = landed.sumOf { it.weightGrams ?: 0 },
                    best = landed
                        .filter { it.weightGrams != null }
                        .maxByOrNull { it.weightGrams ?: 0 }
                        ?.let { BestCatch(it, speciesById[it.speciesId]) },
                    baits = catchList
                        .groupBy { it.bait?.trim()?.lowercase()?.ifBlank { null } }
                        .map { (bait, items) ->
                            BaitStat(
                                // Показывается написание из первой записи, а группировка идёт по
                                // нижнему регистру: «Вертушка» и «вертушка» — одна приманка.
                                bait = items.firstOrNull { it.bait?.isNotBlank() == true }?.bait,
                                caught = items.count { it.outcome != CatchOutcome.LOST },
                                lost = items.count { it.outcome == CatchOutcome.LOST },
                            )
                        }
                        .sortedByDescending { it.total }
                        .take(TOP_ROWS),
                    species = landed
                        .groupingBy { it.speciesId }
                        .eachCount()
                        .mapNotNull { (id, count) -> speciesById[id]?.let { SpeciesStat(it, count) } }
                        .sortedByDescending { it.count }
                        .take(TOP_ROWS),
                    byHour = hours,
                    byPressureTrend = catchList
                        .mapNotNull { item -> trendByTrip[item.tripId] }
                        .groupingBy { it }
                        .eachCount(),
                )
            }.collect { state -> _uiState.value = state }
        }
    }
}
