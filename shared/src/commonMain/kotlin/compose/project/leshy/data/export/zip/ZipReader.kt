package compose.project.leshy.data.export.zip

/**
 * Reads archives produced by [ZipWriter]. Not a general-purpose zip reader: assumes STORED
 * entries and no archive comment (the end-of-central-directory record is expected in exactly the
 * last 22 bytes) — matches the format [ZipWriter] produces, nothing more.
 */
class ZipReader(private val data: ByteArray) {
    class Entry(val name: String, private val dataOffset: Int, private val size: Int, private val archive: ByteArray) {
        fun bytes(): ByteArray = archive.copyOfRange(dataOffset, dataOffset + size)
    }

    val entries: List<Entry> by lazy { parseCentralDirectory() }

    fun readEntry(name: String): ByteArray? = entries.firstOrNull { it.name == name }?.bytes()

    private fun parseCentralDirectory(): List<Entry> {
        require(data.size >= EOCD_SIZE) { "Not a zip archive: too small" }
        val eocdOffset = data.size - EOCD_SIZE
        require(leUInt32(eocdOffset) == END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
            "Not a recognized archive: missing end-of-central-directory record " +
                "(zip comments aren't supported)"
        }
        val recordCount = leUInt16(eocdOffset + 10)
        var pointer = leUInt32(eocdOffset + 16).toInt()

        return buildList(recordCount) {
            repeat(recordCount) {
                require(leUInt32(pointer) == CENTRAL_FILE_HEADER_SIGNATURE) { "Malformed central directory" }
                val size = leUInt32(pointer + 24).toInt()
                val nameLength = leUInt16(pointer + 28)
                val extraLength = leUInt16(pointer + 30)
                val commentLength = leUInt16(pointer + 32)
                val localHeaderOffset = leUInt32(pointer + 42).toInt()
                val name = data.decodeToString(pointer + 46, pointer + 46 + nameLength)

                add(Entry(name, localFileDataOffset(localHeaderOffset), size, data))
                pointer += 46 + nameLength + extraLength + commentLength
            }
        }
    }

    private fun localFileDataOffset(localHeaderOffset: Int): Int {
        require(leUInt32(localHeaderOffset) == LOCAL_FILE_HEADER_SIGNATURE) { "Malformed local file header" }
        val nameLength = leUInt16(localHeaderOffset + 26)
        val extraLength = leUInt16(localHeaderOffset + 28)
        return localHeaderOffset + 30 + nameLength + extraLength
    }

    private fun leUInt16(at: Int): Int =
        (data[at].toInt() and 0xFF) or ((data[at + 1].toInt() and 0xFF) shl 8)

    private fun leUInt32(at: Int): Long =
        leUInt16(at).toLong() or (leUInt16(at + 2).toLong() shl 16)

    private companion object {
        const val EOCD_SIZE = 22
        const val LOCAL_FILE_HEADER_SIGNATURE = 0x04034b50L
        const val CENTRAL_FILE_HEADER_SIGNATURE = 0x02014b50L
        const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50L
    }
}
