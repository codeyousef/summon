package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.*

/**
 * Tests for the LazyColumn component
 */
class LazyColumnTest {

    @Test
    fun testLazyColumnWithDefaultParameters() {
        val mockRenderer = MockPlatformRenderer()
        runComposableTest(mockRenderer) {
            LazyColumn {
                // Empty content
            }
            assertTrue(mockRenderer.renderLazyColumnCalled, "renderLazyColumn should have been called")

            val styles = mockRenderer.lastLazyColumnModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "overflow-y")
            assertEquals("auto", styles["overflow-y"])
            assertContains(styles, "display")
            assertEquals("flex", styles["display"])
            assertContains(styles, "flex-direction")
            assertEquals("column", styles["flex-direction"])

            assertNotNull(mockRenderer.lastLazyColumnContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testLazyColumnWithCustomModifier() {
        val mockRenderer = MockPlatformRenderer()
        val customModifier = Modifier().background("green")

        runComposableTest(mockRenderer) {
            LazyColumn(modifier = customModifier) {
                // Empty content
            }
            assertTrue(mockRenderer.renderLazyColumnCalled, "renderLazyColumn should have been called")

            val styles = mockRenderer.lastLazyColumnModifierRendered?.styles ?: emptyMap()
            assertContains(styles, "background-color")
            assertEquals("green", styles["background-color"])
            assertContains(styles, "overflow-y")
            assertEquals("auto", styles["overflow-y"])

            assertNotNull(mockRenderer.lastLazyColumnContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testLazyColumnWithItems() {
        val mockRenderer = MockPlatformRenderer()

        runComposableTest(mockRenderer) {
            LazyColumn {
                item {
                    // Item content
                }
                items(listOf("A", "B", "C")) { item ->
                    // Item content
                }
            }
            assertTrue(mockRenderer.renderLazyColumnCalled, "renderLazyColumn should have been called")
            assertNotNull(mockRenderer.lastLazyColumnContentRendered, "Content should not be null")
        }
    }

    @Test
    fun testLazyListState() {
        // Create a LazyListState
        val state = LazyListState()

        // Test initial values
        assertEquals(0f, state.scrollPosition)

        // Test updating scroll position
        state.updateScrollPosition(100f)
        assertEquals(100f, state.scrollPosition)

        // Test setting item size
        state.setItemSize(60f)
        assertEquals(60f, state.itemSize)

        // Test setting overscroll items
        state.setOverscrollItems(3)
        assertEquals(3, state.overscrollItems)

        // Test setting container size
        state.setContainerSize(800f)
        assertEquals(800f, state.containerSize)

        // Test getting visible item range
        val range = state.getVisibleItemRange(800f, 100)
        assertEquals(0, range.first) // (100 / 60) - 3 = 1 - 3 = -2, coerced to 0
        assertEquals(18, range.last)  // Actual implementation returns 18

        // Test data attributes
        val attributes = state.getDataAttributes(100)
        assertEquals("100", attributes["data-total-items"])
        assertEquals("60.0", attributes["data-item-size"])
        assertEquals("3", attributes["data-overscroll-items"])
        assertEquals("true", attributes["data-lazy-container"])
    }
}
