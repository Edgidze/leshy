package compose.project.leshy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.util.TurnDirection
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.record.NavigationOverlayState
import kotlin.math.roundToInt

private val PANEL_MIN_HEIGHT = 96.dp

/**
 * Opaque (not translucent) panel — same fully-solid treatment as [MapFilterButton], just greenish
 * (reuses [MaterialTheme.colorScheme.primaryContainer], already tinted forest-green in both themes,
 * no dedicated color token needed). Flush to the screen's top and right edges (rounded only on the
 * bottom-start corner) so it either fully encloses the map's TopEnd compass ornament underneath or
 * doesn't touch it at all — never a torn/half-visible compass.
 */
@Composable
fun NavigationOverlayPanel(state: NavigationOverlayState, onCloseClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(0.5f).defaultMinSize(minHeight = PANEL_MIN_HEIGHT),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(bottomStart = 20.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "${stringResource(StringKey.NavigationDirectionToPrefix)} ${state.targetName}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onCloseClick) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(StringKey.NavigationCloseContentDescription),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Text(
                text = navigationBodyText(state),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun navigationBodyText(state: NavigationOverlayState): String {
    if (state.hasArrived) return stringResource(StringKey.NavigationArrivedPhrase)
    val direction = state.turnDirection ?: return stringResource(StringKey.NavigationDeterminingDirection)
    val distancePrefix = "${stringResource(StringKey.NavigationDistanceToTargetPrefix)} " +
        "${state.distanceMeters.roundToInt()} ${stringResource(StringKey.NavigationMetersSuffix)}"
    return when (direction) {
        TurnDirection.AHEAD -> "$distancePrefix, ${stringResource(StringKey.NavigationGoStraightPhrase)}"
        TurnDirection.RIGHT ->
            "$distancePrefix, ${stringResource(StringKey.NavigationKeepRightPhrase)} ${state.turnDegrees?.roundToInt()}°"
        TurnDirection.LEFT ->
            "$distancePrefix, ${stringResource(StringKey.NavigationKeepLeftPhrase)} ${state.turnDegrees?.roundToInt()}°"
    }
}
