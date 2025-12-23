package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the Box component
 */
class BoxTest {

    @Test
    fun testBoxWithDefaultModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context using runTestComposable
        runComposableTest(mockRenderer) {
            // Call the Box component with default modifier
            Box {
                // Empty content
            }

            // Verify that renderBox was called
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")
            // Verify that the default modifier was used
            // assertEquals(Modifier(), mockRenderer.lastBoxModifierRendered, "Modifier should be the default") // Comparing new Modifier() instances can be tricky.
            assertNotNull(mockRenderer.lastBoxModifierRendered, "Modifier should not be null") // Updated property
            // Check if it's an empty modifier by looking at its styles (if Modifier() creates an empty styles map)
            assertEquals(
                emptyMap<String, String>(),
                mockRenderer.lastBoxModifierRendered?.styles,
                "Modifier should be the default (empty styles)"
            )
            assertNotNull(mockRenderer.lastBoxContentRendered, "Content should not be null") // Check content was passed
        }
    }

    @Test
    fun testBoxWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().background("red")

        // Set up the composition context using runTestComposable
        runComposableTest(mockRenderer) {
            // Call the Box component with custom modifier
            Box(modifier = customModifier) {
                // Empty content
            }

            // Verify that renderBox was called
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")
            // Verify that the custom modifier was used
            // Using assertSame as the exact instance should be passed through
            assertSame(
                customModifier,
                mockRenderer.lastBoxModifierRendered,
                "Modifier should be the custom one"
            ) // Updated property
            assertNotNull(mockRenderer.lastBoxContentRendered, "Content should not be null") // Check content was passed
        }
    }
}
