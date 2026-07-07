package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.presentation.map.MapMode
import compose.project.leshy.presentation.map.MapPeriod
import compose.project.leshy.presentation.map.MapStats
import compose.project.leshy.presentation.map.MapViewModel
import compose.project.leshy.ui.map.AggregatedFindsMap
import compose.project.leshy.ui.util.formatDistanceKm
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.archive_empty
import leshy.shared.generated.resources.map_period_all
import leshy.shared.generated.resources.map_stats_finds_count
import leshy.shared.generated.resources.map_stats_walks_count
import leshy.shared.generated.resources.map_toggle_map
import leshy.shared.generated.resources.map_toggle_stats
import leshy.shared.generated.resources.walk_detail_distance
import leshy.shared.generated.resources.walk_detail_finds_title
import org.jetbrains.compose.resources.stringResource
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
                Text(stringResource(Res.string.map_toggle_map))
            }
            SegmentedButton(
                selected = uiState.mode == MapMode.STATS,
                onClick = { viewModel.selectMode(MapMode.STATS) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(stringResource(Res.string.map_toggle_stats))
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
                    label = { Text(stringResource(Res.string.map_period_all)) },
                )
            }
            items(uiState.availablePeriods, key = { it }) { period ->
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
            Text(stringResource(Res.string.archive_empty))
        }
        return
    }

    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(Res.string.map_stats_walks_count))
            Text(stats.walkCount.toString())
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Res.string.walk_detail_distance))
            Text(formatDistanceKm(stats.totalDistanceMeters))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Res.string.map_stats_finds_count))
            Text(stats.totalMushroomCount.toString())
        }

        Text(
            stringResource(Res.string.walk_detail_finds_title),
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
        stats.categoryCounts.forEach { entry ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(categoryDisplayName(entry.category.nameKey))
                Text(entry.count.toString())
            }
        }
    }
}

private fun MapPeriod.label(): String = "${month.toString().padStart(2, '0')}.$year"
