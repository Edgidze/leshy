package leshy.mushrooms.map.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

// Flat in Documents, same as rememberCameraLauncher (CameraLauncher.ios.kt) already writes
// captured photos into — no "photos" subdirectory on this platform.
class IosPhotoStorage : PhotoStorage {
    @OptIn(ExperimentalForeignApi::class)
    override fun resolvePath(fileName: String): String {
        val documentsPath = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )?.path ?: error("Could not resolve Documents directory")
        return "$documentsPath/$fileName"
    }
}
