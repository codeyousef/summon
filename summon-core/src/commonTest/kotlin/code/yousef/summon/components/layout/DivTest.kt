package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runTestComposable
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertNotNull

/**
 * Tests for the Div and Span components
 */
class DivTest {

    @Test
    fun testDivWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            Div {
                // Empty content
            }
            assertTrue(mockRenderer.renderDivCalled, "renderDiv should have been called")
            assertEquals(Modifier(), mockRenderer.lastDivModifierRendered, "Modifier should be the default")
            assertNotNull(mockRenderer.lastDivContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testDivWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("red")
        runTestComposable(mockRenderer) {
            Div(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderDivCalled, "renderDiv should have been called")
            assertSame(customModifier, mockRenderer.lastDivModifierRendered, "Modifier should be the custom one")
            assertNotNull(mockRenderer.lastDivContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testSpanWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            Span {
                // Empty content
            }
            assertTrue(mockRenderer.renderSpanCalled, "renderSpan should have been called")
            assertEquals(Modifier(), mockRenderer.lastSpanModifierRendered, "Modifier should be the default")
            assertNotNull(mockRenderer.lastSpanContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testSpanWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("blue")
        runTestComposable(mockRenderer) {
            Span(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderSpanCalled, "renderSpan should have been called")
            assertSame(customModifier, mockRenderer.lastSpanModifierRendered, "Modifier should be the custom one")
            assertNotNull(mockRenderer.lastSpanContentRendered, "Content should not be null")
        }
    }
}