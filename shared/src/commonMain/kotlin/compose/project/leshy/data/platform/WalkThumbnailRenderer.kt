package compose.project.leshy.data.platform

import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.GeoPoint

/** One mushroom find, paired with its species — see [WalkThumbnailRenderer.render]'s `speciesMarkers`. */
data class WalkFindMarker(val location: GeoPoint, val category: Category)

/**
 * Renders a static map snapshot (real tiles + route + find markers) for a finished walk, once,
 * off-screen — not a live map view. Implementations resolve their own on-disk cache location
 * (mirrors how [rememberCameraLauncher] resolves its own "photos" directory rather than taking
 * one from the caller, since only platform code has a [android.content.Context] / `NSFileManager`
 * to resolve it from).
 *
 * [anchor] is the walk's current/last known location (e.g. GPS fix at Finish time) — used as the
 * snapshot region when [track] has too few points to bound a region itself (short walks where GPS
 * hadn't produced a second track point yet). Without it, such walks would have no location to
 * render a real map background around and would fall back to a backgroundless silhouette.
 *
 * [sizePx]/[variant] let one walk have more than one cached snapshot at different quality/purpose
 * — the Archive list thumbnail (defaults, `walk_$walkId.png`, plain colored find dots) and a
 * bigger share-quality render (non-default `variant`, real per-species icons via [speciesMarkers])
 * don't collide or overwrite each other. [findLocations] is still always used for the dot path —
 * both the default render, and as a per-marker fallback when [speciesMarkers] is supplied but an
 * individual icon fails to resolve/decode.
 *
 * [markerIconSizePx] is the aspect-fit box each [speciesMarkers] icon is baked into — callers
 * should scale it the same way the live map scales `ui/map/MushroomMarkerIcon.kt`'s
 * `mushroomMarkerSize` (`LocalMushroomMarkerSizeScale`), so the exported map's icons match the
 * user's own marker-size preference from Settings instead of a fixed size.
 *
 * Returns the absolute path to the written PNG on success, or `null` on any failure (no network,
 * snapshot timeout, no location known at all, etc.) — callers must fail gracefully, not crash or
 * block.
 */
interface WalkThumbnailRenderer {
    suspend fun render(
        walkId: Long,
        track: List<GeoPoint>,
        findLocations: List<GeoPoint>,
        anchor: GeoPoint?,
        sizePx: Int = 240,
        variant: String = "",
        speciesMarkers: List<WalkFindMarker> = emptyList(),
        markerIconSizePx: Int = 64,
    ): String?
}
