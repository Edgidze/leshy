package klev.fishing.map.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.LostReason

@Entity(
    tableName = "catches",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SpeciesEntity::class,
            parentColumns = ["id"],
            childColumns = ["speciesId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("tripId"), Index("speciesId")],
)
data class CatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val speciesId: Long,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val weightGrams: Int?,
    val lengthMm: Int?,
    val bait: String?,
    val outcome: CatchOutcome,
    val lostReason: LostReason?,
    val photoPath: String?,
    val note: String?,
)
