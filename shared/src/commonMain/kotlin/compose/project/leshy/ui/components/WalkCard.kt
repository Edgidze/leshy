package compose.project.leshy.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.ui.util.formatDateOnly
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDurationShort

private val THUMBNAIL_SIZE = 120.dp
private val WALK_CARD_PADDING = 8.dp

@Composable
fun WalkCard(
    walk: Walk,
    track: List<GeoPoint>,
    findLocations: List<GeoPoint>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
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
 * `finish()`, by [compose.project.leshy.data.platform.WalkThumbnailRenderer]) — falls back to the
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
