package code.yousef.summon.components.demo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo as ExpectFileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.BorderStyle
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifiers.border
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for the LocalDemoComponent, HoistedDemoComponent, and LocalDemoContainer
 */
class LocalDemoComponentTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderBlockCalled = false
        var renderBoxCalled = false
        var renderColumnCalled = false
        var renderTextCalled = false
        var lastBlockModifier: Modifier? = null
        var lastBoxModifier: Modifier? = null
        var lastColumnModifier: Modifier? = null
        var lastTextModifier: Modifier? = null
        var lastTextContent: String? = null
        var lastBlockContent: (@Composable FlowContent.() -> Unit)? = null
        var lastBoxContent: (@Composable FlowContent.() -> Unit)? = null
        var lastColumnContent: (@Composable FlowContent.() -> Unit)? = null

        override fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
            renderBlockCalled = true
            lastBlockModifier = modifier
            lastBlockContent = content
        }

        override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
            renderBoxCalled = true
            lastBoxModifier = modifier
            lastBoxContent = content
        }

        override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
            renderColumnCalled = true
            lastColumnModifier = modifier
            lastColumnContent = content
        }

        override fun renderText(text: String, modifier: Modifier) {
            renderTextCalled = true
            lastTextContent = text
            lastTextModifier = modifier
        }

        // Minimal implementations for other required methods
        override fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
        override fun renderButton(
            onClick: () -> Unit,
            modifier: Modifier,
            content: @Composable FlowContent.() -> Unit
        ) {
        }

        override fun renderTextField(
            value: String,
            onValueChange: (String) -> Unit,
            modifier: Modifier,
            type: String
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
            onFilesSelected: (List<ExpectFileInfo>) -> Unit,
            accept: String?,
            multiple: Boolean,
            enabled: Boolean,
            capture: String?,
            modifier: Modifier
        ): () -> Unit = { throw NotImplementedError("Mock only") }

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
        override fun renderInline(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderSpan(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderDivider(modifier: Modifier) {}
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
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
    fun testLocalDemoComponentWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LocalDemoComponent with default parameters
            LocalDemoComponent()

            // Verify that renderBlock was called
            assertTrue(mockRenderer.renderBlockCalled, "renderBlock should have been called")

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.lastBlockContent, "Block content should not be null")
        }
    }

    @Test
    fun testLocalDemoComponentWithCustomParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().background("red")

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LocalDemoComponent with custom parameters
            LocalDemoComponent(
                initialValue = "Custom Text",
                modifier = customModifier
            )

            // Verify that renderBlock was called with the correct parameters
            assertTrue(mockRenderer.renderBlockCalled, "renderBlock should have been called")

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.lastBlockContent, "Block content should not be null")

            // Verify that the custom modifier was passed to the Block
            val blockModifier = mockRenderer.lastBlockModifier
            assertNotNull(blockModifier, "Block modifier should not be null")
            val styles = blockModifier.styles
            assertTrue(styles.containsKey("background-color"), "background style should be present")
            assertEquals("red", styles["background-color"], "background should be set to red")
        }
    }

    @Test
    fun testHoistedDemoComponent() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().padding("10px")

        // Track if callback was called
        var callbackCalled = false
        val onTextChange: (String) -> Unit = { callbackCalled = true }

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the HoistedDemoComponent
            HoistedDemoComponent(
                text = "Hoisted Value",
                onTextChange = onTextChange,
                modifier = customModifier
            )

            // Verify that renderBlock was called with the correct parameters
            assertTrue(mockRenderer.renderBlockCalled, "renderBlock should have been called")

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.lastBlockContent, "Block content should not be null")

            // Verify that the custom modifier was passed to the Block
            val blockModifier = mockRenderer.lastBlockModifier
            assertNotNull(blockModifier, "Block modifier should not be null")
            val styles = blockModifier.styles
            assertTrue(styles.containsKey("padding"), "padding style should be present")
            assertEquals("10px", styles["padding"], "padding should be set to 10px")
        }
    }

    @Test
    fun testLocalDemoContainer() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Create a custom modifier
        val customModifier = Modifier().border("1px", BorderStyle.Solid, "black")

        // Content called flag
        var contentCalled = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LocalDemoContainer
            LocalDemoContainer(
                title = "Container Title",
                modifier = customModifier,
                content = {
                    contentCalled = true
                }
            )

            // Verify that renderColumn was called with the correct parameters
            assertTrue(mockRenderer.renderColumnCalled, "renderColumn should have been called")

            // Verify that the content lambda was passed
            assertNotNull(mockRenderer.lastColumnContent, "Column content should not be null")

            // Verify that the custom modifier was passed to the Column
            val columnModifier = mockRenderer.lastColumnModifier
            assertNotNull(columnModifier, "Column modifier should not be null")
            val styles = columnModifier.styles
            assertTrue(styles.containsKey("border"), "border style should be present")
            assertEquals("1px solid black", styles["border"], "border should be set correctly")
        }
    }
}
