package code.yousef.summon.components.display

import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderJvm
import kotlinx.html.stream.appendHTML
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * JVM-specific tests for the Text component.
 * These tests focus on HTML generation and escaping.
 */
class TextJvmTest {

    /**
     * Helper function to render a Text component to an HTML string.
     */
    private fun renderToHtml(component: Text): String {
        val stringBuilder = StringBuilder()
        val consumer = stringBuilder.appendHTML()
        component.renderJvm(consumer)
        return stringBuilder.toString()
    }

    @Test
    fun testBasicTextRendering() {
        // Arrange
        val text = "Hello, World!"
        val component = Text(text)

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("<span"))
        assertTrue(html.contains(">$text<"))
    }

    @Test
    fun testHtmlEscaping() {
        // Arrange
        val dangerousText = "<script>alert('XSS')</script>"
        val component = Text(dangerousText)

        // Act
        val html = renderToHtml(component)

        // Print the actual HTML output for debugging
        println("Actual HTML output: $html")

        // Assert
        // HTML should be properly escaped in the output
        assertFalse(html.contains(dangerousText), "Raw HTML should not appear in rendered output")
        assertTrue(html.contains("&lt;script&gt;"), "HTML tags should be escaped")
        
        // The kotlinx.html library doesn't escape single quotes in the default HTML renderer
        // It's actually acceptable HTML to leave single quotes unescaped when content is in a double-quoted attribute
        // So we'll just check that the angle brackets are properly escaped
        assertTrue(html.contains("alert('XSS')"), "Content inside tags should be preserved with unescaped quotes")
    }

    @Test
    fun testStyleModifiersApplied() {
        // Arrange
        val component = Text(
            text = "Styled Text",
            modifier = Modifier()
                .color("red")
                .fontSize("24px")
                .fontWeight("bold")
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("style="), "Style attribute should exist")
        assertTrue(html.contains("color:red"), "Color style should be applied")
        assertTrue(html.contains("font-size:24px"), "Font size style should be applied")
        assertTrue(html.contains("font-weight:bold"), "Font weight style should be applied")
    }

    @Test
    fun testEmptyTextRendering() {
        // Arrange
        val component = Text("")

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("<span"), "Span tag should be rendered")
        assertTrue(
            html.contains("></span>") || html.contains("> </span>"),
            "Empty content should be properly rendered"
        )
    }

    @Test
    fun testOverflowStylesApplied() {
        // Arrange
        val component = Text(
            text = "Lorem ipsum dolor sit amet...",
            overflow = "ellipsis",
            maxLines = 2
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("text-overflow:ellipsis"), "Text overflow style should be applied")
        assertTrue(html.contains("-webkit-line-clamp:2"), "Line clamp style should be applied")
        assertTrue(html.contains("-webkit-box-orient:vertical"), "Box orient style should be applied")
        assertTrue(html.contains("overflow:hidden"), "Overflow style should be applied")
        assertTrue(html.contains("display:-webkit-box"), "Display style should be applied")
    }

    @Test
    fun testAccessibilityAttributesApplied() {
        // Arrange
        val component = Text(
            text = "Accessible Text",
            role = "heading",
            ariaLabel = "Important heading",
            ariaDescribedBy = "description-id"
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("role=\"heading\""), "Role attribute should be applied")
        assertTrue(html.contains("aria-label=\"Important heading\""), "ARIA label should be applied")
        assertTrue(html.contains("aria-describedby=\"description-id\""), "ARIA describedby should be applied")
    }

    @Test
    fun testLongTextWithoutMaxLines() {
        // Arrange
        val longText = "This is a very long text that should not be truncated because maxLines is not set"
        val component = Text(
            text = longText,
            overflow = "ellipsis"
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("text-overflow:ellipsis"), "Text overflow style should be applied")
        assertFalse(html.contains("-webkit-line-clamp"), "Line clamp style should not be applied")
    }
} 