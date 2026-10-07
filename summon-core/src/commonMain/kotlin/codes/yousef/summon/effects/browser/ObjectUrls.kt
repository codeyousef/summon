package codes.yousef.summon.effects.browser

/** Revocable browser-local URL for already verified decrypted bytes. */
interface OwnedObjectUrl : AutoCloseable {
    val value: String
    val isRevoked: Boolean
    override fun close()
}

sealed interface MediaCapabilityState {
    data object Loading : MediaCapabilityState
    data class Ready(val url: OwnedObjectUrl, val mimeType: String) : MediaCapabilityState
    data object IntegrityFailed : MediaCapabilityState
    data class Unsupported(val mimeType: String) : MediaCapabilityState
    data object Locked : MediaCapabilityState
    data object Expired : MediaCapabilityState
}

class ObjectUrlException(message: String) : Exception(message) {
    companion object {
        internal fun unavailable() = ObjectUrlException("Browser object URLs are unavailable")
    }
}

/**
 * Runs caller-owned capability and integrity checks before creating a browser media capability.
 * Unsupported media and failed integrity checks never expose bytes to an object URL.
 */
suspend fun createVerifiedObjectUrl(
    bytes: ByteArray,
    mimeType: String,
    isMimeTypeSupported: (String) -> Boolean = { true },
    verifyIntegrity: suspend (ByteArray) -> Boolean
): MediaCapabilityState {
    require(mimeType.length in 1..127 && mimeType.none { it <= '\u001f' || it == '\u007f' }) {
        "Invalid media type"
    }
    if (!isMimeTypeSupported(mimeType)) return MediaCapabilityState.Unsupported(mimeType)
    if (!verifyIntegrity(bytes)) return MediaCapabilityState.IntegrityFailed
    return MediaCapabilityState.Ready(createPlatformObjectUrl(bytes, mimeType), mimeType)
}

internal expect fun createPlatformObjectUrl(bytes: ByteArray, mimeType: String): OwnedObjectUrl
