package klev.fishing.map.presentation.record

import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.model.TripWeather
import leshy.mushrooms.map.domain.model.GeoPoint

data class TripUiState(
    val isLoading: Boolean = true,
    val trip: Trip? = null,
    val distanceMeters: Double = 0.0,
    val track: List<GeoPoint> = emptyList(),
    val currentLocation: GeoPoint? = null,
    val locationAvailable: Boolean = true,
    /** Только показываемые виды: снятые в настройках сюда не попадают. */
    val species: List<FishSpecies> = emptyList(),
    val catches: List<Catch> = emptyList(),
    val recentBaits: List<String> = emptyList(),
    /** Способы, которыми ловит этот человек (настройка «мои способы»): из них и предлагается старт. */
    val methods: Set<FishingMethod> = FishingMethod.entries.toSet(),
    val pressureUnit: PressureUnit = PressureUnit.HPA,
    /**
     * Только что записанный одним касанием улов — одноразовый сигнал для снэкбара «Записано ·
     * Уточнить». Держится в UiState, а не в `remember` экрана: правило проекта про одноразовые
     * сигналы (`ui/navigation/CLAUDE.md`) — иначе при восстановлении экрана через `restoreState`
     * снэкбар показался бы снова.
     */
    val justSavedCatchId: Long? = null,
    /** Открыт диалог погоды по окончании рыбалки — для неё, а не для активной. */
    val weatherPromptTripId: Long? = null,
    val weatherDraft: TripWeather? = null,
    val weatherSuggesting: Boolean = false,
    val weatherSuggestFailed: Boolean = false,
) {
    val isRecording: Boolean get() = trip?.isActive == true

    val countsBySpecies: Map<Long, Int>
        get() = catches.groupingBy { it.speciesId }.eachCount()

    /** Вес только взятой и отпущенной рыбы: упущенную не взвешивают, и приписывать ей ноль в
     *  сумму было бы ложью в обе стороны. */
    val totalWeightGrams: Int
        get() = catches.sumOf { it.weightGrams ?: 0 }

    val keptCount: Int get() = catches.count { it.outcome == CatchOutcome.KEPT }
    val lostCount: Int get() = catches.count { it.outcome == CatchOutcome.LOST }

    fun catchById(id: Long): Catch? = catches.firstOrNull { it.id == id }

    fun speciesById(id: Long): FishSpecies? = species.firstOrNull { it.id == id }
}
