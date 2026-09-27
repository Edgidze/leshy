package klev.fishing.map.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import klev.fishing.map.domain.model.Cloudiness
import klev.fishing.map.domain.model.Precipitation
import klev.fishing.map.domain.model.PressureTrend
import klev.fishing.map.domain.model.PressureUnit
import klev.fishing.map.domain.model.TripWeather
import klev.fishing.map.domain.model.WindDirection
import klev.fishing.map.domain.model.hpaTo
import klev.fishing.map.domain.model.toHpaFrom
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import leshy.mushrooms.map.ui.components.DIALOG_HEIGHT_FRACTION
import leshy.mushrooms.map.ui.components.dialogWidth
import kotlin.math.roundToInt

/**
 * Погода на рыбалке. Заполняет человек; кнопка «Подсказать» лишь подставляет то, что о погоде
 * думает сеть, — решение владельца (2026-09-27). Разбор причины — в KDoc
 * `WeatherSuggestionSource`: модель знает узел сетки за час, рыбак знает своё место и свои минуты.
 *
 * Все поля необязательны. Половину рыбак не вспомнит, и это не повод не сохранить остальное —
 * поэтому «Сохранить» активна всегда, а «Не сейчас» закрывает диалог без потери самой рыбалки.
 */
@Composable
fun WeatherDialog(
    draft: TripWeather,
    pressureUnit: PressureUnit,
    suggesting: Boolean,
    suggestFailed: Boolean,
    onChange: ((TripWeather) -> TripWeather) -> Unit,
    onSuggest: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        BoxWithConstraints {
            val maxDialogHeight = maxHeight * DIALOG_HEIGHT_FRACTION
            Surface(
                modifier = Modifier.dialogWidth().imePadding(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.heightIn(max = maxDialogHeight).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = fishStringResource(FishStringKey.WeatherTitle),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = fishStringResource(FishStringKey.WeatherIntro),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Column(
                        modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NumberField(
                                label = fishStringResource(FishStringKey.WeatherAirTemp),
                                suffix = fishStringResource(FishStringKey.UnitCelsius),
                                value = draft.airTempC,
                                onValue = { v -> onChange { it.copy(airTempC = v) } },
                                allowNegative = true,
                                modifier = Modifier.weight(1f),
                            )
                            NumberField(
                                label = fishStringResource(FishStringKey.WeatherWaterTemp),
                                suffix = fishStringResource(FishStringKey.UnitCelsius),
                                value = draft.waterTempC,
                                onValue = { v -> onChange { it.copy(waterTempC = v) } },
                                allowNegative = false,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NumberField(
                                label = fishStringResource(FishStringKey.WeatherPressure),
                                suffix = when (pressureUnit) {
                                    PressureUnit.HPA -> fishStringResource(FishStringKey.SettingsPressureHpa)
                                    PressureUnit.MM_HG -> fishStringResource(FishStringKey.SettingsPressureMmHg)
                                },
                                // Показывается в выбранных единицах, в модель уходит всегда в гПа —
                                // иначе смена настройки переписала бы историю.
                                value = draft.pressureHpa?.hpaTo(pressureUnit),
                                onValue = { v -> onChange { it.copy(pressureHpa = v?.toHpaFrom(pressureUnit)) } },
                                allowNegative = false,
                                modifier = Modifier.weight(1f),
                            )
                            NumberField(
                                label = fishStringResource(FishStringKey.WeatherWind),
                                suffix = fishStringResource(FishStringKey.UnitMeterPerSecond),
                                value = draft.windSpeedMps,
                                onValue = { v -> onChange { it.copy(windSpeedMps = v) } },
                                allowNegative = false,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Label(FishStringKey.WeatherPressureTrend)
                        ChipFlow {
                            PressureTrend.entries.forEach { value ->
                                FilterChip(
                                    selected = draft.pressureTrend == value,
                                    onClick = {
                                        onChange { it.copy(pressureTrend = if (it.pressureTrend == value) null else value) }
                                    },
                                    label = { Text(fishStringResource(value.labelKey())) },
                                )
                            }
                        }

                        Label(FishStringKey.WeatherWindDirection)
                        ChipFlow {
                            WindDirection.entries.forEach { value ->
                                FilterChip(
                                    selected = draft.windDirection == value,
                                    onClick = {
                                        onChange { it.copy(windDirection = if (it.windDirection == value) null else value) }
                                    },
                                    label = { Text(fishStringResource(value.labelKey())) },
                                )
                            }
                        }

                        Label(FishStringKey.WeatherCloudiness)
                        ChipFlow {
                            Cloudiness.entries.forEach { value ->
                                FilterChip(
                                    selected = draft.cloudiness == value,
                                    onClick = {
                                        onChange { it.copy(cloudiness = if (it.cloudiness == value) null else value) }
                                    },
                                    label = { Text(fishStringResource(value.labelKey())) },
                                )
                            }
                        }

                        Label(FishStringKey.WeatherPrecipitation)
                        ChipFlow {
                            Precipitation.entries.forEach { value ->
                                FilterChip(
                                    selected = draft.precipitation == value,
                                    onClick = {
                                        onChange { it.copy(precipitation = if (it.precipitation == value) null else value) }
                                    },
                                    label = { Text(fishStringResource(value.labelKey())) },
                                )
                            }
                        }

                        if (suggestFailed) {
                            Text(
                                text = fishStringResource(FishStringKey.WeatherSuggestFailed),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(onClick = onSuggest, enabled = !suggesting) {
                            if (suggesting) {
                                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                                Text(fishStringResource(FishStringKey.WeatherSuggesting))
                            } else {
                                Text(fishStringResource(FishStringKey.WeatherSuggest))
                            }
                        }
                        TextButton(onClick = onDismiss) { Text(fishStringResource(FishStringKey.WeatherSkip)) }
                        Button(onClick = onSave) { Text(fishStringResource(FishStringKey.Save)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(key: FishStringKey) {
    Text(text = fishStringResource(key), style = MaterialTheme.typography.labelLarge)
}

/**
 * Числовое поле с необязательным значением. Пустая строка — это `null`, а не ноль: «не мерил» и
 * «ноль градусов» — разные факты, и путать их в дневнике нельзя.
 */
@Composable
private fun NumberField(
    label: String,
    suffix: String,
    value: Double?,
    onValue: (Double?) -> Unit,
    allowNegative: Boolean,
    modifier: Modifier = Modifier,
) {
    // Текст поля — своё состояние: иначе «12.» и «-» невозможно набрать, они не парсятся в Double
    // и значение отскакивало бы назад на каждом символе.
    var text by remember(value == null) {
        mutableStateOf(value?.let { formatShort(it) } ?: "")
    }
    OutlinedTextField(
        value = text,
        onValueChange = { raw ->
            val filtered = raw.filter { it.isDigit() || it == '.' || it == ',' || (allowNegative && it == '-') }
                .replace(',', '.')
            text = filtered
            onValue(filtered.toDoubleOrNull())
        },
        label = { Text(label) },
        suffix = { Text(suffix) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
    )
}

private fun formatShort(value: Double): String {
    val rounded = (value * 10).roundToInt() / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}
