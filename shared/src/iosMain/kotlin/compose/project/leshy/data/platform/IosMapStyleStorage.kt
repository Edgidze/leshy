package compose.project.leshy.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

// Flat in Documents, same as IosPhotoStorage — no "map" subdirectory on this platform.
class IosMapStyleStorage : MapStyleStorage {
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
