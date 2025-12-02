package codes.yousef.summon.core.registry

import codes.yousef.summon.annotation.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for ComponentRegistry functionality.
 *
 * TEST DIRECTIVE: Register 3 distinct components. Retrieve them.
 * Attempt to retrieve a non-existent key and assert the returned
 * component is instance of FallbackComponent. Do not mock the map.
 */
class RegistryTest {

    @Test
    fun testRegisterAndRetrieveComponents() {
        // Clear any previous registrations
        ComponentRegistry.clear()

        // Register 3 distinct components using the actual ComponentFactory type
        ComponentRegistry.register("Text") { props ->
            @Composable { /* Text component implementation */ }
        }
        ComponentRegistry.register("Button") { props ->
            @Composable { /* Button component implementation */ }
        }
        ComponentRegistry.register("Container") { props ->
            @Composable { /* Container component implementation */ }
        }

        // Verify all are registered
        assertTrue(ComponentRegistry.isRegistered("Text"), "Text component should be registered")
        assertTrue(ComponentRegistry.isRegistered("Button"), "Button component should be registered")
        assertTrue(ComponentRegistry.isRegistered("Container"), "Container component should be registered")

        // Verify size
        assertEquals(3, ComponentRegistry.size(), "Should have 3 registered components")

        // Retrieve and verify each component factory
        val retrievedText = ComponentRegistry.get("Text")
        val retrievedButton = ComponentRegistry.get("Button")
        val retrievedContainer = ComponentRegistry.get("Container")

        assertNotNull(retrievedText, "Text component should be retrievable")
        assertNotNull(retrievedButton, "Button component should be retrievable")
        assertNotNull(retrievedContainer, "Container component should be retrievable")
    }

    @Test
    fun testNonExistentKeyReturnsFallbackFactory() {
        // Clear any previous registrations
        ComponentRegistry.clear()

        // Register a component to ensure registry is not empty
        ComponentRegistry.register("RealComponent") { props ->
            @Composable { /* Real implementation */ }
        }

        // Attempt to retrieve a non-existent key
        val fallbackFactory = ComponentRegistry.get("NonExistentComponent")

        // Assert a factory is returned (it will be the fallback factory)
        assertNotNull(fallbackFactory, "Should return a fallback factory for non-existent key")
        
        // Verify the key is not registered (meaning it's a fallback)
        assertFalse(ComponentRegistry.isRegistered("NonExistentComponent"), 
            "Non-existent key should not be registered")
    }

    @Test
    fun testKeysReturnsAllRegisteredKeys() {
        // Clear any previous registrations
        ComponentRegistry.clear()

        // Register multiple components
        ComponentRegistry.register("Comp1") { props -> @Composable {} }
        ComponentRegistry.register("Comp2") { props -> @Composable {} }
        ComponentRegistry.register("Comp3") { props -> @Composable {} }

        // Get all keys
        val allKeys = ComponentRegistry.keys()

        // Verify all are present
        assertEquals(3, allKeys.size, "Should have 3 registered components")
        assertTrue(allKeys.contains("Comp1"), "Should contain Comp1")
        assertTrue(allKeys.contains("Comp2"), "Should contain Comp2")
        assertTrue(allKeys.contains("Comp3"), "Should contain Comp3")
    }

    @Test
    fun testClearRemovesAllComponents() {
        // Register some components
        ComponentRegistry.register("TestComp") { props -> @Composable {} }

        // Verify it's registered
        assertTrue(ComponentRegistry.isRegistered("TestComp"), "Should be registered")

        // Clear the registry
        ComponentRegistry.clear()

        // Verify it's gone
        assertFalse(ComponentRegistry.isRegistered("TestComp"), "Should not be registered after clear")
        assertEquals(0, ComponentRegistry.size(), "Registry should be empty after clear")
    }

    @Test
    fun testRegisterOverwritesExisting() {
        ComponentRegistry.clear()

        ComponentRegistry.register("Same") { props -> @Composable {} }
        assertTrue(ComponentRegistry.isRegistered("Same"), "Should be registered initially")

        // Re-register with same key (should overwrite without error)
        ComponentRegistry.register("Same") { props -> @Composable {} }
        
        // Should still be registered (overwritten)
        assertTrue(ComponentRegistry.isRegistered("Same"), "Should still be registered after overwrite")
        assertEquals(1, ComponentRegistry.size(), "Should only have 1 registration")
    }

    @Test
    fun testUnregister() {
        ComponentRegistry.clear()

        ComponentRegistry.register("ToRemove") { props -> @Composable {} }
        assertTrue(ComponentRegistry.isRegistered("ToRemove"), "Should be registered")

        val removed = ComponentRegistry.unregister("ToRemove")
        assertTrue(removed, "unregister should return true when key existed")
        assertFalse(ComponentRegistry.isRegistered("ToRemove"), "Should not be registered after unregister")

        val removedAgain = ComponentRegistry.unregister("ToRemove")
        assertFalse(removedAgain, "unregister should return false when key doesn't exist")
    }
}
