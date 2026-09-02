package leshy.mushrooms.map.data.style

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Shaped like OpenFreeMap's "liberty": one vector source deferred to a TileJSON document, one
 * raster source that already spells its tiles out inline. */
private val STYLE = """
{
  "version": 8,
  "sources": {
    "openmaptiles": { "type": "vector", "url": "https://tiles.openfreemap.org/planet" },
    "ne2_shaded": { "type": "raster", "maxzoom": 6, "tileSize": 256,
      "tiles": ["https://tiles.openfreemap.org/natural_earth/ne2sr/{z}/{x}/{y}.png"] }
  },
  "layers": []
}
""".trimIndent()

private val TILE_JSON = """
{
  "tilejson": "3.0.0",
  "tiles": ["https://tiles.openfreemap.org/planet/20260830_080001_pt/{z}/{x}/{y}.pbf"],
  "minzoom": 0, "maxzoom": 14,
  "bounds": [-180.0, -85.05113, 180.0, 85.05113],
  "attribution": "OpenFreeMap",
  "vector_layers": [{ "id": "place" }]
}
""".trimIndent()

private fun sourceOf(styleJson: String, id: String): JsonObject =
    ((Json.parseToJsonElement(styleJson) as JsonObject)["sources"] as JsonObject)[id] as JsonObject

class MapStyleSourceFreezerTest {

    @Test
    fun inlinesTheResolvedTileTemplateAndItsZoomRange() = runBlocking {
        val frozen = freezeStyleTileSources(STYLE) { TILE_JSON }

        val source = sourceOf(frozen, "openmaptiles")
        assertEquals(
            """["https://tiles.openfreemap.org/planet/20260830_080001_pt/{z}/{x}/{y}.pbf"]""",
            source.getValue("tiles").toString(),
        )
        // Without an explicit zoom range an inline-tiles source defaults to 0..22 and the map would
        // ask for z15+ tiles this source has never had.
        assertEquals("14", source.getValue("maxzoom").toString())
        assertEquals("0", source.getValue("minzoom").toString())
        assertEquals("\"vector\"", source.getValue("type").toString())
        // The indirection itself has to go, or MapLibre keeps resolving it and drifting.
        assertNull(source["url"])
    }

    @Test
    fun leavesAlreadyInlinedSourcesAloneAndNeverFetchesForThem() = runBlocking {
        var fetches = 0
        val frozen = freezeStyleTileSources(STYLE) { fetches++; TILE_JSON }

        assertEquals(sourceOf(STYLE, "ne2_shaded"), sourceOf(frozen, "ne2_shaded"))
        assertEquals(1, fetches)
    }

    @Test
    fun keepsTheStyleUsableWhenTheTileJsonCannotBeResolved() = runBlocking {
        assertEquals(STYLE, freezeStyleTileSources(STYLE) { error("offline") })
        assertEquals(STYLE, freezeStyleTileSources(STYLE) { "not json" })
        // A response with no tile template is no better than no response.
        assertEquals(STYLE, freezeStyleTileSources(STYLE) { """{"tilejson":"3.0.0"}""" })
    }

    @Test
    fun detectsWhetherAPinnedCopyStillNeedsFreezing() = runBlocking {
        assertTrue(styleHasUnfrozenTileSources(STYLE))
        assertFalse(styleHasUnfrozenTileSources(freezeStyleTileSources(STYLE) { TILE_JSON }))
        assertFalse(styleHasUnfrozenTileSources("not a style"))
    }
}
