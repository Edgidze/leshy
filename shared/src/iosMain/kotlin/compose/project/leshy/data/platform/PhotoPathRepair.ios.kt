package compose.project.leshy.data.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val DOCUMENTS_MARKER = "/Documents/"

// iOS can reassign the app's sandbox container UUID (baked into every absolute path saveImage/
// IosPhotoStorage/IosWalkThumbnailRenderer write) across some update/restore events — the file
// itself survives the move, only the old UUID prefix goes stale. The part after "/Documents/" is
// stable across the move, so rebuild the path against the CURRENT Documents dir and confirm the
// file is actually there before trusting it.
@OptIn(ExperimentalForeignApi::class)
actual fun repairStalePhotoPath(storedPath: String): String? {
    val markerIndex = storedPath.lastIndexOf(DOCUMENTS_MARKER)
    if (markerIndex < 0) return null
    val relativePath = storedPath.substring(markerIndex + DOCUMENTS_MARKER.length)
    val documentsPath = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: return null
    val candidatePath = "$documentsPath/$relativePath"
    return candidatePath.takeIf { NSFileManager.defaultManager.fileExistsAtPath(candidatePath) }
}
