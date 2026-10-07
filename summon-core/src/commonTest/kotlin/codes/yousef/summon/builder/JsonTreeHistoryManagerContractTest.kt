package codes.yousef.summon.builder

import codes.yousef.summon.core.registry.JsonBlock
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JsonTreeHistoryManagerContractTest {
    @AfterTest
    fun resetHistory() {
        JsonTreeHistoryManager.clear()
        JsonTreeHistoryManager.maxHistorySize = 50
    }

    @Test
    fun reactiveHistoryClonesTreesTruncatesRedoAndExposesEmptyState() {
        val initial = listOf(JsonBlock("root", mapOf("value" to "one"), listOf(JsonBlock("child"))))
        JsonTreeHistoryManager.initialize(initial)
        assertEquals(initial, JsonTreeHistoryManager.getCurrentState())
        assertFalse(JsonTreeHistoryManager.canUndo.value)
        assertFalse(JsonTreeHistoryManager.canRedo.value)

        val second = listOf(JsonBlock("root", mapOf("value" to "two")))
        JsonTreeHistoryManager.push(second)
        assertTrue(JsonTreeHistoryManager.undo())
        assertEquals(initial, JsonTreeHistoryManager.currentState.value)
        assertTrue(JsonTreeHistoryManager.canRedo.value)

        val replacement = listOf(JsonBlock("replacement"))
        JsonTreeHistoryManager.push(replacement)
        assertFalse(JsonTreeHistoryManager.canRedo.value)
        assertFalse(JsonTreeHistoryManager.redo())
        assertEquals(replacement, JsonTreeHistoryManager.getCurrentState())

        JsonTreeHistoryManager.clear()
        assertEquals(emptyList(), JsonTreeHistoryManager.getCurrentState())
        assertEquals(-1, JsonTreeHistoryManager.currentPosition())
        assertEquals(0, JsonTreeHistoryManager.historySize())
        assertFalse(JsonTreeHistoryManager.undo())
    }

    @Test
    fun capacityEvictsOldTreesAndRejectsInvalidLimits() {
        assertFailsWith<IllegalArgumentException> { JsonTreeHistoryManager.maxHistorySize = 0 }
        JsonTreeHistoryManager.maxHistorySize = 2
        JsonTreeHistoryManager.initialize(listOf(JsonBlock("one")))
        JsonTreeHistoryManager.push(listOf(JsonBlock("two")))
        JsonTreeHistoryManager.push(listOf(JsonBlock("three")))
        assertEquals(2, JsonTreeHistoryManager.historySize())
        assertTrue(JsonTreeHistoryManager.undo())
        assertEquals("two", JsonTreeHistoryManager.getCurrentState().single().type)
        assertFalse(JsonTreeHistoryManager.undo())
        assertTrue(JsonTreeHistoryManager.redo())
    }
}
