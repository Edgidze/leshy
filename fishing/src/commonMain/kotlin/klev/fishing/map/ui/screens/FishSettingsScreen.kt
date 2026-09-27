package klev.fishing.map.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import klev.fishing.map.domain.model.FishingMethod
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import klev.fishing.map.presentation.settings.FishSettingsViewModel
import klev.fishing.map.ui.components.ChipFlow
import klev.fishing.map.ui.components.FishSectionScaffold
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.domain.model.ThemeMode
import org.koin.compose.viewmodel.koinViewModel

/**
 * Настройки блоками-карточками, а не одним свитком строк: блоков немного, и каждый отвечает на свой
 * вопрос («в чём мерить давление», «чем ловлю», «как выглядит», «на каком языке»). Плоский свиток из
 * заголовков и разделителей выглядел как список без начала и конца — в нём не было видно, где
 * кончается одна настройка и начинается другая.
 */
@Composable
fun FishSettingsScreen(
    onMenuClick: () -> Unit,
    viewModel: FishSettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FishSectionScaffold(title = FishStringKey.SettingsTitle, onMenuClick = onMenuClick) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsBlock(
                title = fishStringResource(FishStringKey.SettingsMethodsTitle),
                hint = fishStringResource(FishStringKey.SettingsMethodsHint),
            ) {
                ChipFlow {
                    FishingMethod.entries.forEach { method ->
                        FilterChip(
                            selected = method in uiState.methods,
                            onClick = { viewModel.toggleMethod(method) },
                            label = { Text(fishStringResource(method.labelKey())) },
                        )
                    }
                }
            }

            SettingsBlock(title = fishStringResource(FishStringKey.SettingsPressureUnit)) {
                ChipFlow {
                    PressureUnit.entries.forEach { unit ->
                        FilterChip(
                            selected = uiState.pressureUnit == unit,
                            onClick = { viewModel.setPressureUnit(unit) },
                            label = { Text(fishStringResource(unit.labelKey())) },
                        )
                    }
                }
            }

            SettingsBlock(title = fishStringResource(FishStringKey.SettingsTheme)) {
                // Порядок — светлая, системная, тёмная, то есть шкала, а не перечисление (разбор —
                // KDoc `ThemeMode` в `:shared`). Чипы, а не радиокнопки: три коротких варианта в
                // один ряд читаются целиком, а столбик радиокнопок занимал полэкрана.
                ChipFlow {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = uiState.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            label = { Text(fishStringResource(mode.labelKey())) },
                        )
                    }
                }
            }

            SettingsBlock(title = fishStringResource(FishStringKey.SettingsLanguage)) {
                // Только языки, на которые рыбацкий интерфейс переведён. Остальные сорок появятся
                // тут же, как только появятся их таблицы строк, — см. FishStrings.kt.
                ChipFlow {
                    listOf(AppLanguage.RU, AppLanguage.EN).forEach { language ->
                        FilterChip(
                            selected = uiState.language == language,
                            onClick = { viewModel.setLanguage(language) },
                            label = { Text(language.endonym) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsBlock(
    title: String,
    hint: String? = null,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            content()
        }
    }
}
