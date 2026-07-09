package compose.project.leshy.domain.model

data class Category(
    val id: Long,
    val nameKey: String,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    val isActive: Boolean,
    val edibilityStatus: EdibilityStatus,
)
