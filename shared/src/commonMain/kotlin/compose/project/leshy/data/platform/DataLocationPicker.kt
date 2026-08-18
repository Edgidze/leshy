package compose.project.leshy.data.platform

import androidx.compose.runtime.Composable
import okio.BufferedSink

/** Display label and platform-specific handle for a file the user picked via a system picker. */
data class PickedLocation(val displayName: String, val handle: String)

/**
 * Runs the platform's "save a new zip archive" flow, offering [suggestedFileName] as the default
 * name. On Android this is a single `CreateDocument` system dialog that returns a writable
 * destination immediately; there's no iOS equivalent of that combined dialog, so there the archive
 * is written to a temp file first and `UIDocumentPickerViewController` (exporting mode) is shown
 * only afterwards, to let the user copy it to its final location — see
 * `.claude/plans/export-import.md` Step 7. Either way [writeArchive] is invoked exactly once with
 * a sink to fill, and [onResult] reports the outcome once the whole flow finishes.
 */
@Composable
expect fun rememberExportLauncher(
    suggestedFileName: String,
    writeArchive: suspend (BufferedSink) -> Unit,
    onResult: (Result<Unit>) -> Unit,
): () -> Unit

/**
 * Opens the platform's system file picker restricted to zip archives. On success [onPicked]
 * receives a display label plus a platform-specific handle (content Uri on Android, sandboxed file
 * path on iOS) that [ArchiveFileReader.readBytes] can later resolve into the archive's bytes.
 */
@Composable
expect fun rememberImportFilePicker(onPicked: (PickedLocation) -> Unit): () -> Unit
