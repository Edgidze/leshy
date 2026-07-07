package compose.project.leshy.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.GeoPoint
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.record_map_placeholder_ios
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun LiveTrackMap(
    track: List<GeoPoint>,
    markers: List<MapMarker>,
    currentLocation: GeoPoint?,
    modifier: Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.record_map_placeholder_ios),
            modifier = Modifier.padding(24.dp),
        )
    }
}
