package leshy.mushrooms.map.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Many-to-many: a species can belong to more than one country's collection.
@Entity(
    tableName = "category_collections",
    primaryKeys = ["categoryId", "collectionId"],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("collectionId")],
)
data class CategoryCollectionCrossRef(
    val categoryId: Long,
    val collectionId: Long,
)
