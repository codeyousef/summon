package codes.yousef.summon.components.input

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

actual data class FileInfo(
    actual val name: String,
    actual val size: Long,
    actual val type: String,
    val file: File? = null,
    actual val lastModifiedMillis: Long = file?.lastModified() ?: 0L
) {
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
