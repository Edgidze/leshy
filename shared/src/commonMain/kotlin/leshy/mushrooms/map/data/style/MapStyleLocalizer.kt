package leshy.mushrooms.map.data.style

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray

/**
 * `name:*` translations that OpenFreeMap's planet tiles actually carry, intersected with the
 * [AppLanguage] set. The tiles themselves ship ~80 of them (checked against the `place`/`poi`/
 * `transportation_name` field lists in `https://tiles.openfreemap.org/planet`); the four interface
 * languages MISSING there are `ky`, `tg`, `tk`, `uz` — OpenMapTiles/planetiler simply doesn't
 * include those OSM name tags in its build, so nothing app-side can conjure them.
 */
private val TILE_NAME_LANGUAGES = setOf(
    "az", "be", "bg", "cs", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "hy", "it", "ja",
    "ka", "kk", "ko", "lt", "lv", "pl", "ro", "ru", "sk", "sl", "sr", "sv", "tr", "uk",
)

/**
 * Second choice for the interface languages absent from the tiles, tried before the generic
 * latin/local chain. Only for the two Cyrillic-script ones: a Kyrgyz or Tajik speaker reads
 * `name:ru` far more comfortably than a Latin transliteration of the local name, and OSM coverage
 * of `name:ru` is good across both countries. Latin-script Uzbek and Turkmen need no such
 * detour — `name:latin` is already in their own script.
 */
private val TILE_NAME_FALLBACKS = mapOf(
    AppLanguage.KY to "ru",
    AppLanguage.TG to "ru",
)

/** What the upstream style uses between the two lines of a point label; line labels (roads,
 * waterways) use a space instead. Detected per layer from the expression being replaced rather
 * than hardcoded per layer id, so a restyle upstream can't silently turn road labels two-line. */
private const val DEFAULT_SEPARATOR = "\n"

/**
 * Rewrites every name-based `text-field` in an OpenFreeMap style so labels come out in [language]
 * instead of the upstream default (`name:latin` + `name:nonlatin`).
 *
 * This is purely a client-side render change: the vector tiles already carry every language at
 * once (see [TILE_NAME_LANGUAGES]), so no tile URL, no source and no resource the offline
 * downloader ever asks for is affected — **switching the interface language never invalidates a
 * downloaded region**, and needs no re-download. See `ui/map/CLAUDE.md`, "Язык подписей карты".
 *
 * Any parse failure returns [styleJson] untouched: an unreadable style would mean no map at all,
 * while untranslated labels are merely a cosmetic regression.
 */
fun localizeMapStyle(styleJson: String, language: AppLanguage): String =
    runCatching {
        val root = Json.parseToJsonElement(styleJson) as? JsonObject ?: return styleJson
        val layers = root["layers"] as? JsonArray ?: return styleJson
        val localized = JsonArray(layers.map { layer -> localizeLayer(layer, language) })
        JsonObject(root + ("layers" to localized)).toString()
    }.getOrDefault(styleJson)

private fun localizeLayer(layer: JsonElement, language: AppLanguage): JsonElement {
    val obj = layer as? JsonObject ?: return layer
    val layout = obj["layout"] as? JsonObject ?: return layer
    val textField = layout["text-field"] ?: return layer
    // Highway/route shields label by `ref` ("E60", "M4"), not by name — those stay as they are.
    if (!mentionsName(textField)) return layer
    val replacement = localizedTextField(language, findSeparator(textField) ?: DEFAULT_SEPARATOR)
    return JsonObject(obj + ("layout" to JsonObject(layout + ("text-field" to replacement))))
}

/**
 * `["case", [primary == local], primary, ["concat", primary, sep, local]]` — the localized name on
 * the first line, the on-the-ground local name on the second, collapsed to one line whenever they'd
 * be identical (which is the common case inside the country whose language is selected: there
 * `name:ru`/`name:kk`/... simply equals `name`).
 *
 * Every branch is a `coalesce` ending in `""`, so both sides of the `==` are strings even for
 * objects carrying no name tag at all — comparing two possibly-null expressions is what would
 * otherwise be on shaky ground in MapLibre's expression evaluator.
 */
private fun localizedTextField(language: AppLanguage, separator: String): JsonElement {
    val primary = coalesceOf(primaryFields(language))
    val local = coalesceOf(listOf("name"))
    return buildJsonArray {
        add(JsonPrimitive("case"))
        add(buildJsonArray { add(JsonPrimitive("==")); add(primary); add(local) })
        add(primary)
        add(
            buildJsonArray {
                add(JsonPrimitive("concat"))
                add(primary)
                add(JsonPrimitive(separator))
                add(local)
            },
        )
    }
}

/** Interface language first, then its documented stand-in, then a latin transliteration, then
 * whatever the object is called locally — most rural objects (forest tracks, hamlets) carry only
 * the last one in OSM, in any country. */
private fun primaryFields(language: AppLanguage): List<String> = buildList {
    if (language.code in TILE_NAME_LANGUAGES) add("name:${language.code}")
    TILE_NAME_FALLBACKS[language]?.let { add("name:$it") }
    add("name:latin")
    add("name")
}

private fun coalesceOf(fields: List<String>): JsonArray = buildJsonArray {
    add(JsonPrimitive("coalesce"))
    fields.forEach { field ->
        add(buildJsonArray { add(JsonPrimitive("get")); add(JsonPrimitive(field)) })
    }
    add(JsonPrimitive(""))
}

private fun mentionsName(expression: JsonElement): Boolean = when (expression) {
    is JsonArray -> expression.any { mentionsName(it) }
    is JsonPrimitive -> expression.isString && expression.content.startsWith("name")
    else -> false
}

private fun findSeparator(expression: JsonElement): String? = when (expression) {
    is JsonArray -> expression.firstNotNullOfOrNull { findSeparator(it) }
    is JsonPrimitive -> expression.content.takeIf { expression.isString && (it == " " || it == "\n") }
    else -> null
}
