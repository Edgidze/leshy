package compose.project.leshy.data.platform

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberShareLauncher(): (ShareContent) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { content ->
            val imageUris = content.imagePaths.map { path ->
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))
            }
            val intent = Intent(if (imageUris.size > 1) Intent.ACTION_SEND_MULTIPLE else Intent.ACTION_SEND).apply {
                // "image/*", not the exact "image/png" — several share targets (observed: Telegram)
                // only treat a multi-attachment ACTION_SEND_MULTIPLE as a proper photo group (and
                // show EXTRA_TEXT as its caption) when the declared type is the wildcard form; an
                // exact subtype was silently dropping the caption text with 2+ images attached.
                type = if (imageUris.isEmpty()) "text/plain" else "image/*"
                putExtra(Intent.EXTRA_TEXT, content.text)
                when {
                    imageUris.size > 1 -> putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
                    imageUris.size == 1 -> putExtra(Intent.EXTRA_STREAM, imageUris.first())
                }
                if (imageUris.isNotEmpty()) addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, null))
        }
    }
}
