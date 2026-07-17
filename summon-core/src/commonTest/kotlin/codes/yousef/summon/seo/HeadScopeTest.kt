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
}
