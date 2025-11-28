package codes.yousef.summon.hydration

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.events.Event

// Top-level functions for hydration flag operations (required for WASM js() calls)
private fun isHydrationActive(): Boolean = js("window.__SUMMON_HYDRATION_ACTIVE__ === true")
private fun setHydrationActiveFlag(): Unit = js("window.__SUMMON_HYDRATION_ACTIVE__ = true")
private fun resetHydrationActiveFlag(): Unit = js("window.__SUMMON_HYDRATION_ACTIVE__ = false")

/**
 * Global event listener for WASM target.
 *
 * Handles document-level events for data-action based client-side interactions.
 * Also buffers events for non-hydrated components so they can be replayed
 * after hydration completes.
 */
object GlobalEventListener {
    private var initialized = false
    private val eventHandlers = mutableMapOf<String, (Event) -> Unit>()
    private val hydratedElements = mutableSetOf<String>()
    private val eventBuffer = EventBuffer.instance

    /**
     * Enable/disable verbose logging.
     */
    var enableLogging = false

    /**
     * Check if an element has been marked as hydrated.
     */
    fun isElementHydrated(elementId: String): Boolean {
        return hydratedElements.contains(elementId)
    }

    /**
     * Mark an element as hydrated and replay any buffered events.
     */
    fun markElementHydrated(elementId: String) {
        hydratedElements.add(elementId)

        // Replay any buffered events for this element
        if (eventBuffer.hasEventsFor(elementId)) {
            if (enableLogging) {
                wasmConsoleLog("[Summon WASM] Replaying ${eventBuffer.eventCountFor(elementId)} buffered events for $elementId")
            }
            eventBuffer.replayEventsFor(elementId) { capturedEvent ->
                replayEvent(capturedEvent)
            }
        }
    }

    /**
     * Replay a captured event by dispatching it to the target element.
     */
    private fun replayEvent(capturedEvent: CapturedEvent) {
        val element = document.getElementById(capturedEvent.targetId) as? Element ?: return

        if (enableLogging) {
            wasmConsoleLog("[Summon WASM] Replaying ${capturedEvent.type} event for ${capturedEvent.targetId}")
        }

        // For click events, find and dispatch the action
        if (capturedEvent.type == CapturedEvent.TYPE_CLICK) {
            val actionJson = element.getAttribute("data-action")
            if (actionJson != null) {
                ClientDispatcher.dispatch(actionJson)
            }
        }
    }

    private fun handleEventInternal(event: Event) {
        val target = event.target as? Element ?: return

        // First, look specifically for data-action (for toggle menus, etc.)
        var current: Element? = target
        while (current != null && !current.hasAttribute("data-action")) {
            current = current.parentElement
        }

        if (current != null) {
            val actionJson = current.getAttribute("data-action")
            if (actionJson != null) {
                val elementId = current.id

                // Check if element is hydrated
                if (elementId.isNotEmpty() && !isElementHydrated(elementId)) {
                    // Buffer the event for replay after hydration
                    if (enableLogging) {
                        wasmConsoleLog("[Summon WASM] Buffering ${event.type} for non-hydrated element: $elementId")
                    }
                    eventBuffer.captureEvent(CapturedEvent(
                        type = event.type,
                        targetId = elementId,
                        timestamp = wasmDateNow().toLong()
                    ))
                    event.preventDefault()
                    return
                }

                ClientDispatcher.dispatch(actionJson)
                event.preventDefault()
                return
            }
        }

        // If no data-action found, look for data-sid for other event handling
        current = target
        while (current != null && !current.hasAttribute("data-sid")) {
            current = current.parentElement
        }

        if (current != null) {
            val sid = current.getAttribute("data-sid")
            if (sid != null) {
                val eventType = event.type

                // Check if element is hydrated
                if (!isElementHydrated(sid)) {
                    // Buffer the event for replay after hydration
                    if (enableLogging) {
                        wasmConsoleLog("[Summon WASM] Buffering $eventType for non-hydrated element: $sid")
                    }
                    eventBuffer.captureEvent(CapturedEvent(
                        type = eventType,
                        targetId = sid,
                        timestamp = wasmDateNow().toLong()
                    ))
                    event.preventDefault()
                    return
                }

                handleEvent(eventType, sid, event, current)
            }
        }
    }

    fun init() {
        // Guard against multiple initializations
        if (initialized) return

        // Check if another hydration client (JS or WASM) is already active
        // This prevents double-handling when both bundles load
        if (isHydrationActive()) {
            if (enableLogging) {
                wasmConsoleLog("[Summon WASM] GlobalEventListener.init() - skipping, another hydration client is already active")
            }
            return
        }

        initialized = true

        if (enableLogging) {
            wasmConsoleLog("[Summon WASM] GlobalEventListener.init() - registering document event listeners")
        }

        // Signal to the bootloader and other hydration clients that this one is now active
        setHydrationActiveFlag()

        // Add document-level event listeners for data-action and data-sid handling
        val events = listOf("click", "input", "change", "submit")
        events.forEach { eventType ->
            val handler: (Event) -> Unit = { event -> handleEventInternal(event) }
            eventHandlers[eventType] = handler
            document.addEventListener(eventType, handler)
        }
    }

    // Reset initialization state and remove event listeners (for testing purposes only)
    fun reset() {
        eventHandlers.forEach { (eventType, handler) ->
            document.removeEventListener(eventType, handler)
        }
        eventHandlers.clear()
        hydratedElements.clear()
        eventBuffer.clear()
        initialized = false
        // Also reset the global flag so init() can run again
        resetHydrationActiveFlag()
    }

    fun handleEvent(type: String, sid: String, event: Event, element: Element? = null) {
        val el = element ?: document.querySelector("[data-sid='$sid']") as? Element ?: return

        val actionJson = el.getAttribute("data-action")
        if (actionJson != null) {
            ClientDispatcher.dispatch(actionJson)
            event.preventDefault()
        }
    }
}

/**
 * Get current timestamp from JavaScript Date.now().
 */
@JsFun("() => Date.now()")
private external fun wasmDateNow(): Double
