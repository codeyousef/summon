package codes.yousef.summon.components.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LazyListScopeTest {
    @Test
    fun countBackedIntervalResolvesOnlyRequestedItems() {
        val scope = LazyListScopeImpl()
        val rendered = mutableListOf<Int>()

        scope.items(
            count = 100_000,
            key = { "opaque-$it" }
        ) { index ->
            rendered += index
        }

        assertEquals(100_000, scope.itemCount)
        assertEquals("opaque-50000", scope.metadata(50_000).key)
        assertTrue(rendered.isEmpty())

        scope.render(50_000)
        assertEquals(listOf(50_000), rendered)
    }

    @Test
    fun listIntervalsPreserveKeysMetadataAndContent() {
        val scope = LazyListScopeImpl()
        val rendered = mutableListOf<String>()

        scope.stickyHeader("header") { rendered += "header" }
        scope.items(listOf("a", "b"), key = { "key-$it" }) { rendered += it }
        scope.spacing(height = 10f, width = 20f)
        scope.sectionDivider("divider") { rendered += "divider" }
        scope.itemsIndexed(listOf("x", "y"), key = { index, item -> "$index-$item" }) { index, item ->
            rendered += "$index:$item"
        }

        assertEquals(7, scope.itemCount)
        assertTrue(scope.metadata(0).isHeader)
        assertEquals("key-a", scope.metadata(1).key)
        assertEquals(10f, scope.metadata(3).spacingHeight)
        assertEquals(20f, scope.metadata(3).spacingWidth)
        assertTrue(scope.metadata(4).isDivider)
        assertFalse(scope.metadata(5).isHeader)
        assertEquals("1-y", scope.metadata(6).key)

        for (index in 0 until scope.itemCount) scope.render(index)
        assertEquals(listOf("header", "a", "b", "divider", "0:x", "1:y"), rendered)
    }

    @Test
    fun pagedProviderIsReadOnlyWhenItsVisibleItemRenders() {
        val reads = mutableListOf<Int>()
        val rendered = mutableListOf<Pair<Int, LazyListItemResult<String>>>()
        val provider = object : LazyListDataProvider<String> {
            override val itemCount: Int = 10_000
            override fun key(index: Int): Any = "key-$index"
            override fun itemAt(index: Int): LazyListItemResult<String> {
                reads += index
                return when (index) {
                    5 -> LazyListItemResult.Loading
                    6 -> LazyListItemResult.Locked
                    7 -> LazyListItemResult.PermissionDenied
                    8 -> LazyListItemResult.Error("bounded-code")
                    9 -> LazyListItemResult.Empty
                    else -> LazyListItemResult.Data("value-$index")
                }
            }
        }
        val scope = LazyListScopeImpl()
        scope.items(provider) { index, result -> rendered += index to result }

        assertEquals(10_000, scope.itemCount)
        assertEquals("key-8", scope.metadata(8).key)
        assertTrue(reads.isEmpty())

        for (index in 4..9) scope.render(index)
        assertEquals((4..9).toList(), reads)
        assertIs<LazyListItemResult.Data<String>>(rendered[0].second)
        assertEquals(LazyListItemResult.Loading, rendered[1].second)
        assertEquals(LazyListItemResult.Locked, rendered[2].second)
        assertEquals(LazyListItemResult.PermissionDenied, rendered[3].second)
        assertEquals(LazyListItemResult.Error("bounded-code"), rendered[4].second)
        assertEquals(LazyListItemResult.Empty, rendered[5].second)
    }

    @Test
    fun invalidCountsAndSpacingAreRejected() {
        val scope = LazyListScopeImpl()
        assertFailsWith<IllegalArgumentException> { scope.items(-1) {} }
        assertFailsWith<IllegalArgumentException> {
            scope.items(object : LazyListDataProvider<Unit> {
                override val itemCount: Int = -1
                override fun itemAt(index: Int): LazyListItemResult<Unit> = LazyListItemResult.Empty
            }) { _, _ -> }
        }
        assertFailsWith<IllegalArgumentException> { scope.spacing(height = Float.NaN) }
        assertFailsWith<IllegalArgumentException> { scope.spacing(width = -1f) }
    }
}
