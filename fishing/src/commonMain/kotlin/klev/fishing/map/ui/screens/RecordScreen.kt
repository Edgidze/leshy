package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import klev.fishing.map.ui.components.CatchSheet
import klev.fishing.map.ui.components.ChipFlow
import klev.fishing.map.ui.components.FishSectionScaffold
import klev.fishing.map.ui.components.SpeciesTile
import klev.fishing.map.ui.components.WeatherDialog
import klev.fishing.map.ui.components.formatWeight
import klev.fishing.map.ui.components.speciesDisplayName
import kotlinx.coroutines.delay
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.ui.map.LiveTrackMap
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.map.TrackEndpoints
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.ui.util.formatDurationShort
import org.koin.compose.viewmodel.koinViewModel

/** Высота главных кнопок панели. Material-дефолт 40dp — мало для руки в перчатке. */
private val PRIMARY_BUTTON_HEIGHT = 56.dp

/**
 * «Рыбалка» — карта сверху, панель управления снизу.
 *
 * **Разделение экрана пополам — не про красоту, а про то, чем на воде пользуются.** Карта нужна
 * глазами (где я, где брало), панель — пальцем. Поэтому всё нажимаемое собрано в нижнюю половину, а
 * ввод с клавиатуры убран с экрана целиком: название водоёма уехало в лист «Выезд» (его вводят один
 * раз, и обычно не на воде), а улов пишется касанием плитки без единой цифры.
 *
 * Разбор и внешняя фактура, из которой это следует, — `.claude/plans/fishing-ux.md`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    onMenuClick: () -> Unit,
    viewModel: TripViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val language = LocalAppLanguage.current

    // Часы идущей рыбалки тикают ЗДЕСЬ, а не во ViewModel: иначе каждое обновление времени
    // пересобирало бы UiState и перерисовывало карту раз в секунду.
    var now by remember { mutableLongStateOf(currentTimeMillis()) }
    LaunchedEffect(uiState.isRecording) {
        while (uiState.isRecording) {
            now = currentTimeMillis()
            delay(1000)
        }
    }

    var refiningCatchId by remember { mutableStateOf<Long?>(null) }
    // Удержание плитки = «записать и сразу уточнить». Id новой записи приходит асинхронно (пишет
    // корутина ViewModel), поэтому удержание поднимает флаг, а лист открывает тот же эффект, что
    // показывает снэкбар после обычного касания.
    var refineAfterQuickCatch by remember { mutableStateOf(false) }
    // Свой экземпляр сигнала на стороне экрана. Показать снэкбар прямо в эффекте, ключом которого
    // служит `justSavedCatchId`, нельзя: первое же действие эффекта — потребить сигнал, от этого
    // ключ становится `null`, и эффект отменяется ВМЕСТЕ с ещё не показанным снэкбаром (поймано на
    // эмуляторе: рыба записывалась, сообщения не было). Поэтому сигнал UiState потребляется сразу —
    // как того и требует правило про одноразовые сигналы, — а показывает его второй эффект, ключом
    // которому служит уже эта, экранная копия.
    var pendingSnackbarCatchId by remember { mutableStateOf<Long?>(null) }
    var newCatchOpen by remember { mutableStateOf(false) }
    var tripDetailsOpen by remember { mutableStateOf(false) }
    var finishConfirmOpen by remember { mutableStateOf(false) }

    val savedLabel = fishStringResource(FishStringKey.RecordCatchSaved)
    val refineLabel = fishStringResource(FishStringKey.RecordRefine)
    // Снэкбар — единственное, что сообщает о быстрой записи, и он же единственный путь к её правке
    // сразу после нажатия. Сигнал одноразовый и живёт в UiState (а не в `remember`), иначе при
    // восстановлении экрана он показался бы снова — правило проекта про одноразовые сигналы.
    LaunchedEffect(uiState.justSavedCatchId) {
        val id = uiState.justSavedCatchId ?: return@LaunchedEffect
        viewModel.consumeJustSaved()
        if (refineAfterQuickCatch) {
            // Удержание: лист открывается сразу, и сообщать «записано» уже нечем — это видно по
            // самому листу.
            refineAfterQuickCatch = false
            refiningCatchId = id
        } else {
            pendingSnackbarCatchId = id
        }
    }
    LaunchedEffect(pendingSnackbarCatchId) {
        val id = pendingSnackbarCatchId ?: return@LaunchedEffect
        val name = uiState.catchById(id)
            ?.let { uiState.speciesById(it.speciesId) }
            ?.let { speciesDisplayName(it, language) }
        val result = snackbarHostState.showSnackbar(
            message = if (name == null) savedLabel else "$savedLabel: $name",
            actionLabel = refineLabel,
            duration = SnackbarDuration.Short,
        )
        pendingSnackbarCatchId = null
        if (result == SnackbarResult.ActionPerformed) refiningCatchId = id
    }

    FishSectionScaffold(
        title = FishStringKey.RecordTitle,
        onMenuClick = onMenuClick,
        snackbarHostState = snackbarHostState,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
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

            // Панель отделена от карты своей поверхностью, а не только отбивкой: под ней карта, и
            // без плотного фона кнопки читались бы поверх спутникового пятна или тёмной воды.
            Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 3.dp) {
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
                        IdleControls(methods = uiState.methods, onStart = viewModel::start)
                    } else {
                        StatsRow(
                            elapsed = now - trip.startedAt,
                            distanceMeters = uiState.distanceMeters,
                            catchCount = uiState.catches.size,
                            totalWeightGrams = uiState.totalWeightGrams,
                        )

                        if (uiState.species.isEmpty()) {
                            Text(
                                text = fishStringResource(FishStringKey.RecordSpeciesHidden),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.species, key = { it.id }) { species ->
                                    SpeciesTile(
                                        species = species,
                                        count = uiState.countsBySpecies[species.id] ?: 0,
                                        // Касание — рыба записана. Удержание — записана и сразу
                                        // открыт лист уточнения: снэкбар для этого тоже годится, но
                                        // его надо успеть поймать.
                                        onClick = { viewModel.quickCatch(species.id) },
                                        onLongClick = {
                                            refineAfterQuickCatch = true
                                            viewModel.quickCatch(species.id)
                                        },
                                    )
                                }
                            }
                            Text(
                                text = fishStringResource(FishStringKey.RecordQuickHint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                onClick = { newCatchOpen = true },
                                modifier = Modifier.weight(1f).heightIn(min = PRIMARY_BUTTON_HEIGHT),
                            ) {
                                Text(fishStringResource(FishStringKey.RecordAddCatch))
                            }
                            FilledTonalIconButton(
                                onClick = { tripDetailsOpen = true },
                                modifier = Modifier.heightIn(min = PRIMARY_BUTTON_HEIGHT),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = fishStringResource(FishStringKey.RecordTripDetails),
                                )
                            }
                            OutlinedButton(
                                onClick = { finishConfirmOpen = true },
                                modifier = Modifier.weight(1f).heightIn(min = PRIMARY_BUTTON_HEIGHT),
                            ) {
                                Text(fishStringResource(FishStringKey.RecordFinish))
                            }
                        }
                    }
                }
            }
        }
    }

    val refining = refiningCatchId?.let { uiState.catchById(it) }
    if (refining != null) {
        CatchSheet(
            initial = refining,
            species = uiState.species,
            recentBaits = uiState.recentBaits,
            onDismiss = { refiningCatchId = null },
            onSave = { draft ->
                viewModel.updateCatch(
                    refining.copy(
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
                refiningCatchId = null
            },
            onDelete = {
                viewModel.deleteCatch(refining.id)
                refiningCatchId = null
            },
        )
    }

    if (newCatchOpen) {
        CatchSheet(
            initial = null,
            species = uiState.species,
            recentBaits = uiState.recentBaits,
            onDismiss = { newCatchOpen = false },
            onSave = { draft ->
                viewModel.addCatch(
                    speciesId = draft.speciesId,
                    weightGrams = draft.weightGrams,
                    lengthMm = draft.lengthMm,
                    depthCm = draft.depthCm,
                    bait = draft.bait,
                    outcome = draft.outcome,
                    lostReason = draft.lostReason,
                    note = draft.note,
                )
                newCatchOpen = false
            },
        )
    }

    val trip = uiState.trip
    if (tripDetailsOpen && trip != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { tripDetailsOpen = false }, sheetState = sheetState) {
            Column(
                // imePadding — у содержимого листа: внутри поле названия водоёма, а сцену над
                // клавиатурой на iOS никто не поднимет (правило проекта).
                modifier = Modifier.fillMaxWidth().imePadding().padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = fishStringResource(FishStringKey.RecordTripDetailsTitle),
                    style = MaterialTheme.typography.headlineSmall,
                )
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
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = fishStringResource(FishStringKey.SettingsMethodsTitle),
                    style = MaterialTheme.typography.labelLarge,
                )
                ChipFlow {
                    // В листе выезда показаны ВСЕ способы, а не только «мои»: способ этой рыбалки
                    // мог оказаться разовым (взял лодку у знакомого), и менять ради него настройку
                    // приложения — лишний шаг.
                    FishingMethod.entries.forEach { method ->
                        FilterChip(
                            selected = method == trip.method,
                            onClick = { viewModel.setMethod(method) },
                            label = { Text(fishStringResource(method.labelKey())) },
                        )
                    }
                }
            }
        }
    }

    if (finishConfirmOpen) {
        // Подтверждение, потому что кнопка стоит рядом с теми, которыми пользуются постоянно, а
        // отменить окончание рыбалки нечем: она уедет в архив, а трек остановится.
        AlertDialog(
            onDismissRequest = { finishConfirmOpen = false },
            title = { Text(fishStringResource(FishStringKey.RecordFinishConfirm)) },
            text = { Text(fishStringResource(FishStringKey.RecordFinishConfirmText)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        finishConfirmOpen = false
                        viewModel.finish()
                    },
                ) {
                    Text(fishStringResource(FishStringKey.RecordFinish))
                }
            },
            dismissButton = {
                TextButton(onClick = { finishConfirmOpen = false }) {
                    Text(fishStringResource(FishStringKey.Cancel))
                }
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

/**
 * Старт рыбалки. Способ предлагается только из «моих» (настройка): у кого один способ — тот видит
 * одну кнопку и ни одного лишнего выбора, а не три чипа, из которых два ему никогда не нужны.
 */
@Composable
private fun IdleControls(methods: Set<FishingMethod>, onStart: (FishingMethod) -> Unit) {
    // `firstOrNull` со страховкой: пустого множества хранилище не отдаёт (см.
    // `FishingSettingsRepository.observeMethods`), но экран старта без способа — это экран, с
    // которого нельзя начать рыбалку, и падать тут нечему.
    var method by remember(methods) { mutableStateOf(methods.firstOrNull() ?: FishingMethod.SHORE) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = fishStringResource(FishStringKey.RecordIdleHint),
            style = MaterialTheme.typography.bodyMedium,
        )
        if (methods.size > 1) {
            ChipFlow {
                FishingMethod.entries.filter { it in methods }.forEach { entry ->
                    FilterChip(
                        selected = entry == method,
                        onClick = { method = entry },
                        label = { Text(fishStringResource(entry.labelKey())) },
                    )
                }
            }
        }
        Button(
            onClick = { onStart(method) },
            modifier = Modifier.fillMaxWidth().heightIn(min = PRIMARY_BUTTON_HEIGHT),
        ) {
            Text(fishStringResource(FishStringKey.RecordStart))
        }
    }
}

@Composable
private fun StatsRow(elapsed: Long, distanceMeters: Double, catchCount: Int, totalWeightGrams: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Stat(FishStringKey.RecordDuration, formatDurationShort(elapsed))
        Stat(FishStringKey.RecordDistance, formatDistanceKm(distanceMeters))
        Stat(FishStringKey.RecordCatchCount, catchCount.toString())
        // Вес показывается только когда он есть: у быстрой записи его нет, и столбик «0 г» врал бы
        // про взвешенный ноль.
        if (totalWeightGrams > 0) {
            Stat(FishStringKey.RecordTotalWeight, formatWeight(totalWeightGrams))
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
