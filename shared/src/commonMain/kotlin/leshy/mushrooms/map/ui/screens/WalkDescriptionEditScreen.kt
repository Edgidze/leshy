package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.archive.WalkDetailViewModel

/**
 * A dedicated NavHost screen, not a Dialog — deliberately, to avoid the class of multiline-
 * TextField/IME bug fixed in `AddPlaceDialog` (see its doc comments): that bug is specific to
 * Compose `Dialog` windows not adjusting for SOFT_INPUT on Android, where a programmatic
 * `focusManager.clearFocus()` on a multiline field reliably fails to release the keyboard. A
 * regular NavHost screen renders in the app's main window, which doesn't have that failure mode,
 * so Cancel/Save here can just navigate back — no clearFocus()/focus-sink workaround needed.
 *
 * Shares the parent `WalkDetailViewModel` instance (same pattern as [WalkMapScreen]) rather than
 * owning its own — the walk (and its current description) is already loaded by the time this
 * screen can be reached.
 */
@Composable
fun WalkDescriptionEditScreen(viewModel: WalkDetailViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var description by remember { mutableStateOf(uiState.walk?.description.orEmpty()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(StringKey.WalkDetailDescriptionTitle)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(StringKey.WalkDetailDescriptionCancelContentDescription),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onDescriptionConfirm(description)
                            onBack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(StringKey.WalkDetailDescriptionSaveContentDescription),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).imePadding()) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text(stringResource(StringKey.WalkDetailDescriptionHint)) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
