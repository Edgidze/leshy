package leshy.mushrooms.map.ui.map

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.GeoPoint
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

/**
 * Какие концы трека размечены бейджами.
 *
 * [StartOnly] — для экрана «Запись»: конца у идущей прогулки ещё нет, его роль играет точка
 * геолокации, и бейдж финиша на ней означал бы «прогулка закончена», чего не произошло.
 */
enum class TrackEndpoints { None, StartOnly, StartAndFinish }

/**
 * Диаметр бейджа. Заметно меньше булавки места ([PLACE_MARKER_WIDTH] = 44dp) и фото находки
 * ([MUSHROOM_MARKER_BASE_SIZE] = 64dp) — это разметка маршрута, а не его содержимое, и заслонять
 * содержимое она не должна. По той же причине НЕ масштабируется пользовательской настройкой
 * размера маркеров ([LocalMushroomMarkerSizeScale]): та настройка про то, насколько крупно видно
 * находки, а концы трека к находкам не относятся.
 */
private val TRACK_ENDPOINT_SIZE: Dp = 24.dp

/**
 * Обводка и значок — долями диаметра, а не в `Dp`: [Painter] рисует уже в пикселях бейка, и доля
 * приходит одинаковой при любой плотности экрана сама собой (тот же приём, что в
 * `AndroidWalkThumbnailRenderer`).
 *
 * Обводка белая и та же, что у точки геолокации, — на тёмном лесном массиве или на воде бейдж
 * цвета трека без неё сливается с подложкой. Рисуется ПО самой окружности, поэтому радиус заливки
 * ужат на её половину: иначе внешняя половина обводки вышла бы за край битмапа и обрезалась.
 */
private const val BADGE_STROKE_FRACTION = 1.5f / 24f
private const val BADGE_GLYPH_FRACTION = 0.62f

/**
 * Круглый бейдж цвета трека с белым значком внутри — маркеры начала и конца маршрута ОДНОЙ
 * прогулки.
 *
 * Почему не просто кружок цвета трека: на «Записи» маркер старта в начале прогулки стоит вплотную
 * к точке геолокации, а та и есть кружок с белой обводкой — вышли бы две точки «ты здесь» разного
 * цвета. Значок внутри разводит их по смыслу, ничего не требуя от пользователя.
 *
 * Значки — стоковые Material (`Hiking`/`Flag`), собственной графики тут нет. Если пешеход на
 * устройстве окажется неразборчивым пятном (14dp на бейдж 24dp — предел для значка с такой
 * деталировкой), замена на `Icons.Filled.PlayArrow` — правка одной строки; форма бейджа при этом
 * не меняется.
 *
 * Слои — по одному на конец, а не один общий с иконкой по свойству фичи: один [SymbolLayer] умеет
 * ровно одну `iconImage`, а концов всего два, то есть потолок цены — 2 источника + 2 слоя + 2
 * бейка картинки на карту (см. `ui/map/CLAUDE.md`, «Стоимость слоя»).
 *
 * Вызывать только внутри блока `MaplibreMap { ... }`.
 */
@Composable
fun TrackEndpointsLayer(track: List<GeoPoint>, endpoints: TrackEndpoints, badgeColor: Color) {
    val start = if (endpoints != TrackEndpoints.None) track.firstOrNull() else null
    // Финиш не рисуется, пока трек короче двух точек: старт и финиш совпали бы, бейджи легли бы
    // ровно друг на друга, и верхний просто спрятал бы нижний.
    val finish = if (endpoints == TrackEndpoints.StartAndFinish && track.size >= 2) track.last() else null

    if (start != null) {
        TrackEndpointBadge("track-start", start, rememberTrackEndpointPainter(Icons.Filled.Hiking, badgeColor))
    }
    if (finish != null) {
        TrackEndpointBadge("track-finish", finish, rememberTrackEndpointPainter(Icons.Filled.Flag, badgeColor))
    }
}

@Composable
private fun TrackEndpointBadge(id: String, point: GeoPoint, painter: Painter) {
    val source = rememberGeoJsonSource(GeoJsonData.Features(Point(Position(point.lon, point.lat))))
    SymbolLayer(
        id = id,
        source = source,
        iconImage = image(painter, size = DpSize(TRACK_ENDPOINT_SIZE, TRACK_ENDPOINT_SIZE)),
        // Без этого бейдж пропадает, стоит ему столкнуться с маркером находки или булавкой места:
        // раскладка символов у MapLibre по умолчанию прячет то, что перекрывается, а конец
        // маршрута на поляне с находками перекрывается почти всегда.
        iconAllowOverlap = const(true),
    )
}

/**
 * Новый инстанс [Painter] на каждый новый цвет — обязательно, а не из экономии: `image(painter)`
 * бейкает пейнтер в битмап один раз и кэширует результат по идентичности объекта (подробности —
 * `PlaceMarkerIcon.kt`), так что переживший смену темы пейнтер остался бы в стиле старым цветом.
 */
@Composable
private fun rememberTrackEndpointPainter(icon: ImageVector, badgeColor: Color): Painter {
    val glyph = rememberVectorPainter(icon)
    return remember(glyph, badgeColor) { TrackEndpointPainter(glyph, badgeColor) }
}

private class TrackEndpointPainter(
    private val glyph: Painter,
    private val badgeColor: Color,
) : Painter() {
    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val diameter = size.minDimension
        val strokeWidth = diameter * BADGE_STROKE_FRACTION
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = diameter / 2f - strokeWidth / 2f

        drawCircle(color = badgeColor, radius = radius, center = center)
        drawCircle(color = Color.White, radius = radius, center = center, style = Stroke(width = strokeWidth))

        val glyphSize = diameter * BADGE_GLYPH_FRACTION
        translate(left = center.x - glyphSize / 2f, top = center.y - glyphSize / 2f) {
            with(glyph) { draw(Size(glyphSize, glyphSize), colorFilter = ColorFilter.tint(Color.White)) }
        }
    }
}
