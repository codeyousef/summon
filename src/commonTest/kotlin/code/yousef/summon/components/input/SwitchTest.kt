package code.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock
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
import kotlin.test.*
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

// Mock Renderer implementing PlatformRenderer, focusing on renderSwitch
class MockSwitchRenderer : PlatformRenderer {
    var lastChecked: Boolean? = null
    var lastOnCheckedChange: ((Boolean) -> Unit)? = null
    var lastEnabled: Boolean? = null
    var lastModifier: Modifier? = null
    var renderSwitchCalled = false

    override fun renderSwitch(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        modifier: Modifier
    ) {
        renderSwitchCalled = true
        lastChecked = checked
        lastOnCheckedChange = onCheckedChange
        lastEnabled = enabled
        lastModifier = modifier
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // (Copied from TextAreaTest)
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

    override fun renderRadioButton(selected: Boolean, onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {}
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

    // renderSwitch implemented above
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

class SwitchTest {

    @Test
    fun testBasicSwitchRendering() {
        val mockRenderer = MockSwitchRenderer()
        var checkedState = false
        val testModifier = Modifier()

        runTestComposable(mockRenderer) {
            Switch(
                checked = checkedState,
                onCheckedChange = { checkedState = it },
                modifier = testModifier,
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderSwitchCalled, "renderSwitch should be called")
        assertEquals(false, mockRenderer.lastChecked, "Initial checked state mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
        assertSame(testModifier, mockRenderer.lastModifier, "Modifier mismatch")

        // Simulate state change
        assertNotNull(mockRenderer.lastOnCheckedChange, "onCheckedChange callback should not be null")
        mockRenderer.lastOnCheckedChange?.invoke(true)
        assertEquals(true, checkedState, "Checked state did not update after callback")

        // Recompose with new state (simulate by running composable again)
        runTestComposable(mockRenderer) {
            Switch(
                checked = checkedState, // Now true
                onCheckedChange = { checkedState = it },
                modifier = testModifier,
                enabled = false // Change enabled state for verification
            )
        }
        assertEquals(true, mockRenderer.lastChecked, "Updated checked state mismatch")
        assertEquals(false, mockRenderer.lastEnabled, "Updated enabled state mismatch")
    }

    @Test
    fun testStatefulSwitch() {
        val mockRenderer = MockSwitchRenderer()
        var lastCallbackValue: Boolean? = null

        runTestComposable(mockRenderer) {
            StatefulSwitch(
                initialChecked = true,
                onCheckedChange = { lastCallbackValue = it },
                enabled = true
            )
        }

        assertTrue(mockRenderer.renderSwitchCalled, "renderSwitch should be called for StatefulSwitch")
        assertEquals(true, mockRenderer.lastChecked, "Initial checked state for StatefulSwitch mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state for StatefulSwitch mismatch")
        assertNotNull(
            mockRenderer.lastOnCheckedChange,
            "onCheckedChange callback should not be null for StatefulSwitch"
        )

        // Simulate user toggling the switch via the renderer's callback
        mockRenderer.lastOnCheckedChange?.invoke(false)

        // Verify the external callback was triggered
        assertEquals(false, lastCallbackValue, "onCheckedChange callback mismatch for StatefulSwitch")
    }

    @Test
    fun testStatefulSwitchDefaultInitialState() {
        val mockRenderer = MockSwitchRenderer()
        runTestComposable(mockRenderer) {
            StatefulSwitch() // Uses default initialChecked = false
        }
        assertTrue(mockRenderer.renderSwitchCalled)
        assertFalse(mockRenderer.lastChecked ?: true, "Default initial checked state should be false")
    }

    @Test
    fun testDisabledSwitchDoesNotCallCallback() {
        val mockRenderer = MockSwitchRenderer()
        var callbackCalled = false
        val onValChange: (Boolean) -> Unit = { callbackCalled = true }

        runTestComposable(mockRenderer) {
            Switch(
                checked = false,
                onCheckedChange = onValChange,
                enabled = false, // Explicitly disable the switch
                modifier = Modifier()
            )
        }

        assertTrue(mockRenderer.renderSwitchCalled, "renderSwitch should be called")
        assertEquals(false, mockRenderer.lastEnabled, "Switch should be rendered as disabled")
        assertNotNull(mockRenderer.lastOnCheckedChange, "onCheckedChange callback should be captured")

        // Attempt to invoke the callback (simulating user interaction with a disabled switch)
        mockRenderer.lastOnCheckedChange?.invoke(true)

        // Assert that the external callback variable was NOT changed
        assertFalse(callbackCalled, "onCheckedChange callback should not be invoked when the switch is disabled")
    }
} 