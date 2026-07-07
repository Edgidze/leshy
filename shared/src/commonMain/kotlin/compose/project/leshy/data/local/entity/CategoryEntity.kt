package compose.project.leshy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameKey: String,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    val isActive: Boolean,
)
