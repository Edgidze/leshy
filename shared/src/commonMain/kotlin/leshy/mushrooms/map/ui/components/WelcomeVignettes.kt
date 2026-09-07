package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Крошечные макеты экранов приложения для обзорной страницы первого запуска
 * ([leshy.mushrooms.map.ui.screens.WelcomeScreen]) — «вот как это выглядит внутри», рядом с
 * абзацем текста про соответствующую возможность.
 *
 * Это НЕ скриншоты и не картинки-ресурсы, а обычная вёрстка теми же `MaterialTheme.colorScheme`,
 * что и настоящие экраны, — почему именно так, см. шапку `MiniMockups.kt`, откуда взяты все
 * кирпичики этих макетов (площадка карты, карточка прогулки, плитка гриба, полоска-заглушка
 * вместо строки текста).
 *
 * Своё, местное правило здесь одно и жёсткое: **ни одной подписи**. Обзорную страницу читают на
 * любом из языков интерфейса, макеты на ней узнаваемы формой, а не текстом, — так что переводить
 * в них нечего. Единственные настоящие знаки — цифры счётчика на плитке гриба. (Справка разделов,
 * `HelpIllustrations.kt`, свои картинки местами подписывает — но только готовыми
 * `StringKey`-строками интерфейса.)
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
                // читаться одинаково на любом языке интерфейса, а значит не содержать слов вовсе.
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    MiniTextLine(widthFraction = 1f)
                    MiniTextLine(widthFraction = 0.92f)
                    MiniTextLine(widthFraction = 0.55f)
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
        MiniTextLine(widthFraction = 0.45f, modifier = Modifier.weight(1f))
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
        MiniTextLine(widthFraction = if (selected) 0.75f else 0.6f)
    }
}
