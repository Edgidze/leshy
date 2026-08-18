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
 * Must be called directly inside a `MaplibreMap { ... }` block. [onPlaceClick] fires with the
 * tapped place's [PlaceMarker.id] — resolved trivially per-layer (each layer's `onClick` already
 * knows which place it belongs to via the [key]/closure), no GeoJSON feature-property lookup
 * needed.
 */
@Composable
fun PlaceMarkersLayer(places: List<PlaceMarker>, onPlaceClick: (Long) -> Unit) {
    places.forEach { place ->
        key(place.id) {
            val painter = rememberPlaceMarkerPainter(place.photoPath)
            val source = rememberGeoJsonSource(GeoJsonData.Features(Point(Position(place.lon, place.lat))))
            SymbolLayer(
                id = "place-${place.id}",
                source = source,
                iconImage = image(painter, size = DpSize(PLACE_MARKER_WIDTH, PLACE_MARKER_HEIGHT)),
                iconAnchor = const(SymbolAnchor.Bottom),
                iconAllowOverlap = const(true),
                onClick = { onPlaceClick(place.id); ClickResult.Consume },
            )
        }
    }
}
