package code.yousef.summon.components.input

import code.yousef.summon.components.display.Icon
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderToHtmlString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * JVM-specific tests for the Button component.
 * These tests focus on HTML generation and accessibility attributes.
 */
class ButtonJvmTest {

    /**
     * Helper function to render a Button component to an HTML string.
     */
    private fun renderToHtml(component: Button): String {
        return component.renderToHtmlString()
    }

    @Test
    fun testBasicButtonRendering() {
        // Arrange
        val label = "Click Me"
        val component = Button(label)

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("<button"), "Should render a button element")
        assertTrue(html.contains(">$label<"), "Button should contain the label text")
        assertTrue(html.contains("data-summon-click=\"true\""), "Button should have data attribute for click handling")
    }

    @Test
    fun testButtonStylesApplied() {
        // Arrange
        val component = Button(
            label = "Styled Button",
            modifier = Modifier()
                .backgroundColor("blue")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("style="), "Style attribute should exist")
        assertTrue(html.contains("background-color:blue"), "Background color should be applied")
        assertTrue(html.contains("color:white"), "Text color should be applied")
        assertTrue(html.contains("padding:10px 20px"), "Padding should be applied")
        assertTrue(html.contains("border-radius:4px"), "Border radius should be applied")
    }

    @Test
    fun testDisabledButtonAttributes() {
        // Arrange
        val component = Button(
            label = "Disabled Button",
            disabled = true
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("disabled"), "Disabled attribute should be applied to disabled buttons")
        // In a real implementation, you would also verify CSS classes for disabled styling
    }

    @Test
    fun testAccessibilityAttributes() {
        // Arrange
        val component = Button(
            label = "Accessible Button",
            modifier = Modifier()
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        // In a real implementation, the button should have proper ARIA attributes
        assertTrue(html.contains("role=\"button\"") || html.contains("<button"), 
            "Button should have correct semantic role")
    }

    @Test
    fun testButtonWithIcon() {
        // Arrange - using the proper Icon class
        val component = Button(
            label = "Download",
            icon = Icon.Download, // Use predefined icon from companion object
            iconPosition = Button.IconPosition.START
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("<button"), "Should render a button element")
        assertTrue(html.contains(">Download<"), "Button should contain the label text")
        // In a full implementation, verify icon rendering as well
    }
} 