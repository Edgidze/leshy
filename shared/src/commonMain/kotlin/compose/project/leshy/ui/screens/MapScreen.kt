package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.map.MapMode
import compose.project.leshy.presentation.map.MapPeriod
import compose.project.leshy.presentation.map.MapStats
import compose.project.leshy.presentation.map.MapViewModel
import compose.project.leshy.ui.map.AggregatedFindsMap
import compose.project.leshy.ui.util.formatDistanceKm
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapScreen(viewModel: MapViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
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

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedPeriod == null,
                    onClick = { viewModel.selectPeriod(null) },
                    label = { Text(stringResource(StringKey.MapPeriodAll)) },
                )
            }
            items(uiState.availablePeriods, key = { it.year * 100 + it.month }) { period ->
                FilterChip(
                    selected = uiState.selectedPeriod == period,
                    onClick = { viewModel.selectPeriod(period) },
                    label = { Text(period.label()) },
                )
            }
        }

        when (uiState.mode) {
            MapMode.MAP -> AggregatedFindsMap(
                tracks = uiState.tracks,
                findLocations = uiState.findLocations,
                modifier = Modifier.fillMaxSize().weight(1f).padding(top = 8.dp),
            )
            MapMode.STATS -> MapStatsView(
                stats = uiState.stats,
                modifier = Modifier.fillMaxSize().weight(1f),
            )
        }
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

    Column(modifier = modifier.padding(16.dp)) {
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

private fun MapPeriod.label(): String = "${month.toString().padStart(2, '0')}.$year"
