package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.presentation.species.FishSpeciesViewModel
import klev.fishing.map.ui.components.FishSectionScaffold
import klev.fishing.map.ui.components.RECORD_FISH_TILE_WIDTH
import klev.fishing.map.ui.components.SpeciesTile
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.CountryNames
import leshy.mushrooms.map.i18n.LocalAppLanguage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/** Потолок высоты списка стран в диалоге: их три десятка, а диалог не должен вырастать в экран. */
private val COUNTRY_LIST_MAX_HEIGHT = 420.dp

/**
 * Виды рыб плитками, касание — «в ленте»/«скрыт», плюс подборки по странам.
 *
 * Сетка адаптивная по ширине плитки, а не в фиксированное число столбцов: плитка вида имеет
 * собственную ширину под формат иллюстрации (2:1), и растягивать её под ширину экрана нельзя —
 * картинка поехала бы (правило проекта про потолок ширины у растров).
 */
@Composable
fun FishSpeciesScreen(
    onMenuClick: () -> Unit,
    viewModel: FishSpeciesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var countryPickerOpen by remember { mutableStateOf(false) }

    FishSectionScaffold(title = FishStringKey.SpeciesTitle, onMenuClick = onMenuClick) { padding ->
        LazyVerticalGrid(
            // FixedSize, а не Adaptive: у плитки есть собственная ширина под формат иллюстрации, и
            // растягивать её по ячейке нельзя — на планшете это дало бы картинку вдвое шире
            // исходника (правило проекта про потолок ширины у растров). Остаток ряда уходит в
            // центрирующую отбивку.
            columns = GridCells.FixedSize(RECORD_FISH_TILE_WIDTH),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CollectionCard(
                        appliedCountry = uiState.appliedCountry,
                        shownCount = uiState.shownCount,
                        totalCount = uiState.species.size,
                        onPickCountry = { countryPickerOpen = true },
                        onShowAll = viewModel::showAll,
                    )
                    Text(
                        text = fishStringResource(FishStringKey.SpeciesHint),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
            items(uiState.species, key = { it.id }) { species ->
                SpeciesTile(
                    species = species,
                    count = 0,
                    hidden = !species.isActive,
                    onClick = { viewModel.toggle(species.id) },
                )
            }
        }
    }

    if (countryPickerOpen) {
        CountryPickerDialog(
            codes = uiState.countryCodes,
            // Страна устройства — первой строкой: в девяти случаях из десяти это и есть нужная,
            // а листать три десятка названий ради неё незачем.
            preferredCode = uiState.deviceCountry,
            selectedCode = uiState.appliedCountry,
            onPick = {
                viewModel.applyCountryCollection(it)
                countryPickerOpen = false
            },
            onDismiss = { countryPickerOpen = false },
        )
    }
}

@Composable
private fun CollectionCard(
    appliedCountry: String?,
    shownCount: Int,
    totalCount: Int,
    onPickCountry: () -> Unit,
    onShowAll: () -> Unit,
) {
    val language = LocalAppLanguage.current
    val countryNames = koinInject<CountryNames>()
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = if (appliedCountry == null) {
                    fishStringResource(FishStringKey.SpeciesCollectionManual)
                } else {
                    "${fishStringResource(FishStringKey.SpeciesCollection)}: " +
                        countryDisplayName(appliedCountry, language, countryNames)
                },
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${fishStringResource(FishStringKey.SpeciesInStrip)}: $shownCount / $totalCount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onPickCountry) {
                    Text(fishStringResource(FishStringKey.SpeciesPickCountry))
                }
                if (shownCount < totalCount) {
                    TextButton(onClick = onShowAll) {
                        Text(fishStringResource(FishStringKey.SpeciesShowAll))
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryPickerDialog(
    codes: List<String>,
    preferredCode: String?,
    selectedCode: String?,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val language = LocalAppLanguage.current
    val countryNames = koinInject<CountryNames>()
    // Сортировка по показываемому имени, а не по коду: в русском «Австрия» и «Беларусь» стоят не
    // там, где AT и BY.
    val ordered = remember(codes, language, preferredCode) {
        val byName = codes.sortedBy { countryDisplayName(it, language, countryNames) }
        if (preferredCode == null) byName else listOf(preferredCode) + byName.filterNot { it == preferredCode }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(fishStringResource(FishStringKey.SpeciesCountryTitle)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = fishStringResource(FishStringKey.SpeciesCountryHint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LazyColumn(modifier = Modifier.heightIn(max = COUNTRY_LIST_MAX_HEIGHT)) {
                    items(ordered, key = { it }) { code ->
                        TextButton(
                            onClick = { onPick(code) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = countryDisplayName(code, language, countryNames),
                                style = if (code == selectedCode) {
                                    MaterialTheme.typography.titleMedium
                                } else {
                                    MaterialTheme.typography.bodyLarge
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(fishStringResource(FishStringKey.Cancel)) }
        },
    )
}

/**
 * Название страны на языке интерфейса. Таблица — общая с грибным приложением (`CountryNames` в
 * `:shared`, файлы `countries/<lang>.json`): имена стран одинаковы для обоих продуктов, и заводить
 * вторую копию ради рыбалки незачем. Своего файла у языка нет — остаётся английское имя, и только
 * если нет и его, код страны как есть.
 */
private fun countryDisplayName(code: String, language: AppLanguage, names: CountryNames): String =
    names.namesFor(language)[code] ?: names.namesFor(AppLanguage.EN)[code] ?: code
