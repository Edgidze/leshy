package compose.project.leshy.ui.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.GeoPoint
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.plus
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

private val ROUTE_COLOR = Color(0xFF1B4332)

/**
 * Density tiers for clustered finds: few (< 5, incl. a single unclustered find), medium (5..9),
 * many (10+). Each species gets its own clustered `GeoJsonSource` — so only nearby finds of the
 * SAME species ever merge into one cluster, never across species — rendered with that species'
 * own photo marker (see `MushroomMarkerIcon.kt`) instead of a plain dot, sized as a fraction of
 * the marker photo size already used elsewhere on the map (`MUSHROOM_MARKER_SIZE`,
 * `LiveTrackMap.kt`): smaller for sparser clusters, bigger for denser ones. `sortKey` makes
 * smaller clusters paint in front of bigger ones, and — within the same size tier — a cluster
 * with more finds paint in front of one with fewer; this is only exact within one species' own
 * layer (per MapLibre's `sortKey` semantics it doesn't reorder across different species' layers,
 * only within one — an accepted corner case for two different species overlapping on screen).
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
 * Shows every recorded route as a thin line, with mushroom finds drawn on top as their own
 * species' photo, clustered per-species — finds are the priority signal here, routes are
 * secondary context (per SPEC.md).
 */
@Composable
fun AggregatedFindsMap(
    tracks: Map<Long, List<GeoPoint>>,
    markers: List<MapMarker>,
    modifier: Modifier,
) {
    val cameraState = rememberCameraState(firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = 1.0))

    val allPoints = remember(tracks, markers) {
        tracks.values.flatten().map { it.lat to it.lon } + markers.map { it.lat to it.lon }
    }

    LaunchedEffect(allPoints) {
        if (allPoints.isNotEmpty()) {
            val lats = allPoints.map { it.first }
            val lons = allPoints.map { it.second }
            cameraState.jumpTo(
                BoundingBox(west = lons.min(), south = lats.min(), east = lons.max(), north = lats.max()),
                padding = PaddingValues(32.dp),
            )
        }
    }

    MaplibreMap(
        modifier = modifier,
        baseStyle = OpenFreeMapStyle,
        cameraState = cameraState,
        options = MapOptions(renderOptions = mapRenderOptions),
    ) {
        tracks.forEach { (walkId, points) ->
            if (points.size >= 2) {
                key(walkId) {
                    val routeSource = rememberGeoJsonSource(
                        GeoJsonData.Features(LineString(points.map { Position(it.lon, it.lat) })),
                    )
                    LineLayer(
                        id = "route-$walkId",
                        source = routeSource,
                        color = const(ROUTE_COLOR),
                        width = const(2.dp),
                        opacity = const(0.45f),
                    )
                }
            }
        }

        markers.groupBy { it.iconRef }.forEach { (iconRef, group) ->
            if (iconRef == null) return@forEach
            key(iconRef) {
                val painter = rememberMushroomMarkerPainter(iconRef)
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
                    SymbolLayer(
                        id = "finds-$iconRef",
                        source = findsSource,
                        iconImage = image(painter, size = DpSize(MUSHROOM_MARKER_SIZE, MUSHROOM_MARKER_SIZE)),
                        iconSize = sizeMultiplier,
                        iconAllowOverlap = const(true),
                        sortKey = sortOffset + pointCount,
                    )
                }
            }
        }
    }
}
