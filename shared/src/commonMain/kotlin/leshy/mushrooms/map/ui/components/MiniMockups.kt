package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource

/**
 * Кирпичики, из которых собраны нарисованные (не снятые!) макеты экранов приложения — обзорная
 * страница первого запуска ([WelcomeVignettes.kt]) и картинки в справке разделов
 * ([HelpIllustrations.kt]).
 *
 * Почему рисованные макеты, а не скриншоты: скриншот пришлось бы перевыпускать под каждую из двух
 * тем (светлый снимок горит белым пятном в тёмной), под каждый язык интерфейса и заново после
 * любой правки настоящего экрана; растр такого размера ещё и мылит на плотных экранах. Макет же
 * собран теми же `MaterialTheme.colorScheme` и теми же значками, что и настоящий экран, поэтому
 * следует за темой сам и остаётся узнаваемым.
 *
 * Узнаваемость держится на том, что значки взяты те же самые, что стоят в настоящем интерфейсе:
 * `Menu`/`HelpOutline` — как в [SectionScaffold], `+`/`−` — как в [MushroomTile], силуэт трека —
 * как у [WalkRouteThumbnail].
 *
 * **Слов в этих кирпичиках нет** — вместо строк текста [MiniTextLine]. У обзорной страницы это
 * жёсткое правило (её макеты обязаны читаться одинаково на всех языках, а переводить в них
 * нечего). Справка разделов местами подписывает свои картинки — но только настоящими
 * `StringKey`-строками, уже переведёнными для интерфейса; см. шапку `HelpIllustrations.kt`.
 */

/** Полоска-заглушка вместо строки текста. */
@Composable
internal fun MiniTextLine(
    widthFraction: Float,
    modifier: Modifier = Modifier,
    thickness: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(thickness)
            .clip(CircleShape)
            .background(color),
    )
}

/**
 * Площадка карты. [withTrack] — вариант «Записи»: трек с находками вдоль него. Без него — сводная
 * карта находок: кружки-скопления, как их рисует `ClusteredFindsLayers` на настоящей карте.
 * [pastFinds] добавляет приглушённые отметки прошлых прогулок поверх того же полотна.
 */
@Composable
internal fun MiniMap(
    modifier: Modifier = Modifier,
    withTrack: Boolean,
    pastFinds: Boolean = false,
    corner: Dp = 6.dp,
) {
    val trackColor = MaterialTheme.colorScheme.primary
    val findColor = MaterialTheme.colorScheme.error
    val onFindColor = MaterialTheme.colorScheme.onError
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Две «дороги» крест-накрест — минимум, от которого прямоугольник начинает читаться
            // как карта, а не как пустая заливка.
            drawLine(
                color = gridColor,
                start = Offset(0f, size.height * 0.72f),
                end = Offset(size.width, size.height * 0.34f),
                strokeWidth = size.minDimension * 0.06f,
            )
            drawLine(
                color = gridColor,
                start = Offset(size.width * 0.28f, 0f),
                end = Offset(size.width * 0.46f, size.height),
                strokeWidth = size.minDimension * 0.045f,
            )
            if (pastFinds) {
                listOf(
                    Offset(size.width * 0.14f, size.height * 0.2f),
                    Offset(size.width * 0.88f, size.height * 0.84f),
                    Offset(size.width * 0.64f, size.height * 0.14f),
                    Offset(size.width * 0.4f, size.height * 0.9f),
                ).forEach { center ->
                    drawFindDot(center, size.minDimension * 0.075f, findColor.copy(alpha = 0.4f), onFindColor)
                }
            }
            if (withTrack) {
                drawMiniTrack(trackColor)
                listOf(
                    Offset(size.width * 0.26f, size.height * 0.36f),
                    Offset(size.width * 0.55f, size.height * 0.72f),
                    Offset(size.width * 0.78f, size.height * 0.3f),
                ).forEach { center ->
                    drawFindDot(center, size.minDimension * 0.1f, findColor, onFindColor)
                }
            } else {
                listOf(
                    Offset(size.width * 0.3f, size.height * 0.35f) to size.minDimension * 0.24f,
                    Offset(size.width * 0.68f, size.height * 0.6f) to size.minDimension * 0.17f,
                    Offset(size.width * 0.46f, size.height * 0.82f) to size.minDimension * 0.11f,
                ).forEach { (center, radius) ->
                    drawCircle(color = findColor.copy(alpha = 0.28f), radius = radius * 1.5f, center = center)
                    drawFindDot(center, radius, findColor, onFindColor)
                }
            }
        }
    }
}

/** S-образный «трек» по всей площадке — та же ломаная, что рисует [WalkRouteThumbnail] по данным. */
internal fun DrawScope.drawMiniTrack(color: Color) {
    val path = Path().apply {
        moveTo(size.width * 0.12f, size.height * 0.82f)
        cubicTo(
            size.width * 0.34f, size.height * 0.9f,
            size.width * 0.2f, size.height * 0.3f,
            size.width * 0.52f, size.height * 0.4f,
        )
        cubicTo(
            size.width * 0.84f, size.height * 0.5f,
            size.width * 0.62f, size.height * 0.88f,
            size.width * 0.88f, size.height * 0.7f,
        )
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = size.minDimension * 0.075f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

/** Отметка находки: кружок с обводкой — тот же приём, что у точек находок на снимках маршрутов. */
internal fun DrawScope.drawFindDot(center: Offset, radius: Float, color: Color, outline: Color) {
    drawCircle(color = outline, radius = radius, center = center)
    drawCircle(color = color, radius = radius * 0.78f, center = center)
}

/** Карточка прогулки: слева силуэт трека, справа название и цифры — как в ленте «Архива». */
@Composable
internal fun MiniWalkCard(
    modifier: Modifier = Modifier,
    thumbnailWidth: Dp = 26.dp,
    lineThickness: Dp = 4.dp,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(modifier = Modifier.width(4.dp))
        }
        val trackColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(thumbnailWidth)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            drawMiniTrack(trackColor)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            MiniTextLine(widthFraction = 0.85f, thickness = lineThickness)
            MiniTextLine(widthFraction = 0.5f, thickness = lineThickness)
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(6.dp))
            trailing()
        }
    }
}

/** Плитка гриба из ленты «Записи»: картинка сверху, «−  счётчик  +» снизу. */
@Composable
internal fun MiniMushroomTile(
    count: String,
    modifier: Modifier = Modifier,
    imageSize: Dp = 20.dp,
    controlSize: Dp = 9.dp,
    countFontSize: TextUnit = 9.sp,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_mushrooms),
            contentDescription = null,
            // Вторичный цвет схемы — землисто-коричневый (см. Theme.kt): на настоящей плитке тут
            // цветная фотография гриба, и серый силуэт вместо неё выглядел бы выключенным.
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(imageSize),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(controlSize),
            )
            Text(
                text = count,
                // Единственные настоящие знаки в макетах обзорной страницы — цифры счётчика. Они
                // одинаковы во всех языках интерфейса (числа приложение везде пишет арабскими
                // цифрами), поэтому переводить их не нужно, а без них плитка перестаёт быть
                // узнаваемой.
                fontSize = countFontSize,
                lineHeight = countFontSize * 1.15f,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 3.dp),
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(controlSize),
            )
        }
    }
}
