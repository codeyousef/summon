package codes.yousef.summon.platform

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenderToStringTest {

    @Test
    fun testBasic() {
        // Initialize the platform renderer - using the JVM-specific implementation
        val renderer = codes.yousef.summon.runtime.PlatformRenderer()

        // Render a simple composable
        val result = RenderToString.basic(renderer) {
            // Empty composable
        }

        // Check output structure
        assertTrue(result.contains("<html"), "Should contain HTML tag")
        assertTrue(result.contains("<body"), "Should contain body tag")
        // Note: kotlinx.html may or may not add DOCTYPE depending on configuration
    }

    @Test
    fun testWithMetadata() {
        // Initialize the platform renderer - using the JVM-specific implementation
        val renderer = codes.yousef.summon.runtime.PlatformRenderer()

        // Create metadata
        val metadata = PageMetadata(
            title = "Test Page",
            description = "Test page description",
            customHeadElements = listOf(
                "<link rel=\"stylesheet\" href=\"styles.css\">",
                "<script src=\"app.js\"></script>"
            )
        )

        // Render with metadata
        val result = RenderToString.withMetadata(
            renderer = renderer,
            metadata = metadata
        ) {
            // Empty composable
        }

        // Check output structure
        assertTrue(result.contains("<title>Test Page</title>"), "Should contain title")
        assertTrue(
            result.contains("<meta name=\"description\" content=\"Test page description\">"),
            "Should contain description"
        )
        assertTrue(result.contains("<meta name=\"viewport\""), "Should contain viewport meta")
        assertTrue(result.contains("<meta charset=\"UTF-8\""), "Should contain charset meta")
        assertTrue(
            result.contains("<link rel=\"stylesheet\" href=\"styles.css\">"),
            "Should contain stylesheet link"
        )
        assertTrue(
            result.contains("<script src=\"app.js\"></script>"),
            "Should contain script tag"
        )
    }

    @Test
    fun testWithoutDocType() {
        // Initialize the platform renderer
        val renderer = PlatformRenderer()

        // Create metadata without doctype
        val metadata = PageMetadata(
            includeDocType = false
        )

        // Render with metadata
        val result = RenderToString.withMetadata(
            renderer = renderer,
            metadata = metadata
        ) {
            // Empty composable
        }

        // Check output structure
        assertFalse(result.contains("<!DOCTYPE html>"), "Should not contain DOCTYPE declaration")
        assertTrue(result.contains("<html"), "Should contain HTML tag")
    }

    @Test
    fun testCompatibilityFunction() {
        // Use the compatibility function
        val result = renderToString {
            // Empty composable
        }

        // Check output structure
        assertTrue(result.contains("<html"), "Should contain HTML tag")
        assertTrue(result.contains("<body"), "Should contain body tag")
        // The compatibility function uses basic() which doesn't add DOCTYPE
        assertFalse(result.contains("<!DOCTYPE html>"), "Compatibility function should not add DOCTYPE")
    }
} 