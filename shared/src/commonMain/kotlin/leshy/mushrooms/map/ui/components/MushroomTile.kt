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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

private val MUSHROOM_COUNT_BUTTON_SIZE = 38.dp

/**
 * Значок внутри кнопки счёта — **на деревянном жетоне**. Те же две трети стороны, что у глифа на
 * жетоне раздела: доска скруглена, и значок крупнее упёрся бы в её край.
 */
private val COUNT_ICON_SIZE_ON_BADGE = 26.dp

/**
 * Значок внутри кнопки счёта **без жетона** — мировая редакция. Ровно те 32dp, какими «+» и «−»
 * уехали в Play: под жетон значок ужимали из-за края доски, а в мировой редакции края нет вовсе,
 * и ужатие пришло туда даром — на телефоне значки стали выглядеть мелкими рядом с прежними
 * (репорт владельца 2026-09-30, сверка с установленной сборкой из Play).
 *
 * Больше 32dp не ставится: кнопка [MUSHROOM_COUNT_BUTTON_SIZE] шириной 38dp, и глифу нужно
 * остаться значком внутри кнопки, а не её заливкой. Сторону самой кнопки при этом не вернули к
 * прежним 40dp — три ребёнка ряда тогда занимают ширину плитки ровно без остатка, и при дробной
 * плотности экрана ряд рискует рассыпаться на округлениях (та же ловушка, что у `FindTilesGrid`).
 */
private val COUNT_ICON_SIZE_BARE = 32.dp

/** Отступ ряда счётчика от краёв плитки — он же зазор между жетоном и рамой плашки. Две кнопки по
 * 38dp, счётчик 28dp и эти отступы вместе дают 104dp при ширине плитки 120dp: запас 4dp, чтобы
 * ряд не сплющивал последнюю кнопку при округлениях.
 *
 * **Снизу его нет** — там стоит [MUSHROOM_PHOTO_INSET], и это не экономия места, а выравнивание.
 * Картинка отбита от края плитки снизу ровно на этот инсет, и зазор от неё же до нижнего края
 * жетонов обязан выйти таким же: над картинкой и под ней одинаковые поля, иначе она выглядит
 * сдвинутой вниз, а плитка — раздутой по высоте (репорт владельца 2026-09-29). Отступы жетонов от
 * рамы — сверху и по бокам — при этом остаются прежними: они про то, чтобы две деревянные
 * поверхности не сходились встык, и сокращать их незачем. */
private val COUNT_ROW_PADDING = 6.dp

/** Слот под число находок между кнопками. Фиксированный, а не по содержимому: иначе кнопки
 * «+» и «−» ездили бы по плитке при переходе через десяток. */
private val COUNT_WIDTH = 28.dp

/**
 * Кегль числа находок — по числу знаков, потому что слот [COUNT_WIDTH] один на все значения.
 *
 * Однозначное число (а это почти всякая прогулка) набирается 24sp вместо прежних 20sp: владелец
 * просил цифру крупнее и читаемее с вытянутой руки (2026-09-30). Двузначное осталось на 20sp, а
 * трёхзначное — на 14sp: 24sp на два знака при системном шрифте крупнее обычного уже не
 * помещаются в слот, а расширить слот нечем — ряд и так занимает 104dp из 108dp доступных внутри
 * плитки, и оставшийся зазор держит его от рассыпания на округлениях дробной плотности.
 *
 * Ступенька видна при переходе 9 → 10, и это сознательно: альтернатива — один кегль на все случаи,
 * то есть самый мелкий из трёх у всех подряд ради двух-трёх прогулок за сезон, где вид набрали
 * сотней.
 */
private fun countFontSize(count: Int): TextUnit = when (count.toString().length) {
    1 -> 24.sp
    2 -> 20.sp
    else -> 14.sp
}

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
 * Полная высота плитки ленты «Записи»: строка счётчика — её верхний отступ и сама кнопка, снизу
 * отступа у ряда нет — плюс площадка фото (её ширина равна ширине плитки, высота — по
 * [MUSHROOM_PHOTO_ASPECT_RATIO]). [AddSpeciesTile] задаёт себе эту высоту явно — у него другое
 * внутреннее устройство, совпасть с [MushroomTile] по построению он не может, а лента обязана
 * читаться одной ровной полосой.
 *
 * Слагаемые перечислены здесь ровно те же, что складывает вёрстка [MushroomTile]: до правки
 * 2026-09-29 отступ ряда в этой сумме отсутствовал вовсе, и плитка «добавить свой вид» была на
 * 12dp ниже соседних — лента шла с провалом на последней плитке.
 */
val RECORD_TILE_HEIGHT =
    COUNT_ROW_PADDING + MUSHROOM_COUNT_BUTTON_SIZE + RECORD_MUSHROOM_TILE_WIDTH / MUSHROOM_PHOTO_ASPECT_RATIO

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
                    // Отступ появился вместе с деревянной подложкой кнопок: пока «+» и «−» были
                    // голыми значками, их поле никак не читалось, и край плашки рядом никому не
                    // мешал. У жетона край есть, и он обязан не касаться рамы плашки — иначе две
                    // деревянные поверхности сходятся встык и плитка выглядит собранной из
                    // обрезков. Снизу его нет намеренно — см. [COUNT_ROW_PADDING].
                    .padding(start = COUNT_ROW_PADDING, top = COUNT_ROW_PADDING, end = COUNT_ROW_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Обе кнопки — один и тот же [CountButton]: после трёх правок подряд стало
                // ясно, что двумя разными сборками одинаковых на вид кнопок это не кончится.
                CountButton(
                    icon = Icons.Filled.Remove,
                    onClick = remove,
                    enabled = count > 0,
                )
                Text(
                    text = count.toString(),
                    fontSize = countFontSize(count),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.width(COUNT_WIDTH),
                )
                CountButton(
                    icon = Icons.Filled.Add,
                    onClick = add,
                    enabled = count < MAX_MUSHROOM_FINDS_PER_WALK,
                    onLongHold = onBulkAdd,
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
/**
 * Кнопка счёта находок — «+» и «−» на плитке вида, одна сборка на обе.
 *
 * **Почему не `IconButton`.** Тот приносит с собой две вещи, которые здесь мешают: круглую форму,
 * которой он КЛИПУЕТ своё содержимое (квадратный жетон приезжал обрезанным в круг), и
 * `minimumInteractiveComponentSize()` — узел 48dp при видимых 40dp. Второе особенно дорого: плитка
 * шириной [RECORD_MUSHROOM_TILE_WIDTH], и две области нажатия по 48dp вместе со счётчиком в неё
 * физически не помещаются — `Row` доезжал до последнего ребёнка с нехваткой ширины и сплющивал
 * «+» примерно до 25dp. Пока фона у кнопок не было, разница не читалась; с деревянной подложкой
 * она стала видна сразу (репорт владельца 2026-09-26).
 *
 * Отсюда размеры: [MUSHROOM_COUNT_BUTTON_SIZE] вместе со счётчиком и отбивками укладывается в
 * ширину плитки с запасом, и ОБЕ кнопки получают его одинаково. Это сознательный отказ от
 * рекомендованных Material 48dp: на плитке в 120dp две таких области не существуют, а честные
 * 38dp лучше, чем 48dp у одной кнопки и сплющенные 25dp у соседней.
 *
 * [onLongHold] — только у «+»: удержание открывает массовое добавление. У «−» его нет, и
 * [tapOrHold] в этом случае работает как обычное нажатие.
 */
@Composable
private fun CountButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onLongHold: (() -> Unit)? = null,
    onHoldProgress: (Float) -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
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
            imageVector = icon,
            contentDescription = null,
            // Явный цвет, а не `LocalContentColor`: на жетоне значок всегда светлый (доска тёмная
            // в обеих темах), и гашение выключенного состояния приходится повторить рукой.
            tint = glyphBadgeContentColor(LocalContentColor.current).copy(alpha = if (enabled) 1f else 0.38f),
            modifier = Modifier.size(
                glyphBadgeGlyphSize(onBadge = COUNT_ICON_SIZE_ON_BADGE, fallback = COUNT_ICON_SIZE_BARE),
            ),
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

        // Белый текст с чёрной обводкой — у ОБЕИХ редакций. `design.md` (раздел 8) предлагал
        // российской «табличку»: тёмную плашку со светлым текстом вместо обводки. Собрано и
        // отвергнуто владельцем на устройстве (2026-09-26): «фон под надписями названий тут явно
        // ни к чему». Там же отвергнуты белый мат и алая «картинная» рама вокруг фотографии —
        // плитка остаётся ровно такой, какой была.
        // Высоты у подписи НЕТ, и это не упущение. Была фиксированная (54dp = две строки по 20sp
        // плюс запас), и она же обрезала вторую строку при крупном системном шрифте: буквы растут,
        // 54dp — нет, и «Подосиновик» читался как «Подоси / нов…» с отрезанной наполовину нижней
        // строкой (репорт владельца 2026-09-27, системный шрифт ×2). Подпись прижата к низу
        // плитки, поэтому без фиксированной высоты она занимает ровно столько, сколько нужно
        // буквам, и растёт вверх на фотографию. При обычном шрифте геометрия прежняя до пикселя:
        // нижний край подписи задают те же отступы.
        MushroomOutlinedText(
            text = categoryDisplayName(category),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
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
        // Многоточие у ОБЕИХ надписей, иначе обводка и заливка обрежутся по-разному. Без него
        // лишняя строка просто не рисовалась, и длинное название выглядело как другое, короткое:
        // грузинское «დათვის სოკო» при системном шрифте ×2 читалось как «დათვი / ს», без намёка
        // на то, что слово не поместилось (замечание владельца 2026-09-27).
        Text(
            text = text,
            style = style.copy(color = Color.Black, drawStyle = Stroke(width = strokeWidthPx)),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = text,
            style = style.copy(color = Color.White),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
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
