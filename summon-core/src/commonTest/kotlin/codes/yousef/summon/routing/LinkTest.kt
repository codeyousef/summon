package codes.yousef.summon.routing

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.components.navigation.Tab
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.*

class LinkTest {

    // Basic Mock Composer for setting up CompositionLocal
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

    // Mock Router for testing
    private class MockRouter : Router {
        var lastNavigatedPath: String? = null
        var lastPushState: Boolean = true

        override val currentPath: String = "/current"

        override fun navigate(path: String, pushState: Boolean) {
            lastNavigatedPath = path
            lastPushState = pushState
        }

        @Composable
        override fun create(initialPath: String) {
            // No-op for testing
        }
    }

    // Mock Renderer for testing Link component
    private class MockLinkRenderer : MockPlatformRenderer() {
        var lastHref: String? = null
        var lastTarget: String? = null
        var lastModifier: Modifier? = null
        var lastText: String? = null
        var renderEnhancedLinkCalled = false
        var capturedOnClickHandler: (() -> Unit)? = null

        override fun renderEnhancedLink(
            href: String,
            target: String?,
            title: String?,
            ariaLabel: String?,
            ariaDescribedBy: String?,
            modifier: Modifier,
            content: @Composable () -> Unit
        ) {
            renderEnhancedLinkCalled = true
            lastHref = href
            lastTarget = target
            lastModifier = modifier

            // Extract onClick handler from modifier for testing
            capturedOnClickHandler = modifier.eventHandlers["click"]

            // Execute content to allow Text rendering
            content()
        }

        override fun renderText(text: String, modifier: Modifier) {
            lastText = text
        }

        // Implement all required methods with empty implementations
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
            options: List<SelectOption<T>>,
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
        ): () -> Unit = { }

        override fun renderForm(
            onSubmit: (() -> Unit)?,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
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

        override fun renderDivider(modifier: Modifier) {}
        override fun renderExpansionPanel(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLazyColumn(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderLazyRow(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderResponsiveLayout(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderHtmlTag(
            tagName: String,
            modifier: Modifier,
            content: @Composable FlowContentCompat.() -> Unit
        ) {
        }

        override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {}
        override fun renderAnimatedVisibility(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderAnimatedContent(modifier: Modifier) {}
        override fun renderAnimatedContent(modifier: Modifier, content: @Composable () -> Unit) {}
        override fun renderInline(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderSpan(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
    }

    // Helper to run composable in test environment
    private fun runComposableTest(renderer: PlatformRenderer, router: Router? = null, content: @Composable () -> Unit) {
        CompositionLocal.provideComposer(TestComposer()) {
            val rendererProvider = LocalPlatformRenderer.provides(renderer)

            if (router != null) {
                RouterContext.withRouter(router) {
                    content()
                }
            } else {
                content()
            }
        }
    }

    @Test
    fun testLinkWithRouter() {
        val renderer = MockLinkRenderer()
        val router = MockRouter()

        runComposableTest(renderer, router) {
            Link(text = "Home", href = "/home")
        }

        // Verify the link was rendered with correct href
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/home", renderer.lastHref)
        assertEquals("_self", renderer.lastTarget) // Default target is "_self"

        assertNotNull(renderer.capturedOnClickHandler, "Link should capture click handler")
        renderer.capturedOnClickHandler?.invoke()
        assertEquals("/home", router.lastNavigatedPath, "router should navigate when link is clicked")
    }

    @Test
    fun testLinkWithCustomOnClick() {
        val renderer = MockLinkRenderer()
        val router = MockRouter()
        var customInvoked = false

        runComposableTest(renderer, router) {
            Link(
                text = "Custom Click",
                href = "/custom",
                onClick = { customInvoked = true }
            )
        }

        // Verify the link was rendered with correct href
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/custom", renderer.lastHref)

        assertNotNull(renderer.capturedOnClickHandler, "click handler should be attached")
        renderer.capturedOnClickHandler?.invoke()
        assertTrue(customInvoked, "custom handler should be invoked")
        assertEquals("/custom", router.lastNavigatedPath)
    }

    @Test
    fun testLinkWithExternalTarget() {
        val renderer = MockLinkRenderer()
        val router = MockRouter()

        runComposableTest(renderer, router) {
            Link(
                text = "External",
                href = "https://example.com",
                target = "_blank"
            )
        }

        // Verify target was set correctly
        assertEquals("_blank", renderer.lastTarget)

        renderer.capturedOnClickHandler?.invoke()
        assertNull(router.lastNavigatedPath)
    }

    @Test
    fun testLinkWithoutRouter() {
        val renderer = MockLinkRenderer()

        runComposableTest(renderer) {
            Link(text = "No Router", href = "/no-router")
        }

        // Verify the link was rendered with correct href
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/no-router", renderer.lastHref)

        // Simulate click on the link - should not throw an exception
        renderer.capturedOnClickHandler?.invoke()
    }
}
