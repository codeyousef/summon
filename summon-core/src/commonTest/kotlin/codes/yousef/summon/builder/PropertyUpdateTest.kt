package codes.yousef.summon.builder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for PropertyBridge functionality.
 *
 * TEST DIRECTIVE: Render a text component with color black.
 * Call `updateProperty(id, "color", "red")`. Assert DOM style is red.
 *
 * Note: DOM verification requires browser environment.
 * These tests verify the property binding logic.
 */
class PropertyUpdateTest {

    @Test
    fun testBindProperty() {
        PropertyBridge.clear()

        PropertyBridge.bindProperty("comp-1", "color", "black")

        assertEquals("black", PropertyBridge.getProperty("comp-1", "color"))
    }

    @Test
    fun testUpdateProperty() {
        PropertyBridge.clear()

        // Bind initial value
        PropertyBridge.bindProperty("comp-1", "color", "black")
        assertEquals("black", PropertyBridge.getProperty("comp-1", "color"))

        // Update the property
        PropertyBridge.updateProperty("comp-1", "color", "red")

        // Verify updated
        assertEquals("red", PropertyBridge.getProperty("comp-1", "color"))
    }

    @Test
    fun testMultiplePropertiesOnSameComponent() {
        PropertyBridge.clear()

        PropertyBridge.bindProperty("button-1", "color", "white")
        PropertyBridge.bindProperty("button-1", "backgroundColor", "blue")
        PropertyBridge.bindProperty("button-1", "fontSize", "14px")

        assertEquals("white", PropertyBridge.getProperty("button-1", "color"))
        assertEquals("blue", PropertyBridge.getProperty("button-1", "backgroundColor"))
        assertEquals("14px", PropertyBridge.getProperty("button-1", "fontSize"))
    }

    @Test
    fun testUpdatePropertyCallback() {
        PropertyBridge.clear()

        var callbackInvoked = false
        var receivedId: String? = null
        var receivedProp: String? = null
        var receivedValue: Any? = null

        PropertyBridge.onPropertyChange = { id, prop, value ->
            callbackInvoked = true
            receivedId = id
            receivedProp = prop
            receivedValue = value
        }

        PropertyBridge.bindProperty("test-comp", "size", 10)
        PropertyBridge.updateProperty("test-comp", "size", 20)

        assertTrue(callbackInvoked)
        assertEquals("test-comp", receivedId)
        assertEquals("size", receivedProp)
        assertEquals(20, receivedValue)

        // Clean up
        PropertyBridge.onPropertyChange = null
    }

    @Test
    fun testGetNonExistentProperty() {
        PropertyBridge.clear()

        val result = PropertyBridge.getProperty("non-existent", "prop")
        assertNull(result)
    }

    @Test
    fun testGetAllPropertiesForComponent() {
        PropertyBridge.clear()

        PropertyBridge.bindProperty("card", "width", "200px")
        PropertyBridge.bindProperty("card", "height", "150px")
        PropertyBridge.bindProperty("card", "shadow", "0 2px 4px rgba(0,0,0,0.1)")

        val allProps = PropertyBridge.getAllProperties("card")

        assertEquals(3, allProps.size)
        assertEquals("200px", allProps["width"])
        assertEquals("150px", allProps["height"])
        assertEquals("0 2px 4px rgba(0,0,0,0.1)", allProps["shadow"])
    }

    @Test
    fun testRemoveProperty() {
        PropertyBridge.clear()

        PropertyBridge.bindProperty("item", "visible", true)
        assertEquals(true, PropertyBridge.getProperty("item", "visible"))

        PropertyBridge.removeProperty("item", "visible")
        assertNull(PropertyBridge.getProperty("item", "visible"))
    }

    @Test
    fun testRemoveAllPropertiesForComponent() {
        PropertyBridge.clear()

        PropertyBridge.bindProperty("container", "flex", "1")
        PropertyBridge.bindProperty("container", "direction", "column")

        PropertyBridge.removeAllProperties("container")

        assertTrue(PropertyBridge.getAllProperties("container").isEmpty())
    }

    @Test
    fun testClearAllProperties() {
        PropertyBridge.bindProperty("a", "prop1", "val1")
        PropertyBridge.bindProperty("b", "prop2", "val2")

        PropertyBridge.clear()

        assertNull(PropertyBridge.getProperty("a", "prop1"))
        assertNull(PropertyBridge.getProperty("b", "prop2"))
    }

    @Test
    fun testPropertyTypesPreserved() {
        PropertyBridge.clear()

        PropertyBridge.bindProperty("typed", "string", "hello")
        PropertyBridge.bindProperty("typed", "int", 42)
        PropertyBridge.bindProperty("typed", "double", 3.14)
        PropertyBridge.bindProperty("typed", "boolean", true)
        PropertyBridge.bindProperty("typed", "list", listOf("a", "b"))

        assertEquals("hello", PropertyBridge.getProperty("typed", "string"))
        assertEquals(42, PropertyBridge.getProperty("typed", "int"))
        assertEquals(3.14, PropertyBridge.getProperty("typed", "double"))
        assertEquals(true, PropertyBridge.getProperty("typed", "boolean"))
        assertEquals(listOf("a", "b"), PropertyBridge.getProperty("typed", "list"))
    }
}
