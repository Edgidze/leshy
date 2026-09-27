package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.archive.ArchiveRow
import klev.fishing.map.presentation.archive.FishArchiveViewModel
import klev.fishing.map.ui.components.ChipFlow
import klev.fishing.map.ui.components.FishSectionScaffold
import klev.fishing.map.ui.components.formatPressure
import klev.fishing.map.ui.components.formatWeight
import klev.fishing.map.ui.components.formatWind
import klev.fishing.map.ui.components.speciesDisplayName
import leshy.mushrooms.map.ui.util.formatDateTime
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import leshy.mushrooms.map.ui.util.parseHexColor
import org.koin.compose.viewmodel.koinViewModel

/** Сколько видов показывать в строке архива. Дальше строка перестаёт читаться с одного взгляда. */
private const val ARCHIVE_SPECIES_SHOWN = 4

/** Кружок цвета вида рядом с его именем. */
private val SPECIES_DOT_SIZE = 10.dp

/**
 * Архив выездов.
 *
 * **Строка отвечает на вопрос «что это был за выезд», а не «сколько штук».** Раньше в ней стояло
 * «Поймано: 6» — число, которым одна рыбалка не отличается от другой. Теперь видно, КОГО ловили
 * (виды с количеством, цветом вида), сколько сошло и какая была погода: именно по этому выезд и
 * вспоминают, а дневник ведут ради «что работало».
 */
@Composable
fun FishArchiveScreen(
    onMenuClick: () -> Unit,
    onTripClick: (Long) -> Unit,
    viewModel: FishArchiveViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FishSectionScaffold(title = FishStringKey.ArchiveTitle, onMenuClick = onMenuClick) { padding ->
        if (uiState.rows.isEmpty() && !uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(fishStringResource(FishStringKey.ArchiveEmpty), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = fishStringResource(FishStringKey.ArchiveEmptyHint),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            return@FishSectionScaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(uiState.rows, key = { it.trip.id }) { row ->
                TripRow(
                    row = row,
                    pressureUnit = uiState.pressureUnit,
                    onClick = { onTripClick(row.trip.id) },
                )
            }
        }
    }
}

@Composable
private fun TripRow(row: ArchiveRow, pressureUnit: PressureUnit, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Безымянный выезд озаглавлен ДАТОЙ, а не словом «Рыбалка»: имени у большинства
            // выездов не будет никогда (его вводят руками), и список из одинаковых заголовков не
            // отличает одну строку от другой ничем.
            val named = row.trip.title?.takeIf { it.isNotBlank() }
                ?: row.trip.waterBody?.takeIf { it.isNotBlank() }
            Text(
                text = named ?: formatDateTime(row.trip.startedAt),
                style = MaterialTheme.typography.titleMedium,
            )
            // Способ, длительность и путь — одной строкой через точку: это подпись к выезду, а не
            // данные, которые сравнивают между строками, и колонки им ни к чему.
            val facts = buildList {
                if (named != null) add(formatDateTime(row.trip.startedAt))
                add(fishStringResource(row.trip.method.labelKey()))
                row.trip.durationMillis?.let { add(formatDurationShort(it)) }
                if (row.trip.distanceMeters > 0) add(formatDistanceKm(row.trip.distanceMeters))
            }
            Text(
                text = facts.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (row.speciesCounts.isEmpty() && row.lostCount == 0) {
                Text(
                    text = fishStringResource(FishStringKey.ArchiveCatchesNone),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                ChipFlow {
                    row.speciesCounts.take(ARCHIVE_SPECIES_SHOWN).forEach { entry ->
                        SpeciesCountLabel(
                            name = speciesDisplayName(entry.species),
                            colorHex = entry.species.colorHex,
                            count = entry.count,
                        )
                    }
                    val hidden = row.speciesCounts.size - ARCHIVE_SPECIES_SHOWN
                    if (hidden > 0) {
                        Text(
                            text = "+$hidden",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                val tail = buildList {
                    if (row.totalWeightGrams > 0) add(formatWeight(row.totalWeightGrams))
                    if (row.lostCount > 0) {
                        add("${fishStringResource(FishStringKey.CatchOutcomeLost)}: ${row.lostCount}")
                    }
                }
                if (tail.isNotEmpty()) {
                    Text(
                        text = tail.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Погода — та самая причина, по которой дневник и ведут («что клевало при падающем
            // давлении»). В строке от неё только давление с тенденцией и ветер: остальное читается
            // на экране выезда.
            val weather = row.trip.weather
            if (weather != null) {
                val parts = buildList {
                    weather.pressureHpa?.let { add(formatPressure(it, pressureUnit)) }
                    weather.pressureTrend?.let { add(fishStringResource(it.labelKey())) }
                    weather.windSpeedMps?.let { add(formatWind(it)) }
                    weather.windDirection?.let { add(fishStringResource(it.labelKey())) }
                }
                if (parts.isNotEmpty()) {
                    Text(
                        text = parts.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeciesCountLabel(name: String, colorHex: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        // Кружок цвета вида — тот же цвет, которым вид нарисован на плитке и которым стоит его
        // отметка на карте. Одна рыба опознаётся по цвету во всех трёх местах.
        Box(Modifier.size(SPECIES_DOT_SIZE).clip(CircleShape).background(parseHexColor(colorHex)))
        Text(text = "$name × $count", style = MaterialTheme.typography.bodyMedium)
    }
}
