package compose.project.leshy.domain.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.tan

// Caps how detailed ("zoomed in") an offline download gets and how far it falls back for zooming
// out while offline — kept out of the UI entirely (see PreparationScreen.kt): the user picks an
// area on the map, not a zoom level, and this adapts the detail level down automatically for
// larger areas so no selection ever blows past TILE_BUDGET.
private const val MAX_DETAIL_ZOOM = 16
private const val MIN_DETAIL_ZOOM = 5
private const val ZOOM_FALLBACK_SPAN = 6
private const val TILE_BUDGET = 6_000L

// Rough average payload size for an OpenFreeMap vector tile — real tiles vary a lot with feature
// density (dense city block vs. open forest), so this only has to be right to an order of
// magnitude: enough for "~12 MB" to mean something to a user deciding whether to download, not a
// promise of the exact byte count the native downloader will report once running.
private const val AVG_TILE_BYTES = 20_000L

data class OfflineRegionEstimate(val minZoom: Int, val maxZoom: Int, val estimatedBytes: Long)

fun estimateOfflineRegion(west: Double, south: Double, east: Double, north: Double): OfflineRegionEstimate {
    var maxZoom = MAX_DETAIL_ZOOM
    while (maxZoom > MIN_DETAIL_ZOOM) {
        val minZoom = (maxZoom - ZOOM_FALLBACK_SPAN).coerceAtLeast(MIN_DETAIL_ZOOM)
        val tiles = tileCount(west, south, east, north, minZoom, maxZoom)
        if (tiles <= TILE_BUDGET) return OfflineRegionEstimate(minZoom, maxZoom, tiles * AVG_TILE_BYTES)
        maxZoom--
    }
    val tiles = tileCount(west, south, east, north, MIN_DETAIL_ZOOM, MIN_DETAIL_ZOOM)
    return OfflineRegionEstimate(MIN_DETAIL_ZOOM, MIN_DETAIL_ZOOM, tiles * AVG_TILE_BYTES)
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
