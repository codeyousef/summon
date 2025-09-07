package code.yousef.summon.modifier

// Import necessary functions/enums if not automatically resolved
import code.yousef.summon.modifier.AttributeModifiers.getAttribute // Needed for verification
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AccessibilityModifiersTest {

    @Test
    fun testRoleModifier() {
        val modifier = Modifier().role("button")
        assertEquals("button", modifier.getAttribute("role"), "Role modifier failed.")
    }

    @Test
    fun testAriaLabelModifier() {
        val label = "Descriptive Label"
        val modifier = Modifier().ariaLabel(label)
        assertEquals(label, modifier.getAttribute("aria-label"), "ariaLabel modifier failed.")
    }

    @Test
    fun testAriaHiddenModifier() {
        val modifierTrue = Modifier().ariaHidden(true)
        assertEquals("true", modifierTrue.getAttribute("aria-hidden"), "ariaHidden(true) failed.")

        val modifierFalse = Modifier().ariaHidden(false)
        assertEquals("false", modifierFalse.getAttribute("aria-hidden"), "ariaHidden(false) failed.")
    }
    
    @Test
    fun testAriaCheckedModifier() {
        val modifierTrue = Modifier().ariaChecked(true)
        assertEquals("true", modifierTrue.getAttribute("aria-checked"), "ariaChecked(true) failed.")

        val modifierFalse = Modifier().ariaChecked(false)
        assertEquals("false", modifierFalse.getAttribute("aria-checked"), "ariaChecked(false) failed.")

        val modifierMixed = Modifier().ariaChecked("mixed")
        assertEquals("mixed", modifierMixed.getAttribute("aria-checked"), "ariaChecked(\"mixed\") failed.")
    }

    @Test
    fun testRemoveAttribute() {
        val initialModifier = Modifier()
            .attribute("id", "test-id")
            .attribute("role", "main")
            .attribute("aria-label", "Test Label")

        // Remove existing attribute
        val modified1 = initialModifier.removeAttribute("role")
        assertEquals("test-id", modified1.getAttribute("id"))
        assertNull(modified1.getAttribute("role"), "Attribute 'role' should have been removed.")
        assertEquals("Test Label", modified1.getAttribute("aria-label"))

        // Try removing non-existent attribute
        val modified2 = modified1.removeAttribute("non-existent")
        assertEquals(modified1.styles, modified2.styles, "Removing non-existent attribute changed the modifier.")
    }
    
    @Test
    fun testFocusableModifier() {
        val modifier = Modifier().focusable()
        assertEquals("-1", modifier.getAttribute("tabindex"), "Focusable modifier should set tabindex to -1.")
    }

    @Test
    fun testTabbableModifier() {
        val modifier = Modifier().tabbable()
        assertEquals("0", modifier.getAttribute("tabindex"), "Tabbable modifier should set tabindex to 0.")
    }

    @Test
    fun testDisabledModifier() {
        val modifier = Modifier().disabled()
        assertEquals("", modifier.getAttribute("disabled"), "Disabled modifier should add disabled attribute.") // Empty string value is convention
        assertEquals("true", modifier.getAttribute("aria-disabled"), "Disabled modifier should set aria-disabled to true.")
        assertEquals("-1", modifier.getAttribute("tabindex"), "Disabled modifier should set tabindex to -1.")
    }

    @Test
    fun testAutoFocusModifier() {
        val modifier = Modifier().autoFocus()
        assertEquals("", modifier.getAttribute("autofocus"), "AutoFocus modifier failed.") // Empty string value is convention
    }
    
    // Add tests for other ARIA attributes (ariaPressed, ariaExpanded, etc.) if desired,
    // following the pattern of ariaLabel/ariaHidden.
} 