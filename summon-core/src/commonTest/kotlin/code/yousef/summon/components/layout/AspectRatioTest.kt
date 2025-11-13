package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the AspectRatio component
 */
class AspectRatioTest {

    @Test
    fun testAspectRatioWithDefaultModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context using runTestComposable
        runComposableTest(mockRenderer) {
            // Call the AspectRatio component with default modifier
            AspectRatio(ratio = 16f / 9f) {
                // Empty content
            }
        }

        // Verify that renderAspectRatio was called with the correct parameters
        assertTrue(mockRenderer.renderAspectRatioCalled, "renderAspectRatio should have been called")
        assertEquals(16f / 9f, mockRenderer.lastAspectRatioRatioRendered, "Ratio should be 16/9")
        assertNotNull(mockRenderer.lastAspectRatioModifierRendered, "Modifier should not be null")
        assertNotNull(mockRenderer.lastAspectRatioContentRendered, "Content should not be null")
    }

    @Test
    fun testAspectRatioWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        
        // Create a custom modifier
        val customModifier = Modifier().background("blue")

        // Set up the composition context using runTestComposable
        runComposableTest(mockRenderer) {
            // Call the AspectRatio component with custom modifier
            AspectRatio(ratio = 16f / 9f, modifier = customModifier) {
                // Empty content
            }
        }

        // Verify that renderAspectRatio was called with the correct parameters
        assertTrue(mockRenderer.renderAspectRatioCalled, "renderAspectRatio should have been called")
        assertEquals(16f / 9f, mockRenderer.lastAspectRatioRatioRendered, "Ratio should be 16/9")
        assertSame(customModifier, mockRenderer.lastAspectRatioModifierRendered, "Modifier should be the custom one")
        assertNotNull(mockRenderer.lastAspectRatioContentRendered, "Content should not be null")
    }
    
    @Test
    fun testAspectRatioWithDifferentRatios() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context using runTestComposable
        runComposableTest(mockRenderer) {
            // Test with square aspect ratio (1:1)
            AspectRatio(ratio = 1f) {
                // Empty content
            }
            
            // Verify that renderAspectRatio was called with the correct parameters
            assertTrue(mockRenderer.renderAspectRatioCalled, "renderAspectRatio should have been called")
            assertEquals(1f, mockRenderer.lastAspectRatioRatioRendered, "Ratio should be 1.0")
        }

        runComposableTest(mockRenderer) { 
            // Test with portrait aspect ratio (9:16)
            AspectRatio(ratio = 9f / 16f) {
                // Empty content
            }
            
            // Verify that renderAspectRatio was called with the correct parameters
            assertTrue(mockRenderer.renderAspectRatioCalled, "renderAspectRatio should have been called")
            assertEquals(9f / 16f, mockRenderer.lastAspectRatioRatioRendered, "Ratio should be 9/16")
        }
    }
}