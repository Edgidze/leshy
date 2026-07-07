package compose.project.leshy.domain.model

enum class MarkType {
    MUSHROOM,
    PHOTO,
    POI,
}

data class FieldMark(
    val id: Long,
    val walkId: Long,
    val categoryId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val type: MarkType,
    val photoPath: String?,
)
