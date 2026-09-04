package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.mapfilter.MapFilterViewModel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.viewmodel.koinViewModel

/**
 * Large modal covering most of the screen with a visible margin around it (the previous screen
 * is not a separate page — filtering only adjusts what it shows). Shared identically by the Map
 * screen and the Record screen's filter buttons.
 */
@Composable
fun MapFilterDialog(onDismissRequest: () -> Unit, viewModel: MapFilterViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.88f),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(StringKey.MapFilterBackContentDescription),
                        )
                    }
                    Text(
                        text = stringResource(StringKey.MapFilterDialogTitle),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 16.dp),
                    )
                }
                HorizontalDivider()

                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    item {
                        if (uiState.hasDateRange) {
                            Spacer(modifier = Modifier.height(16.dp))
                            MapDateRangeSlider(uiState, viewModel::setDateRange)
                            Spacer(modifier = Modifier.height(16.dp))
                            MapMonthRangeSlider(uiState, viewModel::setMonthRange)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                        }
                        Text(
                            text = stringResource(StringKey.MapFilterPastRoutesTitle),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                        ToggleFilterRow(
                            label = stringResource(StringKey.MapFilterShowPastRoutes),
                            checked = uiState.showPastRoutes,
                            onToggle = viewModel::setShowPastRoutes,
                        )
                        HorizontalDivider()
                        Text(
                            text = stringResource(StringKey.SettingsCategoriesTitle),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    }
                    items(uiState.categories, key = { it.id }) { category ->
                        SpeciesFilterRow(category, onToggle = { viewModel.setCategoryIncluded(category, it) })
                    }
                }
            }
        }
    }
}

/**
 * Строка фильтра, не относящегося к грибу: та же высота 56.dp и тот же [Switch] справа, что у
 * [SpeciesFilterRow], но БЕЗ слота под картинку слева — ни картинки, ни зарезервированного под неё
 * отступа. Надпись начинается от левого края строки, вровень с заголовками разделов над ней, а не
 * вровень с названиями грибов, сдвинутыми вправо своими иконками.
 */
@Composable
private fun ToggleFilterRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun SpeciesFilterRow(category: Category, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
        CategoryIcon(category = category, modifier = Modifier.size(56.dp))
        Text(
            text = categoryDisplayName(category),
            modifier = Modifier.weight(1f).padding(start = 12.dp),
        )
        Switch(checked = category.isActive, onCheckedChange = onToggle)
    }
}
