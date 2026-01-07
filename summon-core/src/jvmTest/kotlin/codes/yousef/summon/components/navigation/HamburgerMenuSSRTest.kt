package codes.yousef.summon.components.navigation

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * JVM-specific tests for HamburgerMenu component.
 * These tests verify the SSR output to ensure IDs are consistent between
 * the hamburger button and menu content.
 * 
 * Note: HamburgerMenu uses random IDs (hamburger-menu-NNNNNN) to avoid counter
 * synchronization issues during SSR when composable functions may be invoked
 * multiple times without a proper composer context.
 */
class HamburgerMenuSSRTest {

    // Pattern for hamburger menu IDs: hamburger-menu-<6 digits>
    private val menuIdPattern = """hamburger-menu-\d+"""

    @Test
    fun `single hamburger menu has consistent IDs`() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            HamburgerMenu {
                Text("Menu Content", Modifier())
            }
        }

        // Extract the aria-controls value from the button
        val ariaControlsMatch = Regex("""aria-controls="($menuIdPattern)"""").find(html)
        assertNotNull(ariaControlsMatch, "Should find aria-controls attribute")
        val ariaControlsId = ariaControlsMatch.groupValues[1]

        // Extract the id from the menu content div
        val menuIdMatch = Regex("""id="($menuIdPattern)"""").find(html)
        assertNotNull(menuIdMatch, "Should find menu id attribute")
        val menuId = menuIdMatch.groupValues[1]

        // They should be the same
        assertEquals(ariaControlsId, menuId, 
            "aria-controls ($ariaControlsId) should match menu id ($menuId)")

        // Extract targetId from data-action
        val dataActionMatch = Regex("""data-action='([^']+)'""").find(html)
            ?: Regex("""data-action="([^"]+)"""").find(html)
        assertNotNull(dataActionMatch, "Should find data-action attribute")
        val dataAction = dataActionMatch.groupValues[1]
            .replace("&quot;", "\"")  // Handle HTML-encoded quotes
        
        assertTrue(dataAction.contains(ariaControlsId), 
            "data-action should contain the same targetId ($ariaControlsId), got: $dataAction")
    }

    @Test
    fun `multiple hamburger menus have unique but consistent IDs`() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            HamburgerMenu {
                Text("Menu 1 Content", Modifier())
            }
            HamburgerMenu {
                Text("Menu 2 Content", Modifier())
            }
        }

        // Find all aria-controls values
        val ariaControlsMatches = Regex("""aria-controls="($menuIdPattern)"""")
            .findAll(html).toList()
        assertEquals(2, ariaControlsMatches.size, "Should have 2 hamburger menus")

        val ariaControlsIds = ariaControlsMatches.map { it.groupValues[1] }

        // Find all menu ids
        val menuIdMatches = Regex("""id="($menuIdPattern)"""")
            .findAll(html).toList()
        assertEquals(2, menuIdMatches.size, "Should have 2 menu content divs")

        val menuIds = menuIdMatches.map { it.groupValues[1] }

        // Verify uniqueness
        assertEquals(2, ariaControlsIds.toSet().size, "aria-controls IDs should be unique")
        assertEquals(2, menuIds.toSet().size, "Menu IDs should be unique")

        // Verify consistency - each aria-controls should have a matching menu id
        ariaControlsIds.forEach { ariaControlsId ->
            assertTrue(menuIds.contains(ariaControlsId),
                "aria-controls '$ariaControlsId' should have a matching menu id. Menu IDs: $menuIds")
        }

        // Extract and verify data-action for each menu
        val dataActionMatches = Regex("""data-action='([^']+)'""").findAll(html).toList()
            .ifEmpty { Regex("""data-action="([^"]+)"""").findAll(html).toList() }

        assertEquals(2, dataActionMatches.size, "Should have 2 data-action attributes")

        dataActionMatches.forEachIndexed { index, match ->
            val dataAction = match.groupValues[1].replace("&quot;", "\"")
            val expectedId = ariaControlsIds[index]
            assertTrue(dataAction.contains(expectedId),
                "data-action[$index] should contain targetId '$expectedId', got: $dataAction")
        }
    }

    @Test
    fun `hamburger menu button has correct accessibility attributes`() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            HamburgerMenu(
                isOpen = false,
                onToggle = {}
            ) {
                Text("Menu Content", Modifier())
            }
        }

        // Check role="button"
        assertTrue(html.contains("""role="button""""), "Button should have role='button'")
        
        // Check tabindex="0"
        assertTrue(html.contains("""tabindex="0""""), "Button should have tabindex='0'")
        
        // Check aria-expanded="false" (since isOpen = false)
        assertTrue(html.contains("""aria-expanded="false""""), 
            "Button should have aria-expanded='false' when menu is closed")
        
        // Check aria-label
        assertTrue(html.contains("""aria-label="Open menu""""), 
            "Button should have aria-label='Open menu' when closed")
        
        // Check data-hamburger-toggle marker
        assertTrue(html.contains("""data-hamburger-toggle="true""""),
            "Button should have data-hamburger-toggle marker")
    }

    @Test
    fun `hamburger menu content is hidden when closed`() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            HamburgerMenu(
                isOpen = false,
                onToggle = {}
            ) {
                Text("Menu Content", Modifier())
            }
        }

        // Find the menu content container (has hamburger-menu-* id)
        val menuMatch = Regex("""<div[^>]*id="$menuIdPattern"[^>]*>""").find(html)
        assertNotNull(menuMatch, "Should find menu content container")
        
        val menuDiv = menuMatch.value
        assertTrue(menuDiv.contains("display") && menuDiv.contains("none"),
            "Menu content should have display: none when closed. Got: $menuDiv")
    }

    @Test
    fun `hamburger menu content is visible when open`() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            HamburgerMenu(
                isOpen = true,
                onToggle = {}
            ) {
                Text("Menu Content", Modifier())
            }
        }

        // Check aria-expanded is true when open
        assertTrue(html.contains("""aria-expanded="true""""), 
            "Button should have aria-expanded='true' when menu is open")
        
        // Check aria-label changes when open
        assertTrue(html.contains("""aria-label="Close menu""""), 
            "Button should have aria-label='Close menu' when open")

        // Find the menu content container
        val menuMatch = Regex("""<div[^>]*id="$menuIdPattern"[^>]*>""").find(html)
        assertNotNull(menuMatch, "Should find menu content container")
        
        val menuDiv = menuMatch.value
        assertTrue(menuDiv.contains("display") && menuDiv.contains("block"),
            "Menu content should have display: block when open. Got: $menuDiv")
    }

    @Test
    fun `controlled and uncontrolled hamburger menus work the same`() {
        val renderer = PlatformRenderer()

        // Uncontrolled version
        val htmlUncontrolled = renderer.renderComposableRoot {
            HamburgerMenu {
                Text("Uncontrolled Menu", Modifier())
            }
        }

        // Controlled version (closed)
        val htmlControlled = renderer.renderComposableRoot {
            HamburgerMenu(
                isOpen = false,
                onToggle = {}
            ) {
                Text("Controlled Menu", Modifier())
            }
        }

        // Both should have the same structure - role, tabindex, aria attributes
        assertTrue(htmlUncontrolled.contains("""role="button""""))
        assertTrue(htmlControlled.contains("""role="button""""))
        
        assertTrue(htmlUncontrolled.contains("""tabindex="0""""))
        assertTrue(htmlControlled.contains("""tabindex="0""""))
        
        assertTrue(htmlUncontrolled.contains("""aria-expanded="false""""))
        assertTrue(htmlControlled.contains("""aria-expanded="false""""))
        
        assertTrue(htmlUncontrolled.contains("""data-hamburger-toggle="true""""))
        assertTrue(htmlControlled.contains("""data-hamburger-toggle="true""""))
    }

    @Test
    fun `data-action contains valid JSON with targetId`() {
        val renderer = PlatformRenderer()

        val html = renderer.renderComposableRoot {
            HamburgerMenu {
                Text("Menu Content", Modifier())
            }
        }

        // Extract data-action - it may be single or double quoted
        val dataActionMatch = Regex("""data-action='(\{[^']+\})'""").find(html)
            ?: Regex("""data-action="(\{[^"]+\})"""").find(html)
        assertNotNull(dataActionMatch, "Should find data-action attribute with JSON")
        
        val json = dataActionMatch.groupValues[1]
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
        
        // Parse the JSON
        val parsed = kotlinx.serialization.json.Json.parseToJsonElement(json)
        assertTrue(parsed is kotlinx.serialization.json.JsonObject, "data-action should be a JSON object")

        // kotlinx.serialization uses "type" field for sealed class discriminator
        val typeField = parsed["type"]
        assertNotNull(typeField, "JSON should have 'type' field. Got: $parsed")
        assertEquals("\"toggle\"", typeField.toString(), "type should be 'toggle'")
        
        // Should have "targetId" field
        val targetIdField = parsed["targetId"]
        assertNotNull(targetIdField, "JSON should have 'targetId' field")
        assertTrue(targetIdField.toString().contains("hamburger-menu-"),
            "targetId should contain 'hamburger-menu-'")
    }

    @Test
    fun `stress test - many hamburger menus have unique consistent IDs`() {
        val renderer = PlatformRenderer()
        val menuCount = 10

        val html = renderer.renderComposableRoot {
            repeat(menuCount) { index ->
                HamburgerMenu {
                    Text("Menu $index Content", Modifier())
                }
            }
        }

        // Find all aria-controls
        val ariaControlsIds = Regex("""aria-controls="($menuIdPattern)"""")
            .findAll(html).map { it.groupValues[1] }.toList()
        
        // Find all menu ids  
        val menuIds = Regex("""id="($menuIdPattern)"""")
            .findAll(html).map { it.groupValues[1] }.toList()

        assertEquals(menuCount, ariaControlsIds.size, "Should have $menuCount aria-controls")
        assertEquals(menuCount, menuIds.size, "Should have $menuCount menu ids")

        // All should be unique
        assertEquals(menuCount, ariaControlsIds.toSet().size, "All aria-controls should be unique")
        assertEquals(menuCount, menuIds.toSet().size, "All menu ids should be unique")

        // Each aria-controls should match exactly one menu id
        assertEquals(ariaControlsIds.toSet(), menuIds.toSet(),
            "aria-controls IDs should exactly match menu IDs")
    }
}
