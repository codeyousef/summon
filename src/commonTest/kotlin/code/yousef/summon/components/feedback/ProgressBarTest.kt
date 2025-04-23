package code.yousef.summon.components.feedback

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.ProgressBar // Import the component
import code.yousef.summon.components.feedback.ProgressType // Import ProgressType
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.util.runTestComposable
import code.yousef.summon.util.TestFileInfo // Import for consistency in mock renderer boilerplate

// Import other types used in PlatformRenderer methods for the mock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDate
import kotlinx.html.FlowContent
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.components.input.FileInfo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlin.test.assertSame

// Mock Renderer implementing PlatformRenderer, focusing on renderProgress
class MockProgressBarRenderer : PlatformRenderer {
    var renderProgressCalled = false
    var lastProgressValue: Float? = null
    var lastProgressType: ProgressType? = null
    var lastModifier: Modifier? = null

    override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
        renderProgressCalled = true
        lastProgressValue = value
        lastProgressType = type
        lastModifier = modifier
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // (Copied from FormFieldTest - adjust if needed)
    override fun renderFormField(modifier: Modifier, labelId: String?, isRequired: Boolean, isError: Boolean, errorMessageId: String?, content: @Composable FlowContent.() -> Unit) {}
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
    // renderProgress implemented above
    override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = { /* No-op */ }
    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {}
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


class ProgressBarTest {

    @Test
    fun testProgressBarWithSpecificValue() {
        val mockRenderer = MockProgressBarRenderer()
        val testProgress = 0.75f
        val testModifier = Modifier().margin("5px") // Example custom modifier

        runTestComposable(mockRenderer) {
            ProgressBar(
                progress = testProgress,
                modifier = testModifier
            )
        }

        assertTrue(mockRenderer.renderProgressCalled, "renderProgress should be called")
        assertEquals(testProgress, mockRenderer.lastProgressValue, "Progress value mismatch")
        assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressType, "Progress type should be LINEAR")
        assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")

        // Verify default styles are applied ON TOP of the passed modifier
        val capturedModifier = mockRenderer.lastModifier!!
        // Check custom style from testModifier
        assertEquals("5px", capturedModifier.styles["margin"], "Custom modifier style (margin) not found")
        // Check default styles applied by ProgressBar
        assertEquals("8px", capturedModifier.styles["height"], "Default height style not applied/overridden")
        assertEquals("#f0f0f0", capturedModifier.styles["background-color"], "Default background style not applied/overridden")
        assertEquals("4px", capturedModifier.styles["border-radius"], "Default border-radius style not applied/overridden")
    }

    @Test
    fun testProgressBarIndeterminate() {
        val mockRenderer = MockProgressBarRenderer()

        runTestComposable(mockRenderer) {
            ProgressBar(
                progress = null // Indeterminate state
            )
        }

        assertTrue(mockRenderer.renderProgressCalled, "renderProgress should be called for indeterminate")
        assertNull(mockRenderer.lastProgressValue, "Progress value should be null for indeterminate")
        assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressType, "Progress type should be LINEAR for indeterminate")
        assertNotNull(mockRenderer.lastModifier, "Modifier should not be null for indeterminate")

        // Verify default styles are applied (no custom modifier passed)
        val capturedModifier = mockRenderer.lastModifier!!
        assertEquals("8px", capturedModifier.styles["height"], "Default height style not applied")
        assertEquals("#f0f0f0", capturedModifier.styles["background-color"], "Default background style not applied")
        assertEquals("4px", capturedModifier.styles["border-radius"], "Default border-radius style not applied")
    }
} 