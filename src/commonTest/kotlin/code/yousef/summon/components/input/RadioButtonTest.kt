package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.FormContent
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.util.runTestComposable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

class MockRadioButtonRenderer : PlatformRenderer {
    var lastSelected: Boolean? = null
    var lastOnClick: (() -> Unit)? = null
    var lastEnabled: Boolean? = null
    var lastModifier: Modifier? = null
    var renderRadioButtonCalled = false

    override fun renderRadioButton(
        selected: Boolean,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderRadioButtonCalled = true
        lastSelected = selected
        lastOnClick = onClick
        lastEnabled = enabled
        lastModifier = modifier
    }

    override fun renderText(text: String, modifier: Modifier) {}
    override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
    override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
    override fun <T> renderSelect(
        selectedValue: T?,
        onSelectedChange: (T?) -> Unit,
        options: List<RendererSelectOption<T>>,
        modifier: Modifier
    ) {
    }

    override fun renderDatePicker(
        value: LocalDate?,
        onValueChange: (LocalDate?) -> Unit,
        enabled: Boolean,
        min: LocalDate?,
        max: LocalDate?,
        modifier: Modifier
    ) {
    }

    override fun renderTextArea(
        value: String,
        onValueChange: (String) -> Unit,
        enabled: Boolean,
        readOnly: Boolean,
        rows: Int?,
        maxLength: Int?,
        placeholder: String?,
        modifier: Modifier
    ) {
    }

    override fun addHeadElement(content: String) {}
    override fun getHeadElements(): List<String> = emptyList()
    override fun renderComposableRoot(composable: @Composable () -> Unit): String = ""
    override fun renderComposable(composable: @Composable () -> Unit) {}
    override fun renderRow(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderImage(src: String, alt: String, modifier: Modifier) {}
    override fun renderIcon(
        name: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        svgContent: String?,
        type: IconType
    ) {
    }

    override fun renderAlertContainer(
        variant: AlertVariant?,
        modifier: Modifier,
        content: @Composable FlowContent.() -> Unit
    ) {
    }

    override fun renderBadge(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
    }

    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}
    override fun renderFileUpload(
        onFilesSelected: (List<FileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit = {}

    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {}
    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContent.() -> Unit
    ) {
    }

    override fun renderSpacer(modifier: Modifier) {}
    override fun renderRangeSlider(
        value: ClosedFloatingPointRange<Float>,
        onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
    }

    override fun renderSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        valueRange: ClosedFloatingPointRange<Float>,
        steps: Int,
        enabled: Boolean,
        modifier: Modifier
    ) {
    }

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
    }

    override fun renderTimePicker(
        value: LocalTime?,
        onValueChange: (LocalTime?) -> Unit,
        enabled: Boolean,
        is24Hour: Boolean,
        modifier: Modifier
    ) {
    }

    override fun renderAspectRatio(ratio: Float, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderCard(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
    override fun renderLink(href: String, modifier: Modifier) {}
    override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}
    override fun renderEnhancedLink(
        href: String,
        target: String?,
        title: String?,
        ariaLabel: String?,
        ariaDescribedBy: String?,
        modifier: Modifier
    ) {
    }

    override fun renderTabLayout(
        tabs: List<Tab>,
        selectedTabIndex: Int,
        onTabSelected: (Int) -> Unit,
        modifier: Modifier
    ) {
    }

    override fun renderTabLayout(modifier: Modifier, content: @Composable (() -> Unit)) {}
    override fun renderTabLayout(
        tabs: List<String>,
        selectedTab: String,
        onTabSelected: (String) -> Unit,
        modifier: Modifier,
        content: () -> Unit
    ) {
    }

    fun renderTab(tab: Tab, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {}
    override fun renderDivider(modifier: Modifier) {}
    fun renderSnackbarHost(hostState: Any, modifier: Modifier) {}
    override fun renderExpansionPanel(modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {}
    fun renderExpansionPanel(
        expanded: Boolean,
        onExpansionChange: (Boolean) -> Unit,
        modifier: Modifier,
        header: @Composable FlowContent.() -> Unit,
        body: @Composable FlowContent.() -> Unit
    ) {
    }

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

class RadioButtonTest {

    @Test
    fun testBasicRadioButtonRendering() {
        val mockRenderer = MockRadioButtonRenderer()
        var clicked = false
        val testModifier = Modifier()
        val onClickLambda = { clicked = true }
        val expectedModifier = testModifier.cursor("pointer")

        runTestComposable(mockRenderer) {
            RadioButton(
                selected = false,
                onClick = onClickLambda,
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderRadioButtonCalled, "renderRadioButton should be called")
        assertEquals(false, mockRenderer.lastSelected, "Initial selected state mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
        assertEquals(expectedModifier.styles, mockRenderer.lastModifier?.styles, "Modifier styles mismatch")

        assertNotNull(mockRenderer.lastOnClick, "onClick callback should not be null")
        mockRenderer.lastOnClick?.invoke()
        assertTrue(clicked, "onClick callback was not invoked")
    }

    @Test
    fun testInitialSelectedState() {
        val mockRenderer = MockRadioButtonRenderer()
        val onClickLambda = { /* No-op */ }

        runTestComposable(mockRenderer) {
            RadioButton(
                selected = true, // Initial state is selected
                onClick = onClickLambda,
                enabled = true,
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRadioButtonCalled, "renderRadioButton should be called")
        assertEquals(true, mockRenderer.lastSelected, "Initial selected state should be true")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
    }

    @Test
    fun testDisabledRadioButton() {
        val mockRenderer = MockRadioButtonRenderer()
        var clicked = false
        val onClickLambda = { clicked = true }

        runTestComposable(mockRenderer) {
            RadioButton(
                selected = false,
                onClick = onClickLambda,
                enabled = false, // Explicitly disable the button
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderRadioButtonCalled, "renderRadioButton should be called")
        assertEquals(false, mockRenderer.lastSelected, "Initial selected state mismatch")
        assertEquals(false, mockRenderer.lastEnabled, "Button should be rendered as disabled")
        assertNotNull(mockRenderer.lastOnClick, "onClick callback should be captured")

        // Attempt to invoke the callback (simulating user interaction with a disabled button)
        mockRenderer.lastOnClick?.invoke()

        // Assert that the external callback variable was NOT changed
        assertFalse(clicked, "onClick callback should not be invoked when the button is disabled")
    }
}
