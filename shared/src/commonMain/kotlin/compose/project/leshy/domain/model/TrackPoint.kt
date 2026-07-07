package compose.project.leshy.domain.model

data class TrackPoint(
    val id: Long,
    val walkId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val elevation: Double?,
    val sequence: Int,
)
