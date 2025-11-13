package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the Div and Span components
 */
class DivTest {

    @Test
    fun testDivWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
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
        runComposableTest(mockRenderer) {
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
        runComposableTest(mockRenderer) {
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
        runComposableTest(mockRenderer) {
            Span(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderSpanCalled, "renderSpan should have been called")
            assertSame(customModifier, mockRenderer.lastSpanModifierRendered, "Modifier should be the custom one")
            assertNotNull(mockRenderer.lastSpanContentRendered, "Content should not be null")
        }
    }
}