package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.theme.LeshyTheme
import org.jetbrains.compose.resources.painterResource

/** Размер глифа без жетона. Тот же, что Material подставляет своим иконкам по умолчанию. */
private val GLYPH_SIZE = 24.dp

/** Сторона жетона по умолчанию. Глиф занимает в нём примерно те же 2/3, что и в поле иконки
 * Material, — доля вынесена в [GLYPH_RATIO], потому что жетон бывает и другого размера. */
private val BADGE_SIZE = 38.dp

/** Доля жетона под глиф. 24 из 38 — та же пропорция, с которой жетон появился в меню. */
private const val GLYPH_RATIO = 24f / 38f

/**
 * Глиф на жетоне — белый, а не по теме.
 *
 * Жетон деревянный и тёмный в обеих темах: это растр, он не переключается вместе с оформлением.
 * Цвет содержимого поэтому задаётся здесь, а не берётся из `LocalContentColor`, — иначе в светлой
 * теме на тёмном дереве оказался бы тёмный глиф.
 */
private val BADGE_GLYPH_COLOR = Color.White

/**
 * Значок раздела: стоковый глиф Material, лежащий на «жетоне» редакции.
 *
 * Жетона нет ([LeshyTokens.iconBadge] `null`, мировая редакция) — рисуется один глиф, ровно как
 * рисовался раньше, включая размер и цвет по теме. Развилка идёт по ТОКЕНУ, а не по редакции:
 * экран не знает, какой это продукт, и появление третьей редакции сюда не добавит ни строчки.
 *
 * Почему жетон вообще существует — `design.md`, разделы 3 и 9: своего набора глифов редакция не
 * рисует, вместо этого меняется материал, на котором глиф лежит.
 */
@Composable
fun GlyphBadge(painter: Painter, modifier: Modifier = Modifier, size: Dp = BADGE_SIZE) {
    val badge = LeshyTheme.tokens.iconBadge
    if (badge == null) {
        Icon(painter = painter, contentDescription = null, modifier = modifier.size(GLYPH_SIZE))
        return
    }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(badge),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Icon(
            painter = painter,
            contentDescription = null,
            tint = BADGE_GLYPH_COLOR,
            modifier = Modifier.size(size * GLYPH_RATIO),
        )
    }
}

/**
 * Тот же жетон, но **подложкой уже существующей кнопки** — «+»/«−» на плитке вида, боковые
 * кнопки «Записи», значки в блоках статистики.
 *
 * Требование согласованности, а не украшение: жетоны в боковом меню и кнопки со значками стоят
 * на одном экране, и значок на дереве рядом со значком на плашке читался бы как два разных
 * приложения. Поэтому подложка ровно одна на всё — тот же файл, что у [GlyphBadge].
 *
 * Скругление внутри растра уже есть, поэтому [shape] нужен только запасному пути: без жетона
 * место закрашивается [fallbackBackground] (и [fallbackBorder], если он был), то есть мировая
 * редакция получает ровно прежний вид, включая его отсутствие — `Color.Transparent` там, где
 * фона у кнопки не было вовсе.
 */
@Composable
fun Modifier.glyphBadgeBackground(
    shape: Shape,
    fallbackBackground: Color = Color.Transparent,
    fallbackBorder: Color? = null,
): Modifier {
    val badge = LeshyTheme.tokens.iconBadge ?: return this
        .clip(shape)
        .background(fallbackBackground)
        .let { if (fallbackBorder != null) it.border(1.dp, fallbackBorder, shape) else it }
    // drawBehind, а не `Modifier.paint`: тот в любом режиме участвует в измерении и навязывает
    // кнопке либо размер растра, либо всё доступное место — разбор в `Ground.kt`. Жетон
    // растягивается по узлу целиком: он квадратный, и кнопки под ним тоже.
    val painter = painterResource(badge)
    return drawBehind { with(painter) { draw(size) } }
}

/** Цвет значка, лежащего на жетоне: белый, когда жетон есть, и [fallback] — когда его нет. */
@Composable
fun glyphBadgeContentColor(fallback: Color): Color =
    if (LeshyTheme.tokens.iconBadge != null) BADGE_GLYPH_COLOR else fallback
