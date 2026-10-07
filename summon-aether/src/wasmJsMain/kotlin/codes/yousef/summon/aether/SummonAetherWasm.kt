package codes.yousef.summon.aether

import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer

internal actual suspend fun <T> withRenderingContext(
    renderer: PlatformRenderer,
    block: suspend () -> T
): T {
    setPlatformRenderer(renderer)
    return try {
        block()
    } finally {
        CallbackRegistry.abandonRenderContext()
        clearPlatformRenderer()
    }
}
