package compose.project.leshy.data.platform

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onPicked(PickedLocation(context.queryDisplayName(uri), uri.toString()))
    }
    return remember(launcher) {
        { launcher.launch(arrayOf("application/zip", "application/x-zip-compressed")) }
    }
}

// content:// document URIs' lastPathSegment is an opaque provider-assigned document ID (e.g. the
// Downloads provider hands out plain numbers like "3331"), not the file name — OpenableColumns
// .DISPLAY_NAME via a content query is the only reliable cross-provider way to get the real name.
private fun Context.queryDisplayName(uri: Uri): String {
    contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) return cursor.getString(nameIndex)
    }
    return uri.lastPathSegment?.substringAfterLast('/') ?: uri.toString()
}
