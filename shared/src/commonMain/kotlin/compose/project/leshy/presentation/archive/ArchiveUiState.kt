package compose.project.leshy.presentation.archive

import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.Walk

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
