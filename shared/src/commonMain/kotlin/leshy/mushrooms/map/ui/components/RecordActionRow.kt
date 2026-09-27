package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.Edition
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.ui.theme.LeshyTheme
import leshy.mushrooms.map.ui.theme.LocalEdition

/** Высота кнопок ряда — она же сторона круглых боковых кнопок мировой редакции. */
val RECORD_ACTION_BUTTON_HEIGHT = 56.dp

// Ширина центральной «пилюли» мировой редакции — сжимается на узком экране (см.
// CENTER_BUTTON_MIN_WIDTH), чтобы боковые кнопки всегда получали полный слот и не сплющивались.
private val CENTER_BUTTON_MAX_WIDTH = 200.dp
private val CENTER_BUTTON_MIN_WIDTH = 130.dp
private val ROW_HORIZONTAL_PADDING = 16.dp
private val SIDE_BUTTON_SLOT_WIDTH = 64.dp

/** Отбивка между кнопками российского ряда: три равные доски, а не «кружок — пилюля — кружок». */
private val LABELLED_BUTTON_GAP = 8.dp

/**
 * Поля внутри кнопки с подписью. Дефолтные у Material3 — 24dp с каждой стороны, то есть на
 * трети экрана 360dp (104dp) под значок и текст осталось бы 56dp, и не влезло бы ничего.
 */
private val LABELLED_BUTTON_PADDING = PaddingValues(horizontal = 8.dp)

/** Те же поля суммой — столько ширины кнопки содержимому недоступно. */
private val LABELLED_BUTTON_PADDING_WIDTH = 16.dp

/** Значок внутри кнопки с подписью: мельче глифа круглой кнопки (28dp), он тут не один. */
private val LABELLED_BUTTON_ICON_SIZE = 20.dp
private val LABELLED_BUTTON_ICON_GAP = 6.dp

/**
 * Нижний ряд управления прогулкой на «Записи»: отметить место, старт/пауза/продолжить/завершить,
 * поиск вида.
 *
 * **Ряд у редакций разный, и это единственное место, где он выбирается.** Мировая — круглый
 * значок, центральная «пилюля» с подписью, круглый значок; российская — три равные кнопки со
 * значком И подписью. Различие держится двумя функциями рядом, а не ветвлениями внутри одной:
 * правило мержабельности (`docs/russia-edition/README.md`, «Правки общих экранов») — добавленный
 * файл при мердже не конфликтует, отредактированный общий конфликтует всегда.
 *
 * Почему у российской редакции подписи. Ряд занимает самую заметную часть экрана записи, и его
 * облик — то немногое, что видно на скриншоте в магазине (`docs/russia-edition/store-duplication.md`:
 * различают не код, а то, что видит пользователь). Значки при этом остались: это единственный
 * экран, которым пользуются в лесу — мокрый экран, перчатки, взгляд на полсекунды, — и глиф там
 * ловится периферийным зрением, а подпись требует чтения. Высота ряда от подписей не изменилась
 * (56dp): значок и текст стоят в строку, а не друг под другом, поэтому карта не потеряла ни
 * пикселя.
 */
@Composable
fun RecordActionRow(
    isRecording: Boolean,
    isPaused: Boolean,
    onMarkPlaceClick: () -> Unit,
    onSearchClick: () -> Unit,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (LocalEdition.current) {
        Edition.WORLD -> WorldRecordActionRow(
            isRecording = isRecording,
            isPaused = isPaused,
            onMarkPlaceClick = onMarkPlaceClick,
            onSearchClick = onSearchClick,
            onStartClick = onStartClick,
            onPauseClick = onPauseClick,
            onResumeClick = onResumeClick,
            onFinishClick = onFinishClick,
            modifier = modifier,
        )
        Edition.RUSSIA -> RussiaRecordActionRow(
            isRecording = isRecording,
            isPaused = isPaused,
            onMarkPlaceClick = onMarkPlaceClick,
            onSearchClick = onSearchClick,
            onStartClick = onStartClick,
            onPauseClick = onPauseClick,
            onResumeClick = onResumeClick,
            onFinishClick = onFinishClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun WorldRecordActionRow(
    isRecording: Boolean,
    isPaused: Boolean,
    onMarkPlaceClick: () -> Unit,
    onSearchClick: () -> Unit,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // The Start/Pause pill is normally a fixed CENTER_BUTTON_MAX_WIDTH, but on a
        // narrow screen (e.g. iPhone SE's 320dp) that plus two RECORD_ACTION_BUTTON_HEIGHT
        // side buttons doesn't fit — shrinking the pill first keeps each side button's
        // weighted slot at least SIDE_BUTTON_SLOT_WIDTH, so it's never forced smaller
        // than its own icon and centered unevenly inside its slot.
        val centerButtonWidth = (maxWidth - ROW_HORIZONTAL_PADDING * 2 - SIDE_BUTTON_SLOT_WIDTH * 2)
            .coerceIn(CENTER_BUTTON_MIN_WIDTH, CENTER_BUTTON_MAX_WIDTH)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ROW_HORIZONTAL_PADDING, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            when {
                !isRecording -> {
                    // No walk to attach a place to yet — same dimmed/disabled treatment
                    // as a mushroom tile's minus button before any find is logged.
                    RecordSideButton(
                        icon = Icons.Filled.AddLocationAlt,
                        contentDescription = stringResource(StringKey.RecordMarkLocationContentDescription),
                        onClick = onMarkPlaceClick,
                        enabled = false,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    LeshyButton(
                        onClick = onStartClick,
                        shape = LeshyTheme.tokens.shapeActionButton,
                        modifier = Modifier.height(RECORD_ACTION_BUTTON_HEIGHT).width(centerButtonWidth),
                    ) {
                        Text(stringResource(StringKey.RecordStart))
                    }
                    RecordSideButton(
                        icon = Icons.Filled.Search,
                        contentDescription = stringResource(StringKey.RecordSearchContentDescription),
                        onClick = onSearchClick,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
                !isPaused -> {
                    RecordSideButton(
                        icon = Icons.Filled.AddLocationAlt,
                        contentDescription = stringResource(StringKey.RecordMarkLocationContentDescription),
                        onClick = onMarkPlaceClick,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    LeshyButton(
                        onClick = onPauseClick,
                        shape = LeshyTheme.tokens.shapeActionButton,
                        modifier = Modifier.height(RECORD_ACTION_BUTTON_HEIGHT).width(centerButtonWidth),
                    ) {
                        Text(stringResource(StringKey.RecordPause))
                    }
                    RecordSideButton(
                        icon = Icons.Filled.Search,
                        contentDescription = stringResource(StringKey.RecordSearchContentDescription),
                        onClick = onSearchClick,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
                else -> {
                    LeshyButton(
                        onClick = onResumeClick,
                        shape = LeshyTheme.tokens.shapeActionButton,
                        modifier = Modifier.height(RECORD_ACTION_BUTTON_HEIGHT).weight(1f),
                    ) {
                        Text(stringResource(StringKey.RecordResume))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LeshyButton(
                        onClick = onFinishClick,
                        shape = LeshyTheme.tokens.shapeActionButton,
                        modifier = Modifier.height(RECORD_ACTION_BUTTON_HEIGHT).weight(1f),
                    ) {
                        Text(stringResource(StringKey.RecordFinish))
                    }
                }
            }
        }
    }
}

/**
 * Российский ряд: три равные кнопки «значок + подпись».
 *
 * Подписи короткие в одну строку, а не «Добавить место» в две: на трети экрана 360dp кнопке
 * достаётся 104dp (на iPhone SE — 96dp), и двухстрочная подпись там рвётся при первом же
 * увеличении системного шрифта. Значок рядом снимает недосказанность короткого слова — «Найти»
 * рядом с лупой уже не обещает найти гриб в лесу, а называет поиск вида в каталоге.
 */
@Composable
private fun RussiaRecordActionRow(
    isRecording: Boolean,
    isPaused: Boolean,
    onMarkPlaceClick: () -> Unit,
    onSearchClick: () -> Unit,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Пауза снимает с ряда место и поиск: пока прогулка стоит, отмечать нечего, и оба решения —
    // продолжить или закончить — заслуживают половины ряда каждое.
    val buttons = if (isPaused) {
        listOf(
            RecordActionButton(Icons.Filled.PlayArrow, stringResource(StringKey.RecordResume), onResumeClick),
            RecordActionButton(Icons.Filled.Stop, stringResource(StringKey.RecordFinish), onFinishClick),
        )
    } else {
        listOf(
            // Место до старта прогулки привязывать не к чему — та же погашенная кнопка, что и в
            // мировой редакции, только подписанная.
            RecordActionButton(
                icon = Icons.Filled.AddLocationAlt,
                label = stringResource(StringKey.RecordPlaceLabel),
                onClick = onMarkPlaceClick,
                enabled = isRecording,
            ),
            RecordActionButton(
                icon = if (isRecording) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                // «Пауза» — общее слово с мировой редакцией, и это вынужденно: «Остановить» при
                // ширине в треть экрана обрезается многоточием даже при обычном системном шрифте
                // (проверено на эмуляторе, 411dp). Значок ⏸ рядом снимает разницу.
                label = stringResource(if (isRecording) StringKey.RecordPause else StringKey.RecordStartLabel),
                onClick = if (isRecording) onPauseClick else onStartClick,
            ),
            RecordActionButton(Icons.Filled.Search, stringResource(StringKey.RecordSearchLabel), onSearchClick),
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val slotWidth = (maxWidth - ROW_HORIZONTAL_PADDING * 2 -
            LABELLED_BUTTON_GAP * (buttons.size - 1)) / buttons.size
        val showIcons = labelsFitWithIcon(buttons.map { it.label }, slotWidth)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ROW_HORIZONTAL_PADDING, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(LABELLED_BUTTON_GAP),
        ) {
            buttons.forEach { button ->
                LabelledActionButton(
                    button = button,
                    showIcon = showIcons,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Одна кнопка российского ряда — значок, подпись, действие. */
private data class RecordActionButton(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * Помещаются ли ВСЕ подписи ряда вместе со значками в кнопку шириной [slotWidth].
 *
 * Решение общее на весь ряд, а не на кнопку: значок, пропавший у одной кнопки из трёх, читается
 * как сбой, а не как экономия места (увидено на эмуляторе при шрифте ×1.3 — «Начать» осталось без
 * значка между «Местом» и «Найти» со значками).
 *
 * Считается, а не берётся порогом-константой: ширина подписи зависит от языка (`ru`+`en`) и от
 * системного масштаба шрифта, а ширина слота — от экрана и от того, две кнопки в ряду или три.
 * Любая константа тут устарела бы на первом же сочетании.
 */
@Composable
private fun labelsFitWithIcon(labels: List<String>, slotWidth: Dp): Boolean {
    val textStyle = MaterialTheme.typography.labelLarge
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val roomForText = with(density) {
        (slotWidth - LABELLED_BUTTON_PADDING_WIDTH - LABELLED_BUTTON_ICON_SIZE - LABELLED_BUTTON_ICON_GAP).toPx()
    }
    return labels.all { measurer.measure(it, textStyle).size.width <= roomForText }
}

/**
 * Кнопка «значок + подпись» российского ряда.
 *
 * Подпись в одну строку: переносить нечего — русские слова тут неразрывны, а перенос поднял бы
 * высоту всего ряда и отъел бы у карты. Поэтому при крупном системном шрифте выбор стоит между
 * обрезанным «Продолжи…» со значком и целым «Продолжить» без него, и [showIcon] выбирает второе:
 * человек, увеличивший шрифт, увеличил его чтобы читать.
 */
@Composable
private fun LabelledActionButton(
    button: RecordActionButton,
    showIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    LeshyButton(
        onClick = button.onClick,
        enabled = button.enabled,
        shape = LeshyTheme.tokens.shapeActionButton,
        contentPadding = LABELLED_BUTTON_PADDING,
        modifier = modifier.height(RECORD_ACTION_BUTTON_HEIGHT),
    ) {
        if (showIcon) {
            Icon(
                imageVector = button.icon,
                contentDescription = null,
                modifier = Modifier.size(LABELLED_BUTTON_ICON_SIZE),
            )
            Spacer(modifier = Modifier.width(LABELLED_BUTTON_ICON_GAP))
        }
        Text(text = button.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Круглая боковая кнопка мировой редакции: только значок, подпись живёт в `contentDescription`.
 */
@Composable
private fun RecordSideButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(RECORD_ACTION_BUTTON_HEIGHT)
                .glyphBadgeBackground(
                    shape = LeshyTheme.tokens.shapeRoundButton,
                    fallbackBackground = MaterialTheme.colorScheme.secondaryContainer,
                    fallbackBorder = MaterialTheme.colorScheme.outline,
                ),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                // 0.38f matches Material3's own disabled-content alpha (IconButtonDefaults) — the
                // icon is set explicitly here instead of inheriting it, so it must be applied by
                // hand to get the same "faded" look the mushroom tiles' minus button gets for free.
                tint = glyphBadgeContentColor(MaterialTheme.colorScheme.onSecondaryContainer)
                    .copy(alpha = if (enabled) 1f else 0.38f),
                modifier = Modifier.size(RECORD_ACTION_BUTTON_HEIGHT / 2),
            )
        }
    }
}
