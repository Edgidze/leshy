package klev.fishing.map.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import klev.fishing.map.domain.model.Catch
import klev.fishing.map.domain.model.CatchOutcome
import klev.fishing.map.domain.model.FishSpecies
import klev.fishing.map.domain.model.LostReason
import klev.fishing.map.domain.model.MAX_CATCH_DEPTH_CM
import klev.fishing.map.domain.model.MAX_CATCH_LENGTH_MM
import klev.fishing.map.domain.model.MAX_CATCH_WEIGHT_GRAMS
import klev.fishing.map.i18n.FishStringKey
import klev.fishing.map.i18n.fishStringResource
import klev.fishing.map.i18n.labelKey
import leshy.mushrooms.map.ui.util.formatTimeOnly
import kotlin.math.roundToInt

/** Шаг кнопок «−»/«+» у веса: 50 г — мельче не нужно, крупнее не хватит окуню. */
private const val WEIGHT_STEP_GRAMS = 50

/** Шаг у длины — сантиметр. В базе миллиметры, поэтому 10. */
private const val LENGTH_STEP_MM = 10

/** Шаг у глубины — полметра: глубину называют «три с половиной», а не «три сорок восемь». */
private const val DEPTH_STEP_CM = 50

/** Сколько недавних приманок показывать чипами. Дальше ряд перестаёт читаться, а память — помогать. */
private const val RECENT_BAITS_SHOWN = 8

/** Значения одного улова, которые вводит человек. Координату и время приложение знает само. */
data class CatchDraft(
    val speciesId: Long,
    val weightGrams: Int?,
    val lengthMm: Int?,
    val depthCm: Int?,
    val bait: String?,
    val outcome: CatchOutcome,
    val lostReason: LostReason?,
    val note: String?,
)

/**
 * Одна рыба — нижний лист, а не диалог по центру экрана.
 *
 * **Почему лист.** Рыбалка — это одна рука, мокрая или в перчатке, и телефон в ней же. Лист приходит
 * снизу, то есть в зону большого пальца, и крупным кнопкам в нём есть где стоять; диалог по центру
 * ставит те же кнопки в середину экрана, куда большим пальцем не достать, и обычно мельче.
 *
 * **Чего здесь нет намеренно.** Обязательных полей — ни одного, кроме вида (он и так выбран
 * плиткой). Вес часто не измеряют, длину почти никогда, и дневник, который не принимает запись без
 * цифры, просто не заполняют. Вес правится кнопками «−»/«+» на 50 г, чтобы обойтись без клавиатуры;
 * приманка выбирается чипом из недавних, и клавиатура нужна только для новой.
 *
 * **Вид переключается и в правке** — не только при новой записи: промах по соседней плитке в ленте
 * «Рыбалки» самая вероятная ошибка быстрой записи, и исправляться она должна там же, где видна. Но
 * СПИСОК видов в правке свёрнут, и это не мелочь: два десятка чипов занимали весь лист, и вес с
 * приманкой — то, ради чего лист и открыли, — оказывались за нижним краем экрана (видно на
 * эмуляторе 2026-09-28). Имя вида стоит заголовком, рядом кнопка «сменить вид».
 *
 * @param initial уже записанный улов, который уточняют; `null` — новая запись из кнопки «Улов».
 * @param onDelete удалить эту запись. `null` для новой — удалять ещё нечего. Для правки это ЕЩЁ И
 *   отмена промаха: плитка уже записала рыбу, и единственный способ забрать её обратно — здесь.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatchSheet(
    initial: Catch?,
    species: List<FishSpecies>,
    recentBaits: List<String>,
    onDismiss: () -> Unit,
    onSave: (CatchDraft) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var speciesId by remember(initial?.id) { mutableStateOf(initial?.speciesId) }
    var weightText by remember(initial?.id) { mutableStateOf(initial?.weightGrams?.toString().orEmpty()) }
    var lengthText by remember(initial?.id) {
        mutableStateOf(initial?.lengthMm?.let { storedToText(it, LENGTH_SCALE) }.orEmpty())
    }
    var depthText by remember(initial?.id) {
        mutableStateOf(initial?.depthCm?.let { storedToText(it, DEPTH_SCALE) }.orEmpty())
    }
    var bait by remember(initial?.id) { mutableStateOf(initial?.bait.orEmpty()) }
    var baitFieldOpen by remember(initial?.id) { mutableStateOf(false) }
    var outcome by remember(initial?.id) { mutableStateOf(initial?.outcome ?: CatchOutcome.KEPT) }
    var lostReason by remember(initial?.id) { mutableStateOf(initial?.lostReason) }
    var note by remember(initial?.id) { mutableStateOf(initial?.note.orEmpty()) }
    var moreOpen by remember(initial?.id) { mutableStateOf(initial?.note?.isNotBlank() == true) }
    // В новой записи вид ещё не выбран — список открыт. В правке он выбран плиткой, и список
    // сворачивается, освобождая лист под то, ради чего он открыт.
    var speciesPickerOpen by remember(initial?.id) { mutableStateOf(initial == null) }

    val weightGrams = weightText.toIntOrNull()
    val lengthMm = textToStored(lengthText, LENGTH_SCALE)
    val depthCm = textToStored(depthText, DEPTH_SCALE)
    val weightTooBig = weightGrams != null && weightGrams > MAX_CATCH_WEIGHT_GRAMS
    val lengthTooBig = lengthMm != null && lengthMm > MAX_CATCH_LENGTH_MM
    val depthTooBig = depthCm != null && depthCm > MAX_CATCH_DEPTH_CM
    val canSave = speciesId != null && !weightTooBig && !lengthTooBig && !depthTooBig

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            // imePadding у всего содержимого листа, а не у каждого поля: поля здесь лежат в одном
            // прокручиваемом столбце, и поднимать над клавиатурой нужно его целиком. Правило проекта
            // («у каждого поля ввода свой imePadding») именно этим и закрывается — сцену на iOS
            // никто не сдвинет, значит отступ обязан быть свой, и он есть.
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val chosen = species.firstOrNull { it.id == speciesId }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    // Заголовок правки — имя рыбы, а не слово «Улов»: лист открыт из ленты, где
                    // плиток несколько, и первое, что нужно подтвердить глазами, — что правится
                    // та самая.
                    text = if (chosen != null && initial != null) {
                        speciesDisplayName(chosen)
                    } else {
                        fishStringResource(FishStringKey.CatchTitle)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f),
                )
                if (initial != null) {
                    // Время записи — единственный способ отличить друг от друга двух окуней подряд.
                    Text(
                        text = formatTimeOnly(initial.timestamp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (speciesPickerOpen) {
                SheetLabel(fishStringResource(FishStringKey.CatchSpeciesPick))
                ChipFlow {
                    species.forEach { item ->
                        FilterChip(
                            selected = item.id == speciesId,
                            onClick = {
                                speciesId = item.id
                                // В правке список схлопывается обратно: вид выбран, и держать
                                // открытыми два десятка чипов больше незачем.
                                if (initial != null) speciesPickerOpen = false
                            },
                            label = { Text(speciesDisplayName(item)) },
                        )
                    }
                }
            } else {
                TextButton(onClick = { speciesPickerOpen = true }) {
                    Text(fishStringResource(FishStringKey.CatchSpeciesChange))
                }
            }

            SheetLabel(fishStringResource(FishStringKey.CatchOutcome))
            // Крупные кнопки с переносом, а не `SingleChoiceSegmentedButtonRow`: тот делит ширину
            // экрана на три и обрезает подписи при крупном системном шрифте (грабля уже разобрана в
            // `SettingsScreen` грибного приложения). FlowRow вместо обрезки переносит.
            ChipFlow {
                CatchOutcome.entries.forEach { value ->
                    val selected = value == outcome
                    val label = fishStringResource(value.labelKey())
                    val onClick = {
                        outcome = value
                        if (value != CatchOutcome.LOST) lostReason = null
                    }
                    if (selected) {
                        FilledTonalButton(onClick = onClick, modifier = Modifier.heightIn(min = 48.dp)) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(Modifier.widthIn(min = 6.dp))
                            Text(label)
                        }
                    } else {
                        OutlinedButton(onClick = onClick, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(label)
                        }
                    }
                }
            }

            if (outcome == CatchOutcome.LOST) {
                SheetLabel(fishStringResource(FishStringKey.CatchLostReason))
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

            StepperField(
                value = weightText,
                onValueChange = { weightText = it },
                label = fishStringResource(FishStringKey.CatchWeight),
                suffix = fishStringResource(FishStringKey.UnitGram),
                step = WEIGHT_STEP_GRAMS,
                scale = WEIGHT_SCALE,
                isError = weightTooBig,
                errorText = fishStringResource(FishStringKey.CatchWeightTooBig).takeIf { weightTooBig },
            )

            // Глубина стоит рядом с весом при ЛЮБОМ способе ловли, а не только с лодки и со льда
            // (решение владельца 2026-09-28). С берега она тоже известна и тоже объясняет поклёвку —
            // бровка, яма, отмель; прятать её за «Ещё» значило бы решить за берегового рыбака, что
            // глубина его не касается. Необязательна, как и всё здесь.
            StepperField(
                value = depthText,
                onValueChange = { depthText = it },
                label = fishStringResource(FishStringKey.CatchDepth),
                suffix = fishStringResource(FishStringKey.UnitMeter),
                step = DEPTH_STEP_CM,
                scale = DEPTH_SCALE,
                isError = depthTooBig,
                errorText = fishStringResource(FishStringKey.CatchDepthTooBig).takeIf { depthTooBig },
            )

            SheetLabel(fishStringResource(FishStringKey.CatchBait))
            ChipFlow {
                recentBaits.take(RECENT_BAITS_SHOWN).forEach { recent ->
                    FilterChip(
                        selected = bait.equals(recent, ignoreCase = true),
                        onClick = {
                            // Повторное нажатие снимает: рыба бывает и без приманки (на живца с
                            // берега, на голый крючок), и отменить выбор должно быть чем.
                            bait = if (bait.equals(recent, ignoreCase = true)) "" else recent
                            baitFieldOpen = false
                        },
                        label = { Text(recent) },
                    )
                }
                AssistChip(
                    onClick = { baitFieldOpen = !baitFieldOpen },
                    label = { Text(fishStringResource(FishStringKey.CatchBaitOther)) },
                )
            }
            // Поле ввода приманки — только по требованию: у постоянного поля первым делом вылезает
            // клавиатура, а в девяти случаях из десяти приманка уже есть в чипах выше.
            if (baitFieldOpen || (bait.isNotBlank() && recentBaits.none { it.equals(bait, ignoreCase = true) })) {
                OutlinedTextField(
                    value = bait,
                    onValueChange = { bait = it },
                    placeholder = { Text(fishStringResource(FishStringKey.CatchBaitHint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            TextButton(onClick = { moreOpen = !moreOpen }) {
                Text(fishStringResource(if (moreOpen) FishStringKey.CatchLess else FishStringKey.CatchMore))
            }

            if (moreOpen) {
                StepperField(
                    value = lengthText,
                    onValueChange = { lengthText = it },
                    label = fishStringResource(FishStringKey.CatchLength),
                    suffix = fishStringResource(FishStringKey.UnitCentimeter),
                    step = LENGTH_STEP_MM,
                    scale = LENGTH_SCALE,
                    isError = lengthTooBig,
                    errorText = fishStringResource(FishStringKey.CatchLengthTooBig).takeIf { lengthTooBig },
                )
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text(
                            text = fishStringResource(FishStringKey.CatchDelete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Text(fishStringResource(FishStringKey.Cancel)) }
                Button(
                    enabled = canSave,
                    onClick = {
                        val id = speciesId ?: return@Button
                        onSave(
                            CatchDraft(
                                speciesId = id,
                                weightGrams = weightGrams,
                                lengthMm = lengthMm,
                                depthCm = depthCm,
                                bait = bait,
                                outcome = outcome,
                                lostReason = lostReason,
                                note = note,
                            )
                        )
                    },
                ) {
                    Text(fishStringResource(FishStringKey.CatchDone))
                }
            }
        }
    }
}

@Composable
private fun SheetLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelLarge)
}

/**
 * Число с кнопками «−»/«+».
 *
 * Кнопки — не украшение: ввести «350» с клавиатуры на воде в перчатке дороже, чем нажать «+» семь
 * раз одним пальцем, а поле остаётся для тех, кто взвесил точно. Пустое поле плюс «+» даёт первый
 * шаг ([step]), а не ноль: ноль не значит ничего и в базе означал бы «взвесили и получили нуль».
 *
 * @param step шаг кнопок в ЕДИНИЦАХ ХРАНЕНИЯ (граммы, миллиметры, сантиметры).
 * @param scale сколько единиц хранения в одной показываемой: вес — 1 (граммы), длина — 10
 *   (миллиметры на сантиметр), глубина — 100 (сантиметры на метр). Больше единицы — значит поле
 *   дробное, и клавиатура тоже.
 */
@Composable
private fun StepperField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    step: Int,
    scale: Int,
    isError: Boolean,
    errorText: String?,
) {
    val decimal = scale > 1
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val current = textToStored(value, scale)
        val render = { raw: Int ->
            onValueChange(storedToText(raw.coerceAtLeast(0), scale))
        }
        FilledTonalIconButton(
            onClick = { render((current ?: step) - step) },
            enabled = current != null && current > 0,
        ) {
            Icon(Icons.Filled.Remove, contentDescription = null)
        }
        OutlinedTextField(
            value = value,
            onValueChange = { text ->
                onValueChange(
                    if (decimal) {
                        text.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
                    } else {
                        text.filter(Char::isDigit)
                    }
                )
            },
            label = { Text(label) },
            suffix = { Text(suffix) },
            isError = isError,
            supportingText = errorText?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
            ),
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        FilledTonalIconButton(onClick = { render((current ?: 0) + step) }) {
            Icon(Icons.Filled.Add, contentDescription = null)
        }
    }
}

/** Единицы хранения в одной показываемой — см. [StepperField]. */
private const val WEIGHT_SCALE = 1
private const val LENGTH_SCALE = 10
private const val DEPTH_SCALE = 100

/**
 * Единица хранения → текст поля. Целое значение показывается без дробной части («34», а не «34.0»),
 * дробное — с одним знаком: второй знак у глубины и длины не значит ничего, кроме ложной точности.
 */
private fun storedToText(stored: Int, scale: Int): String {
    if (scale == 1) return stored.toString()
    val value = (stored.toDouble() / scale * 10).roundToInt() / 10.0
    return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}

/** Текст поля → единица хранения. Пустое и неразобранное — `null`, то есть «не мерили». */
private fun textToStored(text: String, scale: Int): Int? =
    text.toDoubleOrNull()?.let { (it * scale).roundToInt() }
