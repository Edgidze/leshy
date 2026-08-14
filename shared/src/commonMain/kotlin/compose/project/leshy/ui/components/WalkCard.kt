package compose.project.leshy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.ui.util.formatDateOnly
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDuration

private val THUMBNAIL_SIZE = 72.dp

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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalkRouteThumbnail(
                track = track,
                findLocations = findLocations,
                modifier = Modifier.size(THUMBNAIL_SIZE),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(walk.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    formatDateOnly(walk.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(formatDistanceKm(walk.distanceMeters))
                    Text(walk.endTime?.let { formatDuration(it - walk.startTime) } ?: "—")
                    Text("🍄 ${walk.mushroomCount}")
                }
            }
        }
    }
}
