package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.theme.LeshyTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * «Земля» — фоновая текстура дерева под содержимым раздела (`docs/russia-edition/design.md`,
 * раздел 7). Рисуется в двух местах: [SectionScaffold] (все разделы верхнего уровня) и полотно
 * выдвижного меню (`App.kt`).
 *
 * **Развилка идёт по токену, а не по редакции.** `groundTexture == null` — не рисуется ничего, и
 * под содержимым остаётся тот же сплошной цвет, что был всегда; у мировой редакции токен null
 * постоянно, у российской это заранее предусмотренный аварийный выход, если на устройстве
 * текстура не понравится. Одно значение в наборе величин, и остальное оформление продолжает
 * работать.
 *
 * Текстура не замощается: это одна полноэкранная картинка с [ContentScale.Crop]. Бесшовный тайл
 * генеративные модели почти никогда не дают, а подгонка швов — отдельная работа с
 * непредсказуемым результатом (`design.md`, раздел 7). Кроп ей безразличен по построению: доска
 * снята ровным полем без бликов, виньетки и уникальных примет, поэтому любой её кусок годится
 * как целое — требования к исходнику в `docs/russia-edition/brief-wood-boards.md`.
 */
@Composable
fun GroundTexture(modifier: Modifier = Modifier) {
    val ground = LeshyTheme.tokens.groundTexture ?: return
    Image(
        painter = painterResource(ground),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

/**
 * Доска фоном произвольной поверхности — общий механизм для всех текстур редакции.
 *
 * **`Modifier.paint` для этого не годится, ни с каким значением его параметров.** Проверено на
 * устройстве дважды (репорты владельца 2026-09-26): с `sizeToIntrinsics = true` (дефолт) узел
 * получает размер САМОЙ КАРТИНКИ — лента видов на «Записи» раздулась до 1672 точек высоты доски и
 * схлопнула карту в полоску; с `sizeToIntrinsics = false` тот же модификатор возвращает
 * `minWidth = maxWidth, minHeight = maxHeight`, то есть заставляет узел занять ВСЁ доступное
 * место — лента снова во весь экран, только по другой причине. Оба режима участвуют в измерении,
 * а фону это запрещено по определению: его размер задаёт то, подо что он подложен.
 *
 * Поэтому здесь — [drawWithCache], который в измерении не участвует вовсе. Кадрирование
 * ([ContentScale.Crop]) приходится считать руками: доска масштабируется до наибольшего из двух
 * отношений сторон, центрируется и обрезается по узлу.
 *
 * [fallback] — чем закрашивается место, когда доски нет (мировая редакция, аварийно выключенная
 * текстура). `null` означает «не закрашивать ничем»: у поверхности уже есть свой фон, и второй
 * ей не нужен.
 */
@Composable
private fun Modifier.woodTexture(board: DrawableResource?, fallback: Color?): Modifier {
    if (board == null) return if (fallback != null) background(fallback) else this
    return croppedBehind(painterResource(board))
}

/** Отрисовка [painter] фоном узла с кадрированием по центру. Без участия в измерении. */
private fun Modifier.croppedBehind(painter: Painter): Modifier = drawWithCache {
    val intrinsic = painter.intrinsicSize
    val scale = if (intrinsic.isSpecified && intrinsic.width > 0f && intrinsic.height > 0f) {
        maxOf(size.width / intrinsic.width, size.height / intrinsic.height)
    } else {
        1f
    }
    val drawn = if (scale == 1f && !intrinsic.isSpecified) {
        size
    } else {
        Size(intrinsic.width * scale, intrinsic.height * scale)
    }
    onDrawBehind {
        clipRect {
            translate(left = (size.width - drawn.width) / 2f, top = (size.height - drawn.height) / 2f) {
                with(painter) { draw(drawn) }
            }
        }
    }
}

/**
 * Земля фоном поверхности, которой картинку передать нечем (`ModalDrawerSheet`, лента «Записи»).
 *
 * **Кадрируется по окну, а не по узлу, и это не оптимизация, а единственный способ избежать шва.**
 * Полотно земли рисует `LeshyTheme` на весь экран; поверхность, которая кладёт себе СВОЮ копию
 * доски, кадрирует её по собственному размеру — рисунок волокна в ней не совпадает с тем, что
 * идёт рядом, и на границе видно стык двух разных кусков дерева. Репорт владельца 2026-09-26:
 * лента «Записи» не сходилась с землёй, видной ниже неё, в полосе системной навигации.
 *
 * Поэтому здесь доска растягивается и центрируется так, как если бы рисовалась во весь экран, и
 * узел показывает ровно тот её кусок, что лежит под ним. Сколько бы поверхностей ни несли землю,
 * все они — окна в одно и то же полотно.
 *
 * Досок карточек, кнопок и жетонов это НЕ касается (`cardBackground`, `buttonBackground`): те —
 * отдельные предметы, и каждый показывает свой кусок дерева целиком. Общее полотно сделало бы
 * две соседние карточки одной доской без края между ними.
 */
@Composable
fun Modifier.groundBackground(fallback: Color? = null): Modifier {
    val board = LeshyTheme.tokens.groundTexture ?: return if (fallback != null) background(fallback) else this
    val painter = painterResource(board)
    var window by remember { mutableStateOf(WindowPlacement.Unknown) }
    return this
        .onGloballyPositioned { coordinates ->
            val root = coordinates.findRootCoordinates()
            window = WindowPlacement(
                offset = root.localPositionOf(coordinates, Offset.Zero),
                size = Size(root.size.width.toFloat(), root.size.height.toFloat()),
            )
        }
        .drawWithCache {
            val target = if (window.size.isSpecified && window.size.width > 0f) window.size else size
            val intrinsic = painter.intrinsicSize
            val scale = if (intrinsic.isSpecified && intrinsic.width > 0f && intrinsic.height > 0f) {
                maxOf(target.width / intrinsic.width, target.height / intrinsic.height)
            } else {
                1f
            }
            val drawn = if (intrinsic.isSpecified) Size(intrinsic.width * scale, intrinsic.height * scale) else target
            // Смещение узла внутри окна вычитается: узел рисует тот кусок полотна, который под ним.
            val left = (target.width - drawn.width) / 2f - window.offset.x
            val top = (target.height - drawn.height) / 2f - window.offset.y
            onDrawBehind {
                clipRect {
                    translate(left = left, top = top) {
                        with(painter) { draw(drawn) }
                    }
                }
            }
        }
}

/** Положение узла внутри окна — то, чем [groundBackground] превращает свой кусок в окно в полотно. */
@Immutable
private data class WindowPlacement(val offset: Offset, val size: Size) {
    companion object {
        /** До первого размещения окна ещё нет: узел кадрирует доску по себе, как делал раньше. */
        val Unknown = WindowPlacement(Offset.Zero, Size.Unspecified)
    }
}

/** Доска карточек — плитки видов, карточки прогулок. */
@Composable
fun Modifier.cardBackground(fallback: Color? = null): Modifier =
    woodTexture(LeshyTheme.tokens.cardTexture, fallback)

/**
 * Доска заливных кнопок.
 *
 * **Рисуется по ВИДИМОЙ плашке, а не по узлу, и это главное здесь.** `Button` у Material — это
 * `Surface(onClick)`, обёрнутый в `minimumInteractiveComponentSize()`: область нажатия 48dp, сама
 * кнопка 40dp ([ButtonDefaults.MinHeight]), поверхность с обводкой рисуется по центру узла. Фон,
 * положенный на узел, торчал из-под обводки на 4dp сверху и снизу (репорты владельца 2026-09-26:
 * сначала «обводка идёт явно не по границе», потом «Done другого размера относительно Cancel»).
 *
 * Первая попытка чинила это минимальной высотой узла в 48dp — и делала деревянную кнопку выше
 * соседней контурной, то есть меняла одну несообразность на другую. Правильный путь обратный:
 * узел остаётся областью нажатия, а доска обрезается по плашке.
 */
@Composable
fun Modifier.buttonBackground(
    fallback: Color? = null,
    shape: Shape = LeshyTheme.tokens.shapeButton,
): Modifier {
    val board = LeshyTheme.tokens.buttonTexture ?: return woodTexture(null, fallback)
    return plateTexture(board, ButtonDefaults.MinHeight, shape)
}

/**
 * Доска, обрезанная по плашке высотой [plateHeight], стоящей по центру узла.
 *
 * Общий механизм для всех мест, где Material разводит область нажатия и видимую поверхность:
 * заливные кнопки ([buttonBackground]) и выбранный сегмент переключателя
 * ([selectedSegmentBackground]). Узел выше плашки — доска обрезается; узел равен плашке (кнопка с
 * длинной подписью в две строки) — обрезать нечего, и модификатор ведёт себя как обычный фон.
 */
@Composable
private fun Modifier.plateTexture(board: DrawableResource, plateHeight: Dp, shape: Shape): Modifier {
    val painter = painterResource(board)
    return drawWithCache {
        val visibleHeight = minOf(size.height, maxOf(plateHeight.toPx(), 0f))
        val top = (size.height - visibleHeight) / 2f
        val visible = Size(size.width, visibleHeight)
        val clip = Path().apply {
            addOutline(shape.createOutline(visible, layoutDirection, this@drawWithCache))
            translate(Offset(0f, top))
        }
        val intrinsic = painter.intrinsicSize
        val scale = if (intrinsic.isSpecified && intrinsic.width > 0f && intrinsic.height > 0f) {
            maxOf(visible.width / intrinsic.width, visible.height / intrinsic.height)
        } else {
            1f
        }
        val drawn = if (intrinsic.isSpecified) Size(intrinsic.width * scale, intrinsic.height * scale) else visible
        onDrawBehind {
            clipPath(clip) {
                translate(
                    left = (visible.width - drawn.width) / 2f,
                    top = top + (visible.height - drawn.height) / 2f,
                ) {
                    with(painter) { draw(drawn) }
                }
            }
        }
    }
}

/**
 * Доска под ВЫБРАННЫМ сегментом ряда-переключателя («Экспорт»/«Импорт», инструменты редактора
 * значка).
 *
 * Выбранное положение Material красит сплошным `secondaryContainer` — на экране, где всё
 * остальное деревянное, это единственная плоская заливка, и владелец попросил заменить её на ту
 * же доску, что у заливных кнопок (2026-09-26).
 *
 * **Собственной минимальной высоты, в отличие от [buttonBackground], здесь нет.** Ряд
 * переключателя выравнивает сегменты по одной высоте, и 48dp у выбранного против 40dp у соседних
 * разорвали бы ряд.
 *
 * Невыбранные сегменты и мировая редакция получают модификатор неизменным: там заливки и не было.
 */
@Composable
fun Modifier.selectedSegmentBackground(selected: Boolean, shape: Shape): Modifier {
    val board = LeshyTheme.tokens.buttonTexture
    if (!selected || board == null) return this
    // Та же грабля, что у кнопок, и то же лечение: `SegmentedButton` тоже оборачивает себя в
    // `minimumInteractiveComponentSize()`, а высоту плашки держит своей приватной константой.
    return plateTexture(board, SEGMENT_CONTAINER_HEIGHT, shape)
}

/** Высота плашки сегмента у Material — своя константа, потому что чужая приватна. */
private val SEGMENT_CONTAINER_HEIGHT = 40.dp

/**
 * Цвета ряда-переключателя: у выбранного сегмента контейнер прозрачен (доску рисует
 * [selectedSegmentBackground], а Material закрасил бы её своим цветом поверх), подпись светлая —
 * доска тёмная в обеих темах.
 *
 * Без доски возвращается ровно `SegmentedButtonDefaults.colors()`, то есть мировая редакция
 * остаётся при своём.
 */
@Composable
fun woodenSegmentColors(): SegmentedButtonColors {
    val defaults = SegmentedButtonDefaults.colors()
    if (LeshyTheme.tokens.buttonTexture == null) return defaults
    return SegmentedButtonDefaults.colors(
        activeContainerColor = Color.Transparent,
        activeContentColor = WOOD_CONTENT_COLOR,
    )
}

/**
 * Цвет полотна, поверх которого лежит земля: прозрачный, когда текстура есть, и [fallback] —
 * когда её нет.
 *
 * Нужен потому, что `Scaffold` и `ModalDrawerSheet` красят свой фон сами и закрасили бы картинку.
 * [fallback] — не украшение сигнатуры: у `Scaffold` дефолт `background`, а у полотна меню свой
 * (`leshyDrawerContainerColor`, у мировой редакции это `surfaceContainerLow`, другой тон). Один
 * зашитый цвет на оба места сдвинул бы мировое меню на полтона — ровно то, чего эта правка
 * касаться не должна.
 */
@Composable
fun groundContainerColor(fallback: Color = MaterialTheme.colorScheme.background): Color =
    if (LeshyTheme.tokens.groundTexture != null) Color.Transparent else fallback

/** То же для карточек: прозрачный контейнер, когда доска есть, и [fallback] — когда нет. */
@Composable
fun cardContainerColor(fallback: Color): Color =
    if (LeshyTheme.tokens.cardTexture != null) Color.Transparent else fallback

/**
 * Пометка выбранной карточки ПОВЕРХ доски.
 *
 * Своей заливкой (`secondaryContainer`) карточка пометить себя больше не может: доска рисуется
 * позже контейнера и закрывает его. Поэтому выбранное состояние — притенение самой доски, тем же
 * приёмом, что был у пункта меню: полупрозрачный `onSurface` темнит дерево в светлой теме и
 * высветляет в тёмной, оставляя волокно видимым.
 *
 * Без доски не делает ничего: там метку по-прежнему несёт заливка контейнера, и вторая поверх неё
 * была бы двойной.
 */
@Composable
fun Modifier.cardSelectionTint(selected: Boolean): Modifier {
    if (!selected || LeshyTheme.tokens.cardTexture == null) return this
    return background(MaterialTheme.colorScheme.onSurface.copy(alpha = CARD_SELECTED_ALPHA))
}

/** Насколько притеняется доска под выбранной карточкой: видно с расстояния вытянутой руки и мало,
 * чтобы волокно не пропало. */
private const val CARD_SELECTED_ALPHA = 0.14f

/**
 * Цвета заливной кнопки, лежащей на доске: контейнер прозрачен (иначе Material закрасит доску
 * своим цветом ПОВЕРХ неё), подпись светлая, выключенное состояние гасится той же долей, что
 * гасит содержимое сам Material.
 *
 * Без доски [base] возвращается как есть — вызывающий получает ровно то, что передал.
 */
@Composable
fun woodenButtonColors(base: ButtonColors): ButtonColors {
    if (LeshyTheme.tokens.buttonTexture == null) return base
    return base.copy(
        containerColor = Color.Transparent,
        contentColor = WOOD_CONTENT_COLOR,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = WOOD_CONTENT_COLOR.copy(alpha = WOOD_DISABLED_ALPHA),
    )
}

/** Доля, которой гасится подпись выключенной кнопки на доске — та же, что у Material. */
const val WOOD_DISABLED_ALPHA = 0.38f

/**
 * Цвет содержимого на доске.
 *
 * Доски кнопок и жетонов темны в ОБЕИХ темах — это растр, он не переключается вместе с
 * оформлением, — поэтому текст и значки на них всегда светлые, а не по теме. Без этого в светлой
 * теме на тёмном дереве оказался бы тёмный глиф.
 */
val WOOD_CONTENT_COLOR = Color(0xFFF7EFE2)
