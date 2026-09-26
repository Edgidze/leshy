package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.paint
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import leshy.mushrooms.map.ui.theme.LeshyTheme
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
 * Та же земля, но фоном произвольной поверхности — для компонентов, которым картинку передать
 * нечем, а `Box` вокруг не поставить: полотно выдвижного меню (`ModalDrawerSheet` принимает
 * только `Modifier` и цвет).
 *
 * `Modifier.paint` рисуется ДО содержимого узла, то есть работает как фон; собственный цвет
 * поверхности при этом обязан быть прозрачным ([groundContainerColor]), иначе он ляжет поверх.
 */
@Composable
fun Modifier.groundBackground(): Modifier {
    val ground = LeshyTheme.tokens.groundTexture ?: return this
    return paint(painterResource(ground), contentScale = ContentScale.Crop)
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
