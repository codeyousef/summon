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
                
                // Find closest element with data-sid
                var current: Element? = target
                while (current != null && !current.hasAttribute("data-sid")) {
                    current = current.parentElement
                }
                
                if (current != null) {
                    val sid = current.getAttribute("data-sid")!!
                    handleEvent(eventType, sid, event, current)
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
