package leshy.mushrooms.map.data.style

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Слои взяты из настоящего `styles/liberty` без правок — по одному на каждую форму, которая
 * встречается в нём среди цветовых свойств: простая заливка, цвет внутри `interpolate`, слой с
 * двумя цветовыми свойствами сразу, подпись с обводкой, и слой, которого в таблице нет вовсе. */
private val STYLE = """
{
  "version": 8,
  "sprite": "https://tiles.openfreemap.org/sprites/ofm_f384/ofm",
  "glyphs": "https://tiles.openfreemap.org/fonts/{fontstack}/{range}.pbf",
  "sources": { "openmaptiles": { "type": "vector", "tiles": ["https://tiles.openfreemap.org/planet/20250830_001001_pt/{z}/{x}/{y}.pbf"] } },
  "layers": [
    { "id": "background", "type": "background", "paint": { "background-color": "#f8f4f0" } },
    { "id": "landcover_wood", "type": "fill", "source-layer": "landcover",
      "paint": { "fill-antialias": false, "fill-color": "hsla(98,61%,72%,0.7)", "fill-opacity": 0.4 } },
    { "id": "landuse_residential", "type": "fill", "source-layer": "landuse",
      "paint": { "fill-color": ["interpolate", ["linear"], ["zoom"], 9, "hsla(0,3%,85%,0.84)", 12, "hsla(35,57%,88%,0.49)"] } },
    { "id": "park", "type": "fill", "source-layer": "park",
      "paint": { "fill-color": "#d8e8c8", "fill-opacity": 0.7, "fill-outline-color": "rgba(95, 208, 100, 1)" } },
    { "id": "label_city", "type": "symbol", "source-layer": "place",
      "paint": { "text-color": "#000", "text-halo-color": "#fff", "text-halo-width": 1 } },
    { "id": "some_future_layer", "type": "fill", "source-layer": "landuse",
      "paint": { "fill-color": "#eeeeee" } },
    { "id": "natural_earth", "type": "raster", "source": "ne2_shaded",
      "paint": { "raster-opacity": ["interpolate", ["exponential", 1.5], ["zoom"], 0, 0.6, 6, 0.1] } },
    { "id": "landcover_wetland", "type": "fill", "source-layer": "landcover", "minzoom": 12,
      "paint": { "fill-antialias": true, "fill-opacity": 0.8, "fill-pattern": "wetland_bg_11", "fill-translate-anchor": "map" } },
    { "id": "poi_r1", "type": "symbol", "source-layer": "poi",
      "layout": { "icon-image": "restaurant_11" }, "paint": { "text-color": "#666", "text-halo-color": "#ffffff" } },
    { "id": "road_shield_us", "type": "symbol", "source-layer": "transportation_name",
      "layout": { "icon-image": ["concat", ["get", "network"], "_", ["get", "ref_length"]] } }
  ]
}
""".trimIndent()

private fun paintOf(styleJson: String, layerId: String): JsonObject {
    val layers = (Json.parseToJsonElement(styleJson) as JsonObject)["layers"] as JsonArray
    val layer = layers.map { it as JsonObject }.first { it.getValue("id").jsonPrimitive.content == layerId }
    return layer["paint"] as JsonObject
}

/** `rgba(r, g, b, a)` — единственная форма, в которой [darkenMapStyle] пишет цвет. */
private fun channels(raw: String): Triple<Int, Int, Int> {
    val parts = raw.removePrefix("rgba(").removeSuffix(")").split(",").map { it.trim() }
    return Triple(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
}

private fun alphaOf(raw: String): Double =
    raw.removePrefix("rgba(").removeSuffix(")").split(",")[3].trim().toDouble()

private fun isDark(raw: String): Boolean {
    val (r, g, b) = channels(raw)
    return (r + g + b) / 3 < 96
}

class MapStyleDarkenerTest {

    /** Главный инвариант всей затеи: тёмная тема — это ТОЛЬКО цвета. Стоит трансформации задеть
     * `sources`/`sprite`/`glyphs`, и тайлы с иконками у двух тем разойдутся — а значит скачанный
     * офлайн-участок перестанет обслуживать одну из них. */
    @Test
    fun neverTouchesAnyUrlBearingPartOfTheStyle() {
        val darkened = Json.parseToJsonElement(darkenMapStyle(STYLE)) as JsonObject
        val original = Json.parseToJsonElement(STYLE) as JsonObject

        assertEquals(original["sources"], darkened["sources"])
        assertEquals(original["sprite"], darkened["sprite"])
        assertEquals(original["glyphs"], darkened["glyphs"])
    }

    @Test
    fun repaintsEveryColourPropertyDark() {
        val darkened = darkenMapStyle(STYLE)

        assertTrue(isDark(paintOf(darkened, "background")["background-color"]!!.jsonPrimitive.content))
        assertTrue(isDark(paintOf(darkened, "landcover_wood")["fill-color"]!!.jsonPrimitive.content))
        assertTrue(isDark(paintOf(darkened, "park")["fill-color"]!!.jsonPrimitive.content))
        assertTrue(isDark(paintOf(darkened, "park")["fill-outline-color"]!!.jsonPrimitive.content))
    }

    /** Не-цветовые свойства красить нечем и незачем — а `fill-opacity: 0.4` в разобранном виде
     * легко превратить в `0.4` другого типа или потерять вовсе. */
    @Test
    fun leavesNonColourPaintPropertiesExactlyAsTheyWere() {
        val darkened = paintOf(darkenMapStyle(STYLE), "landcover_wood")

        assertEquals("0.4", darkened["fill-opacity"]!!.jsonPrimitive.content)
        assertEquals("false", darkened["fill-antialias"]!!.jsonPrimitive.content)
    }

    /** Альфа несёт замысел стиля (полупрозрачный лес поверх земли), а не оттенок, поэтому берётся
     * из исходного цвета, а не из таблицы. */
    @Test
    fun keepsTheOriginalAlphaOfEveryColourItReplaces() {
        val darkened = darkenMapStyle(STYLE)

        assertEquals(0.7, alphaOf(paintOf(darkened, "landcover_wood")["fill-color"]!!.jsonPrimitive.content))
        assertEquals(1.0, alphaOf(paintOf(darkened, "background")["background-color"]!!.jsonPrimitive.content))
    }

    /** Цвет внутри `interpolate` — три слоя liberty заданы так. Подменяться должны литералы, а
     * зумы-остановки и сама форма выражения остаться на месте, иначе ramp сломается. */
    @Test
    fun replacesColoursInsideInterpolateExpressionsKeepingTheStops() {
        val fill = paintOf(darkenMapStyle(STYLE), "landuse_residential")["fill-color"] as JsonArray

        assertEquals("interpolate", fill[0].jsonPrimitive.content)
        assertEquals("9", fill[3].jsonPrimitive.content)
        assertEquals("12", fill[5].jsonPrimitive.content)
        assertTrue(isDark(fill[4].jsonPrimitive.content), fill.toString())
        assertTrue(isDark(fill[6].jsonPrimitive.content), fill.toString())
        // Ramp прозрачности жилой застройки задан альфами остановок — они обязаны пережить замену.
        assertEquals(0.84, alphaOf(fill[4].jsonPrimitive.content))
        assertEquals(0.49, alphaOf(fill[6].jsonPrimitive.content))
    }

    /** Слой, которого не было в liberty, когда составлялась таблица: запасная формула обязана
     * увести его в тёмное, а не оставить светлым пятном. */
    @Test
    fun fallsBackToLightnessInversionForALayerMissingFromTheTable() {
        val fill = paintOf(darkenMapStyle(STYLE), "some_future_layer")["fill-color"]!!.jsonPrimitive.content

        assertTrue(isDark(fill), fill)
    }

    /** Болота рисуются картинкой `wetland_bg_11` (средняя яркость 211 из 255) и не имеют ни одного
     * свойства цвета — первая версия трансформера проходила мимо них, и на тёмной карте они
     * остались слепящими пятнами. `fill-pattern` приоритетнее `fill-color`, поэтому проверяется
     * именно то, что он СНЯТ, а не просто дополнен цветом. */
    @Test
    fun replacesTheSpritePatternOfWetlandsWithAPlainFill() {
        val paint = paintOf(darkenMapStyle(STYLE), "landcover_wetland")

        assertEquals(null, paint["fill-pattern"])
        val fill = paint["fill-color"]!!.jsonPrimitive.content
        assertTrue(isDark(fill), fill)
        // Слегка светлее леса и с коричневым уклоном — красный канал выше синего.
        val (r, _, b) = channels(fill)
        assertTrue(r > b, fill)
        // `fill-opacity` — часть замысла слоя, снятие паттерна не должно её задеть.
        assertEquals("0.8", paint["fill-opacity"]!!.jsonPrimitive.content)
    }

    /** Растровая отмывка рельефа — единственный слой вообще без свойств цвета, гасится числами. */
    @Test
    fun dimsTheRasterReliefWhichHasNoColourPropertyAtAll() {
        val paint = paintOf(darkenMapStyle(STYLE), "natural_earth")

        assertEquals(0.25, paint["raster-brightness-max"]!!.jsonPrimitive.content.toDouble())
        assertEquals(-0.4, paint["raster-saturation"]!!.jsonPrimitive.content.toDouble())
        // Собственный ramp прозрачности слоя не трогается.
        assertTrue((paint["raster-opacity"] as JsonArray).size > 1)
    }

    /** Значки POI — белые «таблетки», а перекрасить их нечем: в спрайте OpenFreeMap нет ни одного
     * SDF-значка. Остаётся сбавить непрозрачность. */
    @Test
    fun dimsLightSpriteIconsInsteadOfRecolouringThemBecauseNoneAreSdf() {
        val paint = paintOf(darkenMapStyle(STYLE), "poi_r1")

        assertEquals(0.55, paint["icon-opacity"]!!.jsonPrimitive.content.toDouble())
        // Сам значок остаётся тем же — меняется только его подача.
        assertTrue(isDark(paint["text-halo-color"]!!.jsonPrimitive.content))
    }

    /** Три слоя дорожных щитов в liberty не имеют блока `paint` ВООБЩЕ — они рисуются одним
     * значком из спрайта и больше ничем. Ранний выход «нет paint — нечего красить» молча оставлял
     * их белые щиты (яркость 221–230 при сплошном заполнении) нетронутыми. */
    @Test
    fun addsPaintToALayerThatHadNoPaintBlockAtAll() {
        val paint = paintOf(darkenMapStyle(STYLE), "road_shield_us")

        assertEquals(0.55, paint["icon-opacity"]!!.jsonPrimitive.content.toDouble())
    }

    @Test
    fun returnsTheInputUntouchedWhenTheStyleCannotBeParsed() {
        assertEquals("not json at all", darkenMapStyle("not json at all"))
    }
}
