package leshy.mushrooms.map.presentation.archive

import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.Walk

data class WalkArchiveItem(
    val walk: Walk,
    val track: List<GeoPoint>,
    val findLocations: List<GeoPoint>,
)

data class ArchiveUiState(
    val items: List<WalkArchiveItem> = emptyList(),
    val selectedWalkIds: Set<Long> = emptySet(),
    val showDeleteConfirmation: Boolean = false,
    /**
     * `true` до первой выдачи из базы — то есть «список ещё не известен», а не «список пуст».
     * Различать обязательно: без этого экран между composition и первой эмиссией Room показывал
     * пустое состояние, и у архива с прогулками оно мелькало перед самим списком. Обещание
     * «прогулок пока нет» с крупной картинкой — заявление о факте, и делать его до того, как факт
     * известен, нельзя.
     */
    val isLoading: Boolean = true,
) {
    val isSelectionMode: Boolean get() = selectedWalkIds.isNotEmpty()
}
