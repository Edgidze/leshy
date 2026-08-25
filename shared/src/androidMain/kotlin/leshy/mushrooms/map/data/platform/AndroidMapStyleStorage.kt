package leshy.mushrooms.map.data.platform

import android.content.Context
import java.io.File

class AndroidMapStyleStorage(private val context: Context) : MapStyleStorage {
    override fun resolvePath(fileName: String): String {
        val mapDir = File(context.filesDir, "map").apply { mkdirs() }
        return File(mapDir, fileName).absolutePath
    }
}
