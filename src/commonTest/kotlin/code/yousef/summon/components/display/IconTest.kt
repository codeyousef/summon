package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Tests for the Icon component
 */
class IconTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderIconCalled = false
        var lastIconName: String? = null
        var lastModifier: Modifier? = null
        var lastOnClick: (() -> Unit)? = null
        var lastSvgContent: String? = null
        var lastIconType: IconType? = null

        override fun renderIcon(name: String, modifier: Modifier, onClick: (() -> Unit)?, svgContent: String?, type: IconType) {
            renderIconCalled = true
            lastIconName = name
            lastModifier = modifier
            lastOnClick = onClick
            lastSvgContent = svgContent
            lastIconType = type
        }

        // Minimal implementations for other required methods
        override fun renderText(text: String, modifier: Modifier) {}
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
    fun testIconWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Icon component with default parameters
            Icon(name = "test-icon")

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("test-icon", mockRenderer.lastIconName, "Icon name should be 'test-icon'")

            // Verify the icon type
            assertEquals(IconType.SVG, mockRenderer.lastIconType, "Icon type should be SVG by default")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertEquals(Modifier().styles, mockRenderer.lastModifier?.styles, "Modifier should be the default")

            // Verify that onClick is null
            assertEquals(null, mockRenderer.lastOnClick, "onClick should be null")

            // Verify that svgContent is null
            assertEquals(null, mockRenderer.lastSvgContent, "svgContent should be null")
        }
    }

    @Test
    fun testIconWithCustomModifier() {
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

            // Call the Icon component with custom modifier
            Icon(
                name = "custom-icon",
                modifier = customModifier
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("custom-icon", mockRenderer.lastIconName, "Icon name should be 'custom-icon'")

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
    fun testIconWithSizeAndColor() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Icon component with size and color
            Icon(
                name = "sized-icon",
                size = "32px",
                color = "#FF0000"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("sized-icon", mockRenderer.lastIconName, "Icon name should be 'sized-icon'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("32px", styles["width"], "width should be '32px'")
            assertEquals("32px", styles["height"], "height should be '32px'")
            assertEquals("#FF0000", styles["color"], "color should be '#FF0000'")
        }
    }

    @Test
    fun testIconWithOnClick() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Variable to track if onClick was called
            var onClickCalled = false

            // Call the Icon component with onClick
            Icon(
                name = "clickable-icon",
                onClick = { onClickCalled = true }
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("clickable-icon", mockRenderer.lastIconName, "Icon name should be 'clickable-icon'")

            // Verify the onClick handler
            assertNotNull(mockRenderer.lastOnClick, "onClick should not be null")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("pointer", styles["cursor"], "cursor should be 'pointer'")
            assertEquals("button", styles["__attr:role"], "role should be 'button'")

            // Call the onClick handler and verify it works
            mockRenderer.lastOnClick?.invoke()
            assertTrue(onClickCalled, "onClick should have been called")
        }
    }

    @Test
    fun testIconWithAriaLabel() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Icon component with ariaLabel
            Icon(
                name = "accessible-icon",
                ariaLabel = "Accessible Icon"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("accessible-icon", mockRenderer.lastIconName, "Icon name should be 'accessible-icon'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("Accessible Icon", styles["__attr:aria-label"], "aria-label should be 'Accessible Icon'")
            assertEquals("img", styles["__attr:role"], "role should be 'img'")
        }
    }

    @Test
    fun testMaterialIcon() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the MaterialIcon component
            MaterialIcon(
                name = "info",
                size = "24px",
                color = "#0000FF"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name
            assertEquals("info", mockRenderer.lastIconName, "Icon name should be 'info'")

            // Verify the icon type
            assertEquals(IconType.FONT, mockRenderer.lastIconType, "Icon type should be FONT")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("24px", styles["width"], "width should be '24px'")
            assertEquals("24px", styles["height"], "height should be '24px'")
            assertEquals("#0000FF", styles["color"], "color should be '#0000FF'")
            assertEquals("Material Icons", styles["font-family"], "font-family should be 'Material Icons'")
        }
    }

    @Test
    fun testSvgIcon() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Sample SVG content
            val svgContent = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'><path d='M12 2L2 22h20L12 2z'/></svg>"

            // Call the SvgIcon component
            SvgIcon(
                svgContent = svgContent,
                ariaLabel = "Warning Icon"
            )

            // Verify that renderIcon was called
            assertTrue(mockRenderer.renderIconCalled, "renderIcon should have been called")

            // Verify the icon name (should use ariaLabel as fallback)
            assertEquals("Warning Icon", mockRenderer.lastIconName, "Icon name should be 'Warning Icon'")

            // Verify the icon type
            assertEquals(IconType.SVG, mockRenderer.lastIconType, "Icon type should be SVG")

            // Verify the SVG content
            assertEquals(svgContent, mockRenderer.lastSvgContent, "SVG content should match")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            val styles = mockRenderer.lastModifier!!.styles

            assertEquals("Warning Icon", styles["__attr:aria-label"], "aria-label should be 'Warning Icon'")
            assertEquals("img", styles["__attr:role"], "role should be 'img'")
        }
    }
}
