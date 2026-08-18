package compose.project.leshy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

@Composable
fun WalkMapScreen(viewModel: WalkDetailViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryById = uiState.categories.associateBy { it.id }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    var isEditingPlace by remember { mutableStateOf(false) }
    var confirmDeletePlace by remember { mutableStateOf(false) }
    val selectedPlace = uiState.marks.find { it.id == selectedPlaceId }

    // Full-bleed map with a floating back button, same shape as RecordScreen.kt/MapScreen.kt's
    // floating controls — NOT a Scaffold+TopAppBar wrapping a `.padding(padding)`-ed map. That
    // combination visibly mis-sized the map on iOS only (the whole viewport, ornaments included,
    // pushed down by far more than the app bar's real height — a CMP `Scaffold`/`TopAppBar` inset
    // quirk on iOS, not reproducible on Android), and this is the only map screen in the app that
    // used it; the others already float their controls over an unpadded full-screen map.
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
        )

        // 31.dp clears the native scale bar, which shares this corner (see mapOrnamentOptions) —
        // same rationale/value as MapFilterButton in RecordScreen.kt/MapScreen.kt.
        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(top = 31.dp, start = 16.dp),
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
