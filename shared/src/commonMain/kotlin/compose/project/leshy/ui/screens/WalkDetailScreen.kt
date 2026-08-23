package compose.project.leshy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.domain.model.MarkType
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.archive.WalkDetailViewModel
import compose.project.leshy.ui.components.AddPlaceDialog
import compose.project.leshy.ui.components.DeletePlaceConfirmDialog
import compose.project.leshy.ui.components.MushroomDonutChart
import compose.project.leshy.ui.components.PlaceViewDialog
import compose.project.leshy.ui.components.WalkShareDialog
import compose.project.leshy.ui.util.formatDateTime
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDurationLabeled
import compose.project.leshy.ui.util.formatSpeedKmh
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PLACE_THUMBNAIL_SIZE = 48.dp

private val MUSHROOM_TOAST_DURATION = 3000.milliseconds

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
            TopAppBar(
                title = {
                    Text(
                        walk?.name.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showShareDialog = true }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = stringResource(StringKey.WalkShareContentDescription),
                        )
                    }
                    IconButton(onClick = viewModel::onEditClick) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(StringKey.WalkDetailEditContentDescription),
                        )
                    }
                    IconButton(onClick = viewModel::onDeleteClick) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(StringKey.WalkDetailDeleteContentDescription),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (walk == null) return@Scaffold

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${stringResource(StringKey.WalkDetailStartTime)}: ${formatDateTime(walk.startTime)}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "${stringResource(StringKey.WalkDetailEndTime)}: " +
                                (walk.endTime?.let(::formatDateTime) ?: stringResource(StringKey.WalkDetailInProgress)),
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("${stringResource(StringKey.WalkDetailDistance)}: ${formatDistanceKm(walk.distanceMeters)}")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        Text(
                            "${stringResource(StringKey.WalkDetailDuration)}: " +
                                (walk.endTime?.let { formatDurationLabeled(it - walk.startTime) } ?: "—"),
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        Text(
                            "${stringResource(StringKey.WalkDetailAvgSpeed)}: " +
                                (walk.endTime?.let { formatSpeedKmh(walk.avgSpeed) } ?: "—"),
                        )
                    }

                    Text(
                        if (uiState.mushroomCounts.isEmpty()) {
                            stringResource(StringKey.WalkDetailFindsEmpty)
                        } else {
                            stringResource(StringKey.WalkDetailFindsTitle)
                        },
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                        textDecoration = TextDecoration.Underline,
                    )
                }

                items(uiState.mushroomCounts) { entry ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(categoryDisplayName(entry.category))
                        Text(entry.count.toString())
                    }
                }

                if (uiState.mushroomCounts.isNotEmpty()) {
                    item {
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
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            stringResource(StringKey.WalkDetailDescriptionTitle),
                            textDecoration = TextDecoration.Underline,
                        )
                        IconButton(onClick = onEditDescription) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = stringResource(StringKey.WalkDetailEditDescriptionContentDescription),
                            )
                        }
                    }
                    Text(
                        walk.description?.ifBlank { null } ?: stringResource(StringKey.WalkDetailDescriptionEmpty),
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onEditDescription),
                    )
                }

                if (places.isNotEmpty()) {
                    item {
                        Text(
                            stringResource(StringKey.WalkDetailPlacesTitle),
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
                            textDecoration = TextDecoration.Underline,
                        )
                    }

                    items(places) { place -> PlaceListItem(place = place, onClick = { selectedPlaceId = place.id }) }
                }
            }

            OutlinedButton(onClick = onViewMap, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(stringResource(StringKey.WalkDetailViewMap))
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

@Composable
private fun PlaceListItem(place: FieldMark, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (place.photoPath != null) {
            AsyncImage(
                model = "file://${place.photoPath}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(PLACE_THUMBNAIL_SIZE).clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(place.name?.ifBlank { null } ?: stringResource(StringKey.AddPlaceDefaultName))
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
