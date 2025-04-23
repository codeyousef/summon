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

// Extension functions for testing
private fun Modifier.hasStyle(property: String, value: String): Boolean =
    styles[property] == value

private fun Modifier.hasAttribute(name: String, value: String): Boolean =
    styles["__attr:$name"] == value

/**
 * Tests for the Text component
 */
class TextTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderTextCalled = false
        var renderLabelCalled = false
        var lastText: String? = null
        var lastModifier: Modifier? = null
        var lastForElement: String? = null

        override fun renderText(text: String, modifier: Modifier) {
            renderTextCalled = true
            lastText = text
            lastModifier = modifier
        }

        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {
            renderLabelCalled = true
            lastText = text
            lastModifier = modifier
            lastForElement = forElement
        }

        // Minimal implementations for other required methods
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
    fun testTextWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with default parameters
            Text(text = "Hello, World!")

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
    fun testTextWithStyling() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with styling parameters
            Text(
                text = "Styled Text",
                overflow = "ellipsis",
                lineHeight = "1.5",
                textAlign = "center",
                fontFamily = "Arial, sans-serif",
                textDecoration = "underline",
                textTransform = "uppercase",
                letterSpacing = "0.5px",
                whiteSpace = "nowrap",
                wordBreak = "break-word",
                wordSpacing = "2px",
                textShadow = "1px 1px 2px black"
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Styled Text", mockRenderer.lastText, "Text should be 'Styled Text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("ellipsis", styles["overflow"], "overflow should be 'ellipsis'")
            assertEquals("ellipsis", styles["text-overflow"], "text-overflow should be 'ellipsis'")
            assertEquals("1.5", styles["line-height"], "line-height should be '1.5'")
            assertEquals("center", styles["text-align"], "text-align should be 'center'")
            assertEquals("Arial, sans-serif", styles["font-family"], "font-family should be 'Arial, sans-serif'")
            assertEquals("underline", styles["text-decoration"], "text-decoration should be 'underline'")
            assertEquals("uppercase", styles["text-transform"], "text-transform should be 'uppercase'")
            assertEquals("0.5px", styles["letter-spacing"], "letter-spacing should be '0.5px'")
            assertEquals("nowrap", styles["white-space"], "white-space should be 'nowrap'")
            assertEquals("break-word", styles["word-break"], "word-break should be 'break-word'")
            assertEquals("2px", styles["word-spacing"], "word-spacing should be '2px'")
            assertEquals("1px 1px 2px black", styles["text-shadow"], "text-shadow should be '1px 1px 2px black'")
        }
    }

    @Test
    fun testTextWithMaxLines() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with maxLines parameter
            Text(
                text = "Text with max lines",
                maxLines = 2
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Text with max lines", mockRenderer.lastText, "Text should be 'Text with max lines'")

            // Verify the maxLines styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("-webkit-box", styles["display"], "display should be '-webkit-box'")
            assertEquals("2", styles["-webkit-line-clamp"], "-webkit-line-clamp should be '2'")
            assertEquals("vertical", styles["-webkit-box-orient"], "-webkit-box-orient should be 'vertical'")
            assertEquals("hidden", styles["overflow"], "overflow should be 'hidden'")
        }
    }

    @Test
    fun testTextWithAccessibility() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Text component with accessibility parameters
            Text(
                text = "Accessible Text",
                role = "heading",
                ariaLabel = "Heading Label",
                ariaDescribedBy = "description-id",
                semantic = "heading"
            )

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Accessible Text", mockRenderer.lastText, "Text should be 'Accessible Text'")

            // Verify the accessibility attributes
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("role", "heading"),
                "Modifier should have role attribute"
            )
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("aria-label", "Heading Label"),
                "Modifier should have aria-label attribute"
            )
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("aria-describedby", "description-id"),
                "Modifier should have aria-describedby attribute"
            )
            assertTrue(
                mockRenderer.lastModifier!!.hasAttribute("data-semantic", "heading"),
                "Modifier should have data-semantic attribute"
            )
        }
    }

    @Test
    fun testLabel() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Label component
            Label(
                text = "Username",
                forElement = "username-input"
            )

            // Verify that renderLabel was called
            assertTrue(mockRenderer.renderLabelCalled, "renderLabel should have been called")

            // Verify the text
            assertEquals("Username", mockRenderer.lastText, "Text should be 'Username'")

            // Verify the forElement parameter
            assertEquals("username-input", mockRenderer.lastForElement, "forElement should be 'username-input'")
        }
    }

    @Test
    fun testTextComponent() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Create a TextComponent and render it
            val textComponent = TextComponent(
                text = "Component Text",
                textAlign = "right",
                fontFamily = "Roboto"
            )
            textComponent.render()

            // Verify that renderText was called
            assertTrue(mockRenderer.renderTextCalled, "renderText should have been called")

            // Verify the text
            assertEquals("Component Text", mockRenderer.lastText, "Text should be 'Component Text'")

            // Verify the styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("right", styles["text-align"], "text-align should be 'right'")
            assertEquals("Roboto", styles["font-family"], "font-family should be 'Roboto'")
        }
    }
}