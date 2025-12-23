package codes.yousef.summon.modifier

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModifierTest {

    @Test
    fun testModifierChaining() {
        val modifier = Modifier()
            .style("color", "red")
            .style("background-color", "blue")

        assertEquals("red", modifier.styles["color"])
        assertEquals("blue", modifier.styles["background-color"])
    }

    @Test
    fun testImmutability() {
        val base = Modifier().style("color", "red")
        val extended = base.style("color", "blue")

        assertEquals("red", base.styles["color"])
        assertEquals("blue", extended.styles["color"])
    }

    @Test
    fun testAttributes() {
        val modifier = ModifierImpl(attributes = mapOf("id" to "test-id"))
        assertEquals("test-id", modifier.attributes["id"])
    }
    
    @Test
    fun testFactoryFunction() {
        val modifier = Modifier()
        assertTrue(modifier.styles.isEmpty())
        assertTrue(modifier.attributes.isEmpty())
    }
}
