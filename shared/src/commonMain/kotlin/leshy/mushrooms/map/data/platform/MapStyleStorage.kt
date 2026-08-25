package leshy.mushrooms.map.data.platform

/**
 * Resolves where the pinned local copy of the map style JSON should be written — same shape as
 * [PhotoStorage], only platform code has a `Context`/`NSFileManager` to resolve it from. See
 * `MapStyleCacheRepository` for why this pinned copy exists (freezing the app to the tile URL
 * template it first successfully fetched, instead of silently drifting whenever OpenFreeMap
 * rotates its planet snapshot — see `ui/map/CLAUDE.md`).
 */
interface MapStyleStorage {
    /** Absolute path for the pinned style file named [fileName] inside this app's own storage. */
    fun resolvePath(fileName: String): String
}
