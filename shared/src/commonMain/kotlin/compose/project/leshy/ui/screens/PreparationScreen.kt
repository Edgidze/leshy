package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.OfflineRegionInfo
import compose.project.leshy.domain.model.OfflineRegionStatus
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.preparation.PreparationViewModel
import compose.project.leshy.ui.map.RegionPickerMap
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.spatialk.geojson.Position

// Below this span (in degrees, in either axis) the "download this view" action is a no-op — the
// same degenerate-bbox guard as LiveTrackMap's MIN_BOUNDS_SPAN_DEGREES, here protecting against
// starting a pack for a near-zero-area view.
private const val MIN_DOWNLOAD_SPAN_DEGREES = 0.001
private val WORLD_VIEW_ZOOM = 2.0

@Composable
fun PreparationScreen(modifier: Modifier = Modifier, viewModel: PreparationViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = WORLD_VIEW_ZOOM),
    )

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            RegionPickerMap(cameraState = cameraState, regions = uiState.regions, modifier = Modifier.fillMaxSize())

            FloatingActionButton(
                onClick = {
                    val projection = cameraState.projection ?: return@FloatingActionButton
                    val bbox = projection.queryVisibleBoundingBox()
                    val latSpan = bbox.north - bbox.south
                    val lonSpan = bbox.east - bbox.west
                    if (latSpan < MIN_DOWNLOAD_SPAN_DEGREES || lonSpan < MIN_DOWNLOAD_SPAN_DEGREES) {
                        return@FloatingActionButton
                    }
                    viewModel.onDownloadCurrentViewClicked(
                        west = bbox.west,
                        south = bbox.south,
                        east = bbox.east,
                        north = bbox.north,
                        currentZoom = cameraState.position.zoom,
                    )
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            ) {
                Icon(Icons.Filled.Download, contentDescription = stringResource(StringKey.PreparationDownloadCurrentViewButton))
            }
        }

        if (uiState.regions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(StringKey.PreparationEmptyList))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.regions, key = { it.name }) { region ->
                    OfflineRegionRow(
                        region = region,
                        onPauseClick = { viewModel.onPauseClicked(region.name) },
                        onResumeClick = { viewModel.onResumeClicked(region.name) },
                        onDeleteClick = { viewModel.onDeleteRequested(region.name) },
                    )
                }
            }
        }
    }

    if (uiState.showNameDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onNameDialogDismissed,
            title = { Text(stringResource(StringKey.PreparationRegionNameDialogTitle)) },
            text = {
                OutlinedTextField(
                    value = uiState.nameInput,
                    onValueChange = viewModel::onNameInputChanged,
                    singleLine = true,
                    placeholder = { Text(stringResource(StringKey.PreparationRegionNameLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                val nameTaken = uiState.regions.any { it.name == uiState.nameInput.trim() }
                TextButton(
                    onClick = viewModel::onNameConfirmed,
                    enabled = uiState.nameInput.isNotBlank() && !nameTaken,
                ) {
                    Text(stringResource(StringKey.PreparationSaveButton))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onNameDialogDismissed) {
                    Text(stringResource(StringKey.PreparationCancelButton))
                }
            },
        )
    }

    if (uiState.regionPendingDelete != null) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismissed,
            title = { Text(stringResource(StringKey.PreparationDeleteConfirmTitle)) },
            text = { Text(stringResource(StringKey.PreparationDeleteConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirmed) {
                    Text(stringResource(StringKey.PreparationDeleteConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteDismissed) {
                    Text(stringResource(StringKey.PreparationDeleteConfirmNo))
                }
            },
        )
    }
}

@Composable
private fun OfflineRegionRow(
    region: OfflineRegionInfo,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            OfflineRegionHeaderRow(region, onPauseClick, onResumeClick, onDeleteClick)

            if (region.status == OfflineRegionStatus.DOWNLOADING || region.status == OfflineRegionStatus.PAUSED) {
                val required = region.requiredTileCount
                if (required != null && required > 0) {
                    val fraction = (region.completedTileCount.toFloat() / required.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                }
            }

            Text(
                "${stringResource(StringKey.PreparationTileCountLabel)}: ${region.completedTileCount}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun OfflineRegionHeaderRow(
    region: OfflineRegionInfo,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(region.name, style = MaterialTheme.typography.titleMedium)
            Text(regionStatusLabel(region.status), style = MaterialTheme.typography.bodySmall)
        }
        when (region.status) {
            OfflineRegionStatus.DOWNLOADING -> IconButton(onClick = onPauseClick) {
                Icon(Icons.Filled.Pause, contentDescription = stringResource(StringKey.PreparationPauseContentDescription))
            }
            OfflineRegionStatus.PAUSED -> IconButton(onClick = onResumeClick) {
                Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(StringKey.PreparationResumeContentDescription))
            }
            OfflineRegionStatus.COMPLETE, OfflineRegionStatus.ERROR -> Unit
        }
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(StringKey.PreparationDeleteContentDescription))
        }
    }
}

@Composable
private fun regionStatusLabel(status: OfflineRegionStatus): String = when (status) {
    OfflineRegionStatus.DOWNLOADING -> stringResource(StringKey.PreparationStatusDownloading)
    OfflineRegionStatus.PAUSED -> stringResource(StringKey.PreparationStatusPaused)
    OfflineRegionStatus.COMPLETE -> stringResource(StringKey.PreparationStatusComplete)
    OfflineRegionStatus.ERROR -> stringResource(StringKey.PreparationStatusError)
}
