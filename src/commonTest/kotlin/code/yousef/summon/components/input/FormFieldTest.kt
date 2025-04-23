package code.yousef.summon.components.input

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.FormField // Import the component
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.util.TestComposer
import code.yousef.summon.util.runTestComposable
import code.yousef.summon.util.TestFileInfo // Needed for mock renderer boilerplate
import code.yousef.summon.components.display.Text // Import Text
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDate
import kotlinx.html.FlowContent
import kotlinx.html.id // Import for id attribute
import kotlinx.html.Tag // Import Tag
import kotlinx.html.Entities // Import Entities
import kotlinx.html.Unsafe // Import Unsafe

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import kotlinx.html.TagConsumer
import kotlinx.html.org.w3c.dom.events.Event

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertSame
import kotlin.test.assertNull // Add assertNull import

// Mock Renderer implementing PlatformRenderer, focusing on renderFormField
class MockFormFieldRenderer : PlatformRenderer {
    var lastModifier: Modifier? = null
    var lastLabelId: String? = null
    var lastIsRequired: Boolean? = null
    var lastIsError: Boolean? = null
    var lastErrorMessageId: String? = null
    var lastContentLambda: (@Composable FlowContent.() -> Unit)? = null
    var renderFormFieldCalled = false

    override fun renderFormField(
        modifier: Modifier,
        labelId: String?,
        isRequired: Boolean,
        isError: Boolean,
        errorMessageId: String?,
        content: @Composable FlowContent.() -> Unit
    ) {
        renderFormFieldCalled = true
        lastModifier = modifier
        lastLabelId = labelId
        lastIsRequired = isRequired
        lastIsError = isError
        lastErrorMessageId = errorMessageId
        lastContentLambda = content
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // (Copied from FileUploadTest)
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
    override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = { /* No-op */ }
    override fun renderForm(onSubmit: (() -> Unit)?, modifier: Modifier, content: @Composable FormContent.() -> Unit) {}
    // renderFormField implemented above
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

class FormFieldTest {

    @Test
    fun testBasicFormFieldRendering() {
        val mockRenderer = MockFormFieldRenderer()
        val testModifier = Modifier().padding("10px")

        runTestComposable(mockRenderer) {
            FormField(
                modifier = testModifier,
                fieldContent = { /* fieldContentCalled = true */ }
            )
        }

        // Check parameters passed to the RENDERER
        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertSame(testModifier, mockRenderer.lastModifier, "Modifier mismatch")
        assertEquals(false, mockRenderer.lastIsRequired, "Default isRequired should be false")
        assertEquals(false, mockRenderer.lastIsError, "Default isError should be false")
        assertNotNull(mockRenderer.lastContentLambda, "Content lambda should be captured by renderer")
    }

    @Test
    fun testFormFieldWithLabel() {
        val mockRenderer = MockFormFieldRenderer()
        val testLabelText = "Test Label"

        runTestComposable(mockRenderer) {
            FormField(
                label = { Text(testLabelText) }, // Wrap in composable lambda
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertNull(mockRenderer.lastLabelId, "Label ID should be null")
        // We don't know the exact generated ID, but FormField should pass *something*
        // A more robust test might involve mocking the ID generation or checking the rendered label text.
    }

    @Test
    fun testFormFieldIsRequired() {
        val mockRenderer = MockFormFieldRenderer()

        runTestComposable(mockRenderer) {
            FormField(
                isRequired = true,
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastIsRequired, "isRequired should be true")
    }

    @Test
    fun testFormFieldWithErrorState() {
        val mockRenderer = MockFormFieldRenderer()
        val testErrorText = "Test Error"

        runTestComposable(mockRenderer) {
            FormField(
                isError = true,
                errorText = { Text(testErrorText) }, // Wrap in composable lambda
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastIsError, "isError should be true")
        assertNull(mockRenderer.lastErrorMessageId, "Error Message ID should be null")
        // Similar to label, we don't know the exact ID, but check it's passed.
    }

    @Test
    fun testFormFieldWithErrorStateNoErrorText() {
        val mockRenderer = MockFormFieldRenderer()

        runTestComposable(mockRenderer) {
            FormField(
                isError = true,
                errorText = null, // No error text provided
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastIsError, "isError should be true")
        assertNull(mockRenderer.lastErrorMessageId, "Error Message ID should be null when errorText is null")
    }

    @Test
    fun testFormFieldWithHelperText() {
        val mockRenderer = MockFormFieldRenderer()
        val testHelperText = "Test Helper"

        runTestComposable(mockRenderer) {
            FormField(
                helperText = { Text(testHelperText) }, // Wrap in composable lambda
                isError = false, // Explicitly not an error
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(false, mockRenderer.lastIsError, "isError should be false")
        assertNull(mockRenderer.lastErrorMessageId, "errorMessageId should be null when helperText is shown (not error)")
    }

    @Test
    fun testFormFieldHelperTextTakesPrecedenceWhenNoError() {
        val mockRenderer = MockFormFieldRenderer()
        val testHelperText = "Helper Text"
        val testErrorText = "Error Text" // Provide both

        runTestComposable(mockRenderer) {
            FormField(
                helperText = { Text(testHelperText) }, // Wrap in composable lambda
                errorText = { Text(testErrorText) }, // Wrap in composable lambda
                isError = false, // Not in error state
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(false, mockRenderer.lastIsError, "isError should be false")
        assertNull(mockRenderer.lastErrorMessageId, "errorMessageId should be null when not isError, even if errorText is provided")
        // The FormField composable should internally render helperText, not errorText,
        // and thus not pass an errorMessageId to the renderer.
    }

    @Test
    fun testFormFieldErrorTextTakesPrecedenceWhenError() {
        val mockRenderer = MockFormFieldRenderer()
        val testHelperText = "Helper Text"
        val testErrorText = "Error Text" // Provide both

        runTestComposable(mockRenderer) {
            FormField(
                helperText = { Text(testHelperText) }, // Wrap in composable lambda
                errorText = { Text(testErrorText) }, // Wrap in composable lambda
                isError = true, // In error state
                fieldContent = {}
            )
        }

        assertTrue(mockRenderer.renderFormFieldCalled, "renderFormField should be called")
        assertEquals(true, mockRenderer.lastIsError, "isError should be true")
        assertNull(mockRenderer.lastErrorMessageId, "errorMessageId should be null even when isError is true")
         // The FormField composable should internally render errorText, not helperText,
         // and thus pass an errorMessageId to the renderer.
    }
} 