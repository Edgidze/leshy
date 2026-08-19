package compose.project.leshy.ui.map

/**
 * OpenFreeMap's "liberty" vector style — free, no API key, no usage quota (unlike
 * tile.openstreetmap.org's raster tiles, which are a light-use-only community service and
 * render blurry on HiDPI screens since they're not offered in a retina/@2x variant).
 *
 * The three live map composables (`LiveTrackMap`/`AggregatedFindsMap`/`RegionPickerMap`) don't
 * reference this directly as a `BaseStyle` — they get a pinned/frozen [org.maplibre.compose.style.BaseStyle]
 * from `MapStyleCacheRepository` instead (see its doc comment for why: the remote style.json's
 * tile URL template drifts over time and orphans previously cached/downloaded tiles). This raw
 * string remains the bootstrap URL that repository fetches from, and is also used directly by the
 * native off-screen snapshotters for archive-card thumbnails (Android `MapSnapshotter`, iOS
 * `MLNMapSnapshotter`), which take a plain style URL/path, not the maplibre-compose wrapper.
 */
internal const val OPEN_FREE_MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
