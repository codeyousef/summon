package codes.yousef.summon.hydration

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.events.Event

object GlobalEventListener {
    fun init() {
        val events = listOf("click", "input", "change", "submit")
        events.forEach { eventType ->
            document.addEventListener(eventType, { event ->
                val target = event.target as? Element ?: return@addEventListener
                
                // Find closest element with data-sid or data-action
                // data-action is used by HamburgerMenu and other components for client-side actions
                var current: Element? = target
                while (current != null && 
                       !current.hasAttribute("data-sid") && 
                       !current.hasAttribute("data-action")) {
                    current = current.parentElement
                }
                
                if (current != null) {
                    // Check for data-action first (client-side only actions like ToggleVisibility)
                    val actionJson = current.getAttribute("data-action")
                    if (actionJson != null) {
                        ClientDispatcher.dispatch(actionJson)
                        event.preventDefault()
                        return@addEventListener
                    }
                    
                    // Fall back to data-sid based handling
                    val sid = current.getAttribute("data-sid")
                    if (sid != null) {
                        handleEvent(eventType, sid, event, current)
                    }
                }
            })
        }
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
