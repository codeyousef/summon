package codes.yousef.summon.builder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for EditModeManager functionality.
 *
 * TEST DIRECTIVE: Set Edit Mode. Click link. Assert navigation did NOT happen.
 * Set Preview Mode. Click link. Assert navigation DID happen.
 *
 * Note: Navigation behavior tests require browser environment.
 * These tests verify the mode state management.
 */
class ModeTest {

    @Test
    fun testEditModeToggle() {
        // Start in preview mode (default)
        EditModeManager.enablePreviewMode()
        assertFalse(EditModeManager.isEditMode.value)

        // Enable edit mode
        EditModeManager.enableEditMode()
        assertTrue(EditModeManager.isEditMode.value)

        // Disable edit mode (go to preview)
        EditModeManager.enablePreviewMode()
        assertFalse(EditModeManager.isEditMode.value)
    }

    @Test
    fun testToggleModeFunction() {
        EditModeManager.enablePreviewMode()
        assertFalse(EditModeManager.isEditMode.value)

        EditModeManager.toggleMode()
        assertTrue(EditModeManager.isEditMode.value)

        EditModeManager.toggleMode()
        assertFalse(EditModeManager.isEditMode.value)
    }

    @Test
    fun testModeChangeCallback() {
        var callbackValue: Boolean? = null

        EditModeManager.onModeChange = { isEdit ->
            callbackValue = isEdit
        }

        EditModeManager.enablePreviewMode()
        EditModeManager.enableEditMode()
        assertEquals(true, callbackValue)

        EditModeManager.enablePreviewMode()
        assertEquals(false, callbackValue)

        // Clean up
        EditModeManager.onModeChange = null
    }

    @Test
    fun testPreviewModeClearsSelection() {
        // Select something in edit mode
        EditModeManager.enableEditMode()
        SelectionManager.select("test-component")
        assertTrue(SelectionManager.hasSelection())

        // Switch to preview mode - should clear selection
        EditModeManager.enablePreviewMode()
        assertFalse(SelectionManager.hasSelection())
    }

    @Test
    fun testEnableEditModeIdempotent() {
        var callCount = 0
        EditModeManager.onModeChange = { callCount++ }

        EditModeManager.enablePreviewMode()
        callCount = 0 // Reset after setup

        EditModeManager.enableEditMode()
        assertEquals(1, callCount)

        // Calling again should not trigger callback
        EditModeManager.enableEditMode()
        assertEquals(1, callCount, "Should not trigger callback if already in edit mode")

        // Clean up
        EditModeManager.onModeChange = null
    }

    @Test
    fun testEnablePreviewModeIdempotent() {
        var callCount = 0
        EditModeManager.onModeChange = { callCount++ }

        EditModeManager.enableEditMode()
        callCount = 0 // Reset after setup

        EditModeManager.enablePreviewMode()
        assertEquals(1, callCount)

        // Calling again should not trigger callback
        EditModeManager.enablePreviewMode()
        assertEquals(1, callCount, "Should not trigger callback if already in preview mode")

        // Clean up
        EditModeManager.onModeChange = null
    }

    @Test
    fun testLocalEditModeCompositionLocal() {
        // LocalEditMode should have a default value of false
        assertEquals(false, LocalEditMode.current)
    }
}
