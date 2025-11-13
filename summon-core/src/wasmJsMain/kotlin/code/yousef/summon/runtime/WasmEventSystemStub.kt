package codes.yousef.summon.runtime

import codes.yousef.summon.core.DOMEvent

/**
 * Minimal WASM Event System stub to enable compilation.
 * This is a simplified implementation that provides basic functionality
 * while avoiding WASM-incompatible features like js() calls and dynamic types.
 */
class WasmEventSystem : EventSystemContract {

    // Minimal implementation to satisfy the contract
    override fun <T : DOMEvent> registerHandler(
        element: DOMElement,
        event: EventType<T>,
        handler: (T) -> Unit
    ): EventRegistration {
        // Simplified registration - would need proper implementation
        val handlerId = "event-${wasmPerformanceNow().toLong()}"

        return object : EventRegistration {
            override fun unregister() {
                // Would need proper cleanup
            }
        }
    }
}

/**
 * Event registration stub.
 */
interface EventRegistration {
    fun unregister()
}

/**
 * Event type stub.
 */
interface EventType<T : DOMEvent> {
    val name: String
}

/**
 * Event system contract.
 */
interface EventSystemContract {
    fun <T : DOMEvent> registerHandler(
        element: DOMElement,
        event: EventType<T>,
        handler: (T) -> Unit
    ): EventRegistration
}