package compose.project.leshy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import compose.project.leshy.data.platform.rememberCameraLauncher
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.record.RecordViewModel
import compose.project.leshy.ui.components.CameraTile
import compose.project.leshy.ui.components.MushroomTile
import compose.project.leshy.ui.util.formatDistanceKm
import compose.project.leshy.ui.util.formatDuration
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecordScreen(onViewMap: () -> Unit, viewModel: RecordViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val takePhoto = rememberCameraLauncher { path -> viewModel.onPhotoCaptured(path) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = uiState.walkName,
            onValueChange = viewModel::setWalkName,
            label = { Text(stringResource(StringKey.RecordWalkNameHint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(formatDuration(uiState.elapsedMillis))
            Text(formatDistanceKm(uiState.distanceMeters))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        ) {
            items(uiState.categories, key = { it.id }) { category ->
                MushroomTile(
                    category = category,
                    count = uiState.mushroomCounts[category.id] ?: 0,
                    onAdd = { viewModel.addMushroom(category.id) },
                    onRemove = { viewModel.removeMushroom(category.id) },
                    modifier = Modifier.padding(4.dp),
                )
            }
            item {
                CameraTile(onClick = takePhoto, modifier = Modifier.padding(4.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = viewModel::onStartOrPauseClick, modifier = Modifier.weight(1f)) {
                val label = when {
                    !uiState.isRecording -> StringKey.RecordStart
                    !uiState.isPaused -> StringKey.RecordPause
                    else -> StringKey.RecordResume
                }
                Text(stringResource(label))
            }
            if (uiState.isRecording && uiState.isPaused) {
                Button(onClick = viewModel::finish, modifier = Modifier.weight(1f)) {
                    Text(stringResource(StringKey.RecordFinish))
                }
            }
        }

        OutlinedButton(onClick = onViewMap, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(StringKey.RecordViewMap))
        }
    }
}
