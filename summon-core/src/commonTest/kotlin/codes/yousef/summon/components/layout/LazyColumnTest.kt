package codes.yousef.summon.components.layout

import kotlin.test.*

/**
 * Tests for the LazyColumn component
 */
class LazyColumnTest {


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
