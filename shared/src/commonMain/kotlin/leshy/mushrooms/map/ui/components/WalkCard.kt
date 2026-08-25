package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val THUMBNAIL_SIZE = 120.dp
private val WALK_CARD_PADDING = 8.dp

/** Hold duration that opens Archive's multi-select mode — see CLAUDE.md for the feature spec. */
private val SELECTION_LONG_PRESS_DURATION = 5.seconds

@Composable
fun WalkCard(
    walk: Walk,
    track: List<GeoPoint>,
    findLocations: List<GeoPoint>,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(walk.id) {
                // Custom gesture instead of combinedClickable: the built-in long-press threshold
                // isn't configurable, and this needs a much longer, fixed 5s hold. A delay() timer
                // races the actual release — whichever resolves first wins. Each primitive
                // (awaitFirstDown/waitForUpOrCancellation) needs its own awaitPointerEventScope
                // call, made from this unrestricted outer scope: awaitPointerEventScope's own block
                // is a @RestrictsSuspension scope that can't itself call launch/delay/coroutineScope.
                while (true) {
                    awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
                    var longPressFired = false
                    val up = coroutineScope {
                        val longPressJob = launch {
                            delay(SELECTION_LONG_PRESS_DURATION)
                            longPressFired = true
                            onLongPress()
                        }
                        val result = awaitPointerEventScope { waitForUpOrCancellation() }
                        longPressJob.cancel()
                        result
                    }
                    if (up != null && !longPressFired) onClick()
                }
            },
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Row(
            modifier = Modifier.padding(WALK_CARD_PADDING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalkThumbnail(
                thumbnailPath = walk.thumbnailPath,
                track = track,
                findLocations = findLocations,
                modifier = Modifier.size(THUMBNAIL_SIZE),
            )
            Spacer(modifier = Modifier.width(WALK_CARD_PADDING))
            Column(modifier = Modifier.weight(1f)) {
                Text(walk.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    formatDateOnly(walk.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(formatDistanceKm(walk.distanceMeters))
                    Text(walk.endTime?.let { formatDurationShort(it - walk.startTime) } ?: "—")
                    Text("🍄 ${walk.mushroomCount}")
                }
            }
        }
    }
}

/**
 * Cached, tile-backed snapshot when [thumbnailPath] resolves to a real file (rendered once, at
 * `finish()`, by [leshy.mushrooms.map.data.platform.WalkThumbnailRenderer]) — falls back to the
 * plain [WalkRouteThumbnail] polyline for walks that predate this feature, failed renders (e.g.
 * offline), and the brief window right after Finish before the async render completes. Loaded via
 * Coil (`coil3.compose.AsyncImage`, `"file://"` model — Coil resolves local file URIs on both
 * platforms out of the box, no network engine needed) rather than a platform-specific
 * `expect`/`actual` decoder, per this project's rule of preferring one cross-platform library over
 * duplicated native code wherever one already exists (see CLAUDE.md §5.7).
 */
@Composable
private fun WalkThumbnail(
    thumbnailPath: String?,
    track: List<GeoPoint>,
    findLocations: List<GeoPoint>,
    modifier: Modifier = Modifier,
) {
    var loadFailed by remember(thumbnailPath) { mutableStateOf(false) }
    if (thumbnailPath == null || loadFailed) {
        WalkRouteThumbnail(track = track, findLocations = findLocations, modifier = modifier)
    } else {
        AsyncImage(
            model = "file://$thumbnailPath",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(12.dp)),
            onError = { loadFailed = true },
        )
    }
}
