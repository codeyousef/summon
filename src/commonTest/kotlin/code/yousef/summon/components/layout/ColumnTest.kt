package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runTestComposable
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Tests for the Column component
 */
class ColumnTest {

    @Test
    fun testColumnWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            Column {
                // Empty content
            }
            assertTrue(mockRenderer.renderColumnCalled, "renderColumn should have been called")
            // Verify that fillMaxSize is NOT applied to the modifier
            val expectedStyles = Modifier().styles
            assertEquals(expectedStyles, mockRenderer.lastColumnModifierRendered?.styles, "Modifier should not have fillMaxSize applied")
            assertNotNull(mockRenderer.lastColumnContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testColumnWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("blue")
        runTestComposable(mockRenderer) {
            Column(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderColumnCalled, "renderColumn should have been called")
            // Verify that the custom modifier is used as-is without applying fillMaxSize
            val expectedStyles = customModifier.styles
            assertEquals(expectedStyles, mockRenderer.lastColumnModifierRendered?.styles, "Modifier should be the custom one without fillMaxSize applied")
            assertNotNull(mockRenderer.lastColumnContentRendered, "Content should not be null")
        }
    }
}
