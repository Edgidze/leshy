package klev.fishing.map.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_points",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("tripId")],
)
data class TripTrackPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
)
