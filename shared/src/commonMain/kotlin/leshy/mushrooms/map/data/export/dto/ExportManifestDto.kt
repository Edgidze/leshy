package leshy.mushrooms.map.data.export.dto

import kotlinx.serialization.Serializable

/** Format version of the export archive itself — independent of the Room schema version, so a
 * future DB migration doesn't force a new archive format. Bump only when [ExportManifestDto],
 * [WalkExportDto], [TrackPointExportDto], [ObjectExportDto] or `CategoryExportDto` change shape.
 * v2 (`.claude/plans/user-mushrooms.md`, Phase 6) added the `categories/` folder — an archive
 * without it is still v1-compatible, [ImportDataUseCase] just skips that section. v3
 * (`.claude/plans/user-collections.md`) added `collections/` the same additive way: обе секции
 * необязательны на чтении, поэтому старый архив читается новым приложением полностью.
 *
 * Обратной совместимости при этом нет и не задумано: `ValidateImportArchiveUseCase` отклоняет
 * архив, у которого [ExportManifestDto.schemaVersion] больше известной, целиком — v3-архив в
 * сборку без подборок не импортируется вообще. Это сознательная цена: приложение ещё не
 * опубликовано, старых сборок «в поле» не существует. */
const val EXPORT_SCHEMA_VERSION = 3

const val MANIFEST_ENTRY_NAME = "manifest.json"

@Serializable
data class ExportManifestDto(
    val schemaVersion: Int,
    val exportedAt: Long,
    val walkCount: Int,
)
