package leshy.mushrooms.map.data.export.zip

import okio.BufferedSink

/**
 * Minimal STORED-only (uncompressed) ZIP writer — not a general-purpose zip implementation, only
 * supports what [ZipReader] reads back: no compression, no data descriptors, no archive comment.
 * Photos are already JPEG and the JSON metadata is tiny in comparison, so skipping DEFLATE costs
 * little size here while avoiding a pure-Kotlin deflate implementation (needed since
 * `java.util.zip` isn't available on Kotlin/Native/iOS).
 */
class ZipWriter(private val sink: BufferedSink) {
    private class Entry(val nameBytes: ByteArray, val crc: UInt, val size: Int, val localHeaderOffset: Long)

    private val entries = mutableListOf<Entry>()
    private var offset = 0L

    fun writeEntry(name: String, bytes: ByteArray) {
        val nameBytes = name.encodeToByteArray()
        val crc = Crc32.of(bytes)
        val localHeaderOffset = offset
        require(offset + LOCAL_HEADER_FIXED_SIZE + nameBytes.size + bytes.size <= Int.MAX_VALUE) {
            "Archive exceeds the 2GB ZIP32 limit this writer supports"
        }

        sink.writeIntLe(LOCAL_FILE_HEADER_SIGNATURE)
        sink.writeShortLe(VERSION)
        sink.writeShortLe(0) // general purpose flag
        sink.writeShortLe(METHOD_STORED)
        sink.writeShortLe(DOS_TIME)
        sink.writeShortLe(DOS_DATE)
        sink.writeIntLe(crc.toInt())
        sink.writeIntLe(bytes.size)
        sink.writeIntLe(bytes.size)
        sink.writeShortLe(nameBytes.size)
        sink.writeShortLe(0) // extra field length
        sink.write(nameBytes)
        sink.write(bytes)

        offset += LOCAL_HEADER_FIXED_SIZE + nameBytes.size + bytes.size
        entries += Entry(nameBytes, crc, bytes.size, localHeaderOffset)
    }

    fun finish() {
        val centralDirectoryStart = offset
        for (entry in entries) {
            sink.writeIntLe(CENTRAL_FILE_HEADER_SIGNATURE)
            sink.writeShortLe(VERSION) // version made by
            sink.writeShortLe(VERSION) // version needed to extract
            sink.writeShortLe(0) // general purpose flag
            sink.writeShortLe(METHOD_STORED)
            sink.writeShortLe(DOS_TIME)
            sink.writeShortLe(DOS_DATE)
            sink.writeIntLe(entry.crc.toInt())
            sink.writeIntLe(entry.size)
            sink.writeIntLe(entry.size)
            sink.writeShortLe(entry.nameBytes.size)
            sink.writeShortLe(0) // extra field length
            sink.writeShortLe(0) // file comment length
            sink.writeShortLe(0) // disk number start
            sink.writeShortLe(0) // internal file attributes
            sink.writeIntLe(0) // external file attributes
            sink.writeIntLe(entry.localHeaderOffset.toInt())
            sink.write(entry.nameBytes)

            offset += CENTRAL_HEADER_FIXED_SIZE + entry.nameBytes.size
        }
        val centralDirectorySize = offset - centralDirectoryStart

        sink.writeIntLe(END_OF_CENTRAL_DIRECTORY_SIGNATURE)
        sink.writeShortLe(0) // disk number
        sink.writeShortLe(0) // disk where central directory starts
        sink.writeShortLe(entries.size) // records on this disk
        sink.writeShortLe(entries.size) // total records
        sink.writeIntLe(centralDirectorySize.toInt())
        sink.writeIntLe(centralDirectoryStart.toInt())
        sink.writeShortLe(0) // comment length

        sink.flush()
    }

    private companion object {
        const val LOCAL_FILE_HEADER_SIGNATURE = 0x04034b50
        const val CENTRAL_FILE_HEADER_SIGNATURE = 0x02014b50
        const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50
        const val VERSION = 20
        const val METHOD_STORED = 0
        const val LOCAL_HEADER_FIXED_SIZE = 30
        const val CENTRAL_HEADER_FIXED_SIZE = 46

        // DOS date for 1980-01-01, the format's minimum valid date — an all-zero date is invalid
        // and some strict readers reject it. The exact timestamp isn't meaningful for this archive.
        const val DOS_DATE = 0x21
        const val DOS_TIME = 0x00
    }
}
