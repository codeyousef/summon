package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.* // Covers PlatformRenderer, LocalPlatformRenderer
import code.yousef.summon.runtime.MockPlatformRenderer // Explicit import for shared mock
import code.yousef.summon.util.runTestComposable // Import shared utility
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertNotNull

/**
 * Tests for the Box component
 */
class BoxTest {

    @Test
    fun testBoxWithDefaultModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context using runTestComposable
        runTestComposable(mockRenderer) {
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
            assertEquals(emptyMap<String, String>(), mockRenderer.lastBoxModifierRendered?.styles, "Modifier should be the default (empty styles)")
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
        runTestComposable(mockRenderer) {
            // Call the Box component with custom modifier
            Box(modifier = customModifier) {
                // Empty content
            }

            // Verify that renderBox was called
            assertTrue(mockRenderer.renderBoxCalled, "renderBox should have been called")
            // Verify that the custom modifier was used
            // Using assertSame as the exact instance should be passed through
            assertSame(customModifier, mockRenderer.lastBoxModifierRendered, "Modifier should be the custom one") // Updated property
            assertNotNull(mockRenderer.lastBoxContentRendered, "Content should not be null") // Check content was passed
        }
    }
}
