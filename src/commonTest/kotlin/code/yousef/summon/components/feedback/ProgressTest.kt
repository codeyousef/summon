package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.*

/**
 * Tests for the Progress component and related functions
 */
class ProgressTest {

    /**
     * A mock implementation of PlatformRenderer for testing
     */
    private class MockPlatformRenderer : PlatformRenderer() {
        var renderProgressCalled = false
        var lastProgressValue: Float? = null
        var lastProgressType: ProgressType? = null
        var lastModifier: Modifier? = null

        override fun renderProgress(value: Float?, type: ProgressType, modifier: Modifier) {
            renderProgressCalled = true
            lastProgressValue = value
            lastProgressType = type
            lastModifier = modifier
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
        override fun renderColumn(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderBox(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderText(text: String, modifier: Modifier) {}
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
    fun testProgressDataClass() {
        // Create a Progress instance with default parameters
        val progress = Progress()

        // Verify default values
        assertEquals(ProgressType.LINEAR, progress.type)
        assertNull(progress.value)
        assertEquals(100, progress.maxValue)
        assertEquals("#2196f3", progress.color)
        assertEquals("#e0e0e0", progress.trackColor)
        assertEquals("medium", progress.size)
        assertEquals("4px", progress.thickness)
        assertEquals(ProgressAnimation.SMOOTH, progress.animation)
        assertNull(progress.label)
    }

    @Test
    fun testIsIndeterminate() {
        // Test with null value
        val progress1 = Progress(value = null)
        assertTrue(progress1.isIndeterminate())

        // Test with non-null value but INDETERMINATE type
        val progress2 = Progress(value = 50, type = ProgressType.INDETERMINATE)
        assertTrue(progress2.isIndeterminate())

        // Test with non-null value and non-INDETERMINATE type
        val progress3 = Progress(value = 50, type = ProgressType.LINEAR)
        assertFalse(progress3.isIndeterminate())
    }

    @Test
    fun testGetPercentage() {
        // Test with null value
        val progress1 = Progress(value = null)
        assertEquals(0, progress1.getPercentage())

        // Test with value = 0
        val progress2 = Progress(value = 0)
        assertEquals(0, progress2.getPercentage())

        // Test with value = 50
        val progress3 = Progress(value = 50)
        assertEquals(50, progress3.getPercentage())

        // Test with value = 100
        val progress4 = Progress(value = 100)
        assertEquals(100, progress4.getPercentage())

        // Test with value > maxValue
        val progress5 = Progress(value = 150, maxValue = 100)
        assertEquals(100, progress5.getPercentage()) // Should be clamped to 100

        // Test with custom maxValue
        val progress6 = Progress(value = 50, maxValue = 200)
        assertEquals(25, progress6.getPercentage()) // 50/200 = 25%
    }

    @Test
    fun testGetTypeStyles() {
        // Test LINEAR type
        val linearProgress = Progress(type = ProgressType.LINEAR)
        val linearStyles = linearProgress.getTypeStyles()
        assertEquals("#e0e0e0", linearStyles["background-color"])
        assertEquals("4px", linearStyles["height"])
        assertEquals("100%", linearStyles["width"])

        // Test CIRCULAR type
        val circularProgress = Progress(type = ProgressType.CIRCULAR)
        val circularStyles = circularProgress.getTypeStyles()
        assertEquals("#e0e0e0", circularStyles["background-color"])
        assertEquals("36px", circularStyles["width"])
        assertEquals("36px", circularStyles["height"])
        assertEquals("50%", circularStyles["border-radius"])

        // Test INDETERMINATE type (LINEAR)
        val indeterminateLinear = Progress(type = ProgressType.INDETERMINATE)
        val indeterminateLinearStyles = indeterminateLinear.getTypeStyles()
        assertEquals("#e0e0e0", indeterminateLinearStyles["background-color"])
        assertEquals("4px", indeterminateLinearStyles["height"])
        assertEquals("100%", indeterminateLinearStyles["width"])

        // Test with different sizes
        val smallProgress = Progress(size = "small")
        val smallStyles = smallProgress.getTypeStyles()
        assertEquals("4px", smallStyles["height"])

        val largeProgress = Progress(size = "large")
        val largeStyles = largeProgress.getTypeStyles()
        assertEquals("8px", largeStyles["height"])
    }

    @Test
    fun testGetAnimationStyles() {
        // Test NONE animation
        val noneProgress = Progress(animation = ProgressAnimation.NONE)
        val noneStyles = noneProgress.getAnimationStyles()
        assertTrue(noneStyles.isEmpty())

        // Test SMOOTH animation
        val smoothProgress = Progress(animation = ProgressAnimation.SMOOTH)
        val smoothStyles = smoothProgress.getAnimationStyles()
        assertEquals("width 0.3s ease-in-out, transform 0.3s ease-in-out", smoothStyles["transition"])

        // Test PULSE animation
        val pulseProgress = Progress(animation = ProgressAnimation.PULSE)
        val pulseStyles = pulseProgress.getAnimationStyles()
        assertEquals("pulse 1.5s infinite", pulseStyles["animation"])

        // Test BOUNCE animation
        val bounceProgress = Progress(animation = ProgressAnimation.BOUNCE)
        val bounceStyles = bounceProgress.getAnimationStyles()
        assertEquals("bounce 1s infinite", bounceStyles["animation"])
    }

    @Test
    fun testGetAnimationKeyframes() {
        // Test NONE animation
        val noneProgress = Progress(animation = ProgressAnimation.NONE)
        assertNull(noneProgress.getAnimationKeyframes())

        // Test SMOOTH animation
        val smoothProgress = Progress(animation = ProgressAnimation.SMOOTH)
        assertNull(smoothProgress.getAnimationKeyframes())

        // Test PULSE animation
        val pulseProgress = Progress(animation = ProgressAnimation.PULSE)
        val pulseKeyframes = pulseProgress.getAnimationKeyframes()
        assertNotNull(pulseKeyframes)
        assertTrue(pulseKeyframes.contains("@keyframes pulse"))
        assertTrue(pulseKeyframes.contains("opacity: 1"))
        assertTrue(pulseKeyframes.contains("opacity: 0.6"))

        // Test BOUNCE animation
        val bounceProgress = Progress(animation = ProgressAnimation.BOUNCE)
        val bounceKeyframes = bounceProgress.getAnimationKeyframes()
        assertNotNull(bounceKeyframes)
        assertTrue(bounceKeyframes.contains("@keyframes bounce"))
        assertTrue(bounceKeyframes.contains("translateY(0)"))
        assertTrue(bounceKeyframes.contains("translateY(-5px)"))
    }

    @Test
    fun testGetAccessibilityAttributes() {
        // Test with determinate progress
        val determinateProgress = Progress(value = 50)
        val determinateAttrs = determinateProgress.getAccessibilityAttributes()
        assertEquals("progressbar", determinateAttrs["role"])
        assertEquals("50", determinateAttrs["aria-valuenow"])
        assertEquals("0", determinateAttrs["aria-valuemin"])
        assertEquals("100", determinateAttrs["aria-valuemax"])
        assertEquals("50%", determinateAttrs["aria-valuetext"])

        // Test with indeterminate progress
        val indeterminateProgress = Progress(value = null)
        val indeterminateAttrs = indeterminateProgress.getAccessibilityAttributes()
        assertEquals("progressbar", indeterminateAttrs["role"])
        assertEquals("Loading", indeterminateAttrs["aria-valuetext"])
        assertFalse(indeterminateAttrs.containsKey("aria-valuenow"))

        // Test with label
        val labeledProgress = Progress(value = 50, label = "Loading files")
        val labeledAttrs = labeledProgress.getAccessibilityAttributes()
        assertEquals("Loading files", labeledAttrs["aria-label"])
    }

    @Test
    fun testLinearProgressFunction() {
        // Test with default parameters
        val defaultLinear = linearProgress()
        assertEquals(ProgressType.LINEAR, defaultLinear.type)
        assertNull(defaultLinear.value)
        assertEquals("#2196f3", defaultLinear.color)

        // Test with custom parameters
        val customLinear = linearProgress(value = 75, color = "red", modifier = Modifier().background("blue"))
        assertEquals(ProgressType.LINEAR, customLinear.type)
        assertEquals(75, customLinear.value)
        assertEquals("red", customLinear.color)
        assertTrue(customLinear.modifier.styles.containsKey("background-color"))
        assertEquals("blue", customLinear.modifier.styles["background-color"])
    }

    @Test
    fun testCircularProgressFunction() {
        // Test with default parameters
        val defaultCircular = circularProgress()
        assertEquals(ProgressType.CIRCULAR, defaultCircular.type)
        assertNull(defaultCircular.value)
        assertEquals("medium", defaultCircular.size)
        assertEquals("#2196f3", defaultCircular.color)

        // Test with custom parameters
        val customCircular = circularProgress(
            value = 25,
            size = "large",
            color = "green",
            modifier = Modifier().padding("10px")
        )
        assertEquals(ProgressType.CIRCULAR, customCircular.type)
        assertEquals(25, customCircular.value)
        assertEquals("large", customCircular.size)
        assertEquals("green", customCircular.color)
        assertTrue(customCircular.modifier.styles.containsKey("padding"))
        assertEquals("10px", customCircular.modifier.styles["padding"])
    }

    @Test
    fun testLoadingFunction() {
        // Test with default parameters
        val defaultLoading = loading()
        assertEquals(ProgressType.INDETERMINATE, defaultLoading.type)
        assertNull(defaultLoading.value)
        // The type parameter is used for the visual appearance, not the actual type property
        assertEquals("#2196f3", defaultLoading.color)
        assertEquals(ProgressAnimation.PULSE, defaultLoading.animation)

        // Test with custom parameters
        val customLoading = loading(
            type = ProgressType.LINEAR,
            color = "orange",
            modifier = Modifier().width("200px")
        )
        assertEquals(ProgressType.INDETERMINATE, customLoading.type)
        assertNull(customLoading.value)
        // The type parameter affects the visual appearance but the type property is always INDETERMINATE
        assertEquals("orange", customLoading.color)
        assertEquals(ProgressAnimation.PULSE, customLoading.animation)
        assertTrue(customLoading.modifier.styles.containsKey("width"))
        assertEquals("200px", customLoading.modifier.styles["width"])
    }

    @Test
    fun testLinearProgressComposable() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the LinearProgress composable with default parameters
            LinearProgress()

            // Verify that renderProgress was called with the correct parameters
            assertTrue(mockRenderer.renderProgressCalled, "renderProgress should have been called")
            assertNull(mockRenderer.lastProgressValue, "Progress value should be null")
            assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressType, "Progress type should be LINEAR")
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")

            // Reset the mock
            mockRenderer.renderProgressCalled = false
            mockRenderer.lastProgressValue = null
            mockRenderer.lastProgressType = null
            mockRenderer.lastModifier = null

            // Call the LinearProgress composable with custom parameters
            LinearProgress(
                progress = 0.75f,
                modifier = Modifier().background("yellow")
            )

            // Verify that renderProgress was called with the correct parameters
            assertTrue(mockRenderer.renderProgressCalled, "renderProgress should have been called")
            assertEquals(0.75f, mockRenderer.lastProgressValue, "Progress value should be 0.75f")
            assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressType, "Progress type should be LINEAR")
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(
                mockRenderer.lastModifier!!.styles.containsKey("background-color"),
                "Modifier should have background-color"
            )
            assertEquals(
                "yellow",
                mockRenderer.lastModifier!!.styles["background-color"],
                "Background color should be yellow"
            )
        }
    }

    @Test
    fun testCircularProgressComposable() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the CircularProgress composable with default parameters
            CircularProgress()

            // Verify that renderProgress was called with the correct parameters
            assertTrue(mockRenderer.renderProgressCalled, "renderProgress should have been called")
            assertNull(mockRenderer.lastProgressValue, "Progress value should be null")
            assertEquals(ProgressType.CIRCULAR, mockRenderer.lastProgressType, "Progress type should be CIRCULAR")
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")

            // Reset the mock
            mockRenderer.renderProgressCalled = false
            mockRenderer.lastProgressValue = null
            mockRenderer.lastProgressType = null
            mockRenderer.lastModifier = null

            // Call the CircularProgress composable with custom parameters
            CircularProgress(
                progress = 0.5f,
                modifier = Modifier().padding("5px")
            )

            // Verify that renderProgress was called with the correct parameters
            assertTrue(mockRenderer.renderProgressCalled, "renderProgress should have been called")
            assertEquals(0.5f, mockRenderer.lastProgressValue, "Progress value should be 0.5f")
            assertEquals(ProgressType.CIRCULAR, mockRenderer.lastProgressType, "Progress type should be CIRCULAR")
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(mockRenderer.lastModifier!!.styles.containsKey("padding"), "Modifier should have padding")
            assertEquals("5px", mockRenderer.lastModifier!!.styles["padding"], "Padding should be 5px")
        }
    }

    @Test
    fun testIndeterminateProgressComposable() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Set up the composition context
        CompositionLocal.provideComposer(MockComposer()) {
            // Set the mock renderer as the platform renderer
            setPlatformRenderer(mockRenderer)

            // Call the IndeterminateProgress composable with default parameters
            IndeterminateProgress()

            // Verify that renderProgress was called with the correct parameters
            assertTrue(mockRenderer.renderProgressCalled, "renderProgress should have been called")
            assertNull(mockRenderer.lastProgressValue, "Progress value should be null")
            assertEquals(ProgressType.LINEAR, mockRenderer.lastProgressType, "Progress type should be LINEAR")
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")

            // Reset the mock
            mockRenderer.renderProgressCalled = false
            mockRenderer.lastProgressValue = null
            mockRenderer.lastProgressType = null
            mockRenderer.lastModifier = null

            // Call the IndeterminateProgress composable with custom parameters
            IndeterminateProgress(
                type = ProgressType.CIRCULAR,
                modifier = Modifier().height("30px")
            )

            // Verify that renderProgress was called with the correct parameters
            assertTrue(mockRenderer.renderProgressCalled, "renderProgress should have been called")
            assertNull(mockRenderer.lastProgressValue, "Progress value should be null")
            assertEquals(ProgressType.CIRCULAR, mockRenderer.lastProgressType, "Progress type should be CIRCULAR")
            assertNotNull(mockRenderer.lastModifier, "Modifier should not be null")
            assertTrue(mockRenderer.lastModifier!!.styles.containsKey("height"), "Modifier should have height")
            assertEquals("30px", mockRenderer.lastModifier!!.styles["height"], "Height should be 30px")
        }
    }
}
