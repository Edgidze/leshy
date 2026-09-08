package leshy.mushrooms.map.ui.components

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
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_HOST

/**
 * Floating overlay shown on top of a map when MapLibre reports it failed to fully load the
 * style/tiles (e.g. the tile host is blocked by the user's ISP). Position is entirely up to the
 * caller (via [modifier], typically `Modifier.align(...)` inside the map's own `Box`) since only the
 * screen knows what else — a bottom button row, a floating action button — might already occupy part
 * of the map's bounds.
 *
 * [message] differs by screen because the consequence does: on Record and a walk's map the recording
 * itself is unaffected and the banner says so, while on Preparation the very thing that screen is for
 * — downloading an area — is what cannot happen without the server. Every variant ends with a lead-in
 * for [OPEN_FREE_MAP_HOST], which is appended here so no translation has to carry the host itself.
 *
 * It is no longer the only user-visible signal that something is off: since the bundled fallback
 * style (`data/style/FallbackMapStyle.kt`) the map underneath still draws the user's own track and
 * finds on a plain ground, so this explains a missing basemap rather than a blank screen.
 */
@Composable
fun MapLoadFailedBanner(message: StringKey, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Box {
            Text(
                text = "${stringResource(message)} $OPEN_FREE_MAP_HOST",
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
