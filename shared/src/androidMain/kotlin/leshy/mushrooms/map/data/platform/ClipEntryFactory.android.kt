package leshy.mushrooms.map.data.platform

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.toClipEntry

actual fun plainTextClipEntry(text: String): ClipEntry =
    ClipData.newPlainText("coordinates", text).toClipEntry()
