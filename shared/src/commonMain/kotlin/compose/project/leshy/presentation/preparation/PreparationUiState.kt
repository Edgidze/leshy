package compose.project.leshy.presentation.preparation

import compose.project.leshy.domain.model.OfflineRegionInfo

data class PendingRegionSelection(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
    val minZoom: Int,
    val maxZoom: Int,
)

data class PreparationUiState(
    val regions: List<OfflineRegionInfo> = emptyList(),
    val showNameDialog: Boolean = false,
    val nameInput: String = "",
    val pendingSelection: PendingRegionSelection? = null,
    val regionPendingDelete: String? = null,
)
