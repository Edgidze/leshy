package compose.project.leshy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import compose.project.leshy.data.platform.plainTextClipEntry
import compose.project.leshy.domain.model.FieldMark
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.util.formatCoordinates
import kotlinx.coroutines.launch

/**
 * Read-only counterpart of [AddPlaceDialog] for an existing place — same shell/sizing, but name
 * and description are plain [Text] (not editable), and the bottom Close/Save row is replaced by a
 * top row: back (dismiss, no changes possible here anyway), edit (swaps this dialog for
 * [AddPlaceDialog] pre-filled from [mark] — see its "editing" doc), and delete (caller shows a
 * confirmation before actually removing the mark).
 */
@Composable
fun PlaceViewDialog(
    mark: FieldMark,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val coordinatesText = formatCoordinates(mark.lat, mark.lon)
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = addPlaceDialogProperties(),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    Row {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(StringKey.PlaceViewEditContentDescription),
                            )
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(StringKey.PlaceViewDeleteContentDescription),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Text(text = mark.name.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (mark.photoPath != null) {
                        Card(modifier = Modifier.fillMaxWidth().aspectRatio(1.2f)) {
                            AsyncImage(
                                model = "file://${mark.photoPath}",
                                contentDescription = stringResource(StringKey.AddPlacePhotoContentDescription),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    if (!mark.description.isNullOrBlank()) {
                        Text(
                            text = stringResource(StringKey.AddPlaceDescriptionTitle),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = mark.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text(
                        text = stringResource(StringKey.AddPlaceCoordinatesTitle),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = coordinatesText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(plainTextClipEntry(coordinatesText))
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription =
                                    stringResource(StringKey.AddPlaceCopyCoordinatesContentDescription),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Confirmation gate before [DeletePlaceMarkUseCase][compose.project.leshy.domain.usecase.DeletePlaceMarkUseCase]
 * actually runs — same shape as the walk-level delete confirmation in `WalkDetailScreen.kt`. */
@Composable
fun DeletePlaceConfirmDialog(onConfirm: () -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = { Text(stringResource(StringKey.PlaceDeleteConfirmTitle)) },
        text = { Text(stringResource(StringKey.PlaceDeleteConfirmMessage)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(StringKey.PlaceDeleteConfirmYes)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(StringKey.PlaceDeleteConfirmNo)) }
        },
    )
}
