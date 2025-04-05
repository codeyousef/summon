package code.yousef.summon.components.display

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Minimal tests for the Text component's basic properties.
 */
class TextTest {
    @Test
    fun testBasicProperty() {
        val text = "Test"
        val component = Text(text)
        assertEquals(text, component.text)
    }
} 