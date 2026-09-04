package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import leshy.mushrooms.map.domain.util.MILLIS_PER_DAY
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.mapfilter.MapFilterUiState
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.monthName
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Два ползунка фильтра — диапазон дат и сезон — живут отдельно от [MapFilterDialog], потому что
 * стоят в двух местах сразу: в самом диалоге (вместе с переключателями видов) и прямо на экране
 * «Карта находок», где статистика пересчитывается под ними и водить пальцем удобнее без открытия
 * диалога. Общее у них не только внешность, но и правило фиксации: ползунок ведётся своим
 * локальным состоянием, а в репозиторий значение уходит один раз, на отпускании
 * (`onValueChangeFinished`) — иначе каждый промежуточный кадр перетаскивания порождал бы запись в
 * DataStore и полный пересчёт карты.
 */

/**
 * Есть ли вообще что показывать ползунком дат: пока все прогулки записаны в один календарный день,
 * его концы совпадают, и двигать нечего.
 */
val MapFilterUiState.hasDateRange: Boolean
    get() {
        val min = minWalkStart ?: return false
        val max = maxWalkStart ?: return false
        return min / MILLIS_PER_DAY != max / MILLIS_PER_DAY
    }

@Composable
fun MapDateRangeSlider(
    uiState: MapFilterUiState,
    onRangeChanged: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
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

    Column(modifier = modifier) {
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
fun MapMonthRangeSlider(
    uiState: MapFilterUiState,
    onRangeChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sliderRange by remember(uiState.monthFrom, uiState.monthTo) {
        mutableStateOf(uiState.monthFrom.toFloat()..uiState.monthTo.toFloat())
    }

    Column(modifier = modifier) {
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
