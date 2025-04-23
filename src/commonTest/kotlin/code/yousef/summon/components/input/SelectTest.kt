package code.yousef.summon.components.input

// Import the PlatformRenderer's SelectOption, this is the one the interface method uses
// Import the component's SelectOption for creating the options list passed TO the component
// Import other types used in PlatformRenderer methods for the mock
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.navigation.Tab
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import code.yousef.summon.components.input.SelectOption as ComponentSelectOption
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

class SelectTest {

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
        } // Store for remember/mutableStateOf

        override fun nextSlot() {
            currentSlot++
        }

        override fun getSlot(): Any? = slots.getOrPut(currentSlot) { null } // Fix potential index issue
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

    // Mock Renderer focusing on renderSelect
    class MockSelectRenderer : PlatformRenderer {
        var lastSelectedValue: Any? = null
        var lastOnSelectedChange: ((Any?) -> Unit)? = null
        var lastOptions: List<RendererSelectOption<Any?>>? = null // Capture RendererSelectOption
        var lastModifier: Modifier? = null
        var renderSelectCalled = false

        // This signature MUST match the PlatformRenderer interface exactly
        @Suppress("UNCHECKED_CAST")
        override fun <T> renderSelect(
            selectedValue: T?,
            onSelectedChange: (T?) -> Unit,
            options: List<RendererSelectOption<T>>, // Expect RendererSelectOption
            modifier: Modifier
        ) {
            renderSelectCalled = true
            lastSelectedValue = selectedValue
            lastOnSelectedChange = onSelectedChange as? (Any?) -> Unit
            // Capture the options list as is (it's already List<RendererSelectOption<T>>)
            lastOptions = options as? List<RendererSelectOption<Any?>>
            lastModifier = modifier
        }

        // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
        // (Copy from previous test file)
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

        // renderSelect is implemented above
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
    fun testBasicSelectRendering() {
        val renderer = MockSelectRenderer()
        var selectedState: SummonMutableState<String?>? = null
        // Create options using ComponentSelectOption for the input parameter
        val componentOptions = listOf(
            ComponentSelectOption("val1", "Label 1"),
            ComponentSelectOption("val2", "Label 2", disabled = true)
        )
        // Keep the lambda definition, but don't pass it to Select
        val onSelectChange: (String?) -> Unit = { selectedState?.value = it }

        runTestComposable(renderer) {
            selectedState = mutableStateOf<String?>(null)
            // Add explicit type argument <String> and remove onSelectedChange argument
            Select<String>(
                selectedValue = selectedState!!,
                options = componentOptions
                // onSelectedChange = onSelectChange // Removed
            )
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertEquals(null, renderer.lastSelectedValue)
        // Cannot assertSame on the lambda anymore since we didn't pass it
        // assertSame(onSelectChange, renderer.lastOnSelectedChange)
        assertNotNull(renderer.lastOnSelectedChange) // Check that the default lambda was passed
        assertNotNull(renderer.lastOptions)
        assertEquals(2, renderer.lastOptions!!.size)

        // Assertions now check against the expected RendererSelectOption structure
        // Assuming the structure (value, label, disabled fields) is compatible/intended to match.
        assertEquals("val1", renderer.lastOptions!![0].value) // Check value
        assertEquals("Label 1", renderer.lastOptions!![0].label) // Check label
        // NOTE: RendererSelectOption doesn't have a 'disabled' field in its definition in PlatformRenderer.kt!
        // assertEquals(false, renderer.lastOptions!![0].disabled) // Cannot check disabled here
        assertEquals("val2", renderer.lastOptions!![1].value)
        assertEquals("Label 2", renderer.lastOptions!![1].label)
        // assertEquals(true, renderer.lastOptions!![1].disabled) // Cannot check disabled here

        assertNotNull(renderer.lastModifier)
        assertEquals(null, renderer.lastModifier?.styles?.get("__attr:disabled"))
    }

    @Test
    fun testSelectWithInitialValue() {
        val renderer = MockSelectRenderer()
        var selectedState: SummonMutableState<String?>? = null
        val componentOptions = listOf(ComponentSelectOption("v1", "L1"), ComponentSelectOption("v2", "L2"))

        runTestComposable(renderer) {
            selectedState = mutableStateOf<String?>("v2") // Initial value
            Select(selectedState!!, componentOptions)
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertEquals("v2", renderer.lastSelectedValue)
        assertEquals(componentOptions.size, renderer.lastOptions?.size)
    }

    @Test
    fun testDisabledSelect() {
        val renderer = MockSelectRenderer()
        var selectedState: SummonMutableState<Int?>? = null
        val componentOptions = listOf(ComponentSelectOption(1, "One"))

        runTestComposable(renderer) {
            selectedState = mutableStateOf<Int?>(1)
            Select(selectedState!!, componentOptions, disabled = true)
        }

        assertEquals(true, renderer.renderSelectCalled)
        assertNotNull(renderer.lastModifier)
        assertEquals("disabled", renderer.lastModifier?.styles?.get("__attr:disabled"))
    }

    // TODO: Test other parameters like multiple, size, label, placeholder
    // TODO: Test interaction with onSelectedChange callback
    // TODO: Test validation (might be tricky if SelectState isn't used directly by rendering)
} 