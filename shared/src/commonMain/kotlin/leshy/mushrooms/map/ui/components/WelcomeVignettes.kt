package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource

/**
 * Крошечные макеты экранов приложения для обзорной страницы первого запуска
 * ([leshy.mushrooms.map.ui.screens.WelcomeScreen]) — «вот как это выглядит внутри», рядом с
 * абзацем текста про соответствующую возможность.
 *
 * Это НЕ скриншоты и не картинки-ресурсы, а обычная вёрстка теми же `MaterialTheme.colorScheme`,
 * что и настоящие экраны, — по трём причинам. Скриншот пришлось бы перевыпускать под каждую из
 * двух тем (иначе светлый снимок горит белым пятном в тёмной), под каждый из 33 языков (на нём
 * есть подписи) и заново после любой правки настоящего экрана; растр такого размера ещё и мылит
 * на плотных экранах. Здесь же ни одной подписи нет вовсе — макеты узнаваемы формой, а не
 * текстом, — так что переводить в них нечего.
 *
 * Узнаваемость держится на том, что значки взяты те же самые, что стоят в настоящем интерфейсе:
 * `Menu`/`HelpOutline` — как в [SectionScaffold], список пунктов меню — как `drawerNavEntries`
 * в `App.kt`, `+`/`−` — как в [MushroomTile].
 */

/** Общая высота площадки макета: одинаковая у всех четырёх, иначе колонка карточек «прыгает». */
private val VIGNETTE_HEIGHT = 116.dp

/** Скругление «экранчика» — заметно меньше, чем у настоящих карточек, ровно ради ощущения макета. */
private val VIGNETTE_CORNER = 10.dp

@Composable
private fun VignetteFrame(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(VIGNETTE_HEIGHT)
            .clip(RoundedCornerShape(VIGNETTE_CORNER))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(VIGNETTE_CORNER),
            )
            .padding(6.dp),
    ) {
        content()
    }
}

/**
 * «Запись»: карта с треком и находками сверху, лента плиток грибов снизу — ровно так, как устроен
 * настоящий экран записи.
 */
@Composable
fun RecordVignette(modifier: Modifier = Modifier) {
    VignetteFrame(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MiniMap(modifier = Modifier.fillMaxWidth().weight(1f), withTrack = true)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MiniMushroomTile(count = "3", modifier = Modifier.weight(1f))
                MiniMushroomTile(count = "7", modifier = Modifier.weight(1f))
                MiniMushroomTile(count = "1", modifier = Modifier.weight(1f))
            }
        }
    }
}

/** «Архив» + «Карта находок»: слева лента карточек прогулок, справа общая карта всех находок. */
@Composable
fun ArchiveVignette(modifier: Modifier = Modifier) {
    VignetteFrame(modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                MiniWalkCard(modifier = Modifier.weight(1f))
                MiniWalkCard(modifier = Modifier.weight(1f))
            }
            MiniMap(modifier = Modifier.weight(1f).fillMaxHeight(), withTrack = false)
        }
    }
}

/** Верхняя панель раздела: слева гамбургер, справа «?» — тот самый, о котором говорит текст. */
@Composable
fun HelpVignette(modifier: Modifier = Modifier) {
    VignetteFrame(modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MiniTopBar(highlightHelp = true)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                // Условный «текст подсказки» — три полоски, а не настоящие буквы: макет обязан
                // читаться одинаково на всех 33 языках, а значит не содержать слов вовсе.
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    TextLine(widthFraction = 1f)
                    TextLine(widthFraction = 0.92f)
                    TextLine(widthFraction = 0.55f)
                }
            }
        }
    }
}

/** Боковое меню, выехавшее из-под кнопки-гамбургера, с теми же пунктами, что и настоящее. */
@Composable
fun MenuVignette(modifier: Modifier = Modifier) {
    VignetteFrame(modifier) {
        Row {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.62f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(vertical = 6.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                MiniMenuRow(Icons.Filled.Hiking, selected = true)
                MiniMenuRow(Icons.AutoMirrored.Filled.List, selected = false)
                MiniMenuRow(Icons.Filled.Place, selected = false)
                MiniMenuRow(Icons.Filled.Download, selected = false)
                MiniMenuRow(Icons.Filled.Settings, selected = false)
            }
            // Уцелевшая полоска экрана справа от выехавшей панели, притемнённая как настоящий
            // scrim, — без неё панель читается не как панель поверх экрана, а как сам экран.
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.38f)
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)),
            )
        }
    }
}

@Composable
private fun MiniTopBar(highlightHelp: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 6.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        TextLine(widthFraction = 0.45f, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = null,
            tint = if (highlightHelp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(if (highlightHelp) 18.dp else 14.dp)
                .then(
                    if (highlightHelp) {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape).padding(2.dp)
                    } else {
                        Modifier
                    },
                ),
        )
    }
}

/** Полоска-заглушка вместо строки текста — см. комментарий про 33 языка в [HelpVignette]. */
@Composable
private fun TextLine(widthFraction: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)),
    )
}

@Composable
private fun MiniMenuRow(icon: ImageVector, selected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(11.dp),
        )
        Spacer(modifier = Modifier.width(5.dp))
        TextLine(widthFraction = if (selected) 0.75f else 0.6f)
    }
}

/** Карточка прогулки: слева силуэт трека, справа название и цифры — как в ленте «Архива». */
@Composable
private fun MiniWalkCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val trackColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(26.dp)
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
            TextLine(widthFraction = 0.85f)
            TextLine(widthFraction = 0.5f)
        }
    }
}

/**
 * Площадка карты. [withTrack] — вариант «Записи»: трек с находками вдоль него и точка текущего
 * положения. Без него — сводная карта находок: кружки-скопления, как их рисует
 * `ClusteredFindsLayers` на настоящей карте.
 */
@Composable
private fun MiniMap(modifier: Modifier = Modifier, withTrack: Boolean) {
    val trackColor = MaterialTheme.colorScheme.primary
    val findColor = MaterialTheme.colorScheme.error
    val onFindColor = MaterialTheme.colorScheme.onError
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
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
private fun DrawScope.drawMiniTrack(color: Color) {
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
private fun DrawScope.drawFindDot(center: Offset, radius: Float, color: Color, outline: Color) {
    drawCircle(color = outline, radius = radius, center = center)
    drawCircle(color = color, radius = radius * 0.78f, center = center)
}

/** Плитка гриба из ленты «Записи»: картинка сверху, «−  счётчик  +» снизу. */
@Composable
private fun MiniMushroomTile(count: String, modifier: Modifier = Modifier) {
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
            modifier = Modifier.size(20.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(9.dp),
            )
            Text(
                text = count,
                // Единственные настоящие знаки во всех макетах — цифры счётчика. Они одинаковы во
                // всех 33 языках интерфейса (числа приложение везде пишет арабскими цифрами),
                // поэтому переводить их не нужно, а без них плитка перестаёт быть узнаваемой.
                fontSize = 9.sp,
                lineHeight = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 3.dp),
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(9.dp),
            )
        }
    }
}
