package compose.project.leshy.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.OfflineRegionInfo
import compose.project.leshy.domain.model.OfflineRegionStatus
import compose.project.leshy.domain.util.estimateOfflineRegion
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.preparation.PreparationViewModel
import compose.project.leshy.ui.map.RegionPickerMap
import compose.project.leshy.ui.util.formatMegabytes
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraProjection
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

private val WORLD_VIEW_ZOOM = 2.0

// How much of the map viewport's shorter side the selection box can shrink/grow to. Capped well
// under 1.0 so the collapsed sheet (peek height, drawn over the same viewport) never covers it.
private const val MIN_SELECTION_FRACTION = 0.2f
private const val MAX_SELECTION_FRACTION = 0.7f
private const val DEFAULT_SELECTION_FRACTION = 0.5f

// The sheet's two heights — collapsed just shows a drag handle so the selection box on the map
// stays fully visible; expanded goes to roughly half the map viewport, enough room for the slider
// and the region strip without a second tap.
private val SHEET_PEEK_HEIGHT = 48.dp
private const val SHEET_EXPANDED_FRACTION = 0.5f
private val REGION_CHIP_WIDTH = 160.dp

@Composable
fun PreparationScreen(modifier: Modifier = Modifier, viewModel: PreparationViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = WORLD_VIEW_ZOOM),
    )
    val coroutineScope = rememberCoroutineScope()
    var isSelectingArea by remember { mutableStateOf(false) }
    var selectionFraction by remember { mutableStateOf(DEFAULT_SELECTION_FRACTION) }
    var isSheetExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            stringResource(StringKey.PreparationSubtitle),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val boxMaxWidth = maxWidth
            val boxMaxHeight = maxHeight

            RegionPickerMap(cameraState = cameraState, regions = uiState.regions, modifier = Modifier.fillMaxSize())

            if (isSelectingArea) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(selectionFraction)
                        .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary)),
                )
            } else {
                FloatingActionButton(
                    onClick = {
                        isSelectingArea = true
                        isSheetExpanded = true
                    },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = SHEET_PEEK_HEIGHT + 16.dp, end = 16.dp),
                ) {
                    Icon(Icons.Filled.Download, contentDescription = stringResource(StringKey.PreparationSelectAreaButton))
                }
            }

            // Reading the camera position (not just the projection, which is a stable object
            // reference that doesn't change on pan/zoom) subscribes this recomposition scope to
            // camera moves, so the live size estimate below stays in sync while the user pans or
            // zooms the map underneath the fixed-on-screen selection box.
            cameraState.position
            val projection = cameraState.projection

            PreparationSheet(
                expanded = isSheetExpanded,
                onExpandedChange = { isSheetExpanded = it },
                expandedHeight = boxMaxHeight * SHEET_EXPANDED_FRACTION,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                if (isSelectingArea) {
                    val estimate = projection?.let {
                        val bounds = selectionBoundsFromScreen(it, boxMaxWidth, boxMaxHeight, selectionFraction)
                        estimateOfflineRegion(bounds.west, bounds.south, bounds.east, bounds.north)
                    }
                    if (estimate != null) {
                        Text(
                            "${stringResource(StringKey.PreparationEstimatedSizeLabel)}: " +
                                "≈${formatMegabytes(estimate.estimatedBytes)}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Slider(
                        value = selectionFraction,
                        onValueChange = { selectionFraction = it },
                        valueRange = MIN_SELECTION_FRACTION..MAX_SELECTION_FRACTION,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
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
                                val bounds = selectionBoundsFromScreen(
                                    currentProjection,
                                    boxMaxWidth,
                                    boxMaxHeight,
                                    selectionFraction,
                                )
                                viewModel.onAreaSelected(bounds.west, bounds.south, bounds.east, bounds.north)
                                isSelectingArea = false
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(StringKey.PreparationDownloadThisAreaButton))
                        }
                    }
                }

                if (uiState.regions.isEmpty()) {
                    if (!isSelectingArea) {
                        Text(
                            stringResource(StringKey.PreparationEmptyList),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    ) {
                        items(uiState.regions, key = { it.name }) { region ->
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

/**
 * Slides between [SHEET_PEEK_HEIGHT] (just the handle, so the selection box on the map behind it
 * stays fully visible) and [expandedHeight] on tap of the handle. Deliberately tap-only, not
 * drag-to-resize — a hand-rolled drag gesture here would fight the map's own pan/zoom gestures
 * immediately above it, and isn't worth that risk for what's otherwise a two-state toggle.
 */
@Composable
private fun PreparationSheet(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    expandedHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val animatedHeight by animateDpAsState(if (expanded) expandedHeight else SHEET_PEEK_HEIGHT)
    Surface(
        modifier = modifier.fillMaxWidth().height(animatedHeight),
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SHEET_PEEK_HEIGHT)
                    .clickable { onExpandedChange(!expanded) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                content()
            }
        }
    }
}

private data class SelectionBounds(val west: Double, val south: Double, val east: Double, val north: Double)

// Converts the fixed on-screen selection box (a fraction of the map viewport, centered) into map
// coordinates via the four corners rather than just two — robust if the camera is rotated, same
// "always axis-aligned, possibly a bit larger than what's visually inside the box" tradeoff
// CameraProjection.queryVisibleBoundingBox itself documents for a tilted/rotated view.
private fun selectionBoundsFromScreen(
    projection: CameraProjection,
    maxWidth: Dp,
    maxHeight: Dp,
    fraction: Float,
): SelectionBounds {
    val insetX = maxWidth * (1f - fraction) / 2f
    val insetY = maxHeight * (1f - fraction) / 2f
    val corners = listOf(
        DpOffset(insetX, insetY),
        DpOffset(maxWidth - insetX, insetY),
        DpOffset(insetX, maxHeight - insetY),
        DpOffset(maxWidth - insetX, maxHeight - insetY),
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
