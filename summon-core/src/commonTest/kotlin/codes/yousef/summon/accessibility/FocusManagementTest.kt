package codes.yousef.summon.accessibility

import kotlin.test.*

class FocusManagementTest {

    @Test
    fun testCreateFocusModifier() {
        // Test FOCUSABLE behavior
        val focusableModifier = FocusManagement.createFocusModifier(FocusManagement.FocusBehavior.FOCUSABLE)
        assertEquals("-1", focusableModifier.styles["tabindex"], "FOCUSABLE should have tabindex=-1")

        // Test TABBABLE behavior
        val tabbableModifier = FocusManagement.createFocusModifier(FocusManagement.FocusBehavior.TABBABLE)
        assertEquals("0", tabbableModifier.styles["tabindex"], "TABBABLE should have tabindex=0")

        // Test DISABLED behavior
        val disabledModifier = FocusManagement.createFocusModifier(FocusManagement.FocusBehavior.DISABLED)
        assertEquals("-1", disabledModifier.styles["tabindex"], "DISABLED should have tabindex=-1")
        assertEquals("true", disabledModifier.styles["aria-disabled"], "DISABLED should have aria-disabled=true")

        // Test AUTO_FOCUS behavior
        val autoFocusModifier = FocusManagement.createFocusModifier(FocusManagement.FocusBehavior.AUTO_FOCUS)
        assertEquals("0", autoFocusModifier.styles["tabindex"], "AUTO_FOCUS should have tabindex=0")
        assertEquals("true", autoFocusModifier.styles["autofocus"], "AUTO_FOCUS should have autofocus=true")
    }

    @Test
    fun testCreateFocusPoint() {
        // Test without restore
        val focusPoint = FocusManagement.createFocusPoint("test-id", shouldRestore = false)
        assertEquals("test-id", focusPoint.styles["id"], "Focus point should have correct id")
        assertEquals("true", focusPoint.styles["data-focus-point"], "Focus point should have data-focus-point=true")
        assertNull(
            focusPoint.styles["data-focus-restore"],
            "Focus point without restore should not have data-focus-restore"
        )

        // Test with restore
        val focusPointWithRestore = FocusManagement.createFocusPoint("test-id-2", shouldRestore = true)
        assertEquals("test-id-2", focusPointWithRestore.styles["id"], "Focus point should have correct id")
        assertEquals(
            "true",
            focusPointWithRestore.styles["data-focus-point"],
            "Focus point should have data-focus-point=true"
        )
        assertEquals(
            "true",
            focusPointWithRestore.styles["data-focus-restore"],
            "Focus point with restore should have data-focus-restore=true"
        )
    }

    @Test
    fun testCreateFocusTrap() {
        val focusTrap = FocusManagement.createFocusTrap("trap-id")
        assertEquals("trap-id", focusTrap.styles["data-focus-trap"], "Focus trap should have correct trap id")
        assertEquals("true", focusTrap.styles["data-focus-trap-active"], "Focus trap should be active")
    }

    @Test
    fun testCreateFocusScope() {
        val focusScope = FocusManagement.createFocusScope("scope-id")
        assertEquals("scope-id", focusScope.styles["data-focus-scope"], "Focus scope should have correct scope id")
        assertEquals("group", focusScope.styles["role"], "Focus scope should have role=group")
    }

    @Test
    fun testFocusManagerRegistration() {
        // Clear any existing registrations
        FocusManager.getFocusableElements().forEach { FocusManager.unregisterFocusable(it) }

        // Test registration
        var focusCalled = false
        FocusManager.registerFocusable("test-element") { focusCalled = true }

        // Verify element is registered
        val elements = FocusManager.getFocusableElements()
        assertTrue(elements.contains("test-element"), "Element should be registered")

        // Test focus request
        val focusResult = FocusManager.requestFocus("test-element")
        assertTrue(focusResult, "Focus request should succeed")
        assertTrue(focusCalled, "Focus action should be called")
        assertEquals("test-element", FocusManager.getCurrentFocus(), "Current focus should be updated")

        // Test unregistration
        FocusManager.unregisterFocusable("test-element")
        val elementsAfterUnregister = FocusManager.getFocusableElements()
        assertFalse(elementsAfterUnregister.contains("test-element"), "Element should be unregistered")
        assertNull(FocusManager.getCurrentFocus(), "Current focus should be cleared")

        // Test focus request for non-existent element
        val invalidFocusResult = FocusManager.requestFocus("non-existent")
        assertFalse(invalidFocusResult, "Focus request for non-existent element should fail")
    }

    @Test
    fun testFocusManagerClearFocus() {
        // Register an element and set focus
        FocusManager.registerFocusable("test-clear") { }
        FocusManager.requestFocus("test-clear")
        assertEquals("test-clear", FocusManager.getCurrentFocus(), "Focus should be set")

        // Clear focus
        FocusManager.clearFocus()
        assertNull(FocusManager.getCurrentFocus(), "Focus should be cleared")

        // Clean up
        FocusManager.unregisterFocusable("test-clear")
    }

    @Test
    fun testGenerateRandomId() {
        // Test that generated IDs are of the expected length
        val id1 = generateRandomId()
        assertEquals(10, id1.length, "Generated ID should be 10 characters long")

        // Test that generated IDs are unique
        val id2 = generateRandomId()
        assertNotEquals(id1, id2, "Generated IDs should be unique")

        // Test that generated IDs contain only valid characters
        val validChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val allCharsValid = id1.all { it in validChars }
        assertTrue(allCharsValid, "Generated ID should contain only valid characters")
    }
}