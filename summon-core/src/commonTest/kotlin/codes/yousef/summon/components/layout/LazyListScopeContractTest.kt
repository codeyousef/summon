package codes.yousef.summon.components.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LazyListScopeContractTest {
    @Test
    fun scopeBuildsEveryIntervalTypeWithStableMetadataAndLocalIndices() {
        val rendered = mutableListOf<String>()
        val provider = object : LazyListDataProvider<String> {
            override val itemCount: Int = 2
            override fun key(index: Int): Any = "provider-$index"
            override fun itemAt(index: Int): LazyListItemResult<String> = if (index == 0) {
                LazyListItemResult.Data("value")
            } else {
                LazyListItemResult.Loading
            }
        }
        val scope = LazyListScopeImpl().apply {
            item("single") { rendered += "single" }
            items(2, key = { "count-$it" }) { rendered += "count-$it" }
            items(listOf("a", "b"), key = { "list-$it" }) { rendered += "list-$it" }
            itemsIndexed(listOf("x", "y"), key = { index, item -> "$index-$item" }) { index, item -> rendered += "indexed-$index-$item" }
            items(provider) { index, result -> rendered += "provider-$index-$result" }
            stickyHeader("header") { rendered += "header" }
            sectionDivider("divider") { rendered += "divider" }
            spacing(height = 2f, width = 3f)
        }
        assertEquals(12, scope.itemCount)
        assertEquals("single", scope.metadata(0).key)
        assertEquals("count-1", scope.metadata(2).key)
        assertEquals("list-b", scope.metadata(4).key)
        assertEquals("1-y", scope.metadata(6).key)
        assertEquals("provider-1", scope.metadata(8).key)
        assertTrue(scope.metadata(9).isHeader)
        assertTrue(scope.metadata(10).isDivider)
        assertEquals(2f, scope.metadata(11).spacingHeight)
        assertEquals(3f, scope.metadata(11).spacingWidth)
        repeat(scope.itemCount) { scope.render(it) }
        assertTrue(rendered.contains("provider-0-Data(value=value)"))
        assertTrue(rendered.contains("provider-1-Loading"))
    }

    @Test
    fun scopeRejectsInvalidRangesAndIgnoresEmptyIntervals() {
        val scope = LazyListScopeImpl()
        scope.items(0) { error("must not render") }
        scope.items(emptyList<String>()) { error("must not render") }
        scope.itemsIndexed(emptyList<String>()) { _, _ -> error("must not render") }
        assertEquals(0, scope.itemCount)
        assertFailsWith<IllegalArgumentException> { scope.items(-1) {} }
        assertFailsWith<IllegalArgumentException> { scope.spacing(Float.NaN, 0f) }
        assertFailsWith<IllegalArgumentException> { scope.spacing(0f, -1f) }
        assertFailsWith<IllegalArgumentException> { scope.metadata(0) }
    }
}
