package codes.yousef.summon.effects.browser

@JsFun("(size) => new Uint8Array(size)")
private external fun wasmObjectUrlBytes(size: Int): JsAny

@JsFun("(bytes, index, value) => { bytes[index] = value; }")
private external fun wasmSetObjectUrlByte(bytes: JsAny, index: Int, value: Int)

@JsFun("(bytes, mimeType) => URL.createObjectURL(new Blob([bytes], { type: mimeType }))")
private external fun wasmCreateObjectUrl(bytes: JsAny, mimeType: String): String

@JsFun("(value) => URL.revokeObjectURL(value)")
private external fun wasmRevokeObjectUrl(value: String)

internal actual fun createPlatformObjectUrl(bytes: ByteArray, mimeType: String): OwnedObjectUrl {
    val nativeBytes = wasmObjectUrlBytes(bytes.size)
    for (index in bytes.indices) wasmSetObjectUrlByte(nativeBytes, index, bytes[index].toInt() and 0xff)
    return WasmOwnedObjectUrl(wasmCreateObjectUrl(nativeBytes, mimeType))
}

private class WasmOwnedObjectUrl(override val value: String) : OwnedObjectUrl {
    private var revoked = false
    override val isRevoked: Boolean get() = revoked

    override fun close() {
        if (revoked) return
        revoked = true
        wasmRevokeObjectUrl(value)
    }
}
