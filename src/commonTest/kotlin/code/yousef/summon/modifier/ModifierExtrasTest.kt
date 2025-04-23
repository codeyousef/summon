package code.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ModifierExtrasTest {

    @Test
    fun testOnClick() {
        val handler = { println("Clicked!") }
        val modifier = with(ModifierExtras) {
            Modifier().onClick(handler)
        }
        // Note: This implementation sets "onclick" as a style, which might be incorrect.
        // Testing the behavior as implemented.
        assertEquals(handler.toString(), modifier.styles["onclick"], "ModifierExtras.onClick failed.")
    }

    @Test
    fun testPointerEvents() {
        val value = "none"
        val modifier = with(ModifierExtras) {
            Modifier().pointerEvents(value)
        }
        assertEquals(value, modifier.styles["pointer-events"], "ModifierExtras.pointerEvents failed.")
    }

    @Test
    fun testAttributeAndGetAttribute() {
        val attrName = "data-test"
        val attrValue = "my-value"
        val modifier = with(ModifierExtras) {
            Modifier().attribute(attrName, attrValue)
        }
        
        // Check internal storage
        assertEquals(attrValue, modifier.styles["__attr:$attrName"], "Internal attribute storage failed.")

        // Check retrieval using getAttribute
        val retrievedValue = with(ModifierExtras) {
            modifier.getAttribute(attrName)
        }
        assertEquals(attrValue, retrievedValue, "ModifierExtras.getAttribute failed.")

        // Check non-existent attribute
        val nonExistent = with(ModifierExtras) {
             modifier.getAttribute("non-existent")
        }
        assertNull(nonExistent, "Retrieving non-existent attribute failed.")
    }
} 