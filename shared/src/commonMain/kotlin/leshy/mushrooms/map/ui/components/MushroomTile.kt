package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.domain.model.MAX_MUSHROOM_FINDS_PER_WALK
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.categoryDisplayName
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.ui.theme.LeshyTheme
import leshy.mushrooms.map.ui.util.parseHexColor
import leshy.mushrooms.map.domain.model.Edition
import kotlin.time.Duration.Companion.seconds

private val MUSHROOM_COUNT_BUTTON_SIZE = 40.dp

/** Отступ ряда счётчика от краёв плитки. По вертикали берётся половина — сверху и снизу к нему
 * добавляется собственный инсет области нажатия кнопок. */
private val COUNT_ROW_PADDING = 8.dp

/** Holding the + button this long opens the bulk-add dialog instead of logging a single find. */
private val MUSHROOM_BULK_ADD_HOLD_DURATION = 2.seconds

/** Width [MushroomTile] is displayed at on the record screen — other tiles size themselves relative to it. */
val RECORD_MUSHROOM_TILE_WIDTH = 120.dp

/**
 * Потолок ширины, шире которого иллюстрацию каталога показывать нельзя ни в каком месте
 * приложения. Не вкусовое ограничение, а предел самого файла: после обрезки полей
 * (`tools/crop_mushroom_images.py`) у картинок каталога длинная сторона ровно 242px, и растягивать
 * их бесконечно попросту нечем — дальше это интерполяция, то есть мыло.
 *
 * Откуда 280dp. На Pixel 4a (плотность 2.75) картинка в диалоге массового добавления занимала
 * ~329dp, то есть 905px из 242px исходника — растяжение в 3.7 раза, и владелец подтвердил, что
 * это уже предел приемлемой чёткости. 280dp на той же плотности дают 770px, то есть 3.2 раза —
 * «чуть раньше предела», как и просилось. Число задано в dp, а не долей от исходника: потолок
 * упирается в размер экрана, а крупные экраны (планшеты) почти всегда плотностью 2.0, где 280dp
 * выходят всего 560px — 2.3 раза, с запасом.
 *
 * Отдельная величина от `FIND_TILE_MAX_WIDTH` (200dp, плитка находок на «Карте» и в архиве,
 * `StatsBlocks.kt`): тот потолок про вёрстку подписи с названием вида, этот — про разрешение
 * файла. Совпадать они не обязаны, но 200 < 280, то есть плитка находок в этот потолок
 * укладывается сама.
 */
val MUSHROOM_PHOTO_MAX_WIDTH = 280.dp

/**
 * Соотношение сторон площадки под фото гриба — медианное соотношение самих изображений после
 * обрезки прозрачных полей (`tools/crop_mushroom_images.py`).
 *
 * История в двух шагах. Изначально площадка была 1.5:1, а картинки — квадратные 256×256 и
 * рисовались `ContentScale.Fit`, поэтому вписывались по высоте и оставляли пустыми боковые поля:
 * при ширине плитки 120dp картинка занимала 80×80dp, две трети ширины уходили в никуда. Площадку
 * сделали квадратной — боковые поля исчезли, но осталась пустота СВЕРХУ: гриб занимал в среднем
 * лишь 75% высоты собственного файла, остальное — прозрачные поля внутри изображения, около 15dp
 * пустоты над грибом. В вёрстке этого было не убрать: срезать одинаково для всех можно было лишь
 * 2.7% (столько у самой «прижатой» картинки), иначе другим отрезало бы шляпку.
 *
 * Поэтому поля срезаны в самих файлах, по границе непрозрачности каждого, и площадка приведена к
 * медианному соотношению содержимого. Теперь типичный гриб заполняет площадку целиком, без пустот
 * ни сверху, ни по бокам. У видов с нетипичной пропорцией остаётся небольшой зазор — картинка
 * центрируется в площадке (`ContentScale.Fit` центрирует по обеим осям), подпись при этом
 * по-прежнему прижата к нижнему краю ПЛОЩАДКИ, а не к краю картинки.
 */
const val MUSHROOM_PHOTO_ASPECT_RATIO = 1.26f

/** Полоска фона, остающаяся между картинкой и краем слота с каждой стороны — см. [MushroomPhoto]. */
private val MUSHROOM_PHOTO_INSET = 4.dp

/**
 * Полная высота плитки ленты «Записи»: строка счётчика плюс площадка фото (её ширина равна ширине
 * плитки, высота — по [MUSHROOM_PHOTO_ASPECT_RATIO]). [AddSpeciesTile] задаёт себе эту высоту
 * явно — у него другое внутреннее устройство, совпасть с [MushroomTile] по построению он не может,
 * а лента обязана читаться одной ровной полосой.
 */
val RECORD_TILE_HEIGHT = MUSHROOM_COUNT_BUTTON_SIZE + RECORD_MUSHROOM_TILE_WIDTH / MUSHROOM_PHOTO_ASPECT_RATIO

/**
 * @param onAdd короткое нажатие «+» ИЛИ по картинке гриба. **Возвращает, записана ли находка на
 *   самом деле** — только тогда плитка проигрывает перелив. Отказать вызывающему есть от чего:
 *   прогулка ещё не начата, нет GPS-фикса (тогда он показывает сообщение вместо записи). Зелёный
 *   перелив в ответ на отказ соврал бы, что гриб отмечен, — а это ровно то, ради чего перелив и
 *   заводился.
 * @param onRemove короткое нажатие «−», с тем же смыслом возвращаемого значения.
 * @param onBulkAdd действие двухсекундного удержания «+» или картинки. `null` — удержание не
 *   считается вовсе: ни таймера, ни заливки-индикатора, ни отклика. Так и передаётся, пока
 *   прогулка не начата — массовому добавлению до старта не на чем сработать, а индикатор, за
 *   которым ничего не происходит, хуже отсутствующего.
 */
@Composable
fun MushroomTile(
    category: Category,
    count: Int,
    onAdd: () -> Boolean,
    onRemove: () -> Boolean,
    modifier: Modifier = Modifier,
    onBulkAdd: (() -> Unit)? = null,
) {
    val outlineColor = parseHexColor(category.colorHex)
    // Заливка идёт по всей плитке, хотя удерживают одну лишь кнопку «+»: под пальцем самой кнопки
    // не видно, а плитка из-под него торчит. Читается как «эта плитка набирает заряд» — то есть
    // ровно то, чем массовое добавление и является.
    var holdProgress by remember { mutableFloatStateOf(0f) }
    val tapFlash = rememberTapFlash()
    // Обёртки, а не голые onAdd/onRemove по месту: оба действия вызываются из двух мест каждое
    // («+» и картинка — для добавления), и решение «перелив только если действие принято» обязано
    // быть одним на все точки вызова.
    val add = { if (onAdd()) tapFlash.flash(TapFlashDirection.UP) }
    val remove = { if (onRemove()) tapFlash.flash(TapFlashDirection.DOWN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .holdProgressWipe(
                shape = CardDefaults.shape,
                color = MaterialTheme.colorScheme.primary,
                progress = { holdProgress },
            )
            // Оба перелива — на всей плитке, а не на нажатой кнопке: см. комментарий к
            // holdProgress выше, палец закрывает кнопку целиком. Цвета из темы, не литералы, —
            // иначе в тёмной теме зелёный и красный поплыли бы по контрасту.
            .tapFlashWipe(
                shape = CardDefaults.shape,
                upColor = MaterialTheme.colorScheme.primary,
                downColor = MaterialTheme.colorScheme.error,
                state = tapFlash,
            ),
        border = BorderStroke(2.dp, outlineColor),
        // Доска карточки — другая, чем земля под ней: иначе плитка не читается как предмет,
        // лежащий на земле (см. `LeshyTokens.cardTexture`).
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor(MaterialTheme.colorScheme.surfaceContainerLow),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(modifier = Modifier.cardBackground()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Отступ по вертикали появился вместе с деревянной подложкой кнопок: пока
                    // «+» и «−» были голыми значками, их поле никак не читалось, и край плашки
                    // рядом никому не мешал. У жетона край есть, и он обязан не касаться рамы
                    // плашки — иначе две деревянные поверхности сходятся встык и плитка
                    // выглядит собранной из обрезков. К этим 4dp добавляются ещё 4dp от области
                    // нажатия (48dp вокруг видимых 40dp), итого зазор 8dp сверху и снизу и 12dp
                    // по бокам.
                    .padding(horizontal = COUNT_ROW_PADDING, vertical = COUNT_ROW_PADDING / 2),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Размер кнопки задаётся ВНУТРИ, а не модификатором самой `IconButton`: та
                // оборачивает переданный модификатор в `minimumInteractiveComponentSize()`, и узел
                // получается 48dp при видимых 40dp. Пока фона не было, разница не читалась; с
                // подложкой «−» вышла заметно крупнее «+» (репорт владельца 2026-09-26). Теперь
                // подложка лежит на своих 40dp, а 48dp остаются тем, чем и были, — областью
                // нажатия.
                // shape — токеном: `IconButton` КЛИПУЕТ своё содержимое собственной формой, а она
                // по умолчанию круглая, и квадратный жетон под «−» приезжал обрезанным в круг
                // (репорт владельца 2026-09-26). У «+» этой беды нет: он не `IconButton`, а свой
                // `Box` с той же формой из токена.
                IconButton(
                    onClick = remove,
                    enabled = count > 0,
                    shape = LeshyTheme.tokens.shapeCountButton,
                ) {
                    Box(
                        modifier = Modifier
                            .size(MUSHROOM_COUNT_BUTTON_SIZE)
                            .glyphBadgeBackground(LeshyTheme.tokens.shapeCountButton),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = null,
                            // Явный цвет, а не `LocalContentColor` от `IconButton`: на жетоне
                            // значок всегда светлый (доска тёмная в обеих темах), и гашение
                            // выключенного состояния, которое `IconButton` даёт даром, приходится
                            // повторить рукой.
                            tint = glyphBadgeContentColor(LocalContentColor.current)
                                .copy(alpha = if (count > 0) 1f else 0.38f),
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
                Text(
                    text = count.toString(),
                    fontSize = if (count.toString().length >= 3) 14.sp else 20.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.width(28.dp),
                )
                MushroomAddButton(
                    onClick = add,
                    onLongHold = onBulkAdd,
                    enabled = count < MAX_MUSHROOM_FINDS_PER_WALK,
                    onHoldProgress = { holdProgress = it },
                )
            }
            MushroomPhoto(
                category = category,
                // Картинка — вторая, большая кнопка «+»: тот же жест с теми же порогами и тем же
                // индикатором удержания. Кнопка 40dp под большим пальцем в лесу, в перчатке, на
                // ходу — мелкая мишень; картинка занимает почти всю плитку и промахнуться по ней
                // трудно. Модификатор навешивается здесь, а НЕ внутри MushroomPhoto: тот же
                // composable переиспользует легенда донат-чарта на экране детализации
                // (MushroomLegendTile), где нажимать не на что и нечего добавлять.
                //
                // Прокрутке ленты жест не мешает: tapOrHold ничего не потребляет, и протяжка по
                // картинке уезжает в LazyRow, отменяя нажатие (waitForUpOrCancellation вернёт
                // null) — ровно так же, как это уже работало у кнопки «+».
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(LeshyTheme.tokens.photoAspectRatio)
                    .tapOrHold(
                        holdDuration = MUSHROOM_BULK_ADD_HOLD_DURATION,
                        onTap = add,
                        onHold = { onBulkAdd?.invoke() },
                        enabled = count < MAX_MUSHROOM_FINDS_PER_WALK,
                        holdEnabled = onBulkAdd != null,
                        onHoldProgress = { holdProgress = it },
                    ),
            )
        }
    }
}

/**
 * Rightmost, permanent entry in Record's tile feed (`.claude/plans/user-mushrooms.md`, Phase 4) —
 * not backed by a [Category], just an oversized "+" and a label, opening the species creation form
 * right there on the record screen so a walk in progress is never interrupted. Same footprint as
 * [MushroomTile] (border + rounded card) so it reads as part of the same strip, not a stray button.
 */
@Composable
fun AddSpeciesTile(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
    ) {
        Column(
            // Явная высота, а не aspectRatio: плитка перестала быть квадратной — у MushroomTile
            // над квадратным фото есть ещё строка счётчика, см. RECORD_TILE_HEIGHT.
            modifier = Modifier.fillMaxWidth().height(RECORD_TILE_HEIGHT).padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = stringResource(StringKey.SpeciesAddButton),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/**
 * The + button — a plain tap logs one find ([onClick]), holding it for
 * [MUSHROOM_BULK_ADD_HOLD_DURATION] opens the bulk-add dialog instead ([onLongHold]). Сам жест и
 * все его тонкости — в [tapOrHold]; здесь остаётся ровно то, что относится к кнопке: рябь во
 * время удержания (`indication` + собственный `interactionSource`, чтобы нажатие выглядело
 * обычным нажатием) и гашение значка по достижении [MAX_MUSHROOM_FINDS_PER_WALK].
 *
 * [onLongHold] `null` — удержание не считается: [tapOrHold] не заводит таймер и не зовёт
 * [onHoldProgress], значит и заливки плитки не будет.
 */
@Composable
private fun MushroomAddButton(
    onClick: () -> Unit,
    onLongHold: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onHoldProgress: (Float) -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            // Та же область нажатия, что у «−»: её `IconButton` берёт себе сам, а этой кнопке
            // приходится просить. До этого «+» занимал 40dp против 48dp у соседа — при
            // `SpaceBetween` это ещё и смещало счётчик между ними от центра.
            .minimumInteractiveComponentSize()
            .size(MUSHROOM_COUNT_BUTTON_SIZE)
            .clip(LeshyTheme.tokens.shapeCountButton)
            .glyphBadgeBackground(LeshyTheme.tokens.shapeCountButton)
            .indication(interactionSource, LocalIndication.current)
            .tapOrHold(
                holdDuration = MUSHROOM_BULK_ADD_HOLD_DURATION,
                onTap = onClick,
                onHold = { onLongHold?.invoke() },
                enabled = enabled,
                holdEnabled = onLongHold != null,
                interactionSource = interactionSource,
                onHoldProgress = onHoldProgress,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = null,
            tint = glyphBadgeContentColor(LocalContentColor.current).copy(alpha = if (enabled) 1f else 0.38f),
            modifier = Modifier.size(32.dp),
        )
    }
}

/**
 * The photo/badge/label portion of [MushroomTile] (everything below its count row), reused as-is
 * by [leshy.mushrooms.map.ui.components.MushroomLegendTile] for the walk-detail donut chart's
 * legend — same bordered-plate look, minus the count row that doesn't apply there.
 */
@Composable
fun MushroomPhoto(category: Category, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // Отступ именно у картинки, а не у всего слота: подпись выравнивается по нижнему краю
        // СЛОТА и отступом двигаться не должна. Гарантирует полоску фона с каждой стороны —
        // после обрезки полей в файлах гриб доходит ровно до края собственного изображения, и
        // без этого отступа он касался бы рамки плитки (виды с соотношением шире слота — левой
        // и правой, уже слота — верхней и нижней).
        CategoryIcon(
            category = category,
            modifier = Modifier.fillMaxSize().padding(MUSHROOM_PHOTO_INSET),
        )

        MushroomOutlinedText(
            text = categoryDisplayName(category),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(54.dp)
                // Нижние 4dp — это прежние 2dp внешнего поля плюс 2dp, которые composable
                // добавлял себе сам; сложены в одно число, геометрия подписи не менялась.
                .padding(start = 6.dp, top = 2.dp, end = 6.dp, bottom = 4.dp),
        )
    }
}

/** Кегль подписи с названием вида на плитке. */
val MUSHROOM_LABEL_FONT_SIZE = 18.sp

/**
 * Межстрочное расстояние долей кегля — прежде оба числа стояли рядом константами (18sp и 20sp), и
 * связь между ними держалась на том, что их правят вместе. Теперь кегль у [MushroomOutlinedText]
 * задаёт вызывающий, и связь обязана быть выражена.
 */
private const val LABEL_LINE_HEIGHT_RATIO = 20f / 18f

/**
 * Толщина обводки — тоже долей кегля, из тех же прежних чисел (3dp при 18sp). Не постоянные 3dp:
 * обводка постоянной толщины вокруг более крупного текста читалась бы тоньше, а тот же приём
 * применяется теперь и к счётчику находок на экране детализации, который заметно крупнее подписи.
 *
 * Побочное следствие, оно же исправление: доля берётся от кегля в sp, то есть обводка растёт
 * вместе с системным размером шрифта. Прежние 3dp не росли — при крупном системном шрифте буквы
 * увеличивались, а обводка вокруг них оставалась прежней и относительно бледнела.
 */
private const val LABEL_STROKE_TO_FONT_RATIO = 3f / 18f

/**
 * [lineHeight]/[lineHeightStyle] are explicit (not left to the font's own metrics) so the two-line
 * block's rendered height is the same 2 * [LABEL_LINE_HEIGHT_RATIO] * fontSize regardless of
 * script — fallback fonts for CJK carry noticeably taller ascent/descent than Latin at the same
 * `fontSize`, and the surrounding [Box] in [MushroomOutlinedText] doesn't clip, so with
 * font-derived line height the second line poked out past the plate's fixed-height bottom edge for
 * those languages. [MushroomPhoto]'s label container is sized with a few dp of headroom above the
 * exact 2 * 20.sp this implies — Georgian glyphs (წ, ჯ, ყ, ...) carry deep descenders that still
 * reach past an exactly-fitted box even with [LineHeightStyle.Trim.Both], reproduced live
 * on-device with "მერცხალასოკო"/"მყრალიხრაშუნა".
 */
private fun outlinedTextStyle(fontSize: TextUnit) = TextStyle(
    fontSize = fontSize,
    lineHeight = fontSize * LABEL_LINE_HEIGHT_RATIO,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both,
    ),
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
)

/**
 * Renders [text] with a black outline over a white fill, so it stays readable over any photo.
 * Two stacked [Text] composables (not a manually measured/drawn Canvas) — measuring the same
 * string twice via one [androidx.compose.ui.text.TextMeasurer] with only color/drawStyle
 * differing let the second draw corrupt the first (shared/cached paragraph paint state); plain
 * [Text] calls each own their layout independently and don't hit that.
 *
 * Публичный и с настраиваемым кеглем, потому что этим же приёмом набран счётчик находок на плитке
 * экрана детализации прогулки: обе надписи лежат поверх одной и той же картинки гриба и обязаны
 * читаться одинаково — иначе на одной плитке оказалось бы два разных способа написать текст
 * поверх фотографии.
 */
@Composable
fun MushroomOutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = MUSHROOM_LABEL_FONT_SIZE,
    maxLines: Int = 2,
    contentAlignment: Alignment = Alignment.BottomCenter,
) {
    val style = outlinedTextStyle(fontSize)
    // Ширину надписи держит `textAlign = Center` внутри стиля, а не `fillMaxWidth` у самих `Text`,
    // как было раньше: тому же композаблу теперь достаётся счётчик в углу плитки, которому ширина
    // плитки не нужна — он обязан занимать ровно себя.
    val strokeWidthPx = with(LocalDensity.current) { fontSize.toPx() * LABEL_STROKE_TO_FONT_RATIO }
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        Text(
            text = text,
            style = style.copy(color = Color.Black, drawStyle = Stroke(width = strokeWidthPx)),
            maxLines = maxLines,
        )
        Text(
            text = text,
            style = style.copy(color = Color.White),
            maxLines = maxLines,
        )
    }
}

@Preview
@Composable
fun MushroomTilePreview(){
    LeshyTheme(edition = Edition.WORLD) {
        MushroomTile(
            category = Category(
                1,
                "category_boletus_edulis",
                "#A95620",
                "agaricus_silvicola",
                0,
                true,
            ),
            count = 0,
            onAdd = { true },
            onRemove = { true },
            modifier = Modifier
        )
    }
}
