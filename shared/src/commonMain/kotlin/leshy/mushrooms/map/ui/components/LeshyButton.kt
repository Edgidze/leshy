package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.ui.theme.LeshyTheme

/** Толщина и цвет обводки — те же, что у круглых боковых кнопок «Записи» и у `MapFilterButton`. */
private val BORDER_WIDTH = 1.dp

/** Material3 гасит содержимое выключенной кнопки, но переданную `border` оставляет во всю силу —
 * обводка вокруг погашенной кнопки читалась бы ярче её самой. 0.38f — та же доля, которой в этом
 * проекте гасят выключенное содержимое (см. `RecordSideButton`, `MushroomTile`). */
private const val DISABLED_BORDER_ALPHA = 0.38f

/** Та же доля для погашенной подписи на доске: у выключенной кнопки Material гасит содержимое
 * сам, но только когда цвет содержимого берёт он же, а на доске цвет задаём мы. */
private const val DISABLED_CONTENT_ALPHA = 0.38f

/**
 * Заливная кнопка действия с общей для приложения обводкой.
 *
 * Обводка появилась не как украшение: на «Записи» кнопки висят прямо над картой без непрозрачной
 * подложки, и круглые боковые кнопки (`RecordSideButton`) с кнопкой фильтра (`MapFilterButton`)
 * такую обводку имели с самого начала, а центральная «Старт»/«Пауза» — нет, из-за чего выпадала
 * из собственного ряда. Раз уж контур решили сделать общим языком кнопок, он распространён на все
 * заливные кнопки приложения, а не только на карточные экраны, — иначе Настройки и диалоги
 * разъезжались бы с «Записью».
 *
 * `OutlinedButton` этим компонентом НЕ подменяется: у него обводка своя по определению, и он уже
 * стоит там, где нужна именно вторичная кнопка (Отмена рядом с Готово).
 *
 * [dimmed] — «выглядит выключенной, но нажимается»: кнопка красится цветами выключенного
 * состояния, а `onClick` продолжает приходить. Нужно там, где условие перехода человеку заранее не
 * очевидно и объяснить его можно только в ответ на нажатие (последний шаг онбординга: «Дальше» без
 * единого выбранного гриба показывает предупреждение — см.
 * [leshy.mushrooms.map.ui.screens.OnboardingScreen]). Честный `enabled = false` там не годится
 * ровно тем, что молчит в ответ на тап. Со `enabled = false` не сочетается и не спорит: выключенная
 * кнопка и так нарисована этими же цветами.
 */
@Composable
fun LeshyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dimmed: Boolean = false,
    shape: Shape = LeshyTheme.tokens.shapeButton,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit,
) {
    val outline = MaterialTheme.colorScheme.outline
    val looksEnabled = enabled && !dimmed
    // Доска под кнопкой — та же древесина, что у жетонов под значками: кнопка и значок стоят
    // рядом на одном экране, и разный материал прочитался бы как два разных приложения
    // (требование владельца 2026-09-26). Заливка контейнера при этом обязана стать прозрачной —
    // её рисует сам Material, ПОВЕРХ нашего фона, — а подпись светлеет: доска тёмная в обеих
    // темах. Без доски (мировая редакция) всё остаётся ровно как было, включая переданные
    // вызывающим `colors`.
    val wooden = LeshyTheme.tokens.buttonTexture != null
    val woodenColors = if (wooden) {
        colors.copy(
            containerColor = Color.Transparent,
            contentColor = WOOD_CONTENT_COLOR,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = WOOD_CONTENT_COLOR.copy(alpha = DISABLED_CONTENT_ALPHA),
        )
    } else {
        colors
    }
    Button(
        onClick = onClick,
        // clip перед доской обязателен: `Modifier.paint` не знает про форму кнопки и залил бы
        // прямоугольник целиком, вылезая деревом за скруглённые углы.
        modifier = modifier.clip(shape).buttonBackground(),
        enabled = enabled,
        shape = shape,
        colors = if (dimmed) {
            woodenColors.copy(
                containerColor = woodenColors.disabledContainerColor,
                contentColor = woodenColors.disabledContentColor,
            )
        } else {
            woodenColors
        },
        border = BorderStroke(
            BORDER_WIDTH,
            if (looksEnabled) outline else outline.copy(alpha = DISABLED_BORDER_ALPHA),
        ),
        content = content,
    )
}
