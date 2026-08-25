package leshy.mushrooms.map.data.export.zip

import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

class ZipCodecTest {
    @Test
    fun roundTripsTextAndBinaryEntriesAtNestedPaths() {
        val buffer = Buffer()
        val writer = ZipWriter(buffer)
        val binary = ByteArray(1000) { (it % 256).toByte() }

        writer.writeEntry("manifest.json", """{"schemaVersion":1}""".encodeToByteArray())
        writer.writeEntry("walks/walk-1/walk.json", """{"name":"Тестовая прогулка"}""".encodeToByteArray())
        writer.writeEntry("walks/walk-1/photos/1.jpg", binary)
        writer.writeEntry("empty.json", ByteArray(0))
        writer.finish()

        val reader = ZipReader(buffer.readByteArray())

        assertEquals(4, reader.entries.size)
        assertEquals(
            """{"schemaVersion":1}""",
            reader.readEntry("manifest.json")?.decodeToString(),
        )
        assertEquals(
            """{"name":"Тестовая прогулка"}""",
            reader.readEntry("walks/walk-1/walk.json")?.decodeToString(),
        )
        assertContentEquals(binary, reader.readEntry("walks/walk-1/photos/1.jpg") ?: fail("entry missing"))
        assertContentEquals(ByteArray(0), reader.readEntry("empty.json") ?: fail("entry missing"))
        assertNull(reader.readEntry("does/not/exist"))
    }

    @Test
    fun preservesEntryOrderAndNames() {
        val buffer = Buffer()
        val writer = ZipWriter(buffer)
        val names = (1..20).map { "walks/walk-$it/track.json" }
        names.forEach { writer.writeEntry(it, "[]".encodeToByteArray()) }
        writer.finish()

        val reader = ZipReader(buffer.readByteArray())

        assertEquals(names, reader.entries.map { it.name })
    }

    @Test
    fun crc32MatchesKnownVector() {
        // "123456789" -> 0xCBF43926 is the standard CRC-32/ISO-HDLC test vector.
        assertEquals(0xCBF43926u, Crc32.of("123456789".encodeToByteArray()))
    }

    @Test
    fun rejectsArchiveWithoutEndOfCentralDirectory() {
        val garbage = ByteArray(30)
        val threw = try {
            ZipReader(garbage).entries
            false
        } catch (e: IllegalArgumentException) {
            true
        }
        assertTrue(threw)
    }
}
