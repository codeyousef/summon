package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.renderToHtmlString
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * JVM-specific tests for the Image component.
 * These tests focus on HTML generation and accessibility attributes.
 */
class ImageJvmTest {

    /**
     * Helper function to render an Image component to an HTML string.
     */
    private fun renderToHtml(component: Image): String {
        return component.renderToHtmlString()
    }

    @Test
    fun testBasicImageRendering() {
        // Arrange
        val src = "https://example.com/image.jpg"
        val alt = "Example image"
        val component = Image(src, alt)

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("<img"), "Should render an img element")
        assertTrue(html.contains("src=\"$src\""), "Image should have correct src attribute")
        assertTrue(html.contains("alt=\"$alt\""), "Image should have correct alt attribute")
    }

    @Test
    fun testLoadingAttributeApplied() {
        // Arrange
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            loading = ImageLoading.LAZY
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("loading=\"lazy\""), "Image should have loading attribute applied")
    }

    @Test
    fun testSizingAttributesApplied() {
        // Arrange
        val width = "400px"
        val height = "300px"
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            width = width,
            height = height
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("width=\"$width\""), "Image should have width attribute applied")
        assertTrue(html.contains("height=\"$height\""), "Image should have height attribute applied")
    }

    @Test
    fun testStyleModifiersApplied() {
        // Arrange
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            modifier = Modifier()
                .borderRadius("8px")
                .objectFit("cover")
                .maxWidth("100%")
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("style="), "Style attribute should exist")
        assertTrue(html.contains("border-radius:8px"), "Border radius style should be applied")
        assertTrue(html.contains("object-fit:cover"), "Object-fit style should be applied")
        assertTrue(html.contains("max-width:100%"), "Max width style should be applied")
    }

    @Test
    fun testAccessibilityAttributes() {
        // Arrange
        val contentDescription = "A detailed description of the image content"
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            contentDescription = contentDescription
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("alt=\"Example image\""), "Alt text should be applied")
        // In a full implementation, verify aria-describedby attribute is present
        // and a hidden element with the full description exists
    }

    @Test
    fun testResponsiveAttributes() {
        // Arrange
        val component = Image(
            src = "https://example.com/image.jpg",
            alt = "Example image",
            modifier = Modifier()
                .maxWidth("100%")
                .height("auto")
        )

        // Act
        val html = renderToHtml(component)

        // Assert
        assertTrue(html.contains("style="), "Style attribute should exist")
        assertTrue(html.contains("max-width:100%"), "Max width style should be applied")
        assertTrue(html.contains("height:auto"), "Height style should be applied")
    }
} 