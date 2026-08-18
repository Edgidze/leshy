package compose.project.leshy.ui.map

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import compose.project.leshy.data.platform.disallowHardwareBitmaps

/** Bitmap size requested from MapLibre for a place pin — width of the circular "head"; height adds
 * room below it for the pointed tail so the anchor (bottom tip) lands exactly on the coordinate. */
val PLACE_MARKER_WIDTH: Dp = 44.dp
val PLACE_MARKER_HEIGHT: Dp = 56.dp

/**
 * Google-Maps-style pin: a circle (the place's photo, center-cropped, or a generic icon when no
 * photo was added) with a pointed tail below it. Drawn as one filled [Path] (circle + tail
 * triangle merged into a single silhouette) rather than two separate draw calls, so there's no
 * seam between them.
 */
private class PlaceMarkerPainter(
    private val photo: Painter?,
    private val backgroundColor: Color,
    private val fallbackIcon: Painter,
) : Painter() {
    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val w = size.width
        val h = size.height
        val circleRadius = w / 2f
        val circleCenter = Offset(w / 2f, circleRadius)
        val tailHalfWidth = circleRadius * 0.55f
        val tailTopY = circleCenter.y + circleRadius * 0.75f

        val pinPath = Path().apply {
            addOval(Rect(center = circleCenter, radius = circleRadius))
            moveTo(circleCenter.x - tailHalfWidth, tailTopY)
            lineTo(circleCenter.x, h)
            lineTo(circleCenter.x + tailHalfWidth, tailTopY)
            close()
        }
        drawPath(pinPath, color = backgroundColor)

        val innerRadius = circleRadius * 0.86f
        clipPath(Path().apply { addOval(Rect(center = circleCenter, radius = innerRadius)) }) {
            val intrinsic = photo?.intrinsicSize
            if (photo != null && intrinsic != null && intrinsic.isSpecified && intrinsic.minDimension > 0f) {
                // Center-crop: scale up to fully cover the inner circle, cropping the overflow.
                val scale = maxOf(2 * innerRadius / intrinsic.width, 2 * innerRadius / intrinsic.height)
                val drawSize = Size(intrinsic.width * scale, intrinsic.height * scale)
                translate(
                    left = circleCenter.x - drawSize.width / 2f,
                    top = circleCenter.y - drawSize.height / 2f,
                ) {
                    with(photo) { draw(drawSize) }
                }
            } else {
                val iconSize = innerRadius * 2f * 0.6f
                translate(
                    left = circleCenter.x - iconSize / 2f,
                    top = circleCenter.y - iconSize / 2f,
                ) {
                    with(fallbackIcon) { draw(Size(iconSize, iconSize)) }
                }
            }
        }
    }
}

/**
 * Returns a pin-shaped marker [Painter] for a place, showing [photoPath]'s photo inside the pin's
 * head once loaded, or a generic "place" icon while loading/if there's no photo.
 *
 * The loaded photo is picked up via [coil3.compose.AsyncImagePainter.State.Success] rather than
 * passing Coil's own (initially-blank, later-mutated-in-place) painter straight through: MapLibre's
 * `image(painter, ...)` bakes the painter to a bitmap once and caches that bitmap by the painter's
 * object identity (`LayerPropertyCompiler`/`ImageManager.acquirePainter`, not `@Composable`-aware),
 * so a painter that keeps the same identity across its own loading→loaded transition would get
 * baked permanently blank. Only swapping in a genuinely new [Painter] instance once loading
 * succeeds forces MapLibre to re-bake with the real photo.
 *
 * The request is built with an explicit [ImageRequest.size] rather than a bare path/URL model:
 * this painter is never itself drawn (only [coil3.compose.AsyncImagePainter.State.Success]'s inner
 * painter is, above), so Coil's usual draw-time size inference (`DrawScopeSizeResolver`, hooked from
 * `AsyncImagePainter.onDraw`) never fires and it falls back to decoding the source photo at full
 * camera resolution on every load — an unbounded multi-ten-MB decode per marker that reliably OOM-
 * crashed real Android devices right after saving a place with a photo (not reproducible on
 * iOS/emulators with more headroom). Sizing the request to the marker's own pixel size makes Coil
 * downsample during decode instead.
 *
 * The request also disables Android hardware bitmaps ([disallowHardwareBitmaps]) — MapLibre bakes
 * this painter onto a software canvas, which throws if the decoded photo is a hardware bitmap
 * (Coil3-on-Android's default). Confirmed via on-device logcat as the actual crash on save.
 */
@Composable
fun rememberPlaceMarkerPainter(photoPath: String?): Painter {
    val fallbackIcon = rememberVectorPainter(Icons.Filled.Place)
    val backgroundColor = MaterialTheme.colorScheme.primary
    var loadedPhotoPainter by remember(photoPath) { mutableStateOf<Painter?>(null) }
    if (photoPath != null) {
        val density = LocalDensity.current
        val platformContext = LocalPlatformContext.current
        val request = remember(photoPath, density, platformContext) {
            ImageRequest.Builder(platformContext)
                .data("file://$photoPath")
                .size(with(density) { PLACE_MARKER_WIDTH.roundToPx() }, with(density) { PLACE_MARKER_HEIGHT.roundToPx() })
                .disallowHardwareBitmaps()
                .build()
        }
        rememberAsyncImagePainter(
            model = request,
            onSuccess = { state -> loadedPhotoPainter = state.painter },
        )
    }
    return remember(loadedPhotoPainter, backgroundColor) {
        PlaceMarkerPainter(loadedPhotoPainter, backgroundColor, fallbackIcon)
    }
}
