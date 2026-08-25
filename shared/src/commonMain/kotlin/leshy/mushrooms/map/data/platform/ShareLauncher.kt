package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable

/** Everything one call to the system share sheet needs — a text body, plus zero or more absolute
 * local image paths to attach alongside it. */
data class ShareContent(val text: String, val imagePaths: List<String> = emptyList())

/**
 * Opens the platform's native share sheet (`Intent.ACTION_SEND`/`ACTION_SEND_MULTIPLE` on
 * Android, `UIActivityViewController` on iOS) with the given [ShareContent]. Same shape as
 * `rememberExportLauncher` (`DataLocationPicker.kt`) — resolve a platform handle to the content,
 * hand it to the platform's own picker/sheet UI, no result callback needed since the share sheet
 * itself is the end of this flow (nothing to react to once it's shown).
 */
@Composable
expect fun rememberShareLauncher(): (ShareContent) -> Unit
