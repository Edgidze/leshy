package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.record.TripViewModel
import klev.fishing.map.ui.components.CatchDialog
import klev.fishing.map.ui.components.SpeciesTile
import klev.fishing.map.ui.components.WeatherDialog
import klev.fishing.map.ui.components.formatWeight
import kotlinx.coroutines.delay
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.ui.map.LiveTrackMap
import leshy.mushrooms.map.ui.map.TrackEndpoints
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecordScreen(viewModel: TripViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Часы идущей рыбалки тикают ЗДЕСЬ, а не во ViewModel: иначе каждое обновление времени
    // пересобирало бы UiState и перерисовывало карту раз в секунду.
    var now by remember { mutableLongStateOf(currentTimeMillis()) }
    LaunchedEffect(uiState.isRecording) {
        while (uiState.isRecording) {
            now = currentTimeMillis()
            delay(1000)
        }
    }

    var catchForSpeciesId by remember { mutableStateOf<Long?>(null) }
    var catchPickerOpen by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        val markers = remember(uiState.catches, uiState.species) {
            val colors = uiState.species.associate { it.id to it.colorHex }
            uiState.catches.map { item ->
                MapMarker(
                    lat = item.lat,
                    lon = item.lon,
                    colorHex = colors[item.speciesId] ?: "#4f6b3a",
                )
            }
        }
        LiveTrackMap(
            track = uiState.track,
            markers = markers,
            currentLocation = uiState.currentLocation,
            modifier = Modifier.fillMaxWidth().weight(1f),
            trackEndpoints = if (uiState.isRecording) TrackEndpoints.StartOnly else TrackEndpoints.None,
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (!uiState.locationAvailable) {
                Text(
                    text = fishStringResource(FishStringKey.RecordNoLocation),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            val trip = uiState.trip
            if (trip == null || !trip.isActive) {
                IdleControls(onStart = viewModel::start)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Stat(FishStringKey.RecordDuration, formatDurationShort(now - trip.startedAt))
                    Stat(FishStringKey.RecordDistance, formatDistanceKm(uiState.distanceMeters))
                    Stat(FishStringKey.RecordCatchCount, uiState.catches.size.toString())
                    if (uiState.totalWeightGrams > 0) {
                        Stat(FishStringKey.RecordTotalWeight, formatWeight(uiState.totalWeightGrams))
                    }
                }

                MethodChips(current = trip.method, onPick = viewModel::setMethod)

                var waterBody by remember(trip.id) { mutableStateOf(trip.waterBody.orEmpty()) }
                OutlinedTextField(
                    value = waterBody,
                    onValueChange = {
                        waterBody = it
                        viewModel.setWaterBody(it)
                    },
                    label = { Text(fishStringResource(FishStringKey.RecordWaterBodyLabel)) },
                    placeholder = { Text(fishStringResource(FishStringKey.RecordWaterBodyHint)) },
                    singleLine = true,
                    // imePadding у КАЖДОГО поля ввода — правило проекта: сдвиг всей сцены над
                    // клавиатурой на iOS отключён, подстраховки от рантайма нет.
                    modifier = Modifier.fillMaxWidth().imePadding(),
                )

                if (uiState.species.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.species, key = { it.id }) { species ->
                            SpeciesTile(
                                species = species,
                                count = uiState.countsBySpecies[species.id] ?: 0,
                                onClick = { catchForSpeciesId = species.id },
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = { catchPickerOpen = true }, modifier = Modifier.weight(1f)) {
                        Text(fishStringResource(FishStringKey.RecordAddCatch))
                    }
                    OutlinedButton(onClick = viewModel::finish, modifier = Modifier.weight(1f)) {
                        Text(fishStringResource(FishStringKey.RecordFinish))
                    }
                }
            }
        }
    }

    val speciesId = catchForSpeciesId
    if (speciesId != null || catchPickerOpen) {
        CatchDialog(
            species = uiState.species,
            preselectedSpeciesId = speciesId,
            recentBaits = uiState.recentBaits,
            onDismiss = {
                catchForSpeciesId = null
                catchPickerOpen = false
            },
            onSave = { id, weight, length, bait, outcome, reason, note ->
                viewModel.addCatch(id, weight, length, bait, outcome, reason, note)
                catchForSpeciesId = null
                catchPickerOpen = false
            },
        )
    }

    val weatherDraft = uiState.weatherDraft
    if (uiState.weatherPromptTripId != null && weatherDraft != null) {
        WeatherDialog(
            draft = weatherDraft,
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
private fun IdleControls(onStart: (FishingMethod) -> Unit) {
    var method by remember { mutableStateOf(FishingMethod.SHORE) }
    Card {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = fishStringResource(FishStringKey.RecordIdleHint),
                style = MaterialTheme.typography.bodyMedium,
            )
            MethodChips(current = method, onPick = { method = it })
            Button(onClick = { onStart(method) }, modifier = Modifier.fillMaxWidth()) {
                Text(fishStringResource(FishStringKey.RecordStart))
            }
        }
    }
}

@Composable
private fun MethodChips(current: FishingMethod, onPick: (FishingMethod) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FishingMethod.entries.forEach { method ->
            FilterChip(
                selected = method == current,
                onClick = { onPick(method) },
                label = { Text(fishStringResource(method.labelKey())) },
            )
        }
    }
}

@Composable
private fun Stat(labelKey: FishStringKey, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = fishStringResource(labelKey),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}
