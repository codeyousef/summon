package codes.yousef.summon.components.display

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SafeDocumentPolicyContractTest {
    @Test
    fun outboundLinksRequireExactApprovedAsciiHttpsHosts() {
        val allowed = setOf("example.com")
        val valid = SafeOutboundLink.parse("HTTPS://EXAMPLE.COM/path?q=1#fragment", allowed)!!
        assertEquals("https://example.com/path?q=1#fragment", valid.href)
        assertEquals("example.com", valid.displayHost)
        assertEquals("example.com", SafeOutboundLink.parse("https://%65xample.com", allowed)?.displayHost)

        listOf(
            " http://example.com", "http://example.com", "https://example.com\\path",
            "https://user@example.com", "https://example.com:443", "https://evil.example.com",
            "https://.example.com", "https://example..com", "https://-example.com",
            "https://example-.com", "https://exämple.com", "https://%ZZexample.com",
            "https://example.com/%0Aheader", "https://example.com/%FF", "https://"
        ).forEach { assertNull(SafeOutboundLink.parse(it, allowed), it) }
    }

    @Test
    fun cidAndObjectUrlPoliciesRejectUnsafeReferences() {
        assertEquals("local@example.test", CidReference.parse("cid:<local@example.test>")?.contentId)
        assertEquals("plain", CidReference.parse("plain")?.contentId)
        assertNull(CidReference.parse("cid:"))
        assertNull(CidReference.parse("cid:a/b"))
        assertNull(CidReference.parse("cid:${"a".repeat(256)}"))

        var releases = 0
        val resource = LocalObjectUrl.create("blob:https://example.test/id") { releases++ }!!
        resource.release()
        assertEquals(1, releases)
        assertNull(LocalObjectUrl.create("https://example.test/id") {})
        assertNull(LocalObjectUrl.create("blob:id\nheader") {})
    }

    @Test
    fun documentLimitsAndHeadingInvariantsFailClosed() {
        assertFailsWith<IllegalArgumentException> { SafeDocument.plaintext("x".repeat(1_048_577)) }
        assertFailsWith<IllegalArgumentException> {
            SafeDocument.create(List(10_001) { SafeDocumentNode.LineBreak }, "")
        }
        var nested: SafeDocumentNode = SafeDocumentNode.Text("deep")
        repeat(33) { nested = SafeDocumentNode.Container(SafeContainerKind.PARAGRAPH, listOf(nested)) }
        assertFailsWith<IllegalArgumentException> { SafeDocument.create(listOf(nested), "") }
        assertFailsWith<IllegalArgumentException> {
            SafeDocument.create(listOf(SafeDocumentNode.Text("x".repeat(1_048_576))), "x")
        }
        assertFailsWith<IllegalArgumentException> {
            SafeDocumentNode.Container(SafeContainerKind.PARAGRAPH, emptyList(), headingLevel = 2)
        }
        assertFailsWith<IllegalArgumentException> {
            SafeDocumentNode.Container(SafeContainerKind.HEADING, emptyList(), headingLevel = 7)
        }
    }

    @Test
    fun everySafeNodeKindRendersWithBoundedSemantics() {
        val containers = SafeContainerKind.entries.mapIndexed { index, kind ->
            SafeDocumentNode.Container(
                kind,
                listOf(SafeDocumentNode.Text(kind.name)),
                headingLevel = if (kind == SafeContainerKind.HEADING) 3 else 1
            )
        }
        val reference = CidReference.parse("cid:image")!!
        val link = SafeOutboundLink.parse("https://example.com", setOf("example.com"))!!
        val document = SafeDocument.create(
            containers + SafeDocumentNode.LineBreak +
                SafeDocumentNode.Link(link, listOf(SafeDocumentNode.Text("link"))) +
                SafeDocumentNode.CidImage(reference, "fallback"),
            "plaintext"
        )
        val html = PlatformRenderer().renderComposableRoot {
            SafeDocumentContent(document, cidResolver = CidResolver { LocalObjectUrl.create("blob:image") {} })
        }
        SafeContainerKind.entries.forEach { assertContains(html, it.name) }
        assertContains(html, "aria-level=\"3\"")
        assertContains(html, "role=\"list\"")
        assertContains(html, "role=\"listitem\"")
        assertContains(html, "src=\"blob:image\"")
        assertContains(html, "alt=\"fallback\"")

        val fallback = PlatformRenderer().renderComposableRoot {
            SafeDocumentContent(SafeDocument.create(emptyList(), "plain fallback"))
        }
        assertTrue(fallback.contains("plain fallback"))
    }
}
