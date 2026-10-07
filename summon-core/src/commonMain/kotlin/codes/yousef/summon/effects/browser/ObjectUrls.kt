package codes.yousef.summon.effects.browser

/** Revocable browser-local URL for already verified decrypted bytes. */
interface OwnedObjectUrl : AutoCloseable {
    /** The property declaration value. */
    val value: String
    /** The property declaration value. */
    val isRevoked: Boolean
    /** Closes the operation. */
    override fun close()
}

/** Contract for media capability state. */
sealed interface MediaCapabilityState {
    /** Provides loading operations. */
    data object Loading : MediaCapabilityState
    /**
     * Represents ready.
     *
     * @property url Target URL.
     * @property mimeType The mime type value.
     */
    data class Ready(val url: OwnedObjectUrl, val mimeType: String) : MediaCapabilityState
    /** Provides integrity failed operations. */
    data object IntegrityFailed : MediaCapabilityState
    /**
     * Represents unsupported.
     *
     * @property mimeType The mime type value.
     */
    data class Unsupported(val mimeType: String) : MediaCapabilityState
    /** Provides locked operations. */
    data object Locked : MediaCapabilityState
    /** Provides expired operations. */
    data object Expired : MediaCapabilityState
}

/** Represents object URL exception. */
class ObjectUrlException(message: String) : Exception(message) {
    /** Provides object url exception factory and constant members. */
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
