package compose.project.leshy.domain.model

enum class OfflineRegionStatus { PAUSED, DOWNLOADING, COMPLETE, ERROR }

data class OfflineRegionInfo(
    val name: String,
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double,
    val minZoom: Int,
    val maxZoom: Int,
    val status: OfflineRegionStatus,
    val completedTileCount: Long,
    val completedBytes: Long,
    val requiredTileCount: Long?,
)
