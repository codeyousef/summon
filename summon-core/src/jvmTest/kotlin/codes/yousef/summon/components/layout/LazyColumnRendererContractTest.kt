package codes.yousef.summon.components.layout

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class LazyColumnRendererContractTest {
    @Test
    fun columnRendersBoundedRowsStickyHeadersDividersSpacingAndAccessibility() {
        val state = LazyListState().apply {
            setContainerSize(1_000f)
            setItemSize(40f)
            setOverscrollItems(0)
        }
        val html = PlatformRenderer().renderComposableRoot {
            LazyColumn(state = state) {
                stickyHeader("header") { Text("Header") }
                sectionDivider("divider") { Text("Divider") }
                spacing(height = 4f, width = 2f)
                items(2, key = { "item-$it" }) { Text("Item $it") }
            }
        }
        assertContains(html, "data-total-items=\"5\"")
        assertContains(html, "position: sticky")
        assertContains(html, "data-section-divider=\"true\"")
        assertContains(html, "margin-top: 4.0px")
        assertContains(html, "margin-left: 2.0px")
        assertContains(html, "aria-setsize=\"5\"")
        assertContains(html, "data-lazy-spacer=\"leading\"")
        assertContains(html, "data-lazy-spacer=\"trailing\"")

        state.setExposeItemCountToAccessibility(false)
        val privateCount = PlatformRenderer().renderComposableRoot {
            LazyColumn(state = state) { item { Text("Only") } }
        }
        assertFalse(privateCount.contains("aria-setsize"))
    }

    @Test
    fun duplicateVisibleKeysFailInsteadOfReusingTheWrongRow() {
        val state = LazyListState().apply { setContainerSize(1_000f) }
        assertFailsWith<IllegalStateException> {
            PlatformRenderer().renderComposableRoot {
                LazyColumn(state = state) {
                    items(2, key = { "duplicate" }) { Text("Item $it") }
                }
            }
        }
    }

    @Test
    fun rowRendersHorizontalGeometryAndRejectsDuplicateVisibleKeys() {
        val state = LazyListState().apply {
            setContainerSize(1_000f)
            setItemSize(40f)
            setOverscrollItems(0)
        }
        val html = PlatformRenderer().renderComposableRoot {
            LazyRow(state = state) {
                stickyHeader("header") { Text("Header") }
                sectionDivider("divider") { Text("Divider") }
                items(2, key = { "row-$it" }) { Text("Row $it") }
            }
        }
        assertContains(html, "data-direction=\"row\"")
        assertContains(html, "flex-direction: row")
        assertContains(html, "left: 0")
        assertContains(html, "data-section-divider=\"true\"")
        assertContains(html, "aria-setsize=\"4\"")

        state.setExposeItemCountToAccessibility(false)
        val privateCount = PlatformRenderer().renderComposableRoot {
            LazyRow(state = state) { item { Text("Only") } }
        }
        assertFalse(privateCount.contains("aria-setsize"))

        assertFailsWith<IllegalStateException> {
            PlatformRenderer().renderComposableRoot {
                LazyRow(state = state) {
                    items(2, key = { "duplicate" }) { Text("Row $it") }
                }
            }
        }
    }

}
