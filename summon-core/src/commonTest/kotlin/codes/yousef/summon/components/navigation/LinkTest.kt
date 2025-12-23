package codes.yousef.summon.components.navigation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.IconType
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.feedback.AlertVariant
import codes.yousef.summon.components.feedback.ProgressType
import codes.yousef.summon.components.input.FileInfo
import codes.yousef.summon.core.FlowContentCompat
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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

    // Mock Renderer implementing PlatformRenderer directly
    class MockLinkRenderer : MockPlatformRenderer() {
        var lastHref: String? = null
        var lastTarget: String? = null
        var lastTitle: String? = null
        var lastAriaLabel: String? = null
        var lastAriaDescribedBy: String? = null
        var lastModifier: Modifier? = null
        var renderEnhancedLinkCalled = false
        var renderTextCallCount = 0
        var lastRenderedText: String? = null
        var contentExecuted = false

        // Implement renderEnhancedLink with content
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
            lastTitle = title
            lastAriaLabel = ariaLabel
            lastAriaDescribedBy = ariaDescribedBy
            lastModifier = modifier
            // Execute the content lambda to verify it's passed correctly
            contentExecuted = true
            content()
        }

        // --- Add No-Op implementations for ALL PlatformRenderer methods ---
        // Ones previously added:
        override fun renderText(text: String, modifier: Modifier) {
            renderTextCallCount++
            lastRenderedText = text
        }

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
            content: @Composable (FlowContentCompat.() -> Unit)
        ) {
        }

        override fun renderGrid(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderBlock(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
        override fun renderDiv(modifier: Modifier, content: @Composable FlowContentCompat.() -> Unit) {}
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
            provider.current // Access current to potentially initialize?
            content() // Run the actual composable
        }
    }

    @Test
    fun testBasicLinkRendering() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
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
    fun linkPassesContentToRenderer() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(href = "/docs") {
                Text("Docs")
            }
        }

        assertEquals(true, renderer.contentExecuted)
        assertEquals("Docs", renderer.lastRenderedText)
    }

    @Test
    fun clientNavigationModeOverwritesHrefAndSetsDataHref() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "#features",
                navigationMode = LinkNavigationMode.Client
            ) {
                Text("Read more")
            }
        }

        assertEquals("#", renderer.lastHref)
        val modifier = renderer.lastModifier ?: error("Modifier should be recorded")
        assertEquals("#features", modifier.attributes["data-href"])
    }

    @Test
    fun testLinkWithTargetBlank() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
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
        runComposableTest(renderer) {
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
        runComposableTest(renderer) {
            Link(href = "/page", isNoFollow = true) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertNotNull(renderer.lastModifier)
        assertEquals("nofollow", renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testCombinedFlagsAndRel() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
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
        runComposableTest(renderer) {
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

    @Test
    fun testLinkWithTitle() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/about",
                title = "Learn more about us"
            ) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/about", renderer.lastHref)
        assertEquals("Learn more about us", renderer.lastTitle)
    }

    @Test
    fun testLinkWithCustomModifier() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/contact",
                modifier = Modifier().className("nav-link").id("contact-link")
            ) { /* Content */ }
        }
        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/contact", renderer.lastHref)
        assertNotNull(renderer.lastModifier)
        assertEquals("nav-link", renderer.lastModifier?.attributes?.get("class"))
        assertEquals("contact-link", renderer.lastModifier?.attributes?.get("id"))
    }

    @Test
    fun testLinkWithAllAttributes() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "https://docs.example.com",
                target = "_blank",
                rel = "help",
                title = "Documentation",
                ariaLabel = "View documentation",
                ariaDescribedBy = "docs-desc",
                isExternal = true,
                isNoFollow = true,
                modifier = Modifier().style("color", "blue")
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("https://docs.example.com", renderer.lastHref)
        assertEquals("_blank", renderer.lastTarget)
        assertEquals("Documentation", renderer.lastTitle)
        assertEquals("View documentation", renderer.lastAriaLabel)
        assertEquals("docs-desc", renderer.lastAriaDescribedBy)

        // Check combined rel attributes
        assertNotNull(renderer.lastModifier)
        val relValue = renderer.lastModifier?.attributes?.get("rel")
        val relParts = relValue?.split(" ")?.filter { it.isNotBlank() }?.toSet()
        val expectedRelParts = setOf("help", "noopener", "noreferrer", "nofollow")
        assertEquals(expectedRelParts, relParts)

        // Check custom style
        assertEquals("color: blue;", renderer.lastModifier?.toStyleString())
    }

    @Test
    fun testInternalLinkWithNoAttributes() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(href = "/home") { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("/home", renderer.lastHref)
        assertNull(renderer.lastTarget)
        assertNull(renderer.lastTitle)
        assertNull(renderer.lastAriaLabel)
        assertNull(renderer.lastAriaDescribedBy)
        assertNull(renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testRelativeLink() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(href = "../parent-page") { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("../parent-page", renderer.lastHref)
        assertNull(renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testAnchorLink() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(href = "#section-header") { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("#section-header", renderer.lastHref)
        assertNull(renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testLinkWithMultipleRelValues() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/resource",
                rel = "author license"
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertNotNull(renderer.lastModifier)
        assertEquals("author license", renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testLinkWithEmptyHref() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(href = "") { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("", renderer.lastHref)
    }

    @Test
    fun testMailtoLink() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "mailto:support@example.com",
                title = "Send us an email"
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("mailto:support@example.com", renderer.lastHref)
        assertEquals("Send us an email", renderer.lastTitle)
    }

    @Test
    fun testTelLink() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "tel:+1234567890",
                ariaLabel = "Call us"
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("tel:+1234567890", renderer.lastHref)
        assertEquals("Call us", renderer.lastAriaLabel)
    }

    @Test
    fun testLinkTargetSelf() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/page",
                target = "_self"
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("_self", renderer.lastTarget)
        // Should not add noopener noreferrer for _self
        assertNull(renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testLinkTargetParent() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/page",
                target = "_parent"
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("_parent", renderer.lastTarget)
        // Should not add noopener noreferrer for _parent
        assertNull(renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun testLinkTargetTop() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/page",
                target = "_top"
            ) { /* Content */ }
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("_top", renderer.lastTarget)
        // Should not add noopener noreferrer for _top
        assertNull(renderer.lastModifier?.attributes?.get("rel"))
    }

    @Test
    fun anchorLinkAppliesDataAttributesAndLabelExactlyOnce() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            AnchorLink(
                label = "Get Started",
                href = "#get-started",
                id = "hero-cta",
                target = "_blank",
                dataHref = "#get-started",
                ariaLabel = "Jump to hero CTA",
                dataAttributes = mapOf(
                    "analytics" to "hero",
                    "copy" to "installation"
                )
            )
        }

        assertEquals(true, renderer.renderEnhancedLinkCalled)
        assertEquals("#get-started", renderer.lastHref)
        val modifier = renderer.lastModifier!!
        assertEquals("hero-cta", modifier.attributes["id"])
        assertEquals("#get-started", modifier.attributes["data-href"])
        assertEquals("hero", modifier.attributes["data-analytics"])
        assertEquals("installation", modifier.attributes["data-copy"])
        assertEquals("Get Started", renderer.lastRenderedText)
        assertEquals(1, renderer.renderTextCallCount)
    }

    @Test
    fun linkSupportsStructuredDataAttributes() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "/docs",
                dataHref = "/docs#getting-started",
                dataAttributes = mapOf("copy" to "docs")
            ) {
                Text("Docs")
            }
        }

        val modifier = renderer.lastModifier!!
        assertEquals("/docs#getting-started", modifier.attributes["data-href"])
        assertEquals("docs", modifier.attributes["data-copy"])
    }

    @Test
    fun clientNavigationPrefersExplicitDataHref() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            Link(
                href = "#faq",
                navigationMode = LinkNavigationMode.Client,
                dataHref = "/docs#faq"
            ) {
                Text("FAQ")
            }
        }

        val modifier = renderer.lastModifier!!
        assertEquals("/docs#faq", modifier.attributes["data-href"])
        assertEquals("#", renderer.lastHref)
    }

    @Test
    fun buttonLinkCarriesDataAttributesToModifier() {
        val renderer = MockLinkRenderer()
        runComposableTest(renderer) {
            ButtonLink(
                label = "Launch App",
                href = "/launch",
                dataHref = "/launch?source=hero",
                dataAttributes = mapOf("analytics" to "cta")
            )
        }

        val modifier = renderer.lastModifier!!
        assertEquals("/launch?source=hero", modifier.attributes["data-href"])
        assertEquals("cta", modifier.attributes["data-analytics"])
    }
}
