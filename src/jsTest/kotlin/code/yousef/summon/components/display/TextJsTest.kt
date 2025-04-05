package code.yousef.summon.components.display

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Minimal JS tests for the Text component to avoid ClassCastException
 */
class TextJsTest {
    @Test
    fun testBasicProperty() {
        val text = "Test"
        val component = Text(text)
        assertEquals(text, component.text)
    }
} 