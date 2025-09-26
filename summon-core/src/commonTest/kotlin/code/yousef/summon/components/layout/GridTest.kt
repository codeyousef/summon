package code.yousef.summon.components.layout

import code.yousef.summon.modifier.BorderStyle
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the Grid component
 */
class GridTest {

    @Test
    fun testGridWithDefaultParameters() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            Grid(columns = "1fr 1fr") {
                // Empty content
            }
            assertTrue(mockRenderer.renderGridCalled, "renderGrid should have been called")
            assertNotNull(mockRenderer.lastGridContentRendered, "Content should not be null")

            val styles = mockRenderer.lastGridModifierRendered?.styles ?: emptyMap()
            assertEquals("grid", styles["display"], "display should be set to grid")
            assertEquals("1fr 1fr", styles["grid-template-columns"], "grid-template-columns should be set to '1fr 1fr'")
            assertEquals("auto", styles["grid-template-rows"], "grid-template-rows should be set to 'auto' by default")
            assertEquals("0", styles["gap"], "gap should be set to '0' by default")
            assertTrue(!styles.containsKey("grid-template-areas"), "grid-template-areas should not be set by default")
        }
    }

    @Test
    fun testGridWithAllParameters() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("red")
        runComposableTest(mockRenderer) {
            Grid(
                columns = "repeat(3, 1fr)",
                rows = "auto 1fr auto",
                gap = "10px 20px",
                areas = "'header header header' 'sidebar content content' 'footer footer footer'",
                modifier = customModifier,
                content = {
                    // Empty content
                }
            )
            assertTrue(mockRenderer.renderGridCalled, "renderGrid should have been called")
            assertNotNull(mockRenderer.lastGridContentRendered, "Content should not be null")

            val styles = mockRenderer.lastGridModifierRendered?.styles ?: emptyMap()
            assertEquals("grid", styles["display"], "display should be set to grid")
            assertEquals("repeat(3, 1fr)", styles["grid-template-columns"], "grid-template-columns should be set correctly")
            assertEquals("auto 1fr auto", styles["grid-template-rows"], "grid-template-rows should be set correctly")
            assertEquals("10px 20px", styles["gap"], "gap should be set correctly")
            assertEquals("'header header header' 'sidebar content content' 'footer footer footer'", styles["grid-template-areas"], "grid-template-areas should be set correctly")
            assertTrue(styles.containsKey("background-color"), "background style should be preserved")
            assertEquals("red", styles["background-color"], "background should be set to red")
        }
    }

    @Test
    fun testGridWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier()
            .background("blue")
            .padding("10px")
            .border("1px", BorderStyle.Solid.value, "black")

        runComposableTest(mockRenderer) {
            Grid(
                columns = "1fr 2fr",
                modifier = customModifier,
                content = {
                    // Empty content
                }
            )
            assertTrue(mockRenderer.renderGridCalled, "renderGrid should have been called")
            assertNotNull(mockRenderer.lastGridContentRendered, "Content should not be null")

            val styles = mockRenderer.lastGridModifierRendered?.styles ?: emptyMap()
            assertEquals("grid", styles["display"], "display should be set to grid")
            assertEquals("1fr 2fr", styles["grid-template-columns"], "grid-template-columns should be set correctly")
            assertTrue(styles.containsKey("background-color"), "background style should be preserved")
            assertEquals("blue", styles["background-color"], "background should be set to blue")
            assertTrue(styles.containsKey("padding"), "padding style should be preserved")
            assertEquals("10px", styles["padding"], "padding should be set to 10px")
            assertTrue(styles.containsKey("border"), "border style should be preserved")
        }
    }
}
