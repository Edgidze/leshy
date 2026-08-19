package compose.project.leshy.data.repository

import compose.project.leshy.data.platform.HttpTextFetcher
import compose.project.leshy.data.platform.MapStyleStorage
import compose.project.leshy.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.maplibre.compose.style.BaseStyle

private const val STYLE_CACHE_FILE_NAME = "style.json"

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
     * [compose.project.leshy.domain.usecase.RefreshMapDataUseCase] uses this to decide whether
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
        previous != null && previous != json
    }

    /** Plain URL/path string for consumers that need a bare string, not a [BaseStyle] — the
     * offline-download repository (native `OfflineManager.create` takes a `styleUrl: String`).
     * Returns the pinned local file so downloads match exactly what the live map is showing; falls
     * back to the remote URL only before the very first successful fetch. */
    fun currentStyleReference(): String =
        if (fileSystem.exists(stylePath)) "file://$stylePath" else OPEN_FREE_MAP_STYLE_URL
}
