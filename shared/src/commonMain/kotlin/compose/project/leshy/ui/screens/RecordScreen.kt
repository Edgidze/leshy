package compose.project.leshy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.project.leshy.data.platform.currentTimeMillis
import compose.project.leshy.data.platform.rememberCameraLauncher
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.EdibilityStatus
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.record.RecordUiState
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.ui.components.CameraTile
import compose.project.leshy.ui.components.MushroomTile
import compose.project.leshy.ui.map.LiveTrackMap
import compose.project.leshy.ui.map.MapMarker
import compose.project.leshy.ui.theme.LeshyTheme
import compose.project.leshy.ui.util.formatDateOnly
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDuration
import org.koin.compose.viewmodel.koinViewModel

private val ACTION_BUTTON_HEIGHT = 56.dp
private val ACTION_BUTTON_SHAPE = RoundedCornerShape(20.dp)
private val TILE_WIDTH = 112.dp

@Composable
fun RecordScreen(onFinished: () -> Unit, viewModel: RecordViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val takePhoto = rememberCameraLauncher { path -> viewModel.onPhotoCaptured(path) }

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
        onRemoveMushroom = viewModel::removeMushroom,
        onPhotoClick = takePhoto,
    )
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
    onPhotoClick: () -> Unit,
) {
    var showNameDialog by remember { mutableStateOf(false) }
    val categoryById = uiState.categories.associateBy { it.id }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatDuration(uiState.elapsedMillis), style = MaterialTheme.typography.titleLarge)
            Text(formatDistanceKm(uiState.distanceMeters), style = MaterialTheme.typography.titleLarge)
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
                    markers = uiState.marks.map { mark ->
                        val category = categoryById[mark.categoryId]
                        MapMarker(
                            lat = mark.lat,
                            lon = mark.lon,
                            colorHex = category?.colorHex ?: "#808080",
                            iconRef = category?.iconRef,
                        )
                    },
                    currentLocation = uiState.currentLocation,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Buttons float directly over the map (no opaque backing), the tile scroller below
            // them gets one — Column stacks the two without needing to know the scroller's
            // measured height up front.
            Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    when {
                        !uiState.isRecording -> {
                            Button(
                                onClick = { showNameDialog = true },
                                shape = ACTION_BUTTON_SHAPE,
                                modifier = Modifier.height(ACTION_BUTTON_HEIGHT).width(200.dp),
                            ) {
                                Text(stringResource(StringKey.RecordStart))
                            }
                        }
                        !uiState.isPaused -> {
                            Button(
                                onClick = onPauseOrResumeClick,
                                shape = ACTION_BUTTON_SHAPE,
                                modifier = Modifier.height(ACTION_BUTTON_HEIGHT).width(200.dp),
                            ) {
                                Text(stringResource(StringKey.RecordPause))
                            }
                        }
                        else -> {
                            Button(
                                onClick = onPauseOrResumeClick,
                                shape = ACTION_BUTTON_SHAPE,
                                modifier = Modifier.height(ACTION_BUTTON_HEIGHT).width(150.dp),
                            ) {
                                Text(stringResource(StringKey.RecordResume))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = onFinishClick,
                                shape = ACTION_BUTTON_SHAPE,
                                modifier = Modifier.height(ACTION_BUTTON_HEIGHT).width(150.dp),
                            ) {
                                Text(stringResource(StringKey.RecordFinish))
                            }
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.categories, key = { it.id }) { category ->
                        MushroomTile(
                            category = category,
                            count = uiState.mushroomCounts[category.id] ?: 0,
                            onAdd = { onAddMushroom(category.id) },
                            onRemove = { onRemoveMushroom(category.id) },
                            modifier = Modifier.width(TILE_WIDTH),
                        )
                    }
                    item {
                        CameraTile(onClick = onPhotoClick, modifier = Modifier.width(TILE_WIDTH))
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

// A handful of real catalog entries (see EnsureDefaultCategoriesUseCase) — enough variety
// (all three EdibilityStatus buckets, a mix of icons) to explore MushroomTile without seeding
// the actual default list.
private val PREVIEW_CATEGORIES = listOf(
    Category(1, "category_boletus_edulis", "#A95620", "boletus_edulis", 0, true, EdibilityStatus.EDIBLE),
    Category(2, "category_pleurotus_ostreatus", "#BBAA93", "pleurotus_ostreatus", 1, true, EdibilityStatus.EDIBLE),
    Category(3, "category_lactarius_torminosus", "#D69CA0", "lactarius_torminosus", 2, true, EdibilityStatus.CONDITIONALLY_EDIBLE),
    Category(4, "category_amanita_muscaria", "#D73B21", "amanita_muscaria", 3, true, EdibilityStatus.INEDIBLE),
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
            onPhotoClick = PREVIEW_NOOP,
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
            onPhotoClick = PREVIEW_NOOP,
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
            onPhotoClick = PREVIEW_NOOP,
        )
    }
}
