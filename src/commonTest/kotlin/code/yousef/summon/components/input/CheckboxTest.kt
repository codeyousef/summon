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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * Tests for the Checkbox component
 */
class CheckboxTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderCheckboxCalled = false
        var lastChecked: Boolean? = null
        var lastEnabled: Boolean? = null
        var lastModifier: Modifier? = null
        var lastOnCheckedChange: ((Boolean) -> Unit)? = null

        override fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {
            renderCheckboxCalled = true
            lastChecked = checked
            lastEnabled = enabled
            lastModifier = modifier
            lastOnCheckedChange = onCheckedChange
        }

        // Minimal implementations for other required methods
        override fun renderText(text: String, modifier: Modifier) {}
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
        override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
        override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<SelectOption<T>>, modifier: Modifier) {}
        override fun renderDatePicker(value: LocalDate?, onValueChange: (LocalDate?) -> Unit, enabled: Boolean, min: LocalDate?, max: LocalDate?, modifier: Modifier) {}
        override fun renderTextArea(value: String, onValueChange: (String) -> Unit, enabled: Boolean, readOnly: Boolean, rows: Int?, maxLength: Int?, placeholder: String?, modifier: Modifier) {}
        override fun addHeadElement(content: String) {}
        override fun getHeadElements(): List<String> = emptyList()
        override fun renderComposableRoot(composable: @Composable () -> Unit): String = ""
        override fun renderComposable(composable: @Composable () -> Unit) {}
        override fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderImage(src: String, alt: String, modifier: Modifier) {}
        override fun renderIcon(name: String, modifier: Modifier, onClick: (() -> Unit)?, svgContent: String?, type: IconType) {}
        override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderBadge(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}
        override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = {}
        override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {}
        override fun renderFormField(modifier: Modifier, labelId: String?, isRequired: Boolean, isError: Boolean, errorMessageId: String?, content: @Composable FlowContent.() -> Unit) {}
        override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {}
        override fun renderSpacer(modifier: Modifier) {}
        override fun renderRangeSlider(value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit, valueRange: ClosedFloatingPointRange<Float>, steps: Int, enabled: Boolean, modifier: Modifier) {}
        override fun renderSlider(value: Float, onValueChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float>, steps: Int, enabled: Boolean, modifier: Modifier) {}
        override fun renderSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {}
        override fun renderTimePicker(value: LocalTime?, onValueChange: (LocalTime?) -> Unit, enabled: Boolean, is24Hour: Boolean, modifier: Modifier) {}
        override fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderCard(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderLink(href: String, modifier: Modifier) {}
        override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}
        override fun renderEnhancedLink(href: String, target: String?, title: String?, ariaLabel: String?, ariaDescribedBy: String?, modifier: Modifier) {}
        override fun renderTabLayout(tabs: List<Tab>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {}
        override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderTabLayout(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit, modifier: Modifier, content: () -> Unit) {}
        override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
        override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderAnimatedContent(modifier: Modifier) {}
        override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderInline(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderDivider(modifier: Modifier) {}
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderLazyRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    }

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
            assertFalse(mockRenderer.lastChecked!!, "Checkbox should be unchecked")

            // Verify the enabled state
            assertTrue(mockRenderer.lastEnabled!!, "Checkbox should be enabled")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            // The default modifier includes cursor=pointer
            val expectedStyles = mapOf("cursor" to "pointer")
            assertEquals(expectedStyles, mockRenderer.lastModifier?.styles, "Modifier should have the expected default styles")

            // Verify the onCheckedChange callback
            assertNotNull(mockRenderer.lastOnCheckedChange, "onCheckedChange should not be null")
            mockRenderer.lastOnCheckedChange?.invoke(true)
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
            assertTrue(mockRenderer.lastChecked!!, "Checkbox should be checked")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

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
            assertFalse(mockRenderer.lastEnabled!!, "Checkbox should be disabled")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles
            // In the actual implementation, the disabled state is handled differently
            // The cursor is set to "default" for disabled state
            assertEquals("default", styles["cursor"], "cursor should be 'default' for disabled state")
            // Check if pointer-events is set to "none" for disabled state
            assertEquals("none", styles["pointer-events"], "pointer-events should be 'none' for disabled state")

            // Verify the onCheckedChange callback doesn't update state when disabled
            mockRenderer.lastOnCheckedChange?.invoke(true)
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
            mockRenderer.lastOnCheckedChange?.invoke(true)
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
            assertTrue(mockRenderer.lastChecked!!, "StatefulCheckbox should be initially checked")

            // Verify the onCheckedChange callback
            assertNotNull(mockRenderer.lastOnCheckedChange, "onCheckedChange should not be null")
            mockRenderer.lastOnCheckedChange?.invoke(false)
            assertTrue(callbackCalled, "onCheckedChange callback should have been called")
            assertFalse(callbackValue, "callbackValue should be updated to false")
        }
    }
}
