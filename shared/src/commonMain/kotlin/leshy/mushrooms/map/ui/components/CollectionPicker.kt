package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.LocalAppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.collectionDisplayName
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.CollectionPickState
import leshy.mushrooms.map.presentation.CollectionPickerItem
import leshy.mushrooms.map.presentation.searchOrdered
import leshy.mushrooms.map.presentation.sortCategories

/**
 * Expandable per-collection checklist: a tri-state checkbox toggles every member species at once,
 * expanding a collection shows its members as individually-checkable rows — plain icon + name
 * beside it, same shape as the Filter screen's `SpeciesFilterRow` (`MapFilterDialog.kt`), not the
 * label-baked-into-the-photo look [MushroomPhoto] uses on Record's tiles. Shared by the "Грибы"
 * screen and the first-run onboarding screen (`.claude/plans/countries-and-languages.md`, Phase 3;
 * originally `mushroom-collections.md`, Phases 1/3) — this composable owns no state of its own
 * beyond which sections are expanded and the search query; [items] and the two callbacks are the
 * single source of truth for picking.
 *
 * All 45 country sections start collapsed by default (each section's own `expanded` state, below) —
 * with that many collections the search field is the primary way to find one, not scrolling.
 *
 * **The search field looks up both countries and single species** ("Поиск страны или гриба"):
 * matching countries come first as the same collapsed section headers, then the matching species
 * as standalone rows, identical to the rows inside an expanded section and toggling the very same
 * [Category] (a species picked from a search result is picked everywhere it is a member). Species
 * search exists because a species' presence in the catalog and its presence in *the user's*
 * country's preset are two different things: a mushroom named in the user's language may live only
 * in some other country's preset, and without name search there is no way to reach it short of
 * expanding countries one by one. Every one of the 408 catalog species belongs to at least one
 * country preset, so the union of [items]' members is the whole catalog — no extra data source is
 * needed here.
 *
 * Country filtering is a plain case-insensitive substring match against the resolved country name;
 * species matches are the same substring match, [searchOrdered]-ranked afterwards (prefix matches
 * first) on top of the alphabetical [sortCategories] order, because a species query has far more
 * hits to order than a country one.
 *
 * **Оба хоста ОБЯЗАНЫ отдавать этому пикеру область, которая кончается над клавиатурой** — сам он
 * прокруткой не владеет и клавиатуру подвинуть не может. «Мои грибы» делают это `imePadding()`
 * (`SpeciesScreen`), онбординг — `windowInsetsPadding(WindowInsets.safeDrawing)`, который на iOS
 * включает `ime`. Спасать поле больше некому: на iOS сдвиг сцены к сфокусированному полю снят
 * (`OnFocusBehavior.DoNothing` в `MainViewController.kt` — он уводил под статус-бар шапку экрана,
 * репорт с iPhone SE 2026-09-07), и поле, оставленное под клавиатурой, так под ней и останется.
 */
@Composable
fun CollectionPicker(
    items: List<CollectionPickerItem>,
    onToggleCollection: (CollectionPickerItem) -> Unit,
    onToggleCategory: (Category, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val searchFieldPosition = remember { BringIntoViewRequester() }
    val language = LocalAppLanguage.current
    val trimmedQuery = query.trim()
    val filteredItems = if (trimmedQuery.isEmpty()) {
        items
    } else {
        items.filter {
            collectionDisplayName(it.collection.nameKey, language).contains(trimmedQuery, ignoreCase = true)
        }
    }
    val matchedSpecies = remember(items, trimmedQuery, language) {
        if (trimmedQuery.isEmpty()) {
            emptyList()
        } else {
            val everySpecies = items.flatMap { it.members }.distinctBy { it.id }
            val matched = everySpecies.filter {
                categoryDisplayName(it, language).contains(trimmedQuery, ignoreCase = true)
            }
            searchOrdered(sortCategories(matched, language), trimmedQuery) { categoryDisplayName(it, language) }
        }
    }

    // Держим поле поиска у верхней кромки, пока оно в фокусе: иначе на коротком экране клавиатура
    // закрывает как раз те строки, ради которых поиск и набирают. Запрашивается не прямоугольник
    // поля, а полоса от его верха вниз на всю высоту окна — `BringIntoViewSpec` по умолчанию
    // прокручивает на минимум, достаточный, чтобы прямоугольник стал видимым, и для полосы выше
    // окна прокрутки этот минимум — совместить её ВЕРХ с верхом окна. Прямоугольник размером с
    // само поле он бы вместо этого «подтянул» к нижней кромке, оставив список под клавиатурой.
    // Второй заход — по появлению клавиатуры: в момент фокуса окно прокрутки ещё во всю высоту,
    // после `imePadding` (см. `SpeciesScreen`) его надо выровнять заново.
    val windowHeightPx = LocalWindowInfo.current.containerSize.height.toFloat()
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(isSearchFocused, isKeyboardVisible) {
        if (isSearchFocused) {
            searchFieldPosition.bringIntoView(Rect(left = 0f, top = 0f, right = 1f, bottom = windowHeightPx))
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(stringResource(StringKey.CollectionPickerSearchHint)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .bringIntoViewRequester(searchFieldPosition)
                .onFocusChanged { isSearchFocused = it.isFocused },
        )
        filteredItems.forEach { item ->
            CollectionPickerSection(
                item = item,
                onToggleCollection = onToggleCollection,
                onToggleCategory = onToggleCategory,
            )
        }
        if (matchedSpecies.isNotEmpty()) {
            if (filteredItems.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            matchedSpecies.take(SPECIES_RESULT_LIMIT).forEach { category ->
                SpeciesPickRow(
                    category = category,
                    onToggleCategory = onToggleCategory,
                    startPadding = 0.dp,
                )
            }
            if (matchedSpecies.size > SPECIES_RESULT_LIMIT) {
                Text(
                    text = stringResource(StringKey.CollectionPickerMoreMatches),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

/**
 * Cap on species rows a search shows at once. The picker sits inside a plain `verticalScroll`
 * column, not a `LazyColumn` (both hosts put other content around it), so every row it emits is
 * composed and every catalog illustration in it decoded up front — a one-letter query matches most
 * of the 408 species, and several hundred ~240×200 bitmaps at once is tens of megabytes. Anything
 * past the cap is reported by [StringKey.CollectionPickerMoreMatches] rather than silently dropped.
 */
private const val SPECIES_RESULT_LIMIT = 50

@Composable
private fun CollectionPickerSection(
    item: CollectionPickerItem,
    onToggleCollection: (CollectionPickerItem) -> Unit,
    onToggleCategory: (Category, Boolean) -> Unit,
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
                state = when (item.pickState) {
                    CollectionPickState.ALL -> ToggleableState.On
                    CollectionPickState.NONE -> ToggleableState.Off
                    CollectionPickState.SOME -> ToggleableState.Indeterminate
                },
                onClick = { onToggleCollection(item) },
            )
            Text(
                text = collectionDisplayName(item.collection.nameKey),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
            )
        }

        if (expanded) {
            item.members.forEach { category ->
                SpeciesPickRow(
                    category = category,
                    onToggleCategory = onToggleCategory,
                    startPadding = 24.dp,
                )
            }
        }
    }
}

/**
 * One species with its checkbox — a member row under an expanded country ([startPadding] indents it
 * under the section header) and a search hit standing on its own (no indent, nothing to indent
 * under) are the same row deliberately: the user picks a species the same way in both places.
 */
@Composable
private fun SpeciesPickRow(
    category: Category,
    onToggleCategory: (Category, Boolean) -> Unit,
    startPadding: Dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = category.isPicked,
                onValueChange = { onToggleCategory(category, it) },
                role = Role.Checkbox,
            )
            .padding(start = startPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = category.isPicked, onCheckedChange = null)
        CategoryIcon(category = category, modifier = Modifier.size(56.dp).padding(start = 8.dp))
        Text(
            text = categoryDisplayName(category),
            modifier = Modifier.weight(1f).padding(start = 12.dp),
        )
    }
}
