package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.Trip
import klev.fishing.map.domain.util.moonPhaseAt
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.trip.TripDetailViewModel
import klev.fishing.map.ui.components.CatchSheet
import klev.fishing.map.ui.components.WeatherDialog
import klev.fishing.map.ui.components.formatDepth
import klev.fishing.map.ui.components.formatLength
import klev.fishing.map.ui.components.formatPressure
import klev.fishing.map.ui.components.formatTemperature
import klev.fishing.map.ui.components.formatWeight
import klev.fishing.map.ui.components.formatWind
import klev.fishing.map.ui.components.rememberMapRevealed
import klev.fishing.map.ui.components.speciesDisplayName
import klev.fishing.map.ui.map.CatchMap
import klev.fishing.map.ui.map.CatchMarker
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import leshy.mushrooms.map.ui.util.formatTimeOnly
import leshy.mushrooms.map.ui.util.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

/** Высота карты выезда: достаточно, чтобы увидеть форму маршрута, и мало, чтобы не съесть список. */
private val DETAIL_MAP_HEIGHT = 220.dp

/** Кружок цвета вида у строки улова — тот же приём, что в архиве и на карте. */
private val SPECIES_DOT_SIZE = 12.dp

/**
 * Один выезд целиком.
 *
 * **Каждая строка улова открывает тот же лист, что и на «Рыбалке».** Это и есть вторая половина
 * записи в одно касание: на воде рыба отмечается без цифр, а вечером, дома, ей дописывают вес,
 * приманку и исход — тем же листом, в котором её и уточняли бы сразу. Отдельной кнопки «удалить» у
 * каждой строки больше нет: удаление живёт в листе, рядом с правкой, и не превращает список в
 * частокол красных кнопок.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Long,
    onBack: () -> Unit,
    viewModel: TripDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(tripId) { viewModel.load(tripId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.deleted) { if (uiState.deleted) onBack() }

    var editingCatch by remember { mutableStateOf<Long?>(null) }
    var renameOpen by remember { mutableStateOf(false) }
    var deleteConfirmOpen by remember { mutableStateOf(false) }

    val trip = uiState.trip ?: return
    // Как и в архиве: у безымянного выезда заголовок — дата, а не слово «Рыбалка».
    val title = trip.title?.takeIf { it.isNotBlank() }
        ?: trip.waterBody?.takeIf { it.isNotBlank() }
        ?: formatDateTime(trip.startedAt)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { renameOpen = true }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = fishStringResource(FishStringKey.ArchiveRename),
                        )
                    }
                    IconButton(onClick = { deleteConfirmOpen = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = fishStringResource(FishStringKey.ArchiveDelete),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            if ((uiState.track.isNotEmpty() || uiState.catches.isNotEmpty()) && rememberMapRevealed()) {
                val markers = uiState.catches.map { item ->
                    CatchMarker(
                        lat = item.lat,
                        lon = item.lon,
                        colorHex = uiState.species[item.speciesId]?.colorHex ?: "#4f6b3a",
                    )
                }
                CatchMap(
                    tracks = mapOf(trip.id to uiState.track),
                    markers = markers,
                    modifier = Modifier.fillMaxWidth().height(DETAIL_MAP_HEIGHT),
                )
            }

            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard(trip = trip, catches = uiState.catches)

                WeatherBlock(
                    trip = trip,
                    pressureUnit = uiState.pressureUnit,
                    onEdit = viewModel::editWeather,
                )

                if (uiState.catches.isEmpty()) {
                    Text(fishStringResource(FishStringKey.ArchiveCatchesNone))
                } else {
                    uiState.catches.forEach { item ->
                        CatchRow(
                            item = item,
                            name = uiState.species[item.speciesId]?.let { speciesDisplayName(it) },
                            colorHex = uiState.species[item.speciesId]?.colorHex,
                            onClick = { editingCatch = item.id },
                        )
                    }
                }
            }
        }
    }

    val editing = editingCatch?.let { id -> uiState.catches.firstOrNull { it.id == id } }
    if (editing != null) {
        CatchSheet(
            initial = editing,
            // Виды берутся все, какие знает выезд, плюс активные: рыба могла быть отмечена видом,
            // который позже сняли с ленты, и в правке он обязан оставаться на месте.
            species = uiState.species.values.sortedBy { it.order },
            recentBaits = uiState.recentBaits,
            onDismiss = { editingCatch = null },
            onSave = { draft ->
                viewModel.updateCatch(
                    editing.copy(
                        speciesId = draft.speciesId,
                        weightGrams = draft.weightGrams,
                        lengthMm = draft.lengthMm,
                        depthCm = draft.depthCm,
                        bait = draft.bait?.trim()?.ifBlank { null },
                        outcome = draft.outcome,
                        lostReason = draft.lostReason,
                        note = draft.note?.trim()?.ifBlank { null },
                    )
                )
                editingCatch = null
            },
            onDelete = {
                viewModel.deleteCatch(editing.id)
                editingCatch = null
            },
        )
    }

    if (renameOpen) {
        var name by remember { mutableStateOf(trip.title.orEmpty()) }
        AlertDialog(
            onDismissRequest = { renameOpen = false },
            title = { Text(fishStringResource(FishStringKey.ArchiveRenameTitle)) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    // imePadding у поля — правило проекта: сдвига сцены над клавиатурой на iOS нет.
                    modifier = Modifier.fillMaxWidth().imePadding(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.rename(name)
                        renameOpen = false
                    },
                ) {
                    Text(fishStringResource(FishStringKey.Save))
                }
            },
            dismissButton = {
                TextButton(onClick = { renameOpen = false }) {
                    Text(fishStringResource(FishStringKey.Cancel))
                }
            },
        )
    }

    if (deleteConfirmOpen) {
        // Удаление выезда подтверждается: вместе с ним уходят трек, улов и погода, и вернуть их
        // неоткуда — резервной копии у приложения пока нет.
        AlertDialog(
            onDismissRequest = { deleteConfirmOpen = false },
            title = { Text(fishStringResource(FishStringKey.ArchiveDelete)) },
            text = { Text(fishStringResource(FishStringKey.ArchiveDeleteConfirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteConfirmOpen = false
                        viewModel.delete()
                    },
                ) {
                    Text(fishStringResource(FishStringKey.Delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmOpen = false }) {
                    Text(fishStringResource(FishStringKey.Cancel))
                }
            },
        )
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

/** Когда, чем и сколько: то, что читают первым и не ищут глазами по строкам. */
@Composable
private fun SummaryCard(trip: Trip, catches: List<Catch>) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(formatDateTime(trip.startedAt), style = MaterialTheme.typography.bodyMedium)
            trip.finishedAt?.let { end ->
                Text(
                    text = "${formatTimeOnly(trip.startedAt)} — ${formatTimeOnly(end)} · " +
                        formatDurationShort(end - trip.startedAt),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            val facts = buildList {
                add(fishStringResource(trip.method.labelKey()))
                if (trip.distanceMeters > 0) add(formatDistanceKm(trip.distanceMeters))
                add("${fishStringResource(FishStringKey.RecordCatchCount)}: ${catches.size}")
                val weight = catches.sumOf { it.weightGrams ?: 0 }
                if (weight > 0) add(formatWeight(weight))
            }
            Text(
                text = facts.joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CatchRow(item: Catch, name: String?, colorHex: String?, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (colorHex != null) {
                    Box(Modifier.size(SPECIES_DOT_SIZE).clip(CircleShape).background(parseHexColor(colorHex)))
                }
                Text(
                    text = name ?: item.speciesId.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = formatTimeOnly(item.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val measures = buildList {
                item.weightGrams?.let { add(formatWeight(it)) }
                item.lengthMm?.let { add(formatLength(it)) }
                item.depthCm?.let { add(formatDepth(it)) }
                add(fishStringResource(item.outcome.labelKey()))
                item.lostReason?.let { add(fishStringResource(it.labelKey())) }
                item.bait?.let { add(it) }
            }
            Text(measures.joinToString(" · "), style = MaterialTheme.typography.bodySmall)
            item.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun WeatherBlock(trip: Trip, pressureUnit: PressureUnit, onEdit: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            val weather = trip.weather
            if (weather == null) {
                Text(fishStringResource(FishStringKey.WeatherEmpty), style = MaterialTheme.typography.bodyMedium)
            } else {
                val parts = buildList {
                    weather.airTempC?.let {
                        add("${fishStringResource(FishStringKey.WeatherAirTemp)} ${formatTemperature(it)}")
                    }
                    weather.waterTempC?.let {
                        add("${fishStringResource(FishStringKey.WeatherWaterTemp)} ${formatTemperature(it)}")
                    }
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
