package compose.project.leshy.data.platform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.source
import org.koin.compose.koinInject

/**
 * `PickVisualMedia` rather than a raw `ACTION_OPEN_DOCUMENT`/`GET_CONTENT` intent: it's the modern
 * photo picker (no storage permission at all, and on devices without the system picker the
 * contract transparently falls back to the document UI down to this project's `minSdk 24`). It
 * comes from `androidx.activity`, already a dependency for `rememberCameraLauncher`.
 */
@Composable
actual fun rememberGalleryPicker(onPhotoPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val photoStorage = koinInject<PhotoStorage>()
    val callback = rememberUpdatedState(onPhotoPicked)
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            // The Uri's read grant only lives as long as this result, so the bytes are copied into
            // the app's own photo storage right here — see the expect declaration's doc.
            val path = withContext(Dispatchers.IO) {
                runCatching {
                    val destination = photoStorage.resolvePath("picked_${currentTimeMillis()}.jpg")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileSystem.SYSTEM.sink(destination.toPath()).buffer().use { sink ->
                            sink.writeAll(input.source())
                        }
                    } ?: return@runCatching null
                    destination
                }.getOrNull()
            }
            if (path != null) callback.value(path)
        }
    }

    return remember(launcher) {
        {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}
