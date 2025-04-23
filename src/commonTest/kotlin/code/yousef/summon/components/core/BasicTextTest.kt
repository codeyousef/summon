package code.yousef.summon.components.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.theme.TextStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo as ExpectFileInfo
import code.yousef.summon.components.navigation.Tab
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

/**
 * Tests for the BasicText component
 */
class BasicTextTest {

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
        override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
        override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<code.yousef.summon.runtime.SelectOption<T>>, modifier: Modifier) {}
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
        override fun renderFileUpload(onFilesSelected: (List<ExpectFileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = {}
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
        override fun renderTabLayout(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderTabLayout(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit, modifier: Modifier, content: () -> Unit) {}
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
        override val inserting: Boolean = false

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
    fun testBasicTextWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the BasicText component with default parameters
            BasicText(text = "Hello, World!")

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Hello, World!", mockRenderer.lastText, "Text should be 'Hello, World!'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertEquals(Modifier().styles, mockRenderer.lastModifier?.styles, "Modifier should be the default")
        }
    }

    @Test
    fun testBasicTextWithCustomStyle() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a custom TextStyle
            val customStyle = TextStyle(
                fontFamily = "Arial, sans-serif",
                fontSize = "16px",
                fontWeight = "bold",
                fontStyle = "italic",
                color = "#FF0000",
                textDecoration = "underline",
                lineHeight = "1.5",
                letterSpacing = "0.5px"
            )

            // Call the BasicText component with custom style
            BasicText(
                text = "Styled Text",
                style = customStyle
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Styled Text", mockRenderer.lastText, "Text should be 'Styled Text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("Arial, sans-serif", styles["font-family"], "font-family should be 'Arial, sans-serif'")
            assertEquals("16px", styles["font-size"], "font-size should be '16px'")
            assertEquals("bold", styles["font-weight"], "font-weight should be 'bold'")
            assertEquals("italic", styles["font-style"], "font-style should be 'italic'")
            assertEquals("#FF0000", styles["color"], "color should be '#FF0000'")
            assertEquals("underline", styles["text-decoration"], "text-decoration should be 'underline'")
            assertEquals("1.5", styles["line-height"], "line-height should be '1.5'")
            assertEquals("0.5px", styles["letter-spacing"], "letter-spacing should be '0.5px'")
        }
    }

    @Test
    fun testBasicTextWithCustomModifier() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a custom Modifier
            val customModifier = Modifier()
                .padding("10px")
                .backgroundColor("#EFEFEF")
                .border("1px", "solid", "black")
                .borderRadius("5px")

            // Call the BasicText component with custom modifier
            BasicText(
                text = "Text with Custom Modifier",
                modifier = customModifier
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Text with Custom Modifier", mockRenderer.lastText, "Text should be 'Text with Custom Modifier'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("10px", styles["padding"], "padding should be '10px'")
            assertEquals("#EFEFEF", styles["background-color"], "background-color should be '#EFEFEF'")
            assertEquals("1px solid black", styles["border"], "border should be '1px solid black'")
            assertEquals("5px", styles["border-radius"], "border-radius should be '5px'")
        }
    }

    @Test
    fun testBasicTextWithTextLayoutCallback() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Variable to track if callback was called
        var callbackCalled = false
        var layoutResult: TextLayoutResult? = null

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the BasicText component with onTextLayout callback
            BasicText(
                text = "Text with Layout Callback",
                onTextLayout = { result ->
                    callbackCalled = true
                    layoutResult = result
                }
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Text with Layout Callback", mockRenderer.lastText, "Text should be 'Text with Layout Callback'")

            // Verify the callback was called
            assertTrue(callbackCalled, "onTextLayout callback should have been called")

            // Verify the layout result
            assertNotNull(layoutResult, "TextLayoutResult should not be null")
            assertEquals(25 * 8f, layoutResult!!.width, "Width should be calculated based on text length")
            assertEquals(20f, layoutResult!!.height, "Height should be the default value")
            assertEquals(1, layoutResult!!.lineCount, "Line count should be 1")
            assertFalse(layoutResult!!.hasVisualOverflow, "hasVisualOverflow should be false")
        }
    }
}
