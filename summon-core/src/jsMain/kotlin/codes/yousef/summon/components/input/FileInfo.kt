package codes.yousef.summon.components.input

import kotlinx.coroutines.suspendCancellableCoroutine
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.files.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * User-selected browser file capability.
 *
 * @property name source file name
 * @property size source size in bytes
 * @property type reported media type
 * @property jsFile native browser file retained for bounded reads
 * @property lastModifiedMillis browser modification timestamp
 */
actual data class FileInfo(
    actual val name: String,
    actual val size: Long,
    actual val type: String,
    val jsFile: File,
    actual val lastModifiedMillis: Long = jsFile.lastModified.toLong()
) {
    /** The property declaration value. */
    actual val sourceVersion: FileSourceVersion
        get() = FileSourceVersion(size, lastModifiedMillis)

    actual internal suspend fun readRange(offset: Long, length: Int): ByteArray {
        if (offset < 0 || length < 0 || offset + length.toLong() < offset || offset + length > size) {
            throw FileReadException.InvalidRange()
        }
        if (length == 0) return ByteArray(0)

        return suspendCancellableCoroutine { continuation ->
            val reader = js("new FileReader()")
            reader.onload = {
                if (continuation.isActive) {
                    try {
                        val source = Uint8Array(reader.result as ArrayBuffer)
                        val bytes = ByteArray(source.length)
                        for (index in bytes.indices) bytes[index] = source.asDynamic()[index]
                        continuation.resume(bytes)
                    } catch (_: Throwable) {
                        continuation.resumeWithException(FileReadException.Failed())
                    }
                }
            }
            reader.onerror = {
                if (continuation.isActive) continuation.resumeWithException(FileReadException.Failed())
            }
            reader.onabort = {
                if (continuation.isActive) continuation.cancel()
            }
            continuation.invokeOnCancellation { reader.abort() }
            reader.readAsArrayBuffer(jsFile.asDynamic().slice(offset.toDouble(), (offset + length).toDouble()))
        }
    }
}
