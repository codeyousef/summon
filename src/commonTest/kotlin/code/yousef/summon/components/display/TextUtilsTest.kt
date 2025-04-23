package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the TextUtils components
 */
class TextUtilsTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderTextCalled = false
        var lastText: String? = null
        var lastModifier: Modifier? = null

        override fun renderText(text: String, modifier: Modifier) {
            renderTextCalled = true
            lastText = text
            lastModifier = modifier
        }

        // Minimal implementations for other required methods
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
        override fun renderTextField(
            value: String,
            onValueChange: (String) -> Unit,
            modifier: Modifier,
            type: String
        ) {
        }

        override fun renderButton(
            onClick: () -> Unit,
            modifier: Modifier,
            content: @Composable FlowContent.() -> Unit
        ) {
        }

        override fun <T> renderSelect(
            selectedValue: T?,
            onSelectedChange: (T?) -> Unit,
            options: List<code.yousef.summon.runtime.SelectOption<T>>,
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

    @Test
    fun testH1() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the H1 component
            H1(text = "Heading 1")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Heading 1", mockRenderer.lastText, "Text should be 'Heading 1'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("2rem", styles["font-size"], "font-size should be '2rem'")
            assertEquals("bold", styles["font-weight"], "font-weight should be 'bold'")
            assertEquals("0.67em 0", styles["margin"], "margin should be '0.67em 0'")
            assertEquals("1.2", styles["line-height"], "line-height should be '1.2'")
            assertEquals(
                "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
                styles["font-family"], "font-family should be correct"
            )
        }
    }

    @Test
    fun testParagraph() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Paragraph component
            Paragraph(text = "This is a paragraph")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("This is a paragraph", mockRenderer.lastText, "Text should be 'This is a paragraph'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("1em 0", styles["margin"], "margin should be '1em 0'")
            assertEquals("1.5", styles["line-height"], "line-height should be '1.5'")
            assertEquals(
                "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
                styles["font-family"], "font-family should be correct"
            )
        }
    }

    @Test
    fun testErrorText() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the ErrorText component
            ErrorText(text = "Error message")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Error message", mockRenderer.lastText, "Text should be 'Error message'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("#d32f2f", styles["color"], "color should be '#d32f2f'")
            assertEquals(
                "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
                styles["font-family"], "font-family should be correct"
            )
            assertEquals("1.4", styles["line-height"], "line-height should be '1.4'")
        }
    }

    @Test
    fun testScreenReaderText() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the ScreenReaderText component
            ScreenReaderText(text = "Screen reader only text")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Screen reader only text", mockRenderer.lastText, "Text should be 'Screen reader only text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("absolute", styles["position"], "position should be 'absolute'")
            assertEquals("1px", styles["width"], "width should be '1px'")
            assertEquals("1px", styles["height"], "height should be '1px'")
            assertEquals("0", styles["padding"], "padding should be '0'")
            assertEquals("-1px", styles["margin"], "margin should be '-1px'")
            assertEquals("hidden", styles["overflow"], "overflow should be 'hidden'")
            assertEquals("rect(0, 0, 0, 0)", styles["clip"], "clip should be 'rect(0, 0, 0, 0)'")
            assertEquals("nowrap", styles["white-space"], "white-space should be 'nowrap'")
            assertEquals("0", styles["border"], "border should be '0'")
        }
    }
}