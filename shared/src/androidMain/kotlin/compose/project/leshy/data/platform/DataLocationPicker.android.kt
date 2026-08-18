package compose.project.leshy.data.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.buffer
import okio.sink

@Composable
actual fun rememberExportLauncher(
    suggestedFileName: String,
    writeArchive: suspend (BufferedSink) -> Unit,
    onResult: (Result<Unit>) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val stream = context.contentResolver.openOutputStream(uri)
                        ?: error("Could not open $uri for writing")
                    stream.use { it.sink().buffer().use { sink -> writeArchive(sink) } }
                }
            }
            onResult(result)
        }
    }
    return remember(launcher, suggestedFileName) { { launcher.launch(suggestedFileName) } }
}

@Composable
actual fun rememberImportFilePicker(onPicked: (PickedLocation) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onPicked(PickedLocation(uri.displayName(), uri.toString()))
    }
    return remember(launcher) {
        { launcher.launch(arrayOf("application/zip", "application/x-zip-compressed")) }
    }
}

// SAF tree/document URIs end in a path segment like "primary:Documents/MyFolder" — the part after
// the last '/' (or ':' if there's no subfolder) is the only bit worth showing to the user.
private fun Uri.displayName(): String =
    lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':') ?: toString()
