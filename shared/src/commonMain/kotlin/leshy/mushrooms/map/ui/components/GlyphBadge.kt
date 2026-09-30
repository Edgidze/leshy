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
 * Сторона жетона, на котором глиф выходит ровно [glyphSize] — обратная [GLYPH_RATIO].
 *
 * Нужна там, где размер задаёт СОДЕРЖИМОЕ, а не подложка: значок в блоке статистики виден в обеих
 * редакциях, и требование к нему («вот такой величины глиф») одно на две, а сторона жетона под ним
 * — уже следствие. Считать это в месте вызова руками значило бы разложить пропорцию жетона по
 * экранам: подправив её здесь, пришлось бы искать все такие деления.
 */
fun badgeSizeForGlyph(glyphSize: Dp): Dp = glyphSize / GLYPH_RATIO

/**
 * Место, которое [GlyphBadge] с такими аргументами в самом деле займёт: жетон целиком — или один
 * глиф, если у редакции жетона нет.
 *
 * Нужна вёрстке, которой приходится считать высоту блока со значком заранее (наименьшая высота
 * плашки статистики — `StatsBlocks.kt`). Развилка идёт по тому же токену, что внутри [GlyphBadge],
 * — иначе она разошлась бы с ним при первой же правке.
 */
@Composable
fun glyphBadgeFootprint(size: Dp = BADGE_SIZE, glyphSize: Dp = GLYPH_SIZE): Dp =
    if (LeshyTheme.tokens.iconBadge != null) size else glyphSize

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
 *
 * **[glyphSize] — размер глифа в безжетонной, мировой редакции**, и по умолчанию это [GLYPH_SIZE],
 * то есть дефолт Material. Место вызова, которому нужен глиф крупнее обычного, обязано сказать это
 * ЗДЕСЬ, а не только увеличив [size]: за [size] прячется подложка, которой в мировой редакции нет,
 * и без второго числа значок там остался бы прежних 24dp при выросшем жетоне у соседней редакции.
 * Величины держать согласованными помогает [badgeSizeForGlyph]: `size = badgeSizeForGlyph(g),
 * glyphSize = g`.
 */
@Composable
fun GlyphBadge(
    painter: Painter,
    modifier: Modifier = Modifier,
    size: Dp = BADGE_SIZE,
    glyphSize: Dp = GLYPH_SIZE,
    contentDescription: String? = null,
) {
    val badge = LeshyTheme.tokens.iconBadge
    if (badge == null) {
        Icon(painter = painter, contentDescription = contentDescription, modifier = modifier.size(glyphSize))
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
            contentDescription = contentDescription,
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

/**
 * Размер глифа на кнопке, которой жетон служит ПОДЛОЖКОЙ ([glyphBadgeBackground]): [onBadge] —
 * когда жетон есть, [fallback] — когда его нет.
 *
 * Развилка нужна потому, что требования к глифу на этих двух подложках разные. На доске глиф
 * обязан оставить поля: доска скруглена и темна, глиф во всю сторону кнопки упирается в её край.
 * Без доски край сводить не с чем — там глиф ограничен только размером самой кнопки, и мельчить
 * его незачем.
 *
 * Отдельно от [GlyphBadge] с его [GLYPH_RATIO]: там размер глифа ВЫВОДИТСЯ из стороны жетона,
 * потому что жетон рисует сам composable. Здесь жетон — фон уже существующей кнопки, её сторона
 * задана вёрсткой, и оба числа приходят снаружи.
 */
@Composable
fun glyphBadgeGlyphSize(onBadge: Dp, fallback: Dp): Dp =
    if (LeshyTheme.tokens.iconBadge != null) onBadge else fallback
