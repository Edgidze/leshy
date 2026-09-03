package leshy.mushrooms.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.domain.model.Walk
import leshy.mushrooms.map.i18n.mushroomsUnitLabel
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.ic_mushrooms
import org.jetbrains.compose.resources.painterResource
import leshy.mushrooms.map.ui.util.formatDateOnly
import leshy.mushrooms.map.ui.util.formatDistanceKm
import leshy.mushrooms.map.ui.util.formatDurationShort
import kotlin.time.Duration.Companion.seconds

/**
 * Сторона квадратной миниатюры маршрута. Постоянная и одинаковая у всех карточек списка: и
 * разъехавшийся левый край текстовых колонок, и разная высота карточек читались бы как поломка
 * вёрстки, а не как разные прогулки.
 *
 * **Квадрат при снимке 16:9** — то есть снимок подрезается по бокам ([ContentScale.Crop]), от него
 * остаётся средних 56% ширины. Это сознательная плата, и вот за что.
 *
 * Текстовая колонка справа у разных прогулок разной высоты: название переносится на вторую строку,
 * показатели у прогулки с трёхзначным числом находок — тоже. Всё, чем колонка выше миниатюры,
 * оказывается пустотой по бокам от неё, и на узких экранах это почти каждая карточка.
 *
 * Способов убрать пустоту три, и два из них хуже:
 * 1. одна высота карточки на весь список — пустота остаётся у карточек с коротким текстом, а
 *    список удлиняется на её величину в каждой;
 * 2. миниатюра тянется во всю высоту карточки, какой бы та ни вышла (`IntrinsicSize.Min` у
 *    строки) — пустоты не остаётся вовсе, но высота карточки начинает зависеть от того, насколько
 *    верно посчиталась минимальная внутренняя высота текстовой колонки, а в ней лежит `FlowRow`
 *    с переносами. Просчёт здесь означает обрезанный текст, и это цена, которую платить нельзя:
 *    порванная миниатюра — некрасиво, порванное название прогулки — потеря содержания. **Было
 *    сделано и отвергнуто владельцем именно по этой причине; не возвращать.**
 * 3. **квадрат, прижатый к верхнему левому углу** — то, что здесь. Пустота остаётся, но собирается
 *    в одно поле под картинкой, а не в два по бокам, и глазу такое поле не мешает: край текста
 *    сверху ровный, картинка на своём месте, а низ у карточек и так разный.
 *
 * Обрезка терпима потому, что маршрут в снимке лежит по центру и с полями (`SNAPSHOT_PADDING_PX`
 * в рендерерах), а целиком снимок показывается на экране детализации: миниатюра в списке служит
 * опознанию прогулки, а не разглядыванию маршрута.
 */
private val THUMBNAIL_SIZE = 120.dp

/**
 * Ориентир — не высота букв в строке, а размер эмодзи `🍄`, который здесь стоял раньше: эмодзи
 * рисуется заметно крупнее строчной буквы того же кегля, и замена на значок вровень с буквами
 * читалась как потеря, а не как замена. Картинка заполняет своё поле целиком
 * (`tools/prepare_icon_assets.py`), а грибы шире, чем выше, поэтому видимая высота выходит
 * примерно на десятую меньше этого числа.
 */
private val MUSHROOM_ICON_SIZE = 22.dp
private val WALK_CARD_PADDING = 8.dp

/** Hold duration that opens Archive's multi-select mode — see CLAUDE.md for the feature spec. */
private val SELECTION_LONG_PRESS_DURATION = 5.seconds

@Composable
fun WalkCard(
    walk: Walk,
    track: List<GeoPoint>,
    findLocations: List<GeoPoint>,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Пять секунд — время, которое пользователь не выдержит по догадке, а удержание карточки
    // остаётся единственным способом удалить прогулку (§3.2 дизайн-аудита). Заливка, доходящая до
    // правого края ровно к порогу, объясняет жест с первого случайного касания. Цвет — `primary`,
    // тот же, каким обводится выбранная карточка: то, к чему удержание ведёт.
    var holdProgress by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .holdProgressWipe(
                shape = CardDefaults.shape,
                color = MaterialTheme.colorScheme.primary,
                progress = { holdProgress },
            )
            .tapOrHold(
                holdDuration = SELECTION_LONG_PRESS_DURATION,
                onTap = onClick,
                onHold = onLongPress,
                onHoldProgress = { holdProgress = it },
            ),
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        // Выравнивания у строки нет — то есть дети прижаты к верху, и это главное здесь.
        // Миниатюра обязана стоять в верхнем левом углу: тогда пустота, остающаяся когда текст
        // выше картинки, собирается под картинкой одним полем.
        Row(modifier = Modifier.padding(WALK_CARD_PADDING)) {
            WalkThumbnail(
                thumbnailPath = walk.thumbnailPath,
                track = track,
                findLocations = findLocations,
                modifier = Modifier.size(THUMBNAIL_SIZE),
            )
            Spacer(modifier = Modifier.width(WALK_CARD_PADDING))
            // Текст тоже по верху — своего выравнивания у колонки нет, она берёт то же, что и
            // картинка. Была промежуточная редакция с центрированием по высоте, ради случая, когда
            // текст НИЖЕ картинки: казалось, что там он должен уравновешиваться с ней. На экране
            // это оказалось хуже: первая строка названия у карточек списка вставала на разной
            // высоте — вровень с верхом картинки там, где текст высокий, и ниже там, где короткий,
            // — и список терял ровный край, по которому его просматривают сверху вниз.
            Column(modifier = Modifier.weight(1f)) {
                Text(walk.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    formatDateOnly(walk.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(formatDistanceKm(walk.distanceMeters))
                    Text(walk.endTime?.let { formatDurationShort(it - walk.startTime) } ?: "—")
                    // Был эмодзи "🍄 N" — единственное место в интерфейсе, где смысл нёс символ
                    // из шрифта. Эмодзи рисуется системным цветным шрифтом: он не подчиняется
                    // теме, выглядит по-разному на Android и iOS и не встаёт в один ряд с
                    // остальной служебной графикой. Тот же гриб теперь берётся из общего набора
                    // (ic_mushrooms.webp) и красится текущим цветом контента, как любая иконка.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_mushrooms),
                            contentDescription = "${walk.mushroomCount} ${mushroomsUnitLabel(walk.mushroomCount)}",
                            modifier = Modifier.size(MUSHROOM_ICON_SIZE),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(walk.mushroomCount.toString())
                    }
                }
            }
        }
    }
}

/**
 * Cached, tile-backed snapshot when [thumbnailPath] resolves to a real file (rendered once, at
 * `finish()`, by [leshy.mushrooms.map.data.platform.WalkThumbnailRenderer]) — falls back to the
 * plain [WalkRouteThumbnail] polyline for walks that predate this feature, failed renders (e.g.
 * offline), and the brief window right after Finish before the async render completes. Loaded via
 * Coil (`coil3.compose.AsyncImage`, `"file://"` model — Coil resolves local file URIs on both
 * platforms out of the box, no network engine needed) rather than a platform-specific
 * `expect`/`actual` decoder, per this project's rule of preferring one cross-platform library over
 * duplicated native code wherever one already exists (see CLAUDE.md §5.7).
 */
@Composable
private fun WalkThumbnail(
    thumbnailPath: String?,
    track: List<GeoPoint>,
    findLocations: List<GeoPoint>,
    modifier: Modifier = Modifier,
) {
    var loadFailed by remember(thumbnailPath) { mutableStateOf(false) }
    if (thumbnailPath == null || loadFailed) {
        WalkRouteThumbnail(track = track, findLocations = findLocations, modifier = modifier)
    } else {
        AsyncImage(
            model = "file://$thumbnailPath",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(12.dp)),
            onError = { loadFailed = true },
        )
    }
}
