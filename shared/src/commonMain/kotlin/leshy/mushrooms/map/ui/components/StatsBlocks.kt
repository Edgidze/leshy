package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.archive.CategoryCount
import leshy.mushrooms.map.ui.util.parseHexColor
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource
import kotlin.math.ceil

/**
 * Строительные блоки «страницы-детализации»: показатель плашкой, находки плитками и заголовок
 * раздела между ними. Живут здесь, а не в экране детализации прогулки, где были написаны, потому
 * что ровно из них же собран экран «Карта находок» — та же страница, но по всем прогулкам сразу.
 */

/** Значки показателей — те же три и того же размера, что в шапке «Записи» (`RecordScreen.kt`). */
val METRIC_ICON_SIZE = 28.dp

/**
 * Высота карточки показателя при системном масштабе шрифта — значок, отбивка, две строки
 * `titleLarge` и собственные поля. Задана снизу, а не выведена из содержимого: значения переносятся
 * каждое по своей нужде («24» — одна строка, «12.34 км» — две), и три карточки натуральной высоты
 * встали бы в ряд ступенькой. `IntrinsicSize` эту работу не делает: минимальная внутренняя высота
 * текста меряется по бесконечной ширине, то есть по одной строке, и двухстрочному значению её не
 * хватило бы. Раз при `maxLines = 2` содержимое выше этого числа не бывает, минимум оказывается и
 * максимумом — карточки выходят равными без общей высоты у ряда.
 *
 * Снизу, а не жёстко, — чтобы при крупном системном шрифте карточка росла вслед за содержимым, а
 * не обрезала его. Тогда ряд снова может выйти ступенькой, но ступенька из трёх целых значений
 * лучше трёх подрезанных.
 */
private val METRIC_CARD_MIN_HEIGHT = 116.dp

/**
 * Куда [MetricCard] позволено ужать значение, если оно не встало в две строки `titleLarge`.
 * Ниже этого — уже не «мелко, но читается», а «не прочесть с вытянутой руки», и лучше пусть
 * плашка вырастет вниз (её высота задана только снизу), чем значение станет нечитаемым.
 */
private val METRIC_VALUE_MIN_FONT_SIZE = 13.sp
private val METRIC_VALUE_FONT_STEP = 1.sp

/**
 * Наименьшее число плиток находок в ряду — оно же то, что получается на телефоне вертикально.
 *
 * Две, а не три. Плитка собрана из [MushroomPhoto], а у той подпись с названием вида лежит поверх
 * картинки блоком постоянной высоты (54dp, две строки по 20sp — размер выбран под ленту «Записи»,
 * где плитка шириной 120dp). При трёх колонках плитке достаётся 90–104dp, картинка становится
 * 72–83dp высотой, и подпись съедает три четверти её высоты. При двух колонках плитка выходит
 * 140–160dp, то есть не уже той, под которую подпись и рисовалась.
 */
private const val FIND_TILE_MIN_COLUMNS = 2

/**
 * Потолок ширины плитки. Из него, а не из постоянного числа колонок, считается сам ряд: на широком
 * экране (телефон горизонтально, планшет) двух колонок на всю ширину плитке доставалось бы по
 * 300–400dp, и иллюстрация вида, нарисованная под ленту «Записи» (плитка 120dp), растягивалась бы
 * втрое от своего разрешения — то есть мылилась. Теперь на широком экране растёт ЧИСЛО плиток в
 * ряду, а сама плитка остаётся примерно такой же, как на телефоне вертикально.
 *
 * 200dp — чуть больше того, что выходит на телефоне вертикально (около 176dp на Pixel 4a при
 * полях экрана 16dp), чтобы вертикальная раскладка, ради которой всё и рисовалось, не поехала.
 * Плитка при этом бывает и уже потолка: колонки — целое число, и лишняя ширина делится между ними.
 */
private val FIND_TILE_MAX_WIDTH = 200.dp

private val FIND_TILE_SPACING = 8.dp

/**
 * Кегль счётчика находок на плитке. Крупнее подписи с названием вида ([MUSHROOM_LABEL_FONT_SIZE],
 * 18sp), и это главное про это число: обе надписи лежат на одной плитке, набраны одним приёмом —
 * белым по чёрной обводке — и должны различаться по старшинству, а не спорить. Счётчик тут главнее
 * названия: название вида видно по самой картинке, число по картинке не видно никак.
 */
private val FIND_TILE_COUNT_FONT_SIZE = 28.sp

/**
 * Отступ счётчика от угла плитки. Правый верхний угол выбран не случайно: подпись с названием
 * прижата к нижнему краю, гриб на картинке стоит по центру и растёт снизу вверх, сужаясь к шляпке,
 * — верхние углы у плиток каталога свободны стабильно, в отличие от нижних и середины.
 */
private val FIND_TILE_COUNT_PADDING = 6.dp

/**
 * Добавка к правому отступу счётчика, долей кегля. Нужна затем, чтобы зазор справа от цифры
 * выглядел равным зазору сверху: при одинаковом [FIND_TILE_COUNT_PADDING] с обеих сторон он равным
 * не выглядит, и виноват не отступ, а то, что текстовый блок не облегает цифры.
 *
 * Сверху над цифрами внутри блока лежит подъём шрифта: цифра доходит только до высоты прописной
 * (у Roboto и SF Pro — обе гарнитуры, которыми это в самом деле рисуется, — 0.71 кегля), а блок
 * простирается примерно до 0.90. Справа же от цифры лежит только узкий боковой зазор глифа, около
 * 0.03 кегля. Разница этих двух и есть здешнее число: 0.90 − 0.71 − 0.03 ≈ 0.15.
 *
 * Чёрная обводка на разницу не влияет: она отступает от контура глифа одинаково во все стороны и
 * уменьшает оба зазора на одно и то же.
 *
 * Долей кегля, а не числом в dp, — чтобы поправка росла вместе с системным размером шрифта, как
 * растёт сама цифра и её обводка.
 */
private const val FIND_TILE_COUNT_INK_OVERHANG_EM = 0.15f

/** Отбивка над заголовком раздела — она же задаёт ритм всей страницы-детализации. */
val SECTION_TOP_GAP = 24.dp

/**
 * [value] переносится на вторую строку, а не ужимается: «4 ч 18 мин» и «12.34 км» в блок шириной
 * около 100dp одной строкой не помещаются ни при каком кегле, который ещё читается с вытянутой
 * руки, — а этот экран смотрят в том числе в лесу.
 *
 * И только когда двух строк уже не хватает — на сводном экране это «14 д 7 ч 30 мин» и подобные
 * значения, которых у одной прогулки не бывает, — кегль ужимается, до [METRIC_VALUE_MIN_FONT_SIZE].
 * Обрезки хвоста не бывает ни в каком случае: значение, у которого не видно конца, хуже мелкого.
 */
@Composable
fun MetricCard(icon: Painter, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            // Наименьшая высота стоит на самой колонке, а не на карточке, как стояла раньше, — и
            // только поэтому содержимое вообще можно отцентрировать по высоте. На карточке она
            // растягивала карточку, а колонка внутри оставалась по своему содержимому и прижималась
            // к верху: значение из одной строки висело выше значения из двух, хотя карточки были
            // одной высоты. Теперь лишняя высота достаётся самой колонке, и распределять её внутри
            // есть чему.
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = METRIC_CARD_MIN_HEIGHT)
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            Icon(painter = icon, contentDescription = label, modifier = Modifier.size(METRIC_ICON_SIZE))
            Text(
                text = value,
                // Кегль подбирается под самое значение, а не задан жёстко: на сводном экране в ту
                // же плашку попадает «14 д 7 ч 30 мин» и «1250 км» — двух строк `titleLarge` им не
                // хватает, и при постоянном кегле хвост просто обрезался бы. Уменьшение идёт
                // только когда не влезло; на обычных «24» и «12.34 км» остаётся тот же
                // `titleLarge`, что и был.
                autoSize = TextAutoSize.StepBased(
                    minFontSize = METRIC_VALUE_MIN_FONT_SIZE,
                    maxFontSize = MaterialTheme.typography.titleLarge.fontSize,
                    stepSize = METRIC_VALUE_FONT_STEP,
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

/**
 * Заголовок раздела типографикой, а не подчёркиванием. Подчёркнутый текст в мобильном интерфейсе
 * читается как ссылка — прежние заголовки экрана детализации были подчёркнуты и обещали нажатие,
 * которого не было.
 */
@Composable
fun SectionHeader(title: String, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = SECTION_TOP_GAP, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        action?.invoke()
    }
}

/**
 * Находки плитками с иллюстрацией, названием и числом — вместо прежнего списка строк
 * «название — число». Плитка построена из тех же частей, что плитка ленты «Записи»
 * ([MushroomPhoto] под строкой счётчика, обводка цветом вида), чтобы вид, отмеченный в лесу,
 * выглядел в архиве так же, как выглядел в момент отметки.
 *
 * Ширина плитки считается от ширины экрана, а не задана числом: плитки обязаны ровно закрывать
 * ряд, иначе на узких экранах в ряд встаёт две и треть ширины уходит в пустоту. Число колонок при
 * этом выводится из потолка ширины плитки ([FIND_TILE_MAX_WIDTH]), а не наоборот — там же и зачем.
 *
 * **Неполный ряд стоит по центру, а не прижат влево.** Заполненные ряды закрывают ширину ровно,
 * так что центрирование их не касается вовсе; а вот последнему ряду (и единственному, когда видов
 * меньше, чем колонок) достаётся пустое место, и прижатый влево остаток читался как сбитая
 * вёрстка — особенно на широком экране, где колонок пять, а видов в хвосте одна-две.
 */
@Composable
fun FindTilesGrid(counts: List<CategoryCount>) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        // Округление вверх: колонок берётся столько, чтобы плитка ГАРАНТИРОВАННО не переросла
        // потолок. Ряд при этом всегда закрывается целиком — лишняя ширина делится поровну.
        val columns = ceil(
            (maxWidth + FIND_TILE_SPACING) / (FIND_TILE_MAX_WIDTH + FIND_TILE_SPACING),
        ).toInt().coerceAtLeast(FIND_TILE_MIN_COLUMNS)
        val tileWidth = (maxWidth - FIND_TILE_SPACING * (columns - 1)) / columns
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FIND_TILE_SPACING, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(FIND_TILE_SPACING),
        ) {
            counts.forEach { entry ->
                FindTile(category = entry.category, count = entry.count, width = tileWidth)
            }
        }
    }
}

/**
 * Счётчик стоит НА картинке, а не строкой над ней, как в ленте «Записи». В ленте строка над фото
 * нужна: там между «−» и «+» живёт то самое число, которое меняют пальцем, и оно обязано иметь
 * собственное место, куда не попадёт нажатие по соседней кнопке. Здесь ничего не нажимается,
 * плитка только показывает — и отдельная строка ради одного числа отнимала бы у картинки высоту
 * ни за чем.
 */
@Composable
private fun FindTile(category: Category, count: Int, width: Dp) {
    Card(
        modifier = Modifier.width(width),
        border = BorderStroke(2.dp, parseHexColor(category.colorHex)),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            MushroomPhoto(
                category = category,
                modifier = Modifier.fillMaxWidth().aspectRatio(MUSHROOM_PHOTO_ASPECT_RATIO),
            )
            val countInkOverhang = with(LocalDensity.current) {
                (FIND_TILE_COUNT_FONT_SIZE.toPx() * FIND_TILE_COUNT_INK_OVERHANG_EM).toDp()
            }
            MushroomOutlinedText(
                text = count.toString(),
                fontSize = FIND_TILE_COUNT_FONT_SIZE,
                maxLines = 1,
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    // Только верх и правый край: счётчик прижат к правому верхнему углу, и до
                    // левого края с низом ему дела нет.
                    .padding(
                        top = FIND_TILE_COUNT_PADDING,
                        end = FIND_TILE_COUNT_PADDING + countInkOverhang,
                    ),
            )
        }
    }
}

/**
 * Пустых находок быть не запрещено — вышел, походил, не нашёл (а на сводном экране это ещё и
 * фильтр, под который ничего не подошло). Раньше про это говорил подчёркнутый заголовок «Находок
 * не зафиксировано» на месте списка; теперь это отдельный приглушённый блок, который не
 * притворяется разделом с содержимым.
 */
@Composable
fun FindsEmptyBlock() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = SECTION_TOP_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(METRIC_ICON_SIZE),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(StringKey.WalkDetailFindsEmpty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
