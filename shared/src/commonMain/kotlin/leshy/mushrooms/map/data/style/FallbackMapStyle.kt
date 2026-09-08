package leshy.mushrooms.map.data.style

/**
 * The one map style the app carries inside itself: a single `background` layer and **no sources, no
 * `glyphs`, no `sprite`** — nothing in it points at a server, so there is nothing in it that can rot.
 *
 * **What it is for.** Every overlay the app draws on a live map — the walk's track, find markers,
 * place markers, the location dot — is a MapLibre layer, and a layer can only be attached to a
 * style that loaded. Until this existed, a first launch without network (nothing pinned yet, and
 * [leshy.mushrooms.map.data.repository.MapStyleCacheRepository]'s bootstrap fetch failing) left the
 * map with no style at all, so the user lost not just the basemap but their own track and finds:
 * the Record screen and a walk's detail map came up completely empty, while the same walk's Archive
 * thumbnail still showed its track and finds — those are drawn with a plain `Canvas` over the
 * snapshot and never needed a style. Reported from a device in airplane mode, 2026-09-08.
 *
 * **Why not bundle OpenFreeMap's own `style.json` instead.** What gets pinned is the style with tile
 * URLs already frozen into it ([freezeStyleTileSources]), and those carry OpenFreeMap's versioned
 * planet-snapshot timestamp. A copy of that shipped in the app would name a snapshot directory the
 * server eventually deletes — a bundled style that serves 404s for every tile, and one more thing
 * that silently expires. This style names nothing, so it expires never.
 *
 * **Why one background layer is enough.** Not one of the app's own layers uses `text-field`, so no
 * `glyphs` are needed; every marker icon is baked locally from a Compose `Painter`
 * (`MushroomMarkerIcon.kt`, `PlaceMarkerIcon.kt`), so no `sprite` is needed; and none of them anchor
 * themselves relative to an upstream layer id, so there is nothing for them to look for in the style
 * they land on. What the user gets offline is a plain field of the right colour with all of their own
 * data on it — not a map, but not an empty screen either, and visibly still recording.
 *
 * This is deliberately never handed to [leshy.mushrooms.map.data.platform.PinnedStyleInterceptor]:
 * the native offline downloader and both snapshotters resolve the pinned style URL through it, and a
 * source-less style would make an offline download "succeed" having fetched nothing.
 */
fun fallbackMapStyle(dark: Boolean): String = if (dark) DARK else LIGHT

// Same colour liberty's own `background` layer paints, so the moment a real style arrives (or the
// pinned one loads on the next launch) the ground the user was looking at doesn't jump to another
// shade — the tiles just appear on top of it.
private const val LIGHT = """{"version":8,"name":"Leshy fallback","sources":{},"layers":""" +
    """[{"id":"background","type":"background","paint":{"background-color":"#f8f4f0"}}]}"""

// Through the darkener rather than a second hardcoded colour: the layer id is `background`, exactly
// as in liberty, so this resolves to the same dark ground the real style gets in dark theme, and it
// keeps following that table if the palette is ever retuned.
private val DARK = darkenMapStyle(LIGHT)
