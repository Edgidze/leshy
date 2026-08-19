package compose.project.leshy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import compose.project.leshy.domain.model.AppLanguage
import compose.project.leshy.domain.model.CategorySource
import compose.project.leshy.domain.model.EdibilityStatus

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameKey: String,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    val isActive: Boolean,
    val edibilityStatus: EdibilityStatus,
    val isPicked: Boolean = true,
    val isFilterEligible: Boolean = true,
    val source: CategorySource = CategorySource.APP,
    /** Stored as a JSON object keyed by [AppLanguage.code] — see `Converters`. */
    val customNames: Map<AppLanguage, String> = emptyMap(),
    val scientificName: String? = null,
    val iconFile: String? = null,
)
