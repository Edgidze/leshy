package leshy.mushrooms.map.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.ui.components.MapLoadFailedBanner
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.MultiLineString
import org.maplibre.spatialk.geojson.Position


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
    // Переопределяемы ради заставки на самом экране «Карта находок»: там карта не управляется
    // пальцем (её перекрывает кнопка перехода на полный экран), и обещающие управление компас с
    // линейкой масштаба с неё сняты.
    gestureOptions: GestureOptions = GestureOptions.Standard,
    ornamentOptions: OrnamentOptions = mapOrnamentOptions,
    // Overridable so a caller with its own bottom-anchored controls can keep the tile-load-failed
    // banner clear of them instead of it defaulting to the top.
    bannerAlignment: Alignment = Alignment.TopCenter,
    bannerPadding: PaddingValues = PaddingValues(16.dp),
    // Гасится на заставке «Карты находок»: её перекрывает прозрачная кнопка перехода на полный
    // экран, то есть до крестика на плашке всё равно не дотянуться — а на полноэкранной карте,
    // куда эта кнопка и ведёт, та же плашка покажется как обычно.
    showLoadFailedBanner: Boolean = true,
) {
    val cameraState = rememberCameraState(firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = 1.0))
    val overlayColors = rememberMapOverlayColors()

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
    val tileHost = rememberTileHostWatch()

    Box(modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            baseStyle = baseStyle,
            cameraState = cameraState,
            options = MapOptions(
                renderOptions = mapRenderOptions,
                gestureOptions = gestureOptions,
                ornamentOptions = ornamentOptions,
            ),
            onMapLoadFailed = { tileHost.onStyleLoadFailed() },
        ) {
            // ONE layer for all routes, not one per walk — same rationale as LiveTrackMap's
            // historical tracks: a layer plus its source is a synchronous native style mutation
            // each, and this screen draws the whole archive. See ui/map/CLAUDE.md, «Стоимость слоя».
            val routeLines = remember(tracks) {
                tracks.values
                    .filter { it.size >= 2 }
                    .map { points -> points.map { Position(it.lon, it.lat) } }
            }
            if (routeLines.isNotEmpty()) {
                val routesSource = rememberGeoJsonSource(GeoJsonData.Features(MultiLineString(routeLines)))
                LineLayer(
                    id = "routes",
                    source = routesSource,
                    color = const(overlayColors.track),
                    width = const(2.dp),
                    opacity = const(0.45f),
                )
            }

            ClusteredFindsLayers(markers)
            PlaceMarkersLayer(places, onPlaceClick)
        }
        tileHost.bannerMessage?.takeIf { showLoadFailedBanner }?.let { message ->
            MapLoadFailedBanner(
                message = message,
                onDismiss = tileHost::dismiss,
                modifier = Modifier.align(bannerAlignment).padding(bannerPadding),
            )
        }
    }
}
