package leshy.mushrooms.map.data.platform

import okio.FileSystem
import okio.Path.Companion.toPath

// UIDocumentPickerModeImport already copies the picked file into this app's sandbox, so the
// handle from rememberImportFilePicker is already a plain local path here — no ContentResolver
// equivalent needed, unlike AndroidArchiveFileReader.
class IosArchiveFileReader : ArchiveFileReader {
    override suspend fun readBytes(handle: String): ByteArray =
        FileSystem.SYSTEM.read(handle.toPath()) { readByteArray() }
}
