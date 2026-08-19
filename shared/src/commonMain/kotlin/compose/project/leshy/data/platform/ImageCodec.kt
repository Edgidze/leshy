package compose.project.leshy.data.platform

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Longest-side limit for the working copy the icon editor loads a photo into. A 12 MP camera shot
 * is ~48 MB as ARGB_8888 — editing it at full resolution means guaranteed jank and OOM on weaker
 * Android devices, and the result is downscaled to [CATEGORY_ICON_MAX_DIMENSION] anyway.
 */
const val EDITOR_IMAGE_MAX_DIMENSION = 1536

/**
 * Decodes the image at [path], downscaled so its longest side is at most [maxDimension], applying
 * the photo's own orientation metadata (a phone photo is almost never stored upright).
 * Returns null if the file isn't a decodable image.
 *
 * Platform-specific because the whole point is *not* decoding at full size first: Android samples
 * during decode (`BitmapFactory.inSampleSize`), iOS redraws through UIKit at the target size. Coil
 * — the project's usual cross-platform image path — only produces `Painter`s for display, not a
 * mutable [ImageBitmap] to draw into, so it can't stand in here.
 */
expect suspend fun decodeScaledImage(path: String, maxDimension: Int): ImageBitmap?

/**
 * Encodes [bitmap] as PNG. PNG rather than WebP because the species icon's transparency is the
 * whole point of the editor, and alpha-capable WebP encoding isn't available on iOS without a
 * third-party library — see `.claude/plans/user-mushrooms.md`.
 *
 * The one genuinely irreducible `expect`/`actual` of this feature: Android encodes through
 * `android.graphics.Bitmap`, iOS through Skia (which ships with Compose there, but isn't what
 * Compose draws through on Android). Returns null if the platform encoder fails.
 */
expect fun encodePng(bitmap: ImageBitmap): ByteArray?
