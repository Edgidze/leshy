package compose.project.leshy.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

/** Overall bitmap size requested from MapLibre for a mushroom photo marker. */
val MUSHROOM_MARKER_SIZE: Dp = 64.dp

/**
 * Draws the mushroom photo itself as the marker, aspect-fit within the requested bitmap size —
 * no background disc or ring, the photo's own (already-transparent, see Part 6) silhouette is the
 * whole marker. Built as a hand-drawn [Painter] rather than passing the raw [photo] straight to
 * `image(...)` because MapLibre's `iconImage` rasterizes a [Painter] by insetting to the exact
 * requested bitmap size without preserving aspect ratio on its own — this wrapper computes the
 * fit-scaled size and centers it before delegating the actual drawing to [photo].
 */
private class MushroomMarkerPainter(private val photo: Painter) : Painter() {
    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val box = Size(size.width, size.height)
        val intrinsic = photo.intrinsicSize
        val drawSize = if (intrinsic.isSpecified && intrinsic.minDimension > 0f) {
            val scale = minOf(box.width / intrinsic.width, box.height / intrinsic.height)
            Size(intrinsic.width * scale, intrinsic.height * scale)
        } else {
            box
        }
        translate(left = (box.width - drawSize.width) / 2f, top = (box.height - drawSize.height) / 2f) {
            with(photo) { draw(drawSize) }
        }
    }
}

/** Returns a composite marker [Painter] for [iconRef], or null if there's no matching drawable. */
@Composable
fun rememberMushroomMarkerPainter(iconRef: String): Painter? {
    val drawable = Res.allDrawableResources[iconRef] ?: return null
    val photo = painterResource(drawable)
    return remember(photo) { MushroomMarkerPainter(photo) }
}
