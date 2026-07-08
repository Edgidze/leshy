package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.ui.util.formatDateTime
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDuration

@Composable
fun WalkDetailScreen(viewModel: WalkDetailViewModel, onBack: () -> Unit, onViewMap: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val walk = uiState.walk

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(walk?.name.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        if (walk == null) return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${stringResource(StringKey.WalkDetailStartTime)}: ${formatDateTime(walk.startTime)}")
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "${stringResource(StringKey.WalkDetailEndTime)}: " +
                        (walk.endTime?.let(::formatDateTime) ?: stringResource(StringKey.WalkDetailInProgress)),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("${stringResource(StringKey.WalkDetailDistance)}: ${formatDistanceKm(walk.distanceMeters)}")
                Text(walk.endTime?.let { formatDuration(it - walk.startTime) } ?: "—")
            }

            Text(
                stringResource(StringKey.WalkDetailFindsTitle),
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            )
            uiState.mushroomCounts.forEach { entry ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(categoryDisplayName(entry.category.nameKey))
                    Text(entry.count.toString())
                }
            }

            OutlinedButton(onClick = onViewMap, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(stringResource(StringKey.WalkDetailViewMap))
            }
        }
    }
}
