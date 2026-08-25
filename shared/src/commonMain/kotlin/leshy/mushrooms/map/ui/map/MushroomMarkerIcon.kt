package leshy.mushrooms.map.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import leshy.mushrooms.map.data.platform.PhotoStorage
import leshy.mushrooms.map.data.platform.disallowHardwareBitmaps
import leshy.mushrooms.map.domain.model.CategoryIconSource
import leshy.mushrooms.map.domain.model.MUSHROOM_MARKER_SIZE_SCALE_DEFAULT
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

/** Unscaled bitmap size requested from MapLibre for a mushroom photo marker — the "base" size the
 * user's size setting (`SettingsScreen`) scales up/down. */
val MUSHROOM_MARKER_BASE_SIZE: Dp = 64.dp

/** Current value of the user's marker size setting, provided at the app root from
 * `SettingsRepository.observeMushroomMarkerSizeScale()` — same pattern as `i18n.LocalAppLanguage`. */
val LocalMushroomMarkerSizeScale = compositionLocalOf { MUSHROOM_MARKER_SIZE_SCALE_DEFAULT }

/** [MUSHROOM_MARKER_BASE_SIZE] scaled by the user's current size setting — what map layers should
 * actually request from MapLibre for a mushroom marker's bitmap size. */
val mushroomMarkerSize: Dp
    @Composable get() = MUSHROOM_MARKER_BASE_SIZE * LocalMushroomMarkerSizeScale.current

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

/**
 * Returns a composite marker [Painter] for [source], or null when its image isn't available —
 * no matching bundled drawable, or a user file that hasn't finished loading yet (the marker
 * simply appears once it has).
 */
@Composable
fun rememberMushroomMarkerPainter(source: CategoryIconSource): Painter? {
    val photo = when (source) {
        is CategoryIconSource.Bundled -> Res.allDrawableResources[source.iconRef]?.let { painterResource(it) }
        is CategoryIconSource.UserFile -> rememberUserIconPainter(source.fileName)
    }
    return remember(photo) { photo?.let { MushroomMarkerPainter(it) } }
}

/**
 * Loads a user-created species' illustration from disk as a [Painter] for MapLibre to bake.
 * Deliberately the same shape as [rememberPlaceMarkerPainter] — see its doc for why the painter
 * has to be taken out of [coil3.compose.AsyncImagePainter.State.Success] (a painter keeping its
 * identity across loading→loaded gets baked permanently blank), why the request needs an explicit
 * [ImageRequest.size] (nothing ever draws this painter, so Coil's draw-time size inference never
 * fires and it decodes at full resolution), and why hardware bitmaps must be disallowed (MapLibre
 * bakes onto a software canvas and throws on them).
 */
@Composable
private fun rememberUserIconPainter(fileName: String): Painter? {
    val photoStorage = koinInject<PhotoStorage>()
    val platformContext = LocalPlatformContext.current
    val sizePx = with(LocalDensity.current) { mushroomMarkerSize.roundToPx() }
    var loadedPainter by remember(fileName) { mutableStateOf<Painter?>(null) }
    val request = remember(fileName, sizePx, platformContext, photoStorage) {
        ImageRequest.Builder(platformContext)
            .data("file://${photoStorage.resolvePath(fileName)}")
            .size(sizePx, sizePx)
            .disallowHardwareBitmaps()
            .build()
    }
    rememberAsyncImagePainter(
        model = request,
        onSuccess = { state -> loadedPainter = state.painter },
    )
    return loadedPainter
}
