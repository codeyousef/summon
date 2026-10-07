package codes.yousef.summon.seo

import kotlin.test.Test
import kotlin.test.assertEquals

class HeadScopeTest {

    @Test
    fun escapesTitleTextAndAttributeValues() {
        val elements = mutableListOf<String>()
        val head = DefaultHeadScope(elements::add)

        head.title("A < B & C > D")
        head.meta(
            name = "description\" onload=\"alert(1)",
            content = "<tag> & \"quoted\" and 'single'"
        )
        head.link(
            rel = "canonical",
            href = "https://example.test/?a=1&b=\"two\""
        )

        assertEquals("<title>A &lt; B &amp; C &gt; D</title>", elements[0])
        assertEquals(
            """<meta name="description&quot; onload=&quot;alert(1)" content="&lt;tag&gt; &amp; &quot;quoted&quot; and &#39;single&#39;">""",
            elements[1]
        )
        assertEquals(
            """<link rel="canonical" href="https://example.test/?a=1&amp;b=&quot;two&quot;">""",
            elements[2]
        )
    }

    @Test
    fun preventsInlineRawTextFromClosingItsElement() {
        val elements = mutableListOf<String>()
        val head = DefaultHeadScope(elements::add)

        head.script(content = "window.value = '</ScRiPt><p>unsafe</p>'")
        head.style(content = ".demo::after { content: '</StYlE><p>unsafe</p>'; }")

        assertEquals(
            "<script>window.value = '<\\/script><p>unsafe</p>'</script>",
            elements[0]
        )
        assertEquals(
            "<style>.demo::after { content: '<\\/style><p>unsafe</p>'; }</style>",
            elements[1]
        )
    }

    @Test
    fun emitsEveryOptionalHeadAttributeAndOmitsEmptyVoidElements() {
        val elements = mutableListOf<String>()
        val head = DefaultHeadScope(elements::add)

        head.meta()
        head.base()
        assertEquals(emptyList(), elements)

        head.meta(
            name = "refresh",
            property = "og:title",
            content = "content",
            charset = "utf-8",
            httpEquiv = "refresh"
        )
        head.link(
            rel = "icon",
            href = "/icon.svg",
            type = "image/svg+xml",
            sizes = "any",
            crossorigin = "anonymous",
            media = "(prefers-color-scheme: dark)"
        )
        head.script(
            src = "/app.mjs",
            type = "module",
            async = true,
            defer = true,
            crossorigin = "anonymous"
        )
        head.style("body {}", media = "screen")
        head.base(href = "https://example.test/", target = "_blank")

        assertEquals(
            """<meta charset="utf-8" name="refresh" property="og:title" content="content" http-equiv="refresh">""",
            elements[0]
        )
        assertEquals(
            """<link rel="icon" href="/icon.svg" type="image/svg+xml" sizes="any" crossorigin="anonymous" media="(prefers-color-scheme: dark)">""",
            elements[1]
        )
        assertEquals(
            """<script type="module" src="/app.mjs" async defer crossorigin="anonymous"></script>""",
            elements[2]
        )
        assertEquals("""<style media="screen">body {}</style>""", elements[3])
        assertEquals("""<base href="https://example.test/" target="_blank">""", elements[4])
    }

}
