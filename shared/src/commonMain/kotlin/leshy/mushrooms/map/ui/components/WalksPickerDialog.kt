package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.i18n.walksUnitLabel
import leshy.mushrooms.map.presentation.data.WalksPickState
import leshy.mushrooms.map.presentation.data.WalksPickerGroup
import leshy.mushrooms.map.presentation.data.buildWalksPickerGroups
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.monthName

/**
 * Which walks go into the export archive — a modal sized like [MapFilterDialog], not a full
 * navigation screen (walk selection only narrows what Export writes, same "the previous screen is
 * still there underneath" relationship). Keeps its own draft [selectedIds] so the back arrow can
 * discard whatever was toggled during this session; only the checkmark pushes the draft back to
 * [onConfirm]. Mirrors [CollectionPicker]'s expandable-group-with-tri-state-checkbox shape, grouped
 * by year+month here instead of by collection.
 */
@Composable
fun WalksPickerDialog(
    walks: List<Walk>,
    initiallySelectedIds: Set<Long>,
    onConfirm: (Set<Long>) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedIds by remember(initiallySelectedIds) { mutableStateOf(initiallySelectedIds) }
    val groups = remember(walks, selectedIds) { buildWalksPickerGroups(walks, selectedIds) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(StringKey.DataWalksBackContentDescription),
                        )
                    }
                    Text(
                        text = stringResource(StringKey.DataChooseWalksTitle),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    )
                    IconButton(onClick = { onConfirm(selectedIds) }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(StringKey.DataWalksConfirmContentDescription),
                        )
                    }
                }
                HorizontalDivider()

                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    groups.forEach { group ->
                        item(key = "${group.year}-${group.month}") {
                            WalksPickerGroupSection(
                                group = group,
                                onToggleGroup = { include ->
                                    val groupIds = group.walks.map { it.id }.toSet()
                                    selectedIds = if (include) selectedIds + groupIds else selectedIds - groupIds
                                },
                                onToggleWalk = { walkId, include ->
                                    selectedIds = if (include) selectedIds + walkId else selectedIds - walkId
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalksPickerGroupSection(
    group: WalksPickerGroup,
    onToggleGroup: (Boolean) -> Unit,
    onToggleWalk: (Long, Boolean) -> Unit,
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
                state = when (group.pickState) {
                    WalksPickState.ALL -> ToggleableState.On
                    WalksPickState.NONE -> ToggleableState.Off
                    WalksPickState.SOME -> ToggleableState.Indeterminate
                },
                onClick = { onToggleGroup(group.pickState != WalksPickState.ALL) },
            )
            Text(
                text = "${monthName(group.month)} ${group.year}",
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
            )
        }

        if (expanded) {
            group.walks.forEach { walk ->
                val checked = walk.id in group.selectedIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = checked,
                            onValueChange = { onToggleWalk(walk.id, it) },
                            role = Role.Checkbox,
                        )
                        .padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = checked, onCheckedChange = null)
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(text = walk.name)
                        Text(text = formatDateOnly(walk.startTime), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/** "Selected N walks" — shown on the Export section's walk-picker button (see `DataScreen.kt`). */
@Composable
fun walksSelectedButtonLabel(count: Int): String =
    "${stringResource(StringKey.DataWalksSelectedLabel)} $count ${walksUnitLabel(count)}"
