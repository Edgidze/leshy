package compose.project.leshy.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL

/**
 * Floating overlay shown on top of a map when MapLibre reports it failed to fully load the
 * style/tiles (e.g. the tile host is blocked by the user's ISP) — MapLibre itself just renders a
 * blank background with no error of its own, so this is the only user-visible signal.
 */
@Composable
fun MapLoadFailedBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(
            text = "${stringResource(StringKey.MapTilesLoadFailed)} $OPEN_FREE_MAP_STYLE_URL",
            modifier = Modifier.padding(12.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}
