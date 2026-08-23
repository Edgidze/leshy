package compose.project.leshy.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MAX_MUSHROOM_FINDS_PER_WALK
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.domain.model.iconSource
import compose.project.leshy.domain.util.TurnDirection
import compose.project.leshy.i18n.LocalAppLanguage
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.record.RecordUiState
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.presentation.searchOrderedCategories
import compose.project.leshy.presentation.record.NavigationOverlayState
import compose.project.leshy.ui.components.AddPlaceDialog
import compose.project.leshy.ui.components.AddSpeciesTile
import compose.project.leshy.ui.components.DeletePlaceConfirmDialog
import compose.project.leshy.ui.components.MapFilterButton
import compose.project.leshy.ui.components.MapFilterDialog
import compose.project.leshy.ui.components.MushroomPhoto
import compose.project.leshy.ui.components.MushroomTile
import compose.project.leshy.ui.components.NavigationOverlayPanel
import compose.project.leshy.ui.components.PlaceViewDialog
import compose.project.leshy.ui.components.RECORD_MUSHROOM_TILE_WIDTH
import compose.project.leshy.ui.components.SpeciesFormDialog
import compose.project.leshy.ui.map.LiveTrackMap
import compose.project.leshy.ui.map.MapMarker
import compose.project.leshy.ui.map.PlaceMarker
import compose.project.leshy.ui.theme.LeshyTheme
import compose.project.leshy.ui.util.formatDateOnly
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDuration
import compose.project.leshy.ui.util.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

private val ACTION_BUTTON_HEIGHT = 56.dp
private val ACTION_BUTTON_SHAPE = RoundedCornerShape(20.dp)
private val TILE_WIDTH = RECORD_MUSHROOM_TILE_WIDTH

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

    RecordScreenContent(
        uiState = uiState,
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

/**
 * Pure presentation layer, no [RecordViewModel]/DI dependency — kept separate so it can be driven
 * by hand-built [RecordUiState] samples in [@Preview][Preview] functions below without a Koin
 * graph or platform camera/GPS plumbing.
 */
@Composable
private fun RecordScreenContent(
    uiState: RecordUiState,
    onStartWalk: (String) -> Unit,
    onPauseOrResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    onAddMushroom: (Long) -> Unit,
    onRemoveMushroom: (Long) -> Unit,
    onFilterClick: () -> Unit,
    onAddMushrooms: (Long, Int) -> Unit = { _, _ -> },
    onSaveSpecies: (String, String?, EdibilityStatus, String, ByteArray?) -> Unit = { _, _, _, _, _ -> },
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
    val categoryById = uiState.categories.associateBy { it.id }
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

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatDuration(uiState.elapsedMillis), style = MaterialTheme.typography.titleLarge)
            Text(formatDistanceKm(uiState.distanceMeters), style = MaterialTheme.typography.titleLarge)
        }

        // Current walk's own POI marks plus past walks' ones, deduped — see LiveTrackMap's
        // historicalPlaces param doc for why the dedup matters.
        val currentPlaceMarks = uiState.marks.filter { it.type == MarkType.POI }
        val dedupedHistoricalPlaces = uiState.historicalPlaces
            .filterNot { historical -> uiState.marks.any { it.id == historical.id } }

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
                    markers = uiState.marks.filter { it.type != MarkType.POI }.map { mark ->
                        val category = categoryById[mark.categoryId]
                        MapMarker(
                            lat = mark.lat,
                            lon = mark.lon,
                            colorHex = category?.colorHex ?: "#808080",
                            icon = category?.iconSource(),
                        )
                    },
                    historicalMarkers = uiState.historicalFinds.map { mark ->
                        val category = categoryById[mark.categoryId]
                        MapMarker(
                            lat = mark.lat,
                            lon = mark.lon,
                            colorHex = category?.colorHex ?: "#808080",
                            icon = category?.iconSource(),
                        )
                    },
                    places = currentPlaceMarks.map { mark ->
                        PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath)
                    },
                    onPlaceClick = onPlaceClick,
                    // Excludes the current walk's own places (already shown above, interactive) —
                    // unlike historicalMarkers/historicalFinds, a duplicate here would mean two
                    // literal SymbolLayers stacked on the exact same pin, and whichever one MapLibre
                    // hit-tests first would silently swallow taps meant for the interactive layer.
                    historicalPlaces = dedupedHistoricalPlaces
                        .map { mark -> PlaceMarker(id = mark.id, lat = mark.lat, lon = mark.lon, photoPath = mark.photoPath) },
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
 * Opened by holding a [MushroomTile]'s + button for 2s — equivalent to tapping + [count] times
 * for [category] from the last known location, without [count] individual taps. The field forces
 * [KeyboardType.NumberPassword] (not the plain [KeyboardType.Number]) specifically so the keyboard
 * that pops up is a bare digit pad on BOTH platforms — regular `Number` still offers a decimal
 * separator/other punctuation whose exact glyphs depend on the OS locale, which a find count never
 * needs. Confirming is the field's own IME "Done" key, not a dialog button — there is deliberately
 * no separate confirm affordance, only [onDismissRequest]'s cancel arrow top-left.
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
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(StringKey.RecordBulkAddCancelContentDescription),
                    )
                }
                MushroomPhoto(category = category, modifier = Modifier.fillMaxWidth().aspectRatio(1.5f))
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
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, parseHexColor(category.colorHex), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
    ) {
        MushroomPhoto(category = category, modifier = Modifier.fillMaxSize())
    }
}

// A handful of real catalog entries (see EnsureDefaultCategoriesUseCase) — enough variety
// (both EdibilityStatus buckets, a mix of icons) to explore MushroomTile without seeding
// the actual default list.
private val PREVIEW_CATEGORIES = listOf(
    Category(1, "category_boletus_edulis", "#A95620", "boletus_edulis", 0, true, EdibilityStatus.NOT_SPECIFIED),
    Category(2, "category_pleurotus_ostreatus", "#BBAA93", "pleurotus_ostreatus", 1, true, EdibilityStatus.NOT_SPECIFIED),
    Category(3, "category_lactarius_torminosus", "#D69CA0", "lactarius_torminosus", 2, true, EdibilityStatus.NOT_SPECIFIED),
    Category(4, "category_amanita_muscaria", "#D73B21", "amanita_muscaria", 3, true, EdibilityStatus.POISONOUS),
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
                elapsedMillis = 125_000L,
                distanceMeters = 1240.0,
                mushroomCounts = mapOf(1L to 2, 3L to 1),
            ),
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
                elapsedMillis = 125_000L,
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
                elapsedMillis = 125_000L,
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
                elapsedMillis = 754_000L,
                distanceMeters = 3120.0,
                mushroomCounts = mapOf(1L to 4),
            ),
            onStartWalk = PREVIEW_NOOP_STRING,
            onPauseOrResumeClick = PREVIEW_NOOP,
            onFinishClick = PREVIEW_NOOP,
            onAddMushroom = PREVIEW_NOOP_LONG,
            onRemoveMushroom = PREVIEW_NOOP_LONG,
            onFilterClick = PREVIEW_NOOP,
        )
    }
}
