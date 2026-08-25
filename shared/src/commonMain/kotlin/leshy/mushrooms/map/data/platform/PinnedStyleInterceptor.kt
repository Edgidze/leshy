package leshy.mushrooms.map.data.platform

/**
 * Feeds the app's currently pinned map style JSON (see
 * [leshy.mushrooms.map.data.repository.MapStyleCacheRepository]) straight into the platform's
 * native MapLibre HTTP client, short-circuiting any real network fetch of
 * [leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_STYLE_URL] with these exact bytes instead.
 *
 * Why this exists: MapLibre's native offline downloader (`OfflinePackDefinition.styleUrl`) can only
 * resolve a style through its own real HTTP fetch — it has no API to hand it already-loaded bytes
 * directly, and a `file://` URL is confirmed not to work (`Mbgl-HttpRequest: Unable to parse
 * resourceUrl file://...`). Without this, a region downloaded after OpenFreeMap rotates its planet
 * snapshot on the server gets its tiles cached under a different URL template than what the pinned
 * local style (which the live map always renders from) will ever request again — a silent,
 * permanent cache miss offline, even though the pack honestly reports itself "Complete". Routing the
 * native downloader's style fetch through this interceptor instead means every offline download is
 * always byte-identical to what the live map already uses, forever — no dependency on the server's
 * future behavior. See `ui/map/CLAUDE.md` for the full incident writeup.
 *
 * Installed once, eagerly, at Koin startup (`createdAtStart = true` in each platform module) — must
 * be in place before any `MaplibreMap`/`OfflineManager` first touches the network.
 */
interface PinnedStyleInterceptor {
    /** Replaces the bytes served in place of a real network fetch of the pinned style URL. */
    fun setPinnedStyle(json: String)
}
