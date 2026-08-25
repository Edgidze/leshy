package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberShareLauncher(): (ShareContent) -> Unit =
    remember {
        { content ->
            val activityItems = listOf(content.text) + content.imagePaths.map { NSURL.fileURLWithPath(it) }
            val activityViewController = UIActivityViewController(
                activityItems = activityItems,
                applicationActivities = null,
            )
            UIApplication.sharedApplication.keyWindow
                ?.rootViewController
                ?.presentViewController(activityViewController, animated = true, completion = null)
        }
    }
