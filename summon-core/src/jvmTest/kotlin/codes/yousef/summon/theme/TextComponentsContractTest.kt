package codes.yousef.summon.theme

import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains

class TextComponentsContractTest {
    @Test
    fun everyThemedTextWrapperRendersConfiguredTypographyAndCallerModifier() {
        val original = Theme.getTheme()
        try {
            val complete = Theme.TextStyle(
                fontFamily = "Inter",
                fontSize = "21px",
                fontWeight = "700",
                letterSpacing = "1px",
                color = "#123456",
                textDecoration = "underline",
                lineHeight = "1.4",
                fontStyle = "italic"
            )
            val names = listOf("h1", "h2", "h3", "h4", "h5", "h6", "subtitle", "body", "bodyLarge", "bodySmall", "caption", "button", "overline", "link", "code")
            Theme.setTheme(Theme.ThemeConfig(typography = names.associateWith { complete }))
            val html = PlatformRenderer().renderComposableRoot {
                val modifier = Modifier().attribute("data-themed", "true")
                TextComponents.H1("h1", modifier); TextComponents.H2("h2"); TextComponents.H3("h3")
                TextComponents.H4("h4"); TextComponents.H5("h5"); TextComponents.H6("h6")
                TextComponents.Subtitle("subtitle"); TextComponents.Body("body")
                TextComponents.BodyLarge("bodyLarge"); TextComponents.BodySmall("bodySmall")
                TextComponents.Caption("caption"); TextComponents.Button("button")
                TextComponents.Overline("overline"); TextComponents.Link("link"); TextComponents.Code("code")
            }
            assertContains(html, "data-themed=\"true\"")
            assertContains(html, "font-family: Inter")
            assertContains(html, "font-size: 21px")
            assertContains(html, "font-weight: 700")
            assertContains(html, "letter-spacing: 1px")
            assertContains(html, "color: #123456")
            assertContains(html, "text-decoration: underline")
            assertContains(html, "line-height: 1.4")
            assertContains(html, "font-style: italic")
            names.forEach { assertContains(html, ">$it<") }
        } finally {
            Theme.setTheme(original)
        }
    }

    @Test
    fun absentTypographyPropertiesDoNotInventStyles() {
        val original = Theme.getTheme()
        try {
            Theme.setTheme(Theme.ThemeConfig(typography = mapOf("body" to Theme.TextStyle())))
            val html = PlatformRenderer().renderComposableRoot { TextComponents.Body("plain") }
            assertContains(html, ">plain<")
        } finally {
            Theme.setTheme(original)
        }
    }
}
