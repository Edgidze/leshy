package compose.project.leshy.data.export.dto

import kotlinx.serialization.Serializable

/** Format version of the export archive itself — independent of the Room schema version, so a
 * future DB migration doesn't force a new archive format. Bump only when [ExportManifestDto],
 * [WalkExportDto], [TrackPointExportDto] or [ObjectExportDto] change shape. */
const val EXPORT_SCHEMA_VERSION = 1

const val MANIFEST_ENTRY_NAME = "manifest.json"

@Serializable
data class ExportManifestDto(
    val schemaVersion: Int,
    val appVersion: String,
    val exportedAt: Long,
    val walkCount: Int,
)
