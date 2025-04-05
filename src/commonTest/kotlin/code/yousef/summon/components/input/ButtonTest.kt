package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Common tests for the Button component's basic properties.
 */
class ButtonTest {
    @Test
    fun testButtonLabel() {
        // Arrange
        val label = "Click Me"
        val component = Button(label)
        
        // Assert
        assertEquals(label, component.label, "Button should store the provided label")
    }
    
    @Test
    fun testClickHandler() {
        // Arrange
        var clicked = false
        val component = Button(
            label = "Click Me",
            onClick = { clicked = true }
        )
        
        // Act - simulate a click by directly calling the handler
        component.onClick(Any())
        
        // Assert
        assertTrue(clicked, "Click handler should be invoked when button is clicked")
    }
    
    @Test
    fun testDisabledState() {
        // Arrange
        val component = Button(
            label = "Disabled Button",
            disabled = true
        )
        
        // Assert
        assertTrue(component.disabled, "Button should be in disabled state when disabled is true")
    }
    
    @Test
    fun testButtonVariant() {
        // Arrange
        val component = Button(
            label = "Danger Button",
            variant = Button.ButtonVariant.DANGER
        )
        
        // Assert
        assertEquals(Button.ButtonVariant.DANGER, component.variant, "Button should use the specified variant")
    }
} 