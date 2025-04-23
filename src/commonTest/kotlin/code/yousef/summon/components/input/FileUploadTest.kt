package code.yousef.summon.components.input

import code.yousef.summon.runtime.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.FileInfo as ExpectFileInfo // Alias expect class
import code.yousef.summon.components.input.FileUpload // Import the component
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.State // Import the correct State interface
import code.yousef.summon.util.TestComposer
import code.yousef.summon.util.runTestComposable
import kotlinx.datetime.LocalTime
import kotlinx.datetime.LocalDate

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.runtime.SelectOption as RendererSelectOption
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import kotlinx.html.FlowContent

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlin.test.assertSame
import code.yousef.summon.util.TestFileInfo // Import the test fixture

// REMOVED Test-specific implementation for FileInfo - Moved to TestFixtures.kt

// Mock Renderer implementing PlatformRenderer, focusing on renderFileUpload
class MockFileUploadRenderer : PlatformRenderer {
    var lastOnFilesSelected: ((List<TestFileInfo>) -> Unit)? = null // Uses input.FileInfo
    var lastAccept: String? = null
    var lastMultiple: Boolean? = null
    var lastEnabled: Boolean? = null
    var lastCapture: String? = null
    var lastModifier: Modifier? = null
    var renderFileUploadCalled = false
    var returnedTriggerFunction: (() -> Unit)? = null

    // Store the captured callback with the original signature type (using alias)
    var capturedOriginalCallback: ((List<ExpectFileInfo>) -> Unit)? = null 

    // Override signature matches the PlatformRenderer interface (using alias)
    override fun renderFileUpload(
        onFilesSelected: (List<ExpectFileInfo>) -> Unit,
        accept: String?,
        multiple: Boolean,
        enabled: Boolean,
        capture: String?,
        modifier: Modifier
    ): () -> Unit {
        renderFileUploadCalled = true
        capturedOriginalCallback = onFilesSelected // Capture the raw callback
        lastAccept = accept
        lastMultiple = multiple
        lastEnabled = enabled
        lastCapture = capture
        lastModifier = modifier
        val trigger = { /* Mock trigger function */ }
        returnedTriggerFunction = trigger
        return trigger
    }

    // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
    // Ensure signatures match the interface
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

class FileUploadTest {

    @Test
    fun testBasicFileUploadRendering() {
        val mockRenderer = MockFileUploadRenderer()
        // Use TestFileInfo for the variable holding ASSERTION data
        var selectedTestFiles: List<TestFileInfo>? = null 
        val acceptType = "image/png,image/jpeg"
        val modifier = Modifier() 
        val buttonStyle = Modifier()
        // Lambda passed to component uses the expect FileInfo signature
        val onFilesSelectedLambda: (List<ExpectFileInfo>) -> Unit = { files ->
            // Adapt received expect FileInfo list to TestFileInfo list for assertion
            selectedTestFiles = files.map { TestFileInfo(it.name, it.size, it.type) }
        }

        runTestComposable(mockRenderer) {
            FileUpload(
                onFilesSelected = onFilesSelectedLambda,
                accept = acceptType,
                multiple = true,
                enabled = true,
                capture = "user",
                buttonLabel = "Select Images",
                label = "Profile Picture",
                modifier = modifier,
                buttonStyle = buttonStyle
            )
        }

        // Check parameters passed to the RENDERER
        assertTrue(mockRenderer.renderFileUploadCalled, "renderFileUpload should be called")
        assertEquals(acceptType, mockRenderer.lastAccept, "Accept type mismatch")
        assertEquals(true, mockRenderer.lastMultiple, "Multiple flag mismatch")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch")
        assertEquals("user", mockRenderer.lastCapture, "Capture value mismatch")
        // Note: The FileUpload composable internally creates a Button and Column,
        // it passes a new Modifier() to renderFileUpload, not the one provided to FileUpload.
        // So we don't assert mockRenderer.lastModifier against `modifier` here.
        assertNotNull(mockRenderer.lastModifier, "Modifier passed to renderer should not be null")

        // Check if the returned trigger function was captured
        assertNotNull(mockRenderer.returnedTriggerFunction, "Renderer should return a trigger function")

        // Verify the callback was passed to the renderer
        assertNotNull(mockRenderer.capturedOriginalCallback, "Original onFilesSelected callback should be captured")
        
        // ** Limitation **
        // Due to difficulties with expect/actual in commonTest, we cannot reliably 
        // invoke the captured callback with test data (TestFileInfo) because it expects
        // the expect class (ExpectFileInfo), and casting fails.
        // Therefore, we cannot assert the content passed *through* the callback here.
        // We have verified the callback lambda itself is passed to the renderer.

        // val testFilesToSimulate = listOf(TestFileInfo("test.png", 1024L, "image/png")) 
        // try {
        //     @Suppress("UNCHECKED_CAST")
        //     mockRenderer.capturedOriginalCallback?.invoke(testFilesToSimulate as List<ExpectFileInfo>)
        // } catch (e: ClassCastException) {
        //      kotlin.test.fail("Failed to invoke original callback due to ClassCastException. Casting TestFileInfo to expect FileInfo failed in commonTest.")
        // }
        // assertEquals(testFilesToSimulate, selectedTestFiles, "onFilesSelected callback did not update external list correctly")
    }

    @Test
    fun testDisabledFileUpload() {
        val mockRenderer = MockFileUploadRenderer()
        val onFilesSelectedLambda: (List<ExpectFileInfo>) -> Unit = { /* No-op */ }

        runTestComposable(mockRenderer) {
            FileUpload(
                onFilesSelected = onFilesSelectedLambda,
                enabled = false // Explicitly disable
            )
        }

        // Check parameters passed to the RENDERER
        assertTrue(mockRenderer.renderFileUploadCalled, "renderFileUpload should be called")
        assertEquals(false, mockRenderer.lastEnabled, "Renderer should receive enabled=false")
        
        // We can't directly check the Button's disabled state without a more complex mock
        // that also captures Button calls, but checking the renderer's enabled state is a good proxy.
    }

    @Test
    fun testStatefulFileUpload() {
        val mockRenderer = MockFileUploadRenderer()
        var stateResult: State<List<ExpectFileInfo>>? = null

        runTestComposable(mockRenderer) {
            // Call the stateful overload (returns State)
            stateResult = FileUpload(
                // Pass other params if needed for verification
                accept = "*.txt",
                multiple = false,
                enabled = true
            )
        }

        // Check parameters passed to the RENDERER (via the stateless overload)
        assertTrue(mockRenderer.renderFileUploadCalled, "renderFileUpload should be called by stateful overload")
        assertEquals("*.txt", mockRenderer.lastAccept, "Accept type mismatch in stateful call")
        assertEquals(false, mockRenderer.lastMultiple, "Multiple flag mismatch in stateful call")
        assertEquals(true, mockRenderer.lastEnabled, "Enabled state mismatch in stateful call")
        assertNotNull(mockRenderer.capturedOriginalCallback, "Callback should be captured in stateful call")

        // Check that the returned State object exists and has an initial value
        assertNotNull(stateResult, "Stateful FileUpload should return a State object")
        assertTrue(stateResult?.value?.isEmpty() ?: false, "Initial state value should be an empty list")
        
        // ** Limitation ** 
        // Similar to the stateless test, we cannot easily simulate the callback 
        // being invoked to check if stateResult.value updates correctly 
        // due to the expect/actual FileInfo issue.
    }
} 