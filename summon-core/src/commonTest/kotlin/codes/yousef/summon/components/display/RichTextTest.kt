package codes.yousef.summon.components.display

import codes.yousef.summon.components.foundation.RawHtml
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.runtime.MockPlatformRenderer
import codes.yousef.summon.util.runComposableTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RichTextTest {
    @Test
    fun untrustedStringNeverUsesHtmlSink() {
        val renderer = MockPlatformRenderer()
        runComposableTest(renderer) {
            RichText("<img src=https://tracker.invalid onerror=alert(1)>שלום")
        }
        assertFalse(renderer.renderHtmlCalled)
    }

    @Test
    fun trustedHtmlRequiresExplicitAuthorCodeValue() {
        val renderer = MockPlatformRenderer()
        val trusted = TrustedHtml.fromAuthorCode("<canvas id=\"app-canvas\"></canvas>")

        runComposableTest(renderer) { Html(trusted) }

        assertTrue(renderer.renderHtmlCalled)
        assertEquals(trusted.value, renderer.lastHtmlContentRendered)
    }

    @Test
    fun rawHtmlBuilderIsAnExplicitAuthorCodeBoundary() {
        val renderer = MockPlatformRenderer()

        runComposableTest(renderer) {
            RawHtml { +"<strong>author content</strong>" }
        }

        assertEquals("<strong>author content</strong>", renderer.lastHtmlContentRendered)
    }

    @Test
    fun outboundLinksRequireHttpsAndAnExplicitNormalizedHost() {
        val allowed = setOf("example.com")
        val normalized = SafeOutboundLink.parse("HTTPS://%65xample.com/path?q=1", allowed)

        assertNotNull(normalized)
        assertEquals("https://example.com/path?q=1", normalized.href)
        assertEquals("example.com", normalized.displayHost)
        assertNull(SafeOutboundLink.parse("javascript:alert(1)", allowed))
        assertNull(SafeOutboundLink.parse("data:text/html,boom", allowed))
        assertNull(SafeOutboundLink.parse("blob:https://example.com/id", allowed))
        assertNull(SafeOutboundLink.parse("https://example.com@evil.invalid/", allowed))
        assertNull(SafeOutboundLink.parse("https://evil.invalid/", allowed))
        assertNull(SafeOutboundLink.parse("https://example.com/%0Aboom", allowed))
    }

    @Test
    fun safeDocumentRetainsOnlyTreeAndPlaintextFallback() {
        val link = SafeOutboundLink.parse("https://example.com/read", setOf("example.com"))!!
        val document = SafeDocument.create(
            nodes = listOf(
                SafeDocumentNode.Container(
                    SafeContainerKind.PARAGRAPH,
                    listOf(
                        SafeDocumentNode.Text("RTL שלום <script>not markup</script>"),
                        SafeDocumentNode.Link(link, listOf(SafeDocumentNode.Text("misleading label")))
                    )
                )
            ),
            plaintextFallback = "RTL שלום misleading label (example.com)"
        )

        assertEquals(SafeDocument.POLICY_VERSION, "summon-safe-document-1")
        assertEquals("RTL שלום misleading label (example.com)", document.plaintextFallback)
        assertEquals(1, document.nodes.size)
    }

    @Test
    fun cidCapabilitiesAcceptOnlyLocalObjectUrls() {
        val reference = CidReference.parse("cid:image-1@example.test")
        assertNotNull(reference)
        assertEquals("image-1@example.test", reference.contentId)
        assertNull(CidReference.parse("cid:../../private"))
        assertNotNull(LocalObjectUrl.create("blob:https://app.example/id") {})
        assertNull(LocalObjectUrl.create("https://tracker.invalid/pixel") {})
        assertNull(LocalObjectUrl.create("data:image/png;base64,AA==") {})
    }
}
