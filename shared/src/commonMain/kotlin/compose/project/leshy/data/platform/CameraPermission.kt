package compose.project.leshy.data.platform

import androidx.compose.runtime.Composable

/**
 * Returns a function that, when invoked, calls [onGranted] immediately if CAMERA permission is
 * already granted, or requests it first and calls [onGranted] only once the user actually grants
 * it — the OS permission prompt is triggered lazily, on demand, not proactively.
 */
@Composable
expect fun rememberCameraPermissionRequester(onGranted: () -> Unit): () -> Unit
