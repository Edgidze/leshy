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
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.domain.util.MILLIS_PER_DAY
import leshy.mushrooms.map.presentation.mapfilter.MapFilterUiState
import leshy.mushrooms.map.presentation.mapfilter.MapFilterViewModel
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.monthName
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import kotlin.math.roundToLong

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

                val minWalkStart = uiState.minWalkStart
                val maxWalkStart = uiState.maxWalkStart
                val hasDateRange = minWalkStart != null && maxWalkStart != null &&
                    minWalkStart / MILLIS_PER_DAY != maxWalkStart / MILLIS_PER_DAY

                LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                    item {
                        if (hasDateRange) {
                            Spacer(modifier = Modifier.height(16.dp))
                            DateRangeSection(uiState, viewModel::setDateRange)
                            Spacer(modifier = Modifier.height(16.dp))
                            MonthRangeSection(uiState, viewModel::setMonthRange)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                        }
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

@Composable
private fun DateRangeSection(uiState: MapFilterUiState, onRangeChanged: (Long, Long) -> Unit) {
    val minStart = uiState.minWalkStart ?: return
    val maxStart = uiState.maxWalkStart ?: return
    val minDay = minStart / MILLIS_PER_DAY
    val maxDay = maxStart / MILLIS_PER_DAY

    var sliderRange by remember(uiState.startMillis, uiState.endMillis) {
        mutableStateOf(
            ((uiState.startMillis ?: minStart) / MILLIS_PER_DAY).toFloat()..
                ((uiState.endMillis ?: maxStart) / MILLIS_PER_DAY).toFloat(),
        )
    }

    Column {
        Text(stringResource(StringKey.MapFilterDateRangeTitle), style = MaterialTheme.typography.titleSmall)
        Text(
            "${formatDateOnly(sliderRange.start.roundToLong() * MILLIS_PER_DAY)} – " +
                formatDateOnly(sliderRange.endInclusive.roundToLong() * MILLIS_PER_DAY),
            style = MaterialTheme.typography.bodyMedium,
        )
        RangeSlider(
            value = sliderRange,
            onValueChange = { sliderRange = it },
            valueRange = minDay.toFloat()..maxDay.toFloat(),
            onValueChangeFinished = {
                onRangeChanged(
                    sliderRange.start.roundToLong() * MILLIS_PER_DAY,
                    sliderRange.endInclusive.roundToLong() * MILLIS_PER_DAY,
                )
            },
        )
    }
}

@Composable
private fun MonthRangeSection(uiState: MapFilterUiState, onRangeChanged: (Int, Int) -> Unit) {
    var sliderRange by remember(uiState.monthFrom, uiState.monthTo) {
        mutableStateOf(uiState.monthFrom.toFloat()..uiState.monthTo.toFloat())
    }

    Column {
        Text(stringResource(StringKey.MapFilterMonthRangeTitle), style = MaterialTheme.typography.titleSmall)
        Text(
            "${monthName(sliderRange.start.roundToInt())} – ${monthName(sliderRange.endInclusive.roundToInt())}",
            style = MaterialTheme.typography.bodyMedium,
        )
        RangeSlider(
            value = sliderRange,
            onValueChange = { sliderRange = it },
            valueRange = 1f..12f,
            steps = 10,
            onValueChangeFinished = {
                onRangeChanged(sliderRange.start.roundToInt(), sliderRange.endInclusive.roundToInt())
            },
        )
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
