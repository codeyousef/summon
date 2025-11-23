package codes.yousef.summon.integration

import codes.yousef.summon.components.navigation.HamburgerMenu
import codes.yousef.summon.js.testutil.ensureJsDom
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.renderComposable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.RecomposerHolder
import codes.yousef.summon.runtime.ImmediateScheduler
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import kotlin.test.*

class HamburgerMenuTest {

    private lateinit var renderer: PlatformRenderer
    private lateinit var container: HTMLElement

    @BeforeTest
    fun setUp() {
        // Use immediate scheduler for testing to ensure synchronous updates
        RecomposerHolder.setScheduler(ImmediateScheduler())
        
        renderer = PlatformRenderer()
        ensureJsDom()
        container = document.createElement("div") as HTMLElement
        container.id = "hamburger-test-root"
        document.body?.appendChild(container)
    }

    @AfterTest
    fun tearDown() {
        container.parentElement?.removeChild(container)
    }

    @Test
    fun `hamburger menu toggles content visibility on click`() {
        renderComposable(renderer, {
            HamburgerMenu(
                modifier = Modifier().withAttribute("data-test-id", "hamburger-menu"),
                menuContent = {
                    // Content that should be toggled
                    renderer.renderHtmlTag(
                        "div",
                        Modifier().withAttribute("data-test-id", "menu-content")
                    ) {
                        renderer.renderText("Menu Items", Modifier())
                    }
                }
            )
        }, container)

        // Helper to find elements
        fun getButton(): HTMLElement? = container.querySelector("[data-test-id='hamburger-button']") as? HTMLElement
        fun getMenuContent(): HTMLElement? = container.querySelector("[data-test-id='menu-content']") as? HTMLElement

        // Initial state: Button should exist, content should be hidden
        val button = getButton()
        assertNotNull(button, "Hamburger button should be rendered")
        
        // Verify button accessibility attributes
        assertEquals("button", button.getAttribute("role"), "Element should have role='button'")
        assertEquals("0", button.getAttribute("tabindex"), "Element should be focusable (tabindex='0')")

        assertNull(getMenuContent(), "Menu content should be initially hidden")

        // Click to open
        button.click()
        
        // Verify content is visible
        assertNotNull(getMenuContent(), "Menu content should be visible after click")

        // Click to close
        button.click()

        // Verify content is hidden again
        assertNull(getMenuContent(), "Menu content should be hidden after second click")
    }
}
