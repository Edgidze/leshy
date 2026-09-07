package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
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
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.CategorySource
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.collectionDisplayName
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.UserSpeciesGroup
import leshy.mushrooms.map.presentation.species.SpeciesViewModel
import leshy.mushrooms.map.ui.components.CategoryIcon
import leshy.mushrooms.map.ui.components.CollectionPicker
import leshy.mushrooms.map.ui.components.LeshyButton
import leshy.mushrooms.map.ui.components.MushroomImageDisclaimerBanner
import leshy.mushrooms.map.ui.components.SpeciesFormDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SpeciesScreen(modifier: Modifier = Modifier, viewModel: SpeciesViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var editingSpecies by remember { mutableStateOf<Category?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // imePadding() ПЕРЕД verticalScroll — то есть окно прокрутки кончается там, где начинается
    // клавиатура, а не уходит под неё; поле поиска подборок само подтягивается к его верху
    // (`CollectionPicker`). На iOS это единственное, что удерживает поле над клавиатурой:
    // рантаймовый сдвиг сцены снят, см. `MainViewController.kt`. Онбординг с тем же пикером
    // обходится `WindowInsets.safeDrawing` у внешней колонки — на iOS он включает `ime`.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            stringResource(StringKey.SpeciesCollectionsTitle),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        MushroomImageDisclaimerBanner(modifier = Modifier.padding(bottom = 8.dp))
        CollectionPicker(
            items = uiState.collectionPickerItems,
            query = uiState.collectionQuery,
            onQueryChange = viewModel::onCollectionQueryChange,
            onToggleCollection = viewModel::toggleCollection,
            onToggleCategory = viewModel::setCategoryPicked,
        )

        Text(
            stringResource(StringKey.SpeciesMyMushroomsTitle),
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )
        // Пока база не ответила, «своих грибов нет» — неправда, а не факт: тот же случай, что у
        // Архива и Статистики, только заявление тут строкой, а не картинкой. Индикатор мелкий и
        // стоит ровно на месте строки, чтобы форма вокруг не прыгала.
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(bottom = 8.dp).size(20.dp),
                strokeWidth = 2.dp,
            )
        } else if (uiState.userSpecies.isEmpty()) {
            Text(
                stringResource(StringKey.SpeciesMyMushroomsEmpty),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        } else {
            // Тот же запрос, что и в пикере стран выше: поле ввода на экране одно, и оно обязано
            // находить в том числе свои подборки и свои грибы — их в верхнем пикере нет.
            // Ничего не совпало — блок просто пуст, ровно как страновой пикер выше: своя
            // отбивка на каждый из двух блоков превратила бы пустой поиск в две строки текста.
            filterUserGroups(uiState.userGroups, uiState.collectionQuery, LocalAppLanguage.current).forEach { group ->
                UserCollectionSection(
                    group = group,
                    onToggleCollection = { viewModel.toggleUserCollection(group) },
                    onToggleVisibility = { viewModel.toggleSpeciesVisibility(it) },
                    onEditClick = { species -> editingSpecies = species },
                    onDeleteClick = { viewModel.onDeleteSpeciesClick(it) },
                )
            }
        }

        LeshyButton(
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
            onSave = { name, scientificName, colorHex, iconBytes, collectionName ->
                viewModel.saveSpecies(null, name, scientificName, colorHex, iconBytes, collectionName)
            },
            onDismissRequest = { showCreateDialog = false },
        )
    }

    val speciesBeingEdited = editingSpecies
    if (speciesBeingEdited != null) {
        SpeciesFormDialog(
            existing = speciesBeingEdited,
            language = uiState.language,
            onSave = { name, scientificName, colorHex, iconBytes, collectionName ->
                viewModel.saveSpecies(speciesBeingEdited, name, scientificName, colorHex, iconBytes, collectionName)
            },
            onDismissRequest = { editingSpecies = null },
            // Только у именованной подборки: у «Других» имени нет, и поле обязано остаться пустым
            // со своей подсказкой — иначе перенос «оставить как есть» превратился бы в создание
            // подборки с буквальным именем «Другие».
            initialCollectionName = uiState.userGroups
                .firstOrNull { group -> group.species.any { it.id == speciesBeingEdited.id } }
                ?.collection?.name.orEmpty(),
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

/**
 * Фильтр нижнего блока тем же запросом, что фильтрует страновые подборки сверху. Совпало имя
 * подборки — показывается вся подборка целиком; совпали только грибы — та же подборка, но с одними
 * совпавшими грибами. Пустой запрос не фильтрует ничего.
 *
 * Ограничения на число строк, как у поиска видов в `CollectionPicker`, тут нет и не нужно: там
 * запрос идёт по 408 видам каталога с иллюстрациями, здесь — по своим грибам, которых у человека
 * единицы.
 */
private fun filterUserGroups(
    groups: List<UserSpeciesGroup>,
    query: String,
    language: AppLanguage,
): List<UserSpeciesGroup> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return groups
    return groups.mapNotNull { group ->
        if (collectionDisplayName(group.collection, language).contains(trimmed, ignoreCase = true)) {
            group
        } else {
            group.species.filter { categoryDisplayName(it, language).contains(trimmed, ignoreCase = true) }
                .takeIf { it.isNotEmpty() }
                ?.let { group.copy(species = it) }
        }
    }
}

/**
 * Пользовательская подборка со своими грибами. Раскрыта по умолчанию, в отличие от страновых
 * секций выше: тех 47 и человек ищет среди них конкретную, а своих подборок единицы и до этой
 * работы весь список «Добавленных грибов» был виден целиком.
 *
 * Трисостояние в шапке — то же «выбрать всю подборку», что у страновых, и оно осмысленно ровно так
 * же. Не показывается только у запасных «Других» (`id == 0`): строки такой подборки в базе нет,
 * переключать в ней нечего.
 */
@Composable
private fun UserCollectionSection(
    group: UserSpeciesGroup,
    onToggleCollection: () -> Unit,
    onToggleVisibility: (Category) -> Unit,
    onEditClick: (Category) -> Unit,
    onDeleteClick: (Category) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (group.collection.id != 0L) {
                TriStateCheckbox(
                    state = when {
                        group.species.all { it.isPicked } -> ToggleableState.On
                        group.species.none { it.isPicked } -> ToggleableState.Off
                        else -> ToggleableState.Indeterminate
                    },
                    onClick = onToggleCollection,
                )
            }
            Text(
                text = collectionDisplayName(group.collection),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
            )
        }

        if (expanded) {
            group.species.forEach { species ->
                UserSpeciesRow(
                    category = species,
                    onToggleVisibility = { onToggleVisibility(species) },
                    onEditClick = { onEditClick(species) },
                    onDeleteClick = { onDeleteClick(species) },
                )
            }
        }
    }
}
