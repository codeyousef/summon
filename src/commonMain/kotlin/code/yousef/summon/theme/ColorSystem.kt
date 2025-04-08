package code.yousef.summon.theme

import code.yousef.summon.modifier.Modifier


/**
 * ColorSystem provides predefined color schemes with support for light and dark modes.
 * It defines a consistent color palette that can be used across the application
 * to maintain visual consistency and support theming.
 */
object ColorSystem {
    /**
     * Represents a light/dark mode theme state
     */
    enum class ThemeMode {
        LIGHT,
        DARK,
        SYSTEM
    }

    /**
     * A color palette with light and dark mode variants
     */
    data class ColorPalette(
        val light: Map<String, String>,
        val dark: Map<String, String>
    ) {
        /**
         * Get the appropriate color map based on the theme mode
         * @param themeMode The current theme mode
         * @return The color map for the selected theme mode
         */
        fun forMode(themeMode: ThemeMode): Map<String, String> = when (themeMode) {
            ThemeMode.LIGHT -> light
            ThemeMode.DARK -> dark
            ThemeMode.SYSTEM -> light // Default to light, system detection handled separately
        }
    }

    /**
     * The current theme mode, defaulting to system preference
     */
    private var currentThemeMode = ThemeMode.SYSTEM

    /**
     * Set the theme mode for the application
     * @param mode The theme mode to set
     */
    fun setThemeMode(mode: ThemeMode) {
        currentThemeMode = mode
    }

    /**
     * Get the current theme mode
     * @return The current theme mode
     */
    fun getThemeMode(): ThemeMode = currentThemeMode

    /**
     * Default color palette with semantic color names
     */
    val default = ColorPalette(
        light = mapOf(
            // Backgrounds
            "background" to "#ffffff",
            "surface" to "#f5f5f5",
            "surfaceVariant" to "#e0e0e0",

            // Text
            "onBackground" to "#121212",
            "onSurface" to "#212121",
            "onSurfaceVariant" to "#424242",
            "disabled" to "#9e9e9e",

            // Brand colors
            "primary" to "#1976d2",
            "primaryVariant" to "#1565c0",
            "onPrimary" to "#ffffff",
            "secondary" to "#9c27b0",
            "secondaryVariant" to "#7b1fa2",
            "onSecondary" to "#ffffff",

            // Status colors
            "success" to "#4caf50",
            "onSuccess" to "#ffffff",
            "info" to "#03a9f4",
            "onInfo" to "#ffffff",
            "warning" to "#ff9800",
            "onWarning" to "#ffffff",
            "error" to "#f44336",
            "onError" to "#ffffff",

            // Borders and dividers
            "border" to "#e0e0e0",
            "divider" to "#e0e0e0"
        ),
        dark = mapOf(
            // Backgrounds
            "background" to "#121212",
            "surface" to "#1e1e1e",
            "surfaceVariant" to "#2e2e2e",

            // Text
            "onBackground" to "#ffffff",
            "onSurface" to "#e0e0e0",
            "onSurfaceVariant" to "#b0b0b0",
            "disabled" to "#6e6e6e",

            // Brand colors
            "primary" to "#90caf9",
            "primaryVariant" to "#64b5f6",
            "onPrimary" to "#121212",
            "secondary" to "#ce93d8",
            "secondaryVariant" to "#ba68c8",
            "onSecondary" to "#121212",

            // Status colors
            "success" to "#81c784",
            "onSuccess" to "#121212",
            "info" to "#4fc3f7",
            "onInfo" to "#121212",
            "warning" to "#ffb74d",
            "onWarning" to "#121212",
            "error" to "#e57373",
            "onError" to "#121212",

            // Borders and dividers
            "border" to "#424242",
            "divider" to "#424242"
        )
    )

    /**
     * Material blue theme
     */
    val blue = ColorPalette(
        light = mapOf(
            "primary" to "#2196f3",
            "primaryVariant" to "#1976d2",
            "onPrimary" to "#ffffff",
            "secondary" to "#ff9800",
            "secondaryVariant" to "#f57c00",
            "onSecondary" to "#ffffff"
        ),
        dark = mapOf(
            "primary" to "#90caf9",
            "primaryVariant" to "#64b5f6",
            "onPrimary" to "#121212",
            "secondary" to "#ffb74d",
            "secondaryVariant" to "#ffa726",
            "onSecondary" to "#121212"
        )
    )

    /**
     * Material green theme
     */
    val green = ColorPalette(
        light = mapOf(
            "primary" to "#4caf50",
            "primaryVariant" to "#388e3c",
            "onPrimary" to "#ffffff",
            "secondary" to "#ff5722",
            "secondaryVariant" to "#e64a19",
            "onSecondary" to "#ffffff"
        ),
        dark = mapOf(
            "primary" to "#81c784",
            "primaryVariant" to "#66bb6a",
            "onPrimary" to "#121212",
            "secondary" to "#ff8a65",
            "secondaryVariant" to "#ff7043",
            "onSecondary" to "#121212"
        )
    )

    /**
     * Material purple theme
     */
    val purple = ColorPalette(
        light = mapOf(
            "primary" to "#9c27b0",
            "primaryVariant" to "#7b1fa2",
            "onPrimary" to "#ffffff",
            "secondary" to "#00bcd4",
            "secondaryVariant" to "#0097a7",
            "onSecondary" to "#ffffff"
        ),
        dark = mapOf(
            "primary" to "#ce93d8",
            "primaryVariant" to "#ba68c8",
            "onPrimary" to "#121212",
            "secondary" to "#4dd0e1",
            "secondaryVariant" to "#26c6da",
            "onSecondary" to "#121212"
        )
    )

    /**
     * Get a color from the default palette
     * @param name The semantic color name
     * @param mode The theme mode to use
     * @return The color value, or a fallback if the color is not defined
     */
    fun getColor(name: String, mode: ThemeMode = currentThemeMode): String {
        val colorMap = default.forMode(mode)
        return colorMap[name] ?: "#000000" // Fallback to black
    }

    /**
     * Utility function to create a color with alpha transparency
     * @param hexColor The hex color code (e.g. #FF0000)
     * @param alpha The alpha value (0.0 to 1.0)
     * @return The hex color with alpha transparency
     */
    fun withAlpha(hexColor: String, alpha: Float): String {
        // Make sure alpha is in valid range
        val clampedAlpha = alpha.coerceIn(0f, 1f)

        // Convert to RGB values
        val r = hexColor.substring(1, 3).toInt(16)
        val g = hexColor.substring(3, 5).toInt(16)
        val b = hexColor.substring(5, 7).toInt(16)

        // Return as rgba
        return "rgba($r, $g, $b, $clampedAlpha)"
    }

    /**
     * Utility function to create a lighter version of a color
     * @param hexColor The hex color code
     * @param percent The percent to lighten (0.0 to 1.0)
     * @return The lightened color
     */
    fun lighten(hexColor: String, percent: Float): String {
        // This is a simplified implementation
        // In a real implementation, you would convert to HSL and adjust lightness
        return withAlpha(hexColor, 1f - percent.coerceIn(0f, 1f))
    }

    /**
     * Utility function to create a darker version of a color
     * @param hexColor The hex color code
     * @param percent The percent to darken (0.0 to 1.0)
     * @return The darkened color
     */
    fun darken(hexColor: String, percent: Float): String {
        // This is a simplified implementation
        // In a real implementation, you would convert to HSL and adjust lightness
        return withAlpha(hexColor, 1f - percent.coerceIn(0f, 1f))
    }
}

/**
 * Extension functions for Modifier to use ColorSystem colors
 */

/**
 * Sets the background color using a color from the ColorSystem
 * @param colorName The semantic color name from ColorSystem
 * @param mode Optional theme mode override
 * @return A new Modifier with the background color set
 */
fun Modifier.backgroundColor(colorName: String, mode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): Modifier =
    this.background(ColorSystem.getColor(colorName, mode))

/**
 * Sets the text color using a color from the ColorSystem
 * @param colorName The semantic color name from ColorSystem
 * @param mode Optional theme mode override
 * @return A new Modifier with the text color set
 */
fun Modifier.textColor(colorName: String, mode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): Modifier =
    this.color(ColorSystem.getColor(colorName, mode))

/**
 * Sets the border color using a color from the ColorSystem
 * @param width Border width
 * @param style Border style
 * @param colorName The semantic color name from ColorSystem
 * @param mode Optional theme mode override
 * @return A new Modifier with the border set
 */
fun Modifier.themeBorder(
    width: String,
    style: String = "solid",
    colorName: String = "border",
    mode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()
): Modifier = this.border(width, style, ColorSystem.getColor(colorName, mode)) 
