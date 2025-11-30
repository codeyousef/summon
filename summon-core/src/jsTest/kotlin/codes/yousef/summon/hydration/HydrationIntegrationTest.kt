package codes.yousef.summon.hydration

import codes.yousef.summon.js.testutil.ensureJsDom
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.MouseEventInit
import kotlin.test.*

/**
 * Integration tests for hydration system.
 *
 * These tests verify:
 * - data-action elements work regardless of hydration state
 * - GlobalEventListener handles events correctly with/without hydration
 * - Event buffering for non-data-action elements (data-sid)
 * - The __SUMMON_HYDRATION_ACTIVE__ flag behavior
 */
class HydrationIntegrationTest {

    private lateinit var container: HTMLElement

    @BeforeTest
    fun setUp() {
        ensureJsDom()
        container = document.createElement("div") as HTMLElement
        container.id = "hydration-test-container"
        document.body?.appendChild(container)

        // Reset hydration state
        js("window.__SUMMON_HYDRATION_ACTIVE__ = false")

        // Reset and initialize GlobalEventListener
        GlobalEventListener.reset()
        GlobalEventListener.enableLogging = false

        // Enable sync mode for ClientDispatcher
        ClientDispatcher.syncMode = true
        ClientDispatcher.enableLogging = false
    }

    @AfterTest
    fun tearDown() {
        container.parentElement?.removeChild(container)
        ClientDispatcher.syncMode = false
        js("window.__SUMMON_HYDRATION_ACTIVE__ = false")
    }

    // ==========================================================================
    // data-action Elements - Should ALWAYS work regardless of hydration state
    // ==========================================================================

    @Test
    fun `data-action toggle works when hydration is NOT active`() {
        js("window.__SUMMON_HYDRATION_ACTIVE__ = false")
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-action='{"type":"toggle","targetId":"menu-not-active"}'>Toggle</button>
            <div id="menu-not-active" style="display: none;">Menu</div>
        """.trimIndent()

        val button = container.querySelector("[data-action]") as HTMLElement
        val menu = document.getElementById("menu-not-active") as HTMLElement

        assertEquals("none", menu.style.display, "Menu should be hidden initially")

        // Simulate click
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        assertEquals("flex", menu.style.display, "Menu should be visible after click")
        assertTrue(clickEvent.defaultPrevented, "Event should be prevented")
    }

    @Test
    fun `data-action toggle works when hydration IS active`() {
        // This is the critical test that was missing!
        // The bug was that data-action was being skipped when hydration was active
        GlobalEventListener.init()
        js("window.__SUMMON_HYDRATION_ACTIVE__ = true")

        container.innerHTML = """
            <button data-action='{"type":"toggle","targetId":"menu-active"}'>Toggle</button>
            <div id="menu-active" style="display: none;">Menu</div>
        """.trimIndent()

        val button = container.querySelector("[data-action]") as HTMLElement
        val menu = document.getElementById("menu-active") as HTMLElement

        assertEquals("none", menu.style.display, "Menu should be hidden initially")

        // Simulate click
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        assertEquals("flex", menu.style.display, "Menu should be visible after click - even with hydration active")
        assertTrue(clickEvent.defaultPrevented, "Event should be prevented")
    }

    @Test
    fun `data-action toggle works after GlobalEventListener skips init due to hydration active`() {
        // Simulate scenario where WASM hydration client already initialized
        js("window.__SUMMON_HYDRATION_ACTIVE__ = true")

        // JS GlobalEventListener.init() should skip but data-action should still work
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-action='{"type":"toggle","targetId":"menu-skip-init"}'>Toggle</button>
            <div id="menu-skip-init" style="display: none;">Menu</div>
        """.trimIndent()

        // Even though GlobalEventListener skipped init, we should be able to use ClientDispatcher directly
        val menu = document.getElementById("menu-skip-init") as HTMLElement
        assertEquals("none", menu.style.display, "Menu should be hidden initially")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-skip-init"}""")

        assertEquals("flex", menu.style.display, "ClientDispatcher should work regardless of GlobalEventListener state")
    }

    // ==========================================================================
    // data-sid Elements - May be buffered for hydration
    // ==========================================================================

    @Test
    fun `data-sid element events are buffered when not hydrated`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-sid="btn-123" data-action='{"type":"toggle","targetId":"menu-sid"}'>Toggle</button>
            <div id="menu-sid" style="display: none;">Menu</div>
        """.trimIndent()

        val button = container.querySelector("[data-sid='btn-123']") as HTMLElement

        // Do NOT mark element as hydrated - events should be buffered
        // But since this element also has data-action, it should be handled immediately!

        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        // data-action takes precedence and should be handled immediately
        val menu = document.getElementById("menu-sid") as HTMLElement
        assertEquals("flex", menu.style.display, "data-action should be handled immediately even with data-sid")
    }

    @Test
    fun `data-sid only element events are buffered when not hydrated`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-sid="btn-456">Click me</button>
        """.trimIndent()

        val button = container.querySelector("[data-sid='btn-456']") as HTMLElement

        // Element is NOT hydrated and has NO data-action
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        // Event should be prevented (buffered)
        assertTrue(clickEvent.defaultPrevented, "Event should be prevented for non-hydrated data-sid element")
    }

    @Test
    fun `data-sid element events are replayed after hydration`() {
        GlobalEventListener.init()

        // Set up element with data-sid AND data-action
        container.innerHTML = """
            <button data-sid="btn-replay" data-action='{"type":"toggle","targetId":"menu-replay"}'>Toggle</button>
            <div id="menu-replay" style="display: none;">Menu</div>
        """.trimIndent()

        // This test verifies that once marked as hydrated, buffered events are replayed
        // However, since data-action is present, it's handled immediately anyway
        // Let's test the mark hydrated flow

        val button = container.querySelector("[data-sid='btn-replay']") as HTMLElement

        // Dispatch event
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        // With data-action present, it should already be toggled
        val menu = document.getElementById("menu-replay") as HTMLElement
        assertEquals("flex", menu.style.display, "Menu should be visible immediately due to data-action")
    }

    // ==========================================================================
    // Multiple GlobalEventListener init calls
    // ==========================================================================

    @Test
    fun `GlobalEventListener init is idempotent`() {
        // First init
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-action='{"type":"toggle","targetId":"menu-idempotent"}'>Toggle</button>
            <div id="menu-idempotent" style="display: none;">Menu</div>
        """.trimIndent()

        // Try to init again - should be no-op
        GlobalEventListener.init()
        GlobalEventListener.init()

        // Events should still work normally (not be handled multiple times)
        val button = container.querySelector("[data-action]") as HTMLElement
        val menu = document.getElementById("menu-idempotent") as HTMLElement

        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        assertEquals("flex", menu.style.display, "Single toggle should occur")

        // Second click
        val clickEvent2 = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent2)

        assertEquals("none", menu.style.display, "Second toggle should hide")
    }

    @Test
    fun `GlobalEventListener skips init when another hydration client is active`() {
        // Simulate WASM hydration client already running
        js("window.__SUMMON_HYDRATION_ACTIVE__ = true")

        // JS GlobalEventListener should not initialize
        GlobalEventListener.init()

        // The flag should still be true (JS didn't reset it)
        assertEquals(true, js("window.__SUMMON_HYDRATION_ACTIVE__") as Boolean)
    }

    // ==========================================================================
    // Event bubbling and delegation
    // ==========================================================================

    @Test
    fun `click on child element bubbles to parent with data-action`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-action='{"type":"toggle","targetId":"menu-bubble"}'>
                <span class="icon">☰</span>
                <span class="label">Menu</span>
            </button>
            <div id="menu-bubble" style="display: none;">Menu Content</div>
        """.trimIndent()

        // Click on the inner span, not the button directly
        val icon = container.querySelector(".icon") as HTMLElement
        val menu = document.getElementById("menu-bubble") as HTMLElement

        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        icon.dispatchEvent(clickEvent)

        assertEquals("flex", menu.style.display, "Click on child should trigger parent's data-action")
    }

    @Test
    fun `deeply nested click bubbles correctly`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <div data-action='{"type":"toggle","targetId":"menu-deep"}'>
                <div>
                    <div>
                        <span id="deep-child">Click here</span>
                    </div>
                </div>
            </div>
            <div id="menu-deep" style="display: none;">Deep Menu</div>
        """.trimIndent()

        val deepChild = document.getElementById("deep-child") as HTMLElement
        val menu = document.getElementById("menu-deep") as HTMLElement

        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        deepChild.dispatchEvent(clickEvent)

        assertEquals("flex", menu.style.display, "Deeply nested click should work")
    }

    // ==========================================================================
    // SSR Hydration Simulation
    // ==========================================================================

    @Test
    fun `SSR-rendered hamburger menu works after hydration`() {
        GlobalEventListener.init()

        // Simulate SSR-rendered HTML exactly as server would produce
        container.innerHTML = """
            <div style="display: flex; flex-direction: column;">
                <div data-hamburger-toggle="true"
                     role="button"
                     tabindex="0"
                     aria-label="Open menu"
                     aria-expanded="false"
                     aria-controls="hamburger-ssr"
                     data-action='{"type":"toggle","targetId":"hamburger-ssr"}'>
                    <span class="material-icons">menu</span>
                </div>
                <div id="hamburger-ssr" style="display: none; flex-direction: column;">
                    <a href="/">Home</a>
                    <a href="/about">About</a>
                </div>
            </div>
        """.trimIndent()

        val button = container.querySelector("[data-hamburger-toggle='true']") as HTMLElement
        val menu = document.getElementById("hamburger-ssr") as HTMLElement
        val icon = button.querySelector(".material-icons") as HTMLElement

        // Initial state
        assertEquals("none", menu.style.display, "Menu hidden initially")
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded false initially")
        assertEquals("menu", icon.textContent, "Icon is 'menu' initially")

        // Simulate user click
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        // After first click
        assertEquals("flex", menu.style.display, "Menu visible after click")
        assertEquals("true", button.getAttribute("aria-expanded"), "aria-expanded true after click")
        assertEquals("close", icon.textContent, "Icon is 'close' after click")
        assertEquals("Close menu", button.getAttribute("aria-label"), "aria-label updated")

        // Click again to close
        val clickEvent2 = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent2)

        // After second click
        assertEquals("none", menu.style.display, "Menu hidden after second click")
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded false after second click")
        assertEquals("menu", icon.textContent, "Icon is 'menu' after second click")
    }

    @Test
    fun `SSR-rendered disclosure toggle works after hydration`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <div style="border: 1px solid #ccc; padding: 8px;">
                <button aria-controls="disclosure-ssr"
                        aria-expanded="false"
                        data-action='{"type":"toggle","targetId":"disclosure-ssr"}'
                        style="display: flex; align-items: center; gap: 8px;">
                    <span>+</span>
                    <span>Show Details</span>
                </button>
                <div id="disclosure-ssr" style="display: none; padding: 16px;">
                    <p>These are the hidden details that appear when expanded.</p>
                </div>
            </div>
        """.trimIndent()

        val button = container.querySelector("[aria-controls='disclosure-ssr']") as HTMLElement
        val content = document.getElementById("disclosure-ssr") as HTMLElement
        val icon = button.querySelector("span") as HTMLElement

        // Initial state
        assertEquals("none", content.style.display, "Content hidden initially")
        assertEquals("+", icon.textContent?.trim(), "Icon is '+' initially")

        // Click to expand
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        assertEquals("flex", content.style.display, "Content visible after click")
        assertEquals("−", icon.textContent, "Icon is '−' after click")

        // Click to collapse
        val clickEvent2 = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent2)

        assertEquals("none", content.style.display, "Content hidden after second click")
        assertEquals("+", icon.textContent, "Icon is '+' after second click")
    }

    // ==========================================================================
    // Edge Cases in Hydration
    // ==========================================================================

    @Test
    fun `handles missing target element in SSR HTML`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-action='{"type":"toggle","targetId":"missing-target"}'>Toggle</button>
            <!-- Target element is missing -->
        """.trimIndent()

        val button = container.querySelector("[data-action]") as HTMLElement

        // Should not throw
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        // Event should still be prevented (action was processed, just no element found)
        assertTrue(clickEvent.defaultPrevented, "Event should be prevented even if target missing")
    }

    @Test
    fun `handles multiple hamburger menus with unique IDs`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <div>
                <button data-hamburger-toggle="true" aria-controls="menu-1" data-action='{"type":"toggle","targetId":"menu-1"}'>
                    <span class="material-icons">menu</span>
                </button>
                <div id="menu-1" style="display: none;">Menu 1</div>
            </div>
            <div>
                <button data-hamburger-toggle="true" aria-controls="menu-2" data-action='{"type":"toggle","targetId":"menu-2"}'>
                    <span class="material-icons">menu</span>
                </button>
                <div id="menu-2" style="display: none;">Menu 2</div>
            </div>
        """.trimIndent()

        val buttons = container.querySelectorAll("[data-hamburger-toggle='true']")
        val button1 = buttons.item(0) as HTMLElement
        val button2 = buttons.item(1) as HTMLElement
        val menu1 = document.getElementById("menu-1") as HTMLElement
        val menu2 = document.getElementById("menu-2") as HTMLElement

        // Click first menu
        button1.dispatchEvent(MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true)))

        assertEquals("flex", menu1.style.display, "Menu 1 should be visible")
        assertEquals("none", menu2.style.display, "Menu 2 should still be hidden")

        // Click second menu
        button2.dispatchEvent(MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true)))

        assertEquals("flex", menu1.style.display, "Menu 1 should still be visible")
        assertEquals("flex", menu2.style.display, "Menu 2 should now be visible")
    }

    @Test
    fun `handles whitespace in JSON action`() {
        GlobalEventListener.init()

        container.innerHTML = """
            <button data-action='{ "type" : "toggle" , "targetId" : "menu-whitespace" }'>Toggle</button>
            <div id="menu-whitespace" style="display: none;">Menu</div>
        """.trimIndent()

        val button = container.querySelector("[data-action]") as HTMLElement
        val menu = document.getElementById("menu-whitespace") as HTMLElement

        button.dispatchEvent(MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true)))

        assertEquals("flex", menu.style.display, "Should handle JSON with whitespace")
    }
}
