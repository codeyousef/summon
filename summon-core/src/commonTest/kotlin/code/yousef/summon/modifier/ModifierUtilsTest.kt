package codes.yousef.summon.modifier

import codes.yousef.summon.modifier.AttributeModifiers.getAttribute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModifierUtilsTest {

    @Test
    fun testAttributeModifierAddsCorrectly() {
        val modifier = Modifier()
        val attrName = "data-test-id"
        val attrValue = "my-component-123"

        val modified = modifier.attribute(attrName, attrValue)

        // Verify the internal map contains the attribute
        assertEquals(attrValue, modified.attributes[attrName], "Attribute not stored correctly in attributes map")
    }

    @Test
    fun testGetAttributeRetrievesCorrectly() {
        val modifier = Modifier()
        val attrName = "aria-label"
        val attrValue = "Close Button"

        val modified = modifier.attribute(attrName, attrValue)

        // Use the getAttribute extension function to retrieve
        assertEquals(attrValue, modified.getAttribute(attrName), "getAttribute did not retrieve the correct value")
    }

    @Test
    fun testGetAttributeReturnsNullForNonExistent() {
        val modifier = Modifier()
            .attribute("data-test", "value") // Add some other attribute

        // Try to get an attribute that wasn't added
        assertNull(modifier.getAttribute("non-existent-attr"), "getAttribute should return null for non-existent attribute")
    }

    @Test
    fun testAttributeChaining() {
        val modifier = Modifier()
            .attribute("id", "main-content")
            .attribute("role", "main")
            .attribute("data-loading", "false")

        assertEquals("main-content", modifier.getAttribute("id"))
        assertEquals("main", modifier.getAttribute("role"))
        assertEquals("false", modifier.getAttribute("data-loading"))
        assertNull(modifier.getAttribute("class"))
    }

    @Test
    fun testAttributeOverwriting() {
        val modifier = Modifier()
            .attribute("status", "initial")
            .attribute("status", "updated") // Overwrite the previous value

        assertEquals("updated", modifier.getAttribute("status"), "Attribute value should be overwritten")
    }
} 