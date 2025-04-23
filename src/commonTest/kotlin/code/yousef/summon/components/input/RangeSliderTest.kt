package code.yousef.summon.components.input

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.RangeSlider // Import the component
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.util.TestComposer
import code.yousef.summon.util.runTestComposable

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertSame

// Mock Renderer implementing PlatformRenderer, focusing on renderRangeSlider
class MockRangeSliderRenderer : PlatformRenderer {
    var lastValue: ClosedFloatingPointRange<Float>? = null
    var lastOnValueChange: ((ClosedFloatingPointRange<Float>) -> Unit)? = null
    var lastValueRange: ClosedFloatingPointRange<Float>? = null
    var lastSteps: Int? = null
    var lastEnabled: Boolean? = null
    var lastModifier: Modifier? = null
    var renderRangeSliderCalled = false

    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderRangeSliderCalled = true
        lastValue = value
        lastOnValueChange = onValueChange
        lastValueRange = valueRange
        lastSteps = steps
        lastEnabled = enabled
        lastModifier = modifier
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // (Copied from SliderTest)
    override fun renderText(text: String, modifier: Modifier) {}
    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
    override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
    override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<RendererSelectOption<T>>, modifier: Modifier) {}
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
    override fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {}
    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}
    override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = {}
    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {}
    override fun renderFormField(modifier: Modifier, labelId: String?, isRequired: Boolean, isError: Boolean, errorMessageId: String?, content: @Composable FlowContent.() -> Unit) {}
    override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {}
    override fun renderSpacer(modifier: Modifier) {}
    // renderRangeSlider implemented above
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

class RangeSliderTest {

    @Test
    fun testBasicRangeSliderRendering() {
        val mockRenderer = MockRangeSliderRenderer()
        var sliderRangeValue = 0.2f..0.7f
        val valueRange = 0f..1f
        val steps = 5
        val testModifier = Modifier()
        val onValChange: (ClosedFloatingPointRange<Float>) -> Unit = { sliderRangeValue = it }

        runTestComposable(mockRenderer) {
            RangeSlider(
                value = sliderRangeValue,
                onValueChange = onValChange,
                valueRange = valueRange,
                steps = steps,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderRangeSliderCalled, "renderRangeSlider should be called")
        assertEquals(0.2f..0.7f, mockRenderer.lastValue, "Initial value mismatch")
        assertEquals(valueRange, mockRenderer.lastValueRange, "Value range mismatch")
        assertEquals(steps, mockRenderer.lastSteps, "Steps mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
        // Assuming RangeSlider doesn't add default styles
        assertSame(testModifier, mockRenderer.lastModifier, "Modifier mismatch") 

        // Simulate value change
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should not be null")
        val newValue = 0.3f..0.8f
        mockRenderer.lastOnValueChange?.invoke(newValue)
        assertEquals(newValue, sliderRangeValue, "Slider range value did not update after callback")
    }

    @Test
    fun testDisabledRangeSlider() {
        val mockRenderer = MockRangeSliderRenderer()
        var sliderRangeValue = 0.2f..0.7f
        val valueRange = 0f..1f
        val steps = 5
        val onValChange: (ClosedFloatingPointRange<Float>) -> Unit = { sliderRangeValue = it }

        runTestComposable(mockRenderer) {
            RangeSlider(
                value = sliderRangeValue,
                onValueChange = onValChange,
                valueRange = valueRange,
                steps = steps,
                enabled = false, // Explicitly disable
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRangeSliderCalled, "renderRangeSlider should be called")
        assertEquals(false, mockRenderer.lastEnabled, "Slider should be rendered as disabled")
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should be captured")

        // Attempt to invoke the callback
        val originalValue = sliderRangeValue
        val newValue = 0.3f..0.8f
        mockRenderer.lastOnValueChange?.invoke(newValue)

        // Assert that the external callback variable was NOT changed
        assertEquals(originalValue, sliderRangeValue, "onValueChange callback should not be invoked when the slider is disabled")
    }

    @Test
    fun testStatefulRangeSlider() {
        val mockRenderer = MockRangeSliderRenderer()
        val initialValue = 0.1f..0.6f
        var externalValue: ClosedFloatingPointRange<Float>? = null // Initialize to null
        val valueRange = 0f..1f
        val steps = 10
        val onValChangeExternal: (ClosedFloatingPointRange<Float>) -> Unit = { externalValue = it }

        runTestComposable(mockRenderer) {
            StatefulRangeSlider(
                initialValue = initialValue,
                onValueChange = onValChangeExternal,
                valueRange = valueRange,
                steps = steps,
                isEnabled = true, // Use isEnabled parameter name from StatefulRangeSlider
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRangeSliderCalled, "renderRangeSlider should be called for StatefulRangeSlider")
        assertEquals(initialValue, mockRenderer.lastValue, "Initial value for StatefulRangeSlider mismatch")
        assertEquals(valueRange, mockRenderer.lastValueRange, "Value range for StatefulRangeSlider mismatch")
        assertEquals(steps, mockRenderer.lastSteps, "Steps for StatefulRangeSlider mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state for StatefulRangeSlider mismatch")
        assertNotNull(mockRenderer.lastOnValueChange, "onValueChange callback should not be null for StatefulRangeSlider")

        // Simulate internal state update via renderer callback
        val newValue = 0.4f..0.9f
        mockRenderer.lastOnValueChange?.invoke(newValue)

        // Check external callback was triggered
        assertEquals(newValue, externalValue, "External onValueChange callback mismatch for StatefulRangeSlider")
    }
} 