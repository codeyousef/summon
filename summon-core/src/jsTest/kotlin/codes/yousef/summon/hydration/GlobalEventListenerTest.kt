package codes.yousef.summon.hydration

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.MouseEventInit
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalEventListenerTest {

    @Test
    fun testEventDelegation() {
        // Initialize listeners
        GlobalEventListener.init()

        // Create DOM structure
        // body -> div(data-sid="container") -> button(data-sid="btn-1", data-action=...)
        val container = document.createElement("div") as HTMLElement
        container.setAttribute("data-sid", "container")
        
        val button = document.createElement("button") as HTMLElement
        button.setAttribute("data-sid", "btn-1")
        // JSON for UiAction.Navigate("test-url")
        button.setAttribute("data-action", """{"type":"nav","url":"test-url"}""")
        
        container.appendChild(button)
        document.body!!.appendChild(container)

        // Simulate click
        val event = MouseEvent("click", MouseEventInit(bubbles = true))
        button.dispatchEvent(event)

        // Verify window location changed (ClientDispatcher handles Navigate by setting window.location.href)
        // Note: In HappyDOM/JSDOM, window.location might be mockable or we check if it was set.
        // If window.location.href is not writable in test env, this might fail or need a spy.
        // Assuming HappyDOM allows setting href or we can check it.
        
        // If this fails due to navigation not being supported in test env, we might need to spy on ClientDispatcher.
        // But ClientDispatcher is an object.
        // Let's assume for now we can check window.location.href or that it doesn't crash.
        
        // Actually, checking window.location.href might be tricky if it causes a "navigation" in the test runner.
        // But let's try.
        
        // Alternative: Spy on console.log if ClientDispatcher logs?
        // ClientDispatcher.dispatch(Navigate) sets window.location.href.
        
        // Let's just assert that the event was handled (preventDefault called?).
        // GlobalEventListener calls event.preventDefault() if action is found.
        
        assertEquals(true, event.defaultPrevented, "Event should be prevented if action is dispatched")
    }
}
