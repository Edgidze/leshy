package leshy.mushrooms.map.data.style

import leshy.mushrooms.map.domain.model.AppLanguage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Trimmed to the four layer shapes OpenFreeMap's "liberty" actually has: a two-line point label,
 * a single-line (space-joined) line label, a `ref`-based highway shield, and a layer with no text
 * at all. */
private val STYLE = """
{
  "version": 8,
  "glyphs": "https://tiles.openfreemap.org/fonts/{fontstack}/{range}.pbf",
  "sources": { "openmaptiles": { "type": "vector", "url": "https://tiles.openfreemap.org/planet" } },
  "layers": [
    { "id": "water", "type": "fill", "source": "openmaptiles", "source-layer": "water" },
    { "id": "label_city", "type": "symbol", "source-layer": "place",
      "layout": { "text-size": 12, "text-field":
        ["case", ["has", "name:nonlatin"],
          ["concat", ["get", "name:latin"], "\n", ["get", "name:nonlatin"]],
          ["coalesce", ["get", "name_en"], ["get", "name"]]] } },
    { "id": "highway-name-major", "type": "symbol", "source-layer": "transportation_name",
      "layout": { "text-field":
        ["case", ["has", "name:nonlatin"],
          ["concat", ["get", "name:latin"], " ", ["get", "name:nonlatin"]],
          ["coalesce", ["get", "name_en"], ["get", "name"]]] } },
    { "id": "highway-shield-non-us", "type": "symbol", "source-layer": "transportation_name",
      "layout": { "text-field": ["to-string", ["get", "ref"]] } }
  ]
}
""".trimIndent()

private fun textFieldOf(styleJson: String, layerId: String): String {
    val layers = (Json.parseToJsonElement(styleJson) as JsonObject)["layers"] as JsonArray
    val layer = layers.map { it as JsonObject }.first { it.getValue("id").jsonPrimitive.content == layerId }
    return ((layer["layout"] as JsonObject)["text-field"] as JsonArray).toString()
}

class MapStyleLocalizerTest {

    @Test
    fun putsInterfaceLanguageFirstAndKeepsTheLocalNameAsSecondLine() {
        val textField = textFieldOf(localizeMapStyle(STYLE, AppLanguage.RU), "label_city")

        assertTrue(textField.contains("""["get","name:ru"]"""), textField)
        // Falls through to a transliteration, then to the local name, for the (very common) rural
        // object that carries no translation at all.
        assertTrue(textField.contains("""["get","name:latin"]"""), textField)
        assertTrue(textField.startsWith("""["case","""), textField)
        assertTrue(textField.contains("""concat"""), textField)
    }

    @Test
    fun keepsEachLayersOwnSeparator() {
        val localized = localizeMapStyle(STYLE, AppLanguage.KA)

        assertTrue(textFieldOf(localized, "label_city").contains(",\"\\n\","))
        assertTrue(textFieldOf(localized, "highway-name-major").contains(",\" \","))
    }

    @Test
    fun leavesRefBasedShieldsAlone() {
        val localized = localizeMapStyle(STYLE, AppLanguage.RU)

        assertEquals("""["to-string",["get","ref"]]""", textFieldOf(localized, "highway-shield-non-us"))
    }

    @Test
    fun fallsBackWithoutInventingAMissingTileLanguage() {
        // Uzbek has no `name:uz` in OpenMapTiles' build — latin (its own script) is the next best.
        val uzbek = textFieldOf(localizeMapStyle(STYLE, AppLanguage.UZ), "label_city")
        assertFalse(uzbek.contains("name:uz"), uzbek)
        assertTrue(uzbek.contains("""["get","name:latin"]"""), uzbek)

        // Kyrgyz has none either, but reads Cyrillic — Russian is the documented stand-in.
        val kyrgyz = textFieldOf(localizeMapStyle(STYLE, AppLanguage.KY), "label_city")
        assertFalse(kyrgyz.contains("name:ky"), kyrgyz)
        assertTrue(kyrgyz.contains("""["get","name:ru"]"""), kyrgyz)
    }

    @Test
    fun preservesEverythingOutsideTheLayerList() {
        val localized = Json.parseToJsonElement(localizeMapStyle(STYLE, AppLanguage.HY)) as JsonObject

        assertEquals((Json.parseToJsonElement(STYLE) as JsonObject)["sources"], localized["sources"])
        assertEquals((Json.parseToJsonElement(STYLE) as JsonObject)["glyphs"], localized["glyphs"])
        assertEquals(4, (localized["layers"] as JsonArray).size)
    }

    @Test
    fun returnsTheInputUnchangedWhenItIsNotAStyle() {
        assertEquals("not json at all", localizeMapStyle("not json at all", AppLanguage.RU))
    }
}
