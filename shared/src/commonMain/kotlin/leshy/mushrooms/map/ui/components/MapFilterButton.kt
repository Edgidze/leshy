package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource

/**
 * Плавающая кнопка «Фильтры: N» — на живой карте «Записи» и на полноэкранной сводной карте
 * находок ([leshy.mushrooms.map.ui.screens.FindsMapScreen], там она стоит в одну линию с кнопкой
 * «назад»). Обводка та же, что у круглых боковых кнопок «Записи», чтобы два семейства кнопок
 * читались там как одно.
 *
 * На самой странице «Карта находок» этой кнопки нет: две её оси (даты и сезон) вынесены на
 * страницу ползунками, см. `MapScreen`.
 */
@Composable
fun MapFilterButton(filterCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Icon(imageVector = Icons.Filled.FilterList, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("${stringResource(StringKey.MapFilterButtonLabel)}: $filterCount")
    }
}
