package codes.yousef.summon.components.input

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * JVM file metadata with an optional native read capability.
 *
 * @property name source file name
 * @property size declared source size in bytes
 * @property type reported media type
 * @property file optional native source; absent sources cannot be read
 * @property lastModifiedMillis source modification timestamp
 */
actual data class FileInfo(
    actual val name: String,
    actual val size: Long,
    actual val type: String,
    val file: File? = null,
    actual val lastModifiedMillis: Long = file?.lastModified() ?: 0L
) {
    /** The property declaration value. */
    actual val sourceVersion: FileSourceVersion
        get() = FileSourceVersion(file?.length() ?: size, file?.lastModified() ?: lastModifiedMillis)

    actual internal suspend fun readRange(offset: Long, length: Int): ByteArray {
        val source = file ?: throw FileReadException.Unavailable()
        if (offset < 0 || length < 0 || offset + length.toLong() < offset || offset + length > source.length()) {
            throw FileReadException.InvalidRange()
        }
        if (length == 0) return ByteArray(0)
        return try {
            ByteArray(length).also { bytes ->
                RandomAccessFile(source, "r").use { input ->
                    input.seek(offset)
                    input.readFully(bytes)
                }
            }
        } catch (_: IOException) {
            throw FileReadException.Failed()
        }
    }
}
