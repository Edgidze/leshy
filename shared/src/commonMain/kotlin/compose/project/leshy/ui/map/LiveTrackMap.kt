package compose.project.leshy.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import compose.project.leshy.domain.model.GeoPoint

data class MapMarker(val lat: Double, val lon: Double, val colorHex: String)

/**
 * Shows the track recorded so far plus colored marker dots for finds/photos, and the current
 * device location. Real rendering (MapLibre + OSM raster tiles) is only available on Android for
 * now — see [compose.project.leshy.ui.screens.RecordMapScreen] and CLAUDE.md status for why iOS
 * shows a placeholder until the project can be built with Xcode/CocoaPods.
 */
@Composable
expect fun LiveTrackMap(
    track: List<GeoPoint>,
    markers: List<MapMarker>,
    currentLocation: GeoPoint?,
    modifier: Modifier,
)
