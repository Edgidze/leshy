package leshy.mushrooms.map.data.catalog

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogSourceTest {
    @Test
    fun catalogHas408Entries() {
        assertEquals(408, CatalogSource().entries.size)
    }

    @Test
    fun everyEntryHasANonBlankScientificName() {
        val blank = CatalogSource().entries.filter { it.sci.isBlank() }
        assertTrue(blank.isEmpty(), "Entries without a scientific name: ${blank.map { it.key }}")
    }

    @Test
    fun keysAreUnique() {
        val entries = CatalogSource().entries
        assertEquals(entries.size, entries.map { it.key }.toSet().size)
    }
}
