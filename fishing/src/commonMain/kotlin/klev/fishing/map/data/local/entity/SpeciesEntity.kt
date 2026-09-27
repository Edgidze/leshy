package klev.fishing.map.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import klev.fishing.map.domain.model.SpeciesSource

@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val scientificName: String?,
    val colorHex: String,
    val iconRef: String?,
    val sortOrder: Int,
    val isActive: Boolean,
    val source: SpeciesSource,
    /** JSON `{"RU":"…","EN":"…"}`; пусто у каталожных видов. */
    val customNames: String? = null,
)
