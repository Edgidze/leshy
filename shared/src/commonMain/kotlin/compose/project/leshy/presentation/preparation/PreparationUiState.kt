package compose.project.leshy.presentation.preparation

import compose.project.leshy.domain.model.OfflineRegionInfo

data class PendingRegionSelection(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
    val minZoom: Int,
    val maxZoom: Int,
    val estimatedBytes: Long,
)

data class PreparationUiState(
    val regions: List<OfflineRegionInfo> = emptyList(),
    val showNameDialog: Boolean = false,
    val nameInput: String = "",
    val pendingSelection: PendingRegionSelection? = null,
    val regionPendingDelete: String? = null,
    /** True once a just-started download turned out to use a different tile-URL template than
     * the live map's pinned copy (server-side style changed since the last pin) — user needs to
     * refresh map data in Settings to bring everything back in sync. Stays visible until
     * explicitly dismissed, since nothing on this screen can detect that the user went and fixed
     * it elsewhere. */
    val styleDriftWarningVisible: Boolean = false,
)
