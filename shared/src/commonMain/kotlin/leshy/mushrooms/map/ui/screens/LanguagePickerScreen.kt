package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.searchOrdered

/**
 * Full-screen radio list of all 26 [AppLanguage] values, replacing the old
 * `SingleChoiceSegmentedButtonRow` in `SettingsScreen` now that there are 26 entries instead of 2
 * (`.claude/plans/countries-and-languages.md`, Phase 4). Tapping a row only moves the radio
 * selection locally — [onConfirm] (the checkmark in the top bar) is what actually applies it and
 * navigates back, [onBack] (the arrow) leaves without applying, same cancel/confirm split
 * `WalkDescriptionEditScreen` uses for its own two-icon `TopAppBar`. That split matters more here
 * than it did for the old segmented row: this list is long enough to scroll and search through
 * before landing on a choice, and an instant-apply tap-to-commit (like the segmented row had) would
 * flip the whole interface language on every exploratory tap.
 *
 * Search ranks with [searchOrdered] against `"${endonym} ${englishName}"`, so typing either the
 * language's own name ("Deutsch") or its English name ("German") finds it — necessary since the
 * interface might currently be in a third, unrelated language when this screen is opened.
 *
 * Re-ranking on every keystroke moves the best match to the top of the list, but doesn't move the
 * *scroll position* there — after scrolling down and then typing a query, the top match could land
 * back under the search field, off-screen. [listState] jumps back to the top on every [query]
 * change to keep the best match visible.
 */
@Composable
fun LanguagePickerScreen(currentLanguage: AppLanguage, onConfirm: (AppLanguage) -> Unit, onBack: () -> Unit) {
    var selected by remember { mutableStateOf(currentLanguage) }
    var query by remember { mutableStateOf("") }
    val filtered = searchOrdered(AppLanguage.entries, query) { "${it.endonym} ${it.englishName}" }
    val listState = rememberLazyListState()
    LaunchedEffect(query) { listState.scrollToItem(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(StringKey.SettingsLanguageTitle)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(StringKey.LanguagePickerBackContentDescription),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onConfirm(selected) }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(StringKey.LanguagePickerConfirmContentDescription),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(StringKey.LanguagePickerSearchHint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
            LazyColumn(state = listState) {
                items(filtered, key = { it.code }) { language ->
                    LanguageRow(
                        language = language,
                        selected = language == selected,
                        onClick = { selected = language },
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(language: AppLanguage, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(language.endonym)
            Text(
                text = language.englishName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
