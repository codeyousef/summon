package codes.yousef.summon.integration

import codes.yousef.summon.components.navigation.HamburgerMenu
import codes.yousef.summon.hydration.ClientDispatcher
import codes.yousef.summon.hydration.GlobalEventListener
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
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.MouseEventInit
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

        // Reset and initialize GlobalEventListener for event delegation tests
        GlobalEventListener.reset()
        GlobalEventListener.init()

        // Enable sync mode for ClientDispatcher to ensure DOM changes are immediate
        ClientDispatcher.syncMode = true
    }

    @AfterTest
    fun tearDown() {
        container.parentElement?.removeChild(container)
        // Reset sync mode to default after tests
        ClientDispatcher.syncMode = false
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

    @Test
    fun `hamburger menu button and content have matching IDs`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu Content", Modifier())
            }
        }, container)

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button")

        val ariaControls = button.getAttribute("aria-controls")
        assertNotNull(ariaControls, "Button should have aria-controls")
        assertTrue(ariaControls.startsWith("hamburger-menu-"), "aria-controls should be hamburger-menu-*")

        // Find the menu container by id
        val menuContainer = container.querySelector("#$ariaControls") as? HTMLElement
        assertNotNull(menuContainer, "Should find menu container with id matching aria-controls")

        // Verify data-action contains the same ID
        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "Button should have data-action")
        assertTrue(dataAction.contains(ariaControls), 
            "data-action should contain targetId '$ariaControls', got: $dataAction")
    }

    @Test
    fun `multiple hamburger menus have unique IDs`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu 1", Modifier())
            }
            HamburgerMenu {
                renderer.renderText("Menu 2", Modifier())
            }
        }, container)

        val buttons = container.querySelectorAll("[data-hamburger-toggle='true']")
        assertEquals(2, buttons.length, "Should have 2 hamburger buttons")

        val ids = mutableSetOf<String>()
        for (i in 0 until buttons.length) {
            val button = buttons.item(i) as HTMLElement
            val ariaControls = button.getAttribute("aria-controls")
            assertNotNull(ariaControls, "Button $i should have aria-controls")
            
            // Verify uniqueness
            assertTrue(ids.add(ariaControls), "ID '$ariaControls' should be unique")
            
            // Verify menu container exists with this id
            val menuContainer = container.querySelector("#$ariaControls")
            assertNotNull(menuContainer, "Menu container with id '$ariaControls' should exist")
            
            // Verify data-action contains correct id
            val dataAction = button.getAttribute("data-action")
            assertNotNull(dataAction, "Button should have data-action")
            assertTrue(dataAction.contains(ariaControls),
                "data-action should contain '$ariaControls'")
        }
    }

    @Test
    fun `data-action contains valid toggle action JSON`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu Content", Modifier())
            }
        }, container)

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button")

        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "Button should have data-action")

        // Parse and validate JSON
        val parsed = JSON.parse<dynamic>(dataAction)
        assertEquals("toggle", parsed.type as String, "type should be 'toggle'")
        
        val targetId = parsed.targetId as String
        assertTrue(targetId.startsWith("hamburger-menu-"), "targetId should be hamburger-menu-*")
        
        // Verify this targetId matches aria-controls
        val ariaControls = button.getAttribute("aria-controls")
        assertEquals(ariaControls, targetId, "data-action targetId should match aria-controls")
    }

    @Test
    fun `clicking hamburger button toggles menu visibility`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu Content", Modifier())
            }
        }, container)

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button")

        val ariaControls = button.getAttribute("aria-controls")
        assertNotNull(ariaControls, "Button should have aria-controls")

        val menuContainer = document.getElementById(ariaControls) as? HTMLElement
        assertNotNull(menuContainer, "Should find menu container by ID")

        // Initially hidden
        assertEquals("none", window.getComputedStyle(menuContainer).display, "Menu should be hidden initially")

        // Simulate click using ClientDispatcher directly (since GlobalEventListener uses document-level delegation)
        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "Button should have data-action")
        ClientDispatcher.dispatch(dataAction)

        // After click, menu should be visible
        assertEquals("block", menuContainer.style.display, "Menu should be visible after toggle")

        // Click again to close
        ClientDispatcher.dispatch(dataAction)

        // Menu should be hidden again
        assertEquals("none", menuContainer.style.display, "Menu should be hidden after second toggle")
    }

    @Test
    fun `toggling hamburger menu updates aria-expanded`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu Content", Modifier())
            }
        }, container)

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button")

        // Initially aria-expanded should be false
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded should be false initially")

        // Toggle menu
        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "Button should have data-action")
        ClientDispatcher.dispatch(dataAction)

        // After toggle, aria-expanded should be true
        assertEquals("true", button.getAttribute("aria-expanded"), "aria-expanded should be true after opening")

        // Toggle again
        ClientDispatcher.dispatch(dataAction)

        // After second toggle, aria-expanded should be false again
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded should be false after closing")
    }

    @Test
    fun `toggling hamburger menu updates icon`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu Content", Modifier())
            }
        }, container)

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button")

        val iconSpan = button.querySelector(".material-icons") as? HTMLElement
        assertNotNull(iconSpan, "Should find material icon in button")

        // Initially should show "menu" icon
        assertEquals("menu", iconSpan.textContent, "Icon should be 'menu' initially")

        // Toggle menu
        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "Button should have data-action")
        ClientDispatcher.dispatch(dataAction)

        // After toggle, should show "close" icon
        assertEquals("close", iconSpan.textContent, "Icon should be 'close' when menu is open")

        // Toggle again
        ClientDispatcher.dispatch(dataAction)

        // After second toggle, should show "menu" icon again
        assertEquals("menu", iconSpan.textContent, "Icon should be 'menu' after closing")
    }

    @Test
    fun `GlobalEventListener handles click on hamburger button`() {
        renderComposable(renderer, {
            HamburgerMenu {
                renderer.renderText("Menu Content", Modifier())
            }
        }, container)

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button")

        val ariaControls = button.getAttribute("aria-controls")
        assertNotNull(ariaControls, "Button should have aria-controls")

        val menuContainer = document.getElementById(ariaControls) as? HTMLElement
        assertNotNull(menuContainer, "Should find menu container by ID")
        
        // Verify button has data-action
        val dataAction = button.getAttribute("data-action")
        assertNotNull(dataAction, "Button should have data-action attribute")
        assertTrue(dataAction.contains(ariaControls), "data-action should reference menu ID")

        // Initially hidden
        assertEquals("none", window.getComputedStyle(menuContainer).display, "Menu should be hidden initially")

        // Create test container in body for this specific test to ensure event bubbling
        // Note: The container is already in document.body from setUp, but let's verify
        assertTrue(container.isConnected, "Container should be in the DOM")
        assertTrue(button.isConnected, "Button should be in the DOM")
        
        // Directly test ClientDispatcher first to ensure it works
        ClientDispatcher.dispatch(dataAction)
        assertEquals("block", menuContainer.style.display, "Menu should be visible after direct dispatch")
        
        // Toggle back
        ClientDispatcher.dispatch(dataAction)
        assertEquals("none", menuContainer.style.display, "Menu should be hidden after second dispatch")
        
        // Now test with event dispatch through GlobalEventListener
        val clickEvent = MouseEvent("click", MouseEventInit(bubbles = true, cancelable = true))
        button.dispatchEvent(clickEvent)

        // Event should be prevented
        assertTrue(clickEvent.defaultPrevented, "Click event should be prevented by GlobalEventListener")

        // Menu should now be visible
        assertEquals("block", menuContainer.style.display, "Menu should be visible after click")
    }

    @Test
    fun `SSR-rendered hamburger menu can be hydrated and toggled`() {
        // This test simulates what happens when the server renders HTML and the client hydrates it
        // The key is that the IDs in data-action must match the actual element IDs
        
        val menuId = "hamburger-menu-test-ssr"
        
        // Simulate SSR-rendered HTML (like what the JVM renderer produces)
        container.innerHTML = """
            <div style="display: flex; flex-direction: column;">
                <div data-hamburger-toggle="true" 
                     role="button" 
                     tabindex="0"
                     aria-label="Open menu"
                     aria-expanded="false"
                     aria-controls="$menuId"
                     data-action='{"type":"toggle","targetId":"$menuId"}'>
                    <span class="material-icons">menu</span>
                </div>
                <div id="$menuId" style="display: none;">
                    <span>Menu Content</span>
                </div>
            </div>
        """.trimIndent()

        val button = container.querySelector("[data-hamburger-toggle='true']") as? HTMLElement
        assertNotNull(button, "Should find hamburger button in SSR HTML")

        val menuContainer = document.getElementById(menuId) as? HTMLElement
        assertNotNull(menuContainer, "Should find menu container in SSR HTML")

        // Verify initial state
        assertEquals("none", menuContainer.style.display, "Menu should be hidden initially")
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded should be false")

        // Hydration: ClientDispatcher handles the toggle
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"$menuId"}""")

        // Verify menu is now visible
        assertEquals("block", menuContainer.style.display, "Menu should be visible after hydration toggle")
        assertEquals("true", button.getAttribute("aria-expanded"), "aria-expanded should be true")

        // Toggle back
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"$menuId"}""")

        // Verify menu is hidden again
        assertEquals("none", menuContainer.style.display, "Menu should be hidden after second toggle")
        assertEquals("false", button.getAttribute("aria-expanded"), "aria-expanded should be false")
    }

    @Test
    fun `SSR-rendered hamburger menu with mismatched IDs fails gracefully`() {
        // This test verifies what happens when IDs DON'T match (the bug we fixed)
        
        val buttonTargetId = "hamburger-menu-wrong"
        val actualMenuId = "hamburger-menu-correct"
        
        // Simulate buggy SSR output with mismatched IDs
        container.innerHTML = """
            <div style="display: flex; flex-direction: column;">
                <div data-hamburger-toggle="true" 
                     role="button" 
                     tabindex="0"
                     aria-label="Open menu"
                     aria-expanded="false"
                     aria-controls="$buttonTargetId"
                     data-action='{"type":"toggle","targetId":"$buttonTargetId"}'>
                    <span class="material-icons">menu</span>
                </div>
                <div id="$actualMenuId" style="display: none;">
                    <span>Menu Content</span>
                </div>
            </div>
        """.trimIndent()

        val menuContainer = document.getElementById(actualMenuId) as? HTMLElement
        assertNotNull(menuContainer, "Should find actual menu container")

        // Initial state
        assertEquals("none", menuContainer.style.display, "Menu should be hidden initially")

        // Try to toggle - this should fail to find the element because IDs don't match
        ClientDispatcher.dispatch("""{"type":"toggle","targetId":"$buttonTargetId"}""")

        // Menu should STILL be hidden because the targetId doesn't exist
        assertEquals("none", menuContainer.style.display, 
            "Menu should still be hidden because targetId '$buttonTargetId' doesn't match actual id '$actualMenuId'")
    }
}
