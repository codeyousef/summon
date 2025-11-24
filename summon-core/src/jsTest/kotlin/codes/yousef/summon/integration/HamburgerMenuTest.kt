package codes.yousef.summon.integration

import codes.yousef.summon.components.navigation.HamburgerMenu
import codes.yousef.summon.js.testutil.ensureJsDom
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ModifierExtras.withAttribute
import codes.yousef.summon.renderComposable
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.RecomposerHolder
import codes.yousef.summon.runtime.ImmediateScheduler
import kotlinx.browser.document
import kotlinx.browser.window
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
        // Pre-provide the renderer to the CompositionLocal before composition starts
        // This is needed because Recomposer.compose() accesses LocalPlatformRenderer.current
        // before the root composable has a chance to provide it
        LocalPlatformRenderer.provides(renderer)

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
    fun `hamburger menu renders with correct structure and attributes`() {
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

        // Button should exist
        val button = getButton()
        assertNotNull(button, "Hamburger button should be rendered")

        // Verify button accessibility attributes
        assertEquals("DIV", button.tagName, "Element should be a div styled as a button")
        assertEquals("button", button.getAttribute("role"), "Element should have role='button'")
        assertEquals("0", button.getAttribute("tabindex"), "Element should have tabindex='0'")
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded should be false initially")
        assertEquals("Open menu", button.getAttribute("aria-label"), "aria-label should indicate menu can be opened")

        // Verify data-action is set for client-side handling
        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "data-action attribute should be set for client-side toggle")
        // data-action should contain serialized UiAction with targetId referencing the menu
        assertTrue(dataAction.isNotEmpty(), "data-action should not be empty")

        // Verify hamburger toggle marker is set
        assertEquals("true", button.getAttribute("data-hamburger-toggle"), "data-hamburger-toggle should be true")

        // Menu content should be rendered but hidden (display: none)
        // The new architecture always renders content and toggles visibility client-side
        val menuContent = getMenuContent()
        assertNotNull(menuContent, "Menu content element should always be present in DOM")

        // Find the parent Box that has the display style and id
        val menuContainer = menuContent.parentElement as? HTMLElement
        assertNotNull(menuContainer, "Menu content should have a parent container")
        assertTrue(
            menuContainer.id.startsWith("hamburger-menu-"),
            "Menu container should have hamburger-menu-* id"
        )

        // Verify aria-controls points to the menu container
        assertEquals(
            menuContainer.id,
            button.getAttribute("aria-controls"),
            "aria-controls should reference the menu container id"
        )

        // Menu should be initially hidden via display: none
        val computedDisplay = window.getComputedStyle(menuContainer).display
        assertEquals("none", computedDisplay, "Menu content should be hidden initially")
    }
}
