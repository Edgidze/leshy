package leshy.mushrooms.map.data.export.dto

import kotlinx.serialization.Serializable

const val WALK_ENTRY_NAME = "walk.json"
const val TRACK_ENTRY_NAME = "track.json"
const val OBJECTS_ENTRY_NAME = "objects.json"

fun walkDirectory(originalWalkId: Long) = "walks/walk-$originalWalkId"

/** Mirrors `WalkEntity` minus `id`/`thumbnailPath` — a device-local id can't round-trip through
 * an archive, and the thumbnail is a derived map snapshot (see [ObjectExportDto] for photos,
 * which are original data and do need to travel with the archive). */
@Serializable
data class WalkExportDto(
    val originalId: Long,
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
    val description: String? = null,
)
