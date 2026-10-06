package codes.yousef.summon.components.display

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SafeDocumentJvmTest {
    @Test
    fun untrustedTreeHasTheSameEscapedSemanticsOnSsr() {
        val link = SafeOutboundLink.parse("https://example.com/final", setOf("example.com"))!!
        val cid = CidReference.parse("cid:local@example.test")!!
        val hostile = "<script>globalThis.xss=1</script><img src=https://tracker.invalid>שלום"
        val document = SafeDocument.create(
            nodes = listOf(
                SafeDocumentNode.Text(hostile),
                SafeDocumentNode.Link(link, listOf(SafeDocumentNode.Text("deceptive label"))),
                SafeDocumentNode.CidImage(cid, "Local image unavailable")
            ),
            plaintextFallback = "Plaintext fallback"
        )

        val html = PlatformRenderer().renderComposableRoot {
            SafeDocumentContent(document)
        }

        assertTrue(html.contains("&lt;script&gt;globalThis.xss=1&lt;/script&gt;"))
        assertTrue(html.contains("שלום"))
        assertFalse(html.contains("<script>"))
        assertFalse(html.contains("<img"))
        assertFalse(html.contains("tracker.invalid\""))
        assertTrue(html.contains("href=\"https://example.com/final\""))
        assertTrue(html.contains("(example.com)"))
        assertTrue(html.contains("Local image unavailable"))
    }

    @Test
    fun sourceCollectionsCannotMutateValidatedDocument() {
        val children = mutableListOf<SafeDocumentNode>(SafeDocumentNode.Text("retained"))
        val nodes = mutableListOf<SafeDocumentNode>(
            SafeDocumentNode.Container(SafeContainerKind.PARAGRAPH, children)
        )
        val document = SafeDocument.create(nodes, "fallback")

        children.clear()
        nodes.clear()

        val html = PlatformRenderer().renderComposableRoot {
            SafeDocumentContent(document)
        }
        assertTrue(html.contains("retained"))
        assertFalse(html.contains("fallback"))
    }
}
