package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.theme.LeshyTheme
import org.jetbrains.compose.resources.painterResource

/** Размер глифа. Тот же, что Material подставляет своим иконкам по умолчанию. */
private val GLYPH_SIZE = 24.dp

/** Сторона жетона. Глиф занимает в нём примерно те же 2/3, что и в поле иконки Material. */
private val BADGE_SIZE = 38.dp

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
fun GlyphBadge(painter: Painter, modifier: Modifier = Modifier) {
    val badge = LeshyTheme.tokens.iconBadge
    if (badge == null) {
        Icon(painter = painter, contentDescription = null, modifier = modifier.size(GLYPH_SIZE))
        return
    }
    Box(modifier = modifier.size(BADGE_SIZE), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(badge),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Icon(
            painter = painter,
            contentDescription = null,
            tint = BADGE_GLYPH_COLOR,
            modifier = Modifier.size(GLYPH_SIZE),
        )
    }
}
