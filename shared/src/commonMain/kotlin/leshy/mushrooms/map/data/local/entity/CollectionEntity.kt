package leshy.mushrooms.map.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import leshy.mushrooms.map.domain.model.CollectionSource

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameKey: String,
    val order: Int,
    val source: CollectionSource = CollectionSource.COUNTRY,
    val name: String? = null,
)
