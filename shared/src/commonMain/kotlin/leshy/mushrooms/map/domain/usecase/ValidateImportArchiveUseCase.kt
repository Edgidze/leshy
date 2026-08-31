package leshy.mushrooms.map.domain.usecase

import leshy.mushrooms.map.data.export.dto.CATEGORIES_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.CategoryExportDto
import leshy.mushrooms.map.data.export.dto.EXPORT_SCHEMA_VERSION
import leshy.mushrooms.map.data.export.dto.ExportJson
import leshy.mushrooms.map.data.export.dto.ExportManifestDto
import leshy.mushrooms.map.data.export.dto.MANIFEST_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.OBJECTS_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.ObjectExportDto
import leshy.mushrooms.map.data.export.dto.TRACK_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.TrackPointExportDto
import leshy.mushrooms.map.data.export.dto.WALK_ENTRY_NAME
import leshy.mushrooms.map.data.export.dto.WalkExportDto
import leshy.mushrooms.map.data.export.zip.ZipReader
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString

/**
 * Why an archive was rejected — one enum value per message the user is shown. Deliberately coarse:
 * the point is telling the user what to *do* (pick a different file / update the app), not naming
 * the byte that went wrong.
 */
enum class ImportArchiveProblem {
    /** Not readable as a zip at all — a photo, a text file, a truncated download. */
    NOT_AN_ARCHIVE,

    /** A zip, but not one this app wrote: no `manifest.json`, or one that doesn't parse. */
    NOT_A_LESHY_ARCHIVE,

    /** Written by a newer app version whose format this build doesn't know how to read. */
    NEWER_FORMAT,

    /** Ours by the manifest, but nothing inside it can actually be read — a damaged/edited file. */
    DAMAGED_CONTENT,

    /** Structurally fine, just has nothing to import. */
    NO_WALKS,
}

/**
 * Full-archive check run **before** [ImportDataUseCase] writes anything, so a bad file produces an
 * explanation instead of a half-finished import. This matters more than it looks: the import
 * merges the archive's species into the catalog *first*, before the first walk is even parsed, so
 * without this pass a file that fails halfway would still have left new species rows behind.
 *
 * Everything is wrapped: a malformed zip throws from all over [ZipReader] (bad offsets, bad
 * lengths), with exception types and messages that are implementation detail, not something worth
 * surfacing to the user.
 *
 * Two deliberate calibration choices:
 * - A `categories.json` that is present but unparsable is a hard rejection, where
 *   [ImportDataUseCase] alone would silently skip it and quietly drop every find's species onto
 *   `category_misc`. Its ABSENCE stays fine — that is simply a v1 archive.
 * - A single malformed walk is NOT a rejection. [ImportDataUseCase] skips such a walk and reports
 *   it in [ImportDataUseCase.Result.failedWalkCount] on purpose, and throwing away nine good walks
 *   over one bad one would be worse for the user than importing nine and saying so. The archive is
 *   only rejected when NOT ONE walk parses — at that point there is nothing to import and
 *   "damaged" is simply the truth.
 */
class ValidateImportArchiveUseCase {

    /** `null` means the archive is fit to import. */
    operator fun invoke(archiveBytes: ByteArray): ImportArchiveProblem? {
        val reader = runCatching { ZipReader(archiveBytes).also { it.entries } }.getOrNull()
            ?: return ImportArchiveProblem.NOT_AN_ARCHIVE

        val manifestBytes = runCatching { reader.readEntry(MANIFEST_ENTRY_NAME) }.getOrNull()
            ?: return ImportArchiveProblem.NOT_A_LESHY_ARCHIVE
        val manifest = runCatching {
            ExportJson.decodeFromString<ExportManifestDto>(manifestBytes.decodeToString())
        }.getOrNull() ?: return ImportArchiveProblem.NOT_A_LESHY_ARCHIVE

        if (manifest.schemaVersion > EXPORT_SCHEMA_VERSION) return ImportArchiveProblem.NEWER_FORMAT

        val entryNames = runCatching { reader.entries.map { it.name } }.getOrNull()
            ?: return ImportArchiveProblem.DAMAGED_CONTENT

        if (CATEGORIES_ENTRY_NAME in entryNames) {
            val ok = runCatching {
                ExportJson.decodeFromString(
                    ListSerializer(CategoryExportDto.serializer()),
                    reader.readEntry(CATEGORIES_ENTRY_NAME)!!.decodeToString(),
                )
            }.isSuccess
            if (!ok) return ImportArchiveProblem.DAMAGED_CONTENT
        }

        val walkDirs = entryNames
            .filter { it.endsWith("/$WALK_ENTRY_NAME") }
            .map { it.removeSuffix("/$WALK_ENTRY_NAME") }
            .distinct()
        if (walkDirs.isEmpty()) return ImportArchiveProblem.NO_WALKS

        val anyWalkParses = walkDirs.any { dir ->
            runCatching {
                ExportJson.decodeFromString<WalkExportDto>(
                    reader.readEntry("$dir/$WALK_ENTRY_NAME")!!.decodeToString(),
                )
                reader.readEntry("$dir/$TRACK_ENTRY_NAME")?.let {
                    ExportJson.decodeFromString(ListSerializer(TrackPointExportDto.serializer()), it.decodeToString())
                }
                reader.readEntry("$dir/$OBJECTS_ENTRY_NAME")?.let {
                    ExportJson.decodeFromString(ListSerializer(ObjectExportDto.serializer()), it.decodeToString())
                }
            }.isSuccess
        }
        return if (anyWalkParses) null else ImportArchiveProblem.DAMAGED_CONTENT
    }
}
