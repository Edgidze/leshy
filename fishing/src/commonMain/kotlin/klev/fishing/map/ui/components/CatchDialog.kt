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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.LostReason
import klev.fishing.map.domain.model.MAX_CATCH_LENGTH_MM
import klev.fishing.map.domain.model.MAX_CATCH_WEIGHT_GRAMS
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import leshy.mushrooms.map.ui.components.DIALOG_HEIGHT_FRACTION
import leshy.mushrooms.map.ui.components.dialogWidth

/**
 * Запись одного экземпляра улова — тот самый экран, которого у грибов нет и быть не может.
 *
 * Обязательное поле здесь ровно одно — вид. Всё остальное (вес, длина, приманка, заметка) пустое по
 * умолчанию и остаётся пустым без единого упрёка: рыбу часто не взвешивают и почти никогда не
 * мерят, а дневник, который требует цифру, чтобы принять запись, просто не заполняют.
 *
 * Исход — три кнопки, и «Упущена» среди них равноправна. Поклёвка была, приманка сработала, место
 * рабочее; выбрасывать это из дневника значит терять половину того, ради чего он ведётся.
 */
@Composable
fun CatchDialog(
    species: List<FishSpecies>,
    preselectedSpeciesId: Long?,
    recentBaits: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        speciesId: Long,
        weightGrams: Int?,
        lengthMm: Int?,
        bait: String?,
        outcome: CatchOutcome,
        lostReason: LostReason?,
        note: String?,
    ) -> Unit,
) {
    var speciesId by remember { mutableStateOf(preselectedSpeciesId) }
    var weightText by remember { mutableStateOf("") }
    var lengthText by remember { mutableStateOf("") }
    var bait by remember { mutableStateOf("") }
    var outcome by remember { mutableStateOf(CatchOutcome.KEPT) }
    var lostReason by remember { mutableStateOf<LostReason?>(null) }
    var note by remember { mutableStateOf("") }

    val weightGrams = weightText.toIntOrNull()
    val lengthMm = lengthText.toDoubleOrNull()?.let { (it * 10).toInt() }
    val weightTooBig = weightGrams != null && weightGrams > MAX_CATCH_WEIGHT_GRAMS
    val lengthTooBig = lengthMm != null && lengthMm > MAX_CATCH_LENGTH_MM
    val canSave = speciesId != null && !weightTooBig && !lengthTooBig

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
                        text = fishStringResource(FishStringKey.CatchTitle),
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Column(
                        // weight(1f, fill = false) — иначе потолок высоты превращается в точную
                        // высоту и короткое содержимое добивается пустотой (см. DialogSizing).
                        modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = fishStringResource(FishStringKey.CatchSpeciesPick),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        ChipFlow {
                            species.forEach { item ->
                                FilterChip(
                                    selected = item.id == speciesId,
                                    onClick = { speciesId = item.id },
                                    label = { Text(speciesDisplayName(item)) },
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it.filter(Char::isDigit) },
                                label = { Text(fishStringResource(FishStringKey.CatchWeight)) },
                                suffix = { Text(fishStringResource(FishStringKey.UnitGram)) },
                                isError = weightTooBig,
                                supportingText = if (weightTooBig) {
                                    { Text(fishStringResource(FishStringKey.CatchWeightTooBig)) }
                                } else {
                                    null
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedTextField(
                                value = lengthText,
                                onValueChange = { text ->
                                    lengthText = text.filter { it.isDigit() || it == '.' || it == ',' }
                                        .replace(',', '.')
                                },
                                label = { Text(fishStringResource(FishStringKey.CatchLength)) },
                                suffix = { Text(fishStringResource(FishStringKey.UnitCentimeter)) },
                                isError = lengthTooBig,
                                supportingText = if (lengthTooBig) {
                                    { Text(fishStringResource(FishStringKey.CatchLengthTooBig)) }
                                } else {
                                    null
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        OutlinedTextField(
                            value = bait,
                            onValueChange = { bait = it },
                            label = { Text(fishStringResource(FishStringKey.CatchBait)) },
                            placeholder = { Text(fishStringResource(FishStringKey.CatchBaitHint)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (recentBaits.isNotEmpty()) {
                            // Подсказки из того, что пользователь уже вводил: имя набирается руками
                            // каждый раз, и одна опечатка молча плодит двойника в статистике.
                            Text(
                                text = fishStringResource(FishStringKey.CatchBaitRecent),
                                style = MaterialTheme.typography.labelSmall,
                            )
                            ChipFlow {
                                recentBaits.take(8).forEach { recent ->
                                    AssistChip(onClick = { bait = recent }, label = { Text(recent) })
                                }
                            }
                        }

                        Text(
                            text = fishStringResource(FishStringKey.CatchOutcome),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        ChipFlow {
                            CatchOutcome.entries.forEach { value ->
                                FilterChip(
                                    selected = value == outcome,
                                    onClick = {
                                        outcome = value
                                        if (value != CatchOutcome.LOST) lostReason = null
                                    },
                                    label = { Text(fishStringResource(value.labelKey())) },
                                )
                            }
                        }

                        if (outcome == CatchOutcome.LOST) {
                            Text(
                                text = fishStringResource(FishStringKey.CatchLostReason),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            ChipFlow {
                                LostReason.entries.forEach { reason ->
                                    FilterChip(
                                        selected = reason == lostReason,
                                        onClick = { lostReason = reason },
                                        label = { Text(fishStringResource(reason.labelKey())) },
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text(fishStringResource(FishStringKey.CatchNote)) },
                            placeholder = { Text(fishStringResource(FishStringKey.CatchNoteHint)) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = onDismiss) { Text(fishStringResource(FishStringKey.Cancel)) }
                        Button(
                            enabled = canSave,
                            onClick = {
                                val id = speciesId ?: return@Button
                                onSave(id, weightGrams, lengthMm, bait, outcome, lostReason, note)
                            },
                        ) {
                            Text(fishStringResource(FishStringKey.CatchSave))
                        }
                    }
                }
            }
        }
    }
}
