package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable

/**
 * Returns a function that, when invoked, calls [onGranted] immediately if CAMERA permission is
 * already granted, or requests it first and calls [onGranted] only once the user actually grants
 * it — the OS permission prompt is triggered lazily, on demand, not proactively.
 *
 * [onDenied] fires when the user refuses (or has already refused permanently, in which case no
 * prompt appears at all). Without it, refusing left the photo button as a button that silently
 * does nothing every time it is tapped, with no way for the user to learn why.
 */
@Composable
expect fun rememberCameraPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit,
): () -> Unit
