package compose.project.leshy.ui.map

import org.maplibre.compose.style.BaseStyle

/**
 * OpenFreeMap's "liberty" vector style — free, no API key, no usage quota (unlike
 * tile.openstreetmap.org's raster tiles, which are a light-use-only community service and
 * render blurry on HiDPI screens since they're not offered in a retina/@2x variant).
 */
internal val OpenFreeMapStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty")
