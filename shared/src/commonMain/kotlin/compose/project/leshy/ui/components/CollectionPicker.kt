package compose.project.leshy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import compose.project.leshy.domain.model.Category
import compose.project.leshy.i18n.StringKey
import compose.project.leshy.i18n.categoryDisplayName
import compose.project.leshy.i18n.collectionDisplayName
import compose.project.leshy.i18n.stringResource
import compose.project.leshy.presentation.CollectionPickState
import compose.project.leshy.presentation.CollectionPickerItem

/**
 * Expandable per-collection checklist: a tri-state checkbox toggles every member species at once,
 * expanding a collection shows its members as individually-checkable rows — plain icon + name
 * beside it, same shape as the Filter screen's `SpeciesFilterRow` (`MapFilterDialog.kt`), not the
 * label-baked-into-the-photo look [MushroomPhoto] uses on Record's tiles. Shared by the "Грибы"
 * screen and the first-run onboarding screen (`.claude/plans/countries-and-languages.md`, Phase 3;
 * originally `mushroom-collections.md`, Phases 1/3) — this composable owns no state of its own
 * beyond which sections are expanded and the search query; [items] and the two callbacks are the
 * single source of truth for picking.
 *
 * All 33 country sections start collapsed by default (each section's own `expanded` state, below) —
 * with that many collections the search field is the primary way to find one, not scrolling.
 * Filtering is a plain case-insensitive substring match against the resolved country name, not the
 * ranked startsWith/contains/fuzzy-prefix scheme `searchOrderedCategories` uses for species search
 * (`presentation/CLAUDE.md`) — country names are short and mostly non-overlapping, so a simple
 * filter is enough; Phase 4 of the plan already intends to generalize that ranking for reuse by the
 * language picker, and this can switch to it then instead of duplicating it now.
 */
@Composable
fun CollectionPicker(
    items: List<CollectionPickerItem>,
    onToggleCollection: (CollectionPickerItem) -> Unit,
    onToggleCategory: (Category, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val filteredItems = if (query.isBlank()) {
        items
    } else {
        items.filter { collectionDisplayName(it.collection.nameKey).contains(query, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(stringResource(StringKey.CollectionPickerSearchHint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        )
        filteredItems.forEach { item ->
            CollectionPickerSection(
                item = item,
                onToggleCollection = onToggleCollection,
                onToggleCategory = onToggleCategory,
            )
        }
    }
}

@Composable
private fun CollectionPickerSection(
    item: CollectionPickerItem,
    onToggleCollection: (CollectionPickerItem) -> Unit,
    onToggleCategory: (Category, Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TriStateCheckbox(
                state = when (item.pickState) {
                    CollectionPickState.ALL -> ToggleableState.On
                    CollectionPickState.NONE -> ToggleableState.Off
                    CollectionPickState.SOME -> ToggleableState.Indeterminate
                },
                onClick = { onToggleCollection(item) },
            )
            Text(
                text = collectionDisplayName(item.collection.nameKey),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
            )
        }

        if (expanded) {
            item.members.forEach { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .toggleable(
                            value = category.isPicked,
                            onValueChange = { onToggleCategory(category, it) },
                            role = Role.Checkbox,
                        )
                        .padding(start = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = category.isPicked, onCheckedChange = null)
                    CategoryIcon(category = category, modifier = Modifier.size(56.dp).padding(start = 8.dp))
                    Text(
                        text = categoryDisplayName(category),
                        modifier = Modifier.weight(1f).padding(start = 12.dp),
                    )
                }
            }
        }
    }
}
