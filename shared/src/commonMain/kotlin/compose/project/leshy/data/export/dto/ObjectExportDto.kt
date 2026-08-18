package compose.project.leshy.data.export.dto

import kotlinx.serialization.Serializable

/** Path of a find's photo within its walk directory, relative to the walk directory root (i.e.
 * as stored in [ObjectExportDto.photoFile]) — keyed by the original (export-time, device-local)
 * object id purely to keep filenames unique and human-browsable; import discards it. */
fun photoEntryName(originalObjectId: Long, extension: String) = "photos/$originalObjectId.$extension"

/** Mirrors `ObjectEntity` minus `id`/`walkId`/`categoryId`: `categoryId` is replaced by
 * [categoryNameKey] (stable across installs, `CategoryEntity.nameKey`; a numeric category id
 * isn't — the 30-mushroom catalog is reseeded per install, not exported/imported itself). `type`
 * is `ObjectType.name` as plain text rather than importing the Room entity's enum type directly,
 * so this DTO layer stays decoupled from Room. [photoFile] is the archive-relative path from
 * [photoEntryName], or null if the find has no photo. */
@Serializable
data class ObjectExportDto(
    val categoryNameKey: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long,
    val type: String,
    val photoFile: String?,
    val name: String?,
    val description: String?,
)
