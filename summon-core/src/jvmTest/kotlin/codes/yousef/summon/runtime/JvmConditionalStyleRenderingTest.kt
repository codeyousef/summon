package codes.yousef.summon.runtime

import codes.yousef.summon.modifier.MediaQuery
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.active
import codes.yousef.summon.modifier.child
import codes.yousef.summon.modifier.color
import codes.yousef.summon.modifier.display
import codes.yousef.summon.modifier.focus
import codes.yousef.summon.modifier.focusWithin
import codes.yousef.summon.modifier.hover
import codes.yousef.summon.modifier.mediaQuery
import codes.yousef.summon.modifier.nthChild
import codes.yousef.summon.modifier.style
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmConditionalStyleRenderingTest {

    @Test
    fun nonHydratedSsrEmitsStructuredConditionalCssInTheSameDocument() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            renderer.renderDiv(
                Modifier()
                    .display("block")
                    .color("black")
                    .hover(
                        Modifier()
                            .display("none")
                            .color("red")
                            .style("backgroundImage", "url(https://example.test/hover.png)")
                    )
                    .focus(Modifier().color("blue"))
                    .active(Modifier().style("transform", "scale(0.98)"))
                    .focusWithin(Modifier().style("borderColor", "purple"))
                    .nthChild("2n", Modifier().color("green"))
                    .mediaQuery(MediaQuery.MinWidth(960)) {
                        display("grid").color("navy")
                    }
                    .child(".item") { color("orange") },
            ) {}
        }

        assertTrue(html.contains("style=\"display: block; color: black;\""))
        assertTrue(html.contains("data-summon-style-id=\"summon-style-0\""))
        assertTrue(html.contains("<style data-summon-conditional-styles=\"true\">"))
        assertTrue(html.contains("[data-summon-style-id=\"summon-style-0\"]:hover"))
        assertTrue(html.contains("display: none !important;"))
        assertTrue(html.contains("color: red !important;"))
        assertTrue(html.contains("background-image: url(https://example.test/hover.png) !important;"))
        assertTrue(html.contains(":focus"))
        assertTrue(html.contains(":active"))
        assertTrue(html.contains(":focus-within"))
        assertTrue(html.contains(":nth-child(2n)"))
        assertTrue(html.contains("@media (min-width: 960px)"))
        assertTrue(html.contains("display: grid !important;"))
        assertTrue(html.contains("] > .item"))
        assertTrue(html.indexOf("<style data-summon-conditional-styles") < html.indexOf("</head>"))
        assertEquals(1, html.split("<style data-summon-conditional-styles").size - 1)
    }

    @Test
    fun hydratedSsrResetsDeterministicStyleMarkersBetweenDocuments() {
        val renderer = PlatformRenderer()
        val modifier = Modifier()
            .color("black")
            .hover(Modifier().color("white"))
            .mediaQuery(MediaQuery.MaxWidth(640)) { display("none") }

        val first = renderer.renderComposableRootWithHydration {
            renderer.renderDiv(modifier) {}
        }
        val second = renderer.renderComposableRootWithHydration {
            renderer.renderDiv(modifier) {}
        }

        listOf(first, second).forEach { html ->
            assertTrue(html.contains("data-summon-style-id=\"summon-style-0\""))
            assertTrue(html.contains("[data-summon-style-id=\"summon-style-0\"]:hover"))
            assertTrue(html.contains("color: white !important;"))
            assertTrue(html.contains("@media (max-width: 640px)"))
            assertTrue(html.contains("display: none !important;"))
        }
    }
}
