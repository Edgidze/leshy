package compose.project.leshy.data.platform

import androidx.compose.runtime.Composable

/** Display label for a folder/file the user picked via the system picker. */
data class PickedLocation(val displayName: String)

/**
 * Opens the platform's system folder picker (Storage Access Framework tree picker on Android,
 * `UIDocumentPickerViewController` in folder mode on iOS). On success [onPicked] receives a
 * display label for the chosen folder.
 */
@Composable
expect fun rememberExportFolderPicker(onPicked: (PickedLocation) -> Unit): () -> Unit

/**
 * Opens the platform's system file picker restricted to zip archives. On success [onPicked]
 * receives a display label for the chosen file.
 */
@Composable
expect fun rememberImportFilePicker(onPicked: (PickedLocation) -> Unit): () -> Unit
