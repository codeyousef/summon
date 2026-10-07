package codes.yousef.summon.effects.browser

internal actual fun createPlatformObjectUrl(bytes: ByteArray, mimeType: String): OwnedObjectUrl =
    throw ObjectUrlException.unavailable()
