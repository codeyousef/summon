package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.modifier.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.*

/**
 * Tests for the PlatformRenderer interface contract
 */
class PlatformRendererTest {

    /**
     * A test implementation of PlatformRenderer that tracks method calls
     */
    private class TestPlatformRenderer : MockPlatformRenderer() {
        // Tracking variables for method calls
        override var renderTextCalled = false
        override var renderButtonCalled = false
        var renderComposableRootCalled = false
        override var renderRowCalled = false
        override var renderColumnCalled = false
        override var renderBoxCalled = false

        // Parameters passed to methods
        var lastTextContent = ""
        var lastModifier: Modifier? = null
        var lastButtonClick: (() -> Unit)? = null
        var lastTextFieldValue = ""
        var lastTextFieldType = ""
        var lastComposableRootResult = ""

        // Implementation of interface methods with tracking
        override fun renderText(text: String, modifier: Modifier) {
            renderTextCalled = true
            lastTextContent = text
            lastModifier = modifier
        }

        override fun renderButton(
            onClick: () -> Unit,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
            renderButtonCalled = true
            lastButtonClick = onClick
            lastModifier = modifier
        }

        override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {
            renderTextFieldCalled = true
            lastTextFieldValue = value
            lastTextFieldType = type
            lastModifier = modifier
        }

        override fun <T> renderSelect(
            selectedValue: T?,
            onSelectedChange: (T?) -> Unit,
            options: List<code.yousef.summon.runtime.SelectOption<T>>,
            modifier: Modifier
        ) {
            renderSelectCalled = true
            lastModifier = modifier
        }

        override fun renderComposableRoot(composable: @Composable () -> Unit): String {
            renderComposableRootCalled = true
            lastComposableRootResult = "Test Render Result"
            return lastComposableRootResult
        }

        override fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
            renderRowCalled = true
            lastModifier = modifier
        }

        override fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
            renderColumnCalled = true
            lastModifier = modifier
        }

        override fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {
            renderBoxCalled = true
            lastModifier = modifier
        }

        // Minimal implementations for other required methods
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
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
        override fun renderComposable(composable: @Composable () -> Unit) {}
        override fun renderImage(src: String, alt: String?, modifier: Modifier) {}
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
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
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

        override fun renderForm(
            onSubmit: (() -> Unit)?,
            modifier: Modifier,
            content: @Composable FormContent.() -> Unit
        ) {
        }

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

        override fun renderAspectRatio(
            ratio: Float,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLink(href: String, modifier: Modifier) {}
        override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}
        override fun renderEnhancedLink(
            href: String,
            target: String?,
            title: String?,
            ariaLabel: String?,
            ariaDescribedBy: String?,
            modifier: Modifier,
            fallbackText: String?
        ) {
        }

        override fun renderTabLayout(
            tabs: List<Tab>,
            selectedTabIndex: Int,
            onTabSelected: (Int) -> Unit,
            modifier: Modifier
        ) {
        }

        override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderTabLayout(
            tabs: List<String>,
            selectedTab: String,
            onTabSelected: (String) -> Unit,
            modifier: Modifier,
            content: () -> Unit
        ) {
        }

        override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
        override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderAnimatedContent(modifier: Modifier) {}
        override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDivider(modifier: Modifier) {}
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderHtmlTag(
            tagName: String,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }
    }

    @Test
    fun testRenderText() {
        val renderer = TestPlatformRenderer()
        val testText = "Hello, World!"
        val testModifier = Modifier()

        // Call the method
        renderer.renderText(testText, testModifier)

        // Verify the method was called with correct parameters
        assertTrue(renderer.renderTextCalled, "renderText should have been called")
        assertEquals(testText, renderer.lastTextContent, "Text content should match")
        assertNotNull(renderer.lastModifier, "Modifier should not be null")
    }

    @Test
    fun testRenderButton() {
        val renderer = TestPlatformRenderer()
        val testModifier = Modifier()
        var clickCalled = false
        val onClick = { clickCalled = true }

        // Call the method
        renderer.renderButton(onClick, testModifier) {}

        // Verify the method was called with correct parameters
        assertTrue(renderer.renderButtonCalled, "renderButton should have been called")
        assertNotNull(renderer.lastModifier, "Modifier should not be null")

        // Verify the onClick handler works
        assertFalse(clickCalled, "Click handler should not be called yet")
        renderer.lastButtonClick?.invoke()
        assertTrue(clickCalled, "Click handler should be called after invoking")
    }

    @Test
    fun testRenderTextField() {
        val renderer = TestPlatformRenderer()
        val testValue = "Test Input"
        val testType = "password"
        val testModifier = Modifier()

        // Call the method
        renderer.renderTextField(testValue, {}, testModifier, testType)

        // Verify the method was called with correct parameters
        assertTrue(renderer.renderTextFieldCalled, "renderTextField should have been called")
        assertEquals(testValue, renderer.lastTextFieldValue, "Text field value should match")
        assertEquals(testType, renderer.lastTextFieldType, "Text field type should match")
        assertNotNull(renderer.lastModifier, "Modifier should not be null")
    }

    @Test
    fun testRenderComposableRoot() {
        val renderer = TestPlatformRenderer()

        // Call the method
        val result = renderer.renderComposableRoot {}

        // Verify the method was called and returned the expected result
        assertTrue(renderer.renderComposableRootCalled, "renderComposableRoot should have been called")
        assertEquals("Test Render Result", result, "renderComposableRoot should return the expected result")
        assertEquals("Test Render Result", renderer.lastComposableRootResult, "lastComposableRootResult should be set")
    }

    @Test
    fun testLayoutComponents() {
        val renderer = TestPlatformRenderer()
        val rowModifier = Modifier()
        val columnModifier = Modifier()
        val boxModifier = Modifier()

        // Call the methods
        renderer.renderRow(rowModifier) {}
        assertTrue(renderer.renderRowCalled, "renderRow should have been called")
        assertNotNull(renderer.lastModifier, "Modifier should not be null")

        renderer.renderColumn(columnModifier) {}
        assertTrue(renderer.renderColumnCalled, "renderColumn should have been called")
        assertNotNull(renderer.lastModifier, "Modifier should not be null")

        renderer.renderBox(boxModifier) {}
        assertTrue(renderer.renderBoxCalled, "renderBox should have been called")
        assertNotNull(renderer.lastModifier, "Modifier should not be null")
    }
}
