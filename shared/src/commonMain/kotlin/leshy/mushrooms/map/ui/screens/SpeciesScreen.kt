package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.species.SpeciesViewModel
import leshy.mushrooms.map.ui.components.CategoryIcon
import leshy.mushrooms.map.ui.components.CollectionPicker
import leshy.mushrooms.map.ui.components.MushroomImageDisclaimerBanner
import leshy.mushrooms.map.ui.components.SpeciesFormDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeciesScreen(modifier: Modifier = Modifier, viewModel: SpeciesViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var editingSpecies by remember { mutableStateOf<Category?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(
            stringResource(StringKey.SpeciesCollectionsTitle),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        MushroomImageDisclaimerBanner(modifier = Modifier.padding(bottom = 8.dp))
        CollectionPicker(
            items = uiState.collectionPickerItems,
            onToggleCollection = viewModel::toggleCollection,
            onToggleCategory = viewModel::setCategoryPicked,
        )

        Text(
            stringResource(StringKey.SpeciesMyMushroomsTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        if (uiState.userSpecies.isEmpty()) {
            Text(
                stringResource(StringKey.SpeciesMyMushroomsEmpty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        } else {
            uiState.userSpecies.forEach { species ->
                UserSpeciesRow(
                    category = species,
                    onToggleVisibility = { viewModel.toggleSpeciesVisibility(species) },
                    onEditClick = { editingSpecies = species },
                    onDeleteClick = { viewModel.onDeleteSpeciesClick(species) },
                )
            }
        }

        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Text(stringResource(StringKey.SpeciesAddButton))
        }
    }

    if (showCreateDialog) {
        SpeciesFormDialog(
            existing = null,
            language = uiState.language,
            onSave = { name, scientificName, colorHex, iconBytes ->
                viewModel.saveSpecies(null, name, scientificName, colorHex, iconBytes)
            },
            onDismissRequest = { showCreateDialog = false },
        )
    }

    val speciesBeingEdited = editingSpecies
    if (speciesBeingEdited != null) {
        SpeciesFormDialog(
            existing = speciesBeingEdited,
            language = uiState.language,
            onSave = { name, scientificName, colorHex, iconBytes ->
                viewModel.saveSpecies(speciesBeingEdited, name, scientificName, colorHex, iconBytes)
            },
            onDismissRequest = { editingSpecies = null },
        )
    }

    val pendingDelete = uiState.pendingDelete
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteSpeciesDismiss,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = { Text(stringResource(StringKey.SpeciesDeleteConfirmTitle)) },
            text = { Text(stringResource(StringKey.SpeciesDeleteConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteSpeciesConfirm) {
                    Text(stringResource(StringKey.SpeciesDeleteConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteSpeciesDismiss) {
                    Text(stringResource(StringKey.SpeciesDeleteConfirmNo))
                }
            },
        )
    }
}

@Composable
private fun UserSpeciesRow(
    category: Category,
    onToggleVisibility: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = category.isPicked,
                onValueChange = { onToggleVisibility() },
                role = Role.Checkbox,
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = category.isPicked,
            onCheckedChange = null,
        )
        CategoryIcon(category = category, modifier = Modifier.size(48.dp).padding(start = 4.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(categoryDisplayName(category))
            if (category.source == CategorySource.IMPORTED) {
                Text(
                    stringResource(StringKey.SpeciesListImportedLabel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(StringKey.SpeciesListEditContentDescription),
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(StringKey.SpeciesListDeleteContentDescription),
                )
            }
        }
    }
}
