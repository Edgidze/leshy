package leshy.mushrooms.map.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.CategorySource

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameKey: String,
    val colorHex: String,
    val iconRef: String?,
    val order: Int,
    val isActive: Boolean,
    val isPicked: Boolean = true,
    val isFilterEligible: Boolean = true,
    val source: CategorySource = CategorySource.APP,
    /** Stored as a JSON object keyed by [AppLanguage.code] — see `Converters`. */
    val customNames: Map<AppLanguage, String> = emptyMap(),
    val scientificName: String? = null,
    val iconFile: String? = null,
)
