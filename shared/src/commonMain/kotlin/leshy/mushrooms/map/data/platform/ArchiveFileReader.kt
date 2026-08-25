package leshy.mushrooms.map.data.platform

/**
 * Reads the bytes behind a [PickedLocation.handle] returned by [rememberImportFilePicker] —
 * mirrors [PhotoStorage]: only platform code has a `ContentResolver`/`NSFileManager` to resolve a
 * handle from, so the caller (`ImportDataUseCase`'s caller in `DataViewModel`) reads the archive
 * bytes here and hands them to the cross-platform use case as a plain `ByteArray`.
 */
interface ArchiveFileReader {
    /** Bytes of the file behind [handle] (a content Uri on Android, a file path on iOS). */
    suspend fun readBytes(handle: String): ByteArray
}
