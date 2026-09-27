package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.archive.FishArchiveViewModel
import klev.fishing.map.ui.components.formatWeight
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FishArchiveScreen(
    onTripClick: (Long) -> Unit,
    viewModel: FishArchiveViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.rows.isEmpty() && !uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(fishStringResource(FishStringKey.ArchiveEmpty), style = MaterialTheme.typography.titleMedium)
            Text(
                text = fishStringResource(FishStringKey.ArchiveEmptyHint),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(uiState.rows, key = { it.trip.id }) { row ->
            Card(onClick = { onTripClick(row.trip.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = row.trip.title?.takeIf { it.isNotBlank() }
                            ?: row.trip.waterBody?.takeIf { it.isNotBlank() }
                            ?: fishStringResource(FishStringKey.ArchiveUnnamedTrip),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = formatDateTime(row.trip.startedAt),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(fishStringResource(row.trip.method.labelKey()), style = MaterialTheme.typography.bodySmall)
                        row.trip.durationMillis?.let {
                            Text(formatDurationShort(it), style = MaterialTheme.typography.bodySmall)
                        }
                        if (row.trip.distanceMeters > 0) {
                            Text(
                                text = formatDistanceKm(row.trip.distanceMeters),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    Text(
                        text = if (row.catchCount == 0) {
                            fishStringResource(FishStringKey.ArchiveCatchesNone)
                        } else {
                            val weight = if (row.totalWeightGrams > 0) ", ${formatWeight(row.totalWeightGrams)}" else ""
                            "${fishStringResource(FishStringKey.RecordCatchCount)}: ${row.catchCount}$weight"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
