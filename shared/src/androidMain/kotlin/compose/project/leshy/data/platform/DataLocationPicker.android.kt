package compose.project.leshy.data.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberExportFolderPicker(onPicked: (PickedLocation) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) onPicked(PickedLocation(uri.displayName()))
    }
    return remember(launcher) { { launcher.launch(null) } }
}

@Composable
actual fun rememberImportFilePicker(onPicked: (PickedLocation) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onPicked(PickedLocation(uri.displayName()))
    }
    return remember(launcher) {
        { launcher.launch(arrayOf("application/zip", "application/x-zip-compressed")) }
    }
}

// SAF tree/document URIs end in a path segment like "primary:Documents/MyFolder" — the part after
// the last '/' (or ':' if there's no subfolder) is the only bit worth showing to the user.
private fun Uri.displayName(): String =
    lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':') ?: toString()
