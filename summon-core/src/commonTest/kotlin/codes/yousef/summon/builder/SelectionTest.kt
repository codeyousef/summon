package codes.yousef.summon.builder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for SelectionManager functionality.
 *
 * TEST DIRECTIVE: Click a component. Verify `selection` state matches ID.
 * Verify Overlay div exists and matches dimensions of the target.
 */
class SelectionTest {

    @Test
    fun testSelectComponent() {
        // Clear any previous selection
        SelectionManager.clearSelection()

        // Select a component
        SelectionManager.select("component-123")

        // Verify selection state matches ID
        assertEquals("component-123", SelectionManager.selectedId.value)
        assertTrue(SelectionManager.hasSelection())
    }

    @Test
    fun testClearSelection() {
        // First select something
        SelectionManager.select("component-456")
        assertEquals("component-456", SelectionManager.selectedId.value)

        // Clear selection
        SelectionManager.clearSelection()

        // Verify cleared
        assertNull(SelectionManager.selectedId.value)
        assertFalse(SelectionManager.hasSelection())
    }

    @Test
    fun testSelectReplacesExisting() {
        SelectionManager.select("first-component")
        assertEquals("first-component", SelectionManager.selectedId.value)

        SelectionManager.select("second-component")
        assertEquals("second-component", SelectionManager.selectedId.value)
    }

    @Test
    fun testIsSelected() {
        SelectionManager.clearSelection()

        SelectionManager.select("target-id")

        assertTrue(SelectionManager.isSelected("target-id"))
        assertFalse(SelectionManager.isSelected("other-id"))
    }

    @Test
    fun testSelectionCallback() {
        var callbackInvoked = false
        var selectedId: String? = null

        SelectionManager.onSelectionChange = { id ->
            callbackInvoked = true
            selectedId = id
        }

        SelectionManager.select("callback-test")

        assertTrue(callbackInvoked)
        assertEquals("callback-test", selectedId)

        // Clean up
        SelectionManager.onSelectionChange = null
    }

    @Test
    fun testDeselect() {
        SelectionManager.select("to-deselect")
        assertTrue(SelectionManager.hasSelection())

        SelectionManager.deselect()
        assertFalse(SelectionManager.hasSelection())
        assertNull(SelectionManager.selectedId.value)
    }
}
