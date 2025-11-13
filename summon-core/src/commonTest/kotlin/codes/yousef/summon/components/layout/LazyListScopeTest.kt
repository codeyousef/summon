package codes.yousef.summon.components.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LazyListScopeTest {

    @Test
    fun testSingleItem() {
        val scope = LazyListScopeImpl()
        var called = false

        // Add a single item
        scope.item(key = "testKey") {
            called = true
        }

        // Verify the item was added correctly
        assertEquals(1, scope.items.size, "Should have 1 item")
        assertEquals(1, scope.keys.size, "Should have 1 key")
        assertEquals("testKey", scope.keys[0], "Key should match")
        assertFalse(scope.isHeader[0], "Should not be a header")
        assertFalse(scope.isDivider[0], "Should not be a divider")
        assertEquals(0f to 0f, scope.spacings[0], "Spacing should be zero")

        // Call the item content and verify it works
        scope.items[0].invoke()
        assertTrue(called, "Item content should be called")
    }

    @Test
    fun testItems() {
        val scope = LazyListScopeImpl()
        val testItems = listOf("Item1", "Item2", "Item3")
        val calledItems = mutableListOf<String>()

        // Add multiple items
        scope.items(
            items = testItems,
            key = { item -> "key_$item" }
        ) { item ->
            calledItems.add(item)
        }

        // Verify items were added correctly
        assertEquals(3, scope.items.size, "Should have 3 items")
        assertEquals(3, scope.keys.size, "Should have 3 keys")
        assertEquals("key_Item1", scope.keys[0], "First key should match")
        assertEquals("key_Item2", scope.keys[1], "Second key should match")
        assertEquals("key_Item3", scope.keys[2], "Third key should match")

        // Call each item's content and verify it works
        scope.items.forEach { it.invoke() }
        assertEquals(testItems, calledItems, "All items should be called in order")

        // Verify none are headers or dividers
        scope.isHeader.forEach { assertFalse(it, "Should not be a header") }
        scope.isDivider.forEach { assertFalse(it, "Should not be a divider") }

        // Verify spacings are zero
        scope.spacings.forEach { assertEquals(0f to 0f, it, "Spacing should be zero") }
    }

    @Test
    fun testItemsIndexed() {
        val scope = LazyListScopeImpl()
        val testItems = listOf("Item1", "Item2", "Item3")
        val calledItems = mutableListOf<Pair<Int, String>>()

        // Add multiple indexed items
        scope.itemsIndexed(
            items = testItems,
            key = { index, item -> "key_${index}_$item" }
        ) { index, item ->
            calledItems.add(index to item)
        }

        // Verify items were added correctly
        assertEquals(3, scope.items.size, "Should have 3 items")
        assertEquals(3, scope.keys.size, "Should have 3 keys")
        assertEquals("key_0_Item1", scope.keys[0], "First key should match")
        assertEquals("key_1_Item2", scope.keys[1], "Second key should match")
        assertEquals("key_2_Item3", scope.keys[2], "Third key should match")

        // Call each item's content and verify it works
        scope.items.forEach { it.invoke() }
        assertEquals(
            listOf(0 to "Item1", 1 to "Item2", 2 to "Item3"),
            calledItems,
            "All items should be called with correct indices"
        )

        // Verify none are headers or dividers
        scope.isHeader.forEach { assertFalse(it, "Should not be a header") }
        scope.isDivider.forEach { assertFalse(it, "Should not be a divider") }
    }

    @Test
    fun testStickyHeader() {
        val scope = LazyListScopeImpl()
        var called = false

        // Add a sticky header
        scope.stickyHeader(key = "headerKey") {
            called = true
        }

        // Verify the header was added correctly
        assertEquals(1, scope.items.size, "Should have 1 item")
        assertEquals(1, scope.keys.size, "Should have 1 key")
        assertEquals("headerKey", scope.keys[0], "Key should match")
        assertTrue(scope.isHeader[0], "Should be a header")
        assertFalse(scope.isDivider[0], "Should not be a divider")

        // Call the header content and verify it works
        scope.items[0].invoke()
        assertTrue(called, "Header content should be called")
    }

    @Test
    fun testSectionDivider() {
        val scope = LazyListScopeImpl()
        var called = false

        // Add a section divider
        scope.sectionDivider(key = "dividerKey") {
            called = true
        }

        // Verify the divider was added correctly
        assertEquals(1, scope.items.size, "Should have 1 item")
        assertEquals(1, scope.keys.size, "Should have 1 key")
        assertEquals("dividerKey", scope.keys[0], "Key should match")
        assertFalse(scope.isHeader[0], "Should not be a header")
        assertTrue(scope.isDivider[0], "Should be a divider")

        // Call the divider content and verify it works
        scope.items[0].invoke()
        assertTrue(called, "Divider content should be called")
    }

    @Test
    fun testSpacing() {
        val scope = LazyListScopeImpl()

        // Add spacing
        scope.spacing(height = 10f, width = 20f)

        // Verify the spacing was added correctly
        assertEquals(1, scope.items.size, "Should have 1 item")
        assertEquals(1, scope.keys.size, "Should have 1 key")
        assertEquals(null, scope.keys[0], "Key should be null")
        assertFalse(scope.isHeader[0], "Should not be a header")
        assertFalse(scope.isDivider[0], "Should not be a divider")
        assertEquals(10f to 20f, scope.spacings[0], "Spacing should match")
    }

    @Test
    fun testMixedItems() {
        val scope = LazyListScopeImpl()

        // Add various types of items
        scope.stickyHeader(key = "header") { }
        scope.item(key = "item1") { }
        scope.items(listOf("A", "B")) { }
        scope.spacing(height = 5f)
        scope.sectionDivider(key = "divider") { }
        scope.itemsIndexed(listOf("X", "Y")) { _, _ -> }

        // Verify the correct number of items
        assertEquals(8, scope.items.size, "Should have 8 items total")
        assertEquals(8, scope.keys.size, "Should have 8 keys")
        assertEquals(8, scope.isHeader.size, "Should have 8 header flags")
        assertEquals(8, scope.isDivider.size, "Should have 8 divider flags")
        assertEquals(8, scope.spacings.size, "Should have 8 spacing values")

        // Verify the types are correct
        assertTrue(scope.isHeader[0], "First item should be a header")
        assertFalse(scope.isHeader[1], "Second item should not be a header")
        assertFalse(scope.isDivider[0], "First item should not be a divider")
        assertTrue(scope.isDivider[5], "Sixth item should be a divider")
        assertEquals(5f to 0f, scope.spacings[4], "Fifth item should have spacing")
    }
}
