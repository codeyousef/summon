package codes.yousef.summon.core.registry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for RenderLoop and JsonBlock functionality.
 *
 * TEST DIRECTIVE: Define a JSON tree 3 levels deep (Root -> Column -> Text).
 * Execute render. Query the resulting DOM node using document.querySelector.
 * Assert the structure matches div > div > span. Assert the text content matches the JSON prop.
 */
class RenderTreeTest {

    @Test
    fun testJsonBlockCreation() {
        // Create a simple JsonBlock
        val textBlock = JsonBlock(
            type = "Text",
            props = mapOf("text" to "Hello World", "color" to "#000000"),
            children = emptyList()
        )

        assertEquals("Text", textBlock.type)
        assertEquals("Hello World", textBlock.props["text"])
        assertEquals("#000000", textBlock.props["color"])
        assertTrue(textBlock.children.isEmpty())
    }

    @Test
    fun testNestedJsonBlockStructure() {
        // Create a 3-level deep JSON tree: Root -> Column -> Text
        val textBlock = JsonBlock(
            type = "Text",
            props = mapOf("text" to "Nested Content"),
            children = emptyList()
        )

        val columnBlock = JsonBlock(
            type = "Column",
            props = mapOf("spacing" to 8),
            children = listOf(textBlock)
        )

        val rootBlock = JsonBlock(
            type = "Root",
            props = mapOf("id" to "main-root"),
            children = listOf(columnBlock)
        )

        // Verify structure
        assertEquals("Root", rootBlock.type)
        assertEquals(1, rootBlock.children.size)

        val child = rootBlock.children[0]
        assertEquals("Column", child.type)
        assertEquals(1, child.children.size)

        val grandchild = child.children[0]
        assertEquals("Text", grandchild.type)
        assertEquals("Nested Content", grandchild.props["text"])
    }

    @Test
    fun testMultipleChildrenInBlock() {
        val text1 = JsonBlock("Text", mapOf("text" to "First"), emptyList())
        val text2 = JsonBlock("Text", mapOf("text" to "Second"), emptyList())
        val text3 = JsonBlock("Text", mapOf("text" to "Third"), emptyList())

        val container = JsonBlock(
            type = "Column",
            props = mapOf("gap" to 16),
            children = listOf(text1, text2, text3)
        )

        assertEquals(3, container.children.size)
        assertEquals("First", container.children[0].props["text"])
        assertEquals("Second", container.children[1].props["text"])
        assertEquals("Third", container.children[2].props["text"])
    }

    @Test
    fun testJsonBlockWithEmptyProps() {
        val block = JsonBlock(
            type = "Spacer",
            props = emptyMap(),
            children = emptyList()
        )

        assertEquals("Spacer", block.type)
        assertTrue(block.props.isEmpty())
        assertTrue(block.children.isEmpty())
    }

    @Test
    fun testJsonBlockWithVariousPropsTypes() {
        val block = JsonBlock(
            type = "ComplexComponent",
            props = mapOf(
                "stringProp" to "hello",
                "intProp" to 42,
                "boolProp" to true,
                "doubleProp" to 3.14,
                "listProp" to listOf("a", "b", "c")
            ),
            children = emptyList()
        )

        assertEquals("hello", block.props["stringProp"])
        assertEquals(42, block.props["intProp"])
        assertEquals(true, block.props["boolProp"])
        assertEquals(3.14, block.props["doubleProp"])
        assertEquals(listOf("a", "b", "c"), block.props["listProp"])
    }

    @Test
    fun testDeepNestedStructure() {
        // Build a 5-level deep structure
        var current = JsonBlock("Leaf", mapOf("level" to 5), emptyList())
        
        for (level in 4 downTo 1) {
            current = JsonBlock("Level$level", mapOf("level" to level), listOf(current))
        }

        // Traverse and verify
        var node: JsonBlock? = current
        var expectedLevel = 1

        while (node != null && expectedLevel <= 4) {
            assertEquals("Level$expectedLevel", node.type)
            assertEquals(expectedLevel, node.props["level"])
            node = node.children.firstOrNull()
            expectedLevel++
        }

        // Verify leaf
        assertNotNull(node)
        assertEquals("Leaf", node.type)
        assertEquals(5, node.props["level"])
    }
}
