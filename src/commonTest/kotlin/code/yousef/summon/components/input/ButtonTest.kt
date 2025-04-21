package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertNotNull

// Extension functions for testing
private fun Modifier.hasStyle(property: String, value: String): Boolean = 
    styles[property] == value

private fun Modifier.hasBackground(color: String): Boolean = 
    hasStyle("background", color) || hasStyle("background-color", color)

private fun Modifier.hasColor(color: String): Boolean = 
    hasStyle("color", color)

/**
 * Tests for the Button component
 */
class ButtonTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderButtonCalled = false
        var lastOnClick: (() -> Unit)? = null
        var lastModifier: Modifier? = null
        var lastContent: (@Composable FlowContent.() -> Unit)? = null
        var lastIconName: String? = null
        var lastIconPosition: IconPosition? = null
        var lastLabel: String? = null
        var lastDisabled: Boolean = false

        override fun renderButton(onClick: () -> Unit, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
            renderButtonCalled = true
            lastOnClick = onClick
            lastModifier = modifier
            lastContent = content
        }

        // Track icon rendering to verify icon position
        override fun renderIcon(name: String, modifier: Modifier, onClick: (() -> Unit)?, svgContent: String?, type: IconType) {
            lastIconName = name
        }

        // Minimal implementations for other required methods
        override fun renderText(text: String, modifier: Modifier) {
            lastLabel = text
        }
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
        override fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, type: String) {}
        override fun <T> renderSelect(selectedValue: T?, onSelectedChange: (T?) -> Unit, options: List<SelectOption<T>>, modifier: Modifier) {}
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
        override fun renderAlertContainer(variant: AlertVariant?, modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderBadge(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean, modifier: Modifier) {}
        override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {}
        override fun renderFileUpload(onFilesSelected: (List<FileInfo>) -> Unit, accept: String?, multiple: Boolean, enabled: Boolean, capture: String?, modifier: Modifier): () -> Unit = {}
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
    fun testButtonWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var buttonClicked = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component with default parameters
            Button(
                onClick = { buttonClicked = true },
                label = "Test Button"
            )

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the onClick handler
            assertNotNull(mockRenderer.lastOnClick, "onClick handler should not be null")
            mockRenderer.lastOnClick?.invoke()
            assertTrue(buttonClicked, "onClick handler should have been called")

            // Verify the modifier has primary button styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val modifier = mockRenderer.lastModifier!!
            assertTrue(modifier.hasBackground("#0d6efd"), "Primary button should have blue background")
            assertTrue(modifier.hasColor("#ffffff"), "Primary button should have white text")

            // Verify the content was provided
            assertNotNull(mockRenderer.lastContent, "Content should not be null")
        }
    }

    @Test
    fun testButtonVariants() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Test PRIMARY variant
            Button(
                onClick = { },
                label = "Primary Button",
                variant = ButtonVariant.PRIMARY
            )
            assertTrue(mockRenderer.lastModifier!!.hasBackground("#0d6efd"), "Primary button should have blue background")
            assertTrue(mockRenderer.lastModifier!!.hasColor("#ffffff"), "Primary button should have white text")

            // Test SECONDARY variant
            Button(
                onClick = { },
                label = "Secondary Button",
                variant = ButtonVariant.SECONDARY
            )
            assertTrue(mockRenderer.lastModifier!!.hasBackground("#6c757d"), "Secondary button should have gray background")
            assertTrue(mockRenderer.lastModifier!!.hasColor("#ffffff"), "Secondary button should have white text")

            // Test DANGER variant
            Button(
                onClick = { },
                label = "Danger Button",
                variant = ButtonVariant.DANGER
            )
            assertTrue(mockRenderer.lastModifier!!.hasBackground("#dc3545"), "Danger button should have red background")
            assertTrue(mockRenderer.lastModifier!!.hasColor("#ffffff"), "Danger button should have white text")
        }
    }

    @Test
    fun testDisabledButton() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()
        var buttonClicked = false

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Button component with disabled=true
            Button(
                onClick = { buttonClicked = true },
                label = "Disabled Button",
                disabled = true
            )

            // Verify that renderButton was called
            assertTrue(mockRenderer.renderButtonCalled, "renderButton should have been called")

            // Verify the modifier has disabled styling
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val modifier = mockRenderer.lastModifier!!
            assertTrue(modifier.hasStyle("cursor", "not-allowed"), "Disabled button should have not-allowed cursor")
            assertTrue(modifier.hasStyle("pointer-events", "none"), "Disabled button should have pointer-events none")

            // Verify the onClick handler is empty when disabled
            assertNotNull(mockRenderer.lastOnClick, "onClick handler should not be null")
            mockRenderer.lastOnClick?.invoke()
            assertFalse(buttonClicked, "onClick handler should not be called when button is disabled")
        }
    }

    @Test
    fun testButtonWithIcon() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Test icon at START position
            Button(
                onClick = { },
                label = "Button with Start Icon",
                iconName = "test-icon",
                iconPosition = IconPosition.START
            )

            // Verify the content was provided
            assertNotNull(mockRenderer.lastContent, "Content should not be null")

            // Test icon at END position
            Button(
                onClick = { },
                label = "Button with End Icon",
                iconName = "test-icon",
                iconPosition = IconPosition.END
            )

            // Verify the content was provided
            assertNotNull(mockRenderer.lastContent, "Content should not be null")
        }
    }
}
