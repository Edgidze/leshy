package leshy.mushrooms.map.data.platform

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidArchiveFileReader(private val context: Context) : ArchiveFileReader {
    override suspend fun readBytes(handle: String): ByteArray = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(Uri.parse(handle))?.use { it.readBytes() }
            ?: error("Could not open $handle for reading")
    }
}
