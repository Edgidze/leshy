package compose.project.leshy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_points",
    foreignKeys = [
        ForeignKey(
            entity = WalkEntity::class,
            parentColumns = ["id"],
            childColumns = ["walkId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("walkId")],
)
data class TrackPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val walkId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val elevation: Double?,
    val sequence: Int,
)
