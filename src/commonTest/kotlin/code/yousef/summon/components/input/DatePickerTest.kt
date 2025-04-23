package code.yousef.summon.components.input

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.DatePicker // Import the component
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.cursor
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.util.TestComposer
import code.yousef.summon.util.runTestComposable
import kotlinx.datetime.LocalDate

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.attribute
import code.yousef.summon.modifier.attributes
import code.yousef.summon.modifier.pointerEvents
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertNull

// Mock Renderer implementing PlatformRenderer, focusing on renderDatePicker
class MockDatePickerRenderer : PlatformRenderer {
    var lastValue: LocalDate? = null
    var lastOnValueChange: ((LocalDate?) -> Unit)? = null
    var lastEnabled: Boolean? = null
    var lastMinDate: LocalDate? = null
    var lastMaxDate: LocalDate? = null
    var lastModifier: Modifier? = null
    var renderDatePickerCalled = false

    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
        renderDatePickerCalled = true
        lastValue = value
        lastOnValueChange = onValueChange
        lastEnabled = enabled
        lastMinDate = min
        lastMaxDate = max
        lastModifier = modifier
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // (Copied from RangeSliderTest)
    override fun renderText(text: String, modifier: Modifier) {}
    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
    override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
    override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<RendererSelectOption<T>>, modifier: Modifier) {}
    // renderDatePicker implemented above
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
    override fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {}
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
    override fun renderTabLayout(modifier: Modifier, content: @Composable (() -> Unit)) {}
    override fun renderTabLayout(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit, modifier: Modifier, content: () -> Unit) {}
    fun renderTab(tab: Tab, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {}
    override fun renderDivider(modifier: Modifier) {}
    fun renderSnackbarHost(hostState: Any, modifier: Modifier) {}
    override fun renderExpansionPanel(modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {}
    fun renderExpansionPanel(expanded: Boolean, onExpansionChange: (Boolean) -> Unit, modifier: Modifier, header: @Composable FlowContent.() -> Unit, body: @Composable FlowContent.() -> Unit) {}
    override fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    fun renderBasicText(text: String, modifier: Modifier) {}
    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable() () -> Unit) {}
    override fun renderAnimatedContent(modifier: Modifier) {}
    override fun renderAnimatedContent(modifier: Modifier, content: @Composable() () -> Unit) {}
    override fun renderInline(modifier: Modifier, content: @Composable() FlowContent.() -> Unit) {}
    override fun renderSpan(modifier: Modifier, content: @Composable() FlowContent.() -> Unit) {}
    override fun renderLazyColumn(modifier: Modifier, content: @Composable() FlowContent.() -> Unit) {}
    override fun renderLazyRow(modifier: Modifier, content: @Composable() FlowContent.() -> Unit) {}
    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable() FlowContent.() -> Unit) {}
    override fun renderHtmlTag(tagName: String, modifier: Modifier, content: @Composable() FlowContent.() -> Unit) {}
}

class DatePickerTest {

    @Test
    fun testBasicDatePickerRendering() {
        val mockRenderer = MockDatePickerRenderer()
        var selectedDate: LocalDate? = LocalDate(2024, 1, 15)
        val minDate = LocalDate(2024, 1, 1)
        val maxDate = LocalDate(2024, 12, 31)
        val testModifier = Modifier()
        val onValChange: (LocalDate?) -> Unit = { selectedDate = it }

        runTestComposable(mockRenderer) {
            DatePicker(
                value = selectedDate,
                onValueChange = onValChange,
                minDate = minDate,
                maxDate = maxDate,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderDatePickerCalled, "renderDatePicker should be called")
        assertEquals(selectedDate, mockRenderer.lastValue, "Initial value mismatch")
        assertEquals(minDate, mockRenderer.lastMinDate, "Min date mismatch")
        assertEquals(maxDate, mockRenderer.lastMaxDate, "Max date mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
        
        // Check modifier - DatePicker applies opacity and cursor styles
        val expectedModifier = testModifier
            .opacity(1f) // enabled = true
            .cursor("pointer") // enabled = true
            // .pointerEvents("none") // Not applied when enabled
            .attribute("data-date-format", "yyyy-MM-dd") // Default format
            .attribute("data-min-date", minDate.toString())
            .attribute("data-max-date", maxDate.toString())
            // Add other attributes like label, initialDisplayMonth if testing them
            
        assertEquals(expectedModifier.styles, mockRenderer.lastModifier?.styles, "Modifier styles mismatch")
        assertEquals(expectedModifier.attributes, mockRenderer.lastModifier?.attributes, "Modifier attributes mismatch")

        // Simulate value change
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should not be null")
        val newDate = LocalDate(2024, 5, 20)
        mockRenderer.lastOnValueChange?.invoke(newDate)
        assertEquals(newDate, selectedDate, "Date value did not update after callback")

        // Simulate clearing the date
        mockRenderer.lastOnValueChange?.invoke(null)
        assertNull(selectedDate, "Date value did not clear after callback with null")
    }

    @Test
    fun testDisabledDatePicker() {
        val mockRenderer = MockDatePickerRenderer()
        var selectedDate: LocalDate? = LocalDate(2024, 1, 15)
        val onValChange: (LocalDate?) -> Unit = { selectedDate = it }

        runTestComposable(mockRenderer) {
            DatePicker(
                value = selectedDate,
                onValueChange = onValChange,
                enabled = false // Explicitly disable
            )
        }

        assertTrue(mockRenderer.renderDatePickerCalled, "renderDatePicker should be called")
        assertEquals(false, mockRenderer.lastEnabled, "DatePicker should be rendered as disabled")
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should be captured")

        // Check modifier for disabled state
        val expectedModifier = Modifier()
            .opacity(0.6f) // disabled
            .cursor("default") // disabled
            .pointerEvents("none") // disabled
            .attribute("data-date-format", "yyyy-MM-dd") // Default format
        // min/max attributes not added if null
            
        assertEquals(expectedModifier.styles, mockRenderer.lastModifier?.styles, "Disabled modifier styles mismatch")
        assertEquals(expectedModifier.attributes, mockRenderer.lastModifier?.attributes, "Disabled modifier attributes mismatch")

        // Attempt to invoke the callback
        val originalValue = selectedDate
        mockRenderer.lastOnValueChange?.invoke(LocalDate(2024, 6, 1))

        // Assert that the external callback variable was NOT changed
        assertEquals(originalValue, selectedDate, "onValueChange callback should not be invoked when the DatePicker is disabled")
    }

    @Test
    fun testDatePickerRangeValidation() {
        val mockRenderer = MockDatePickerRenderer()
        var selectedDate: LocalDate? = LocalDate(2024, 3, 15)
        val minDate = LocalDate(2024, 2, 1)
        val maxDate = LocalDate(2024, 4, 30)
        val onValChange: (LocalDate?) -> Unit = { selectedDate = it }

        runTestComposable(mockRenderer) {
            DatePicker(
                value = selectedDate,
                onValueChange = onValChange,
                minDate = minDate,
                maxDate = maxDate,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderDatePickerCalled, "renderDatePicker should be called")
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should not be null")

        // 1. Try date before minDate - should not update
        val dateBeforeMin = LocalDate(2024, 1, 20)
        val valueBeforeAttempt = selectedDate
        mockRenderer.lastOnValueChange?.invoke(dateBeforeMin)
        assertEquals(valueBeforeAttempt, selectedDate, "Date should not update when selected date is before minDate")

        // 2. Try date after maxDate - should not update
        val dateAfterMax = LocalDate(2024, 5, 5)
        mockRenderer.lastOnValueChange?.invoke(dateAfterMax)
        assertEquals(valueBeforeAttempt, selectedDate, "Date should not update when selected date is after maxDate")

        // 3. Try date within range - should update
        val dateInRange = LocalDate(2024, 3, 10)
        mockRenderer.lastOnValueChange?.invoke(dateInRange)
        assertEquals(dateInRange, selectedDate, "Date should update when selected date is within range")

        // 4. Try null date - should update (clear value)
        mockRenderer.lastOnValueChange?.invoke(null)
        assertNull(selectedDate, "Date should clear when null is selected")
    }
} 