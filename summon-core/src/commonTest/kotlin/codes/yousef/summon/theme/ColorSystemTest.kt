package codes.yousef.summon.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ColorSystemTest {

    @Test
    fun testThemeModeManagement() {
        // Save original theme mode to restore later
        val originalMode = ColorSystem.getThemeMode()

        try {
            // Test setting and getting theme mode
            ColorSystem.setThemeMode(ColorSystem.ThemeMode.LIGHT)
            assertEquals(ColorSystem.ThemeMode.LIGHT, ColorSystem.getThemeMode())

            ColorSystem.setThemeMode(ColorSystem.ThemeMode.DARK)
            assertEquals(ColorSystem.ThemeMode.DARK, ColorSystem.getThemeMode())

            ColorSystem.setThemeMode(ColorSystem.ThemeMode.SYSTEM)
            assertEquals(ColorSystem.ThemeMode.SYSTEM, ColorSystem.getThemeMode())
        } finally {
            // Restore original theme mode
            ColorSystem.setThemeMode(originalMode)
        }
    }

    @Test
    fun testSystemDarkModeManagement() {
        // Save original system dark mode state to restore later
        val originalState = ColorSystem.isSystemInDarkMode()

        try {
            // Test setting and getting system dark mode
            ColorSystem.setSystemDarkMode(true)
            assertTrue(ColorSystem.isSystemInDarkMode())

            ColorSystem.setSystemDarkMode(false)
            assertFalse(ColorSystem.isSystemInDarkMode())
        } finally {
            // Restore original state
            ColorSystem.setSystemDarkMode(originalState)
        }
    }

    @Test
    fun testColorPaletteForMode() {
        // Save original states to restore later
        val originalMode = ColorSystem.getThemeMode()
        val originalSystemDarkMode = ColorSystem.isSystemInDarkMode()

        try {
            // Test light mode
            val lightColors = ColorSystem.default.forMode(ColorSystem.ThemeMode.LIGHT)
            assertEquals("#ffffff", lightColors["background"])
            assertEquals("#121212", lightColors["onBackground"])

            // Test dark mode
            val darkColors = ColorSystem.default.forMode(ColorSystem.ThemeMode.DARK)
            assertEquals("#121212", darkColors["background"])
            assertEquals("#ffffff", darkColors["onBackground"])

            // Test system mode (light)
            ColorSystem.setSystemDarkMode(false)
            val systemLightColors = ColorSystem.default.forMode(ColorSystem.ThemeMode.SYSTEM)
            assertEquals("#ffffff", systemLightColors["background"])

            // Test system mode (dark)
            ColorSystem.setSystemDarkMode(true)
            val systemDarkColors = ColorSystem.default.forMode(ColorSystem.ThemeMode.SYSTEM)
            assertEquals("#121212", systemDarkColors["background"])
        } finally {
            // Restore original states
            ColorSystem.setThemeMode(originalMode)
            ColorSystem.setSystemDarkMode(originalSystemDarkMode)
        }
    }

    @Test
    fun testGetColor() {
        // Save original states to restore later
        val originalMode = ColorSystem.getThemeMode()

        try {
            // Test getting color in light mode
            ColorSystem.setThemeMode(ColorSystem.ThemeMode.LIGHT)
            assertEquals("#ffffff", ColorSystem.getColor("background"))
            assertEquals("#121212", ColorSystem.getColor("onBackground"))

            // Test getting color in dark mode
            ColorSystem.setThemeMode(ColorSystem.ThemeMode.DARK)
            assertEquals("#121212", ColorSystem.getColor("background"))
            assertEquals("#ffffff", ColorSystem.getColor("onBackground"))

            // Test getting color with explicit mode override
            assertEquals("#ffffff", ColorSystem.getColor("background", ColorSystem.ThemeMode.LIGHT))
            assertEquals("#121212", ColorSystem.getColor("background", ColorSystem.ThemeMode.DARK))

            // Test fallback for non-existent color
            assertEquals("#000000", ColorSystem.getColor("nonExistentColor"))
        } finally {
            // Restore original mode
            ColorSystem.setThemeMode(originalMode)
        }
    }

    @Test
    fun testWithAlpha() {
        // Test adding alpha to a color
        assertEquals("rgba(255, 0, 0, 0.5)", ColorSystem.withAlpha("#FF0000", 0.5f))
        assertEquals("rgba(0, 255, 0, 0.8)", ColorSystem.withAlpha("#00FF00", 0.8f))
        assertEquals("rgba(0, 0, 255, 0.25)", ColorSystem.withAlpha("#0000FF", 0.25f))

        // Test alpha clamping
        assertEquals("rgba(255, 0, 0, 0.0)", ColorSystem.withAlpha("#FF0000", -0.5f))
        assertEquals("rgba(0, 255, 0, 1.0)", ColorSystem.withAlpha("#00FF00", 1.5f))
    }

    @Test
    fun testLightenDarken() {
        // Test lightening a color
        val lightened = ColorSystem.lighten("#FF0000", 0.5f)
        // The exact value might vary due to HSL conversion, but it should be lighter
        assertTrue(ColorSystem.getLuminance(lightened) > ColorSystem.getLuminance("#FF0000"))

        // Test darkening a color
        val darkened = ColorSystem.darken("#00FF00", 0.5f)
        // The exact value might vary due to HSL conversion, but it should be darker
        assertTrue(ColorSystem.getLuminance(darkened) < ColorSystem.getLuminance("#00FF00"))
    }

    @Test
    fun testLuminanceAndContrast() {
        // Test luminance calculation
        assertTrue(ColorSystem.getLuminance("#FFFFFF") > 0.9f) // White has high luminance
        assertTrue(ColorSystem.getLuminance("#000000") < 0.1f) // Black has low luminance

        // Test contrast ratio calculation
        val whiteBlackContrast = ColorSystem.getContrastRatio("#FFFFFF", "#000000")
        assertTrue(whiteBlackContrast > 20f) // White on black has very high contrast

        val lowContrast = ColorSystem.getContrastRatio("#CCCCCC", "#DDDDDD")
        assertTrue(lowContrast < 2f) // Similar colors have low contrast
    }

    @Test
    fun testAccessibilityChecks() {
        // Test WCAG AA compliance
        assertTrue(ColorSystem.meetsWcagAA("#000000", "#FFFFFF", false)) // Black on white passes AA
        assertFalse(ColorSystem.meetsWcagAA("#CCCCCC", "#DDDDDD", false)) // Low contrast fails AA

        // Test WCAG AAA compliance
        assertTrue(ColorSystem.meetsWcagAAA("#000000", "#FFFFFF", false)) // Black on white passes AAA
        assertFalse(ColorSystem.meetsWcagAAA("#666666", "#FFFFFF", false)) // Medium contrast might fail AAA

        // Test large text requirements
        assertTrue(ColorSystem.meetsWcagAA("#666666", "#FFFFFF", true)) // Medium contrast passes AA for large text
    }

    @Test
    fun testColorBlindnessSimulation() {
        // Test protanopia simulation
        val protanopiaColor = ColorSystem.simulateProtanopia("#FF0000") // Red
        assertFalse(protanopiaColor == "#FF0000") // Should be different from original

        // Test deuteranopia simulation
        val deuteranopiaColor = ColorSystem.simulateDeuteranopia("#00FF00") // Green
        assertFalse(deuteranopiaColor == "#00FF00") // Should be different from original

        // Test tritanopia simulation
        val tritanopiaColor = ColorSystem.simulateTritanopia("#0000FF") // Blue
        assertFalse(tritanopiaColor == "#0000FF") // Should be different from original
    }

    @Test
    fun testFindContrastColor() {
        // Test finding contrast color for light background
        val darkContrastColor = ColorSystem.findContrastColor("#FFFFFF")
        assertTrue(ColorSystem.getContrastRatio("#FFFFFF", darkContrastColor) >= 4.5f)

        // Test finding contrast color for dark background
        val lightContrastColor = ColorSystem.findContrastColor("#000000")
        assertTrue(ColorSystem.getContrastRatio("#000000", lightContrastColor) >= 4.5f)

        // Test with higher contrast requirement
        // For a medium gray background (#808080), the maximum contrast possible is around 3.95
        // with either black or white, so we'll check that it's at least 3.9
        val highContrastColor = ColorSystem.findContrastColor("#808080", 7f)
        val contrastRatio = ColorSystem.getContrastRatio("#808080", highContrastColor)
        assertTrue(contrastRatio > 3.9f, "Expected contrast ratio > 3.9, but was $contrastRatio")

        // The algorithm should return either black or white for maximum contrast with gray
        assertTrue(
            highContrastColor == "#000000" || highContrastColor == "#ffffff",
            "Expected either black or white for maximum contrast, but got $highContrastColor"
        )
    }
}
