package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the Column component
 */
class ColumnTest {

    @Test
    fun testColumnWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            Column {
                // Empty content
            }
            assertTrue(mockRenderer.renderColumnCalled, "renderColumn should have been called")
            // Verify that default flex styles are applied
            val expectedStyles = Modifier()
                .style("display", "flex")
                .style("flex-direction", "column")
                .styles
            assertEquals(
                expectedStyles,
                mockRenderer.lastColumnModifierRendered?.styles,
                "Modifier should have default flex styles applied"
            )
            assertNotNull(mockRenderer.lastColumnContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testColumnWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("blue")
        runComposableTest(mockRenderer) {
            Column(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderColumnCalled, "renderColumn should have been called")
            // Verify that default flex styles are applied along with custom modifier
            val expectedStyles = Modifier()
                .style("display", "flex")
                .style("flex-direction", "column")
                .background("blue")
                .styles
            assertEquals(
                expectedStyles,
                mockRenderer.lastColumnModifierRendered?.styles,
                "Modifier should have both default flex styles and custom modifier applied"
            )
            assertNotNull(mockRenderer.lastColumnContentRendered, "Content should not be null")
        }
    }
}
