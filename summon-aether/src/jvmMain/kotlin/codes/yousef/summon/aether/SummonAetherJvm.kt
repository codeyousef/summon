package codes.yousef.summon.aether

import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.RenderingContextElement
import kotlinx.coroutines.withContext

internal actual suspend fun <T> withRenderingContext(
    renderer: PlatformRenderer,
    block: suspend () -> T
): T = withContext(RenderingContextElement(renderer)) {
    try {
        block()
    } finally {
        CallbackRegistry.abandonRenderContext()
    }
}
