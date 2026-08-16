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
 * (normally TopStart) — and our own "Filters: N" button (see RecordScreen.kt/MapScreen.kt) sits
 * at TopStart to clear the compass in exchange, pushed down far enough to clear the scale bar,
 * since (unlike the compass, only visible while rotated) the scale bar is always on screen.
 *
 * Scale bar goes on the START side specifically because of how the two platforms draw it: on
 * Android, `android-plugin-scalebar-v9`'s `ScaleBarWidget.onDraw()` always paints growing
 * rightward from a fixed `marginLeft` (set once from the *maximum* possible bar width, not the
 * live one — internal to maplibre-compose, not something we can hook into), so its LEFT edge is
 * what stays visually anchored regardless of alignment; putting it at TopStart makes that fixed
 * edge coincide with the actual left margin instead of floating short of the TopEnd slot's right
 * edge. On iOS, `MLNScaleBar` is a real UIView pinned via Auto Layout, so a leading (TopStart)
 * constraint likewise keeps its left edge fixed while it shrinks/grows to the right. Net result:
 * left-edge-fixed, growing-right on both platforms — visually consistent left-to-right.
 */
val mapOrnamentOptions = OrnamentOptions(
    compassAlignment = Alignment.TopEnd,
    scaleBarAlignment = Alignment.TopStart,
)
