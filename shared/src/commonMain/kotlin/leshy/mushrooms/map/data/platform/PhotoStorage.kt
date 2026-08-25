package leshy.mushrooms.map.data.platform

/**
 * Resolves where a newly imported photo should be written on disk — mirrors how
 * [rememberCameraLauncher]/[WalkThumbnailRenderer] resolve their own storage location rather than
 * taking one from the caller, since only platform code has a [android.content.Context]/
 * `NSFileManager` to resolve it from. Writing the actual bytes is cross-platform (okio) and stays
 * in the caller (`ImportDataUseCase`) — this interface only answers "where".
 */
interface PhotoStorage {
    /** Absolute path for a new photo file named [fileName] inside this app's own photo storage. */
    fun resolvePath(fileName: String): String
}
