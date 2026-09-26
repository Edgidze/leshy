package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
 * `Modifier.paint` рисуется ДО содержимого узла, то есть работает как фон; собственная заливка
 * поверхности при этом обязана быть прозрачной, иначе она ляжет поверх.
 *
 * [fallback] — чем закрашивается место, когда доски нет (мировая редакция, аварийно выключенная
 * текстура). `null` означает «не закрашивать ничем»: у поверхности уже есть свой фон, и второй
 * ей не нужен.
 */
@Composable
private fun Modifier.woodTexture(board: DrawableResource?, fallback: Color?): Modifier {
    if (board == null) return if (fallback != null) background(fallback) else this
    return paint(painterResource(board), contentScale = ContentScale.Crop)
}

/** Земля фоном поверхности, которой картинку передать нечем (`ModalDrawerSheet`, лента «Записи»). */
@Composable
fun Modifier.groundBackground(fallback: Color? = null): Modifier =
    woodTexture(LeshyTheme.tokens.groundTexture, fallback)

/** Доска карточек — плитки видов, карточки прогулок. */
@Composable
fun Modifier.cardBackground(fallback: Color? = null): Modifier =
    woodTexture(LeshyTheme.tokens.cardTexture, fallback)

/** Доска заливных кнопок. */
@Composable
fun Modifier.buttonBackground(fallback: Color? = null): Modifier =
    woodTexture(LeshyTheme.tokens.buttonTexture, fallback)

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
 * Цвет содержимого на доске.
 *
 * Доски кнопок и жетонов темны в ОБЕИХ темах — это растр, он не переключается вместе с
 * оформлением, — поэтому текст и значки на них всегда светлые, а не по теме. Без этого в светлой
 * теме на тёмном дереве оказался бы тёмный глиф.
 */
val WOOD_CONTENT_COLOR = Color(0xFFF7EFE2)
