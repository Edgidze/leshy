package leshy.mushrooms.map.domain.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.tan

// Caps how detailed ("zoomed in") an offline download gets and how far it falls back for zooming
// out while offline — kept out of the UI entirely (see PreparationScreen.kt): the user picks an
// area on the map, not a zoom level, and this adapts the detail level down automatically for
// larger areas so no selection ever blows past TILE_BUDGET.
// 14 because that is OpenFreeMap's planet source's own maxzoom — the deepest tile that exists.
// Asking the native downloader for more never downloaded anything extra (mbgl clamps the tile
// pyramid to the source's zoom range), but it DID poison the budget below: z15 is 4x and z16 is 16x
// the tile count of z14, so counting those phantom levels burned the budget on tiles that were never
// going to be fetched: minZoom came out two levels shallower than it should (z10 instead of z8,
// leaving almost no room to zoom out offline), and large areas got pushed below z14 — the one level
// that carries buildings, POIs and most labels.
private const val MAX_DETAIL_ZOOM = 14
private const val MIN_DETAIL_ZOOM = 5
private const val ZOOM_FALLBACK_SPAN = 6
private const val TILE_BUDGET = 6_000L

/** The zoom pyramid a selected area will be downloaded at. Carries no size forecast on purpose:
 * tile weight at one zoom swings ~10x between open forest and dense city, so a single predicted
 * megabyte figure is wrong for most terrain (measured 2026-09-02, see `ui/map/CLAUDE.md`). */
data class OfflineRegionEstimate(val minZoom: Int, val maxZoom: Int)

fun estimateOfflineRegion(west: Double, south: Double, east: Double, north: Double): OfflineRegionEstimate {
    var maxZoom = MAX_DETAIL_ZOOM
    while (maxZoom > MIN_DETAIL_ZOOM) {
        val minZoom = (maxZoom - ZOOM_FALLBACK_SPAN).coerceAtLeast(MIN_DETAIL_ZOOM)
        if (tileCount(west, south, east, north, minZoom, maxZoom) <= TILE_BUDGET) {
            return OfflineRegionEstimate(minZoom, maxZoom)
        }
        maxZoom--
    }
    return OfflineRegionEstimate(MIN_DETAIL_ZOOM, MIN_DETAIL_ZOOM)
}

// Standard Web Mercator slippy-tile math — the same scheme OfflinePackDefinition.TilePyramid
// downloads against.
private fun tileCount(west: Double, south: Double, east: Double, north: Double, minZoom: Int, maxZoom: Int): Long =
    (minZoom..maxZoom).sumOf { zoom -> tilesAtZoom(west, south, east, north, zoom) }

private fun tilesAtZoom(west: Double, south: Double, east: Double, north: Double, zoom: Int): Long {
    val tilesPerAxis = 1L shl zoom
    val xMin = lonToTileX(west, tilesPerAxis)
    val xMax = lonToTileX(east, tilesPerAxis)
    val yMin = latToTileY(north, tilesPerAxis)
    val yMax = latToTileY(south, tilesPerAxis)
    return (xMax - xMin + 1) * (yMax - yMin + 1)
}

private fun lonToTileX(lon: Double, tilesPerAxis: Long): Long =
    (((lon + 180.0) / 360.0) * tilesPerAxis).toLong().coerceIn(0, tilesPerAxis - 1)

private fun latToTileY(lat: Double, tilesPerAxis: Long): Long {
    val latRad = lat * PI / 180.0
    val y = (1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * tilesPerAxis
    return y.toLong().coerceIn(0, tilesPerAxis - 1)
}
