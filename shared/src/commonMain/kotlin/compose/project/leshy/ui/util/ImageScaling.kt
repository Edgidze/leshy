package compose.project.leshy.ui.util

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

/**
 * Returns a copy of this bitmap whose longest side is [maxDimension], or the bitmap itself if it's
 * already that small (never upscales). Aspect ratio is preserved.
 *
 * Plain Compose graphics, so it works identically on both platforms: the icon editor's final
 * render to a fixed-size [ImageBitmap] uses exactly the same
 * [Canvas]-over-an-[ImageBitmap] mechanism (`.claude/plans/user-mushrooms.md`, Phase 3).
 */
fun ImageBitmap.scaledToMaxDimension(maxDimension: Int): ImageBitmap {
    val longestSide = maxOf(width, height)
    if (longestSide <= maxDimension) return this

    val scale = maxDimension.toFloat() / longestSide
    val targetWidth = (width * scale).roundToInt().coerceAtLeast(1)
    val targetHeight = (height * scale).roundToInt().coerceAtLeast(1)

    val scaled = ImageBitmap(targetWidth, targetHeight)
    Canvas(scaled).drawImageRect(
        image = this,
        srcOffset = IntOffset.Zero,
        srcSize = IntSize(width, height),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(targetWidth, targetHeight),
        // Downscaling by a large factor with the default (low) quality visibly aliases a detailed
        // photo; this is a one-shot operation on a small image, so quality wins over speed.
        paint = Paint().apply { filterQuality = FilterQuality.High },
    )
    return scaled
}
