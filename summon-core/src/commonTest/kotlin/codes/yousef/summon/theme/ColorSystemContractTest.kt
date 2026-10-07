package codes.yousef.summon.theme

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ColorSystemContractTest {
    @AfterTest
    fun resetTheme() {
        ColorSystem.setSystemDarkMode(false)
        ColorSystem.setThemeMode(ColorSystem.ThemeMode.SYSTEM)
    }

    @Test
    fun palettesResolveExplicitAndSystemModesWithFallback() {
        assertEquals(ColorSystem.default.light, ColorSystem.default.forMode(ColorSystem.ThemeMode.LIGHT))
        assertEquals(ColorSystem.default.dark, ColorSystem.default.forMode(ColorSystem.ThemeMode.DARK))
        ColorSystem.setSystemDarkMode(false)
        assertEquals(ColorSystem.default.light, ColorSystem.default.forMode(ColorSystem.ThemeMode.SYSTEM))
        ColorSystem.setSystemDarkMode(true)
        assertTrue(ColorSystem.isSystemInDarkMode())
        assertEquals(ColorSystem.default.dark, ColorSystem.default.forMode(ColorSystem.ThemeMode.SYSTEM))
        ColorSystem.setThemeMode(ColorSystem.ThemeMode.DARK)
        assertEquals(ColorSystem.ThemeMode.DARK, ColorSystem.getThemeMode())
        assertEquals("#90caf9", ColorSystem.getColor("primary"))
        assertEquals("#000000", ColorSystem.getColor("missing", ColorSystem.ThemeMode.LIGHT))
    }

    @Test
    fun alphaLightnessContrastAndVisionTransformsHonorBoundaries() {
        assertEquals("rgba(255, 0, 128, 0.0)", ColorSystem.withAlpha("#ff0080", -1f))
        assertEquals("rgba(255, 0, 128, 1.0)", ColorSystem.withAlpha("#ff0080", 2f))
        assertEquals("rgba(255, 0, 128, 0.5)", ColorSystem.withAlpha("#ff0080", 0.5f))
        assertEquals("rgba(255, 0, 128, 0.8)", ColorSystem.withAlpha("#ff0080", 0.8f))
        assertEquals("rgba(255, 0, 128, 0.25)", ColorSystem.withAlpha("#ff0080", 0.25f))
        assertEquals("rgba(255, 0, 128, 0.3)", ColorSystem.withAlpha("#ff0080", 0.3f))
        assertEquals("#ffffff", ColorSystem.lighten("#000000", 1f))
        assertEquals("#000000", ColorSystem.darken("#ffffff", 1f))
        assertTrue(ColorSystem.getLuminance("#ffffff") > ColorSystem.getLuminance("#000000"))
        assertTrue(ColorSystem.getContrastRatio("#000000", "#ffffff") > 20f)
        assertTrue(ColorSystem.meetsWcagAA("#000000", "#ffffff"))
        assertTrue(ColorSystem.meetsWcagAA("#000000", "#ffffff", isLargeText = true))
        assertTrue(ColorSystem.meetsWcagAAA("#000000", "#ffffff"))
        assertTrue(ColorSystem.meetsWcagAAA("#000000", "#ffffff", isLargeText = true))
        assertFalse(ColorSystem.meetsWcagAA("#777777", "#888888"))
        assertFalse(ColorSystem.meetsWcagAAA("#777777", "#888888", isLargeText = true))
        listOf(
            ColorSystem.simulateProtanopia("#123456"),
            ColorSystem.simulateDeuteranopia("#123456"),
            ColorSystem.simulateTritanopia("#123456"),
        ).forEach { transformed ->
            assertTrue(Regex("#[0-9a-f]{6}").matches(transformed))
        }
        assertTrue(Regex("#[0-9a-f]{6}").matches(ColorSystem.findContrastColor("#ffffff")))
        assertTrue(Regex("#[0-9a-f]{6}").matches(ColorSystem.findContrastColor("#000000", startColor = "#777777")))
    }
}
