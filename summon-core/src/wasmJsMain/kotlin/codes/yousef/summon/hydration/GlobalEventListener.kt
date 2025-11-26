package codes.yousef.summon.hydration

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.events.Event

// Top-level function to set hydration active flag (required for WASM js() calls)
private fun setHydrationActiveFlag(): Unit = js("window.__SUMMON_HYDRATION_ACTIVE__ = true")

/**
 * Global event listener for WASM target.
 * Handles document-level events for data-action based client-side interactions.
 */
object GlobalEventListener {
    private var initialized = false
    private val eventHandlers = mutableMapOf<String, (Event) -> Unit>()
    
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
                handleEvent(eventType, sid, event, current)
            }
        }
    }
    
    fun init() {
        // Guard against multiple initializations
        if (initialized) return
        initialized = true

        // Signal to the bootloader that WASM hydration is now active
        // This prevents the bootloader from double-handling data-action clicks
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
        initialized = false
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
