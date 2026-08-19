package compose.project.leshy.ui.map

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import compose.project.leshy.data.repository.MapStyleCacheRepository
import compose.project.leshy.domain.model.CategoryIconSource
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.ui.components.MapLoadFailedBanner
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraMoveReason
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.MultiPoint
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

data class MapMarker(
    val lat: Double,
    val lon: Double,
    val colorHex: String,
    /** Null for PHOTO/POI marks and for species without an illustration — those fall back to a
     * plain [colorHex] circle instead of a photo marker. */
    val icon: CategoryIconSource? = null,
)

private val TRACK_COLOR = Color(0xFF1B4332)
private val CURRENT_LOCATION_COLOR = Color(0xFF2196F3)
// Amber — the one hue not already claimed by the map: track is dark green, the location dot is
// blue, OpenFreeMap's forest/terrain fill is green/tan, and mushroom marker colors vary by
// category but skew toward browns/reds. Amber reads as a temporary guide, not a recorded feature.
private val NAVIGATION_LINE_COLOR = Color(0xFFFF8F00)
private val NAVIGATION_LINE_DASH = listOf(2f, 2f)
private const val DEFAULT_ZOOM = 15.0
private const val MIN_BOUNDS_SPAN_DEGREES = 0.001
private val FOLLOW_RESUME_DELAY = 10.seconds

/** Shared default starting camera position — also used by callers that hoist their own [CameraState]. */
fun defaultLiveTrackCameraPosition(currentLocation: GeoPoint?): CameraPosition = CameraPosition(
    target = Position(currentLocation?.lon ?: 0.0, currentLocation?.lat ?: 0.0),
    zoom = DEFAULT_ZOOM,
)

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
    historicalMarkers: List<MapMarker> = emptyList(),
    places: List<PlaceMarker> = emptyList(),
    onPlaceClick: (Long) -> Unit = {},
    historicalPlaces: List<PlaceMarker> = emptyList(),
    // Native long-press (see PlaceMarkersLayer's doc) on either a current-walk or historical place
    // marker — RecordScreen.kt wires this to activate navigate-to-place.
    onPlaceLongPress: (Long) -> Unit = {},
    // Straight-line guide to the active navigation target (RecordScreen's long-press-to-navigate) —
    // null whenever navigation isn't active, so no line is drawn.
    navigationTargetLat: Double? = null,
    navigationTargetLon: Double? = null,
    // Overridable only for screens that render this map full-bleed under the system status bar
    // (WalkMapScreen.kt) — those need extra top padding on the ornaments to clear it. Callers that
    // sit below a Scaffold/TopAppBar (RecordScreen.kt/MapScreen.kt) already start below the status
    // bar and must keep the shared default (no double-inset).
    ornamentOptions: OrnamentOptions = mapOrnamentOptions,
    // Hoistable so callers can imperatively control or observe the camera from outside.
    cameraState: CameraState = rememberCameraState(firstPosition = defaultLiveTrackCameraPosition(currentLocation)),
    // Overridable so a caller with its own bottom-anchored controls (RecordScreen's Start/Pause
    // pill + mushroom tile scroller) can keep the tile-load-failed banner clear of them instead of
    // it defaulting to the top.
    bannerAlignment: Alignment = Alignment.TopCenter,
    bannerPadding: PaddingValues = PaddingValues(16.dp),
) {
    val historyPoints = remember(track, markers, places) {
        track.map { it.lat to it.lon } + markers.map { it.lat to it.lon } + places.map { it.lat to it.lon }
    }

    // Auto-follow (recentering the camera on each new fix) is convenient until the user actually
    // touches the map — at that point it starts fighting their pan/zoom instead of helping. So it
    // suspends on any user gesture and resumes FOLLOW_RESUME_DELAY after the user stops touching
    // the map, so a walk in progress doesn't leave them stranded off-screen indefinitely. Restarted
    // on every new gesture, not just the first — a user who pans twice within the window shouldn't
    // have the map jerk back to their location between the two.
    //
    // Own camera jumps below report CameraMoveReason.PROGRAMMATIC, not GESTURE, so they never
    // suspend/reschedule this themselves. isCameraMoving (not moveReason) is what's watched for
    // edges: moveReason is sticky (never reset to NONE once a gesture happens) so writing the same
    // GESTURE value again on a second pan is a no-op under Compose's structural equality and
    // wouldn't emit from snapshotFlow — isCameraMoving genuinely alternates true/false once per
    // discrete gesture (native onCameraMoveStarted/onCameraIdle), so it's the reliable edge signal;
    // moveReason is only read (not collected) at each edge to attribute it to a gesture vs ourselves.
    var followEnabled by remember(cameraState) { mutableStateOf(true) }
    LaunchedEffect(cameraState) {
        var resumeJob: Job? = null
        snapshotFlow { cameraState.isCameraMoving }.collect { isMoving ->
            if (cameraState.moveReason != CameraMoveReason.GESTURE) return@collect
            if (isMoving) {
                resumeJob?.cancel()
                followEnabled = false
            } else {
                resumeJob = launch {
                    delay(FOLLOW_RESUME_DELAY)
                    followEnabled = true
                }
            }
        }
    }

    LaunchedEffect(currentLocation, historyPoints, followEnabled) {
        if (!followEnabled) return@LaunchedEffect
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
            options = MapOptions(renderOptions = mapRenderOptions, ornamentOptions = ornamentOptions),
            onMapLoadFailed = { tilesLoadFailed = true },
            onMapLoadFinished = { tilesLoadFailed = false },
        ) {
            ClusteredFindsLayers(historicalMarkers, idPrefix = "historical")
            // Reuses the same onPlaceClick as the current walk's own places below — safe because
            // RecordScreen.kt excludes the current walk's marks from historicalPlaces, so the two
            // layers' place ids never collide.
            PlaceMarkersLayer(historicalPlaces, onPlaceClick, idPrefix = "historical-place", onPlaceLongPress = onPlaceLongPress)

            if (track.size >= 2) {
                val trackSource = rememberGeoJsonSource(
                    GeoJsonData.Features(LineString(track.map { Position(it.lon, it.lat) })),
                )
                LineLayer(id = "track-line", source = trackSource, color = const(TRACK_COLOR), width = const(4.dp))
            }

            if (currentLocation != null && navigationTargetLat != null && navigationTargetLon != null) {
                val navigationLineSource = rememberGeoJsonSource(
                    GeoJsonData.Features(
                        LineString(
                            listOf(
                                Position(currentLocation.lon, currentLocation.lat),
                                Position(navigationTargetLon, navigationTargetLat),
                            ),
                        ),
                    ),
                )
                LineLayer(
                    id = "navigation-line",
                    source = navigationLineSource,
                    color = const(NAVIGATION_LINE_COLOR),
                    width = const(3.dp),
                    dasharray = const(NAVIGATION_LINE_DASH),
                )
            }

            val (photoMarkers, mushroomMarkers) = markers.partition { it.icon == null }

            mushroomMarkers.groupBy { it.icon }.forEach { (icon, group) ->
                requireNotNull(icon)
                key(icon.key) {
                    val painter = rememberMushroomMarkerPainter(icon)
                    if (painter != null) {
                        val marksSource = rememberGeoJsonSource(
                            GeoJsonData.Features(MultiPoint(group.map { Position(it.lon, it.lat) })),
                        )
                        val markerSize = mushroomMarkerSize
                        SymbolLayer(
                            id = "marks-${icon.key}",
                            source = marksSource,
                            iconImage = image(painter, size = DpSize(markerSize, markerSize)),
                            iconAllowOverlap = const(true),
                        )
                    }
                }
            }

            photoMarkers.groupBy { it.colorHex }.forEach { (colorHex, group) ->
                key(colorHex) {
                    val color = runCatching { Color(("ff" + colorHex.removePrefix("#")).toLong(16)) }
                        .getOrDefault(Color.Gray)
                    val marksSource = rememberGeoJsonSource(
                        GeoJsonData.Features(MultiPoint(group.map { Position(it.lon, it.lat) })),
                    )
                    CircleLayer(id = "marks-$colorHex", source = marksSource, color = const(color), radius = const(6.dp))
                }
            }

            PlaceMarkersLayer(places, onPlaceClick, onPlaceLongPress = onPlaceLongPress)

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
        if (tilesLoadFailed) {
            MapLoadFailedBanner(
                onDismiss = { tilesLoadFailed = false },
                modifier = Modifier.align(bannerAlignment).padding(bannerPadding),
            )
        }
    }
}
