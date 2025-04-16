package code.yousef.summon.platform

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.JvmPlatformRenderer
import code.yousef.summon.platform.RenderToString
import code.yousef.summon.platform.PageMetadata
import code.yousef.summon.platform.renderToString
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class RenderToStringTest {
    
    @Test
    fun testBasic() {
        // Initialize the platform renderer
        val renderer = JvmPlatformRenderer()
        
        // Render a simple composable
        val result = RenderToString.basic(renderer) {
            // Empty composable
        }
        
        // Check output structure
        assertTrue(result.contains("<html"), "Should contain HTML tag")
        assertTrue(result.contains("<body"), "Should contain body tag")
        assertTrue(result.contains("<!DOCTYPE html>"), "Should contain DOCTYPE declaration")
    }
    
    @Test
    fun testWithMetadata() {
        // Initialize the platform renderer
        val renderer = JvmPlatformRenderer()
        
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
        assertTrue(result.contains("<meta name=\"description\" content=\"Test page description\">"), 
            "Should contain description")
        assertTrue(result.contains("<meta name=\"viewport\""), "Should contain viewport meta")
        assertTrue(result.contains("<meta charset=\"UTF-8\""), "Should contain charset meta")
        assertTrue(result.contains("<link rel=\"stylesheet\" href=\"styles.css\">"), 
            "Should contain stylesheet link")
        assertTrue(result.contains("<script src=\"app.js\"></script>"), 
            "Should contain script tag")
    }
    
    @Test
    fun testWithoutDocType() {
        // Initialize the platform renderer
        val renderer = JvmPlatformRenderer()
        
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
        assertTrue(result.contains("<!DOCTYPE html>"), "Should contain DOCTYPE declaration")
    }
} 