package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import leshy.mushrooms.map.data.platform.WALK_THUMBNAIL_ASPECT_RATIO
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.mushroomsUnitLabel
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel
import leshy.mushrooms.map.ui.components.FindTilesGrid
import leshy.mushrooms.map.ui.components.FindsEmptyBlock
import leshy.mushrooms.map.ui.components.MetricCard
import leshy.mushrooms.map.ui.components.MushroomDonutChart
import leshy.mushrooms.map.ui.components.PlaceMarkDialogs
import leshy.mushrooms.map.ui.components.SectionHeader
import leshy.mushrooms.map.ui.components.WalkRouteThumbnail
import leshy.mushrooms.map.ui.components.WalkShareDialog
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationLabeled
import leshy.mushrooms.map.ui.util.formatSpeedKmh
import leshy.mushrooms.map.ui.util.formatTimeOnly
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import leshy.shared.generated.resources.ic_route
import leshy.shared.generated.resources.ic_stopwatch
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

private val SCREEN_PADDING = 16.dp

// Nothing in this bar is taller than an icon button once the title is gone — see the TopAppBar below.
private val TOP_BAR_HEIGHT = 48.dp

// Caps how much of the bar a single action label may claim before it ellipsizes, so a language
// with long words for "share"/"delete" can't push the pair past the screen edge.
private val ACTION_LABEL_MAX_WIDTH = 120.dp

private val MUSHROOM_TOAST_DURATION = 3000.milliseconds

/**
 * Высота заставки задаётся пропорцией снимка, а не числом. Числом она и была задана (200dp) — при
 * квадратном снимке, который в эту полосу вписывался по ширине и обрезался сверху и снизу. Обрезка
 * и оказалась тем, из-за чего заставка выглядела плохо: снимок 240×240 растягивался до ширины
 * экрана впятеро, а потом у растянутого срезалось 44% высоты вместе с куском маршрута. Теперь
 * снимок снимается сразу в этой пропорции (см. [WALK_THUMBNAIL_ASPECT_RATIO]), и показывается
 * целиком, ничего не теряя.
 */
private val HERO_CORNER_RADIUS = 16.dp

private val PLACE_THUMBNAIL_SIZE = 64.dp

@Composable
fun WalkDetailScreen(
    viewModel: WalkDetailViewModel,
    onBack: () -> Unit,
    onViewMap: () -> Unit,
    onEditDescription: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val walk = uiState.walk
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val places = uiState.marks.filter { it.type == MarkType.POI }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }
    val selectedPlace = places.find { it.id == selectedPlaceId }
    val findLocations = remember(uiState.marks) {
        uiState.marks.filter { it.type == MarkType.MUSHROOM }
            .map { GeoPoint(it.lat, it.lon, null, it.timestamp) }
    }

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) onBack()
    }

    if (uiState.showEditDialog && walk != null) {
        WalkNameEditDialog(
            initialName = walk.name,
            onConfirm = viewModel::onEditConfirm,
            onDismissRequest = viewModel::onEditDismiss,
        )
    }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = { Text(stringResource(StringKey.WalkDetailDeleteConfirmTitle)) },
            text = { Text(stringResource(StringKey.WalkDetailDeleteConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirm) {
                    Text(stringResource(StringKey.WalkDetailDeleteConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteDismiss) {
                    Text(stringResource(StringKey.WalkDetailDeleteConfirmNo))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            // The name moved out of the bar and onto its own row below (see WalkHeading), so the
            // bar carries no title at all any more — which is also why it can be shorter than the
            // Material default of TopAppBarDefaults.TopAppBarExpandedHeight (64.dp): nothing in it
            // is taller than an icon button. Still a real TopAppBar rather than a hand-rolled Row,
            // so it keeps handling the status-bar inset itself under enableEdgeToEdge().
            TopAppBar(
                title = {},
                expandedHeight = TOP_BAR_HEIGHT,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    LabeledAction(
                        icon = Icons.Filled.Share,
                        label = stringResource(StringKey.WalkDetailShareAction),
                        onClick = { showShareDialog = true },
                    )
                    LabeledAction(
                        icon = Icons.Filled.Delete,
                        label = stringResource(StringKey.WalkDetailDeleteAction),
                        onClick = viewModel::onDeleteClick,
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (walk == null) return@Scaffold

        // Один список на весь экран, без закреплённой снизу кнопки: «Смотреть карту» переехало
        // на саму заставку (см. [WalkHero]), и держать ради него отдельный подвал больше не надо.
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = SCREEN_PADDING, end = SCREEN_PADDING, bottom = 24.dp),
        ) {
            item {
                WalkHeading(walk = walk, onEditClick = viewModel::onEditClick)
                WalkHero(
                    walk = walk,
                    track = uiState.track,
                    findLocations = findLocations,
                    onClick = onViewMap,
                )
                WalkMetricsRow(walk = walk, findCount = uiState.mushroomCounts.sumOf { it.count })
                WalkTimeline(walk = walk)
            }

            if (uiState.mushroomCounts.isEmpty()) {
                item { FindsEmptyBlock() }
            } else {
                item {
                    SectionHeader(title = stringResource(StringKey.WalkDetailFindsTitle))
                    FindTilesGrid(counts = uiState.mushroomCounts)
                    MushroomDonutChart(
                        counts = uiState.mushroomCounts,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        onMushroomClick = { name ->
                            coroutineScope.launch {
                                // showSnackbar only accepts the fixed Short/Long/Indefinite
                                // durations — Indefinite plus a manual dismiss after exactly
                                // MUSHROOM_TOAST_DURATION is how you get a custom one.
                                launch {
                                    delay(MUSHROOM_TOAST_DURATION)
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                                snackbarHostState.showSnackbar(message = name, duration = SnackbarDuration.Indefinite)
                            }
                        },
                    )
                }
            }

            item {
                SectionHeader(
                    title = stringResource(StringKey.WalkDetailDescriptionTitle),
                    action = {
                        IconButton(onClick = onEditDescription) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = stringResource(StringKey.WalkDetailEditDescriptionContentDescription),
                            )
                        }
                    },
                )
                DescriptionCard(description = walk.description, onClick = onEditDescription)
            }

            if (places.isNotEmpty()) {
                item { SectionHeader(title = stringResource(StringKey.WalkDetailPlacesTitle)) }
                items(places) { place ->
                    PlaceListItem(place = place, onClick = { selectedPlaceId = place.id })
                }
            }
        }
    }

    PlaceMarkDialogs(
        place = selectedPlace,
        onUpdate = viewModel::updatePlace,
        onDelete = viewModel::deletePlace,
        onDismissRequest = { selectedPlaceId = null },
    )

    if (showShareDialog && walk != null) {
        WalkShareDialog(
            walk = walk,
            mushroomCounts = uiState.mushroomCounts,
            track = uiState.track,
            marks = uiState.marks,
            categories = uiState.categories,
            onDismiss = { showShareDialog = false },
        )
    }
}

/**
 * Заголовок воспоминания: дата надстрочником, под ней — название прогулки и карандаш переименования.
 *
 * Дата стоит НАД названием, а не рядом с ним и не строкой «Старт: 20.08.2026 07:14» ниже: архив —
 * это долгая память, и вспоминают прогулку по дню, а не по названию (название по умолчанию —
 * «Прогулка от 20.08.2026», то есть та же дата). Поэтому дата набрана отдельно и цветом темы, а
 * время старта и финиша ушло в [WalkTimeline] — там оно уже без даты, повторять её незачем.
 */
@Composable
private fun WalkHeading(walk: Walk, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp)) {
        Text(
            text = formatDateOnly(walk.startTime),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = walk.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = stringResource(StringKey.WalkDetailEditContentDescription),
                )
            }
        }
    }
}

/**
 * Карта прогулки во всю ширину — то, ради чего экран открывают. Картинка та же, что на карточке
 * архива (`Walk.thumbnailPath`, снимок с настоящими тайлами, отрисованный один раз на «Финише»),
 * с тем же запасным вариантом — векторным силуэтом маршрута [WalkRouteThumbnail] — для прогулок
 * старше этой возможности, для неудавшихся снимков (например, без сети) и для окна между
 * «Финишем» и готовностью снимка.
 *
 * Вся заставка — кнопка на полный экран карты. Прежняя `OutlinedButton` внизу экрана этим не
 * подменена молча: подпись с тем же текстом стоит на самой заставке, иначе нажимаемость картинки
 * ничем бы не выдавалась.
 *
 * **У прогулки без геоданных заставка не кнопка и подписи не несёт.** Прогулка, записанная там,
 * где GPS так и не дал фикса, не имеет ни трека, ни координат находок — показывать на полном
 * экране карты нечего, и «Смотреть карту» вело бы на пустую карту. Вместо снимка там стоит гриб
 * на фоне (см. [WalkRouteThumbnail]), и нажимать на него не на что.
 */
@Composable
private fun WalkHero(walk: Walk, track: List<GeoPoint>, findLocations: List<GeoPoint>, onClick: () -> Unit) {
    var loadFailed by remember(walk.thumbnailPath) { mutableStateOf(false) }
    val thumbnailPath = walk.thumbnailPath
    val hasGeodata = track.isNotEmpty() || findLocations.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(WALK_THUMBNAIL_ASPECT_RATIO)
            .clip(RoundedCornerShape(HERO_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(if (hasGeodata) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        if (thumbnailPath == null || loadFailed) {
            WalkRouteThumbnail(
                track = track,
                findLocations = findLocations,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = "file://$thumbnailPath",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = { loadFailed = true },
            )
        }

        // Подложка у подписи непрозрачная: она ложится на карту, где под ней может оказаться что
        // угодно — от светлого поля до тёмного леса.
        if (hasGeodata) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(StringKey.WalkDetailViewMap),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * Три показателя прогулки крупными блоками: находки, километраж, продолжительность.
 *
 * Раньше на их месте стояли шесть строк «ключ: значение» подряд — форма отчёта, а не форма
 * воспоминания. Порядок не случаен: находки первыми, потому что `VISION.md` первым же абзацем
 * говорит, что трек и километраж — контекст, а ценность приложения — история находок.
 *
 * Подписи словами не поставлены сознательно, ровно как в шапке «Записи»: показатель называет свой
 * значок, а слово («Продолжительность» — 16 букв в русском и длиннее в половине из 26 языков) в
 * блок шириной в треть экрана не встаёт ни в одном разумном кегле. Название уходит в
 * `contentDescription` значка, поэтому чтение вслух ничего не теряет.
 */
@Composable
private fun WalkMetricsRow(walk: Walk, findCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetricCard(
            icon = painterResource(Res.drawable.ic_mushrooms),
            label = "$findCount ${mushroomsUnitLabel(findCount)}",
            value = findCount.toString(),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            icon = painterResource(Res.drawable.ic_route),
            label = stringResource(StringKey.WalkDetailDistance),
            value = formatDistanceKm(walk.distanceMeters),
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            icon = painterResource(Res.drawable.ic_stopwatch),
            label = stringResource(StringKey.WalkDetailDuration),
            value = walk.endTime?.let { formatDurationLabeled(it - walk.startTime) } ?: "—",
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Старт, финиш и средняя скорость — приглушённым мелким набором под показателями. Это не отмена
 * прежних строк «ключ: значение», а понижение их в правах: цифры, которые смотрят раз в жизни,
 * не должны стоять тем же кеглем, что и те, ради которых экран открывают.
 *
 * Время старта и финиша — без даты, она уже стоит заголовком ([WalkHeading]). Исключение —
 * прогулка, перевалившая за полночь: у такой финиш показывается с датой целиком, иначе «07:14 →
 * 01:30» читалось бы как прогулка длиной в минус шесть часов. Сравниваются готовые строки дат, а
 * не календарные поля: разбор дат уже сделан внутри форматтеров, и повторять его ради одного
 * сравнения незачем.
 */
@Composable
private fun WalkTimeline(walk: Walk) {
    val endTime = walk.endTime
    val endLabel = when {
        endTime == null -> stringResource(StringKey.WalkDetailInProgress)
        formatDateOnly(endTime) == formatDateOnly(walk.startTime) -> formatTimeOnly(endTime)
        else -> formatDateTime(endTime)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        DetailRow(label = stringResource(StringKey.WalkDetailStartTime), value = formatTimeOnly(walk.startTime))
        DetailRow(label = stringResource(StringKey.WalkDetailEndTime), value = endLabel)
        DetailRow(
            label = stringResource(StringKey.WalkDetailAvgSpeed),
            value = if (endTime == null) "—" else formatSpeedKmh(walk.avgSpeed),
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f, fill = false),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
@Composable
private fun DescriptionCard(description: String?, onClick: () -> Unit) {
    val text = description?.ifBlank { null }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Text(
            text = text ?: stringResource(StringKey.WalkDetailDescriptionEmpty),
            style = MaterialTheme.typography.bodyLarge,
            color = if (text == null) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

/**
 * Top-bar action with its name spelled out next to the icon. `contentDescription` is null on the
 * icon on purpose — the visible label already names the action for a screen reader, and setting
 * both would have it announced twice.
 */
@Composable
private fun LabeledAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 10.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = ACTION_LABEL_MAX_WIDTH),
        )
    }
}

/**
 * Место с фотографией — карточкой, а не строкой списка: фотография места это единственная
 * настоящая картинка, которую пользователь снял сам, и 48dp рядом со строчкой текста ей мало.
 * Место без фотографии получает поле того же размера со значком, чтобы список не рвался по
 * высоте от того, к чему фото приложили, а к чему нет.
 */
@Composable
private fun PlaceListItem(place: FieldMark, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (place.photoPath != null) {
                AsyncImage(
                    model = "file://${place.photoPath}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(PLACE_THUMBNAIL_SIZE).clip(RoundedCornerShape(8.dp)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(PLACE_THUMBNAIL_SIZE)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name?.ifBlank { null } ?: stringResource(StringKey.AddPlaceDefaultName),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val placeDescription = place.description?.ifBlank { null }
                if (placeDescription != null) {
                    Text(
                        text = placeDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun WalkNameEditDialog(initialName: String, onConfirm: (String) -> Unit, onDismissRequest: () -> Unit) {
    var nameInput by remember { mutableStateOf(initialName) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    fun confirm() {
        focusManager.clearFocus()
        keyboardController?.hide()
        onConfirm(nameInput.ifBlank { initialName })
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f).imePadding(),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = { Text(stringResource(StringKey.WalkDetailEditWalkNameTitle)) },
        text = {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { confirm() }),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            IconButton(onClick = { confirm() }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(StringKey.WalkDetailConfirmEditWalkNameContentDescription),
                )
            }
        },
    )
}
