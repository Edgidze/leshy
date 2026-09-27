package klev.fishing.map.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Ряд чипов с переносом. Ширину ячеек НЕ считает: чипы меряют себя сами.
 *
 * Это важно, потому что ручной расчёт ширины ячейки во `FlowRow` — известные грабли проекта:
 * `FlowRow` округляет ширину ребёнка и отбивку отдельными `roundToPx()`, и при дробной плотности
 * сумма вылезает на пиксель за ширину ряда, после чего ряд рассыпается в одну колонку (разбор —
 * `FindTilesGrid` в `:shared`). Здесь этой арифметики нет вовсе, и появляться ей тут незачем.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipFlow(modifier: Modifier = Modifier, content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}
