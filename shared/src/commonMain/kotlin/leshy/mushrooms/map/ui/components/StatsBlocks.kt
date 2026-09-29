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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import leshy.mushrooms.map.domain.model.Category
import leshy.mushrooms.map.i18n.StringKey
import leshy.mushrooms.map.i18n.stringResource
import leshy.mushrooms.map.presentation.archive.CategoryCount
import leshy.mushrooms.map.ui.util.parseHexColor
import leshy.mushrooms.map.ui.theme.LeshyTheme

import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource
import kotlin.math.ceil

/**
 * Строительные блоки «страницы-детализации»: показатель плашкой, находки плитками и заголовок
 * раздела между ними. Живут здесь, а не в экране детализации прогулки, где были написаны, потому
 * что ровно из них же собран экран «Карта находок» — та же страница, но по всем прогулкам сразу.
 */

/** Значок пустого состояния «находок не зафиксировано» — при тексте, поэтому и размер обычный
 * значка при тексте. С величиной значка В плашке показателя ([METRIC_GLYPH_SIZE]) не связан: там
 * значок стоит вместо текста, а не при нём. */
val METRIC_ICON_SIZE = 28.dp

/**
 * Значок в плашке показателя — крупнее общеприложенческих 24dp, и это сознательное исключение из
 * «везде один размер».
 *
 * Причина в том, что в плашке значок — не пометка при тексте, а половина её содержимого: подписи
 * словами у показателя нет вовсе (см. `WalkMetricsRow` в `WalkDetailScreen.kt`), значок называет
 * показатель сам. При 24dp он читался как значок при отсутствующем тексте — мелкий на плашке
 * высотой под 130dp (репорт владельца 2026-09-29). Прежние 28dp ([METRIC_ICON_SIZE]) до жетонов
 * были ближе к делу, но жетон их не унаследовал: безжетонный путь [GlyphBadge] рисовал дефолтные
 * 24dp, и мировая редакция тихо потеряла 4dp.
 *
 * Величина одна на обе редакции — задаётся ГЛИФ, а сторона жетона под ним выводится
 * ([badgeSizeForGlyph]). Наоборот не работает: сторону жетона мировая редакция не видит.
 */
private val METRIC_GLYPH_SIZE = 32.dp

/** Сторона жетона под значком блока статистики — та, на которой глиф выходит [METRIC_GLYPH_SIZE]
 * при пропорции жетона бокового меню (24 из 38). */
private val METRIC_BADGE_SIZE = badgeSizeForGlyph(METRIC_GLYPH_SIZE)

/** Поля плашки показателя по вертикали и отбивка между значком и значением — они же слагаемые
 * наименьшей высоты, см. [metricCardMinHeight]. */
private val METRIC_CARD_VERTICAL_PADDING = 12.dp
private val METRIC_CARD_GAP = 6.dp

/**
 * Наименьшая высота плашки показателя: поля, значок и ДВЕ строки значения — столько, сколько занял
 * бы самый высокий из показателей ряда.
 *
 * Задана снизу, а не выведена из содержимого каждой плашки: значения переносятся каждое по своей
 * нужде («24» — одна строка, «12.34 км» — две), и плашки натуральной высоты встали бы в ряд
 * ступенькой. `IntrinsicSize` эту работу не делает: минимальная внутренняя высота текста меряется
 * по бесконечной ширине, то есть по одной строке, и двухстрочному значению её не хватило бы. Раз
 * при `maxLines = 2` содержимое выше этого числа не бывает, минимум оказывается и максимумом —
 * плашки выходят равными без общей высоты у ряда.
 *
 * **Считается, а не стоит числом.** Стояло — 116dp, выведенные под мировую редакцию (глиф 24dp,
 * `titleLarge`), и в российской они не значили ничего: жетон там 44dp вместо 24 и кегль на 10%
 * крупнее, так что двухстрочная плашка вырастала до ~137dp, а однострочная оставалась на 116 — ряд
 * шёл ступенькой в 20dp. Теперь оба слагаемых берутся те же, что рисуются, поэтому равенство
 * держится в любой редакции и при любом кегле значения.
 *
 * Перевод sp→dp идёт через плотность, то есть высота растёт вместе с системным размером шрифта —
 * ровно затем, чтобы при крупном шрифте плашка росла вслед за содержимым, а не обрезала его.
 */
@Composable
private fun metricCardMinHeight(fontSize: TextUnit): Dp {
    val lines = with(LocalDensity.current) { (fontSize * METRIC_VALUE_LINE_HEIGHT_RATIO * 2f).toDp() }
    return METRIC_CARD_VERTICAL_PADDING * 2 +
        glyphBadgeFootprint(size = METRIC_BADGE_SIZE, glyphSize = METRIC_GLYPH_SIZE) +
        METRIC_CARD_GAP +
        lines
}

/**
 * Межстрочное расстояние значения долей кегля, а не по метрикам гарнитуры: кегль здесь подбирается
 * ([MetricValueScale]), и межстрочное обязано ехать за ним — иначе при ужатом кегле строки
 * разъезжаются, а при крупном слипаются. Доля — та же, что у `titleLarge` Material (28 из 22),
 * то есть вид двухстрочного значения не менялся.
 */
private const val METRIC_VALUE_LINE_HEIGHT_RATIO = 28f / 22f

/**
 * Куда [MetricCard] позволено ужать значение, если оно не встало в две строки.
 * Ниже этого — уже не «мелко, но читается», а «не прочесть с вытянутой руки», и лучше пусть
 * плашка вырастет вниз (её высота задана только снизу), чем значение станет нечитаемым.
 */
private val METRIC_VALUE_MIN_FONT_SIZE = 16.sp
private val METRIC_VALUE_FONT_STEP = 2.sp

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
 * Кегль значения, **общий на все плашки экрана**.
 *
 * Зачем общий. Плашки стоят рядом и читаются как один прибор: четыре разных кегля в четырёх
 * одинаковых окошках выглядят поломкой, а не подгонкой (требование владельца 2026-09-29). Пока
 * кегль подбирала каждая плашка сама (`TextAutoSize` внутри `Text`), расхождение было делом случая
 * — оно и не проявлялось только потому, что прежние 22sp влезали почти всегда; с более крупным
 * кеглем случай наступает на первом же узком экране с длинным «12 ч 05 мин».
 *
 * Как подбирается. Сверху вниз от кегля [rememberMetricValueScale] шагами
 * [METRIC_VALUE_FONT_STEP]: плашка, которой содержимое не встало в две строки, зовёт [shrinkToFit],
 * и кегль уменьшается У ВСЕХ. Движение только в одну сторону, поэтому подбор сходится (в худшем
 * случае — на [METRIC_VALUE_MIN_FONT_SIZE]) и не может зациклиться на двух плашках, тянущих кегль
 * в разные стороны. Цена — кадр-два на шаг, пока значения раскладываются; сбрасывается подбор
 * только сменой самих значений, то есть при обычной прокрутке экрана его не видно.
 *
 * Ужиматься ниже [METRIC_VALUE_MIN_FONT_SIZE] нечему: значение переносится на вторую строку
 * («4 ч 18 мин» и «12.34 км» в плашку шириной около 100dp одной строкой не встают ни при каком
 * читаемом с вытянутой руки кегле), и двух строк не хватает только сводным значениям вроде
 * «14 д 7 ч 30 мин», которых у одной прогулки не бывает, — а они стоят на вдвое более широких
 * плашках «Карты находок».
 */
@Stable
class MetricValueScale internal constructor(maxFontSize: TextUnit) {
    var fontSize: TextUnit by mutableStateOf(maxFontSize)
        private set

    // Арифметика по числу в sp, а не операторами `TextUnit`: сложение и вычитание у него есть
    // только между величинами одного типа и тут не выводятся, а обе участвующие величины заданы в
    // sp по построению.
    internal fun shrinkToFit() {
        if (fontSize > METRIC_VALUE_MIN_FONT_SIZE) {
            fontSize = (fontSize.value - METRIC_VALUE_FONT_STEP.value).sp
        }
    }
}

/**
 * Подбор кегля на ряд плашек. [values] — сами значения, и они здесь ключ памяти: сменились
 * значения — подбор начинается заново с потолка, иначе однажды ужатый кегль остался бы ужатым и
 * после того, как длинное значение сменилось коротким.
 *
 * Потолок — `headlineMedium` шкалы, то есть 28sp в мировой редакции и на ступень крупнее в
 * российской (`LeshyTokens.typeScaleStep`). Взят из шкалы, а не числом: значение в плашке — самая
 * крупная надпись экрана после его заголовка, и шкале оно принадлежит наравне с ним. Прежний
 * потолок `titleLarge` (22sp) владелец на устройстве назвал мелким в обеих редакциях
 * (2026-09-29) — и мельче значка, который рядом вырос до [METRIC_GLYPH_SIZE].
 */
@Composable
fun rememberMetricValueScale(values: List<String>): MetricValueScale {
    val maxFontSize = MaterialTheme.typography.headlineMedium.fontSize
    return remember(maxFontSize, values) { MetricValueScale(maxFontSize) }
}

/**
 * [value] переносится на вторую строку, а не ужимается, и кегль у него общий на весь ряд плашек —
 * см. [MetricValueScale]. Обрезки хвоста не бывает ни в каком случае: значение, у которого не видно
 * конца, хуже мелкого.
 *
 * [label] — название показателя словами. На плашке его нет (значок называет показатель сам, см.
 * `WalkMetricsRow`), оно уходит в `contentDescription` значка, чтобы чтение вслух ничего не
 * теряло.
 */
@Composable
fun MetricCard(
    icon: Painter,
    label: String,
    value: String,
    scale: MetricValueScale,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        border = cardFrameBorder(),
        // Доска вместо заливки: блок статистики — такая же дощечка на земле, как карточка вида
        // (`design.md`, раздел 7). Цвет содержимого задаётся явно, потому что прозрачному
        // контейнеру Material подобрать его не из чего.
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor(MaterialTheme.colorScheme.surfaceContainer),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
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
                .cardBackground()
                .heightIn(min = metricCardMinHeight(scale.fontSize))
                .padding(vertical = METRIC_CARD_VERTICAL_PADDING, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(METRIC_CARD_GAP, Alignment.CenterVertically),
        ) {
            // Жетон под значком — тот же, что в боковом меню и на кнопках счёта: согласованность
            // подложек по всему приложению (требование владельца 2026-09-26). Размер сказан обоими
            // числами, потому что видимого значка это касается в обеих редакциях: без жетона
            // (мировая) рисуется один глиф, и он обязан выйти той же величины, что глиф на жетоне.
            GlyphBadge(
                painter = icon,
                size = METRIC_BADGE_SIZE,
                glyphSize = METRIC_GLYPH_SIZE,
                contentDescription = label,
            )
            Text(
                text = value,
                // Кегль — общий на ряд, не свой у каждой плашки (см. [MetricValueScale]); о том,
                // что содержимое не встало, плашка сообщает подбору сама, по итогу раскладки.
                fontSize = scale.fontSize,
                lineHeight = scale.fontSize * METRIC_VALUE_LINE_HEIGHT_RATIO,
                onTextLayout = { if (it.hasVisualOverflow) scale.shrinkToFit() },
                style = MaterialTheme.typography.headlineMedium,
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
        ).toInt().coerceAtLeast(LeshyTheme.tokens.findTileMinColumns)
        // Ширина плитки делится В ПИКСЕЛЯХ, целочисленно, а не в Dp — и это не придирка к точности,
        // а единственный способ, чтобы ряд вообще собрался. Меряет FlowRow в пикселях: и ширину
        // плитки, и отбивку он получает через `roundToPx()`, каждую округляя ОТДЕЛЬНО. При дробной
        // плотности экрана сумма округлений вылезает на пиксель за ширину ряда — и FlowRow, честно
        // увидев переполнение, переносит вторую плитку вниз. Получается колонка половинных плиток
        // посреди пустого экрана. Наблюдалось на Android с плотностью 2.8125: отбивка 8dp = 22.5px
        // округляется вверх до 23, а плитка считалась в Dp ИСХОДЯ из ровных 22.5 — ряду не хватало
        // ровно одного пикселя. На iPhone с целой плотностью 2.0 того же экрана всё сходилось, и
        // две колонки стояли как задумано.
        val tileWidth = with(LocalDensity.current) {
            val spacingPx = FIND_TILE_SPACING.roundToPx()
            // Деление нацело: остаток (меньше пикселя на плитку) достаётся отбивке, а не наоборот.
            ((constraints.maxWidth - spacingPx * (columns - 1)) / columns).toDp()
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FIND_TILE_SPACING, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(FIND_TILE_SPACING),
            maxItemsInEachRow = columns,
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
        // Та же доска, что под плиткой вида на «Записи» и под блоками статистики: плитка находки
        // на экранах детализации и сводной карты — такой же предмет, лежащий на земле, и
        // выпадать из общего материала ей незачем.
        colors = CardDefaults.cardColors(
            containerColor = cardContainerColor(MaterialTheme.colorScheme.surfaceContainerLow),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Box(modifier = Modifier.fillMaxWidth().cardBackground()) {
            MushroomPhoto(
                category = category,
                modifier = Modifier.fillMaxWidth().aspectRatio(LeshyTheme.tokens.photoAspectRatio),
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
