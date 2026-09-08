package leshy.mushrooms.map.data.repository

import leshy.mushrooms.map.data.platform.HttpTextFetcher
import leshy.mushrooms.map.data.platform.MapStyleStorage
import leshy.mushrooms.map.data.platform.PinnedStyleInterceptor
import leshy.mushrooms.map.data.style.darkenMapStyle
import leshy.mushrooms.map.data.style.fallbackMapStyle
import leshy.mushrooms.map.data.style.freezeStyleTileSources
import leshy.mushrooms.map.data.style.localizeMapStyle
import leshy.mushrooms.map.data.style.styleHasUnfrozenTileSources
import leshy.mushrooms.map.domain.model.AppLanguage
import leshy.mushrooms.map.ui.map.OPEN_FREE_MAP_STYLE_URL
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
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

/** Сколько ждать ответа в [MapStyleCacheRepository.probeTileHost], прежде чем считать канал
 * непригодным. Дольше держать бессмысленно: за это время не пришли 43 КБ стиля, значит мировой вид
 * (сотни килобайт вектора на тайл плюс растр Natural Earth) не придёт и за минуты. */
private val TILE_HOST_PROBE_TIMEOUT = 8.seconds

/** Ответ медленнее этого — уже «медленно», хотя связь и есть: 43 КБ за три секунды это ~15 КБ/с, а
 * один тайл мирового зума весит до 1.3 МБ. */
private val TILE_HOST_SLOW_AFTER = 3.seconds

/**
 * Freezes the app to the FIRST tile URL template it ever successfully resolves from OpenFreeMap's
 * `style.json`, instead of re-fetching (and silently drifting to) whatever template is live on
 * every single map screen visit. Both MapLibre's ambient tile cache and native offline packs are
 * keyed by the literal request URL, which embeds a versioned planet-snapshot timestamp
 * (`.../planet/<timestamp>/{z}/{x}/{y}.pbf`) — OpenFreeMap bumps that timestamp when it rebuilds,
 * which otherwise orphans every previously cached/downloaded tile out from under the live map with
 * no warning. See `ui/map/CLAUDE.md` for the full incident writeup.
 *
 * What gets pinned is the style with its tile URL TEMPLATES already resolved into it
 * ([freezeStyleTileSources]) — pinning the style alone was never enough, since OpenFreeMap's
 * `style.json` carries no tile URL at all, only a pointer to a TileJSON document that MapLibre
 * re-fetches daily and that is where the rotating snapshot timestamp actually lives.
 *
 * The pinned copy only ever changes via an explicit [refreshFromNetwork] call (wired to a user
 * action in Settings) — never automatically beyond the very first launch — so a region downloaded
 * once keeps rendering under the same template forever, until the user deliberately opts into
 * fresher map data (and is warned that offline regions may then need re-downloading).
 *
 * Orthogonal to all of that, the label language rides on top: what's pinned on disk and compared
 * across refreshes is always the RAW upstream JSON, while what the live map and the native SDK
 * actually get is that JSON run through [localizeMapStyle] for the current interface language (see
 * [setLabelLanguage]). Keeping the two apart is what stops a language switch from looking like a
 * style change to [leshy.mushrooms.map.domain.usecase.RefreshMapDataUseCase] and needlessly
 * re-downloading every offline region — which it must not, since the language of a label is decided
 * at render time from tile data that already carries every language at once.
 */
class MapStyleCacheRepository(
    private val storage: MapStyleStorage,
    private val httpTextFetcher: HttpTextFetcher,
    private val pinnedStyleInterceptor: PinnedStyleInterceptor,
) {
    private val fileSystem = FileSystem.SYSTEM
    private val stylePath: Path get() = storage.resolvePath(STYLE_CACHE_FILE_NAME).toPath()

    /**
     * Starts on the bundled [fallbackMapStyle] rather than on `BaseStyle.Uri(OPEN_FREE_MAP_STYLE_URL)`,
     * which is what it used to be. Two things that fallback never did: it loads instantly and offline,
     * so the app's own layers (track, finds, location dot) have a style to attach to even on a first
     * launch with no network — see [fallbackMapStyle] for why that was the whole bug — and it costs no
     * fetch of an unpinned remote style that [publish] then replaced anyway.
     */
    private val _baseStyle = MutableStateFlow<BaseStyle>(BaseStyle.Json(fallbackMapStyle(dark = false)))
    val baseStyle: StateFlow<BaseStyle> = _baseStyle.asStateFlow()

    private val loadMutex = Mutex()
    private var loaded = false

    /** The pinned bytes exactly as fetched — the input [publish] re-localizes on every language
     * change, and the only thing a refresh ever compares against. */
    private val pinnedRawJson = MutableStateFlow<String?>(null)
    private val labelLanguage = MutableStateFlow(AppLanguage.EN)
    private val darkTheme = MutableStateFlow(false)

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
                    // Publish first, migrate second: the map must come up instantly and offline,
                    // and the migration below needs the network.
                    publish(cached)
                    freezeCachedStyleOnce(cached)
                    true
                } else {
                    // No pinned copy yet (very first launch) and no network — leave `loaded` false
                    // so the next screen visit retries automatically, instead of staying on the
                    // bundled fallback style for the rest of the app session.
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
        val json = freezeStyleTileSources(httpTextFetcher.fetchText(OPEN_FREE_MAP_STYLE_URL)) { url ->
            httpTextFetcher.fetchText(url)
        }
        fileSystem.createDirectories(stylePath.parent!!)
        fileSystem.write(stylePath) { writeUtf8(json) }
        publish(json)
        // Raw vs raw, never the localized derivatives — otherwise every interface language would
        // read as "the style changed" and re-queue every downloaded region for nothing.
        previous != null && previous != json
    }

    /**
     * Switches map labels to [language] — called from `App()` whenever the interface language
     * changes (and once at startup). Costs a JSON re-parse of the ~43 KB style and nothing else: no
     * network, no disk write, and explicitly no effect on offline packs, whose tiles carry all
     * languages at once and are keyed by URLs this never touches.
     *
     * Also re-arms [PinnedStyleInterceptor], so the native SDK — the offline downloader and both
     * platforms' archive-thumbnail snapshotters, which can only take a style URL — resolves
     * [OPEN_FREE_MAP_STYLE_URL] to bytes localized the same way. Those bytes are always the LIGHT
     * ones, even in dark theme — see [publish] for why.
     */
    suspend fun setLabelLanguage(language: AppLanguage) {
        if (labelLanguage.value == language) return
        labelLanguage.value = language
        // Nothing pinned yet: the fallback style carries no labels at all, and ensureLoaded() will
        // publish under this language on its own once there is something to publish.
        val raw = pinnedRawJson.value ?: return
        withContext(Dispatchers.Default) { publish(raw) }
    }

    /**
     * Switches the live map between the light pinned style and its dark repaint — called from
     * `App()` whenever the resolved theme changes (and once at startup). Rides on top of the pin
     * exactly like [setLabelLanguage] does, and for the same reason it is safe: [darkenMapStyle]
     * rewrites `paint` colours only, so not one URL — tile template, sprite or glyphs — differs
     * between the two themes. **Switching the theme therefore never invalidates a downloaded
     * offline region**, and needs no second pinned style.
     */
    suspend fun setDarkTheme(dark: Boolean) {
        if (darkTheme.value == dark) return
        darkTheme.value = dark
        // Nothing pinned yet — the map is showing the bundled fallback, which has a light and a dark
        // ground of its own and must follow the theme just like the real style does.
        val raw = pinnedRawJson.value ?: run {
            _baseStyle.value = BaseStyle.Json(fallbackMapStyle(dark))
            return
        }
        withContext(Dispatchers.Default) { publish(raw) }
    }

    /**
     * One-off migration for a style pinned before tile URLs were frozen into it (see
     * [freezeStyleTileSources]): resolves the TileJSON once and rewrites the pinned file in place.
     * Deliberately NOT treated as a style refresh — it re-queues nothing, because the whole point is
     * to stop the tile template from moving under downloaded regions, not to start big downloads
     * behind the user's back. Regions downloaded under an older template were already orphaned
     * before this ran; they stay that way until the user deletes and re-downloads them.
     *
     * Silent no-op without network — [ensureLoaded] retries on the next launch, and until then the
     * app behaves exactly as it did before.
     */
    private suspend fun freezeCachedStyleOnce(cached: String) {
        if (!styleHasUnfrozenTileSources(cached)) return
        val frozen = runCatching { freezeStyleTileSources(cached) { url -> httpTextFetcher.fetchText(url) } }
            .getOrNull() ?: return
        if (frozen == cached) return
        runCatching { fileSystem.write(stylePath) { writeUtf8(frozen) } }.onSuccess { publish(frozen) }
    }

    /**
     * The interceptor is deliberately fed the LIGHT bytes even in dark theme, while only the live
     * map gets the repaint. Two reasons, both load-bearing:
     *
     * - The interceptor is what the archive-thumbnail snapshotters resolve through, and walk
     *   thumbnails are rendered once at finish and then cached as PNGs forever — a theme switch
     *   cannot go back and repaint the ones already on disk. Pinning them to light keeps the
     *   Archive uniform instead of striping it by whichever theme was on that day.
     * - It is also what the offline downloader reads, and since [darkenMapStyle] changes no URL,
     *   the resource set is identical either way — there is nothing for a dark variant to add.
     */
    private fun publish(rawStyleJson: String) {
        pinnedRawJson.value = rawStyleJson
        val localized = localizeMapStyle(rawStyleJson, labelLanguage.value)
        _baseStyle.value = BaseStyle.Json(if (darkTheme.value) darkenMapStyle(localized) else localized)
        pinnedStyleInterceptor.setPinnedStyle(localized)
    }

    /** Independent health probe for the tile host — needed because once the style is pinned (or
     * served from the bundled fallback), MapLibre loads it locally and instantly and reports
     * success regardless of whether the network is up, and maplibre-compose exposes no per-tile
     * signal at all to app code. Everything that can actually go wrong with the basemap therefore
     * happens with no error anywhere: the screen just stays the colour of the `background` layer.
     * Reuses the small `style.json` fetch rather than a dedicated endpoint — same host, cheap
     * payload, and it never writes to the pinned file (this is read-only probing, not a refresh).
     *
     * Returns three states because the user-visible consequence differs. [TileHostStatus.Slow] is
     * the case reported from a Russian mobile network on 2026-09-08: the map does load, it just
     * takes minutes, and telling that user "no connection" is simply false — while telling them
     * nothing (what the old boolean did whenever the probe squeaked through) leaves a white screen
     * unexplained. A request still in flight at [TILE_HOST_PROBE_TIMEOUT] counts as slow, not dead:
     * a link that hasn't failed yet is one that may still deliver, and a genuinely refused or
     * unroutable host fails long before that on both platforms. */
    suspend fun probeTileHost(): TileHostStatus {
        val started = TimeSource.Monotonic.markNow()
        val result = withTimeoutOrNull(TILE_HOST_PROBE_TIMEOUT) {
            runCatching { httpTextFetcher.fetchText(OPEN_FREE_MAP_STYLE_URL) }
        }
        return when {
            result == null -> TileHostStatus.Slow
            result.isFailure -> TileHostStatus.Unreachable
            started.elapsedNow() >= TILE_HOST_SLOW_AFTER -> TileHostStatus.Slow
            else -> TileHostStatus.Reachable
        }
    }
}

/** What [MapStyleCacheRepository.probeTileHost] found. [Slow] and [Unreachable] carry different
 * banners (`ui/map/TileHostWatch.kt`): one says the map will arrive eventually, the other says it
 * won't arrive at all. */
enum class TileHostStatus { Reachable, Slow, Unreachable }
