package compose.project.leshy.data.platform

import android.content.Context
import java.io.File

// Same "photos" subdirectory rememberCameraLauncher (CameraLauncher.android.kt) already writes
// captured photos into, so imported and camera-captured photos live side by side.
class AndroidPhotoStorage(private val context: Context) : PhotoStorage {
    override fun resolvePath(fileName: String): String {
        val photosDir = File(context.filesDir, "photos").apply { mkdirs() }
        return File(photosDir, fileName).absolutePath
    }
}
