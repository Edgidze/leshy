package klev.fishing.map.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.summary.FishSummaryViewModel
import klev.fishing.map.ui.components.FishSectionScaffold
import klev.fishing.map.ui.components.formatWeight
import klev.fishing.map.ui.components.speciesDisplayName
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

/** Высота столбиков часовой гистограммы. Больше не нужно: она сравнивает, а не измеряет. */
private val HOUR_CHART_HEIGHT = 72.dp

/** Скругление верхушки столбика. */
private val BAR_CORNER = 4.dp

/** Высота полоски в строке приманки или вида. */
private val ROW_BAR_HEIGHT = 8.dp

/** Кружок цвета вида — тот же приём, что в архиве и на карте. */
private val SPECIES_DOT_SIZE = 10.dp

/**
 * «Итоги» — разбор записанного.
 *
 * **Графика здесь одноцветная и одномерная, и это не бедность.** Всё, что показано, — это величины
 * одного рода (сколько рыбы), поэтому цвет несёт ровно одно значение — «это данные», — а различает
 * столбики длина. Единственное исключение — список видов: там цвет принадлежит самой рыбе и
 * совпадает с её цветом на плитке и на карте, то есть опять-таки не кодирует ничего нового.
 *
 * Легенд нет ни у одной картинки: ряд везде один, и его называет заголовок блока.
 */
@Composable
fun FishSummaryScreen(
    onMenuClick: () -> Unit,
    viewModel: FishSummaryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FishSectionScaffold(title = FishStringKey.SummaryTitle, onMenuClick = onMenuClick) { padding ->
        if (!uiState.hasData && !uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(fishStringResource(FishStringKey.SummaryEmpty), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = fishStringResource(FishStringKey.SummaryEmptyHint),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            return@FishSectionScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Три числа-заголовка: их читают первыми и не сравнивают между собой, поэтому это
            // именно числа, а не диаграмма из трёх столбиков.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatTile(
                    label = fishStringResource(FishStringKey.SummaryTrips),
                    value = uiState.tripCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = fishStringResource(FishStringKey.SummaryFish),
                    value = uiState.catchCount.toString(),
                    modifier = Modifier.weight(1f),
                )
                if (uiState.totalWeightGrams > 0) {
                    StatTile(
                        label = fishStringResource(FishStringKey.SummaryWeight),
                        value = formatWeight(uiState.totalWeightGrams),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            uiState.best?.let { best ->
                val weight = best.item.weightGrams
                if (weight != null) {
                    SummaryBlock(fishStringResource(FishStringKey.SummaryBest)) {
                        Text(
                            text = best.species?.let { speciesDisplayName(it) }.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "${formatWeight(weight)} · ${formatDateTime(best.item.timestamp)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            if (uiState.baits.isNotEmpty()) {
                SummaryBlock(fishStringResource(FishStringKey.SummaryBaits)) {
                    val max = uiState.baits.maxOf { it.total }
                    uiState.baits.forEach { stat ->
                        BarRow(
                            label = stat.bait ?: fishStringResource(FishStringKey.SummaryBaitNone),
                            value = stat.total.toString() +
                                if (stat.lost > 0) {
                                    " · ${fishStringResource(FishStringKey.SummaryLost)} ${stat.lost}"
                                } else {
                                    ""
                                },
                            fraction = stat.total.toFloat() / max,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            if (uiState.species.isNotEmpty()) {
                SummaryBlock(fishStringResource(FishStringKey.SummarySpecies)) {
                    val max = uiState.species.maxOf { it.count }
                    uiState.species.forEach { stat ->
                        BarRow(
                            label = speciesDisplayName(stat.species),
                            value = stat.count.toString(),
                            fraction = stat.count.toFloat() / max,
                            color = parseHexColor(stat.species.colorHex),
                            dotColor = parseHexColor(stat.species.colorHex),
                        )
                    }
                }
            }

            if (uiState.byHour.any { it > 0 }) {
                SummaryBlock(fishStringResource(FishStringKey.SummaryHours)) {
                    Text(
                        text = fishStringResource(FishStringKey.SummaryHoursHint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HourChart(counts = uiState.byHour)
                }
            }

            if (uiState.byPressureTrend.isNotEmpty()) {
                SummaryBlock(fishStringResource(FishStringKey.SummaryPressure)) {
                    val max = uiState.byPressureTrend.values.max()
                    // Порядок — шкала (растёт → ровно → падает), а не по убыванию числа: так строки
                    // стоят на одном месте от выезда к выезду и сравниваются глазом.
                    PressureTrend.entries.forEach { trend ->
                        val count = uiState.byPressureTrend[trend] ?: 0
                        BarRow(
                            label = fishStringResource(trend.labelKey()),
                            value = count.toString(),
                            fraction = if (max == 0) 0f else count.toFloat() / max,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SummaryBlock(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

/**
 * Строка «название — полоска — число». Полоска доля от наибольшего в своём блоке: сравниваются
 * строки между собой, а не с абсолютной шкалой, которой у «сколько рыбы на приманку» и нет.
 *
 * Число стоит текстом рядом, а не подписью на полоске: полоска короткой строки уже самой подписи, и
 * цифра на ней не помещается.
 */
@Composable
private fun BarRow(
    label: String,
    value: String,
    fraction: Float,
    color: Color,
    dotColor: Color? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (dotColor != null) {
                Box(Modifier.size(SPECIES_DOT_SIZE).clip(CircleShape).background(dotColor))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_BAR_HEIGHT)
                .clip(RoundedCornerShape(BAR_CORNER))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(BAR_CORNER))
                    .background(color),
            )
        }
    }
}

/**
 * Поклёвки по часам суток.
 *
 * Двадцать четыре столбика одного цвета: сравнивается высота, а не оттенок. Подписаны только 0, 6,
 * 12 и 18 — подпись у каждого часа превращает ось в сплошную полосу цифр, которую никто не читает.
 * Высота столбика — доля от самого высокого часа: абсолютной шкалы у «числа поклёвок» нет, а
 * вопрос, на который блок отвечает, — «когда брало ЧАЩЕ».
 */
@Composable
private fun HourChart(counts: List<Int>) {
    val max = counts.maxOrNull() ?: 0
    if (max == 0) return
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(HOUR_CHART_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            counts.forEach { count ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        // Пустой час — тонкая полоска у основания, а не пустота: иначе в ряду
                        // появляются дыры, и соседние часы кажутся соседними по времени, хотя между
                        // ними пропущен целый час.
                        .fillMaxHeight(if (count == 0) 0.04f else count.toFloat() / max)
                        .clip(RoundedCornerShape(topStart = BAR_CORNER, topEnd = BAR_CORNER))
                        .background(
                            if (count == 0) {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        ),
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            // По подписи на каждую четверть суток: каждая занимает свою четверть ширины и
            // прижата к её началу — ровно под первым столбиком этой четверти.
            listOf(0, 6, 12, 18).forEach { hour ->
                Text(
                    text = hour.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}
