package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

/**
 * WebAssembly implementation of platform-specific DOM operations.
 */

actual fun scrollIntoViewPlatform(nativeElementId: String, behavior: String) {
    wasmScrollElementIntoView(nativeElementId, behavior)
}

actual fun getComputedStylePlatform(nativeElementId: String, property: String): String? {
    return wasmGetComputedStyleProperty(nativeElementId, property)
}