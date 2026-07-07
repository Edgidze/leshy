package compose.project.leshy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ObjectType {
    MUSHROOM,
    PHOTO,
    POI,
}

@Entity(
    tableName = "objects",
    foreignKeys = [
        ForeignKey(
            entity = WalkEntity::class,
            parentColumns = ["id"],
            childColumns = ["walkId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
        ),
    ],
    indices = [Index("walkId"), Index("categoryId")],
)
data class ObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val walkId: Long,
    val categoryId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val type: ObjectType,
    val photoPath: String?,
)
