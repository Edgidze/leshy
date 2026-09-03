package leshy.mushrooms.map.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.data.platform.currentTimeMillis
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.MAX_MUSHROOM_FINDS_PER_WALK
import leshy.mushrooms.map.domain.model.MarkType
import leshy.mushrooms.map.domain.model.iconSource
import leshy.mushrooms.map.domain.util.TurnDirection
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.i18n.mushroomsUnitLabel
import leshy.mushrooms.map.presentation.record.RecordUiState
import leshy.mushrooms.map.presentation.record.RecordViewModel
import leshy.mushrooms.map.presentation.searchOrderedCategories
import leshy.mushrooms.map.presentation.record.NavigationOverlayState
import leshy.mushrooms.map.ui.components.AddPlaceDialog
import leshy.mushrooms.map.ui.components.AddSpeciesTile
import leshy.mushrooms.map.ui.components.DeletePlaceConfirmDialog
import leshy.mushrooms.map.ui.components.MapFilterButton
import leshy.mushrooms.map.ui.components.MapFilterDialog
import leshy.mushrooms.map.ui.components.MUSHROOM_PHOTO_ASPECT_RATIO
import leshy.mushrooms.map.ui.components.MushroomPhoto
import leshy.mushrooms.map.ui.components.MushroomTile
import leshy.mushrooms.map.ui.components.NavigationOverlayPanel
import leshy.mushrooms.map.ui.components.PlaceViewDialog
import leshy.mushrooms.map.ui.components.RECORD_MUSHROOM_TILE_WIDTH
import leshy.mushrooms.map.ui.components.SpeciesFormDialog
import leshy.mushrooms.map.ui.map.LiveTrackMap
import leshy.mushrooms.map.ui.map.MapMarker
import leshy.mushrooms.map.ui.map.PlaceMarker
import leshy.mushrooms.map.ui.theme.LeshyTheme
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import leshy.shared.generated.resources.ic_route
import leshy.shared.generated.resources.ic_stopwatch
import org.jetbrains.compose.resources.painterResource
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDistanceKmValue
import leshy.mushrooms.map.ui.util.formatDuration
import leshy.mushrooms.map.ui.util.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

private val ACTION_BUTTON_HEIGHT = 56.dp
private val ACTION_BUTTON_SHAPE = RoundedCornerShape(20.dp)
private val TILE_WIDTH = RECORD_MUSHROOM_TILE_WIDTH

/**
 * Значки шапки. Крупнее строчных букв рядом намеренно: значения набраны `titleLarge` (22sp), у
 * которого высота прописной около 16dp, и значок вровень с ними читался мелким довеском к цифрам,
 * а не парой к ним. Картинки заполняют своё поле целиком (`tools/prepare_icon_assets.py`), поэтому
 * это число и есть видимая высота значка, без скрытых полей внутри файла.
 */
private val STAT_ICON_SIZE = 30.dp
private val STAT_ICON_GAP = 6.dp

/** Поля строки показателей. Уже прежних 20.dp: три показателя вместо двух, и запас по ширине
 * нужнее полей — сама строка ничем не отделена от карты под ней, поэтому воздух ей дают отступы
 * сверху и снизу, а не по бокам. */
private val STAT_ROW_PADDING = 12.dp

/**
 * Ширина строки показателей, начиная с которой у километража остаётся видимая подпись «км».
 * Записана как «экран 360dp минус собственные поля строки», потому что порог осмыслен именно в
 * ширине устройства, а сравнивается с шириной содержимого: `padding` стоит снаружи
 * `BoxWithConstraints` и из его `maxWidth` уже вычтен.
 *
 * Почему порог, а не замер по факту: чтобы узнать, помещается ли всё, надо померить самый широкий
 * из трёх показателей — время, — а его чтение обязано оставаться внутри [ElapsedTimeText]
 * (см. его док). Замер в родителе вернул бы посекундную инвалидацию шапки, ровно ту, ради
 * устранения которой время и вынесено в отдельный `StateFlow`.
 *
 * Умножается на `fontScale`: перекрытие определяется шириной текста, а она растёт вместе с
 * системным размером шрифта, тогда как ширина экрана — нет.
 */
private val STAT_ROW_UNIT_LABEL_MIN_WIDTH = 360.dp - STAT_ROW_PADDING * 2

// Gap between tiles in the feed's LazyRow — also fed into the pixel-distance math for the
// slow scroll-to-front below, so keep the two in sync if this ever changes.
private val TILE_SPACING = 8.dp

// Start/Pause pill's preferred width — shrunk on narrow screens (see CENTER_BUTTON_MIN_WIDTH)
// so the round side buttons always get their full ACTION_BUTTON_HEIGHT slot and never compress.
private val CENTER_BUTTON_MAX_WIDTH = 200.dp
private val CENTER_BUTTON_MIN_WIDTH = 130.dp
private val ROW_HORIZONTAL_PADDING = 16.dp
private val SIDE_BUTTON_SLOT_WIDTH = 64.dp

@Composable
fun RecordScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    // Collected without `by` on purpose: the snapshot read has to happen inside the callee's own
    // restart scope, not here — reading it in this composable would put the once-a-second
    // invalidation straight back onto the whole screen. See RecordViewModel.elapsedMillis.
    val elapsedMillisState = viewModel.elapsedMillis.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var selectedPlaceId by remember { mutableStateOf<Long?>(null) }
    var isEditingPlace by remember { mutableStateOf(false) }
    var confirmDeletePlace by remember { mutableStateOf(false) }
    val selectedPlace = uiState.marks.find { it.id == selectedPlaceId }
        ?: uiState.historicalPlaces.find { it.id == selectedPlaceId }

    LaunchedEffect(uiState.justFinished) {
        if (uiState.justFinished) {
            onFinished()
            viewModel.consumeFinished()
        }
    }

    // GPS is only subscribed to while this screen is actually in front of the user — the
    // ViewModel outlives the composable (it is scoped to the Record back-stack entry, see
    // presentation/CLAUDE.md), so without this the location collector kept running on every other
    // section. A walk in progress is unaffected: the ViewModel keeps GPS alive on isRecording
    // regardless of this flag, which is what makes background recording work.
    LifecycleResumeEffect(viewModel) {
        viewModel.onRecordScreenResumed()
        onPauseOrDispose { viewModel.onRecordScreenPaused() }
    }

    RecordScreenContent(
        uiState = uiState,
        elapsedMillis = { elapsedMillisState.value },
        onStartWalk = { name ->
            viewModel.setWalkName(name)
            viewModel.onStartOrPauseClick()
        },
        onPauseOrResumeClick = viewModel::onStartOrPauseClick,
        onFinishClick = viewModel::finish,
        onAddMushroom = viewModel::addMushroom,
        onAddMushrooms = viewModel::addMushrooms,
        onRemoveMushroom = viewModel::removeMushroom,
        onSaveSpecies = viewModel::saveNewSpecies,
        onFilterClick = { showFilterDialog = true },
        onMarkLocationClick = { showAddPlaceDialog = true },
        onSearchClick = { showSearchDialog = true },
        onPlaceClick = { id -> selectedPlaceId = id },
        onMarkerLongPressed = viewModel::activateNavigationTo,
        onCloseNavigation = viewModel::deactivateNavigation,
        onTileFeedInteraction = viewModel::notifyTileFeedInteraction,
        modifier = modifier,
    )

    if (showFilterDialog) {
        MapFilterDialog(onDismissRequest = { showFilterDialog = false })
    }

    if (showAddPlaceDialog) {
        AddPlaceDialog(
            location = uiState.currentLocation,
            onSave = viewModel::addPlace,
            onDismissRequest = { showAddPlaceDialog = false },
        )
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

    if (showSearchDialog) {
        MushroomSearchDialog(
            categories = uiState.categories,
            onSelect = { categoryId ->
                viewModel.bringCategoryToFront(categoryId)
                showSearchDialog = false
            },
            onDismissRequest = { showSearchDialog = false },
        )
    }
}

/** Значок показателя вместе с отбивкой до значения — три места в строке, одинаковые до знака. */
@Composable
private fun StatIcon(icon: Painter, contentDescription: String) {
    Icon(painter = icon, contentDescription = contentDescription, modifier = Modifier.size(STAT_ICON_SIZE))
    Spacer(modifier = Modifier.width(STAT_ICON_GAP))
}

/**
 * Its own composable — and therefore its own restart scope — reading [elapsedMillis] through a
 * lambda so that the snapshot read lands in *this* scope. `Row`/`Column` are inline, so calling it
 * directly in [RecordScreenContent]'s body would hoist the read back up and invalidate the whole
 * screen once a second, which is exactly what the split exists to prevent. See
 * [RecordViewModel.elapsedMillis].
 */
@Composable
private fun ElapsedTimeText(elapsedMillis: () -> Long) {
    Text(formatDuration(elapsedMillis()), style = MaterialTheme.typography.titleLarge)
}

/**
 * Pure presentation layer, no [RecordViewModel]/DI dependency — kept separate so it can be driven
 * by hand-built [RecordUiState] samples in [@Preview][Preview] functions below without a Koin
 * graph or platform camera/GPS plumbing.
 */
@Composable
private fun RecordScreenContent(
    uiState: RecordUiState,
    /** Deferred read — see [ElapsedTimeText]; never a plain `Long` parameter. */
    elapsedMillis: () -> Long,
    onStartWalk: (String) -> Unit,
    onPauseOrResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    onAddMushroom: (Long) -> Unit,
    onRemoveMushroom: (Long) -> Unit,
    onFilterClick: () -> Unit,
    onAddMushrooms: (Long, Int) -> Unit = { _, _ -> },
    onSaveSpecies: (String, String?, String, ByteArray?) -> Unit = { _, _, _, _ -> },
    onMarkLocationClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onPlaceClick: (Long) -> Unit = {},
    onMarkerLongPressed: (Long) -> Unit = {},
    onCloseNavigation: () -> Unit = {},
    onTileFeedInteraction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var bulkAddCategoryId by remember { mutableStateOf<Long?>(null) }
    var showAddSpeciesDialog by remember { mutableStateOf(false) }
    val categoryById = remember(uiState.categories) { uiState.categories.associateBy { it.id } }
    val tileListState = rememberLazyListState()

    // Measured (not hardcoded) so the tile-load-failed banner clears the Start/Pause pill + tile
    // scroller regardless of their actual height (system font scale, narrow-screen pill shrinking).
    val density = LocalDensity.current
    var bottomControlsHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(uiState.scrollToStartSignal) {
        if (uiState.scrollToStartSignal == 0) return@LaunchedEffect
        val slowDurationMillis = uiState.scrollToStartDurationMillis
        if (slowDurationMillis == null) {
            // Deliberate jump-to-tile (search-dialog selection, new-species creation) — snap to
            // the front at the feed's usual scroll speed, no need to draw it out.
            tileListState.animateScrollToItem(0)
        } else {
            // A settled +/- reorder — scroll to the front slowly over slowDurationMillis so the
            // motion is actually observable instead of reading as a teleport (see
            // RecordUiState.scrollToStartDurationMillis). All tiles share TILE_WIDTH, so the pixel
            // distance to the front can be computed directly instead of needing off-screen items
            // to already be laid out.
            val tileExtentPx = with(density) { (TILE_WIDTH + TILE_SPACING).toPx() }
            val distancePx = tileListState.firstVisibleItemIndex * tileExtentPx +
                tileListState.firstVisibleItemScrollOffset
            if (distancePx > 0f) {
                tileListState.animateScrollBy(-distancePx, tween(slowDurationMillis))
            }
        }
    }

    // Manual dragging of the feed counts as activity for RecordViewModel's reorder quiet window,
    // same as a +/- tap — a no-op there while nothing is pending, so this also safely fires for
    // the animateScrollToItem(0) call above without re-arming anything.
    LaunchedEffect(tileListState) {
        snapshotFlow { tileListState.isScrollInProgress }.collect { inProgress ->
            if (inProgress) onTileFeedInteraction()
        }
    }

    // Суммируется здесь, а не в RecordViewModel: mushroomCounts уже лежит в состоянии и читается
    // на этом же экране лентой плиток, так что новое поле в UiState только дублировало бы источник
    // истины. remember на самой карте — пересчёт нужен при отметке находки, а не при каждом
    // GPS-фиксе, которых за прогулку на порядки больше.
    val totalMushroomCount = remember(uiState.mushroomCounts) { uiState.mushroomCounts.values.sum() }

    Column(modifier = modifier.fillMaxSize()) {
        // Три показателя, находки — посередине. Раньше их тут не было вовсе: шапка показывала
        // время и километраж, то есть ровно метрики бегового трекера, тогда как VISION.md первым
        // же абзацем говорит, что трек и километраж — контекст, а ценность — история находок.
        // Середина строки — сильнейшая позиция из трёх, туда счётчик и встал.
        // Три показателя: время слева, находки по центру, километраж справа.
        //
        // Box с тремя независимо выровненными детьми, а не Row. Row перепробован в двух видах, и
        // оба проигрывают. `Arrangement.SpaceBetween` раздаёт промежутки между элементами
        // натуральной ширины — середина при этом попадает в центр ЭКРАНА только если крайние
        // показатели равной ширины, а они не равны почти никогда: слева «1:23:45», справа
        // «12.34 км». Счётчик находок, ради позиции которого всё и затевалось, уезжал бы то влево,
        // то вправо и подрагивал на каждой смене разрядности времени. Равные трети чинят центр, но
        // режут ширину: каждому показателю достаётся треть строки независимо от того, сколько ему
        // нужно, и на узком экране с крупным системным шрифтом у километража отрезало «км»
        // (проверено на 360dp при масштабе шрифта 1.3).
        //
        // Box снимает оба: середина выровнена по центру всей строки, а каждый показатель меряется
        // по своему содержимому и ничего не теряет. Взамен появляется своя цена: Box ничего не
        // ужимает и не переносит, поэтому при нехватке ширины дети просто накладываются друг на
        // друга. Здесь было написано, что до этого «уже за пределами» рабочих размеров, — неверно,
        // 320dp iPhone SE перекрытие даёт (скриншот владельца, 3 сентября). Чем это лечится и чего
        // это лечение не покрывает — в комментарии внутри и у STAT_ROW_UNIT_LABEL_MIN_WIDTH.
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().padding(horizontal = STAT_ROW_PADDING, vertical = 12.dp),
        ) {
            // Проверено на устройстве владельца (iPhone SE, 320dp): при трёхзначном счётчике
            // находок правый показатель наезжает на середину ещё до двузначного километража —
            // «122» и «0.05 km» смыкаются. Оценка сходится: содержимому остаётся 296dp, середина
            // забирает ~73, на каждый край приходится ~112, а километраж с подписью просит ~111.
            // Подпись снимается только на узких экранах: на широких она нужна, потому что «0.05»
            // само по себе ни о чём не говорит — в отличие от тикающего времени слева, чей смысл
            // виден из того, что оно тикает.
            //
            // Чего это НЕ чинит: левый край. «1:23:45» просит ~109dp, и при четырёхзначном
            // счётчике находок (края получают по ~106) время наедет на середину так же. Дальше
            // 320dp этим способом не спасти — там придётся снимать что-то ещё.
            val showDistanceUnit = maxWidth >= STAT_ROW_UNIT_LABEL_MIN_WIDTH * LocalDensity.current.fontScale
            // Значок и значение — соседи внутри inline-Row, а не обёрнуты в общий композабл:
            // ElapsedTimeText обязан остаться собственной restart-scope (см. его док), а обёртка
            // втянула бы посекундное чтение в себя вместе со значком.
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatIcon(painterResource(Res.drawable.ic_stopwatch), stringResource(StringKey.WalkDetailDuration))
                ElapsedTimeText(elapsedMillis)
            }
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatIcon(
                    icon = painterResource(Res.drawable.ic_mushrooms),
                    contentDescription = "$totalMushroomCount ${mushroomsUnitLabel(totalMushroomCount)}",
                )
                Text(totalMushroomCount.toString(), style = MaterialTheme.typography.titleLarge)
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatIcon(painterResource(Res.drawable.ic_route), stringResource(StringKey.WalkDetailDistance))
                // Полное значение считается всегда: без подписи оно уходит в contentDescription,
                // чтобы снятие «км» было чисто зрительным и не обедняло чтение вслух.
                val withUnit = formatDistanceKm(uiState.distanceMeters)
                Text(
                    text = if (showDistanceUnit) withUnit else formatDistanceKmValue(uiState.distanceMeters),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    modifier = if (showDistanceUnit) {
                        Modifier
                    } else {
                        Modifier.semantics { contentDescription = withUnit }
                    },
                )
            }
        }

        // A full-width strip rather than an overlay on the map: it must not fight the filter
        // button, the navigation panel or the tile-load banner for the map's corners, and unlike
        // those it is not transient — it stays until the user actually fixes something in the
        // system settings.
        if (uiState.locationUnavailable) {
            Text(
                text = stringResource(StringKey.RecordLocationUnavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        // Current walk's own POI marks plus past walks' ones, deduped — see LiveTrackMap's
        // historicalPlaces param doc for why the dedup matters.
        val currentPlaceMarks = remember(uiState.marks) { uiState.marks.filter { it.type == MarkType.POI } }
        val dedupedHistoricalPlaces = remember(uiState.historicalPlaces, uiState.marks) {
            // Set rather than the nested `any` this used to do: with both lists growing over a
            // long walk that was quadratic, and it ran on every single recomposition.
            val currentIds = uiState.marks.mapTo(mutableSetOf()) { it.id }
            uiState.historicalPlaces.filterNot { it.id in currentIds }
        }

        // Every list below is remembered on its inputs. LiveTrackMap compares its parameters by
        // instance, so rebuilding them each recomposition handed MapLibre fresh-but-equal lists and
        // made it re-diff its layers for unchanged data. Cheap to keep: when nothing changed the
        // keys are the very same instances, so the comparison short-circuits on identity.
        val findMarkers = remember(uiState.marks, categoryById) {
            uiState.marks.filter { it.type != MarkType.POI }.map { mark ->
                val category = categoryById[mark.categoryId]
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = category?.colorHex ?: "#808080",
                    icon = category?.iconSource(),
                )
            }
        }
        val historicalFindMarkers = remember(uiState.historicalFinds, categoryById) {
            uiState.historicalFinds.map { mark ->
                val category = categoryById[mark.categoryId]
                MapMarker(
                    lat = mark.lat,
                    lon = mark.lon,
                    colorHex = category?.colorHex ?: "#808080",
                    icon = category?.iconSource(),
                )
            }
        }
        val placeMarkers = remember(currentPlaceMarks) {
            currentPlaceMarks.map { mark ->
                PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
            }
        }
        val historicalPlaceMarkers = remember(dedupedHistoricalPlaces) {
            dedupedHistoricalPlaces.map { mark ->
                PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (LocalInspectionMode.current) {
                // The real map is a native MapLibre view (SurfaceView/TextureView) that the
                // IDE's static preview renderer can't drive — swap in a placeholder so the rest
                // of the layout (buttons, tile scroller) is still explorable in @Preview.
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                )
            } else {
                LiveTrackMap(
                    track = uiState.trackPoints,
                    markers = findMarkers,
                    historicalMarkers = historicalFindMarkers,
                    historicalTracks = uiState.historicalTracks,
                    places = placeMarkers,
                    onPlaceClick = onPlaceClick,
                    // Excludes the current walk's own places (already shown above, interactive) —
                    // unlike historicalMarkers/historicalFinds, a duplicate here would mean two
                    // literal SymbolLayers stacked on the exact same pin, and whichever one MapLibre
                    // hit-tests first would silently swallow taps meant for the interactive layer.
                    historicalPlaces = historicalPlaceMarkers,
                    // Place markers can't yet be long-pressed on an unstarted walk — same gating as
                    // the "mark location" button.
                    onPlaceLongPress = { id -> if (uiState.isRecording) onMarkerLongPressed(id) },
                    currentLocation = uiState.currentLocation,
                    navigationTargetLat = uiState.navigationTarget?.targetLat,
                    navigationTargetLon = uiState.navigationTarget?.targetLon,
                    modifier = Modifier.fillMaxSize(),
                    bannerAlignment = Alignment.BottomCenter,
                    bannerPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = bottomControlsHeight + 8.dp),
                )
            }

            // 31.dp clears the native scale bar, which now shares this corner (see
            // mapOrnamentOptions) — same rationale as MapScreen.kt (halved gap).
            MapFilterButton(
                filterCount = uiState.filterCount,
                onClick = onFilterClick,
                modifier = Modifier.align(Alignment.TopStart).padding(top = 31.dp, start = 16.dp),
            )

            uiState.navigationTarget?.let { navigationTarget ->
                NavigationOverlayPanel(
                    state = navigationTarget,
                    onCloseClick = onCloseNavigation,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }

            // Buttons float directly over the map (no opaque backing), the tile scroller below
            // them gets one — Column stacks the two without needing to know the scroller's
            // measured height up front.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .onSizeChanged { bottomControlsHeight = with(density) { it.height.toDp() } },
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    // The Start/Pause pill is normally a fixed CENTER_BUTTON_MAX_WIDTH, but on a
                    // narrow screen (e.g. iPhone SE's 320dp) that plus two ACTION_BUTTON_HEIGHT
                    // side buttons doesn't fit — shrinking the pill first keeps each side button's
                    // weighted slot at least SIDE_BUTTON_SLOT_WIDTH, so it's never forced smaller
                    // than its own icon and centered unevenly inside its slot.
                    val centerButtonWidth = (maxWidth - ROW_HORIZONTAL_PADDING * 2 - SIDE_BUTTON_SLOT_WIDTH * 2)
                        .coerceIn(CENTER_BUTTON_MIN_WIDTH, CENTER_BUTTON_MAX_WIDTH)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ROW_HORIZONTAL_PADDING, vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        when {
                            !uiState.isRecording -> {
                                // No walk to attach a place to yet — same dimmed/disabled treatment
                                // as a mushroom tile's minus button before any find is logged.
                                RecordSideButton(
                                    icon = Icons.Filled.AddLocationAlt,
                                    contentDescription = stringResource(StringKey.RecordMarkLocationContentDescription),
                                    onClick = onMarkLocationClick,
                                    enabled = false,
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                )
                                Button(
                                    onClick = { showNameDialog = true },
                                    shape = ACTION_BUTTON_SHAPE,
                                    modifier = Modifier.height(ACTION_BUTTON_HEIGHT).width(centerButtonWidth),
                                ) {
                                    Text(stringResource(StringKey.RecordStart))
                                }
                                RecordSideButton(
                                    icon = Icons.Filled.Search,
                                    contentDescription = stringResource(StringKey.RecordSearchContentDescription),
                                    onClick = onSearchClick,
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                )
                            }
                            !uiState.isPaused -> {
                                RecordSideButton(
                                    icon = Icons.Filled.AddLocationAlt,
                                    contentDescription = stringResource(StringKey.RecordMarkLocationContentDescription),
                                    onClick = onMarkLocationClick,
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                )
                                Button(
                                    onClick = onPauseOrResumeClick,
                                    shape = ACTION_BUTTON_SHAPE,
                                    modifier = Modifier.height(ACTION_BUTTON_HEIGHT).width(centerButtonWidth),
                                ) {
                                    Text(stringResource(StringKey.RecordPause))
                                }
                                RecordSideButton(
                                    icon = Icons.Filled.Search,
                                    contentDescription = stringResource(StringKey.RecordSearchContentDescription),
                                    onClick = onSearchClick,
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                )
                            }
                            else -> {
                                Button(
                                    onClick = onPauseOrResumeClick,
                                    shape = ACTION_BUTTON_SHAPE,
                                    modifier = Modifier.height(ACTION_BUTTON_HEIGHT).weight(1f),
                                ) {
                                    Text(stringResource(StringKey.RecordResume))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = onFinishClick,
                                    shape = ACTION_BUTTON_SHAPE,
                                    modifier = Modifier.height(ACTION_BUTTON_HEIGHT).weight(1f),
                                ) {
                                    Text(stringResource(StringKey.RecordFinish))
                                }
                            }
                        }
                    }
                }

                LazyRow(
                    state = tileListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(TILE_SPACING),
                ) {
                    items(uiState.categories, key = { it.id }) { category ->
                        MushroomTile(
                            category = category,
                            count = uiState.mushroomCounts[category.id] ?: 0,
                            onAdd = { onAddMushroom(category.id) },
                            onRemove = { onRemoveMushroom(category.id) },
                            onBulkAdd = { if (uiState.isRecording) bulkAddCategoryId = category.id },
                            // Only animates when a settled +/- reorder set a slow duration (see
                            // the scrollToStartSignal LaunchedEffect above) — null placementSpec
                            // means no placement animation, preserving the instant reorder that's
                            // deliberate for search-dialog selection / new-species creation.
                            modifier = Modifier.width(TILE_WIDTH)
                                .animateItem(placementSpec = uiState.scrollToStartDurationMillis?.let { tween(it) }),
                        )
                    }
                    item {
                        AddSpeciesTile(
                            onClick = { showAddSpeciesDialog = true },
                            modifier = Modifier.width(TILE_WIDTH),
                        )
                    }
                }
            }
        }
    }

    if (showNameDialog) {
        WalkNameDialog(
            onConfirm = { name ->
                onStartWalk(name)
                showNameDialog = false
            },
            onDismissRequest = { showNameDialog = false },
        )
    }

    val bulkAddCategory = categoryById[bulkAddCategoryId]
    if (bulkAddCategory != null) {
        MushroomBulkAddDialog(
            category = bulkAddCategory,
            currentCount = uiState.mushroomCounts[bulkAddCategory.id] ?: 0,
            onConfirm = { count -> onAddMushrooms(bulkAddCategory.id, count) },
            onDismissRequest = { bulkAddCategoryId = null },
        )
    }

    if (showAddSpeciesDialog) {
        SpeciesFormDialog(
            existing = null,
            language = LocalAppLanguage.current,
            onSave = onSaveSpecies,
            onDismissRequest = { showAddSpeciesDialog = false },
        )
    }
}

/**
 * Small round button flanking the single centered action button (START or Pause) — hidden once
 * paused, when the Resume/Finish pair fills the whole row and would otherwise overlap it.
 */
@Composable
private fun RecordSideButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(ACTION_BUTTON_HEIGHT)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                // 0.38f matches Material3's own disabled-content alpha (IconButtonDefaults) — the
                // icon is set explicitly here instead of inheriting it, so it must be applied by
                // hand to get the same "faded" look the mushroom tiles' minus button gets for free.
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = if (enabled) 1f else 0.38f),
                modifier = Modifier.size(ACTION_BUTTON_HEIGHT / 2),
            )
        }
    }
}

@Composable
private fun WalkNameDialog(onConfirm: (String) -> Unit, onDismissRequest: () -> Unit) {
    val defaultName = "${stringResource(StringKey.RecordDefaultWalkNamePrefix)} ${formatDateOnly(currentTimeMillis())}"
    var nameInput by remember { mutableStateOf(defaultName) }
    var touched by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    fun confirm() {
        focusManager.clearFocus()
        keyboardController?.hide()
        onConfirm(nameInput.ifBlank { defaultName })
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = { Text(stringResource(StringKey.RecordSetWalkNameTitle)) },
        text = {
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    touched = true
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = if (touched) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    },
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { confirm() }),
                modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                    if (focusState.isFocused && !touched) {
                        nameInput = ""
                        touched = true
                    }
                },
            )
        },
        confirmButton = {
            IconButton(onClick = { confirm() }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(StringKey.RecordConfirmWalkNameContentDescription),
                )
            }
        },
    )
}

/**
 * Below this available height (after [Modifier.imePadding] has already subtracted the keyboard),
 * [MushroomBulkAddDialog] shows the photo at [TILE_WIDTH] — the Record feed's tile size — instead
 * of its normal large identification-sized plate. Threshold has headroom over the real minimum
 * (~400dp: back arrow + tile-sized photo + question + text field + padding) so compact mode kicks
 * in a bit before content would actually start clipping, not exactly at the cutoff. Below either
 * size, [verticalScroll] on the content [Column] is still the hard backstop.
 */
private val BULK_ADD_COMPACT_HEIGHT_THRESHOLD = 500.dp

/**
 * Opened by holding a [MushroomTile]'s + button for 2s — equivalent to tapping + [count] times
 * for [category] from the last known location, without [count] individual taps. The field forces
 * [KeyboardType.NumberPassword] (not the plain [KeyboardType.Number]) specifically so the keyboard
 * that pops up is a bare digit pad on BOTH platforms — regular `Number` still offers a decimal
 * separator/other punctuation whose exact glyphs depend on the OS locale, which a find count never
 * needs. Confirming works two ways: the field's own IME "Done" key, and a checkmark [IconButton]
 * next to [onDismissRequest]'s cancel arrow. The checkmark is required, not just a convenience —
 * iOS's numeric keypad (`NumberPassword`/`Number`) has no Done/return key at all, so without it
 * there would be no way to submit a count on iOS. It's disabled while the field isn't a positive
 * number — [confirm] treats an empty/zero field as a dismiss (indistinguishable from tapping the
 * cancel arrow), which would be a confusing thing for a checkmark specifically to do on tap.
 *
 * Input is capped at 3 digits — [MAX_MUSHROOM_FINDS_PER_WALK] is the largest count that could ever
 * be valid, so a longer input could never confirm anyway; this also sidesteps `toIntOrNull()`
 * silently returning `null` (and the dialog no-op'ing with no feedback) on an absurdly long digit
 * string. If [currentCount] plus the entered count would exceed the cap, confirming shows
 * [MushroomBulkAddLimitDialog] on top instead of adding anything — the count field is left as-is
 * underneath so the user can correct it rather than having to retype it.
 */
@Composable
private fun MushroomBulkAddDialog(
    category: Category,
    currentCount: Int,
    onConfirm: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var countInput by remember { mutableStateOf("") }
    var showLimitWarning by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun confirm() {
        val count = countInput.toIntOrNull() ?: 0
        when {
            count <= 0 -> onDismissRequest()
            currentCount + count > MAX_MUSHROOM_FINDS_PER_WALK -> showLimitWarning = true
            else -> {
                onConfirm(count)
                onDismissRequest()
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        // imePadding() here (not just inside the Column) so maxHeight below already excludes the
        // keyboard — that's what compactPhoto and the Surface's height cap both need to react to.
        BoxWithConstraints(modifier = Modifier.fillMaxWidth(0.92f).imePadding()) {
            val compactPhoto = maxHeight < BULK_ADD_COMPACT_HEIGHT_THRESHOLD
            Surface(
                modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 4.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    // Confirm has to live here, not on the field's IME "Done" key alone — iOS's
                    // NumberPassword/numeric keypad has no Done/return key at all, so without this
                    // checkmark there is no way to submit the count on iOS.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(StringKey.RecordBulkAddCancelContentDescription),
                            )
                        }
                        IconButton(onClick = { confirm() }, enabled = countInput.toIntOrNull()?.let { it > 0 } == true) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(StringKey.RecordBulkAddConfirmContentDescription),
                            )
                        }
                    }
                    if (compactPhoto) {
                        // Тесно по высоте — площадка того же размера, что у плиток на «Записи»:
                        // какой гриб добавляем, пользователь уже выбрал (это открывается долгим
                        // нажатием на плитке), крупное опознавательное фото здесь не обязательно.
                        MushroomPhoto(
                            category = category,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(TILE_WIDTH)
                                .aspectRatio(MUSHROOM_PHOTO_ASPECT_RATIO),
                        )
                    } else {
                        // Единственная площадка фото гриба, оставшаяся прямоугольной. Здесь квадрат не
                        // подходит: ширину задаёт сам диалог (92% экрана), поэтому квадратное фото было бы
                        // высотой почти во всю ширину экрана, и на невысоком телефоне поле ввода числа
                        // ушло бы под клавиатуру — а без него диалог бесполезен. Боковые поля картинки тут
                        // не жалко: фото и так крупное, это опознавательный снимок, а не компактная плитка.
                        MushroomPhoto(category = category, modifier = Modifier.fillMaxWidth().aspectRatio(1.5f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(StringKey.RecordBulkAddQuestion),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = countInput,
                        onValueChange = { new -> if (new.all(Char::isDigit) && new.length <= 3) countInput = new },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { confirm() }),
                        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    )
                }
            }
        }
    }

    if (showLimitWarning) {
        MushroomBulkAddLimitDialog(onDismissRequest = { showLimitWarning = false })
    }
}

/** Shown by [MushroomBulkAddDialog] when the entered count would push a species' total past
 * [MAX_MUSHROOM_FINDS_PER_WALK] for the walk — single acknowledgment button, no title, matching
 * the bulk-add dialog it sits on top of, which also has no title. */
@Composable
private fun MushroomBulkAddLimitDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        text = { Text(stringResource(StringKey.RecordBulkAddLimitMessage)) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(StringKey.RecordBulkAddLimitConfirm)) }
        },
    )
}

/**
 * Lets the user jump straight to a mushroom's tile in a long catalog by typing its name, instead
 * of scrolling the feed. Selecting a result just surfaces that tile at the front of the feed (via
 * [RecordViewModel.bringCategoryToFront]) — it does not itself log a find, unlike tapping the
 * tile's own + button back on the record screen.
 */
@Composable
private fun MushroomSearchDialog(
    categories: List<Category>,
    onSelect: (Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val language = LocalAppLanguage.current
    val orderedCategories = remember(categories, query, language) {
        searchOrderedCategories(categories, query, language)
    }
    val resultListState = rememberLazyListState()

    LaunchedEffect(query) {
        resultListState.animateScrollToItem(0)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(StringKey.RecordSearchDialogTitle),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    state = resultListState,
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(orderedCategories, key = { it.id }) { category ->
                        SearchResultTile(
                            category = category,
                            onClick = { onSelect(category.id) },
                            modifier = Modifier.width(TILE_WIDTH),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultTile(category: Category, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            // То же соотношение, что у площадки фото на плитке ленты — под пропорции обрезанных
            // изображений каталога, см. MUSHROOM_PHOTO_ASPECT_RATIO.
            .aspectRatio(MUSHROOM_PHOTO_ASPECT_RATIO)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, parseHexColor(category.colorHex), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        MushroomPhoto(category = category, modifier = Modifier.fillMaxSize())
    }
}

// A handful of real catalog entries (see EnsureDefaultCategoriesUseCase) — enough variety
// (a mix of icons) to explore MushroomTile without seeding the actual default list.
private val PREVIEW_CATEGORIES = listOf(
    Category(1, "category_boletus_edulis", "#A95620", "boletus_edulis", 0, true),
    Category(2, "category_pleurotus_ostreatus", "#BBAA93", "pleurotus_ostreatus", 1, true),
    Category(3, "category_lactarius_torminosus", "#D69CA0", "lactarius_torminosus", 2, true),
    Category(4, "category_amanita_muscaria", "#D73B21", "amanita_muscaria", 3, true),
)

private val PREVIEW_NOOP_STRING: (String) -> Unit = {}
private val PREVIEW_NOOP_LONG: (Long) -> Unit = {}
private val PREVIEW_NOOP: () -> Unit = {}

@Composable
@Preview
private fun RecordScreenStartPreview() {
    LeshyTheme {
        RecordScreenContent(
            uiState = RecordUiState(categories = PREVIEW_CATEGORIES),
            elapsedMillis = { 0L },
            onStartWalk = PREVIEW_NOOP_STRING,
            onPauseOrResumeClick = PREVIEW_NOOP,
            onFinishClick = PREVIEW_NOOP,
            onAddMushroom = PREVIEW_NOOP_LONG,
            onRemoveMushroom = PREVIEW_NOOP_LONG,
            onFilterClick = PREVIEW_NOOP,
        )
    }
}

@Composable
@Preview
private fun RecordScreenRecordingPreview() {
    LeshyTheme {
        RecordScreenContent(
            uiState = RecordUiState(
                categories = PREVIEW_CATEGORIES,
                isRecording = true,
                distanceMeters = 1240.0,
                mushroomCounts = mapOf(1L to 2, 3L to 1),
            ),
            elapsedMillis = { 125_000L },
            onStartWalk = PREVIEW_NOOP_STRING,
            onPauseOrResumeClick = PREVIEW_NOOP,
            onFinishClick = PREVIEW_NOOP,
            onAddMushroom = PREVIEW_NOOP_LONG,
            onRemoveMushroom = PREVIEW_NOOP_LONG,
            onFilterClick = PREVIEW_NOOP,
        )
    }
}

@Composable
@Preview
private fun RecordScreenNavigatingPreview() {
    LeshyTheme {
        RecordScreenContent(
            uiState = RecordUiState(
                categories = PREVIEW_CATEGORIES,
                isRecording = true,
                distanceMeters = 1240.0,
                mushroomCounts = mapOf(1L to 2, 3L to 1),
                navigationTarget = NavigationOverlayState(
                    targetId = 1L,
                    targetName = "Старый пень",
                    targetLat = 55.7522,
                    targetLon = 37.6156,
                    distanceMeters = 87.0,
                    hasArrived = false,
                    turnDirection = TurnDirection.RIGHT,
                    turnDegrees = 42.0,
                ),
            ),
            elapsedMillis = { 125_000L },
            onStartWalk = PREVIEW_NOOP_STRING,
            onPauseOrResumeClick = PREVIEW_NOOP,
            onFinishClick = PREVIEW_NOOP,
            onAddMushroom = PREVIEW_NOOP_LONG,
            onRemoveMushroom = PREVIEW_NOOP_LONG,
            onFilterClick = PREVIEW_NOOP,
        )
    }
}

@Composable
@Preview
private fun RecordScreenArrivedPreview() {
    LeshyTheme {
        RecordScreenContent(
            uiState = RecordUiState(
                categories = PREVIEW_CATEGORIES,
                isRecording = true,
                distanceMeters = 1240.0,
                mushroomCounts = mapOf(1L to 2, 3L to 1),
                navigationTarget = NavigationOverlayState(
                    targetId = 1L,
                    targetName = "Старый пень",
                    targetLat = 55.7522,
                    targetLon = 37.6156,
                    distanceMeters = 8.0,
                    hasArrived = true,
                    turnDirection = TurnDirection.AHEAD,
                    turnDegrees = null,
                ),
            ),
            elapsedMillis = { 125_000L },
            onStartWalk = PREVIEW_NOOP_STRING,
            onPauseOrResumeClick = PREVIEW_NOOP,
            onFinishClick = PREVIEW_NOOP,
            onAddMushroom = PREVIEW_NOOP_LONG,
            onRemoveMushroom = PREVIEW_NOOP_LONG,
            onFilterClick = PREVIEW_NOOP,
        )
    }
}

@Composable
@Preview
private fun RecordScreenPausedPreview() {
    LeshyTheme {
        RecordScreenContent(
            uiState = RecordUiState(
                categories = PREVIEW_CATEGORIES,
                isRecording = true,
                isPaused = true,
                distanceMeters = 3120.0,
                mushroomCounts = mapOf(1L to 4),
            ),
            elapsedMillis = { 754_000L },
            onStartWalk = PREVIEW_NOOP_STRING,
            onPauseOrResumeClick = PREVIEW_NOOP,
            onFinishClick = PREVIEW_NOOP,
            onAddMushroom = PREVIEW_NOOP_LONG,
            onRemoveMushroom = PREVIEW_NOOP_LONG,
            onFilterClick = PREVIEW_NOOP,
        )
    }
}
