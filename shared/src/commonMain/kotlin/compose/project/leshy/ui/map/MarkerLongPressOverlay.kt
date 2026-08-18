package compose.project.leshy.ui.map

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraState
import org.maplibre.spatialk.geojson.Position

data class NavigationTargetCandidate(val id: Long, val lat: Double, val lon: Double)

private val DEFAULT_HIT_RADIUS = 24.dp

/** Same hold duration/pattern as `WalkCard.kt`'s Archive multi-select gesture — see its CLAUDE.md doc comment. */
private val MARKER_LONG_PRESS_DURATION = 5.seconds

/**
 * One small (2×[hitRadius]) invisible hotspot per candidate, positioned at its live projected
 * screen location and reusing `WalkCard.kt`'s exact tap-vs-5s-hold gesture. Deliberately NOT a
 * single full-size transparent overlay: an earlier version tried that and it silently broke ALL
 * map interaction (pan included, even far from any marker) everywhere the overlay's bounds
 * covered, confirmed on-device — Compose's pointer-input arbitration with the interop native map
 * view doesn't let an overlapping sibling `pointerInput` coexist with it, consumed or not. Small,
 * marker-sized hotspots avoid that: outside their tiny footprint there's no competing pointerInput
 * at all, so the rest of the map keeps panning/zooming normally, exactly like `MapFilterButton` or
 * any other small floating control already does over this same map.
 *
 * Because a hotspot fully owns its marker's screen position, it also supersedes
 * [PlaceMarkersLayer]'s own native `onClick` for that marker while this overlay is present — so
 * this composable takes over BOTH plain-tap ([onMarkerTapped]) and long-press ([onMarkerLongPressed])
 * for every candidate, rather than only long-press.
 */
@Composable
fun MarkerLongPressOverlay(
    cameraState: CameraState,
    candidates: List<NavigationTargetCandidate>,
    onMarkerTapped: (Long) -> Unit,
    onMarkerLongPressed: (Long) -> Unit,
    modifier: Modifier = Modifier,
    hitRadius: Dp = DEFAULT_HIT_RADIUS,
) {
    // Reading cameraState.position forces recomposition on every native pan/zoom (the library
    // writes it back on camera movement), keeping each hotspot aligned with its rendered pin.
    val cameraPosition = cameraState.position
    val projection = cameraState.projection ?: return
    Box(modifier = modifier) {
        candidates.forEach { candidate ->
            key(candidate.id) {
                val screen = remember(cameraPosition, candidate) {
                    projection.screenLocationFromPosition(Position(candidate.lon, candidate.lat))
                }
                Box(
                    modifier = Modifier
                        .offset(x = screen.x - hitRadius, y = screen.y - hitRadius)
                        .size(hitRadius * 2)
                        .pointerInput(candidate.id) {
                            while (true) {
                                awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
                                var longPressFired = false
                                val up = coroutineScope {
                                    val longPressJob = launch {
                                        delay(MARKER_LONG_PRESS_DURATION)
                                        longPressFired = true
                                        onMarkerLongPressed(candidate.id)
                                    }
                                    val result = awaitPointerEventScope { waitForUpOrCancellation() }
                                    longPressJob.cancel()
                                    result
                                }
                                if (up != null && !longPressFired) onMarkerTapped(candidate.id)
                            }
                        },
                )
            }
        }
    }
}
