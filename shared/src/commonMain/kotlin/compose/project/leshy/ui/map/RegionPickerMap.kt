package compose.project.leshy.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import compose.project.leshy.data.repository.MapStyleCacheRepository
import compose.project.leshy.domain.model.OfflineRegionInfo
import compose.project.leshy.domain.model.OfflineRegionStatus
import compose.project.leshy.ui.components.MapLoadFailedBanner
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Polygon
import org.maplibre.spatialk.geojson.Position

private val REGION_OUTLINE_COMPLETE = Color(0xFF1B4332)
private val REGION_OUTLINE_IN_PROGRESS = Color(0xFF2196F3)
private val REGION_OUTLINE_ERROR = Color(0xFFB3261E)

/**
 * Lets the user pan/zoom to pick an area, with existing offline regions overlaid as outlined
 * rectangles so they can see what's already downloaded before picking a new one. Bounds come from
 * [OfflineRegionInfo] (the domain model), not the maplibre-compose offline types directly — the
 * outline is built from plain west/south/east/north here rather than via the library's
 * `rememberOfflinePacksSource`, since that helper takes the raw `OfflinePack` type the repository
 * intentionally doesn't leak past the data layer.
 */
@Composable
fun RegionPickerMap(
    cameraState: CameraState,
    regions: List<OfflineRegionInfo>,
    modifier: Modifier = Modifier,
) {
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
            regions.forEach { region ->
                key(region.name) {
                    val outlineSource = rememberGeoJsonSource(
                        GeoJsonData.Features(
                            Polygon(
                                listOf(
                                    Position(region.west, region.south),
                                    Position(region.west, region.north),
                                    Position(region.east, region.north),
                                    Position(region.east, region.south),
                                    Position(region.west, region.south),
                                ),
                            ),
                        ),
                    )
                    LineLayer(
                        id = "offline-region-${region.name}",
                        source = outlineSource,
                        color = const(region.status.outlineColor()),
                        width = const(2.dp),
                    )
                }
            }
        }
        if (tilesLoadFailed) {
            MapLoadFailedBanner(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

private fun OfflineRegionStatus.outlineColor(): Color = when (this) {
    OfflineRegionStatus.COMPLETE -> REGION_OUTLINE_COMPLETE
    OfflineRegionStatus.DOWNLOADING, OfflineRegionStatus.PAUSED -> REGION_OUTLINE_IN_PROGRESS
    OfflineRegionStatus.ERROR -> REGION_OUTLINE_ERROR
}
