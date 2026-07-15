package compose.project.leshy.ui.map

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

/** Overall on-screen diameter of a mushroom photo marker, rings included. */
val MUSHROOM_MARKER_SIZE: Dp = 32.dp

private val MARKER_RING_WIDTH = 2.dp

/**
 * Draws a mushroom photo marker: grey disc (same [MaterialTheme.colorScheme.surfaceContainerHighest]
 * background as behind the photo on the Record screen's tiles, so the source PNGs' transparent
 * edges read the same way) behind a circle-clipped, aspect-fit photo, ringed white-then-black —
 * both rings [MARKER_RING_WIDTH], matching the width the current-location dot's stroke already
 * used. Built as a hand-drawn [Painter] (not a plain [image] of the raw PNG) because MapLibre's
 * `iconImage` only rasterizes whatever a single [Painter] draws — the background/rings have to be
 * baked into that same bitmap.
 */
private class MushroomMarkerPainter(
    private val photo: Painter,
    private val backgroundColor: Color,
) : Painter() {
    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val outerRadius = size.minDimension / 2f
        val ringWidthPx = MARKER_RING_WIDTH.toPx()
        val whiteRadius = outerRadius - ringWidthPx
        val imageRadius = whiteRadius - ringWidthPx
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(color = Color.Black, radius = outerRadius, center = center)
        drawCircle(color = Color.White, radius = whiteRadius, center = center)
        drawCircle(color = backgroundColor, radius = imageRadius, center = center)

        val clip = Path().apply { addOval(Rect(center = center, radius = imageRadius)) }
        clipPath(clip) {
            val box = imageRadius * 2f
            val intrinsic = photo.intrinsicSize
            val drawSize = if (intrinsic.isSpecified && intrinsic.minDimension > 0f) {
                val scale = minOf(box / intrinsic.width, box / intrinsic.height)
                Size(intrinsic.width * scale, intrinsic.height * scale)
            } else {
                Size(box, box)
            }
            translate(left = center.x - drawSize.width / 2f, top = center.y - drawSize.height / 2f) {
                with(photo) { draw(drawSize) }
            }
        }
    }
}

/** Returns a composite marker [Painter] for [iconRef], or null if there's no matching drawable. */
@Composable
fun rememberMushroomMarkerPainter(iconRef: String): Painter? {
    val drawable = Res.allDrawableResources[iconRef] ?: return null
    val photo = painterResource(drawable)
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest
    return remember(photo, backgroundColor) { MushroomMarkerPainter(photo, backgroundColor) }
}
