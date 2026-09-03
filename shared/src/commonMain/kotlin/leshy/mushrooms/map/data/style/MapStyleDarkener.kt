package leshy.mushrooms.map.data.style

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Перекрашивает запиненный светлый стиль OpenFreeMap («liberty») в тёмный, НЕ трогая ничего,
 * кроме цветов: `sources`, `sprite`, `glyphs`, фильтры и `layout` остаются байт-в-байт. Это
 * ровно то же свойство, на котором держится [localizeMapStyle], и по той же причине оно здесь
 * критично: ни один URL не меняется, поэтому тайлы, спрайты и шрифты у светлой и тёмной темы
 * общие — **переключение темы не инвалидирует ни одного скачанного офлайн-участка** и не требует
 * второго запиненного стиля.
 *
 * **Почему таблица, а не формула.** Палитра снята с готового тёмного стиля
 * `tiles.openfreemap.org/styles/dark` (форк openmaptiles/dark-matter) и наложена на структуру
 * liberty. Сам тот стиль подставить вместо liberty нельзя: в нём 47 слоёв против 111, из них
 * общих с liberty всего 5, нет НИ ОДНОГО слоя POI, вчетверо меньше слоёв дорог, нет подписей
 * троп и рек, а его `landcover_wood` ссылается на `wood-pattern`, которого нет в спрайте, на
 * который он же и указывает. Перенос палитры даёт тёмные цвета оттуда и содержимое карты отсюда.
 *
 * Три места, где палитра сознательно отступает от Dark Matter — он рассчитан быть чёрным холстом
 * под data-оверлеи, и там вода, лес и подписи почти сливаются с фоном, что для полевого
 * приложения не годится (контраст к фону, посчитан по WCAG):
 * вода 1.14:1 → 1.48:1, лес 1.06:1 → 1.31:1, подписи 3.4:1 → 5.7–8.2:1.
 *
 * Любая ошибка разбора возвращает [styleJson] нетронутым: светлая карта в тёмной теме — косметика,
 * отсутствие карты — нет.
 */
fun darkenMapStyle(styleJson: String): String =
    runCatching {
        val root = Json.parseToJsonElement(styleJson) as? JsonObject ?: return styleJson
        val layers = root["layers"] as? JsonArray ?: return styleJson
        JsonObject(root + ("layers" to JsonArray(layers.map(::darkenLayer)))).toString()
    }.getOrDefault(styleJson)

private fun darkenLayer(layer: JsonElement): JsonElement {
    val obj = layer as? JsonObject ?: return layer
    val paint = obj["paint"] as? JsonObject ?: return layer
    val overrides = DARK_PAINT_COLORS[(obj["id"] as? JsonPrimitive)?.contentOrNullSafe()]
    val repainted = paint.mapValues { (property, value) ->
        if (!property.endsWith("-color")) value else recolor(value, overrides?.get(property))
    }
    return JsonObject(obj + ("paint" to JsonObject(repainted)))
}

/**
 * [target] `null` — слой, которого не было в liberty на момент составления таблицы (стиль
 * обновляется только по явной кнопке в Настройках, но обновиться может). Тогда работает запасная
 * формула: инверсия светлоты в HSL с сохранением тона. Она заметно грубее подобранной вручную
 * палитры, но не даёт новому слою остаться светлым пятном на тёмной карте.
 */
private fun recolor(value: JsonElement, target: String?): JsonElement = when (value) {
    // Цвет внутри interpolate/step: подменяются строковые литералы, структура выражения и
    // положение остановок сохраняются (в liberty так заданы landuse_residential, road_motorway и
    // обводка building).
    is JsonArray -> JsonArray(value.map { recolor(it, target) })
    is JsonPrimitive -> {
        val source = value.contentOrNullSafe()?.let(::parseCssColor)
        if (source == null) {
            value
        } else {
            // Альфа всегда берётся из исходного цвета: она несёт замысел стиля (полупрозрачный
            // лес, ramp прозрачности у жилой застройки), а не оттенок, и таблица её не дублирует.
            val replacement = target?.let(::parseCssColor)?.copy(alpha = source.alpha) ?: source.inverted()
            JsonPrimitive(replacement.toCssRgba())
        }
    }
    else -> value
}

private fun JsonPrimitive.contentOrNullSafe(): String? = if (isString) content else null

private data class Rgba(val r: Int, val g: Int, val b: Int, val alpha: Double) {
    fun toCssRgba(): String = "rgba($r, $g, $b, $alpha)"

    /** Инверсия светлоты с сохранением тона и насыщенности — запасной путь, см. [recolor]. */
    fun inverted(): Rgba {
        val max = maxOf(r, g, b) / 255.0
        val min = minOf(r, g, b) / 255.0
        val lightness = (max + min) / 2
        val scale = if (lightness <= 0.0) 0.0 else (1 - lightness) / lightness
        // Тон сохраняется тем, что все три канала масштабируются одним множителем.
        return Rgba(
            (r * scale).toInt().coerceIn(0, 255),
            (g * scale).toInt().coerceIn(0, 255),
            (b * scale).toInt().coerceIn(0, 255),
            alpha,
        )
    }
}

private val HEX = Regex("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")
private val RGB = Regex("^rgba?\\(([^)]*)\\)$")
private val HSL = Regex("^hsla?\\(([^)]*)\\)$")

/** Разбирает три записи цвета, которые реально встречаются в стилях OpenFreeMap: `#rgb`/`#rrggbb`,
 * `rgb()`/`rgba()` и `hsl()`/`hsla()`. Всё остальное (в том числе именованные цвета) — `null`,
 * такая строка остаётся нетронутой. */
private fun parseCssColor(raw: String): Rgba? {
    val value = raw.trim()
    HEX.find(value)?.let { match ->
        val hex = match.groupValues[1]
        val full = if (hex.length == 3) hex.map { "$it$it" }.joinToString("") else hex
        return Rgba(
            full.substring(0, 2).toInt(16),
            full.substring(2, 4).toInt(16),
            full.substring(4, 6).toInt(16),
            1.0,
        )
    }
    RGB.find(value)?.let { match ->
        val parts = match.groupValues[1].split(",").map { it.trim() }
        if (parts.size < 3) return null
        return Rgba(
            parts[0].toDoubleOrNull()?.toInt() ?: return null,
            parts[1].toDoubleOrNull()?.toInt() ?: return null,
            parts[2].toDoubleOrNull()?.toInt() ?: return null,
            parts.getOrNull(3)?.toDoubleOrNull() ?: 1.0,
        )
    }
    HSL.find(value)?.let { match ->
        val parts = match.groupValues[1].split(",").map { it.trim().removeSuffix("%") }
        if (parts.size < 3) return null
        val h = parts[0].toDoubleOrNull() ?: return null
        val s = (parts[1].toDoubleOrNull() ?: return null) / 100
        val l = (parts[2].toDoubleOrNull() ?: return null) / 100
        val a = parts.getOrNull(3)?.toDoubleOrNull() ?: 1.0
        val c = (1 - kotlin.math.abs(2 * l - 1)) * s
        val hp = ((h % 360) + 360) % 360 / 60
        val x = c * (1 - kotlin.math.abs(hp % 2 - 1))
        val (r1, g1, b1) = when (hp.toInt()) {
            0 -> Triple(c, x, 0.0)
            1 -> Triple(x, c, 0.0)
            2 -> Triple(0.0, c, x)
            3 -> Triple(0.0, x, c)
            4 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }
        val m = l - c / 2
        return Rgba(
            ((r1 + m) * 255).toInt().coerceIn(0, 255),
            ((g1 + m) * 255).toInt().coerceIn(0, 255),
            ((b1 + m) * 255).toInt().coerceIn(0, 255),
            a,
        )
    }
    return null
}

/**
 * Тёмный цвет для каждого цветового свойства каждого слоя liberty — 123 значения на 103 слоя,
 * ровно столько, сколько их в стиле (проверяется `MapStyleDarkenerTest`). Порядок совпадает с
 * порядком слоёв в самом стиле, снизу вверх по отрисовке.
 */
private val DARK_PAINT_COLORS: Map<String, Map<String, String>> = mapOf(
    "background" to mapOf("background-color" to "#0c0c0c"),
    "park" to mapOf("fill-color" to "#16241a", "fill-outline-color" to "#24382a"),
    "park_outline" to mapOf("line-color" to "#1e2f24"),
    "landuse_residential" to mapOf("fill-color" to "#0d0d0d"),
    "landcover_wood" to mapOf("fill-color" to "hsl(140,22%,38%)"),
    "landcover_grass" to mapOf("fill-color" to "#18231a"),
    "landcover_ice" to mapOf("fill-color" to "#1e2226"),
    "landuse_pitch" to mapOf("fill-color" to "#161a15"),
    "landuse_track" to mapOf("fill-color" to "#161a15"),
    "landuse_cemetery" to mapOf("fill-color" to "#161a15"),
    "landuse_hospital" to mapOf("fill-color" to "#1c1416"),
    "landuse_school" to mapOf("fill-color" to "#1a1a14"),
    "waterway_tunnel" to mapOf("line-color" to "#1d3e5c"),
    "waterway_river" to mapOf("line-color" to "#1d3e5c"),
    "waterway_other" to mapOf("line-color" to "#1d3e5c"),
    "water" to mapOf("fill-color" to "#16324a"),
    "landcover_sand" to mapOf("fill-color" to "#221f16"),
    "aeroway_fill" to mapOf("fill-color" to "#101010"),
    "aeroway_runway" to mapOf("line-color" to "#181818"),
    "aeroway_taxiway" to mapOf("line-color" to "#181818"),
    "tunnel_motorway_link_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_service_track_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_link_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_street_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_secondary_tertiary_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_trunk_primary_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_motorway_casing" to mapOf("line-color" to "#3c3c3c"),
    "tunnel_path_pedestrian" to mapOf("line-color" to "#4a4a4a"),
    "tunnel_motorway_link" to mapOf("line-color" to "#2a2a2a"),
    "tunnel_service_track" to mapOf("line-color" to "#181818"),
    "tunnel_link" to mapOf("line-color" to "#121212"),
    "tunnel_minor" to mapOf("line-color" to "#181818"),
    "tunnel_secondary_tertiary" to mapOf("line-color" to "#121212"),
    "tunnel_trunk_primary" to mapOf("line-color" to "#121212"),
    "tunnel_motorway" to mapOf("line-color" to "#2a2a2a"),
    "tunnel_major_rail" to mapOf("line-color" to "#232323"),
    "tunnel_major_rail_hatching" to mapOf("line-color" to "#0c0c0c"),
    "tunnel_transit_rail" to mapOf("line-color" to "#232323"),
    "tunnel_transit_rail_hatching" to mapOf("line-color" to "#0c0c0c"),
    "road_motorway_link_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_service_track_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_link_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_minor_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_secondary_tertiary_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_trunk_primary_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_motorway_casing" to mapOf("line-color" to "#3c3c3c"),
    "road_path_pedestrian" to mapOf("line-color" to "#4a4a4a"),
    "road_motorway_link" to mapOf("line-color" to "#2a2a2a"),
    "road_service_track" to mapOf("line-color" to "#181818"),
    "road_link" to mapOf("line-color" to "#121212"),
    "road_minor" to mapOf("line-color" to "#181818"),
    "road_secondary_tertiary" to mapOf("line-color" to "#121212"),
    "road_trunk_primary" to mapOf("line-color" to "#121212"),
    "road_motorway" to mapOf("line-color" to "#2a2a2a"),
    "road_major_rail" to mapOf("line-color" to "#232323"),
    "road_major_rail_hatching" to mapOf("line-color" to "#0c0c0c"),
    "road_transit_rail" to mapOf("line-color" to "#232323"),
    "road_transit_rail_hatching" to mapOf("line-color" to "#0c0c0c"),
    "bridge_motorway_link_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_service_track_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_link_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_street_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_path_pedestrian_casing" to mapOf("line-color" to "#333333"),
    "bridge_secondary_tertiary_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_trunk_primary_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_motorway_casing" to mapOf("line-color" to "#3c3c3c"),
    "bridge_path_pedestrian" to mapOf("line-color" to "#4a4a4a"),
    "bridge_motorway_link" to mapOf("line-color" to "#2a2a2a"),
    "bridge_service_track" to mapOf("line-color" to "#181818"),
    "bridge_link" to mapOf("line-color" to "#121212"),
    "bridge_street" to mapOf("line-color" to "#181818"),
    "bridge_secondary_tertiary" to mapOf("line-color" to "#121212"),
    "bridge_trunk_primary" to mapOf("line-color" to "#121212"),
    "bridge_motorway" to mapOf("line-color" to "#2a2a2a"),
    "bridge_major_rail" to mapOf("line-color" to "#232323"),
    "bridge_major_rail_hatching" to mapOf("line-color" to "#0c0c0c"),
    "bridge_transit_rail" to mapOf("line-color" to "#232323"),
    "bridge_transit_rail_hatching" to mapOf("line-color" to "#0c0c0c"),
    "building" to mapOf("fill-color" to "#181818", "fill-outline-color" to "#2c2c32"),
    "building-3d" to mapOf("fill-extrusion-color" to "#181818"),
    "boundary_3" to mapOf("line-color" to "#363636"),
    "boundary_2" to mapOf("line-color" to "#3b3b3b"),
    "boundary_disputed" to mapOf("line-color" to "#3b3b3b"),
    "waterway_line_label" to mapOf("text-color" to "#5f93c4", "text-halo-color" to "rgba(0,0,0,0.7)"),
    "water_name_point_label" to mapOf("text-color" to "#5f93c4", "text-halo-color" to "rgba(0,0,0,0.7)"),
    "water_name_line_label" to mapOf("text-color" to "#5f93c4", "text-halo-color" to "rgba(0,0,0,0.7)"),
    "poi_r20" to mapOf("text-color" to "#8a8a8a", "text-halo-color" to "#000000"),
    "poi_r7" to mapOf("text-color" to "#8a8a8a", "text-halo-color" to "#000000"),
    "poi_r1" to mapOf("text-color" to "#8a8a8a", "text-halo-color" to "#000000"),
    "poi_transit" to mapOf("text-color" to "#5f93c4", "text-halo-color" to "#000000"),
    "highway-name-path" to mapOf("text-color" to "#8f8f8f", "text-halo-color" to "#0c0c0c"),
    "highway-name-minor" to mapOf("text-color" to "#8f8f8f"),
    "highway-name-major" to mapOf("text-color" to "#8f8f8f"),
    "airport" to mapOf("text-color" to "#8a8a8a", "text-halo-color" to "#000000"),
    "label_other" to mapOf("text-color" to "#8a8a8a", "text-halo-color" to "#000000"),
    "label_village" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
    "label_town" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
    "label_state" to mapOf("text-color" to "#8a8a8a", "text-halo-color" to "#000000"),
    "label_city" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
    "label_city_capital" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
    "label_country_3" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
    "label_country_2" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
    "label_country_1" to mapOf("text-color" to "#a8a8a8", "text-halo-color" to "#000000"),
)
