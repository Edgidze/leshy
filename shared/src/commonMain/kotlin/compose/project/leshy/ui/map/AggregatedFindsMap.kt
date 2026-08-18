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
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Position

private val ROUTE_COLOR = Color(0xFF1B4332)

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
    places: List<PlaceMarker> = emptyList(),
    onPlaceClick: (Long) -> Unit = {},
) {
    val cameraState = rememberCameraState(firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = 1.0))

    val allPoints = remember(tracks, markers, places) {
        tracks.values.flatten().map { it.lat to it.lon } +
            markers.map { it.lat to it.lon } +
            places.map { it.lat to it.lon }
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
        options = MapOptions(renderOptions = mapRenderOptions, ornamentOptions = mapOrnamentOptions),
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

        ClusteredFindsLayers(markers)
        PlaceMarkersLayer(places, onPlaceClick)
    }
}
