package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.presentation.map.CatchMapViewModel
import klev.fishing.map.ui.components.speciesDisplayName
import leshy.mushrooms.map.ui.map.AggregatedFindsMap
import leshy.mushrooms.map.ui.map.MapMarker
import org.koin.compose.viewmodel.koinViewModel

/**
 * Общая карта улова за всё время. Фильтр по виду — один ряд чипов: у рыбы видов десятки, а не
 * четыреста, и полноэкранный фильтр, как у грибов, здесь был бы не нужен.
 */
@Composable
fun CatchMapScreen(viewModel: CatchMapViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.catches.isEmpty() && !uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(fishStringResource(FishStringKey.MapEmpty), style = MaterialTheme.typography.titleMedium)
            Text(
                text = fishStringResource(FishStringKey.MapEmptyHint),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        val colors = uiState.species.associate { it.id to it.colorHex }
        val markers = uiState.visibleCatches.map { item ->
            MapMarker(lat = item.lat, lon = item.lon, colorHex = colors[item.speciesId] ?: "#4f6b3a")
        }
        AggregatedFindsMap(
            tracks = uiState.tracks,
            markers = markers,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = uiState.speciesFilter == null,
                    onClick = { viewModel.setSpeciesFilter(null) },
                    label = { Text(fishStringResource(FishStringKey.MapAllSpecies)) },
                )
            }
            items(uiState.species, key = { it.id }) { species ->
                FilterChip(
                    selected = uiState.speciesFilter == species.id,
                    onClick = {
                        viewModel.setSpeciesFilter(if (uiState.speciesFilter == species.id) null else species.id)
                    },
                    label = { Text(speciesDisplayName(species)) },
                )
            }
        }
    }
}
