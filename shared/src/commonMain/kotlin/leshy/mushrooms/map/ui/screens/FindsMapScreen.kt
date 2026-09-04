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
import leshy.mushrooms.map.domain.model.iconSource
import leshy.mushrooms.map.presentation.map.MapViewModel
import leshy.mushrooms.map.ui.components.MapFilterButton
import leshy.mushrooms.map.ui.components.MapFilterDialog
import leshy.mushrooms.map.ui.components.PlaceMarkDialogs
import leshy.mushrooms.map.ui.map.AggregatedFindsMap
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.map.PlaceMarker

/**
 * Сводная карта во весь экран — то, куда ведёт заставка «Карты находок» ([MapScreen]).
 *
 * Оправа общая с картой одной прогулки ([FullScreenMapScaffold]), содержимое — та же
 * [AggregatedFindsMap], что стоит на заставке, только с жестами, отмеченными местами и кнопкой
 * фильтра: полный фильтр (виды грибов) на страницу свода не влезал и живёт здесь.
 */
@Composable
fun FindsMapScreen(viewModel: MapViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryById = uiState.categories.associateBy { it.id }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    val selectedPlace = uiState.placeMarks.find { it.id == selectedPlaceId }

    FullScreenMapScaffold(
        onBack = onBack,
        action = {
            MapFilterButton(
                filterCount = uiState.filterCount,
                onClick = { showFilterDialog = true },
            )
        },
    ) { ornamentOptions, bannerPadding ->
        AggregatedFindsMap(
            tracks = uiState.tracks,
            markers = uiState.findMarks.map { mark ->
                val category = categoryById[mark.categoryId]
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = category?.colorHex ?: "#808080",
                    icon = category?.iconSource(),
                )
            },
            modifier = Modifier.fillMaxSize(),
            places = uiState.placeMarks.map { mark ->
                PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
            },
            onPlaceClick = { id -> selectedPlaceId = id },
            ornamentOptions = ornamentOptions,
            bannerAlignment = Alignment.BottomCenter,
            bannerPadding = bannerPadding,
        )
    }

    if (showFilterDialog) {
        MapFilterDialog(onDismissRequest = { showFilterDialog = false })
    }

    PlaceMarkDialogs(
        place = selectedPlace,
        onUpdate = viewModel::updatePlace,
        onDelete = viewModel::deletePlace,
        onDismissRequest = { selectedPlaceId = null },
    )
}
