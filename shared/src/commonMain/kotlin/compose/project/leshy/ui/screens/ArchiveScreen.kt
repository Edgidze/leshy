package compose.project.leshy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.archive.ArchiveViewModel
import compose.project.leshy.ui.components.WalkCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ArchiveScreen(
    onWalkClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArchiveViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Leaving selection mode: hardware/gesture back, navigating to another section (this
    // composable leaves composition), or dismissing the delete dialog with "No" (handled in
    // ArchiveViewModel.onDeleteDismiss). App process death trivially drops the in-memory selection.
    BackHandler(enabled = uiState.isSelectionMode) { viewModel.clearSelection() }
    DisposableEffect(Unit) { onDispose { viewModel.clearSelection() } }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            modifier = Modifier.border(
                BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                shape = AlertDialogDefaults.shape,
            ),
            text = { Text(stringResource(StringKey.ArchiveDeleteConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirm) {
                    Text(stringResource(StringKey.ArchiveDeleteConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteDismiss) {
                    Text(stringResource(StringKey.ArchiveDeleteConfirmNo))
                }
            },
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Outside the LazyColumn so it stays pinned at the top of the screen while the list
        // beneath it scrolls.
        if (uiState.isSelectionMode) {
            Button(
                onClick = viewModel::onDeleteClick,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null)
                Text(
                    stringResource(StringKey.ArchiveDeleteWalksButton),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        if (uiState.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(StringKey.ArchiveEmpty))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.items, key = { it.walk.id }) { item ->
                    val walkId = item.walk.id
                    WalkCard(
                        walk = item.walk,
                        track = item.track,
                        findLocations = item.findLocations,
                        isSelected = walkId in uiState.selectedWalkIds,
                        onClick = {
                            if (uiState.isSelectionMode) {
                                viewModel.toggleSelection(walkId)
                            } else {
                                onWalkClick(walkId)
                            }
                        },
                        onLongPress = { viewModel.selectWalk(walkId) },
                    )
                }
            }
        }
    }
}
