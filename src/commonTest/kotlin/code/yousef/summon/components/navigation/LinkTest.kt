package code.yousef.summon.components.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconType
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.FileInfo
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.html.FlowContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

    // Mock Renderer implementing PlatformRenderer directly
    class MockLinkRenderer : MockPlatformRenderer() {
        var lastHref: String? = null
        var lastTarget: String? = null
        var lastTitle: String? = null
        var lastAriaLabel: String? = null
        var lastAriaDescribedBy: String? = null
        var lastModifier: Modifier? = null
        var renderEnhancedLinkCalled = false

        // Implement renderEnhancedLink
        override fun renderEnhancedLink(
            href: String,
            target: String?,
            title: String?,
            ariaLabel: String?,
            ariaDescribedBy: String?,
            modifier: Modifier
        ) {
            renderEnhancedLinkCalled = true
            lastHref = href
            lastTarget = target
            lastTitle = title
            lastAriaLabel = ariaLabel
            lastAriaDescribedBy = ariaDescribedBy
            lastModifier = modifier
        }

        // --- Add No-Op implementations for ALL PlatformRenderer methods ---
        // Ones previously added:
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

        // Updated/Corrected based on previous reading:
        override fun renderTabLayout(
            tabs: List<Tab>,
            selectedTabIndex: Int,
            onTabSelected: (Int) -> Unit,
            modifier: Modifier
        ) {
        }

        override fun renderTabLayout(
            modifier: Modifier,
            content: @Composable (() -> Unit)
        ) {
        }

        override fun renderTabLayout(
            tabs: List<String>,
            selectedTab: String,
            onTabSelected: (String) -> Unit,
            modifier: Modifier,
            content: () -> Unit
        ) {
        }

        override fun renderDivider(modifier: Modifier) {}
        override fun renderExpansionPanel(
            modifier: Modifier,
            content: @Composable (FlowContent.() -> Unit)
        ) {
        }

        override fun renderGrid(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {}
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
    private fun runTestComposable(renderer: PlatformRenderer, content: @Composable () -> Unit) {
        CompositionLocal.provideComposer(TestComposer()) {
            val provider = LocalPlatformRenderer.provides(renderer)
            provider.current // Access current to potentially initialize?
            content() // Run the actual composable
        }
    }

    @Test
    fun testBasicLinkRendering() {
        val renderer = MockLinkRenderer()
        runTestComposable(renderer) {
            Link(href = "https://example.com") { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("https://example.com", renderer.lastHref)
        assertNull(renderer.lastTarget)
        assertNull(renderer.lastTitle)
        assertNull(renderer.lastAriaLabel)
        assertNull(renderer.lastAriaDescribedBy)
        assertNotNull(renderer.lastModifier)
        assertEquals(null, renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testLinkWithTargetBlank() {
        val renderer = MockLinkRenderer()
        runTestComposable(renderer) {
            Link(href = "/internal", target = "_blank") { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/internal", renderer.lastHref)
        assertEquals("_blank", renderer.lastTarget)
        assertNotNull(renderer.lastModifier)
        assertEquals("noopener noreferrer", renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testExternalLinkFlag() {
        val renderer = MockLinkRenderer()
        runTestComposable(renderer) {
            Link(href = "https://external.site", isExternal = true) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("https://external.site", renderer.lastHref)
        assertNull(renderer.lastTarget) // Target not set explicitly
        assertNotNull(renderer.lastModifier)
        assertEquals("noopener noreferrer", renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testNoFollowLinkFlag() {
        val renderer = MockLinkRenderer()
        runTestComposable(renderer) {
            Link(href = "/page", isNoFollow = true) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertNotNull(renderer.lastModifier)
        assertEquals("nofollow", renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testCombinedFlagsAndRel() {
        val renderer = MockLinkRenderer()
        runTestComposable(renderer) {
            Link(
                href = "https://example.com",
                target = "_blank", // Adds noopener, noreferrer
                rel = "author external", // Provided base rel
                isNoFollow = true // Adds nofollow
            ) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertNotNull(renderer.lastModifier)
        val expectedRelParts = setOf("author", "external", "noopener", "noreferrer", "nofollow")
        val actualRelParts =
            renderer.lastModifier?.attributes?.get("rel")?.split(" ")?.filter { it.isNotBlank() }?.toSet()
        assertEquals(expectedRelParts, actualRelParts)
    }

    @Test
    fun testAriaAttributes() {
        val renderer = MockLinkRenderer()
        runTestComposable(renderer) {
            Link(
                href = "/test",
                ariaLabel = "Test Page Link",
                ariaDescribedBy = "link-description"
            ) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("Test Page Link", renderer.lastAriaLabel)
        assertEquals("link-description", renderer.lastAriaDescribedBy)
    }

    // TODO: Test ExternalLink helper
    // TODO: Test ButtonLink helper
    // TODO: Test link content rendering
} 