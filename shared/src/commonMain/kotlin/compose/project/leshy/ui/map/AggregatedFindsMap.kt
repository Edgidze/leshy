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
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.HeatmapLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.MultiPoint
import org.maplibre.spatialk.geojson.Position

private val ROUTE_COLOR = Color(0xFF1B4332)

/**
 * Default heatmap intensity (1.0) makes even a single isolated find peak at full density, so it
 * paints red on its own — there's no room left to distinguish "one find" from "a dozen finds in
 * the same spot". HeatmapLayer's density accumulates additively for nearby/overlapping points
 * (a point of weight 10 == 10 overlapping points of weight 1, per its own doc), so lowering
 * intensity uniformly stretches that accumulation across the whole 0..1 color ramp instead of
 * saturating on the first point. Tuned so a lone find lands just inside the blue end, ~5
 * co-located finds reach green, and ~10+ climb into yellow/red — i.e. "red" now means "a real
 * hotspot", not "a mushroom was found here at all". Confirmed on the iOS simulator with seeded
 * finds (1/5/12 per spot); not re-checked on Android since HeatmapLayer crashes the Android
 * emulator's renderer on this machine (see CLAUDE.md) — should still hold there since it's the
 * same shared Compose code, but worth a glance on a real Android device.
 */
private const val HEATMAP_INTENSITY = 0.02f
private val HEATMAP_RADIUS = 20.dp

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
                GeoJsonData.Features(MultiPoint(findLocations.map { Position(it.lon, it.lat) })),
            )
            HeatmapLayer(
                id = "finds-heatmap",
                source = findsSource,
                radius = const(HEATMAP_RADIUS),
                intensity = const(HEATMAP_INTENSITY),
            )
        }
    }
}
