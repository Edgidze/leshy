package compose.project.leshy.ui.map

import org.maplibre.compose.map.RenderOptions

/**
 * SurfaceView (the library default) renders on its own compositor layer and ignores the
 * graphicsLayer alpha Compose Navigation applies during screen transitions, so the map stays
 * fully opaque and visibly bleeds through the fade into the next screen. TextureView participates
 * in normal View compositing and fades correctly, at some rendering performance cost — only
 * relevant on Android, since RenderOptions.RenderMode doesn't exist on iOS.
 */
expect val mapRenderOptions: RenderOptions
