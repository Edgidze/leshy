package compose.project.leshy.ui.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.GeoPoint
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.MultiPoint
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

data class MapMarker(val lat: Double, val lon: Double, val colorHex: String)

private val TRACK_COLOR = Color(0xFF1B4332)
private val CURRENT_LOCATION_COLOR = Color(0xFF2196F3)
private const val DEFAULT_ZOOM = 15.0
private const val MIN_BOUNDS_SPAN_DEGREES = 0.001

/**
 * Shows the track recorded so far plus colored marker dots for finds/photos, and the current
 * device location. Backed by MapLibre + OpenFreeMap vector tiles (no API keys) on both Android
 * and iOS.
 */
@Composable
fun LiveTrackMap(
    track: List<GeoPoint>,
    markers: List<MapMarker>,
    currentLocation: GeoPoint?,
    modifier: Modifier,
) {
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(currentLocation?.lon ?: 0.0, currentLocation?.lat ?: 0.0),
            zoom = DEFAULT_ZOOM,
        ),
    )

    val historyPoints = remember(track, markers) {
        track.map { it.lat to it.lon } + markers.map { it.lat to it.lon }
    }

    LaunchedEffect(currentLocation, historyPoints) {
        if (currentLocation != null) {
            cameraState.position = cameraState.position.copy(
                target = Position(currentLocation.lon, currentLocation.lat),
            )
        } else if (historyPoints.isNotEmpty()) {
            val lats = historyPoints.map { it.first }
            val lons = historyPoints.map { it.second }
            val latSpan = lats.max() - lats.min()
            val lonSpan = lons.max() - lons.min()
            if (latSpan < MIN_BOUNDS_SPAN_DEGREES && lonSpan < MIN_BOUNDS_SPAN_DEGREES) {
                // A degenerate (near-zero-area) bounding box would otherwise make jumpTo zoom in
                // all the way to the tile server's limit, rendering blurry upscaled tiles.
                cameraState.position = cameraState.position.copy(
                    target = Position((lons.min() + lons.max()) / 2, (lats.min() + lats.max()) / 2),
                    zoom = DEFAULT_ZOOM,
                )
            } else {
                cameraState.jumpTo(
                    BoundingBox(west = lons.min(), south = lats.min(), east = lons.max(), north = lats.max()),
                    padding = PaddingValues(32.dp),
                )
            }
        }
    }

    MaplibreMap(modifier = modifier, baseStyle = OpenFreeMapStyle, cameraState = cameraState) {
        if (track.size >= 2) {
            val trackSource = rememberGeoJsonSource(
                GeoJsonData.Features(LineString(track.map { Position(it.lon, it.lat) })),
            )
            LineLayer(id = "track-line", source = trackSource, color = const(TRACK_COLOR), width = const(4.dp))
        }

        markers.groupBy { it.colorHex }.forEach { (colorHex, group) ->
            val color = runCatching { Color(("ff" + colorHex.removePrefix("#")).toLong(16)) }
                .getOrDefault(Color.Gray)
            val marksSource = rememberGeoJsonSource(
                GeoJsonData.Features(MultiPoint(group.map { Position(it.lon, it.lat) })),
            )
            CircleLayer(id = "marks-$colorHex", source = marksSource, color = const(color), radius = const(6.dp))
        }

        currentLocation?.let { location ->
            val currentLocationSource = rememberGeoJsonSource(
                GeoJsonData.Features(Point(Position(location.lon, location.lat))),
            )
            CircleLayer(
                id = "current-location",
                source = currentLocationSource,
                color = const(CURRENT_LOCATION_COLOR),
                radius = const(7.dp),
                strokeColor = const(Color.White),
                strokeWidth = const(2.dp),
            )
        }
    }
}
