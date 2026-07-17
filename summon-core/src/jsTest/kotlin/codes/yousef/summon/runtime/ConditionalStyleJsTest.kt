package codes.yousef.summon.runtime

import codes.yousef.summon.js.testutil.ensureJsDom
import codes.yousef.summon.modifier.MediaQuery
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.child
import codes.yousef.summon.modifier.color
import codes.yousef.summon.modifier.display
import codes.yousef.summon.modifier.hover
import codes.yousef.summon.modifier.mediaQuery
import codes.yousef.summon.modifier.nthChild
import kotlinx.browser.document
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ConditionalStyleJsTest {

    @BeforeTest
    fun setUpDom() {
        ensureJsDom()
        document.head?.innerHTML = ""
        document.body?.innerHTML = ""
    }

    @Test
    fun jsRendererProcessesStateMediaAndScopedCompatibilityData() {
        val renderer = PlatformRenderer()
        val html = renderer.renderComposableRoot {
            renderer.renderRow(
                Modifier()
                    .display("block")
                    .color("black")
                    .hover(Modifier().display("none").color("red"))
                    .nthChild("2n", Modifier().color("green"))
                    .mediaQuery(MediaQuery.MinWidth(960)) {
                        display("grid").color("navy")
                    }
                    .child(".item") { color("orange") },
            ) {}
        }

        val injectedCss = document.head?.textContent.orEmpty()

        assertTrue(html.contains("display:flex") || html.contains("display: flex"))
        assertTrue(html.contains("color:black") || html.contains("color: black"))
        assertTrue(html.contains("class=\"pseudo-"))
        assertTrue(html.contains("scoped-"))
        assertTrue(!html.contains("pseudo-:"), "Pseudo selector syntax must not leak into class names")
        assertTrue(injectedCss.contains(":hover"))
        assertTrue(injectedCss.contains("display: none !important;"))
        assertTrue(injectedCss.contains("color: red !important;"))
        assertTrue(injectedCss.contains(":nth-child(2n)"))
        assertTrue(injectedCss.contains("@media (min-width: 960px)"))
        assertTrue(injectedCss.contains("display: grid !important;"))
        assertTrue(injectedCss.contains(" > .item"))
        assertTrue(injectedCss.contains("color: orange !important;"))
    }
}
