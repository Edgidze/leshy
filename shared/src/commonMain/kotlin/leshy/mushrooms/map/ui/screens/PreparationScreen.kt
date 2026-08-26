package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.OfflineRegionInfo
import leshy.mushrooms.map.domain.model.OfflineRegionStatus
import leshy.mushrooms.map.domain.util.estimateOfflineRegion
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.preparation.PreparationViewModel
import leshy.mushrooms.map.ui.map.RegionPickerMap
import leshy.mushrooms.map.ui.util.formatMegabytes
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraProjection
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

private val WORLD_VIEW_ZOOM = 2.0

private val REGION_CHIP_WIDTH = 140.dp
private val STRIP_BACKGROUND_ALPHA = 0.92f

@Composable
fun PreparationScreen(modifier: Modifier = Modifier, viewModel: PreparationViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = WORLD_VIEW_ZOOM),
    )
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    var isSelectingArea by remember { mutableStateOf(false) }
    // The usable area's measured size — everything above the bottom strip, whatever that strip's
    // current content happens to need. Tracked via onSizeChanged rather than BoxWithConstraints
    // because the bounds math below (the live estimate, the confirm button) needs it from sibling
    // scopes, not just the box it's measured on. This is also exactly the area that's actually
    // visible on screen once the strip covers the rest, so it doubles as the download region.
    var usableAreaSizePx by remember { mutableStateOf(IntSize.Zero) }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            stringResource(StringKey.PreparationSubtitle),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            RegionPickerMap(cameraState = cameraState, regions = uiState.regions, modifier = Modifier.fillMaxSize())

            // Reading the camera position (not just the projection, which is a stable object
            // reference that doesn't change on pan/zoom) subscribes this recomposition scope to
            // camera moves, so the live size estimate below stays in sync while the user pans or
            // zooms the map underneath.
            cameraState.position
            val projection = cameraState.projection
            val usableWidth = with(density) { usableAreaSizePx.width.toDp() }
            val usableHeight = with(density) { usableAreaSizePx.height.toDp() }

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).onSizeChanged { usableAreaSizePx = it },
                ) {
                    if (!isSelectingArea) {
                        FloatingActionButton(
                            onClick = { isSelectingArea = true },
                            // bottom = 64.dp, not the usual 16.dp: this corner is also where
                            // mapOrnamentOptions leaves the OSM/OpenFreeMap attribution button
                            // (library default, BottomEnd) — a plain 16.dp FAB footprint sits
                            // directly on top of it and hides the ODbL-mandated attribution.
                            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 64.dp),
                        ) {
                            Icon(Icons.Filled.Download, contentDescription = stringResource(StringKey.PreparationSelectAreaButton))
                        }
                    }
                }

                if (isSelectingArea) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = STRIP_BACKGROUND_ALPHA))
                            .padding(16.dp),
                    ) {
                        val estimate = projection?.let {
                            val bounds = visibleBoundsFromScreen(it, usableWidth, usableHeight)
                            estimateOfflineRegion(bounds.west, bounds.south, bounds.east, bounds.north)
                        }
                        if (estimate != null) {
                            Text(
                                "${stringResource(StringKey.PreparationEstimatedSizeLabel)}: " +
                                    "≈${formatMegabytes(estimate.estimatedBytes)}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedButton(
                                onClick = { isSelectingArea = false },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(stringResource(StringKey.PreparationCancelButton))
                            }
                            Button(
                                onClick = {
                                    val currentProjection = cameraState.projection ?: return@Button
                                    val bounds = visibleBoundsFromScreen(currentProjection, usableWidth, usableHeight)
                                    viewModel.onAreaSelected(bounds.west, bounds.south, bounds.east, bounds.north)
                                    isSelectingArea = false
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(stringResource(StringKey.PreparationDownloadThisAreaButton))
                            }
                        }
                    }
                } else if (uiState.regions.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = STRIP_BACKGROUND_ALPHA)),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Newest first — regions come back in creation order, so the one that just
                        // finished naming lands as the leftmost chip instead of requiring a scroll.
                        items(uiState.regions.reversed(), key = { it.name }) { region ->
                            OfflineRegionChip(
                                region = region,
                                onChipClick = {
                                    coroutineScope.launch {
                                        cameraState.animateTo(
                                            BoundingBox(
                                                west = region.west,
                                                south = region.south,
                                                east = region.east,
                                                north = region.north,
                                            ),
                                        )
                                    }
                                },
                                onPauseClick = { viewModel.onPauseClicked(region.name) },
                                onResumeClick = { viewModel.onResumeClicked(region.name) },
                                onDeleteClick = { viewModel.onDeleteRequested(region.name) },
                                onRetryClick = { viewModel.onRetryClicked(region.name) },
                                modifier = Modifier.width(REGION_CHIP_WIDTH),
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showNameDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onNameDialogDismissed,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = { Text(stringResource(StringKey.PreparationRegionNameDialogTitle)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.nameInput,
                        onValueChange = viewModel::onNameInputChanged,
                        singleLine = true,
                        placeholder = { Text(stringResource(StringKey.PreparationRegionNameLabel)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    uiState.pendingSelection?.let { selection ->
                        Text(
                            "${stringResource(StringKey.PreparationEstimatedSizeLabel)}: ≈${formatMegabytes(selection.estimatedBytes)}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
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
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
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

private data class SelectionBounds(val west: Double, val south: Double, val east: Double, val north: Double)

// Converts the usable on-screen area (everything above the bottom strip — i.e. what's actually
// visible on screen right now) into map coordinates via its four corners rather than just two —
// robust if the camera is rotated, same "always axis-aligned, possibly a bit larger than what's
// visually inside the box" tradeoff CameraProjection.queryVisibleBoundingBox itself documents for
// a tilted view.
private fun visibleBoundsFromScreen(projection: CameraProjection, maxWidth: Dp, maxHeight: Dp): SelectionBounds {
    val corners = listOf(
        DpOffset(0.dp, 0.dp),
        DpOffset(maxWidth, 0.dp),
        DpOffset(0.dp, maxHeight),
        DpOffset(maxWidth, maxHeight),
    ).map(projection::positionFromScreenLocation)
    return SelectionBounds(
        west = corners.minOf { it.longitude },
        south = corners.minOf { it.latitude },
        east = corners.maxOf { it.longitude },
        north = corners.maxOf { it.latitude },
    )
}

@Composable
private fun OfflineRegionChip(
    region: OfflineRegionInfo,
    onChipClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onChipClick, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(
                region.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
            )
            Text(regionStatusLabel(region.status), style = MaterialTheme.typography.bodySmall)

            if (region.status == OfflineRegionStatus.DOWNLOADING || region.status == OfflineRegionStatus.PAUSED) {
                val required = region.requiredTileCount
                if (required != null && required > 0) {
                    val fraction = (region.completedTileCount.toFloat() / required.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    )
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                }
            }

            Text(
                "≈${formatMegabytes(region.completedBytes)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.End) {
                when (region.status) {
                    OfflineRegionStatus.DOWNLOADING -> IconButton(onClick = onPauseClick) {
                        Icon(Icons.Filled.Pause, contentDescription = stringResource(StringKey.PreparationPauseContentDescription))
                    }
                    OfflineRegionStatus.PAUSED -> IconButton(onClick = onResumeClick) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(StringKey.PreparationResumeContentDescription))
                    }
                    OfflineRegionStatus.ERROR -> IconButton(onClick = onRetryClick) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(StringKey.PreparationRetryContentDescription))
                    }
                    OfflineRegionStatus.COMPLETE -> Unit
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(StringKey.PreparationDeleteContentDescription))
                }
            }
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
