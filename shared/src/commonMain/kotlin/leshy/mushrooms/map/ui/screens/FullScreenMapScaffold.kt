package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.map.mapOrnamentOptions
import org.maplibre.compose.map.OrnamentOptions

/** См. комментарий у ряда кнопок в [FullScreenMapScaffold] — столько занимает линейка масштаба. */
private val MAP_CHROME_TOP_OFFSET = 31.dp

/**
 * Карта во весь экран с кнопкой «назад» поверх неё — общая оправа двух экранов: карты одной
 * прогулки ([WalkMapScreen]) и сводной карты находок ([FindsMapScreen]). Отличаются они только
 * тем, какая карта внутри и что стоит справа от «назад» ([action]), а вся возня с системными
 * отступами у них одна и та же — и написана она была ровно один раз, для карты прогулки.
 *
 * В отличие от `RecordScreen.kt`/`MapScreen.kt`, здесь нет `Scaffold` с `TopAppBar`, который
 * съел бы верхний системный отступ (эта пара заметно перекашивала размер карты на iOS — см.
 * `ui/map/CLAUDE.md`), поэтому карта во весь экран и правда уходит под строку состояния и вырез
 * камеры. На других экранах фиксированные 31.dp сверху обходят только линейку масштаба, потому
 * что там карту уже опустил `Scaffold`; здесь тем же 31.dp приходится обходить ещё и сам вырез,
 * поэтому системный отступ прибавляется к ним.
 */
@Composable
fun FullScreenMapScaffold(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    map: @Composable (ornamentOptions: OrnamentOptions, bannerPadding: PaddingValues) -> Unit,
) {
    val topInset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()
    // По той же причине, что и topInset: `Scaffold`, который отодвинул бы содержимое от нижней
    // системной панели/жестовой полосы, здесь тоже нет, и плашка обязана учесть её сама.
    val bottomInset = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()

    Box(modifier = modifier.fillMaxSize()) {
        map(
            mapOrnamentOptions.copy(padding = PaddingValues(top = topInset)),
            PaddingValues(start = 16.dp, end = 16.dp, bottom = bottomInset + 16.dp),
        )

        // 31.dp обходят линейку масштаба, стоящую в этом же углу (см. mapOrnamentOptions) — то же
        // число и по той же причине, что у MapFilterButton в RecordScreen.kt/MapScreen.kt.
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = topInset + MAP_CHROME_TOP_OFFSET, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                tonalElevation = 4.dp,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            }
            action?.invoke()
        }
    }
}
