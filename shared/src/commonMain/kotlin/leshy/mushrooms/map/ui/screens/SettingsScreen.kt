package leshy.mushrooms.map.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MAX
import leshy.mushrooms.map.domain.model.MUSHROOM_MARKER_SIZE_SCALE_MIN
import leshy.mushrooms.map.domain.model.ThemeMode
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.regionsUnitLabel
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.settings.SettingsViewModel
import leshy.mushrooms.map.ui.components.CategoryIcon
import leshy.mushrooms.map.ui.components.LeshyButton
import leshy.mushrooms.map.ui.components.PrivacyPolicyLink
import leshy.mushrooms.map.ui.map.MUSHROOM_MARKER_BASE_SIZE
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onLanguageClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLanguageClick)
                .padding(top = 24.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${stringResource(StringKey.SettingsLanguageTitle)} → ${uiState.language.endonym}",
                modifier = Modifier.weight(1f),
            )
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }

        SettingsSectionTitle(stringResource(StringKey.SettingsThemeTitle))
        // `selectableGroup` — не декорация: без него скринридер читает три отдельных переключателя
        // вместо одной группы «вариант 2 из 3», и это единственное, чем радиогруппа отличается от
        // трёх независимых строк с точки зрения доступности.
        Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
            ThemeMode.entries.forEach { mode ->
                ThemeModeOption(
                    mode = mode,
                    selected = uiState.themeMode == mode,
                    onSelect = { viewModel.setThemeMode(mode) },
                )
            }
        }

        SettingsSectionTitle(stringResource(StringKey.SettingsMushroomSizeTitle))
        MushroomMarkerSizeSlider(
            scale = uiState.mushroomMarkerSizeScale,
            previewCategory = uiState.previewCategory,
            onScaleChangeFinished = viewModel::setMushroomMarkerSizeScale,
        )

        SettingsSectionTitle(stringResource(StringKey.SettingsMushroomSortTitle))
        FreezeMushroomOrderOption(
            checked = uiState.freezeMushroomOrder,
            onCheckedChange = viewModel::setFreezeMushroomOrder,
        )
        ResetMushroomOrderOnWalkFinishOption(
            checked = uiState.resetMushroomOrderOnWalkFinish,
            onCheckedChange = viewModel::setResetMushroomOrderOnWalkFinish,
        )

        SettingsSectionTitle(stringResource(StringKey.SettingsMapDataTitle))
        LeshyButton(
            onClick = viewModel::onUpdateMapDataClick,
            enabled = !uiState.isRefreshingMapData,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            if (uiState.isRefreshingMapData) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(StringKey.SettingsRefreshMapDataButton))
            }
        }
        if (uiState.mapDataRefreshFailed) {
            Text(
                stringResource(StringKey.SettingsMapDataRefreshError),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (uiState.mapDataRegionsRedownloading > 0) {
            val count = uiState.mapDataRegionsRedownloading
            Text(
                "${stringResource(StringKey.SettingsMapDataRedownloadingPrefix)} " +
                    "$count ${regionsUnitLabel(count)} " +
                    stringResource(StringKey.SettingsMapDataRedownloadingSuffix),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        LeshyButton(
            onClick = viewModel::onClearMapCacheClick,
            enabled = !uiState.isClearingMapCache,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            if (uiState.isClearingMapCache) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(stringResource(StringKey.SettingsClearMapCacheButton))
            }
        }
        if (uiState.mapCacheCleared) {
            Text(
                stringResource(StringKey.SettingsMapCacheCleared),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        // Вторая (и после первого запуска — единственная) точка входа к публичной политике
        // конфиденциальности: Google требует, чтобы ссылка была достижима ИЗ приложения, а экран
        // онбординга показывается один раз за установку. Подробности — `ui/components/PrivacyPolicyLink.kt`.
        PrivacyPolicyLink(modifier = Modifier.padding(top = 32.dp))

        // «О приложении» — версия, атрибуция карты и лицензии зависимостей. Последней строкой и
        // отдельным экраном: тексты лицензий обязаны доехать до пользователя (см. `AboutScreen`),
        // но никто не должен на них натыкаться, листая настройки.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAboutClick)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(StringKey.AboutTitle), modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }

    if (uiState.showUpdateMapDataConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::onUpdateMapDataDismiss,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = { Text(stringResource(StringKey.SettingsMapDataUpdateConfirmTitle)) },
            text = { Text(stringResource(StringKey.SettingsMapDataUpdateConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onUpdateMapDataConfirm) {
                    Text(stringResource(StringKey.SettingsMapDataUpdateConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onUpdateMapDataDismiss) {
                    Text(stringResource(StringKey.SettingsMapDataUpdateConfirmNo))
                }
            },
        )
    }

    if (uiState.showClearMapCacheConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::onClearMapCacheDismiss,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = { Text(stringResource(StringKey.SettingsClearMapCacheConfirmTitle)) },
            text = { Text(stringResource(StringKey.SettingsClearMapCacheConfirmMessage)) },
            confirmButton = {
                TextButton(onClick = viewModel::onClearMapCacheConfirm) {
                    Text(stringResource(StringKey.SettingsClearMapCacheConfirmYes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onClearMapCacheDismiss) {
                    Text(stringResource(StringKey.SettingsClearMapCacheConfirmNo))
                }
            },
        )
    }
}

/** Section header used throughout Settings — underlined so subsections read as distinct groups
 * while scrolling a screen that's otherwise plain text and controls with no card/divider chrome. */
@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
    )
}

/**
 * Вариант оформления строкой с радиокнопкой. Раньше три варианта стояли одним
 * `SingleChoiceSegmentedButtonRow` — ряд из трёх кнопок делит ширину экрана на три и вынуждает
 * подбирать названия под ширину кнопки, а не под смысл; на узком экране с крупным системным
 * шрифтом подпись в такой кнопке всё равно обрезается. Три строки места стоят дёшево, а читаются
 * одинаково при любом размере шрифта.
 *
 * Нажимается вся строка (`selectable` на `Row`, у самой `RadioButton` обработчик снят) — тот же
 * приём, что у галочек ниже: цель шириной в строку попадается пальцем надёжнее, чем кружок 20 dp.
 */
@Composable
private fun ThemeModeOption(mode: ThemeMode, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = stringResource(
                when (mode) {
                    ThemeMode.LIGHT -> StringKey.SettingsThemeLight
                    ThemeMode.SYSTEM -> StringKey.SettingsThemeSystem
                    ThemeMode.DARK -> StringKey.SettingsThemeDark
                },
            ),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun FreezeMushroomOrderOption(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox)
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(
            text = stringResource(StringKey.SettingsFreezeMushroomOrder),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun ResetMushroomOrderOnWalkFinishOption(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox)
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(
            text = stringResource(StringKey.SettingsResetMushroomOrderOnWalkFinish),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

/**
 * A single-thumb [Slider] plus a preview photo below it that's resized live as the thumb is
 * dragged — `sliderValue` (not [scale]) drives the preview, so it tracks the drag with zero lag;
 * [scale] only commits to the [SettingsViewModel] (and from there, `DataStore`) once the drag ends,
 * same "local slider state + `onValueChangeFinished`" pattern `MapFilterDialog`'s range sliders use.
 */
@Composable
private fun MushroomMarkerSizeSlider(
    scale: Float,
    previewCategory: Category?,
    onScaleChangeFinished: (Float) -> Unit,
) {
    var sliderValue by remember(scale) { mutableStateOf(scale) }

    Slider(
        value = sliderValue,
        onValueChange = { sliderValue = it },
        valueRange = MUSHROOM_MARKER_SIZE_SCALE_MIN..MUSHROOM_MARKER_SIZE_SCALE_MAX,
        onValueChangeFinished = { onScaleChangeFinished(sliderValue) },
        modifier = Modifier.fillMaxWidth(),
        // Material3's current default track draws a gap around the thumb plus a stop-indicator
        // dot at the end — reads as a broken/disconnected track for a plain 0..1 scale slider
        // with no steps, so both are turned off for a classic continuous look.
        track = { sliderState ->
            SliderDefaults.Track(sliderState = sliderState, drawStopIndicator = null, thumbTrackGapSize = 0.dp)
        },
    )

    if (previewCategory != null) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            CategoryIcon(
                category = previewCategory,
                modifier = Modifier.size(MUSHROOM_MARKER_BASE_SIZE * sliderValue),
                contentDescription = categoryDisplayName(previewCategory),
            )
        }
    }
}
