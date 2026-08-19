package compose.project.leshy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.ui.components.AddPlaceDialog
import compose.project.leshy.ui.components.DeletePlaceConfirmDialog
import compose.project.leshy.ui.components.PlaceViewDialog
import compose.project.leshy.ui.map.LiveTrackMap
import compose.project.leshy.ui.map.MapMarker
import compose.project.leshy.ui.map.PlaceMarker
import compose.project.leshy.ui.map.mapOrnamentOptions

@Composable
fun WalkMapScreen(viewModel: WalkDetailViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryById = uiState.categories.associateBy { it.id }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    var isEditingPlace by remember { mutableStateOf(false) }
    var confirmDeletePlace by remember { mutableStateOf(false) }
    val selectedPlace = uiState.marks.find { it.id == selectedPlaceId }

    // Unlike RecordScreen.kt/MapScreen.kt, this screen has no Scaffold+TopAppBar to consume the
    // top system inset (that combination visibly mis-sized the map on iOS only — see
    // ui/map/CLAUDE.md), so the full-bleed map underneath actually extends behind the status
    // bar/camera cutout. Every other map screen's fixed 31.dp top offset only has to clear the
    // scale bar because a Scaffold already pushed the map below the status bar for them; here it
    // must also clear the cutout itself, so the safe-drawing inset is added on top of that 31.dp.
    val topInset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
    // Same reasoning as topInset above — no Scaffold here to already push content clear of the
    // bottom system nav bar/gesture inset, so the banner has to account for it itself.
    val bottomInset = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()

    Box(modifier = Modifier.fillMaxSize()) {
        LiveTrackMap(
            track = uiState.track,
            markers = uiState.marks.filter { it.type != MarkType.POI }.map { mark ->
                val category = categoryById[mark.categoryId]
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = category?.colorHex ?: "#808080",
                    iconRef = category?.iconRef,
                )
            },
            places = uiState.marks.filter { it.type == MarkType.POI }.map { mark ->
                PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
            },
            onPlaceClick = { id -> selectedPlaceId = id },
            currentLocation = null,
            modifier = Modifier.fillMaxSize(),
            ornamentOptions = mapOrnamentOptions.copy(padding = PaddingValues(top = topInset)),
            bannerAlignment = Alignment.BottomCenter,
            bannerPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = bottomInset + 16.dp),
        )

        // 31.dp clears the native scale bar, which shares this corner (see mapOrnamentOptions) —
        // same rationale/value as MapFilterButton in RecordScreen.kt/MapScreen.kt. topInset on top
        // of that clears the status bar/camera cutout itself (see comment above).
        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(top = topInset + 31.dp, start = 16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 4.dp,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
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
