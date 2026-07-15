package compose.project.leshy.ui.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.GeoPoint
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
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
 * many (10+) — "many" is meant to read as a real hotspot, not "a mushroom was found here at all".
 * Was a HeatmapLayer (see git history) but that crashes natively in libmaplibre.so on real Android
 * hardware (confirmed via `adb logcat -b crash`, not just the emulator as first thought) — replaced
 * with a clustered CircleLayer, which uses the same rendering path already proven crash-free
 * elsewhere in the app (marker dots in LiveTrackMap).
 */
private val FEW_COLOR = Color(0xFF1E88E5)
private val MEDIUM_COLOR = Color(0xFF43A047)
private val MANY_COLOR = Color(0xFFE53935)
private val FEW_RADIUS = 8.dp
private val MEDIUM_RADIUS = 13.dp
private val MANY_RADIUS = 18.dp
private const val MEDIUM_THRESHOLD = 5f
private const val MANY_THRESHOLD = 10f

/**
 * Shows every recorded route as a thin line, with a heatmap of mushroom finds drawn on top —
 * finds are the priority signal here, routes are secondary context (per SPEC.md).
 */
@Composable
fun AggregatedFindsMap(
    tracks: Map<Long, List<GeoPoint>>,
    findLocations: List<GeoPoint>,
    modifier: Modifier,
) {
    val cameraState = rememberCameraState(firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = 1.0))

    val allPoints = remember(tracks, findLocations) { tracks.values.flatten() + findLocations }

    LaunchedEffect(allPoints) {
        if (allPoints.isNotEmpty()) {
            val lats = allPoints.map { it.lat }
            val lons = allPoints.map { it.lon }
            cameraState.jumpTo(
                BoundingBox(west = lons.min(), south = lats.min(), east = lons.max(), north = lats.max()),
                padding = PaddingValues(32.dp),
            )
        }
    }

    MaplibreMap(modifier = modifier, baseStyle = OpenFreeMapStyle, cameraState = cameraState) {
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

        if (findLocations.isNotEmpty()) {
            val findsSource = rememberGeoJsonSource(
                GeoJsonData.Features(
                    FeatureCollection(findLocations.map { Feature(Point(Position(it.lon, it.lat)), properties = null) }),
                ),
                options = GeoJsonOptions(cluster = true),
            )
            val pointCount = feature["point_count"].asNumber(const(1f))
            CircleLayer(
                id = "finds-density",
                source = findsSource,
                color = step(pointCount, const(FEW_COLOR), MEDIUM_THRESHOLD to const(MEDIUM_COLOR), MANY_THRESHOLD to const(MANY_COLOR)),
                radius = step(pointCount, const(FEW_RADIUS), MEDIUM_THRESHOLD to const(MEDIUM_RADIUS), MANY_THRESHOLD to const(MANY_RADIUS)),
                opacity = const(0.8f),
                strokeColor = const(Color.White),
                strokeWidth = const(1.dp),
            )
        }
    }
}
