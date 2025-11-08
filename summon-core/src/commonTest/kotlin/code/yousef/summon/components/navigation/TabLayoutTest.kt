package code.yousef.summon.components.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.core.FlowContentCompat
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi // Opt-in for Uuid usage
class TabLayoutTest {

    // Basic Mock Composer
    private class TestComposer : Composer {
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
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun recompose() {
            // Mock/Test implementation
        }

        override fun rememberedValue(key: Any): Any? {
            return null
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            // Mock/Test implementation
        }

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

    // Mock Renderer focusing on renderTabLayout
    class MockTabLayoutRenderer : MockPlatformRenderer() {
        var lastTabs: List<Tab>? = null
        var lastSelectedTabIndex: Int? = null
        var lastOnTabSelected: ((Int) -> Unit)? = null
        var lastModifier: Modifier? = null
        override var renderTabLayoutCalled = false

        // The specific overload called by the TabLayout composable
        override fun renderTabLayout(
            tabs: List<Tab>,
            selectedTabIndex: Int,
            onTabSelected: (Int) -> Unit,
            modifier: Modifier
        ) {
            renderTabLayoutCalled = true
            lastTabs = tabs
            lastSelectedTabIndex = selectedTabIndex
            lastOnTabSelected = onTabSelected
            lastModifier = modifier
        }

        // --- Add No-Op implementations for ALL other PlatformRenderer methods ---
        override fun renderText(text: String, modifier: Modifier) {}
        override fun renderLabel(text: String, modifier: Modifier, forElement: String?) {}
        override fun renderButton(
            onClick: () -> Unit,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
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
        override fun renderRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderBox(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
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
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderBadge(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
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
            content: @Composable FlowContentCompat.() -> Unit
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

        override fun renderAspectRatio(
            ratio: Float,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderCard(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLink(href: String, modifier: Modifier) {}
        override fun renderLink(modifier: Modifier, href: String, content: @Composable () -> Unit) {}
        override fun renderEnhancedLink(
            href: String,
            target: String?,
            title: String?,
            ariaLabel: String?,
            ariaDescribedBy: String?,
            modifier: Modifier,
            fallbackText: String?
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
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable (FlowContentCompat.() -> Unit)) {}
        fun renderExpansionPanel(
            expanded: Boolean,
            onExpansionChange: (Boolean) -> Unit,
            modifier: Modifier,
            header: @Composable FlowContentCompat.() -> Unit,
            body: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        fun renderBasicText(text: String, modifier: Modifier) {}
        override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
        override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable() () -> Unit) {}
        override fun renderAnimatedContent(modifier: Modifier) {}
        override fun renderAnimatedContent(modifier: Modifier, content: @Composable() () -> Unit) {}
        override fun renderInline(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
        override fun renderSpan(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
        override fun renderLazyColumn(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
        override fun renderLazyRow(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
        override fun renderResponsiveLayout(modifier: Modifier, content: @Composable() FlowContentCompat.() -> Unit) {}
        override fun renderHtmlTag(
            tagName: String,
            modifier: Modifier,
            content: @Composable() FlowContentCompat.() -> Unit
        ) {
        }
    }

    // Helper to run composable in test environment
    private fun runComposableTest(renderer: PlatformRenderer, content: @Composable () -> Unit) {
        CompositionLocal.provideComposer(TestComposer()) {
            val provider = LocalPlatformRenderer.provides(renderer)
            provider.current // Access current
            content()
        }
    }

    @Test
    fun testTabLayoutRenderingWithDefaults() {
        val renderer = MockTabLayoutRenderer()
        val testTabs = listOf(
            Tab(Uuid.random(), "Tab 1", { /* Content 1 */ }),
            Tab(Uuid.random(), "Tab 2", { /* Content 2 */ })
        )

        runComposableTest(renderer) {
            TabLayout(tabs = testTabs)
        }

        assertEquals(true, renderer.renderTabLayoutCalled)
        assertEquals(testTabs, renderer.lastTabs)
        assertEquals(0, renderer.lastSelectedTabIndex) // Default index
        assertNotNull(renderer.lastOnTabSelected) // Default non-null callback
        assertNotNull(renderer.lastModifier)
    }

    @Test
    fun testTabLayoutRenderingWithCustomSelection() {
        val renderer = MockTabLayoutRenderer()
        val testTabs = listOf(
            Tab(Uuid.random(), "A", { }),
            Tab(Uuid.random(), "B", { }),
            Tab(Uuid.random(), "C", { })
        )
        var selectedIndexCallback = -1
        val onSelect: (Int) -> Unit = { selectedIndexCallback = it }
        val customModifier = Modifier().width("100%")

        runComposableTest(renderer) {
            TabLayout(
                tabs = testTabs,
                selectedTabIndex = 1, // Select second tab
                onTabSelected = onSelect,
                modifier = customModifier
            )
        }

        assertEquals(true, renderer.renderTabLayoutCalled)
        assertEquals(testTabs, renderer.lastTabs)
        assertEquals(1, renderer.lastSelectedTabIndex)
        assertSame(onSelect, renderer.lastOnTabSelected)
        assertSame(customModifier, renderer.lastModifier)

        // Test callback invocation (optional, depends on renderer mock)
        // renderer.lastOnTabSelected?.invoke(2)
        // assertEquals(2, selectedIndexCallback)
    }

    @Test
    fun testTabLayoutWithNoSelectionCallback() {
        val renderer = MockTabLayoutRenderer()
        val testTabs = listOf(Tab(Uuid.random(), "Only Tab", { }))

        runComposableTest(renderer) {
            TabLayout(tabs = testTabs, onTabSelected = null) // Explicitly pass null
        }

        assertEquals(true, renderer.renderTabLayoutCalled)
        assertEquals(testTabs, renderer.lastTabs)
        assertEquals(0, renderer.lastSelectedTabIndex)
        assertNotNull(renderer.lastOnTabSelected) // Should receive the default empty lambda
        assertNotNull(renderer.lastModifier)
    }
}

// Helper extension if needed
// expect fun Modifier.width(value: String): Modifier 
