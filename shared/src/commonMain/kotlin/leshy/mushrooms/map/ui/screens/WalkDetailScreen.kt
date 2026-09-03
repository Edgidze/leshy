package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.FieldMark
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.mushroomsUnitLabel
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.archive.CategoryCount
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel
import leshy.mushrooms.map.ui.components.AddPlaceDialog
import leshy.mushrooms.map.ui.components.DeletePlaceConfirmDialog
import leshy.mushrooms.map.ui.components.MUSHROOM_PHOTO_ASPECT_RATIO
import leshy.mushrooms.map.ui.components.MushroomDonutChart
import leshy.mushrooms.map.ui.components.MushroomPhoto
import leshy.mushrooms.map.ui.components.PlaceViewDialog
import leshy.mushrooms.map.ui.components.WalkRouteThumbnail
import leshy.mushrooms.map.ui.components.WalkShareDialog
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationLabeled
import leshy.mushrooms.map.ui.util.formatSpeedKmh
import leshy.mushrooms.map.ui.util.formatTimeOnly
import leshy.mushrooms.map.ui.util.parseHexColor
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
 * Высота карты-заставки. Не квадрат, хотя снимок квадратный: экран открывается сверху, и заставка
 * во весь квадрат съедала бы всё первое, что видно, не оставляя места ни названию, ни показателям.
 * Снимок вписывается по ширине и подрезается сверху и снизу ([ContentScale.Crop]) — маршрут в
 * снимке всегда с полями (`SNAPSHOT_PADDING_PX`), так что подрезка забирает поля, а не трек.
 */
private val HERO_HEIGHT = 200.dp
private val HERO_CORNER_RADIUS = 16.dp

/** Значки показателей — те же три и того же размера, что в шапке «Записи» (`RecordScreen.kt`). */
private val METRIC_ICON_SIZE = 28.dp

/**
 * Высота карточки показателя при системном масштабе шрифта — значок, отбивка, две строки
 * `titleLarge` и собственные поля. Задана снизу, а не выведена из содержимого: значения переносятся
 * каждое по своей нужде («24» — одна строка, «12.34 км» — две), и три карточки натуральной высоты
 * встали бы в ряд ступенькой. `IntrinsicSize` эту работу не делает: минимальная внутренняя высота
 * текста меряется по бесконечной ширине, то есть по одной строке, и двухстрочному значению её не
 * хватило бы. Раз при `maxLines = 2` содержимое выше этого числа не бывает, минимум оказывается и
 * максимумом — карточки выходят равными без общей высоты у ряда.
 */
private val METRIC_CARD_MIN_HEIGHT = 116.dp

/**
 * Сколько плиток находок встаёт в ряд. Ширина плитки считается от этого числа, не наоборот.
 *
 * Две, а не три. Плитка собрана из [MushroomPhoto], а у той подпись с названием вида лежит поверх
 * картинки блоком постоянной высоты (54dp, две строки по 20sp — размер выбран под ленту «Записи»,
 * где плитка шириной 120dp). При трёх колонках плитке достаётся 90–104dp, картинка становится
 * 72–83dp высотой, и подпись съедает три четверти её высоты. При двух колонках плитка выходит
 * 140–160dp, то есть не уже той, под которую подпись и рисовалась.
 */
private const val FIND_TILE_COLUMNS = 2
private val FIND_TILE_SPACING = 8.dp
private val FIND_TILE_COUNT_ROW_HEIGHT = 28.dp

private val PLACE_THUMBNAIL_SIZE = 64.dp
private val SECTION_TOP_GAP = 24.dp

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
    var isEditingPlace by remember { mutableStateOf(false) }
    var confirmDeletePlace by remember { mutableStateOf(false) }
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

    if (selectedPlace != null) {
        if (isEditingPlace) {
            AddPlaceDialog(
                location = GeoPoint(selectedPlace.lat, selectedPlace.lon, null, selectedPlace.timestamp),
                initialName = selectedPlace.name,
                initialDescription = selectedPlace.description.orEmpty(),
                initialPhotoPath = selectedPlace.photoPath,
                onSave = { name, description, photoPath ->
                    viewModel.updatePlace(selectedPlace, name, description, photoPath)
                    isEditingPlace = false
                },
                onDismissRequest = { isEditingPlace = false },
            )
        } else {
            PlaceViewDialog(
                mark = selectedPlace,
                onEditClick = { isEditingPlace = true },
                onDeleteClick = { confirmDeletePlace = true },
                onDismissRequest = { selectedPlaceId = null },
            )
        }
    }

    if (confirmDeletePlace && selectedPlace != null) {
        DeletePlaceConfirmDialog(
            onConfirm = {
                viewModel.deletePlace(selectedPlace)
                confirmDeletePlace = false
                selectedPlaceId = null
            },
            onDismissRequest = { confirmDeletePlace = false },
        )
    }

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
 */
@Composable
private fun WalkHero(walk: Walk, track: List<GeoPoint>, findLocations: List<GeoPoint>, onClick: () -> Unit) {
    var loadFailed by remember(walk.thumbnailPath) { mutableStateOf(false) }
    val thumbnailPath = walk.thumbnailPath

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HERO_HEIGHT)
            .clip(RoundedCornerShape(HERO_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
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
 * [value] переносится на вторую строку, а не ужимается и не обрезается: «4 ч 18 мин» и «12.34 км»
 * в блок шириной около 100dp одной строкой не помещаются ни при каком кегле, который ещё читается
 * с вытянутой руки, — а этот экран смотрят в том числе в лесу.
 */
@Composable
private fun MetricCard(icon: Painter, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.heightIn(min = METRIC_CARD_MIN_HEIGHT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(painter = icon, contentDescription = label, modifier = Modifier.size(METRIC_ICON_SIZE))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
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

/**
 * Заголовок раздела типографикой, а не подчёркиванием. Подчёркнутый текст в мобильном интерфейсе
 * читается как ссылка — прежние заголовки этого экрана были подчёркнуты и обещали нажатие,
 * которого не было.
 */
@Composable
private fun SectionHeader(title: String, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = SECTION_TOP_GAP, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        action?.invoke()
    }
}

/**
 * Находки плитками с иллюстрацией, названием и числом — вместо прежнего списка строк
 * «название — число». Плитка построена из тех же частей, что плитка ленты «Записи»
 * ([MushroomPhoto] под строкой счётчика, обводка цветом вида), чтобы вид, отмеченный в лесу,
 * выглядел в архиве так же, как выглядел в момент отметки.
 *
 * Ширина плитки считается от ширины экрана, а не задана числом: [FIND_TILE_COLUMNS] плиток обязаны
 * ровно закрывать ряд, иначе на узких экранах в ряд встаёт две и треть ширины уходит в пустоту.
 */
@Composable
private fun FindTilesGrid(counts: List<CategoryCount>) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val tileWidth = (maxWidth - FIND_TILE_SPACING * (FIND_TILE_COLUMNS - 1)) / FIND_TILE_COLUMNS
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FIND_TILE_SPACING),
            verticalArrangement = Arrangement.spacedBy(FIND_TILE_SPACING),
        ) {
            counts.forEach { entry ->
                FindTile(category = entry.category, count = entry.count, width = tileWidth)
            }
        }
    }
}

@Composable
private fun FindTile(category: Category, count: Int, width: Dp) {
    Card(
        modifier = Modifier.width(width),
        border = BorderStroke(2.dp, parseHexColor(category.colorHex)),
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(FIND_TILE_COUNT_ROW_HEIGHT),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
            MushroomPhoto(
                category = category,
                modifier = Modifier.fillMaxWidth().aspectRatio(MUSHROOM_PHOTO_ASPECT_RATIO),
            )
        }
    }
}

/**
 * Пустых находок у прогулки быть не запрещено — вышел, походил, не нашёл. Раньше про это говорил
 * подчёркнутый заголовок «Находок не зафиксировано» на месте списка; теперь это отдельный
 * приглушённый блок, который не притворяется разделом с содержимым.
 */
@Composable
private fun FindsEmptyBlock() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = SECTION_TOP_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(METRIC_ICON_SIZE),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(StringKey.WalkDetailFindsEmpty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
        modifier = Modifier.fillMaxWidth(0.9f),
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
