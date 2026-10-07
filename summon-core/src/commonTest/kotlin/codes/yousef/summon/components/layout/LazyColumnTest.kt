package codes.yousef.summon.components.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LazyColumnTest {
    @Test
    fun fixedRowsHaveBoundedBeginningMiddleAndEndWindows() {
        val state = LazyListState()
        state.setItemSize(40f)
        state.setOverscrollItems(10)
        state.setContainerSize(600f)

        val beginning = state.layout(600f, 100_000)
        assertEquals(0..25, beginning.range)
        assertEquals(0f, beginning.leadingExtent)
        assertEquals(4_000_000f, beginning.totalExtent)
        assertTrue(beginning.range.count() <= 36)

        state.updateScrollPosition(2_000_000f)
        val middle = state.layout(600f, 100_000)
        assertEquals(49_990..50_025, middle.range)
        assertEquals(1_999_600f, middle.leadingExtent)
        assertEquals(1_998_960f, middle.trailingExtent)
        assertEquals(36, middle.range.count())

        state.updateScrollPosition(4_000_000f)
        val end = state.layout(600f, 100_000)
        assertEquals(99_975..99_999, end.range)
        assertEquals(0f, end.trailingExtent)
        assertTrue(end.range.count() <= 36)

        state.updateScrollPosition(1_000_000f)
        assertEquals(24_990..25_025, state.layout(600f, 100_000).range)
    }

    @Test
    fun measurementAboveAnchorCorrectsScrollWithoutChangingVisibleIdentity() {
        val state = LazyListState()
        state.setItemSize(40f)
        state.setContainerSize(600f)
        state.updateScrollPosition(1_000f)
        val before = state.layout(600f, 1_000)
        assertEquals(23, before.range.first)

        state.updateMeasuredItem(index = 5, size = 80f)
        assertEquals(1_040f, state.scrollPosition)
        val after = state.layout(600f, 1_000)
        assertEquals(before.range, after.range)
        assertEquals(before.leadingExtent + 40f, after.leadingExtent)
    }

    @Test
    fun ensureVisibleAndAccessibilityTotalAreExplicit() {
        val state = LazyListState()
        state.setItemSize(50f)
        state.setContainerSize(200f)
        state.layout(200f, 100)

        state.ensureItemVisible(20, 100)
        assertEquals(850f, state.scrollPosition)
        state.ensureItemVisible(18, 100)
        assertEquals(850f, state.scrollPosition)
        state.ensureItemVisible(2, 100)
        assertEquals(100f, state.scrollPosition)

        assertTrue(state.exposeItemCountToAccessibility)
        state.setExposeItemCountToAccessibility(false)
        assertFalse(state.exposeItemCountToAccessibility)
    }

    @Test
    fun stateRejectsInvalidGeometry() {
        val state = LazyListState()
        assertFailsWith<IllegalArgumentException> { state.setItemSize(0f) }
        assertFailsWith<IllegalArgumentException> { state.setItemSize(Float.NaN) }
        assertFailsWith<IllegalArgumentException> { state.setOverscrollItems(-1) }
        assertFailsWith<IllegalArgumentException> { state.setContainerSize(-1f) }
        assertFailsWith<IllegalArgumentException> { state.updateScrollPosition(Float.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { state.ensureItemVisible(1, 1) }
    }

    @Test
    fun dataAttributesContainOnlyGeometryNotItemKeys() {
        val state = LazyListState()
        state.setItemSize(60f)
        state.setOverscrollItems(3)
        val attributes = state.getDataAttributes(100)
        assertEquals("100", attributes["data-total-items"])
        assertEquals("60.0", attributes["data-item-size"])
        assertEquals("3", attributes["data-overscroll-items"])
        assertEquals("true", attributes["data-lazy-container"])
        assertFalse(attributes.keys.any { it.contains("key") })
    }
}
