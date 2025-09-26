package code.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.core.FlowContentCompat
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.FormContent
import code.yousef.summon.runtime.MockPlatformRenderer
import code.yousef.summon.util.runComposableTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.*
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

// Mock Renderer implementing PlatformRenderer, focusing on renderTimePicker
class MockTimePickerRenderer : MockPlatformRenderer() {
    var lastValue: LocalTime? = null
    var lastOnValueChange: ((LocalTime?) -> Unit)? = null
    var lastEnabled: Boolean? = null
    var lastIs24Hour: Boolean? = null
    var lastModifier: Modifier? = null
    override var renderTimePickerCalled = false

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
        renderTimePickerCalled = true
        lastValue = value
        lastOnValueChange = onValueChange
        lastEnabled = enabled
        lastIs24Hour = is24Hour
        lastModifier = modifier
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // (Copied from DatePickerTest)
    override fun renderText(text: String, modifier: Modifier) {}
    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
    override fun renderButton(
        onClick: () -> Unit,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
    }
    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
    override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<RendererSelectOption<T>>, modifier: Modifier) {}
    override fun renderDatePicker(value: LocalDate?, onValueChange: (LocalDate?) -> Unit, enabled: Boolean, min: LocalDate?, max: LocalDate?, modifier: Modifier) {}
    override fun renderTextArea(value: String, onValueChange: (String) -> Unit, enabled: Boolean, readOnly: Boolean, rows: Int?, maxLength: Int?, placeholder: String?, modifier: Modifier) {}
    override fun addHeadElement(content: String) {}
    override fun getHeadElements(): List<String> = emptyList()
    override fun renderComposableRoot(composable: @Composable () -> Unit): String = ""
    override fun renderComposable(composable: @Composable () -> Unit) {}
    override fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderImage(src: String, alt: String?, modifier: Modifier) {}
    override fun renderIcon(name: String, modifier: Modifier, onClick: (() -> Unit)?, svgContent: String?, type: IconType) {}
    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
    }

    override fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {}
    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}
    override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = {}
    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {}
    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
    }
    override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {}
    override fun renderSpacer(modifier: Modifier) {}
    override fun renderRangeSlider(value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit, valueRange: ClosedFloatingPointRange<Float>, steps: Int, enabled: Boolean, modifier: Modifier) {}
    override fun renderSlider(value: Float, onValueChange: (Float) -> Unit, valueRange: ClosedFloatingPointRange<Float>, steps: Int, enabled: Boolean, modifier: Modifier) {}
    override fun renderSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {}
    // renderTimePicker implemented above
    override fun renderAspectRatio(
        ratio: Float,
        modifier: Modifier,
        content: @Composable FlowContentCompat.() -> Unit
    ) {
    }

    override fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderLink(href: String, modifier: Modifier) {}
    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}
    override fun renderEnhancedLink(href: String, target: String?, title: String?, ariaLabel: String?, ariaDescribedBy: String?, modifier: Modifier) {}
    override fun renderTabLayout(tabs: List<Tab>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {}
    override fun renderTabLayout(modifier: Modifier, content: @Composable (() -> Unit)) {}
    override fun renderTabLayout(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit, modifier: Modifier, content: () -> Unit) {}
    fun renderTab(tab: Tab, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {}
    override fun renderDivider(modifier: Modifier) {}
    fun renderSnackbarHost(hostState: Any, modifier: Modifier) {}
    override fun renderExpansionPanel(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit)) {}
    fun renderExpansionPanel(
        expanded: Boolean,
        onExpansionChange: (Boolean) -> Unit,
        modifier: Modifier,
        header: @Composable FlowContentCompat.() -> Unit,
        body: @Composable FlowContentCompat.() -> Unit
    ) {
    }

    override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    fun renderBasicText(text: String, modifier: Modifier) {}
    override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
    override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable() () -> Unit) {}
    override fun renderAnimatedContent(modifier: Modifier) {}
    override fun renderAnimatedContent(modifier: Modifier, content: @Composable() () -> Unit) {}
    override fun renderInline(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
    override fun renderSpan(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
    override fun renderLazyColumn(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
    override fun renderLazyRow(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
    override fun renderResponsiveLayout(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
    override fun renderHtmlTag(
        tagName: String,
        modifier: Modifier,
        content: @Composable() FlowContentCompat.() -> Unit
    ) {
    }
}

class TimePickerTest {

    @Test
    fun testBasicTimePickerRendering() {
        val mockRenderer = MockTimePickerRenderer()
        var selectedTime: LocalTime? = LocalTime(14, 30, 0)
        val testModifier = Modifier()
        val onValChange: (LocalTime?) -> Unit = { selectedTime = it }

        runComposableTest(mockRenderer) {
            TimePicker(
                value = selectedTime,
                onValueChange = onValChange,
                is24Hour = true,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderTimePickerCalled, "renderTimePicker should be called")
        assertEquals(selectedTime, mockRenderer.lastValue, "Initial value mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
        assertEquals(true, mockRenderer.lastIs24Hour, "is24Hour format mismatch")
        // Assume TimePicker doesn't add default styles/attributes
        assertSame(testModifier, mockRenderer.lastModifier, "Modifier mismatch")

        // Simulate value change
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should not be null")
        val newTime = LocalTime(9, 15, 30)
        mockRenderer.lastOnValueChange?.invoke(newTime)
        assertEquals(newTime, selectedTime, "Time value did not update after callback")

        // Simulate clearing the time
        mockRenderer.lastOnValueChange?.invoke(null)
        assertNull(selectedTime, "Time value did not clear after callback with null")
    }

    @Test
    fun testDisabledTimePicker() {
        val mockRenderer = MockTimePickerRenderer()
        var selectedTime: LocalTime? = LocalTime(10, 0, 0)
        val onValChange: (LocalTime?) -> Unit = { selectedTime = it }

        runComposableTest(mockRenderer) {
            TimePicker(
                value = selectedTime,
                onValueChange = onValChange,
                enabled = false // Explicitly disable
            )
        }

        assertTrue(mockRenderer.renderTimePickerCalled, "renderTimePicker should be called")
        assertEquals(false, mockRenderer.lastEnabled, "TimePicker should be rendered as disabled")
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should be captured")

        // Attempt to invoke the callback
        val originalValue = selectedTime
        mockRenderer.lastOnValueChange?.invoke(LocalTime(11, 30, 0))

        // Assert that the external callback variable was NOT changed
        assertEquals(originalValue, selectedTime, "onValueChange callback should not be invoked when the TimePicker is disabled")
    }

    @Test
    fun testStatefulTimePicker() {
        val mockRenderer = MockTimePickerRenderer()
        val initialValue = LocalTime(8, 0, 0)
        var externalValue: LocalTime? = null // Initialize to null
        val onValChangeExternal: (LocalTime?) -> Unit = { externalValue = it }

        runComposableTest(mockRenderer) {
            StatefulTimePicker(
                initialValue = initialValue,
                onValueChange = onValChangeExternal,
                is24Hour = false,
                enabled = true,
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderTimePickerCalled, "renderTimePicker should be called for StatefulTimePicker")
        assertEquals(initialValue, mockRenderer.lastValue, "Initial value for StatefulTimePicker mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state for StatefulTimePicker mismatch")
        assertEquals(false, mockRenderer.lastIs24Hour, "is24Hour format for StatefulTimePicker mismatch")
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should not be null for StatefulTimePicker")

        // Simulate internal state update via renderer callback
        val newValue = LocalTime(17, 45, 15)
        mockRenderer.lastOnValueChange?.invoke(newValue)

        // Check external callback was triggered
        assertEquals(newValue, externalValue, "External onValueChange callback mismatch for StatefulTimePicker")

        // Simulate clearing the value
        mockRenderer.lastOnValueChange?.invoke(null)
        assertNull(externalValue, "External callback did not receive null when value cleared")
    }
} 