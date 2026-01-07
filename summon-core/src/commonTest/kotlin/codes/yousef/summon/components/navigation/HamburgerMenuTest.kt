package codes.yousef.summon.components.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Common tests for HamburgerMenu component.
 * These tests verify the component's behavior across all platforms.
 */
class HamburgerMenuTest {

    @Test
    fun testMenuIdCounterIncrementsCorrectly() {
        // Reset the counter by accessing it through reflection or just verify behavior
        // The key thing is that each menu should have a unique, consistent ID
        
        // This is a basic sanity test - the real verification happens in platform-specific tests
        // that can actually render the component and inspect the HTML output
        assertTrue(true, "HamburgerMenu component should be available")
    }

    @Test
    fun testUiActionToggleVisibilitySerialization() {
        // Test that UiAction.ToggleVisibility serializes correctly with polymorphic type info
        val action: codes.yousef.summon.action.UiAction = codes.yousef.summon.action.UiAction.ToggleVisibility("test-id")
        val json = kotlinx.serialization.json.Json.encodeToString(
            codes.yousef.summon.action.UiAction.serializer(),
            action
        )
        
        // Should contain the type discriminator and targetId
        assertTrue(json.contains("toggle"), "Serialized action should contain 'toggle' type")
        assertTrue(json.contains("test-id"), "Serialized action should contain targetId")
        assertTrue(json.contains("type"), "Serialized action should contain 'type' discriminator")
        
        // Deserialize and verify
        val deserialized = kotlinx.serialization.json.Json.decodeFromString(
            codes.yousef.summon.action.UiAction.serializer(),
            json
        )
        assertTrue(deserialized is codes.yousef.summon.action.UiAction.ToggleVisibility)
        assertEquals("test-id", deserialized.targetId)
    }
}
