package compose.project.leshy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import compose.project.leshy.i18n.collectionDisplayName
import compose.project.leshy.presentation.CollectionPickState
import compose.project.leshy.presentation.CollectionPickerItem

/**
 * Expandable per-collection checklist: a tri-state checkbox toggles every member species at once,
 * expanding a collection shows its members as individually-checkable [MushroomPhoto] thumbnails.
 * Shared by the Settings screen and the first-run onboarding screen (`.claude/plans/
 * mushroom-collections.md`, Phases 1/3) — this composable owns no state of its own beyond which
 * sections are expanded, [items] and the two callbacks are the single source of truth.
 */
@Composable
fun CollectionPicker(
    items: List<CollectionPickerItem>,
    onToggleCollection: (CollectionPickerItem) -> Unit,
    onToggleCategory: (Category, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEach { item ->
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
                        .toggleable(
                            value = category.isPicked,
                            onValueChange = { onToggleCategory(category, it) },
                            role = Role.Checkbox,
                        )
                        .padding(start = 24.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Checkbox(checked = category.isPicked, onCheckedChange = null)
                    MushroomPhoto(category = category, modifier = Modifier.width(96.dp).aspectRatio(1.5f))
                }
            }
        }
    }
}
