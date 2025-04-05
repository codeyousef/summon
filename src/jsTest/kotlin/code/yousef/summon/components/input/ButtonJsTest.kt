package code.yousef.summon.components.input

import code.yousef.summon.components.input.Button
import code.yousef.summon.verifyProperties
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * JS tests for the Button component
 */
class ButtonJsTest {
    @Test
    fun testBasicProperty() {
        // Test basic properties
        val label = "Click Me"
        val component = Button(label)
        
        assertTrue(component.verifyProperties(label))
    }
    
    @Test
    fun testDisabledProperty() {
        val label = "Disabled Button"
        val component = Button(label, disabled = true)
        
        assertTrue(component.verifyProperties(label, expectedDisabled = true))
    }
} 