package leshy.mushrooms.map.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import leshy.mushrooms.map.domain.model.GeoPoint
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.IconPitchAlignment
import org.maplibre.compose.expressions.value.IconRotationAlignment
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

/**
 * Сторона битмапа сектора. Точка местоположения — его ЦЕНТР, поэтому длина луча ограничена
 * половиной стороны: `iconRotate` вращает иконку вокруг её центра, и только при таком раскладе
 * вершина сектора остаётся ровно на координате при любом угле.
 */
private val HEADING_CONE_SIZE: Dp = 76.dp

/** Длина луча и раствор сектора, долями стороны битмапа и в градусах. */
private const val CONE_RADIUS_FRACTION = 0.47f
private const val CONE_TOTAL_ANGLE_DEGREES = 60f

/**
 * Непрозрачность сектора: у вершины, в середине луча и на каком расстоянии от вершины стоит эта
 * середина. Три остановки, а не две, — иначе линейное затухание от вершины к концу съедает
 * бóльшую часть луча: на половине длины от 0.85 остаётся 0.42, и сектор превращается в еле
 * заметную дымку. Со средней остановкой густая часть держится до двух третей длины, а гаснет
 * только хвост.
 *
 * Первая версия шла двумя остановками от 0.45 — владелец на устройстве сообщил, что сектора
 * практически не видно (2026-09-24). Число поднято вместе с формой затухания, потому что виновато
 * было и то, и другое.
 */
private const val CONE_START_ALPHA = 0.85f
private const val CONE_MID_ALPHA = 0.6f
private const val CONE_MID_STOP = 0.66f

/**
 * Сектор направления взгляда — то, куда повёрнут телефон, — от точки местоположения.
 *
 * Рисуется ПОД самой точкой (вызывать перед её `CircleLayer`): вершина сектора и центр точки
 * совпадают, и точка должна оставаться сверху цельным кружком.
 *
 * [headingDegrees] — лямбда, а не значение, СПЕЦИАЛЬНО: компас присылает до 16 событий в секунду
 * (`SENSOR_DELAY_UI` плюс порог `HEADING_MIN_CHANGE_DEGREES`), и чтение его значения здесь, внутри
 * собственной области перекомпоновки этого слоя, оставляет экран «Запись» в покое — тогда как то
 * же значение в составе `RecordUiState` перекомпоновывало бы экран целиком с той же частотой.
 * Подробности — KDoc `RecordViewModel.deviceHeading`.
 *
 * Вызывать только внутри блока `MaplibreMap { ... }`.
 */
@Composable
fun LocationHeadingLayer(location: GeoPoint, headingDegrees: () -> Double?, color: Color) {
    val heading = headingDegrees()
    if (heading == null) return

    val painter = remember(color) { HeadingConePainter(color) }
    val source = rememberGeoJsonSource(GeoJsonData.Features(Point(Position(location.lon, location.lat))))
    SymbolLayer(
        id = "current-location-heading",
        source = source,
        iconImage = image(painter, size = DpSize(HEADING_CONE_SIZE, HEADING_CONE_SIZE)),
        // Курс отсчитывается от ИСТИННОГО севера (договор `HeadingProvider`), поэтому и сектор
        // обязан отсчитываться от севера карты, а не от верха экрана: карту можно повернуть
        // жестом (на то у неё и компас в орнаментах), и с выравниванием по вьюпорту сектор
        // показывал бы верное направление только при карте «севером вверх».
        iconRotationAlignment = const(IconRotationAlignment.Map),
        // По той же причине сектор лежит в плоскости земли, а не стоит к экрану: на наклонённой
        // карте он должен уходить в перспективу вместе с ней.
        iconPitchAlignment = const(IconPitchAlignment.Map),
        iconRotate = const(heading.toFloat()),
        // Иначе раскладка символов прячет сектор, стоит ему задеть маркер находки или булавку
        // места, — а он задевает их постоянно, потому что человек стоит среди своих же находок.
        iconAllowOverlap = const(true),
    )
}

/**
 * Сектор с растворяющимся к концу лучом, смотрящий ВВЕРХ битмапа (то есть на север при
 * `iconRotate = 0`), вершиной точно в центре.
 *
 * Радиальный градиент, а не заливка: жёсткий край луча читался бы как граница чего-то реального —
 * области, зоны охвата, — а сектор не обозначает ничего, кроме направления.
 */
private class HeadingConePainter(private val color: Color) : Painter() {
    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * CONE_RADIUS_FRACTION
        // -90° — верх битмапа: у Compose угол 0° смотрит вправо, а отсчёт идёт по часовой стрелке.
        val startAngle = -90f - CONE_TOTAL_ANGLE_DEGREES / 2f

        val cone = Path().apply {
            moveTo(center.x, center.y)
            arcTo(
                rect = Rect(center = center, radius = radius),
                startAngleDegrees = startAngle,
                sweepAngleDegrees = CONE_TOTAL_ANGLE_DEGREES,
                forceMoveTo = false,
            )
            close()
        }
        drawPath(
            path = cone,
            brush = Brush.radialGradient(
                0f to color.copy(alpha = CONE_START_ALPHA),
                CONE_MID_STOP to color.copy(alpha = CONE_MID_ALPHA),
                1f to color.copy(alpha = 0f),
                center = center,
                radius = radius,
            ),
        )
    }
}
