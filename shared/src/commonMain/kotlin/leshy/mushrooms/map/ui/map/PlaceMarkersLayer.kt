package leshy.mushrooms.map.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Geometry
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

data class PlaceMarker(val id: Long, val lat: Double, val lon: Double, val photoPath: String?)

/**
 * Renders [places] as pin-shaped markers (see `PlaceMarkerIcon.kt`). No clustering — each place is
 * an individually user-authored note, not a density signal like finds are.
 *
 * **Разбито надвое по цене слоя** (см. `ui/map/CLAUDE.md`, «Стоимость слоя»): места БЕЗ фото все
 * до одного рисуются дефолтным пином, то есть делят одну иконку — им хватает ОДНОГО слоя с общим
 * источником, а id кликнутого места достаётся из `properties` фичи. Места С фото так не
 * схлопываются: у каждого своя картинка, а один `SymbolLayer` умеет ровно одну `iconImage`; они
 * остаются слоем на место и выдаются по одному за кадр, тем же приёмом, что `ClusteredFindsLayers`.
 *
 * Must be called directly inside a `MaplibreMap { ... }` block. [onPlaceClick]/[onPlaceLongPress]
 * fire with the tapped/held place's [PlaceMarker.id] — resolved trivially per-layer (each layer's
 * `onClick`/`onLongClick` already knows which place it belongs to via the [key]/closure), no
 * GeoJSON feature-property lookup needed. [idPrefix] keeps layer/source ids from colliding if this
 * is called twice on the same map (e.g. `LiveTrackMap`'s current-walk places plus a background
 * layer of other walks' places — same rationale as `ClusteredFindsLayers.idPrefix`).
 *
 * [onPlaceLongPress] is the map's own native long-click gesture (`addOnMapLongClickListener`
 * Android-side, a `UILongPressGestureRecognizer` registered directly on the `MLNMapView`
 * iOS-side) — deliberately NOT a Compose `pointerInput` sibling drawn on top of the map. An
 * earlier version tried exactly that (`MarkerLongPressOverlay.kt`, removed) and it silently never
 * fired on iOS: `IosMapView.kt`'s embedded `MLNMapView` uses `UIKitInteropInteractionMode
 * .NonCooperative`, under which touches landing inside the interop view's bounds never reach a
 * sibling Compose composable at all, regardless of z-order or how small its hit target is. Routing
 * through the layer's own native recognizer sidesteps that entirely, at the cost of losing control
 * over the hold duration — it's whatever `UILongPressGestureRecognizer`/Android's `GestureDetector`
 * default to (~500ms), since the library exposes no way to configure it.
 */
@Composable
fun PlaceMarkersLayer(
    places: List<PlaceMarker>,
    onPlaceClick: (Long) -> Unit,
    idPrefix: String = "place",
    onPlaceLongPress: (Long) -> Unit = {},
) {
    val (plain, photographed) = remember(places) { places.partition { it.photoPath == null } }

    if (plain.isNotEmpty()) {
        val painter = rememberPlaceMarkerPainter(null)
        val plainSource = rememberGeoJsonSource(
            GeoJsonData.Features(
                FeatureCollection(
                    plain.map { place ->
                        Feature(
                            Point(Position(place.lon, place.lat)),
                            properties = buildJsonObject { put(PLACE_ID_PROPERTY, place.id) },
                        )
                    },
                ),
            ),
        )
        SymbolLayer(
            id = "$idPrefix-plain",
            source = plainSource,
            iconImage = image(painter, size = DpSize(PLACE_MARKER_WIDTH, PLACE_MARKER_HEIGHT)),
            iconAnchor = const(SymbolAnchor.Bottom),
            iconAllowOverlap = const(true),
            onClick = { features -> features.placeId()?.let(onPlaceClick); ClickResult.Consume },
            onLongClick = { features -> features.placeId()?.let(onPlaceLongPress); ClickResult.Consume },
        )
    }

    // Места с фото — по слою на место, каждое стоит декода фото, растеризации пина и регистрации
    // битмапа в стиле. Пачка сдвигается за кадр перехода целиком, не дробится, см.
    // [rememberHistoryRevealed].
    val revealed = rememberHistoryRevealed(photographed)

    if (revealed) photographed.forEach { place ->
        key(place.id) {
            val painter = rememberPlaceMarkerPainter(place.photoPath)
            val source = rememberGeoJsonSource(GeoJsonData.Features(Point(Position(place.lon, place.lat))))
            SymbolLayer(
                id = "$idPrefix-${place.id}",
                source = source,
                iconImage = image(painter, size = DpSize(PLACE_MARKER_WIDTH, PLACE_MARKER_HEIGHT)),
                iconAnchor = const(SymbolAnchor.Bottom),
                iconAllowOverlap = const(true),
                onClick = { onPlaceClick(place.id); ClickResult.Consume },
                onLongClick = { onPlaceLongPress(place.id); ClickResult.Consume },
            )
        }
    }
}

/** Ключ, под которым id места едет в `properties` фичи общего слоя мест без фото. */
private const val PLACE_ID_PROPERTY = "placeId"

/** Id места из кликнутых фич общего слоя — обратная сторона [PLACE_ID_PROPERTY]. */
private fun List<Feature<Geometry, JsonObject?>>.placeId(): Long? =
    firstNotNullOfOrNull { it.properties?.get(PLACE_ID_PROPERTY)?.jsonPrimitive?.longOrNull }
