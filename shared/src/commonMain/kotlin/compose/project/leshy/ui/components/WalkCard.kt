package compose.project.leshy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.Walk
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDuration

@Composable
fun WalkCard(walk: Walk, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(walk.name, style = MaterialTheme.typography.titleMedium)
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
