package codes.yousef.summon.theme

import codes.yousef.summon.core.style.Color
import codes.yousef.summon.modifier.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ThemeTest {

    @Test
    fun testTextStyleCreation() {
        // Test creating a TextStyle with the create method
        val style = Theme.TextStyle.create(
            fontFamily = "Arial",
            fontSize = 16,
            fontSizeUnit = "px",
            fontWeight = FontWeight.Bold,
            color = Color.RED,
            textDecoration = "underline"
        )

        // Verify properties are set correctly
        assertEquals("Arial", style.fontFamily)
        assertEquals("16px", style.fontSize)
        assertEquals(FontWeight.Bold.value, style.fontWeight)
        assertEquals("underline", style.textDecoration)
        assertEquals(16, style.fontSizeNumber)
        assertEquals("px", style.fontSizeUnit)
        assertEquals(FontWeight.Bold, style.fontWeightEnum)
        assertEquals(Color.RED, style.colorValue)
        assertEquals(Color.RED.toHexString(), style.color)
    }

    @Test
    fun testThemeConfigCreation() {
        // Test creating a ThemeConfig
        val config = Theme.ThemeConfig(
            colorPalette = ColorSystem.default,
            customValues = mapOf("customKey" to "customValue")
        )

        // Verify properties
        assertEquals(ColorSystem.default, config.colorPalette)
        assertEquals("customValue", config.customValues["customKey"])

        // Verify default properties
        assertNotNull(config.typography)
        assertNotNull(config.spacing)
        assertNotNull(config.borderRadius)
        assertNotNull(config.elevation)
        assertNotNull(config.typographyTheme)
        assertNotNull(config.spacingTheme)
        assertNotNull(config.borderRadiusTheme)
        assertNotNull(config.elevationTheme)
    }

    @Test
    fun testThemeSetAndGet() {
        // Save original theme to restore later
        val originalTheme = Theme.getTheme()

        try {
            // Create a custom theme
            val customTheme = Theme.ThemeConfig(
                colorPalette = ColorSystem.blue,
                customValues = mapOf("testKey" to "testValue")
            )

            // Set the theme
            Theme.setTheme(customTheme)

            // Verify the theme was set
            val retrievedTheme = Theme.getTheme()
            assertEquals(customTheme, retrievedTheme)
            assertEquals(ColorSystem.blue, retrievedTheme.colorPalette)
            assertEquals("testValue", retrievedTheme.customValues["testKey"])
        } finally {
            // Restore original theme
            Theme.setTheme(originalTheme)
        }
    }

    @Test
    fun testThemeGetters() {
        // Save original theme to restore later
        val originalTheme = Theme.getTheme()

        try {
            // Create a custom theme with specific values
            val customTypography = mapOf(
                "customStyle" to Theme.TextStyle.create(
                    fontSize = 20,
                    fontWeight = FontWeight.Bold
                )
            )

            val customSpacing = mapOf(
                "customSpacing" to "16px"
            )

            val customBorderRadius = mapOf(
                "customRadius" to "8px"
            )

            val customElevation = mapOf(
                "customElevation" to "0 4px 8px rgba(0,0,0,0.1)"
            )

            val customTheme = Theme.ThemeConfig(
                typography = customTypography,
                spacing = customSpacing,
                borderRadius = customBorderRadius,
                elevation = customElevation,
                customValues = mapOf("customKey" to "customValue")
            )

            // Set the theme
            Theme.setTheme(customTheme)

            // Test getters
            val textStyle = Theme.getTextStyle("customStyle")
            assertEquals("20rem", textStyle.fontSize)
            assertEquals(FontWeight.Bold.value, textStyle.fontWeight)

            assertEquals("16px", Theme.getSpacing("customSpacing"))
            assertEquals("8px", Theme.getBorderRadius("customRadius"))
            assertEquals("0 4px 8px rgba(0,0,0,0.1)", Theme.getElevation("customElevation"))
            assertEquals("customValue", Theme.getCustomValue("customKey", "defaultValue"))
            assertEquals("defaultValue", Theme.getCustomValue("nonExistentKey", "defaultValue"))

            // Test typed theme getters
            assertNotNull(Theme.getTypographyTheme())
            assertNotNull(Theme.getSpacingTheme())
            assertNotNull(Theme.getBorderRadiusTheme())
            assertNotNull(Theme.getElevationTheme())
        } finally {
            // Restore original theme
            Theme.setTheme(originalTheme)
        }
    }

    @Test
    fun testCreateTheme() {
        // Test creating a theme by extending an existing theme
        val baseTheme = Theme.Themes.light

        val newTheme = Theme.createTheme(baseTheme) {
            copy(
                customValues = mapOf("newKey" to "newValue")
            )
        }

        // Verify the new theme has the base theme's properties plus modifications
        assertEquals(baseTheme.colorPalette, newTheme.colorPalette)
        assertEquals("newValue", newTheme.customValues["newKey"])
    }

    // We'll skip testing the modifier extensions directly since they require
    // a more complex setup with the actual Modifier implementation.
    // Instead, we'll focus on testing the core Theme functionality.

    @Test
    fun testThemeColorFunctions() {
        // Save original theme to restore later
        val originalTheme = Theme.getTheme()

        try {
            // Create a custom theme with specific values
            val customTheme = Theme.ThemeConfig(
                colorPalette = ColorSystem.ColorPalette(
                    light = mapOf("testColor" to "#FF0000"),
                    dark = mapOf("testColor" to "#00FF00")
                )
            )

            // Set the theme
            Theme.setTheme(customTheme)

            // Test color getter
            assertEquals("#FF0000", Theme.getColor("testColor"))

            // Test with specific theme mode
            assertEquals("#00FF00", Theme.getColor("testColor", ColorSystem.ThemeMode.DARK))
        } finally {
            // Restore original theme
            Theme.setTheme(originalTheme)
        }
    }

    @Test
    fun testThemeSpacingFunctions() {
        // Save original theme to restore later
        val originalTheme = Theme.getTheme()

        try {
            // Create a custom theme with specific values
            val customTheme = Theme.ThemeConfig(
                spacing = mapOf("testSpacing" to "10px")
            )

            // Set the theme
            Theme.setTheme(customTheme)

            // Test spacing getter
            assertEquals("10px", Theme.getSpacing("testSpacing"))
        } finally {
            // Restore original theme
            Theme.setTheme(originalTheme)
        }
    }

    @Test
    fun testThemeBorderRadiusFunctions() {
        // Save original theme to restore later
        val originalTheme = Theme.getTheme()

        try {
            // Create a custom theme with specific values
            val customTheme = Theme.ThemeConfig(
                borderRadius = mapOf("testRadius" to "5px")
            )

            // Set the theme
            Theme.setTheme(customTheme)

            // Test border radius getter
            assertEquals("5px", Theme.getBorderRadius("testRadius"))
        } finally {
            // Restore original theme
            Theme.setTheme(originalTheme)
        }
    }

    @Test
    fun testThemeElevationFunctions() {
        // Save original theme to restore later
        val originalTheme = Theme.getTheme()

        try {
            // Create a custom theme with specific values
            val customTheme = Theme.ThemeConfig(
                elevation = mapOf("testElevation" to "0 2px 4px rgba(0,0,0,0.2)")
            )

            // Set the theme
            Theme.setTheme(customTheme)

            // Test elevation getter
            assertEquals("0 2px 4px rgba(0,0,0,0.2)", Theme.getElevation("testElevation"))
        } finally {
            // Restore original theme
            Theme.setTheme(originalTheme)
        }
    }

    @Test
    fun testPredefinedThemes() {
        // Test that predefined themes exist and have expected properties
        val lightTheme = Theme.Themes.light
        assertEquals(ColorSystem.default, lightTheme.colorPalette)

        val darkTheme = Theme.Themes.dark
        assertEquals(ColorSystem.default, darkTheme.colorPalette)

        val blueTheme = Theme.Themes.blue
        assertEquals(ColorSystem.blue, blueTheme.colorPalette)

        val greenTheme = Theme.Themes.green
        assertEquals(ColorSystem.green, greenTheme.colorPalette)

        val purpleTheme = Theme.Themes.purple
        assertEquals(ColorSystem.purple, purpleTheme.colorPalette)
    }
}
