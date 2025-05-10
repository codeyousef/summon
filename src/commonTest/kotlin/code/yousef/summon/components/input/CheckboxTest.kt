package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.validation.Validator
import code.yousef.summon.validation.ValidationResult
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.runtime.SelectOption
import code.yousef.summon.runtime.MockPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * Tests for the Checkbox component
 */
class CheckboxTest {

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = true

        override fun startNode() {}
        override fun startGroup(key: Any?) {}
        override fun endNode() {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {}
        override fun endCompose() {}
        override fun <T> compose(composable: @Composable () -> T): T {
            @Suppress("UNCHECKED_CAST")
            return null as T
        }
    }

    /**
     * A mock validator for testing
     */
    private class MockValidator(private val isValid: Boolean, private val errorMessage: String) : Validator {
        override fun validate(value: String): ValidationResult {
            return ValidationResult(isValid, errorMessage)
        }
    }

    @Test
    fun testCheckboxWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Checkbox component with default parameters
            var checkedState = false
            Checkbox(
                checked = false,
                onCheckedChange = { checkedState = it }
            )

            // Verify that renderCheckbox was called
            assertTrue(mockRenderer.renderCheckboxCalled, "renderCheckbox should have been called")

            // Verify the checked state
            assertFalse(mockRenderer.lastCheckboxCheckedRendered!!, "Checkbox should be unchecked")

            // Verify the enabled state
            assertTrue(mockRenderer.lastCheckboxEnabledRendered!!, "Checkbox should be enabled")

            // Verify the modifier
            assertNotNull(mockRenderer.lastCheckboxModifierRendered, "Modifier should not be null")
            // The default modifier includes cursor=pointer
            val expectedStyles = mapOf("cursor" to "pointer")
            assertEquals(expectedStyles, mockRenderer.lastCheckboxModifierRendered!!.styles, "Modifier should have the expected default styles")

            // Verify the onCheckedChange callback
            assertNotNull(mockRenderer.lastCheckboxOnCheckedChangeRendered, "onCheckedChange should not be null")
            mockRenderer.lastCheckboxOnCheckedChangeRendered?.invoke(true)
            assertTrue(checkedState, "checkedState should be updated to true")
        }
    }

    @Test
    fun testCheckboxWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a custom Modifier
            val customModifier = Modifier()
                .padding("10px")
                .backgroundColor("#EFEFEF")
                .border("1px", "solid", "black")
                .borderRadius("5px")

            // Call the Checkbox component with custom modifier
            Checkbox(
                checked = true,
                onCheckedChange = {},
                modifier = customModifier
            )

            // Verify that renderCheckbox was called
            assertTrue(mockRenderer.renderCheckboxCalled, "renderCheckbox should have been called")

            // Verify the checked state
            assertTrue(mockRenderer.lastCheckboxCheckedRendered!!, "Checkbox should be checked")

            // Verify the modifier
            assertNotNull(mockRenderer.lastCheckboxModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastCheckboxModifierRendered!!.styles

            assertEquals("10px", styles["padding"], "padding should be '10px'")
            assertEquals("#EFEFEF", styles["background-color"], "background-color should be '#EFEFEF'")
            assertEquals("1px solid black", styles["border"], "border should be '1px solid black'")
            assertEquals("5px", styles["border-radius"], "border-radius should be '5px'")
        }
    }

    @Test
    fun testDisabledCheckbox() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Checkbox component with enabled=false
            var checkedState = false
            Checkbox(
                checked = false,
                onCheckedChange = { checkedState = it },
                enabled = false
            )

            // Verify that renderCheckbox was called
            assertTrue(mockRenderer.renderCheckboxCalled, "renderCheckbox should have been called")

            // Verify the enabled state
            assertFalse(mockRenderer.lastCheckboxEnabledRendered!!, "Checkbox should be disabled")

            // Verify the modifier
            assertNotNull(mockRenderer.lastCheckboxModifierRendered, "Modifier should not be null")
            val styles = mockRenderer.lastCheckboxModifierRendered!!.styles
            // In the actual implementation, the disabled state is handled differently
            // The cursor is set to "default" for disabled state
            assertEquals("default", styles["cursor"], "cursor should be 'default' for disabled state")
            // Check if pointer-events is set to "none" for disabled state
            assertEquals("none", styles["pointer-events"], "pointer-events should be 'none' for disabled state")

            // Verify the onCheckedChange callback doesn't update state when disabled
            mockRenderer.lastCheckboxOnCheckedChangeRendered?.invoke(true)
            assertFalse(checkedState, "checkedState should not be updated when disabled")
        }
    }

    @Test
    fun testCheckboxWithValidation() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a validator that fails when the checkbox is checked
            val validator = MockValidator(false, "Checkbox should not be checked")

            // Call the Checkbox component with validation
            var checkedState = false
            Checkbox(
                checked = false,
                onCheckedChange = { checkedState = it },
                validators = listOf(validator)
            )

            // Verify that renderCheckbox was called
            assertTrue(mockRenderer.renderCheckboxCalled, "renderCheckbox should have been called")

            // Verify the onCheckedChange callback still updates state even with validation errors
            mockRenderer.lastCheckboxOnCheckedChangeRendered?.invoke(true)
            assertTrue(checkedState, "checkedState should be updated even with validation errors")
        }
    }

    @Test
    fun testStatefulCheckbox() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Track if the callback was called
            var callbackCalled = false
            var callbackValue = false

            // Call the StatefulCheckbox component
            StatefulCheckbox(
                initialChecked = true,
                onCheckedChange = { 
                    callbackCalled = true
                    callbackValue = it
                }
            )

            // Verify that renderCheckbox was called
            assertTrue(mockRenderer.renderCheckboxCalled, "renderCheckbox should have been called")

            // Verify the checked state
            assertTrue(mockRenderer.lastCheckboxCheckedRendered!!, "StatefulCheckbox should be initially checked")

            // Verify the onCheckedChange callback
            assertNotNull(mockRenderer.lastCheckboxOnCheckedChangeRendered, "onCheckedChange should not be null")
            mockRenderer.lastCheckboxOnCheckedChangeRendered?.invoke(false)
            assertTrue(callbackCalled, "onCheckedChange callback should have been called")
            assertFalse(callbackValue, "callbackValue should be updated to false")
        }
    }
}
