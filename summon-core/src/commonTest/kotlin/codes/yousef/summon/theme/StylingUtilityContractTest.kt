package codes.yousef.summon.theme

import codes.yousef.summon.animation.KeyframesGenerator
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.cssVar
import codes.yousef.summon.modifier.cssVars
import codes.yousef.summon.modifier.style
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StylingUtilityContractTest {
    @AfterTest
    fun clearStyles() = StyleSheet.clearStyles()

    @Test
    fun cssVariablesNormalizePrefixesAndFallbacks() {
        val modifier = Modifier().cssVar("primary", "red").cssVars(mapOf("--gap" to "8px", "radius" to "4px"))
        assertEquals("red", modifier.styles["--primary"])
        assertEquals("8px", modifier.styles["--gap"])
        assertEquals("4px", modifier.styles["--radius"])
        assertEquals("var(--primary)", cssVar("primary"))
        assertEquals("var(--primary, blue)", cssVar("--primary", "blue"))
    }

    @Test
    fun styleSheetsApplyExtendReplaceRemoveAndRegisterDeclaratively() {
        val base = StyleSheet.createStyle("base") { style("color", "red") }
        assertTrue(StyleSheet.hasStyle("base"))
        assertEquals("red", base.styles["color"])
        assertEquals("red", Modifier().applyStyle("base").styles["color"])
        assertTrue(Modifier().applyStyle("missing").styles.isEmpty())

        StyleSheet.extendStyle("base", "extended") { style("padding", "4px") }
        val combined = Modifier().applyStyles("base", "extended")
        assertEquals("red", combined.styles["color"])
        assertEquals("4px", combined.styles["padding"])

        createStyleSheet {
            style("local") { style("margin", "1px") }
            extendStyle("local-extended", "local") { style("border", "none") }
            extendStyle("global-extended", "base") { style("opacity", "0.5") }
        }
        assertEquals("1px", StyleSheet.getStyle("local-extended").styles["margin"])
        assertEquals("red", StyleSheet.getStyle("global-extended").styles["color"])
        StyleSheet.removeStyle("base")
        assertFalse(StyleSheet.hasStyle("base"))
    }

    @Test
    fun keyframeGeneratorsPreserveNamesValuesAndCustomSteps() {
        val css = listOf(
            KeyframesGenerator.fadeIn("a"), KeyframesGenerator.fadeOut("b"),
            KeyframesGenerator.slideInFromTop("c", 1), KeyframesGenerator.slideInFromBottom("d", 2),
            KeyframesGenerator.slideInFromLeft("e", 3), KeyframesGenerator.slideInFromRight("f", 4),
            KeyframesGenerator.zoomIn("g", 0.2f), KeyframesGenerator.zoomOut("h", 0.3f),
            KeyframesGenerator.pulse("i", 0.8f, 1.2f), KeyframesGenerator.shake("j", 8),
            KeyframesGenerator.float("k", 9), KeyframesGenerator.blinkingCursor("l"),
            KeyframesGenerator.flipX("m"), KeyframesGenerator.flipY("n"),
            KeyframesGenerator.bounce("o"), KeyframesGenerator.typing("p"),
            KeyframesGenerator.colorChange("q", "red", "blue"),
            KeyframesGenerator.backgroundColorChange("r", "black", "white"),
        ).joinToString("\n")
        ('a'..'r').forEach { assertContains(css, "@keyframes $it") }

        val custom = KeyframesGenerator.custom(
            "custom",
            linkedMapOf("0%" to linkedMapOf("opacity" to "0", "transform" to "scale(0)"), "100%" to mapOf("opacity" to "1")),
        )
        assertContains(custom, "transform: scale(0);")
        assertContains(KeyframesGenerator.custom("empty", emptyMap()), "@keyframes empty")
        val eased = KeyframesGenerator.fromEasing("ease", { it * it }, "opacity", 0f, 1f, 4)
        assertContains(eased, "100%")
        val finalOpacity = eased.substringAfter("100% { opacity: ").substringBefore(';').toFloat()
        assertEquals(1f, finalOpacity)
    }

    @Test
    fun spacingHelpersPreserveEveryAxisAndDirection() {
        assertEquals("12px", Spacing.custom(3))
        assertEquals("1px 2px 3px 4px", Spacing.padding("1px", "2px", "3px", "4px"))
        assertEquals("1px 2px", Spacing.padding("1px", "2px"))
        assertEquals("1px 2px 3px 4px", Spacing.margin("1px", "2px", "3px", "4px"))
        assertEquals("1px 2px", Spacing.margin("1px", "2px"))
    }
}
