package compose.project.leshy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walks")
data class WalkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val startTime: Long,
    val endTime: Long?,
    val distanceMeters: Double,
    val avgSpeed: Double,
    val startLat: Double,
    val startLon: Double,
    val endLat: Double?,
    val endLon: Double?,
    val mushroomCount: Int,
)
