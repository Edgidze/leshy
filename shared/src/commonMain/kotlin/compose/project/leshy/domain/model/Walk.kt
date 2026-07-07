package compose.project.leshy.domain.model

data class Walk(
    val id: Long,
    val name: String,
    val startTime: Long,
    val endTime: Long?,
    val distanceMeters: Double,
    val avgSpeed: Double,
    val startLat: Double,
    val startLon: Double,
    val endLat: Double?,
    val endLon: Double?,
    val mushroomCount: Int,
)
