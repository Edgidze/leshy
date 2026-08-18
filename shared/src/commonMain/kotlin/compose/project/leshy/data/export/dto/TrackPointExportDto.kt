package compose.project.leshy.data.export.dto

import kotlinx.serialization.Serializable

/** Mirrors `TrackPointEntity` minus `id`/`walkId` — both are device-local. */
@Serializable
data class TrackPointExportDto(
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val elevation: Double?,
    val sequence: Int,
)
