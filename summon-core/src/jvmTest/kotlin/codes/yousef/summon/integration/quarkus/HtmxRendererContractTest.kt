package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.integration.quarkus.htmx.htmlAttribute
import codes.yousef.summon.integration.quarkus.htmx.htmx
import codes.yousef.summon.integration.quarkus.htmx.htmxGet
import codes.yousef.summon.integration.quarkus.renderer.HtmxAwareRenderer
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class HtmxRendererContractTest {
    @Test
    fun enhancedRendererEmitsHtmxAttributesAndExplicitTrustedMarkup() {
        val renderer = HtmxAwareRenderer()
        val html = renderer.renderToString {
            val platform = LocalPlatformRenderer.current
            platform.renderText(
                "Load details",
                Modifier()
                    .htmlAttribute("id", "details")
                    .htmxGet("/details", target = "#details", swap = "outerHTML", trigger = "click")
            )
            renderer.RawHtml(TrustedHtml.fromAuthorCode("<strong>Author markup</strong>"))
        }

        assertContains(html, "id=\"details\"")
        assertContains(html, "hx-get=\"/details\"")
        assertContains(html, "hx-target=\"#details\"")
        assertContains(html, "hx-swap=\"outerHTML\"")
        assertContains(html, "hx-trigger=\"click\"")
        assertContains(html, "<strong>Author markup</strong>")
        assertFalse(html.contains("__raw_html"))
    }

    @Test
    fun activeOrMalformedAttributeNamesAreRejected() {
        assertFailsWith<IllegalArgumentException> { Modifier().htmlAttribute("onclick", "alert(1)") }
        assertFailsWith<IllegalArgumentException> { Modifier().htmlAttribute("srcdoc", "<script></script>") }
        assertFailsWith<IllegalArgumentException> { Modifier().htmlAttribute("__raw_html", "<b>unsafe</b>") }
        assertFailsWith<IllegalArgumentException> { Modifier().htmlAttribute("bad name", "value") }
        assertFailsWith<IllegalArgumentException> { Modifier().htmx("unknown", "value") }
    }

    @Test
    fun trustedTemplateEscapesTitleAndUsesNoInlineOrThirdPartyScript() {
        val html = HtmxAwareRenderer().renderTemplate(
            "<&>\"'",
            TrustedHtml.fromAuthorCode("<section>Trusted body</section>")
        )

        assertContains(html, "<title>&lt;&amp;&gt;&quot;&#39;</title>")
        assertContains(html, "<main id=\"app\"><section>Trusted body</section></main>")
        assertFalse(html.contains("<script"))
        assertFalse(html.contains("unpkg.com"))
    }

    @Test
    fun legacyQuarkusHelpersEscapeTextAndNeverEmitStringHandlers() {
        val renderer = QuarkusExtension.SummonRenderer()
        val heading = renderer.renderHeading(2, "Hello <team>")
        val paragraph = renderer.renderParagraph("A & B")
        val button = renderer.renderButton("Save \"now\"")
        val page = renderer.renderTemplate(
            "Safe",
            TrustedHtml.fromAuthorCode(heading.value + paragraph.value + button.value)
        )

        assertContains(page, "<h2>Hello &lt;team&gt;</h2>")
        assertContains(page, "<p>A &amp; B</p>")
        assertContains(page, "Save &quot;now&quot;")
        assertFalse(page.contains("onclick="))
        assertFailsWith<IllegalArgumentException> { renderer.renderHeading(0, "Invalid") }
        assertFailsWith<IllegalArgumentException> { renderer.renderHeading(7, "Invalid") }
    }
}
