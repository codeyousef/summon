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
 * Tests for the Image component
 */
class ImageTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer {
        var renderImageCalled = false
        var lastSrc: String? = null
        var lastAlt: String? = null
        var lastModifier: Modifier? = null

        override fun renderImage(src: String, alt: String, modifier: Modifier) {
            renderImageCalled = true
            lastSrc = src
            lastAlt = alt
            lastModifier = modifier
        }

        // Minimal implementations for other required methods
        override fun renderText(text: String, modifier: Modifier) {}
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
    fun testImageWithDefaultParameters() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with default parameters
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image"
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastSrc,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastAlt, "alt should be 'Example image'")

            // Verify the modifier
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertEquals(Modifier().styles, mockRenderer.lastModifier?.styles, "Modifier should be the default")
        }
    }

    @Test
    fun testImageWithCustomModifier() {
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

            // Call the Image component with custom modifier
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                modifier = customModifier
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastSrc,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastAlt, "alt should be 'Example image'")

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
    fun testImageWithWidthAndHeight() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with width and height
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                width = "300px",
                height = "200px"
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastSrc,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastAlt, "alt should be 'Example image'")

            // Note: In the current implementation, width and height are stored in attributes but not applied to the modifier
            // This test verifies the current behavior, but it might need to be updated if the implementation changes
        }
    }

    @Test
    fun testImageWithContentDescription() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with contentDescription
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                contentDescription = "A detailed description of the image content"
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastSrc,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastAlt, "alt should be 'Example image'")

            // Note: In the current implementation, contentDescription is stored in attributes but not applied to the modifier
            // This test verifies the current behavior, but it might need to be updated if the implementation changes
        }
    }

    @Test
    fun testImageWithLoadingStrategy() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide the mock renderer to the LocalPlatformRenderer
            val provider = LocalPlatformRenderer.provides(mockRenderer)

            // Call the Image component with eager loading
            Image(
                src = "https://example.com/image.jpg",
                alt = "Example image",
                loading = ImageLoading.EAGER
            )

            // Verify that renderImage was called
            assertTrue(mockRenderer.renderImageCalled, "renderImage should have been called")

            // Verify the src
            assertEquals(
                "https://example.com/image.jpg",
                mockRenderer.lastSrc,
                "src should be 'https://example.com/image.jpg'"
            )

            // Verify the alt
            assertEquals("Example image", mockRenderer.lastAlt, "alt should be 'Example image'")

            // Note: In the current implementation, loading is stored in attributes but not applied to the modifier
            // This test verifies the current behavior, but it might need to be updated if the implementation changes
        }
    }
}