package compose.project.leshy.ui.map

import org.maplibre.compose.style.BaseStyle

/**
 * OpenFreeMap's "liberty" vector style — free, no API key, no usage quota (unlike
 * tile.openstreetmap.org's raster tiles, which are a light-use-only community service and
 * render blurry on HiDPI screens since they're not offered in a retina/@2x variant).
 *
 * Exposed as a raw string too (not just wrapped in [BaseStyle.Uri]) because the native
 * off-screen snapshotters used for archive-card thumbnails (Android `MapSnapshotter`, iOS
 * `MLNMapSnapshotter`) take a plain style URL, not the maplibre-compose `BaseStyle` wrapper.
 */
internal const val OPEN_FREE_MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
internal val OpenFreeMapStyle = BaseStyle.Uri(OPEN_FREE_MAP_STYLE_URL)
