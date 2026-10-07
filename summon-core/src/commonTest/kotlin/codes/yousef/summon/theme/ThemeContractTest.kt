package codes.yousef.summon.theme

import codes.yousef.summon.core.style.Color
import codes.yousef.summon.modifier.FontWeight
import codes.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ThemeContractTest {
    @Test
    fun typedAndLegacyTextStylesPreserveEverySupportedProperty() {
        val typed = Theme.TextStyle.create(
            fontFamily = "Inter",
            fontSize = 1.25,
            fontSizeUnit = "rem",
            fontWeight = FontWeight.SemiBold,
            fontStyle = "italic",
            color = Color.hex("#123456"),
            textDecoration = "underline",
            lineHeight = 1.5,
            letterSpacing = 0.02,
            letterSpacingUnit = "em"
        )
        assertEquals("1.25rem", typed.fontSize)
        assertEquals("600", typed.fontWeight)
        assertEquals("#123456ff", typed.color)
        assertEquals("1.5", typed.lineHeight)
        assertEquals("0.02em", typed.letterSpacing)

        val partial = Theme.TextStyle.create(fontSize = 12, fontSizeUnit = null, letterSpacing = 2, letterSpacingUnit = null)
        assertEquals(null, partial.fontSize)
        assertEquals(null, partial.letterSpacing)
        val empty = Theme.TextStyle.create()
        assertEquals(null, empty.fontSize)

        val original = Theme.getTheme()
        try {
            val legacy = Theme.TextStyle(
                fontFamily = "serif",
                fontSize = "14px",
                fontWeight = "700",
                fontStyle = "oblique",
                color = "red",
                textDecoration = "line-through",
                lineHeight = "2",
                letterSpacing = "1px"
            )
            val custom = original.copy(
                typography = original.typography + mapOf("typed" to typed, "legacy" to legacy),
                spacing = original.spacing + mapOf("custom" to "3rem"),
                borderRadius = original.borderRadius + mapOf("custom" to "12px"),
                elevation = original.elevation + mapOf("custom" to "shadow"),
                customValues = mapOf("token" to "value")
            )
            Theme.setTheme(custom)

            val typedStyles = Modifier().themeTextStyle("typed").styles
            assertEquals("Inter", typedStyles["font-family"])
            assertEquals("1.25rem", typedStyles["font-size"])
            assertEquals("600", typedStyles["font-weight"])
            assertEquals("italic", typedStyles["font-style"])
            assertEquals("rgba(18, 52, 86, 1.0)", typedStyles["color"])
            assertEquals("underline", typedStyles["text-decoration"])
            assertEquals("1.5", typedStyles["line-height"])
            assertEquals("0.02em", typedStyles["letter-spacing"])

            val legacyStyles = Modifier().themeTextStyle("legacy").styles
            assertEquals("14px", legacyStyles["font-size"])
            assertEquals("700", legacyStyles["font-weight"])
            assertEquals("red", legacyStyles["color"])
            assertEquals("2", legacyStyles["line-height"])
            assertEquals("1px", legacyStyles["letter-spacing"])

            assertEquals("3rem", Theme.getSpacing("custom"))
            assertEquals(Spacing.md, Theme.getSpacing("missing"))
            assertEquals("12px", Theme.getBorderRadius("custom"))
            assertEquals("0", Theme.getBorderRadius("missing"))
            assertEquals("shadow", Theme.getElevation("custom"))
            assertEquals("none", Theme.getElevation("missing"))
            assertEquals("value", Theme.getCustomValue("token", "fallback"))
            assertEquals("fallback", Theme.getCustomValue("missing", "fallback"))
            assertEquals("#000000", Theme.getColor("missing"))
            assertSame(custom.typographyTheme, Theme.getTypographyTheme())
            assertSame(custom.spacingTheme, Theme.getSpacingTheme())
            assertSame(custom.borderRadiusTheme, Theme.getBorderRadiusTheme())
            assertSame(custom.elevationTheme, Theme.getElevationTheme())
        } finally {
            Theme.setTheme(original)
        }
    }

    @Test
    fun directionalSpacingAndThemeModifiersCoverAllShorthandForms() {
        val original = Theme.getTheme()
        try {
            Theme.setTheme(Theme.Themes.light)
            assertEquals("0", Modifier().themePadding().styles["padding"])
            assertEquals("4px", Modifier().themePadding(top = "xs").styles["padding"])
            assertEquals("4px 8px", Modifier().themePadding(top = "xs", right = "sm").styles["padding"])
            assertEquals(
                "4px 8px 16px",
                Modifier().themePadding(top = "xs", right = "sm", bottom = "md").styles["padding"]
            )
            assertEquals(
                "4px 8px 16px 24px",
                Modifier().themePadding(top = "xs", right = "sm", bottom = "md", left = "lg").styles["padding"]
            )
            assertEquals("0", Modifier().themeMargin().styles["margin"])
            assertEquals("4px", Modifier().themeMargin(top = "xs").styles["margin"])
            assertEquals("4px 8px", Modifier().themeMargin(top = "xs", right = "sm").styles["margin"])
            assertEquals(
                "4px 8px 16px",
                Modifier().themeMargin(top = "xs", right = "sm", bottom = "md").styles["margin"]
            )
            assertEquals(
                "4px 8px 16px 24px",
                Modifier().themeMargin(top = "xs", right = "sm", bottom = "md", left = "lg").styles["margin"]
            )

            val combined = Modifier()
                .themeColor("primary")
                .themeBackgroundColor("surface")
                .themeStyleBorder("1px", "solid", "primary")
                .themeBorderRadius("md")
                .themePadding("sm")
                .themeMargin("lg")
                .themeElevation("xs")
            assertEquals("8px", combined.styles["border-radius"])
            assertEquals("8px", combined.styles["padding"])
            assertEquals("24px", combined.styles["margin"])
        } finally {
            Theme.setTheme(original)
        }
    }

    @Test
    fun predefinedAndExtendedThemesExposeIndependentConfiguration() {
        listOf(Theme.Themes.light, Theme.Themes.dark, Theme.Themes.blue, Theme.Themes.green, Theme.Themes.purple)
            .forEach { theme -> assertEquals("16px", theme.spacing.getValue("md")) }
        val extended = Theme.createTheme(Theme.Themes.light) { copy(customValues = mapOf("brand" to "summon")) }
        assertEquals("summon", extended.customValues["brand"])
    }
}
