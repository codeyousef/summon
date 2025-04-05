package code.yousef.summon.components.display

import code.yousef.summon.components.display.Text
import code.yousef.summon.verifyProperties
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * JS tests for the Text component
 */
class TextJsTest {
    @Test
    fun testBasicProperty() {
        // Test basic properties
        val text = "Hello World"
        val component = Text(text)
        
        assertTrue(component.verifyProperties(text))
    }
} 