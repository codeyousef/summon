package codes.yousef.summon.hydration

import codes.yousef.summon.js.testutil.ensureJsDom
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import kotlin.test.*

/**
 * Comprehensive tests for ClientDispatcher toggle functionality.
 *
 * These tests cover:
 * - Basic toggle visibility
 * - Display value preservation (flex, grid, block, inline-flex, etc.)
 * - Aria attribute updates
 * - Icon updates for hamburger menus
 * - Disclosure toggle (+/-) icon updates
 * - Edge cases like missing elements, rapid toggling, nested toggles
 * - Interaction with hydration state
 */
class ClientDispatcherTest {

    private lateinit var container: HTMLElement

    @BeforeTest
    fun setUp() {
        ensureJsDom()
        container = document.createElement("div") as HTMLElement
        container.id = "test-container"
        document.body?.appendChild(container)

        // Enable sync mode for immediate DOM updates in tests
        ClientDispatcher.syncMode = true
        ClientDispatcher.enableLogging = false

        // Reset GlobalEventListener
        GlobalEventListener.reset()
    }

    @AfterTest
    fun tearDown() {
        container.parentElement?.removeChild(container)
        ClientDispatcher.syncMode = false
        ClientDispatcher.enableLogging = true
    }

    // ==========================================================================
    // Basic Toggle Tests
    // ==========================================================================

    @Test
    fun `toggle shows hidden element with default flex display`() {
        container.innerHTML = """
            <div id="menu-1" style="display: none;">Menu Content</div>
        """.trimIndent()

        val menu = document.getElementById("menu-1") as HTMLElement
        assertEquals("none", menu.style.display, "Should be hidden initially")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-1"}""")

        assertEquals("flex", menu.style.display, "Should show with flex (default)")
    }

    @Test
    fun `toggle hides visible element`() {
        container.innerHTML = """
            <div id="menu-2" style="display: flex;">Menu Content</div>
        """.trimIndent()

        val menu = document.getElementById("menu-2") as HTMLElement
        assertEquals("flex", menu.style.display, "Should be visible initially")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-2"}""")

        assertEquals("none", menu.style.display, "Should be hidden after toggle")
    }

    @Test
    fun `toggle cycles correctly - hide, show, hide`() {
        container.innerHTML = """
            <div id="menu-3" style="display: flex;">Menu Content</div>
        """.trimIndent()

        val menu = document.getElementById("menu-3") as HTMLElement

        // First toggle: hide
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-3"}""")
        assertEquals("none", menu.style.display, "Should be hidden after first toggle")

        // Second toggle: show
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-3"}""")
        assertEquals("flex", menu.style.display, "Should be visible after second toggle")

        // Third toggle: hide
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-3"}""")
        assertEquals("none", menu.style.display, "Should be hidden after third toggle")
    }

    // ==========================================================================
    // Display Value Preservation Tests
    // ==========================================================================

    @Test
    fun `preserves flex display value`() {
        container.innerHTML = """
            <div id="flex-menu" style="display: flex;">Flex Menu</div>
        """.trimIndent()

        val menu = document.getElementById("flex-menu") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"flex-menu"}""")
        assertEquals("none", menu.style.display, "Should be hidden")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"flex-menu"}""")
        assertEquals("flex", menu.style.display, "Should restore flex")
    }

    @Test
    fun `preserves grid display value`() {
        container.innerHTML = """
            <div id="grid-menu" style="display: grid;">Grid Menu</div>
        """.trimIndent()

        val menu = document.getElementById("grid-menu") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"grid-menu"}""")
        assertEquals("none", menu.style.display, "Should be hidden")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"grid-menu"}""")
        assertEquals("grid", menu.style.display, "Should restore grid")
    }

    @Test
    fun `preserves block display value`() {
        container.innerHTML = """
            <div id="block-menu" style="display: block;">Block Menu</div>
        """.trimIndent()

        val menu = document.getElementById("block-menu") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"block-menu"}""")
        assertEquals("none", menu.style.display, "Should be hidden")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"block-menu"}""")
        assertEquals("block", menu.style.display, "Should restore block")
    }

    @Test
    fun `preserves inline-flex display value`() {
        container.innerHTML = """
            <div id="inline-flex-menu" style="display: inline-flex;">Inline Flex Menu</div>
        """.trimIndent()

        val menu = document.getElementById("inline-flex-menu") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"inline-flex-menu"}""")
        assertEquals("none", menu.style.display, "Should be hidden")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"inline-flex-menu"}""")
        assertEquals("inline-flex", menu.style.display, "Should restore inline-flex")
    }

    @Test
    fun `preserves inline-block display value`() {
        container.innerHTML = """
            <div id="inline-block-menu" style="display: inline-block;">Inline Block Menu</div>
        """.trimIndent()

        val menu = document.getElementById("inline-block-menu") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"inline-block-menu"}""")
        assertEquals("none", menu.style.display, "Should be hidden")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"inline-block-menu"}""")
        assertEquals("inline-block", menu.style.display, "Should restore inline-block")
    }

    @Test
    fun `uses data-original-display attribute when present`() {
        container.innerHTML = """
            <div id="preset-menu" style="display: none;" data-original-display="grid">Menu</div>
        """.trimIndent()

        val menu = document.getElementById("preset-menu") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"preset-menu"}""")
        assertEquals("grid", menu.style.display, "Should use data-original-display value")
    }

    @Test
    fun `stores original display in data attribute on first toggle`() {
        container.innerHTML = """
            <div id="store-test" style="display: inline-grid;">Menu</div>
        """.trimIndent()

        val menu = document.getElementById("store-test") as HTMLElement
        assertNull(menu.getAttribute("data-original-display"), "Should not have data-original-display initially")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"store-test"}""")
        assertEquals("inline-grid", menu.getAttribute("data-original-display"), "Should store original display")
    }

    // ==========================================================================
    // Aria Attribute Tests
    // ==========================================================================

    @Test
    fun `updates aria-expanded on trigger button`() {
        container.innerHTML = """
            <button id="trigger" aria-controls="menu-aria" aria-expanded="false">Toggle</button>
            <div id="menu-aria" style="display: none;">Menu</div>
        """.trimIndent()

        val trigger = document.getElementById("trigger") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-aria"}""")
        assertEquals("true", trigger.getAttribute("aria-expanded"), "Should set aria-expanded to true")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-aria"}""")
        assertEquals("false", trigger.getAttribute("aria-expanded"), "Should set aria-expanded to false")
    }

    @Test
    fun `finds trigger by aria-controls attribute`() {
        // Note: The code first looks for aria-controls, then falls back to data-action selector
        // aria-controls is the primary way to find the trigger
        container.innerHTML = """
            <button id="trigger-aria" aria-controls="menu-aria-test" aria-expanded="false">Toggle</button>
            <div id="menu-aria-test" style="display: none;">Menu</div>
        """.trimIndent()

        val trigger = document.getElementById("trigger-aria") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-aria-test"}""")
        assertEquals("true", trigger.getAttribute("aria-expanded"), "Should update aria-expanded via aria-controls")
    }

    // ==========================================================================
    // Hamburger Menu Icon Tests
    // ==========================================================================

    @Test
    fun `updates hamburger menu icon on toggle`() {
        container.innerHTML = """
            <button data-hamburger-toggle="true" aria-controls="hamburger-menu" aria-expanded="false" aria-label="Open menu">
                <span class="material-icons">menu</span>
            </button>
            <div id="hamburger-menu" style="display: none;">Menu</div>
        """.trimIndent()

        val button = container.querySelector("[data-hamburger-toggle='true']") as HTMLElement
        val icon = button.querySelector(".material-icons") as HTMLElement

        assertEquals("menu", icon.textContent, "Icon should be 'menu' initially")
        assertEquals("Open menu", button.getAttribute("aria-label"), "aria-label should be 'Open menu'")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"hamburger-menu"}""")

        assertEquals("close", icon.textContent, "Icon should be 'close' when open")
        assertEquals("Close menu", button.getAttribute("aria-label"), "aria-label should be 'Close menu'")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"hamburger-menu"}""")

        assertEquals("menu", icon.textContent, "Icon should be 'menu' when closed")
        assertEquals("Open menu", button.getAttribute("aria-label"), "aria-label should be 'Open menu'")
    }

    // ==========================================================================
    // Disclosure Toggle (+/-) Tests
    // ==========================================================================

    @Test
    fun `updates disclosure plus-minus icon`() {
        container.innerHTML = """
            <button aria-controls="disclosure-content" aria-expanded="false">
                <span>+</span>
                <span>Show Details</span>
            </button>
            <div id="disclosure-content" style="display: none;">Details</div>
        """.trimIndent()

        val button = container.querySelector("[aria-controls='disclosure-content']") as HTMLElement
        val icon = button.querySelector("span:not(.material-icons)") as HTMLElement

        assertEquals("+", icon.textContent?.trim(), "Icon should be '+' initially")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"disclosure-content"}""")

        assertEquals("−", icon.textContent, "Icon should be '−' when expanded")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"disclosure-content"}""")

        assertEquals("+", icon.textContent, "Icon should be '+' when collapsed")
    }

    @Test
    fun `handles minus sign variations`() {
        // Test with regular hyphen-minus (U+002D)
        container.innerHTML = """
            <button aria-controls="disclosure-hyphen" aria-expanded="true">
                <span>-</span>
            </button>
            <div id="disclosure-hyphen" style="display: flex;">Content</div>
        """.trimIndent()

        val icon = container.querySelector("[aria-controls='disclosure-hyphen'] span") as HTMLElement
        assertEquals("-", icon.textContent?.trim(), "Should have hyphen-minus initially")

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"disclosure-hyphen"}""")

        assertEquals("+", icon.textContent, "Should change to plus when hidden")
    }

    // ==========================================================================
    // Edge Cases
    // ==========================================================================

    @Test
    fun `handles missing element gracefully`() {
        // Should not throw
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"nonexistent-element"}""")
        // Test passes if no exception is thrown
    }

    @Test
    fun `handles null-like JSON gracefully`() {
        // Valid JSON that represents null/empty - should not crash
        ClientDispatcher.dispatch("""{"type":""}""")
        ClientDispatcher.dispatch("""{}""")
        // Test passes if no exception propagates
    }

    @Test
    fun `handles unknown action type gracefully`() {
        // Unknown action type should not crash
        ClientDispatcher.dispatch("""{"type":"unknown","foo":"bar"}""")
        // Test passes if no exception propagates
    }

    @Test
    fun `handles rapid toggling`() {
        container.innerHTML = """
            <div id="rapid-menu" style="display: none;">Menu</div>
        """.trimIndent()

        val menu = document.getElementById("rapid-menu") as HTMLElement

        // Rapid fire 10 toggles
        repeat(10) {
            ClientDispatcher.dispatch("""{"type":"toggle","targetId":"rapid-menu"}""")
        }

        // After 10 toggles (even number), should be back to hidden
        assertEquals("none", menu.style.display, "Should be hidden after even number of toggles")

        // One more toggle to make it visible
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"rapid-menu"}""")
        assertEquals("flex", menu.style.display, "Should be visible after odd number of toggles")
    }

    @Test
    fun `handles multiple independent toggles`() {
        container.innerHTML = """
            <div id="menu-a" style="display: none;">Menu A</div>
            <div id="menu-b" style="display: flex;">Menu B</div>
            <div id="menu-c" style="display: none;">Menu C</div>
        """.trimIndent()

        val menuA = document.getElementById("menu-a") as HTMLElement
        val menuB = document.getElementById("menu-b") as HTMLElement
        val menuC = document.getElementById("menu-c") as HTMLElement

        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-a"}""")
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"menu-b"}""")

        assertEquals("flex", menuA.style.display, "Menu A should be visible")
        assertEquals("none", menuB.style.display, "Menu B should be hidden")
        assertEquals("none", menuC.style.display, "Menu C should still be hidden")
    }

    @Test
    fun `handles nested toggle targets`() {
        container.innerHTML = """
            <div id="outer-menu" style="display: flex;">
                <div id="inner-menu" style="display: none;">Inner Content</div>
            </div>
        """.trimIndent()

        val outer = document.getElementById("outer-menu") as HTMLElement
        val inner = document.getElementById("inner-menu") as HTMLElement

        // Toggle inner
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"inner-menu"}""")
        assertEquals("flex", outer.style.display, "Outer should remain visible")
        assertEquals("flex", inner.style.display, "Inner should be visible")

        // Toggle outer
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"outer-menu"}""")
        assertEquals("none", outer.style.display, "Outer should be hidden")
        assertEquals("flex", inner.style.display, "Inner style should remain")
    }

    @Test
    fun `handles element with no initial inline style`() {
        container.innerHTML = """
            <div id="no-style-menu">Menu with no display style</div>
        """.trimIndent()

        val menu = document.getElementById("no-style-menu") as HTMLElement

        // Element has no inline style, but computed style will be "block" (default for div)
        // When visible, we store the computed display in data-original-display
        // First toggle: hide the element
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"no-style-menu"}""")
        assertEquals("none", menu.style.display, "Should be hidden after toggle")

        // The original display (block) should have been stored
        val storedDisplay = menu.getAttribute("data-original-display")
        assertEquals("block", storedDisplay, "Should store computed 'block' as original")

        // Second toggle: show with stored original display
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"no-style-menu"}""")
        assertEquals("block", menu.style.display, "Should restore to stored 'block' display")
    }

    // ==========================================================================
    // Navigate Action Tests
    // ==========================================================================

    @Test
    fun `navigate action parses correctly`() {
        // We can't actually test navigation, but we can verify parsing works
        // by ensuring no errors are thrown
        ClientDispatcher.dispatch("""{"type":"nav","url":"https://example.com"}""")
        // Test passes if no exception
    }

    // ==========================================================================
    // Sync Mode Tests
    // ==========================================================================

    @Test
    fun `sync mode flushes DOM changes immediately`() {
        container.innerHTML = """
            <div id="sync-test" style="display: none;">Content</div>
        """.trimIndent()

        val menu = document.getElementById("sync-test") as HTMLElement

        // With sync mode enabled, changes should be immediate
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"sync-test"}""")

        // Check immediately - should already be updated
        assertEquals("flex", menu.style.display, "Should be visible immediately in sync mode")
    }
}
