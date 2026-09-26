package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.theme.LeshyTheme
import leshy.mushrooms.map.ui.theme.SurfaceStyle

/**
 * Рамы, маты и таблички — обрамление поверхностей редакции.
 *
 * ## Развилка идёт по величине, а не по редакции
 *
 * Каждая функция этого файла у мировой редакции возвращает ровно то, что ей передали: рамы там
 * нулевой толщины, мата нет, стиль поверхности [SurfaceStyle.FLAT]. То есть вызов на любом
 * экране мировое приложение не меняет ни на пиксель — это проверяется скриншот-дифом
 * (`docs/russia-edition/track-app.md`, «Как проверять „мировое приложение не изменилось"»), а не
 * обещается.
 *
 * Отсюда правило вызова: **обрамление навешивается на общий composable, а не заводится второй
 * его копией под редакцию.** Ни одного `if (edition == …)` в экранах не появляется, и счётчик
 * таких `if` остаётся нулевым (`docs/russia-edition/README.md`, «Правки общих экранов»).
 *
 * ## Два обрамления: рама предмета и мат под текстом
 *
 * Словарь взят из иконки владельца (`design.md`, раздел 3):
 *
 * - **рама карточки** — тонкая «мебельная» линия цвета `outline`: карточка это предмет,
 *   лежащий на земле;
 * - **мат** — почти белая плашка под текстом, который иначе лежал бы прямо на доске.
 *
 * Разница не косметическая: у мирового приложения элементы растворяются в фоне, у обрамлённой
 * редакции **каждый элемент обведён**, и именно это вместе с малыми радиусами даёт главный
 * сдвиг силуэта.
 *
 * **Чего здесь нет и почему.** `design.md` описывал ещё два приёма — белый мат с алой
 * «картинной» рамой вокруг фотографии вида и «табличку» (тёмную плашку со светлым текстом) под
 * подписями и заголовками. Оба собраны и оба отвергнуты владельцем на устройстве 2026-09-26:
 * рама спорила с рамкой цвета вида в паре миллиметров от неё, а плашка под текстом оказалась
 * лишней и там, где подпись лежит на фотографии, и там, где заголовок лежит на доске. Разбор
 * решения — `docs/russia-edition/design.md`, раздел 6.
 */

/**
 * Рама карточки для компонентов, у которых есть собственный параметр `border` (`Card`,
 * `Surface`): `null` — рамы нет, то есть мировой вид.
 *
 * Отдельно от [cardFrame] потому, что у `Card` рамка рисуется ВНУТРИ его формы и скругляется
 * вместе с ней; тот же контур, навешенный модификатором снаружи, лёг бы поверх содержимого.
 *
 * [selected] — рамка выбранного состояния. Она главнее: у карточки может быть выбранное
 * состояние, обведённое `primary`, и подменять его рамой редакции значило бы стереть смысл ради
 * оформления.
 */
@Composable
fun cardFrameBorder(selected: BorderStroke? = null): BorderStroke? {
    if (selected != null) return selected
    val width = LeshyTheme.tokens.frameWidth
    if (width <= 0.dp) return null
    return BorderStroke(width, MaterialTheme.colorScheme.outline)
}

/**
 * Та же рама модификатором — для поверхностей, собранных из `Box`/`Column` без параметра
 * `border` (панели поверх карты, плашки подсказок).
 */
@Composable
fun Modifier.cardFrame(shape: Shape): Modifier {
    val width = LeshyTheme.tokens.frameWidth
    if (width <= 0.dp) return this
    return border(width, MaterialTheme.colorScheme.outline, shape)
}

/**
 * Рама диалога, который Material рисует сам.
 *
 * `AlertDialog` параметра `border` не имеет вовсе, и единственное место, куда раму можно
 * положить, — модификатор вызывающего: он попадает на тот самый `Box`, который `BasicAlertDialog`
 * ограничивает своим `sizeIn`, то есть на границы самого диалога.
 *
 * **Ставится ПОСЛЕДНИМ в цепочке** — после `imePadding()`, если он там есть. Модификатор,
 * стоящий раньше, обводит узел ДО отступа, и с поднятой клавиатурой рама осталась бы висеть
 * вокруг пустого места, отданного ей.
 *
 * Диалогам, собранным на `Surface` вручную, это не нужно: у них есть собственный параметр
 * `border` ([cardFrameBorder]), и рама там рисуется внутри формы поверхности, а не снаружи узла.
 */
@Composable
fun Modifier.dialogFrame(): Modifier = cardFrame(LeshyTheme.tokens.shapeDialog)

/**
 * Мат под текстом — почти белая плашка с рамой, на которой живёт текст, лежащий на земле.
 *
 * Правило, ради которого это существует: **текст всегда на мате или на табличке, никогда на
 * текстуре** (`design.md`, раздел 3). Оно не стилистическое — приложением пользуются на солнце в
 * лесу.
 *
 * У мировой редакции ([SurfaceStyle.FLAT]) мат не появляется: там фон экрана и есть мат, и
 * подложка поверх него была бы вторым слоем того же цвета. Содержимое отдаётся вызывающему как
 * есть, без своей колонки и без отступов, — то есть экран остаётся прежним до пикселя.
 */
@Composable
fun MatSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (LeshyTheme.tokens.surfaceStyle == SurfaceStyle.FLAT) {
        Column(modifier = modifier, horizontalAlignment = horizontalAlignment, content = content)
        return
    }
    Surface(
        modifier = modifier,
        shape = LeshyTheme.tokens.shapeWalkCard,
        color = MaterialTheme.colorScheme.surface,
        border = cardFrameBorder(),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }
}
