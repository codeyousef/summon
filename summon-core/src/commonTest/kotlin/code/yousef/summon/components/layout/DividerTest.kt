package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer // Explicit import for shared mock
import code.yousef.summon.util.runTestComposable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertSame

/**
 * Tests for the Divider component
 */
class DividerTest {

    @Test
    fun testDividerWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            Divider()
            assertTrue(mockRenderer.renderDividerCalled, "renderDivider should have been called")
            assertEquals(Modifier(), mockRenderer.lastDividerModifierRendered, "Modifier should be the default")
        }
    }

    @Test
    fun testDividerWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("red")
        runTestComposable(mockRenderer) {
            Divider(modifier = customModifier)
            assertTrue(mockRenderer.renderDividerCalled, "renderDivider should have been called")
            assertSame(customModifier, mockRenderer.lastDividerModifierRendered, "Modifier should be the custom one")
        }
    }

    @Test
    fun testDividerWithCustomParameters() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("blue")
        runTestComposable(mockRenderer) {
            Divider(
                isVertical = true,
                thickness = "2px",
                color = "#FF0000",
                length = "50%",
                modifier = customModifier
            )
            assertTrue(mockRenderer.renderDividerCalled, "renderDivider should have been called")
            assertSame(customModifier, mockRenderer.lastDividerModifierRendered, "Modifier should be the custom one passed to Divider")

            // Note: Currently, the Divider implementation doesn't use the isVertical, thickness, color, or length parameters
            // to modify the Modifier in a way that would be separately testable here without inspecting the Modifier's properties.
            // These parameters are documented but not used in the implementation to alter the base modifier passed.
        }
    }
}