package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KeyboardNavigationTest {
    
    @Test
    fun testFocusableExtension() {
        // Test with default tab index
        val defaultModifier = KeyboardNavigation.run { Modifier().focusable() }
        assertEquals("0", defaultModifier.styles["tabindex"], "Default tabindex should be 0")
        
        // Test with custom tab index
        val customModifier = KeyboardNavigation.run { Modifier().focusable(tabIndex = -1) }
        assertEquals("-1", customModifier.styles["tabindex"], "Custom tabindex should be applied")
        
        // Test chaining with existing modifier
        val baseModifier = Modifier().style("color", "red")
        val chainedModifier = KeyboardNavigation.run { baseModifier.focusable(tabIndex = 1) }
        assertEquals("red", chainedModifier.styles["color"], "Original styles should be preserved")
        assertEquals("1", chainedModifier.styles["tabindex"], "Tabindex should be applied")
    }
    
    @Test
    fun testFocusTrapExtension() {
        val trapId = "test-trap"
        val modifier = KeyboardNavigation.run { Modifier().focusTrap(trapId) }
        
        assertEquals(trapId, modifier.styles["data-focus-trap"], "Focus trap ID should be applied")
        assertEquals("true", modifier.styles["data-focus-trap-active"], "Focus trap should be active")
        
        // Test chaining
        val baseModifier = Modifier().style("color", "blue")
        val chainedModifier = KeyboardNavigation.run { baseModifier.focusTrap(trapId) }
        assertEquals("blue", chainedModifier.styles["color"], "Original styles should be preserved")
        assertEquals(trapId, chainedModifier.styles["data-focus-trap"], "Focus trap ID should be applied")
    }
    
    @Test
    fun testAutoFocusExtension() {
        val modifier = KeyboardNavigation.run { Modifier().autoFocus() }
        assertEquals("true", modifier.styles["autofocus"], "Autofocus should be applied")
        
        // Test chaining
        val baseModifier = Modifier().style("color", "green")
        val chainedModifier = KeyboardNavigation.run { baseModifier.autoFocus() }
        assertEquals("green", chainedModifier.styles["color"], "Original styles should be preserved")
        assertEquals("true", chainedModifier.styles["autofocus"], "Autofocus should be applied")
    }
    
    @Test
    fun testKeyboardHandlersExtension() {
        // Create a map of key handlers
        val keyHandlers = mapOf(
            KeyboardKeys.ENTER to { println("Enter pressed") },
            KeyboardKeys.SPACE to { println("Space pressed") }
        )
        
        val modifier = KeyboardNavigation.run { Modifier().keyboardHandlers(keyHandlers) }
        
        // Check that the correct number of handlers is recorded
        assertEquals("2", modifier.styles["data-kbd-handlers-count"], "Handler count should be correct")
        
        // Check that key mappings are recorded
        assertTrue(modifier.styles.containsKey("data-kbd-key-0"), "First key should be recorded")
        assertTrue(modifier.styles.containsKey("data-kbd-key-1"), "Second key should be recorded")
        assertTrue(modifier.styles.containsKey("data-kbd-handler-0"), "First handler should be recorded")
        assertTrue(modifier.styles.containsKey("data-kbd-handler-1"), "Second handler should be recorded")
        
        // Check that the keys are correctly mapped
        // Note: We can't guarantee the order of entries in the map, so we check both possibilities
        val key0 = modifier.styles["data-kbd-key-0"]
        val key1 = modifier.styles["data-kbd-key-1"]
        assertTrue(
            (key0 == KeyboardKeys.ENTER && key1 == KeyboardKeys.SPACE) ||
            (key0 == KeyboardKeys.SPACE && key1 == KeyboardKeys.ENTER),
            "Keys should be correctly mapped"
        )
    }
    
    @Test
    fun testKeyboardNavigationExtensionWithDefaults() {
        // Test with default configuration
        val config = KeyboardNavigation.KeyboardNavigationConfig()
        val modifier = KeyboardNavigation.run { Modifier().keyboardNavigation(config) }
        
        // Should only apply the tabindex
        assertEquals("0", modifier.styles["tabindex"], "Default tabindex should be applied")
        assertNull(modifier.styles["autofocus"], "Autofocus should not be applied by default")
        assertNull(modifier.styles["data-focus-trap"], "Focus trap should not be applied by default")
        assertNull(modifier.styles["data-kbd-handlers-count"], "Key handlers should not be applied by default")
    }
    
    @Test
    fun testKeyboardNavigationExtensionWithFullConfig() {
        // Create a configuration with all options enabled
        val keyHandlers = mapOf(
            KeyboardKeys.ENTER to { println("Enter pressed") }
        )
        
        val config = KeyboardNavigation.KeyboardNavigationConfig(
            trapFocus = true,
            useArrowKeys = true,
            autoFocus = true,
            tabIndex = 1,
            keyHandlers = keyHandlers
        )
        
        val modifier = KeyboardNavigation.run { Modifier().keyboardNavigation(config) }
        
        // Check that all options are applied
        assertEquals("1", modifier.styles["tabindex"], "Custom tabindex should be applied")
        assertEquals("true", modifier.styles["autofocus"], "Autofocus should be applied")
        assertTrue(modifier.styles.containsKey("data-focus-trap"), "Focus trap should be applied")
        assertEquals("true", modifier.styles["data-focus-trap-active"], "Focus trap should be active")
        
        // Check that key handlers are applied
        // We expect 5 handlers: 1 custom + 4 arrow keys
        assertEquals("5", modifier.styles["data-kbd-handlers-count"], "Handler count should include custom and arrow keys")
        
        // Check that arrow keys are included
        val allKeys = (0 until 5).mapNotNull { modifier.styles["data-kbd-key-$it"] }
        assertTrue(allKeys.contains(KeyboardKeys.ARROW_UP), "Arrow up should be included")
        assertTrue(allKeys.contains(KeyboardKeys.ARROW_DOWN), "Arrow down should be included")
        assertTrue(allKeys.contains(KeyboardKeys.ARROW_LEFT), "Arrow left should be included")
        assertTrue(allKeys.contains(KeyboardKeys.ARROW_RIGHT), "Arrow right should be included")
        assertTrue(allKeys.contains(KeyboardKeys.ENTER), "Custom key should be included")
    }
    
    @Test
    fun testKeyboardKeysConstants() {
        // Verify that keyboard key constants have the expected values
        assertEquals("Tab", KeyboardKeys.TAB)
        assertEquals("Enter", KeyboardKeys.ENTER)
        assertEquals(" ", KeyboardKeys.SPACE)
        assertEquals("Escape", KeyboardKeys.ESCAPE)
        assertEquals("ArrowUp", KeyboardKeys.ARROW_UP)
        assertEquals("ArrowDown", KeyboardKeys.ARROW_DOWN)
        assertEquals("ArrowLeft", KeyboardKeys.ARROW_LEFT)
        assertEquals("ArrowRight", KeyboardKeys.ARROW_RIGHT)
        assertEquals("Home", KeyboardKeys.HOME)
        assertEquals("End", KeyboardKeys.END)
        assertEquals("PageUp", KeyboardKeys.PAGE_UP)
        assertEquals("PageDown", KeyboardKeys.PAGE_DOWN)
    }
}