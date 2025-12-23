package codes.yousef.summon.aether

import codes.yousef.summon.runtime.CallbackContextElement
import kotlinx.coroutines.withContext

internal actual suspend fun <T> withRenderingContext(block: suspend () -> T): T {
    val callbackContext = CallbackContextElement()
    return withContext(callbackContext) {
        block()
    }
}
