package leshy.mushrooms.map.data.platform

import androidx.compose.runtime.Composable

/**
 * Returns a launcher that opens the platform's photo-library picker for a single image. On success
 * [onPhotoPicked] is invoked with an absolute path to a copy of the chosen image inside this app's
 * own photo storage ([PhotoStorage]) — the exact same contract as [rememberCameraLauncher], so the
 * two are interchangeable as sources for the species-icon editor.
 *
 * Copying is not optional: Android hands back a `content://` Uri whose read permission dies with
 * the activity result, and iOS hands back an in-memory `UIImage` from an out-of-process picker.
 * Neither is a file this app can re-open later.
 */
@Composable
expect fun rememberGalleryPicker(onPhotoPicked: (String) -> Unit): () -> Unit
