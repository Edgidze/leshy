package leshy.mushrooms.map.data.style

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** TileJSON keys worth carrying into the style. `minzoom`/`maxzoom` are NOT optional in practice:
 * a source with inline `tiles` and no zoom range defaults to 0..22, and the live map would then ask
 * for z15+ tiles that OpenFreeMap's planet source (maxzoom 14) doesn't have. */
private val CARRIED_TILEJSON_KEYS = listOf("tiles", "minzoom", "maxzoom", "bounds", "scheme", "attribution")

/**
 * Replaces every `"url": "<TileJSON>"` source in a style with the tile URL template that TileJSON
 * currently resolves to, so the style pins the ACTUAL tile URLs rather than a pointer to whatever
 * they are today.
 *
 * **This is what makes pinning work at all.** OpenFreeMap's `style.json` doesn't name any tile URL —
 * it names `https://tiles.openfreemap.org/planet`, and only THAT document carries the versioned
 * planet-snapshot timestamp (`.../planet/<timestamp>/{z}/{x}/{y}.pbf`). It's served with
 * `Cache-Control: max-age=86400`, so any online day after OpenFreeMap rebuilds the planet, MapLibre
 * re-fetches it and stores the new template under the same URL key — silently orphaning every tile
 * of every already-downloaded offline pack, which is stored under the old template. Pinning
 * `style.json` alone never protected against this: `style.json` itself stays byte-identical across
 * rotations, so it also made "Обновить данные карты" report "nothing changed" while the regions
 * were in fact already dead. See `ui/map/CLAUDE.md`, "Дрейф жил в TileJSON, а не в style.json".
 *
 * Per-source best effort: a source whose TileJSON can't be fetched or parsed is left exactly as it
 * was (a style that still resolves late beats no map at all).
 */
suspend fun freezeStyleTileSources(styleJson: String, fetchText: suspend (String) -> String): String {
    val root = runCatching { Json.parseToJsonElement(styleJson) as? JsonObject }.getOrNull() ?: return styleJson
    val sources = root["sources"] as? JsonObject ?: return styleJson
    if (!styleHasUnfrozenTileSources(styleJson)) return styleJson

    val frozen = sources.mapValues { (_, source) -> freezeSource(source, fetchText) }
    // Byte-identical return when nothing resolved (every fetch failed, e.g. offline): callers
    // compare this against what's already pinned to decide whether to rewrite the file, and a
    // re-serialized-but-unchanged style would read as a change.
    if (frozen == sources.toMap()) return styleJson
    return JsonObject(root + ("sources" to JsonObject(frozen))).toString()
}

/** Whether [styleJson] still defers any source to a TileJSON document — i.e. whether it's an
 * already-pinned copy from before tile URLs were frozen, and needs a one-off migration. */
fun styleHasUnfrozenTileSources(styleJson: String): Boolean {
    val root = runCatching { Json.parseToJsonElement(styleJson) as? JsonObject }.getOrNull() ?: return false
    val sources = root["sources"] as? JsonObject ?: return false
    return sources.values.any { tileJsonUrlOf(it) != null }
}

private suspend fun freezeSource(source: JsonElement, fetchText: suspend (String) -> String): JsonElement {
    val obj = source as? JsonObject ?: return source
    val tileJsonUrl = tileJsonUrlOf(obj) ?: return source
    val tileJson = runCatching { Json.parseToJsonElement(fetchText(tileJsonUrl)) as? JsonObject }
        .getOrNull() ?: return source
    // No tile template resolved means this fetch answered with something unusable — keep the
    // indirection rather than pinning a source that can't serve a single tile.
    if (tileJson["tiles"] !is JsonArray) return source

    val carried = CARRIED_TILEJSON_KEYS.mapNotNull { key -> tileJson[key]?.let { key to it } }
    return JsonObject(obj - "url" + carried)
}

private fun tileJsonUrlOf(source: JsonElement): String? {
    val obj = source as? JsonObject ?: return null
    // Inline `tiles` already win over `url` in the style spec — such a source is nothing to resolve.
    if (obj["tiles"] != null) return null
    val url = (obj["url"] as? JsonPrimitive)?.takeIf { it.isString }?.content ?: return null
    return url.takeIf { it.startsWith("http://") || it.startsWith("https://") }
}
