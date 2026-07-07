package compose.project.leshy.domain.model

data class GeoPoint(
    val lat: Double,
    val lon: Double,
    val elevation: Double?,
    val timestamp: Long,
)
