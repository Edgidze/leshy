package compose.project.leshy.data.platform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberCameraLauncher(onPhotoCaptured: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val file = pendingPhotoFile
        if (success && file != null) {
            onPhotoCaptured(file.absolutePath)
        }
        pendingPhotoFile = null
    }

    return remember(context) {
        {
            val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
            val file = File(photosDir, "mark_${currentTimeMillis()}.jpg")
            pendingPhotoFile = file
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            launcher.launch(uri)
        }
    }
}
