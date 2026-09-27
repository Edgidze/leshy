package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.util.moonPhaseAt
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.trip.TripDetailViewModel
import klev.fishing.map.ui.components.WeatherDialog
import klev.fishing.map.ui.components.formatLength
import klev.fishing.map.ui.components.formatPressure
import klev.fishing.map.ui.components.formatTemperature
import klev.fishing.map.ui.components.formatWeight
import klev.fishing.map.ui.components.formatWind
import klev.fishing.map.ui.components.speciesDisplayName
import leshy.mushrooms.map.ui.map.AggregatedFindsMap
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import leshy.mushrooms.map.ui.util.formatTimeOnly
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TripDetailScreen(
    tripId: Long,
    onBack: () -> Unit,
    viewModel: TripDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(tripId) { viewModel.load(tripId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.deleted) { if (uiState.deleted) onBack() }

    val trip = uiState.trip ?: return

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(4.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
            Text(
                text = trip.title?.takeIf { it.isNotBlank() }
                    ?: trip.waterBody?.takeIf { it.isNotBlank() }
                    ?: fishStringResource(FishStringKey.ArchiveUnnamedTrip),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp, top = 12.dp),
            )
        }

        if (uiState.track.isNotEmpty() || uiState.catches.isNotEmpty()) {
            val markers = uiState.catches.map { item ->
                MapMarker(
                    lat = item.lat,
                    lon = item.lon,
                    colorHex = uiState.species[item.speciesId]?.colorHex ?: "#4f6b3a",
                )
            }
            AggregatedFindsMap(
                tracks = mapOf(trip.id to uiState.track),
                markers = markers,
                modifier = Modifier.fillMaxWidth().height(220.dp),
            )
        }

        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(formatDateTime(trip.startedAt), style = MaterialTheme.typography.bodyMedium)
            trip.finishedAt?.let { end ->
                Text(
                    text = "${formatTimeOnly(trip.startedAt)} — ${formatTimeOnly(end)}, " +
                        formatDurationShort(end - trip.startedAt),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(fishStringResource(trip.method.labelKey()), style = MaterialTheme.typography.bodyMedium)
                if (trip.distanceMeters > 0) {
                    Text(formatDistanceKm(trip.distanceMeters), style = MaterialTheme.typography.bodyMedium)
                }
            }

            WeatherBlock(
                trip = trip,
                pressureUnit = uiState.pressureUnit,
                onEdit = viewModel::editWeather,
            )

            if (uiState.catches.isEmpty()) {
                Text(fishStringResource(FishStringKey.ArchiveCatchesNone))
            } else {
                uiState.catches.forEach { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = uiState.species[item.speciesId]?.let { speciesDisplayName(it) }
                                    ?: item.speciesId.toString(),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            val measures = buildList {
                                item.weightGrams?.let { add(formatWeight(it)) }
                                item.lengthMm?.let { add(formatLength(it)) }
                                add(fishStringResource(item.outcome.labelKey()))
                                item.lostReason?.let { add(fishStringResource(it.labelKey())) }
                                item.bait?.let { add(it) }
                            }
                            Text(measures.joinToString(" · "), style = MaterialTheme.typography.bodySmall)
                            item.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                            TextButton(onClick = { viewModel.deleteCatch(item.id) }) {
                                Text(fishStringResource(FishStringKey.Delete))
                            }
                        }
                    }
                }
            }

            OutlinedButton(onClick = viewModel::delete, modifier = Modifier.fillMaxWidth()) {
                Text(fishStringResource(FishStringKey.ArchiveDelete))
            }
        }
    }

    val draft = uiState.weatherDraft
    if (draft != null) {
        WeatherDialog(
            draft = draft,
            pressureUnit = uiState.pressureUnit,
            suggesting = uiState.weatherSuggesting,
            suggestFailed = uiState.weatherSuggestFailed,
            onChange = viewModel::updateWeatherDraft,
            onSuggest = viewModel::suggestWeather,
            onSave = viewModel::saveWeather,
            onDismiss = viewModel::dismissWeather,
        )
    }
}

@Composable
private fun WeatherBlock(
    trip: klev.fishing.map.domain.model.Trip,
    pressureUnit: klev.fishing.map.domain.model.PressureUnit,
    onEdit: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val weather = trip.weather
            if (weather == null) {
                Text(fishStringResource(FishStringKey.WeatherEmpty), style = MaterialTheme.typography.bodyMedium)
            } else {
                val parts = buildList {
                    weather.airTempC?.let { add("${fishStringResource(FishStringKey.WeatherAirTemp)} ${formatTemperature(it)}") }
                    weather.waterTempC?.let { add("${fishStringResource(FishStringKey.WeatherWaterTemp)} ${formatTemperature(it)}") }
                    weather.pressureHpa?.let { add(formatPressure(it, pressureUnit)) }
                    weather.pressureTrend?.let { add(fishStringResource(it.labelKey())) }
                    weather.windSpeedMps?.let { add(formatWind(it)) }
                    weather.windDirection?.let { add(fishStringResource(it.labelKey())) }
                    weather.cloudiness?.let { add(fishStringResource(it.labelKey())) }
                    weather.precipitation?.let { add(fishStringResource(it.labelKey())) }
                }
                Text(parts.joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
                // Откуда цифры: подставленное сервисом и не тронутое — это мнение модели о узле
                // сетки, а не наблюдение, и читающий через год имеет право это различать.
                Text(
                    text = fishStringResource(weather.provenance.labelKey()),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            // Фаза Луны не вводится и не подсказывается: она вычисляется по дате (чистая
            // астрономия, офлайн, одинаковая во всём мире).
            Text(
                text = "${fishStringResource(FishStringKey.WeatherMoonPhase)}: " +
                    fishStringResource(moonPhaseAt(trip.startedAt).labelKey()),
                style = MaterialTheme.typography.bodySmall,
            )
            TextButton(onClick = onEdit) {
                Text(fishStringResource(FishStringKey.WeatherEdit))
            }
        }
    }
}
