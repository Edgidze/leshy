package leshy.mushrooms.map.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.ui.components.MapLoadFailedBanner
import org.koin.compose.koinInject
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
    // Overridable so a caller with its own bottom-anchored controls can keep the tile-load-failed
    // banner clear of them instead of it defaulting to the top.
    bannerAlignment: Alignment = Alignment.TopCenter,
    bannerPadding: PaddingValues = PaddingValues(16.dp),
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

    val mapStyleCacheRepository = koinInject<MapStyleCacheRepository>()
    val baseStyle by mapStyleCacheRepository.baseStyle.collectAsState()
    var tilesLoadFailed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        mapStyleCacheRepository.ensureLoaded()
        // Independent of onMapLoadFailed/onMapLoadFinished below — those only reflect whether the
        // (now locally-pinned) style loaded, not whether the tile host is actually reachable. See
        // MapStyleCacheRepository.isTileHostReachable's doc comment.
        if (!mapStyleCacheRepository.isTileHostReachable()) {
            tilesLoadFailed = true
        }
    }

    Box(modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            baseStyle = baseStyle,
            cameraState = cameraState,
            options = MapOptions(renderOptions = mapRenderOptions, ornamentOptions = mapOrnamentOptions),
            onMapLoadFailed = { tilesLoadFailed = true },
            onMapLoadFinished = { tilesLoadFailed = false },
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
        if (tilesLoadFailed) {
            MapLoadFailedBanner(
                onDismiss = { tilesLoadFailed = false },
                modifier = Modifier.align(bannerAlignment).padding(bannerPadding),
            )
        }
    }
}
