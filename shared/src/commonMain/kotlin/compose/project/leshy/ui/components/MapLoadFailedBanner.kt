package compose.project.leshy.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL

/**
 * Floating overlay shown on top of a map when MapLibre reports it failed to fully load the
 * style/tiles (e.g. the tile host is blocked by the user's ISP) — MapLibre itself just renders a
 * blank background with no error of its own, so this is the only user-visible signal. Position is
 * entirely up to the caller (via [modifier], typically `Modifier.align(...)` inside the map's own
 * `Box`) since only the screen knows what else — a bottom button row, a floating action button —
 * might already occupy part of the map's bounds.
 */
@Composable
fun MapLoadFailedBanner(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Box {
            Text(
                text = "${stringResource(StringKey.MapTilesLoadFailed)} $OPEN_FREE_MAP_STYLE_URL",
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 40.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(StringKey.MapTilesLoadFailedDismissContentDescription),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
