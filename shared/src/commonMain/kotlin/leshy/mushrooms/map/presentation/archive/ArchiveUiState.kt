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
) {
    val isSelectionMode: Boolean get() = selectedWalkIds.isNotEmpty()
}
