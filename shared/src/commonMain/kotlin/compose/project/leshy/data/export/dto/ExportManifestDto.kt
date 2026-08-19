package compose.project.leshy.data.export.dto

import kotlinx.serialization.Serializable

/** Format version of the export archive itself — independent of the Room schema version, so a
 * future DB migration doesn't force a new archive format. Bump only when [ExportManifestDto],
 * [WalkExportDto], [TrackPointExportDto], [ObjectExportDto] or `CategoryExportDto` change shape.
 * v2 (`.claude/plans/user-mushrooms.md`, Phase 6) added the `categories/` folder — an archive
 * without it is still v1-compatible, [ImportDataUseCase] just skips that section. */
const val EXPORT_SCHEMA_VERSION = 2

const val MANIFEST_ENTRY_NAME = "manifest.json"

@Serializable
data class ExportManifestDto(
    val schemaVersion: Int,
    val exportedAt: Long,
    val walkCount: Int,
)
