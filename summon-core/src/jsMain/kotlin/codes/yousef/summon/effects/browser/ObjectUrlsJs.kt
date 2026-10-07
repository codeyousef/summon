package codes.yousef.summon.effects.browser

import org.khronos.webgl.Uint8Array

internal actual fun createPlatformObjectUrl(bytes: ByteArray, mimeType: String): OwnedObjectUrl {
    val nativeBytes = Uint8Array(bytes.size)
    for (index in bytes.indices) nativeBytes.asDynamic()[index] = bytes[index]
    val url = createNativeObjectUrl(nativeBytes, mimeType)
    return JsOwnedObjectUrl(url)
}

private fun createNativeObjectUrl(bytes: Uint8Array, mimeType: String): String =
    js("URL.createObjectURL(new Blob([bytes], { type: mimeType }))") as String

private fun revokeNativeObjectUrl(value: String) {
    js("URL.revokeObjectURL(value)")
}

private class JsOwnedObjectUrl(override val value: String) : OwnedObjectUrl {
    private var revoked = false
    override val isRevoked: Boolean get() = revoked

    override fun close() {
        if (revoked) return
        revoked = true
        revokeNativeObjectUrl(value)
    }
}
