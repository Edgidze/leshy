package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.iconSource
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel
import leshy.mushrooms.map.ui.components.PlaceMarkDialogs
import leshy.mushrooms.map.ui.map.LiveTrackMap
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.map.PlaceMarker
import leshy.mushrooms.map.ui.map.TrackEndpoints

@Composable
fun WalkMapScreen(viewModel: WalkDetailViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryById = uiState.categories.associateBy { it.id }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    val selectedPlace = uiState.marks.find { it.id == selectedPlaceId }

    FullScreenMapScaffold(onBack = onBack) { ornamentOptions, bannerPadding ->
        LiveTrackMap(
            track = uiState.track,
            markers = uiState.marks.filter { it.type != MarkType.POI }.map { mark ->
                val category = categoryById[mark.categoryId]
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = category?.colorHex ?: "#808080",
                    icon = category?.iconSource(),
                )
            },
            places = uiState.marks.filter { it.type == MarkType.POI }.map { mark ->
                PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
            },
            onPlaceClick = { id -> selectedPlaceId = id },
            currentLocation = null,
            modifier = Modifier.fillMaxSize(),
            trackEndpoints = TrackEndpoints.StartAndFinish,
            ornamentOptions = ornamentOptions,
            bannerAlignment = Alignment.BottomCenter,
            bannerPadding = bannerPadding,
        )
    }

    PlaceMarkDialogs(
        place = selectedPlace,
        onUpdate = viewModel::updatePlace,
        onDelete = viewModel::deletePlace,
        onDismissRequest = { selectedPlaceId = null },
    )
}
