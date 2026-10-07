package codes.yousef.summon.components.input

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@JsFun(
    """(file, start, end, onSuccess, onFailure) => {
        const reader = new FileReader();
        let settled = false;
        reader.onload = () => {
            if (settled) return;
            settled = true;
            onSuccess(new Uint8Array(reader.result));
        };
        reader.onerror = () => {
            if (settled) return;
            settled = true;
            onFailure(reader.error && reader.error.name ? reader.error.name : 'operation');
        };
        reader.onabort = () => {
            if (settled) return;
            settled = true;
            onFailure('AbortError');
        };
        reader.readAsArrayBuffer(file.slice(start, end));
        return reader;
    }"""
)
private external fun wasmReadFileRange(
    file: JsAny,
    start: Double,
    end: Double,
    onSuccess: (JsAny) -> Unit,
    onFailure: (String) -> Unit
): JsAny

@JsFun("(reader) => { try { reader.abort(); } catch (_) {} }")
private external fun wasmAbortFileRead(reader: JsAny)

@JsFun("(bytes) => bytes.length")
private external fun wasmFileByteLength(bytes: JsAny): Int

@JsFun("(bytes, index) => bytes[index]")
private external fun wasmFileByteAt(bytes: JsAny, index: Int): Int

/**
 * User-selected WASM browser file capability.
 *
 * @property name source file name
 * @property size source size in bytes
 * @property type reported media type
 * @property lastModifiedMillis browser modification timestamp

 * @property nativeFile The native file value.
 */
actual class FileInfo internal constructor(
    actual val name: String,
    actual val size: Long,
    actual val type: String,
    actual val lastModifiedMillis: Long,
    internal val nativeFile: JsAny
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
            val reader = wasmReadFileRange(
                nativeFile,
                offset.toDouble(),
                (offset + length).toDouble(),
                { source ->
                    if (continuation.isActive) {
                        try {
                            val bytes = ByteArray(wasmFileByteLength(source))
                            for (index in bytes.indices) bytes[index] = wasmFileByteAt(source, index).toByte()
                            continuation.resume(bytes)
                        } catch (_: Throwable) {
                            continuation.resumeWithException(FileReadException.Failed())
                        }
                    }
                },
                { kind ->
                    if (continuation.isActive) {
                        if (kind == "AbortError") continuation.cancel()
                        else continuation.resumeWithException(FileReadException.Failed())
                    }
                }
            )
            continuation.invokeOnCancellation { wasmAbortFileRead(reader) }
        }
    }

    /** Returns [name] for destructuring compatibility. */
    actual operator fun component1(): String = name
    /** Returns [size] for destructuring compatibility. */
    actual operator fun component2(): Long = size
    /** Returns [type] for destructuring compatibility. */
    actual operator fun component3(): String = type
}
