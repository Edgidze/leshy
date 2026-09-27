package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.presentation.settings.FishSettingsViewModel
import klev.fishing.map.ui.components.ChipFlow
import klev.fishing.map.ui.components.speciesDisplayName
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FishSettingsScreen(viewModel: FishSettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(fishStringResource(FishStringKey.SettingsPressureUnit), style = MaterialTheme.typography.titleMedium)
        ChipFlow {
            PressureUnit.entries.forEach { unit ->
                FilterChip(
                    selected = uiState.pressureUnit == unit,
                    onClick = { viewModel.setPressureUnit(unit) },
                    label = {
                        Text(
                            when (unit) {
                                PressureUnit.HPA -> fishStringResource(FishStringKey.SettingsPressureHpa)
                                PressureUnit.MM_HG -> fishStringResource(FishStringKey.SettingsPressureMmHg)
                            }
                        )
                    },
                )
            }
        }

        HorizontalDivider()

        Text(fishStringResource(FishStringKey.SettingsTheme), style = MaterialTheme.typography.titleMedium)
        // Порядок вариантов — как у грибов: светлая, системная, тёмная, то есть шкала, а не
        // перечисление (разбор — KDoc `ThemeMode` в `:shared`).
        ThemeMode.entries.forEach { mode ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = uiState.themeMode == mode, onClick = { viewModel.setThemeMode(mode) })
                Text(mode.name)
            }
        }

        HorizontalDivider()

        Text(fishStringResource(FishStringKey.SettingsLanguage), style = MaterialTheme.typography.titleMedium)
        // Только языки, на которые рыбацкий интерфейс переведён. Остальные сорок появятся тут же,
        // как только появятся их таблицы строк, — см. FishStrings.kt.
        ChipFlow {
            listOf(AppLanguage.RU, AppLanguage.EN).forEach { language ->
                FilterChip(
                    selected = uiState.language == language,
                    onClick = { viewModel.setLanguage(language) },
                    label = { Text(language.endonym) },
                )
            }
        }

        HorizontalDivider()

        Text(fishStringResource(FishStringKey.SettingsSpeciesTitle), style = MaterialTheme.typography.titleMedium)
        Text(fishStringResource(FishStringKey.SettingsSpeciesHint), style = MaterialTheme.typography.bodySmall)
        uiState.species.forEach { species ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = species.isActive,
                    onCheckedChange = { viewModel.setSpeciesActive(species.id, it) },
                )
                Text(speciesDisplayName(species))
            }
        }
    }
}
