package compose.project.leshy.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.unit.DpSize
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.plus
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

/**
 * Density tiers for clustered finds: few (< 5, incl. a single unclustered find), medium (5..9),
 * many (10+). Each species gets its own clustered `GeoJsonSource` — so only nearby finds of the
 * SAME species ever merge into one cluster, never across species — rendered with that species'
 * own photo marker (see `MushroomMarkerIcon.kt`) instead of a plain dot, sized as a fraction of
 * the marker photo size already used elsewhere on the map (`mushroomMarkerSize`): smaller for
 * sparser clusters, bigger for denser ones. `sortKey` makes smaller clusters paint in front of
 * bigger ones, and — within the same size tier — a cluster with more finds paint in front of one
 * with fewer; this is only exact within one species' own layer (per MapLibre's `sortKey`
 * semantics it doesn't reorder across different species' layers, only within one — an accepted
 * corner case for two different species overlapping on screen).
 */
private const val FEW_SIZE_FRACTION = 0.25f
private const val MEDIUM_SIZE_FRACTION = 0.5f
private const val MANY_SIZE_FRACTION = 0.75f
private const val MEDIUM_THRESHOLD = 5f
private const val MANY_THRESHOLD = 10f

// Offsets are inverted relative to size: the SMALLEST tier gets the HIGHEST offset so it paints
// last/on top ("smaller in front"); spaced 1,000,000 apart so the raw point_count tie-breaker
// added on top ("more finds paints in front of fewer within the same tier") can never cross a
// tier boundary.
private const val FEW_SORT_OFFSET = 2_000_000f
private const val MEDIUM_SORT_OFFSET = 1_000_000f
private const val MANY_SORT_OFFSET = 0f

/**
 * Renders [markers] as per-species clustered `SymbolLayer`s (see tier doc above). Shared between
 * `AggregatedFindsMap` (the "Карта" screen's all-history view) and `LiveTrackMap` (the "Запись"
 * screen's background layer of previously-found mushrooms) — must be called directly inside a
 * `MaplibreMap { ... }` block. [idPrefix] keeps layer/source ids from colliding if both a
 * foreground and a background clustered layer ever end up on the same map.
 */
@Composable
fun ClusteredFindsLayers(markers: List<MapMarker>, idPrefix: String = "finds") {
    markers.groupBy { it.icon }.forEach { (icon, group) ->
        if (icon == null) return@forEach
        key(icon.key) {
            val painter = rememberMushroomMarkerPainter(icon)
            if (painter != null) {
                val findsSource = rememberGeoJsonSource(
                    GeoJsonData.Features(
                        FeatureCollection(group.map { Feature(Point(Position(it.lon, it.lat)), properties = null) }),
                    ),
                    options = GeoJsonOptions(cluster = true),
                )
                val pointCount = feature["point_count"].asNumber(const(1f))
                val sizeMultiplier = step(
                    pointCount,
                    const(FEW_SIZE_FRACTION),
                    MEDIUM_THRESHOLD to const(MEDIUM_SIZE_FRACTION),
                    MANY_THRESHOLD to const(MANY_SIZE_FRACTION),
                )
                val sortOffset = step(
                    pointCount,
                    const(FEW_SORT_OFFSET),
                    MEDIUM_THRESHOLD to const(MEDIUM_SORT_OFFSET),
                    MANY_THRESHOLD to const(MANY_SORT_OFFSET),
                )
                val markerSize = mushroomMarkerSize
                SymbolLayer(
                    id = "$idPrefix-${icon.key}",
                    source = findsSource,
                    iconImage = image(painter, size = DpSize(markerSize, markerSize)),
                    iconSize = sizeMultiplier,
                    iconAllowOverlap = const(true),
                    sortKey = sortOffset + pointCount,
                )
            }
        }
    }
}
