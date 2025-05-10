package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runTestComposable
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertSame

/**
 * Tests for the Card component
 */
class CardTest {

    @Test
    fun testCardWithDefaultModifier() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            Card {
                // Empty content
            }
            assertTrue(mockRenderer.renderCardCalled, "renderCard should have been called")
            assertNotNull(mockRenderer.lastCardModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastCardModifierRendered?.styles ?: emptyMap()
            assertTrue(styles.containsKey("box-shadow"), "Default modifier should have box-shadow style")
            assertTrue(styles.containsKey("border-radius"), "Default modifier should have border-radius style")
            assertNotNull(mockRenderer.lastCardContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testCardWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("yellow")
        runTestComposable(mockRenderer) {
            Card(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderCardCalled, "renderCard should have been called")
            assertNotNull(mockRenderer.lastCardModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastCardModifierRendered?.styles ?: emptyMap()
            assertTrue(styles.containsKey("background-color"), "Modifier should have background-color style")
            assertEquals("yellow", styles["background-color"], "Background should be yellow")
            assertTrue(styles.containsKey("box-shadow"), "Modifier should still have box-shadow style")
            assertTrue(styles.containsKey("border-radius"), "Modifier should still have border-radius style")
            assertNotNull(mockRenderer.lastCardContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testCardWithCustomElevationAndBorderRadius() {
        val mockRenderer = MockPlatformRenderer()
        runTestComposable(mockRenderer) {
            Card(
                elevation = "5px",
                borderRadius = "10px"
            ) {
                // Empty content
            }
            assertTrue(mockRenderer.renderCardCalled, "renderCard should have been called")
            assertNotNull(mockRenderer.lastCardModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastCardModifierRendered?.styles ?: emptyMap()
            assertTrue(styles.containsKey("box-shadow"), "Modifier should have box-shadow style for elevation")
            assertEquals("10px", styles["border-radius"], "Border radius should be 10px")
            assertNotNull(mockRenderer.lastCardContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testCardWithOnClickHandler() {
        val mockRenderer = MockPlatformRenderer()
        var onClickCalled = false
        val onClickLambda = { onClickCalled = true }
        runTestComposable(mockRenderer) {
            Card(onClick = onClickLambda) {
                // Empty content
            }
            assertTrue(mockRenderer.renderCardCalled, "renderCard should have been called")
            assertNotNull(mockRenderer.lastCardModifierRendered, "Modifier should not be null")
            val clickEventHandler = mockRenderer.lastCardModifierRendered?.events?.get("click")
            assertNotNull(clickEventHandler, "Modifier should have a click event handler")
            assertSame(onClickLambda, mockRenderer.lastCardModifierRendered?.onClickLambda, "onClick lambda should be the same instance")
            assertNotNull(mockRenderer.lastCardContentRendered, "Content should not be null")
        }
    }
}
