package compose.project.leshy.ui.map

import androidx.compose.ui.Alignment
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions

/**
 * SurfaceView (the library default) renders on its own compositor layer and ignores the
 * graphicsLayer alpha Compose Navigation applies during screen transitions, so the map stays
 * fully opaque and visibly bleeds through the fade into the next screen. TextureView participates
 * in normal View compositing and fades correctly, at some rendering performance cost — only
 * relevant on Android, since RenderOptions.RenderMode doesn't exist on iOS.
 */
expect val mapRenderOptions: RenderOptions

/**
 * Swaps the library's default corners for the compass (normally TopEnd) and the scale bar
 * (normally TopStart): the compass would otherwise land exactly on top of our own "Filters: N"
 * button, which also sits at TopEnd (see RecordScreen.kt/MapScreen.kt) — moving the compass to
 * TopStart clears that collision and puts it where the user wants it. The scale bar swaps to
 * TopEnd in exchange; MapScreen.kt/RecordScreen.kt push the "Filters: N" button down far enough
 * to clear it, since (unlike the compass, only visible while rotated) the scale bar is always on
 * screen.
 */
val mapOrnamentOptions = OrnamentOptions(
    compassAlignment = Alignment.TopStart,
    scaleBarAlignment = Alignment.TopEnd,
)
