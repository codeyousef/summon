package code.yousef.summon.components.input

// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.*
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

class TextAreaTest {

    // Basic Mock Composer
    private class TestComposer : Composer {
        private val slots = mutableMapOf<Int, Any?>()
        private var currentSlot = 0
        override val inserting: Boolean get() = true
        private var nodeDepth = 0
        override fun startNode() {
            nodeDepth++
        }

        override fun endNode() {
            nodeDepth--
        }

        override fun startGroup(key: Any?) {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {
            slots[currentSlot] = value
        }

        override fun nextSlot() {
            currentSlot++
        }

        override fun getSlot(): Any? = slots.getOrPut(currentSlot) { null }
        override fun setSlot(value: Any?) {
            slots[currentSlot] = value
        }

        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {
            startNode()
        }

        override fun endCompose() {
            endNode()
        }

        override fun <T> compose(composable: @Composable () -> T): T {
            startCompose()
            val result = composable()
            endCompose()
            return result
        }
    }

    // Mock Renderer focusing on renderTextArea
    class MockTextAreaRenderer : PlatformRenderer {
        var lastValue: String? = null
        var lastOnValueChange: ((String) -> Unit)? = null
        var lastEnabled: Boolean? = null
        var lastReadOnly: Boolean? = null
        var lastRows: Int? = null
        var lastMaxLength: Int? = null
        var lastPlaceholder: String? = null
        var lastModifier: Modifier? = null
        var renderTextAreaCalled = false

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
            renderTextAreaCalled = true
            lastValue = value
            lastOnValueChange = onValueChange
            lastEnabled = enabled
            lastReadOnly = readOnly
            lastRows = rows
            lastMaxLength = maxLength
            lastPlaceholder = placeholder
            lastModifier = modifier
        }

        // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
        // (Copied from SelectTest)
        override fun renderText(text: String, modifier: Modifier) {}
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
            options: List<RendererSelectOption<T>>,
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

        // renderTextArea implemented above
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

        override fun renderTabLayout(modifier: Modifier, content: @Composable (() -> Unit)) {}
        override fun renderTabLayout(
            tabs: List<String>,
            selectedTab: String,
            onTabSelected: (String) -> Unit,
            modifier: Modifier,
            content: () -> Unit
        ) {
        }

        fun renderTab(tab: Tab, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {}
        override fun renderDivider(modifier: Modifier) {}
        fun renderSnackbarHost(hostState: Any, modifier: Modifier) {}
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable (FlowContent.() -> Unit)) {}
        fun renderExpansionPanel(
            expanded: Boolean,
            onExpansionChange: (Boolean) -> Unit,
            modifier: Modifier,
            header: @Composable FlowContent.() -> Unit,
            body: @Composable FlowContent.() -> Unit
        ) {
        }

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
        override fun renderHtmlTag(
            tagName: String,
            modifier: Modifier,
            content: @Composable() FlowContent.() -> Unit
        ) {
        }
    }

    // Helper to run composable in test environment
    private fun runTestComposable(renderer: PlatformRenderer, block: @Composable () -> Unit) {
        CompositionLocal.provideComposer(TestComposer()) {
            val provider = LocalPlatformRenderer.provides(renderer)
            provider.current // Access current
            block()
        }
    }

    @Test
    fun testBasicTextAreaRendering() {
        val renderer = MockTextAreaRenderer()
        var textValue = "Initial Text"
        val onValChange: (String) -> Unit = { textValue = it }
        val mod = Modifier()

        runTestComposable(renderer) {
            TextArea(
                value = textValue,
                onValueChange = onValChange,
                modifier = mod,
                enabled = false,
                readOnly = true,
                rows = 5,
                maxLength = 100,
                placeholder = "Enter text..."
            )
        }

        assertTrue(renderer.renderTextAreaCalled)
        assertEquals("Initial Text", renderer.lastValue)
        assertSame(onValChange, renderer.lastOnValueChange)
        assertEquals(false, renderer.lastEnabled)
        assertEquals(true, renderer.lastReadOnly)
        assertEquals(5, renderer.lastRows)
        assertEquals(100, renderer.lastMaxLength)
        assertEquals("Enter text...", renderer.lastPlaceholder)
        assertSame(mod, renderer.lastModifier)

        // Simulate value change from renderer
        renderer.lastOnValueChange?.invoke("New Text")
        assertEquals("New Text", textValue)
    }

    @Test
    fun testStatefulTextArea() {
        val renderer = MockTextAreaRenderer()
        var externalValue = "Initial"
        val onValChangeExternal: (String) -> Unit = { externalValue = it }

        runTestComposable(renderer) {
            StatefulTextArea(
                initialValue = "Start",
                onValueChange = onValChangeExternal,
                placeholder = "Stateful"
            )
        }

        assertTrue(renderer.renderTextAreaCalled)
        assertEquals("Start", renderer.lastValue) // Initial value from state
        assertEquals("Stateful", renderer.lastPlaceholder)
        assertNotNull(renderer.lastOnValueChange)

        // Simulate internal state update via renderer callback
        renderer.lastOnValueChange?.invoke("Updated")

        // Check external callback was triggered
        assertEquals("Updated", externalValue)

        // Note: We cannot directly check the internal textState.value easily here,
        // but we verified the external callback received the update.
        // A subsequent recomposition would show the renderer receiving "Updated".
    }
} 