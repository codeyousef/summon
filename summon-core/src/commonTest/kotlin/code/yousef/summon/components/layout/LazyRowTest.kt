package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the LazyRow component
 */
class LazyRowTest {

    @Test
    fun testLazyRowWithDefaultParameters() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            LazyRow {
                // Empty content
            }
            assertTrue(mockRenderer.renderLazyRowCalled, "renderLazyRow should have been called")

            val styles = mockRenderer.lastLazyRowModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "overflow-x")
            assertEquals("auto", styles["overflow-x"])
            assertContains(styles, "display")
            assertEquals("flex", styles["display"])
            assertContains(styles, "flex-direction")
            assertEquals("row", styles["flex-direction"])

            assertNotNull(mockRenderer.lastLazyRowContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testLazyRowWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("green")

        runComposableTest(mockRenderer) {
            LazyRow(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderLazyRowCalled, "renderLazyRow should have been called")

            val styles = mockRenderer.lastLazyRowModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "background-color")
            assertEquals("green", styles["background-color"])
            assertContains(styles, "overflow-x")
            assertEquals("auto", styles["overflow-x"])

            assertNotNull(mockRenderer.lastLazyRowContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testLazyRowWithItems() {
        val mockRenderer = MockPlatformRenderer()

        runComposableTest(mockRenderer) {
            LazyRow {
                item {
                    // Item content
                }
                items(listOf("A", "B", "C")) { item ->
                    // Item content
                }
            }
            assertTrue(mockRenderer.renderLazyRowCalled, "renderLazyRow should have been called")
            assertNotNull(mockRenderer.lastLazyRowContentRendered, "Content should not be null")
        }
    }
}