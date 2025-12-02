package codes.yousef.summon.builder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for HistoryManager (Undo/Redo) functionality.
 *
 * TEST DIRECTIVE: Make Change A. Make Change B. Call Undo.
 * Verify State equals A. Call Redo. Verify State equals B.
 */
class UndoTest {

    @Test
    fun testUndoRedo() {
        // Create a fresh history manager
        val history = HistoryManager<String>()

        // Initial state
        history.push("Initial")

        // Make Change A
        history.push("Change A")
        assertEquals("Change A", history.current())

        // Make Change B
        history.push("Change B")
        assertEquals("Change B", history.current())

        // Call Undo - should go back to A
        assertTrue(history.canUndo())
        history.undo()
        assertEquals("Change A", history.current())

        // Call Redo - should go forward to B
        assertTrue(history.canRedo())
        history.redo()
        assertEquals("Change B", history.current())
    }

    @Test
    fun testCannotUndoPastInitial() {
        val history = HistoryManager<String>()
        history.push("Only State")

        assertFalse(history.canUndo(), "Should not be able to undo with only one state")
    }

    @Test
    fun testCannotRedoAtEnd() {
        val history = HistoryManager<String>()
        history.push("State 1")
        history.push("State 2")

        assertFalse(history.canRedo(), "Should not be able to redo at the end")
    }

    @Test
    fun testPushClearsRedoHistory() {
        val history = HistoryManager<String>()
        history.push("State 1")
        history.push("State 2")
        history.push("State 3")

        // Undo twice
        history.undo()
        history.undo()
        assertEquals("State 1", history.current())

        // Now push a new state - should clear redo history
        history.push("New Branch")
        assertEquals("New Branch", history.current())

        // Redo should not be available
        assertFalse(history.canRedo())
    }

    @Test
    fun testMultipleUndos() {
        val history = HistoryManager<Int>()
        history.push(1)
        history.push(2)
        history.push(3)
        history.push(4)
        history.push(5)

        assertEquals(5, history.current())

        history.undo()
        assertEquals(4, history.current())

        history.undo()
        assertEquals(3, history.current())

        history.undo()
        assertEquals(2, history.current())

        history.undo()
        assertEquals(1, history.current())

        assertFalse(history.canUndo())
    }

    @Test
    fun testMultipleRedos() {
        val history = HistoryManager<Int>()
        history.push(1)
        history.push(2)
        history.push(3)

        // Undo all the way back
        history.undo()
        history.undo()
        assertEquals(1, history.current())

        // Redo back to the end
        history.redo()
        assertEquals(2, history.current())

        history.redo()
        assertEquals(3, history.current())

        assertFalse(history.canRedo())
    }

    @Test
    fun testHistorySize() {
        val history = HistoryManager<String>()

        assertEquals(0, history.size())

        history.push("A")
        assertEquals(1, history.size())

        history.push("B")
        assertEquals(2, history.size())

        history.push("C")
        assertEquals(3, history.size())
    }

    @Test
    fun testClearHistory() {
        val history = HistoryManager<String>()
        history.push("A")
        history.push("B")
        history.push("C")

        history.clear()

        assertEquals(0, history.size())
        assertFalse(history.canUndo())
        assertFalse(history.canRedo())
    }

    @Test
    fun testComplexStateObjects() {
        data class AppState(val count: Int, val name: String)

        val history = HistoryManager<AppState>()
        history.push(AppState(0, "Initial"))
        history.push(AppState(1, "First"))
        history.push(AppState(2, "Second"))

        assertEquals(AppState(2, "Second"), history.current())

        history.undo()
        assertEquals(AppState(1, "First"), history.current())

        history.redo()
        assertEquals(AppState(2, "Second"), history.current())
    }
}
