package compose.project.leshy.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.map.MapMode
import compose.project.leshy.presentation.map.MapStats
import compose.project.leshy.presentation.map.MapViewModel
import compose.project.leshy.ui.components.AddPlaceDialog
import compose.project.leshy.ui.components.DeletePlaceConfirmDialog
import compose.project.leshy.ui.components.MapFilterButton
import compose.project.leshy.ui.components.MapFilterDialog
import compose.project.leshy.ui.components.PlaceViewDialog
import compose.project.leshy.ui.map.AggregatedFindsMap
import compose.project.leshy.ui.map.MapMarker
import compose.project.leshy.ui.map.PlaceMarker
import compose.project.leshy.ui.util.formatDistanceKm
import org.koin.compose.viewmodel.koinViewModel

// In Map mode the button now shares its TopStart corner with the native scale bar (see
// mapOrnamentOptions — scale bar moved there instead of the compass, which now takes TopEnd).
// The scale bar is always visible (not just while rotated, unlike the compass), so this offset
// must reliably clear it: `barHeight(2dp) + textSize(8dp) + textBarMargin(2dp) +
// 2*borderWidth(2dp) = 14dp` of scale-bar content, plus the library's own fixed 8dp top inset
// (android-plugin-scalebar-v9 3.0.2 defaults, applied by maplibre-compose's AndroidScaleBar) puts
// its bottom edge around 22.dp from the map's top edge — 31.dp leaves a half-sized (9.dp) visual
// gap below it (was 18.dp).
// In Stats mode there's no map at all (a flat text list), so nothing to clear there.
private val FILTER_BUTTON_OFFSET_MAP = 31.dp
private val FILTER_BUTTON_OFFSET_STATS = (-6).dp

@Composable
fun MapScreen(modifier: Modifier = Modifier, viewModel: MapViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    var isEditingPlace by remember { mutableStateOf(false) }
    var confirmDeletePlace by remember { mutableStateOf(false) }
    val selectedPlace = uiState.placeMarks.find { it.id == selectedPlaceId }

    Column(modifier = modifier.fillMaxSize()) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            SegmentedButton(
                selected = uiState.mode == MapMode.MAP,
                onClick = { viewModel.selectMode(MapMode.MAP) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(stringResource(StringKey.MapToggleMap))
            }
            SegmentedButton(
                selected = uiState.mode == MapMode.STATS,
                onClick = { viewModel.selectMode(MapMode.STATS) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(stringResource(StringKey.MapToggleStats))
            }
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (uiState.mode) {
                MapMode.MAP -> {
                    val categoryById = uiState.categories.associateBy { it.id }
                    AggregatedFindsMap(
                        tracks = uiState.tracks,
                        markers = uiState.findMarks.map { mark ->
                            val category = categoryById[mark.categoryId]
                            MapMarker(
                                lat = mark.lat,
                                lon = mark.lon,
                                colorHex = category?.colorHex ?: "#808080",
                                iconRef = category?.iconRef,
                            )
                        },
                        places = uiState.placeMarks.map { mark ->
                            PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
                        },
                        onPlaceClick = { id -> selectedPlaceId = id },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                MapMode.STATS -> MapStatsView(stats = uiState.stats, modifier = Modifier.fillMaxSize())
            }

            val filterButtonOffset by animateDpAsState(
                targetValue = if (uiState.mode == MapMode.STATS) {
                    FILTER_BUTTON_OFFSET_STATS
                } else {
                    FILTER_BUTTON_OFFSET_MAP
                },
            )
            MapFilterButton(
                filterCount = uiState.filterCount,
                onClick = { showFilterDialog = true },
                modifier = Modifier.align(Alignment.TopStart).offset(y = filterButtonOffset).padding(start = 16.dp),
            )
        }
    }

    if (showFilterDialog) {
        MapFilterDialog(onDismissRequest = { showFilterDialog = false })
    }

    if (selectedPlace != null) {
        if (isEditingPlace) {
            AddPlaceDialog(
                location = GeoPoint(selectedPlace.lat, selectedPlace.lon, null, selectedPlace.timestamp),
                initialName = selectedPlace.name,
                initialDescription = selectedPlace.description.orEmpty(),
                initialPhotoPath = selectedPlace.photoPath,
                onSave = { name, description, photoPath ->
                    viewModel.updatePlace(selectedPlace, name, description, photoPath)
                    isEditingPlace = false
                },
                onDismissRequest = { isEditingPlace = false },
            )
        } else {
            PlaceViewDialog(
                mark = selectedPlace,
                onEditClick = { isEditingPlace = true },
                onDeleteClick = { confirmDeletePlace = true },
                onDismissRequest = { selectedPlaceId = null },
            )
        }
    }

    if (confirmDeletePlace && selectedPlace != null) {
        DeletePlaceConfirmDialog(
            onConfirm = {
                viewModel.deletePlace(selectedPlace)
                confirmDeletePlace = false
                selectedPlaceId = null
            },
            onDismissRequest = { confirmDeletePlace = false },
        )
    }
}

@Composable
private fun MapStatsView(stats: MapStats, modifier: Modifier = Modifier) {
    if (stats.walkCount == 0) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(stringResource(StringKey.ArchiveEmpty))
        }
        return
    }

    Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 50.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(StringKey.MapStatsWalksCount))
            Text(stats.walkCount.toString())
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(StringKey.WalkDetailDistance))
            Text(formatDistanceKm(stats.totalDistanceMeters))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(StringKey.MapStatsFindsCount))
            Text(stats.totalMushroomCount.toString())
        }

        Text(
            stringResource(StringKey.WalkDetailFindsTitle),
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(stats.categoryCounts) { entry ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(categoryDisplayName(entry.category.nameKey))
                    Text(entry.count.toString())
                }
            }
        }
    }
}
