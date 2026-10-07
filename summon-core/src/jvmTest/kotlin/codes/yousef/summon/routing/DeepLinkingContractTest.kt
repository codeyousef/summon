package codes.yousef.summon.routing

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame

class DeepLinkingContractTest {
    @Test
    fun percentEncodingRoundTripsReservedAndUnicodeValues() {
        val deepLinks = DeepLinking.getInstance()
        assertSame(deepLinks, DeepLinking.getInstance())
        val raw = "AZaz09-_.~ !\"#\$%&'()*+,/:;=?@[\\]{|} café 😀"
        val encoded = deepLinks.encodeURIComponent(raw)
        assertFalse(encoded.contains(' '))
        assertFalse(encoded.contains("%25" + "20"), "Percent escapes must not be escaped a second time")
        assertEquals(raw, deepLinks.decodeURIComponent(encoded))
        assertEquals("bad%ZZvalue", deepLinks.decodeURIComponent("bad%ZZvalue"))

        val url = deepLinks.createDeepLink("messages", linkedMapOf("query" to "hello world", "emoji" to "😀"), "thread")
        assertEquals("/messages?query=hello%20world&emoji=%F0%9F%98%80#thread", url)
        val parsed = deepLinks.parseDeepLink(url)
        assertEquals("/messages", parsed.path)
        assertEquals(mapOf("query" to "hello world", "emoji" to "😀"), parsed.queryParams)
        assertEquals("thread", parsed.fragment)
        assertEquals(DeepLinking.DeepLinkInfo("/plain", emptyMap(), null), DeepLinking.parseUrl("/plain"))
        assertEquals("/plain", DeepLinking.createUrl("/plain"))
        assertEquals("", deepLinks.parseDeepLink("").path)
    }

    @Test
    fun routeUrlUtilitiesReplacePathParametersAndRejectUnsafeResults() {
        assertEquals(
            "/mail/thread%20one/message-two?view=full%20screen",
            generateUrl(
                "/mail/{thread}/:message",
                mapOf("thread" to "thread one", "message" to "message-two"),
                mapOf("view" to "full screen")
            )
        )
        assertEquals("/mail/static", generateUrl("/mail/static", emptyMap()))
        assertFailsWith<IllegalArgumentException> {
            generateUrl("/mail/{message}", mapOf("message" to "message/two"))
        }
        assertFailsWith<IllegalArgumentException> { generateUrl("https://example.test/{id}", mapOf("id" to "one")) }
        assertFailsWith<IllegalArgumentException> { generateUrl("/../{id}", mapOf("id" to "one")) }
        assertEquals(mapOf("a" to "hello world", "empty" to ""), extractQueryParams("/path?a=hello%20world&ignored&empty="))
        assertEquals(emptyMap(), extractQueryParams("/path"))
        assertNull(DeepLinkManager.handleDeepLink("/safe"))
    }

    @Test
    fun metadataEscapesAttributesWithAndWithoutSocialImage() {
        val hostile = "<&>\"'"
        val full = PlatformRenderer().renderComposableRoot {
            DeepLinking.getInstance().MetaTags(
                path = "/path?$hostile",
                title = "title$hostile",
                description = "description$hostile",
                imageUrl = "/image?$hostile",
                type = "article$hostile"
            )
        }
        assertContains(full, "title&lt;&amp;&gt;&quot;&#39;")
        assertContains(full, "summary_large_image")
        assertContains(full, "og:image")
        assertFalse(full.contains("title<&>"))

        val minimal = PlatformRenderer().renderComposableRoot {
            DeepLinking.getInstance().MetaTags("/plain", "Plain", "Description")
        }
        assertContains(minimal, "twitter:card")
        assertContains(minimal, "summary")
        assertFalse(minimal.contains("og:image"))
    }
}
