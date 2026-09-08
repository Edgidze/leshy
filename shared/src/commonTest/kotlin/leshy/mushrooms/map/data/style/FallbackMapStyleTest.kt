package leshy.mushrooms.map.data.style

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun parse(dark: Boolean): JsonObject =
    Json.parseToJsonElement(fallbackMapStyle(dark)) as JsonObject

class FallbackMapStyleTest {
    /** Единственное настоящее требование к этому стилю: он не должен ссылаться НИ НА ЧТО снаружи.
     * Источник, спрайт или шрифты в нём — это URL, который однажды перестанет отвечать, и тогда
     * запасной стиль перестанет грузиться ровно в той ситуации, ради которой заведён. */
    @Test
    fun referencesNothingRemote() {
        listOf(true, false).forEach { dark ->
            val root = parse(dark)
            assertEquals(emptyMap(), (root["sources"] as JsonObject).toMap(), "sources, dark=$dark")
            assertNull(root["sprite"], "sprite, dark=$dark")
            assertNull(root["glyphs"], "glyphs, dark=$dark")
            assertTrue("http" !in fallbackMapStyle(dark), "любой http(s)-адрес, dark=$dark")
        }
    }

    /** Один слой фона, и его id — `background`: по этому id тёмная палитра
     * ([darkenMapStyle]) и находит, чем его перекрасить. */
    @Test
    fun isASingleBackgroundLayer() {
        listOf(true, false).forEach { dark ->
            val layers = parse(dark)["layers"] as JsonArray
            assertEquals(1, layers.size, "число слоёв, dark=$dark")
            assertEquals("background", layers[0].jsonObject["id"]!!.jsonPrimitive.content, "id, dark=$dark")
            assertEquals("background", layers[0].jsonObject["type"]!!.jsonPrimitive.content, "type, dark=$dark")
        }
    }

    /** Тёмный вариант получается тем же перекрасом, что и настоящая карта, — значит он и правда
     * тёмный, а не копия светлого (что случилось бы, потеряй таблица палитры слой `background`). */
    @Test
    fun darkGroundDiffersFromLight() {
        fun backgroundColor(dark: Boolean): String =
            ((parse(dark)["layers"] as JsonArray)[0].jsonObject["paint"] as JsonObject)["background-color"]!!
                .jsonPrimitive.content

        assertNotEquals(backgroundColor(dark = false), backgroundColor(dark = true))
    }
}
