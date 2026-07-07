package compose.project.leshy.data.platform

import androidx.compose.runtime.Composable

/**
 * Returns a launcher that opens the platform camera. On success [onPhotoCaptured] is
 * invoked with an absolute path to the saved image file.
 */
@Composable
expect fun rememberCameraLauncher(onPhotoCaptured: (String) -> Unit): () -> Unit
