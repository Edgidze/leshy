package compose.project.leshy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import compose.project.leshy.data.platform.plainTextClipEntry
import compose.project.leshy.data.platform.rememberCameraLauncher
import compose.project.leshy.data.platform.rememberCameraPermissionRequester
import compose.project.leshy.domain.model.GeoPoint
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.ui.util.formatCoordinates
import kotlinx.coroutines.launch

/**
 * Large modal covering most of the screen, same visual language as [MapFilterDialog] — a "place"
 * is created here in one shot (name, photo, description, coordinates) then persisted as a single
 * POI-typed [compose.project.leshy.domain.model.FieldMark] on confirm. No draft is saved on
 * dismiss/discard — this replaces the old bare camera plate that used to sit after the mushroom
 * tiles on the Record screen.
 */
@Composable
fun AddPlaceDialog(
    location: GeoPoint?,
    onSave: (name: String, description: String, photoPath: String?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val defaultName = stringResource(StringKey.AddPlaceDefaultName)
    var name by remember { mutableStateOf(defaultName) }
    var nameTouched by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var photoPath by remember { mutableStateOf<String?>(null) }
    val takePhoto = rememberCameraLauncher { path -> photoPath = path }
    val requestPhoto = rememberCameraPermissionRequester(onGranted = takePhoto)
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coordinatesText = location?.let { formatCoordinates(it.lat, it.lon) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = stringResource(StringKey.AddPlaceTitle), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameTouched = true
                        },
                        label = { Text(stringResource(StringKey.AddPlaceNameHint)) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = if (nameTouched) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                        ),
                        modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                            if (focusState.isFocused && !nameTouched) {
                                name = ""
                                nameTouched = true
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PlacePhotoBox(photoPath = photoPath, onClick = requestPhoto, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(StringKey.AddPlaceDescriptionTitle),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(StringKey.AddPlaceDescriptionHint)) },
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus(force = true)
                            },
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (coordinatesText != null) {
                        Spacer(modifier = Modifier.height(16.dp))
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

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(StringKey.AddPlaceDiscardContentDescription),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                    IconButton(
                        onClick = {
                            onSave(name.ifBlank { defaultName }, description, photoPath)
                            onDismissRequest()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(StringKey.AddPlaceSaveContentDescription),
                        )
                    }
                }
            }
        }
    }
}

/** Very large tap target — a placeholder camera button until a photo is taken, then the photo itself (retake on tap). */
@Composable
private fun PlacePhotoBox(photoPath: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.aspectRatio(1.2f)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (photoPath == null) {
                IconButton(onClick = onClick, modifier = Modifier.size(72.dp)) {
                    Icon(
                        imageVector = Icons.Filled.AddAPhoto,
                        contentDescription = stringResource(StringKey.AddPlacePhotoContentDescription),
                        modifier = Modifier.size(56.dp),
                    )
                }
            } else {
                AsyncImage(
                    model = "file://$photoPath",
                    contentDescription = stringResource(StringKey.AddPlacePhotoContentDescription),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clickable(onClick = onClick),
                )
            }
        }
    }
}
