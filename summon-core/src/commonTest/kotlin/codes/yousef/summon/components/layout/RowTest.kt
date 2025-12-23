package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the Row component
 */
class RowTest {

    @Test
    fun testRowWithDefaultParameters() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            Row {
                // Empty content
            }
            assertTrue(mockRenderer.renderRowCalled, "renderRow should have been called")

            val styles = mockRenderer.lastRowModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "display")
            assertEquals("flex", styles["display"])
            assertContains(styles, "flex-direction")
            assertEquals("row", styles["flex-direction"])

            assertNotNull(mockRenderer.lastRowContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testRowWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("green")

        runComposableTest(mockRenderer) {
            Row(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderRowCalled, "renderRow should have been called")

            val styles = mockRenderer.lastRowModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "background-color")
            assertEquals("green", styles["background-color"])
            // Default row styles should still be there if not overridden by customModifier
            assertContains(styles, "display")
            assertEquals("flex", styles["display"])
            assertContains(styles, "flex-direction")
            assertEquals("row", styles["flex-direction"])

            assertNotNull(mockRenderer.lastRowContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testAlignmentAndArrangementEnums() {
        // Test that the Alignment and Arrangement enums exist and have the expected values
        assertEquals(3, Alignment.Vertical.values().size, "Alignment.Vertical should have 3 values")
        assertEquals(3, Alignment.Horizontal.values().size, "Alignment.Horizontal should have 3 values")
        assertEquals(6, Arrangement.Horizontal.values().size, "Arrangement.Horizontal should have 6 values")
        assertEquals(6, Arrangement.Vertical.values().size, "Arrangement.Vertical should have 6 values")

        // Test specific enum values
        assertEquals(Alignment.Vertical.Top, Alignment.Vertical.valueOf("Top"))
        assertEquals(Alignment.Horizontal.Start, Alignment.Horizontal.valueOf("Start"))
        assertEquals(Arrangement.Horizontal.SpaceBetween, Arrangement.Horizontal.valueOf("SpaceBetween"))
        assertEquals(Arrangement.Vertical.SpaceAround, Arrangement.Vertical.valueOf("SpaceAround"))
    }
}