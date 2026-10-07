package codes.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SnapshotMutableListContractTest {
    @Test
    fun everyMutableListOperationPreservesStandardCollectionSemantics() {
        val values = mutableStateListOf("b", "c")
        assertTrue(values.addAll(0, listOf("a")))
        assertFalse(values.addAll(0, emptyList()))
        assertFalse(values.addAll(emptyList()))
        assertEquals(listOf("a", "b", "c"), values.toList())
        assertEquals(1, values.indexOf("b"))
        assertEquals(1, values.lastIndexOf("b"))
        assertTrue(values.containsAll(listOf("a", "c")))
        assertEquals(listOf("b", "c"), values.subList(1, 3))
        assertEquals("a", values.set(0, "A"))
        assertEquals("A", values.removeAt(0))
        assertFalse(values.remove("missing"))
        assertTrue(values.remove("b"))
        assertFalse(values.removeAll(listOf("missing")))
        values.addAll(listOf("c", "d"))
        assertTrue(values.removeAll(listOf("c")))
        assertFalse(values.retainAll(listOf("d")))
        values.add("e")
        assertTrue(values.retainAll(listOf("e")))
        assertEquals(listOf("e"), values.listIterator().asSequence().toList())
        assertEquals("e", values.listIterator(0).next())
        values.clear()
        assertTrue(values.isEmpty())
        values.clear()
    }
}
