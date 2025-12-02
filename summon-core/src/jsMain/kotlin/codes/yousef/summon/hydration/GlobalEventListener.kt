package codes.yousef.summon.hydration

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.events.Event

/**
 * Global event listener for document-level event handling.
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
    var enableLogging = true

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
                console.log("[Summon JS] Replaying ${eventBuffer.eventCountFor(elementId)} buffered events for $elementId")
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
            console.log("[Summon JS] Replaying ${capturedEvent.type} event for ${capturedEvent.targetId}")
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

        // Only log click events to avoid spam
        if (event.type == "click" && enableLogging) {
            console.log("[Summon JS] Click event on: ${target.tagName}")
        }

        // First, look specifically for data-action (for toggle menus, etc.)
        // data-action elements are client-side only and should ALWAYS be handled immediately
        // without buffering - they don't require hydration state
        var current: Element? = target
        while (current != null && !current.hasAttribute("data-action")) {
            current = current.parentElement
        }

        if (current != null) {
            val actionJson = current.getAttribute("data-action")
            if (actionJson != null) {
                if (enableLogging) {
                    console.log("[Summon JS] Found data-action on ${current.tagName}: $actionJson")
                }
                // Dispatch immediately - data-action is client-side only, no hydration needed
                ClientDispatcher.dispatch(actionJson)
                event.preventDefault()
                event.stopPropagation()
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
                        console.log("[Summon JS] Buffering $eventType for non-hydrated element: $sid")
                    }
                    eventBuffer.captureEvent(CapturedEvent(
                        type = eventType,
                        targetId = sid,
                        timestamp = currentTimeMillis()
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
        if (js("window.__SUMMON_HYDRATION_ACTIVE__ === true") as Boolean) {
            if (enableLogging) {
                console.log("[Summon JS] GlobalEventListener.init() - skipping, another hydration client is already active")
            }
            return
        }

        initialized = true

        if (enableLogging) {
            console.log("[Summon JS] GlobalEventListener.init() - registering document event listeners")
        }

        // Signal to the bootloader and other hydration clients that this one is now active
        js("window.__SUMMON_HYDRATION_ACTIVE__ = true")

        val events = listOf("click", "input", "change", "submit")
        events.forEach { eventType ->
            val handler: (Event) -> Unit = { event -> handleEventInternal(event) }
            eventHandlers[eventType] = handler
            document.addEventListener(eventType, handler)
        }

        if (enableLogging) {
            console.log("[Summon JS] GlobalEventListener initialized successfully")
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
        js("window.__SUMMON_HYDRATION_ACTIVE__ = false")
    }

    fun handleEvent(type: String, sid: String, event: Event, element: Element? = null) {
        val el = element ?: document.querySelector("[data-sid='$sid']") ?: return

        val actionJson = el.getAttribute("data-action")
        if (actionJson != null) {
            ClientDispatcher.dispatch(actionJson)
            event.preventDefault()
        }
    }
}

/**
 * Get current time in milliseconds.
 */
private fun currentTimeMillis(): Long {
    return (js("Date.now()") as Number).toLong()
}
