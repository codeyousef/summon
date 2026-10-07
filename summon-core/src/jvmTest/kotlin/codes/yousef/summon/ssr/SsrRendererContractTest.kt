package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.security.PublicHydrationState
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SsrRendererContractTest {
    private val hostile = "<&>\"'"

    @Test
    fun dynamicRendererEscapesEveryMetadataSurfaceAndEmitsOnlyInertPublicState() {
        val seo = SeoMetadata(
            title = "title$hostile",
            description = "description$hostile",
            keywords = listOf("one$hostile", "two"),
            canonical = "https://example.test/?q=$hostile",
            openGraph = OpenGraphMetadata(
                title = "og-title$hostile",
                description = "og-description$hostile",
                type = "article$hostile",
                url = "https://example.test/og?$hostile",
                image = "https://example.test/image?$hostile",
                siteName = "site$hostile"
            ),
            twitterCard = TwitterCardMetadata(
                card = "summary$hostile",
                site = "site$hostile",
                creator = "creator$hostile",
                title = "twitter-title$hostile",
                description = "twitter-description$hostile",
                image = "https://example.test/twitter?$hostile"
            ),
            structuredData = "{\"value\":\"</script><script>fail()</script>\"}",
            robots = "noindex$hostile",
            customMetaTags = mapOf("custom$hostile" to "value$hostile")
        )
        val context = RenderContext(
            enableHydration = true,
            metadata = mapOf(
                "extra$hostile" to "value$hostile",
                "stylesheet" to "https://example.test/style?$hostile",
                "stylesheet.print" to "/print$hostile.css",
                "theme" to "dark$hostile"
            ),
            seoMetadata = seo,
            publicState = PublicHydrationState("{\"safe\":\"</script>\"}")
        )
        val html = DynamicRenderer(PlatformRenderer()).render({ Text("dynamic") }, context)

        assertContains(html, "dynamic")
        assertContains(html, "<title>title&lt;&amp;&gt;&quot;&#39;</title>")
        assertContains(html, "id=\"summon-public-state\"")
        assertContains(html, "\\u003c/script>")
        assertContains(html, "summon-hydration.js")
        assertFalse(html.contains("<script>fail()</script>"))
        assertFalse(html.contains("style?<&>"))

        val minimal = DynamicRenderer(PlatformRenderer()).render({ Text("minimal") }, RenderContext())
        assertContains(minimal, "minimal")
        assertFalse(minimal.contains("summon-public-state"))
        assertFalse(minimal.contains("summon-hydration.js"))
    }

    @Test
    fun streamingRendererCoversLogicalAndFallbackChunkingWithEscapedHead() = runBlocking {
        val richContext = RenderContext(
            enableHydration = true,
            metadata = mapOf("custom" to "value$hostile"),
            seoMetadata = SeoMetadata(
                title = "stream$hostile",
                description = "description$hostile",
                keywords = listOf("one", "two"),
                canonical = "https://example.test/$hostile",
                openGraph = OpenGraphMetadata("title", "description", "article", "/url", "/image", "site"),
                twitterCard = TwitterCardMetadata("summary_large_image", "@site", "@creator", "title", "description", "/image"),
                structuredData = "{\"x\":\"</script>\"}",
                customMetaTags = mapOf("custom" to "custom-value")
            ),
            publicState = PublicHydrationState("{\"stream\":true}")
        )
        val chunks = StreamingRenderer(platformRenderer = PlatformRenderer(), chunkSize = 24)
            .renderStream({ Text("first section with spaces ${"x".repeat(120)}") }, richContext)
            .toList()
        val html = chunks.joinToString("")
        assertTrue(chunks.count { it.contains("data-summon-chunk=\"") } > 1)
        assertContains(html, "<title>stream&lt;&amp;&gt;&quot;&#39;</title>")
        assertContains(html, "summon-stream-complete")
        assertContains(html, "summon-public-state")
        assertContains(html, "summon-hydration.js")
        assertFalse(html.contains("</script><script>"))

        val minimal = StreamingRenderer(platformRenderer = PlatformRenderer(), chunkSize = 4096)
            .renderStream({ Text("short") }, RenderContext())
            .toList().joinToString("")
        assertContains(minimal, "short")
        assertFalse(minimal.contains("summon-stream-complete"))
        assertFalse(minimal.contains("summon-public-state"))
    }

    @Test
    fun staticRendererEscapesMetadataAndSupportsCompleteAndMinimalDocuments() {
        val metadata = linkedMapOf(
            "title" to "static$hostile",
            "description" to "description$hostile",
            "canonical" to "https://example.test/$hostile",
            "author" to "author$hostile",
            "keywords" to "keywords$hostile",
            "robots" to "robots$hostile",
            "og:title" to "og$hostile",
            "og:description" to "og-description$hostile",
            "og:image" to "/image$hostile",
            "og:url" to "/url$hostile",
            "og:type" to "article$hostile",
            "twitter:card" to "summary$hostile",
            "twitter:site" to "site$hostile",
            "twitter:creator" to "creator$hostile",
            "lang" to "en$hostile",
            "dir" to "ltr$hostile",
            "favicon" to "/icon$hostile",
            "custom$hostile" to "value$hostile",
            "twitter:extra$hostile" to "social$hostile"
        )
        val context = RenderContext(
            metadata = metadata,
            seoMetadata = SeoMetadata(customMetaTags = metadata),
            publicState = PublicHydrationState("{\"static\":\"</script>\"}")
        )
        val html = StaticRenderer(PlatformRenderer()).render({ Text("static") }, context)
        assertContains(html, "<title>static&lt;&amp;&gt;&quot;&#39;</title>")
        assertContains(html, "custom&lt;&amp;&gt;&quot;&#39;")
        assertContains(html, "summon-public-state")
        assertFalse(html.contains("static<&>"))

        val minimal = StaticRenderer(PlatformRenderer()).render({ Text("minimal") }, RenderContext())
        assertContains(minimal, "<title>Static Page</title>")
        assertContains(minimal, "minimal")
        assertFalse(minimal.contains("summon-public-state"))

        val utility = StaticRendering.renderToString({ Text("utility") }, mapOf("title" to "Utility"))
        assertContains(utility, "<title>Utility</title>")
        assertContains(renderDocumentToString(PlatformRenderer()) { Text("document") }, "document")
    }
    @Test
    fun bodyExtractionHandlesAttributesAndMalformedOrFragmentRendererOutput() {
        class FixedRenderer(private val output: String) : PlatformRenderer() {
            override fun renderComposableRoot(composable: @Composable (() -> Unit)): String = output
        }

        assertTrue(renderToString(FixedRenderer("<html><body class=\"app\"> body </body></html>")) {}.html == "body")
        val missingClose = "<html><body>unterminated"
        assertTrue(renderToString(FixedRenderer(missingClose)) {}.html == missingClose)
        val fragment = "<main>fragment</main>"
        assertTrue(renderToString(FixedRenderer(fragment)) {}.html == fragment)
    }

    @Test
    fun streamingUtilityChunksLargeDocumentsAndEmitsHydrationCompletion() = runBlocking {
        val chunks = StreamingSSR.renderToFlow {
            Text("x".repeat(9_000))
        }.toList()
        val html = chunks.joinToString("")
        assertTrue(chunks.count { it.contains("data-summon-chunk=\"") } >= 3)
        assertContains(html, "data-summon-stream-complete=\"true\"")
        assertContains(html, "\"strategy\":\"PROGRESSIVE\"")

        val custom = StreamingSSR.createRenderer(
            platformRenderer = PlatformRenderer(),
            chunkSize = 8,
        ).renderStream({ Text("words separated by spaces") }, RenderContext()).toList()
        assertTrue(custom.count { it.contains("data-summon-chunk=\"") } > 1)
    }

    @Test
    fun standalonePageUtilityAcceptsEscapedSeoAndOptionalPublicState() {
        val seo = SeoMetadata(
            title = "title$hostile",
            description = "description$hostile",
            keywords = listOf("one", "two$hostile"),
            canonical = "https://example.test/$hostile",
            openGraph = OpenGraphMetadata(
                title = "og-title$hostile",
                description = "og-description$hostile",
                type = "article$hostile",
                url = "https://example.test/og$hostile",
                image = "https://example.test/image$hostile",
            ),
            twitterCard = TwitterCardMetadata(
                card = "summary$hostile",
                site = "site$hostile",
                creator = "creator$hostile",
            ),
            robots = "index$hostile",
            customMetaTags = linkedMapOf(
                "og:locale" to "en$hostile",
                "twitter:label" to "label$hostile",
                "author" to "author$hostile",
            ),
        )
        val html = ServerSideRenderUtils.renderPageToString(
            rootComposable = { Text("body") },
            publicState = PublicHydrationState("""{"safe":true}"""),
            seoMetadata = seo,
        )
        assertContains(html, "<title>title&lt;&amp;&gt;&quot;&#39;</title>")
        assertContains(html, "property=\"og:locale\"")
        assertContains(html, "property=\"twitter:label\"")
        assertContains(html, "name=\"author\"")
        assertContains(html, """{"safe":true}""")
        assertContains(scriptSafeJson("\u2028\u2029<"), "\\u2028\\u2029\\u003c")
        assertFalse(html.contains("title<&>"))

        val minimal = ServerSideRenderUtils.renderPageToString(
            rootComposable = { Text("minimal") },
            includeHydrationScript = false,
        )
        assertContains(minimal, "<title>SSR Page</title>")
        assertFalse(minimal.contains("summon-public-state"))
        assertTrue(scriptSafeJson("plain") == "plain")
        assertFailsWith<IllegalArgumentException> { scriptSafeJson("x".repeat(65_537)) }
    }

}
