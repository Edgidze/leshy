package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.ui.components.MushroomDonutChart
import compose.project.leshy.ui.util.formatDateTime
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDurationLabeled
import compose.project.leshy.ui.util.formatSpeedKmh
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MUSHROOM_TOAST_DURATION_MILLIS = 3000L

@Composable
fun WalkDetailScreen(viewModel: WalkDetailViewModel, onBack: () -> Unit, onViewMap: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val walk = uiState.walk
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onBack()
    }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            title = { Text(stringResource(StringKey.WalkDetailDeleteConfirmTitle)) },
            text = { Text(stringResource(StringKey.WalkDetailDeleteConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirm) {
                    Text(stringResource(StringKey.WalkDetailDeleteConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteDismiss) {
                    Text(stringResource(StringKey.WalkDetailDeleteConfirmNo))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(walk?.name.orEmpty()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onDeleteClick) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(StringKey.WalkDetailDeleteContentDescription),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (walk == null) return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${stringResource(StringKey.WalkDetailStartTime)}: ${formatDateTime(walk.startTime)}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "${stringResource(StringKey.WalkDetailEndTime)}: " +
                                (walk.endTime?.let(::formatDateTime) ?: stringResource(StringKey.WalkDetailInProgress)),
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("${stringResource(StringKey.WalkDetailDistance)}: ${formatDistanceKm(walk.distanceMeters)}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        Text(
                            "${stringResource(StringKey.WalkDetailDuration)}: " +
                                (walk.endTime?.let { formatDurationLabeled(it - walk.startTime) } ?: "—"),
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        Text(
                            "${stringResource(StringKey.WalkDetailAvgSpeed)}: " +
                                (walk.endTime?.let { formatSpeedKmh(walk.avgSpeed) } ?: "—"),
                        )
                    }

                    Text(
                        stringResource(StringKey.WalkDetailFindsTitle),
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        textDecoration = TextDecoration.Underline,
                    )
                }

                items(uiState.mushroomCounts) { entry ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(categoryDisplayName(entry.category.nameKey))
                        Text(entry.count.toString())
                    }
                }

                if (uiState.mushroomCounts.isNotEmpty()) {
                    item {
                        MushroomDonutChart(
                            counts = uiState.mushroomCounts,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            onMushroomClick = { name ->
                                coroutineScope.launch {
                                    // showSnackbar only accepts the fixed Short/Long/Indefinite
                                    // durations — Indefinite plus a manual dismiss after exactly
                                    // MUSHROOM_TOAST_DURATION_MILLIS is how you get a custom one.
                                    launch {
                                        delay(MUSHROOM_TOAST_DURATION_MILLIS)
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                    }
                                    snackbarHostState.showSnackbar(message = name, duration = SnackbarDuration.Indefinite)
                                }
                            },
                        )
                    }
                }
            }

            OutlinedButton(onClick = onViewMap, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(stringResource(StringKey.WalkDetailViewMap))
            }
        }
    }
}
