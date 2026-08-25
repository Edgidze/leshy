package leshy.mushrooms.map.data.repository

import leshy.mushrooms.map.data.platform.HttpTextFetcher
import leshy.mushrooms.map.data.platform.MapStyleStorage
import leshy.mushrooms.map.data.platform.PinnedStyleInterceptor
import leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.maplibre.compose.style.BaseStyle

private const val STYLE_CACHE_FILE_NAME = "style.json"
private val TILE_HOST_PROBE_TIMEOUT = 8.seconds

/**
 * Freezes the app to the FIRST tile URL template it ever successfully resolves from OpenFreeMap's
 * `style.json`, instead of re-fetching (and silently drifting to) whatever template is live on
 * every single map screen visit. Both MapLibre's ambient tile cache and native offline packs are
 * keyed by the literal request URL, which embeds a versioned planet-snapshot timestamp
 * (`.../planet/<timestamp>/{z}/{x}/{y}.pbf`) — OpenFreeMap bumps that timestamp when it rebuilds,
 * which otherwise orphans every previously cached/downloaded tile out from under the live map with
 * no warning. See `ui/map/CLAUDE.md` for the full incident writeup.
 *
 * The pinned copy only ever changes via an explicit [refreshFromNetwork] call (wired to a user
 * action in Settings) — never automatically beyond the very first launch — so a region downloaded
 * once keeps rendering under the same template forever, until the user deliberately opts into
 * fresher map data (and is warned that offline regions may then need re-downloading).
 */
class MapStyleCacheRepository(
    private val storage: MapStyleStorage,
    private val httpTextFetcher: HttpTextFetcher,
    private val pinnedStyleInterceptor: PinnedStyleInterceptor,
) {
    private val fileSystem = FileSystem.SYSTEM
    private val stylePath: Path get() = storage.resolvePath(STYLE_CACHE_FILE_NAME).toPath()

    private val _baseStyle = MutableStateFlow<BaseStyle>(BaseStyle.Uri(OPEN_FREE_MAP_STYLE_URL))
    val baseStyle: StateFlow<BaseStyle> = _baseStyle.asStateFlow()

    private val loadMutex = Mutex()
    private var loaded = false

    /** Loads any already-pinned copy from disk; on the very first ever launch (no pinned copy
     * yet), fetches once from the network so every subsequent screen visit uses the frozen local
     * copy. Safe to call from every map screen — only does real work once per app session. */
    suspend fun ensureLoaded() {
        if (loaded) return
        loadMutex.withLock {
            if (loaded) return
            withContext(Dispatchers.Default) {
                val cached = runCatching { fileSystem.read(stylePath) { readUtf8() } }.getOrNull()
                loaded = if (cached != null) {
                    _baseStyle.value = BaseStyle.Json(cached)
                    pinnedStyleInterceptor.setPinnedStyle(cached)
                    true
                } else {
                    // No pinned copy yet (very first launch) and no network — leave `loaded` false
                    // so the next screen visit retries automatically, instead of getting stuck on
                    // the unpinned remote-Uri fallback for the rest of the app session.
                    refreshFromNetworkLocked().isSuccess
                }
            }
        }
    }

    /** Explicit, user-triggered re-fetch — the only way the pinned style (and thus every future
     * map screen and offline download) changes after the first successful fetch. A failed refresh
     * never un-pins an already-successfully-loaded copy. The [Result]'s payload is whether the
     * fetched content actually differs from what was pinned before — `false` means either nothing
     * changed server-side or there was no previous pin to compare against —
     * [leshy.mushrooms.map.domain.usecase.RefreshMapDataUseCase] uses this to decide whether
     * existing offline regions need re-downloading. */
    suspend fun refreshFromNetwork(): Result<Boolean> = loadMutex.withLock {
        withContext(Dispatchers.Default) { refreshFromNetworkLocked() }.also { loaded = it.isSuccess || loaded }
    }

    private suspend fun refreshFromNetworkLocked(): Result<Boolean> = runCatching {
        val previous = runCatching { fileSystem.read(stylePath) { readUtf8() } }.getOrNull()
        val json = httpTextFetcher.fetchText(OPEN_FREE_MAP_STYLE_URL)
        fileSystem.createDirectories(stylePath.parent!!)
        fileSystem.write(stylePath) { writeUtf8(json) }
        _baseStyle.value = BaseStyle.Json(json)
        pinnedStyleInterceptor.setPinnedStyle(json)
        previous != null && previous != json
    }

    /** Independent connectivity probe for the tile host — needed because once the style is
     * pinned, MapLibre loads it from the local file instantly and reports success
     * (`onMapLoadFinished`) regardless of whether the network is up, and maplibre-compose exposes
     * no per-tile failure signal to app code. A blocked/unreachable host (the original bug report —
     * an ISP blocking `tiles.openfreemap.org`) otherwise fails completely silently. Reuses the
     * small `style.json` fetch rather than a dedicated endpoint — same host, cheap payload, and it
     * never writes to the pinned file (this is read-only probing, not a refresh). */
    suspend fun isTileHostReachable(): Boolean =
        withTimeoutOrNull(TILE_HOST_PROBE_TIMEOUT) {
            runCatching { httpTextFetcher.fetchText(OPEN_FREE_MAP_STYLE_URL) }.isSuccess
        } ?: false
}
