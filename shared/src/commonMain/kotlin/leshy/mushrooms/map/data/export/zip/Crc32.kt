package leshy.mushrooms.map.data.export.zip

// java.util.zip.CRC32 isn't available on Kotlin/Native (iOS) — table-based IEEE CRC-32,
// same algorithm/table ZIP itself specifies for the local/central directory header checksum.
@OptIn(ExperimentalUnsignedTypes::class)
internal object Crc32 {
    private val table = UIntArray(256) { n ->
        var c = n.toUInt()
        repeat(8) {
            c = if (c and 1u != 0u) (0xEDB88320u xor (c shr 1)) else (c shr 1)
        }
        c
    }

    fun of(bytes: ByteArray): UInt {
        var crc = 0xFFFFFFFFu
        for (b in bytes) {
            val index = ((crc xor b.toUInt()) and 0xFFu).toInt()
            crc = table[index] xor (crc shr 8)
        }
        return crc xor 0xFFFFFFFFu
    }
}
