package compose.project.leshy.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.unit.DpSize
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

data class PlaceMarker(val id: Long, val lat: Double, val lon: Double, val photoPath: String?)

/**
 * Renders [places] as pin-shaped markers (see `PlaceMarkerIcon.kt`), one `SymbolLayer` per place —
 * unlike mushroom finds, which share one icon per species and so get grouped into one layer per
 * species (`ClusteredFindsLayers.kt`/`LiveTrackMap.kt`), each place has its own distinct photo, so
 * there's nothing to group by; this follows the same "one layer per icon" shape the rest of the
 * file already uses, just with the group size always 1. No clustering — each place is an
 * individually user-authored note, not a density signal like finds are.
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
    places.forEach { place ->
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
