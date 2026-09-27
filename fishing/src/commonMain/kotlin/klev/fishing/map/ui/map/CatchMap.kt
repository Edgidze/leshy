package klev.fishing.map.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.MultiLineString
import org.maplibre.spatialk.geojson.MultiPoint
import org.maplibre.spatialk.geojson.Position
import leshy.mushrooms.map.data.repository.MapStyleCacheRepository
import leshy.mushrooms.map.domain.model.GeoPoint
import leshy.mushrooms.map.ui.map.mapOrnamentOptions
import leshy.mushrooms.map.ui.map.mapRenderOptions
import leshy.mushrooms.map.ui.map.rememberMapOverlayColors
import leshy.mushrooms.map.ui.util.parseHexColor
import org.koin.compose.koinInject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource

/**
 * Ниже этого размаха рамка считается вырожденной — вся рыба поймана фактически в одной точке.
 * Число и его причина взяты у `LiveTrackMap` в `:shared`: подогнать камеру под рамку нулевой
 * площади значит уехать на предельный зум тайлового сервера и показать размытую растяжку вместо
 * карты. Там константа приватная, поэтому здесь повторено значение, а не сам вывод.
 */
private const val MIN_BOUNDS_SPAN_DEGREES = 0.001

/** Зум для такой вырожденной рамки — уровень улицы, как у грибной карты прогулки. */
private const val SINGLE_SPOT_ZOOM = 15.0

/** Одна отметка улова на карте: где и каким цветом (цвет — вида). */
data class CatchMarker(val lat: Double, val lon: Double, val colorHex: String)

/**
 * Карта улова за всё время.
 *
 * **Своя, а не `AggregatedFindsMap` из `:shared`** — хотя та делает ровно то же самое и уже
 * переиспользуется на детализации рыбалки. Причина в её слое отметок: `ClusteredFindsLayers`
 * группирует маркеры ПО ИЛЛЮСТРАЦИИ и молча выбрасывает те, у которых её нет
 * (`mapNotNull { (icon, group) -> icon?.let { … } }`). У грибов иллюстрация есть почти у каждого
 * вида, у рыб сейчас нет ни у одного — и карта улова получалась пустой при непустом архиве.
 * Правка на стороне `:shared` была бы вмешательством в грибной экран ради рыбацкого; здесь
 * отметки рисуются кружком цвета вида, как `LiveTrackMap` рисует отметки без иллюстрации.
 *
 * Когда у рыб появятся иллюстрации, этот файл — место, где они подключатся.
 */
@Composable
fun CatchMap(
    tracks: Map<Long, List<GeoPoint>>,
    markers: List<CatchMarker>,
    modifier: Modifier = Modifier,
) {
    val cameraState = rememberCameraState(firstPosition = CameraPosition(target = Position(0.0, 0.0), zoom = 1.0))
    val overlayColors = rememberMapOverlayColors()

    val allPoints = remember(tracks, markers) {
        tracks.values.flatten().map { it.lat to it.lon } + markers.map { it.lat to it.lon }
    }
    LaunchedEffect(allPoints) {
        if (allPoints.isEmpty()) return@LaunchedEffect
        val lats = allPoints.map { it.first }
        val lons = allPoints.map { it.second }
        if (lats.max() - lats.min() < MIN_BOUNDS_SPAN_DEGREES &&
            lons.max() - lons.min() < MIN_BOUNDS_SPAN_DEGREES
        ) {
            // Вся рыба в одной точке — рыбалка со стоянки, а не с обходом. Подгонка под такую
            // рамку уводила камеру на предельный зум, и карта выезда открывалась серым пятном без
            // единого ориентира (видно на эмуляторе 2026-09-28).
            cameraState.position = cameraState.position.copy(
                target = Position((lons.min() + lons.max()) / 2, (lats.min() + lats.max()) / 2),
                zoom = SINGLE_SPOT_ZOOM,
            )
        } else {
            cameraState.jumpTo(
                BoundingBox(west = lons.min(), south = lats.min(), east = lons.max(), north = lats.max()),
                padding = PaddingValues(32.dp),
            )
        }
    }

    val mapStyleCacheRepository = koinInject<MapStyleCacheRepository>()
    val baseStyle by mapStyleCacheRepository.baseStyle.collectAsState()

    Box(modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            baseStyle = baseStyle,
            cameraState = cameraState,
            options = MapOptions(renderOptions = mapRenderOptions, ornamentOptions = mapOrnamentOptions),
        ) {
            // ОДИН слой на все треки, а не по слою на рыбалку: слой вместе с источником — это
            // синхронная правка стиля в нативном коде, и каждая такая правка заставляет MapLibre
            // перевалидировать стиль целиком (разбор — ui/map/CLAUDE.md в :shared, «Стоимость слоя»).
            val routeLines = remember(tracks) {
                tracks.values.filter { it.size >= 2 }.map { points -> points.map { Position(it.lon, it.lat) } }
            }
            if (routeLines.isNotEmpty()) {
                val routesSource = rememberGeoJsonSource(GeoJsonData.Features(MultiLineString(routeLines)))
                LineLayer(
                    id = "catch-routes",
                    source = routesSource,
                    color = const(overlayColors.track),
                    width = const(2.dp),
                    opacity = const(0.45f),
                )
            }

            // По слою на ЦВЕТ, а не на отметку: видов в одной поездке единицы, а слоёв по числу
            // отметок было бы столько же, сколько рыбы.
            markers.groupBy { it.colorHex }.forEach { (colorHex, group) ->
                key(colorHex) {
                    val source = rememberGeoJsonSource(
                        GeoJsonData.Features(MultiPoint(group.map { Position(it.lon, it.lat) })),
                    )
                    CircleLayer(
                        id = "catches-$colorHex",
                        source = source,
                        color = const(parseHexColor(colorHex, Color.Gray)),
                        radius = const(7.dp),
                        strokeColor = const(Color.White),
                        strokeWidth = const(2.dp),
                    )
                }
            }
        }
    }
}
