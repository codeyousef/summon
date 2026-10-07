package codes.yousef.summon.components.html

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class SemanticElementsContractTest {
    @Test
    fun optionalInlineTextListAndDisclosureAttributesRenderExactly() {
        val html = PlatformRenderer().renderComposableRoot {
            A("/file", "_blank", "noopener", "file.txt", "en", "text/plain") { Text("download") }
            A("/plain") { Text("plain") }
            Time("2026-01-01") { Text("date") }
            Time { Text("unknown date") }
            Q("https://example.test/source") { Text("quote") }
            Q { Text("uncited") }
            Blockquote("https://example.test/long") { Text("long quote") }
            Blockquote { Text("uncited long") }
            Ol(start = 3, reversed = true, type = "I") { Li(7) { Text("item") } }
            Ol { Li { Text("default item") } }
            Details(open = true, onToggle = {}, name = "accordion") { Summary { Text("summary") } }
            Details { Summary { Text("closed") } }
            Dialog(open = true) { Text("dialog") }
            Dialog { Text("closed dialog") }
        }
        assertContains(html, "href=\"/file\"")
        assertContains(html, "target=\"_blank\"")
        assertContains(html, "download=\"file.txt\"")
        assertContains(html, "hreflang=\"en\"")
        assertContains(html, "type=\"text/plain\"")
        assertContains(html, "datetime=\"2026-01-01\"")
        assertContains(html, "cite=\"https://example.test/source\"")
        assertContains(html, "start=\"3\"")
        assertContains(html, "reversed=\"\"")
        assertContains(html, "value=\"7\"")
        assertContains(html, "name=\"accordion\"")
        assertContains(html, "<dialog open=\"\"")
        assertFalse(html.contains("<time datetime=\"\">"))
        assertContains(html, "data-summon-event-toggle-id")
    }

    @Test
    fun semanticWrappersEmitTheirNativeTags() {
        val html = PlatformRenderer().renderComposableRoot {
            H1 { Text("h1") }; H2 { Text("h2") }; H3 { Text("h3") }
            H4 { Text("h4") }; H5 { Text("h5") }; H6 { Text("h6") }
            P { Text("p") }; Pre { Code { Text("code") } }; Strong { Text("strong") }; Em { Text("em") }
            Small { Text("small") }; Mark { Text("mark") }
            Del("https://example.test/old", "2025-01-01") { Text("del") }
            Ins("https://example.test/new", "2026-01-01") { Text("ins") }
            Sub { Text("sub") }; Sup { Text("sup") }; U { Text("u") }; S { Text("s") }
            Span { Text("span") }; Abbr("Hypertext") { Text("HTML") }; Cite { Text("cite") }
            Kbd { Text("kbd") }; Samp { Text("samp") }; Var { Text("var") }; Dfn { Text("dfn") }
            Data("42") { Text("data") }; Bdi { Text("bdi") }; Bdo("rtl") { Text("bdo") }
            Ruby { Rt { Text("rt") }; Rp { Text("rp") } }
            Ul { Li { Text("ul") } }; Dl { Dt { Text("term") }; Dd { Text("definition") } }; Menu { Li { Text("menu") } }
        }
        listOf("h1", "h2", "h3", "h4", "h5", "h6", "p", "pre", "code", "strong", "em", "small", "mark", "del", "ins", "sub", "sup", "u", "s", "span", "abbr", "cite", "kbd", "samp", "var", "dfn", "data", "bdi", "bdo", "ruby", "rt", "rp", "ul", "li", "dl", "dt", "dd", "menu").forEach {
            assertContains(html, "<$it")
        }
        assertContains(html, "title=\"Hypertext\"")
        assertContains(html, "value=\"42\"")
        assertContains(html, "dir=\"rtl\"")
    }

    @Test
    fun platformRendererSupportsEveryDeclaredNativeAndGenericTag() {
        val tags = listOf(
            "button", "div", "header", "nav", "main", "footer", "section", "article", "aside", "address",
            "hgroup", "search", "h1", "h2", "h3", "h4", "h5", "h6", "p", "blockquote", "pre", "code", "hr",
            "span", "a", "strong", "em", "small", "mark", "del", "ins", "sub", "sup", "s", "u", "b", "i",
            "abbr", "cite", "q", "kbd", "samp", "var", "dfn", "time", "bdi", "bdo", "br", "data", "ruby",
            "rt", "rp", "wbr", "ul", "ol", "dl", "li", "dt", "dd", "menu", "table", "caption", "colgroup",
            "col", "thead", "tbody", "tfoot", "tr", "th", "td", "figure", "figcaption", "iframe", "embed",
            "object", "param", "source", "track", "audio", "video", "picture", "img", "map", "area", "meter",
            "progress", "details", "summary", "dialog", "form", "input", "textarea", "select", "option",
            "optgroup", "label", "fieldset", "legend", "datalist", "output", "custom-element"
        )
        val html = PlatformRenderer().renderComposableRoot {
            val renderer = codes.yousef.summon.runtime.LocalPlatformRenderer.current
            tags.forEach { tag ->
                renderer.renderHtmlTag(tag, codes.yousef.summon.modifier.Modifier()) { Text(tag) }
            }
        }
        tags.forEach { assertContains(html, "<$it") }
    }
    @Test
    fun customTagsPreserveStyledEscapedAttributesAndExplicitHydrationIdentity() {
        val html = PlatformRenderer().renderComposableRoot {
            val renderer = codes.yousef.summon.runtime.LocalPlatformRenderer.current
            renderer.renderHtmlTag(
                "private-widget",
                Modifier()
                    .style("backgroundColor", "red")
                    .attribute("data-summon-id", "stable-id")
                    .attribute("data-value", "&<>\"'")
            ) { Text("custom") }
        }
        assertContains(html, "<private-widget")
        assertContains(html, "style=\"background-color: red\"")
        assertContains(html, "data-summon-id=\"stable-id\"")
        assertContains(html, "data-value=\"&amp;&lt;&gt;&quot;&#39;\"")
    }

}
