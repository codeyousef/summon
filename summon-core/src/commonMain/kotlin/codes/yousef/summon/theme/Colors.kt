package codes.yousef.summon.theme

import codes.yousef.summon.modifier.*

/**
 * Helper object providing access to theme colors with Material Design naming.
 * Uses the ColorSystem for consistent color management across light and dark themes.
 */
object ColorHelpers {
    /**
     * Get a color from the current theme
     * @param name The semantic color name
     * @param themeMode Optional theme mode override
     * @return The color value as a String
     */
    fun get(name: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): String {
        return Theme.getColor(name, themeMode)
    }

    // Primary colors
    val primary get() = get("primary")
    val primaryVariant get() = get("primaryVariant")
    val onPrimary get() = get("onPrimary")

    // Secondary colors
    val secondary get() = get("secondary")
    val secondaryVariant get() = get("secondaryVariant")
    val onSecondary get() = get("onSecondary")

    // Background colors
    val background get() = get("background")
    val surface get() = get("surface")
    val surfaceVariant get() = get("surfaceVariant")

    // On colors (text and icons)
    val onBackground get() = get("onBackground")
    val onSurface get() = get("onSurface")
    val onSurfaceVariant get() = get("onSurfaceVariant")

    // Status colors
    val success get() = get("success")
    val onSuccess get() = get("onSuccess")
    val info get() = get("info")
    val onInfo get() = get("onInfo")
    val warning get() = get("warning")
    val onWarning get() = get("onWarning")
    val error get() = get("error")
    val onError get() = get("onError")

    // Other colors
    val disabled get() = get("disabled")
    val border get() = get("border")
    val divider get() = get("divider")

    /**
     * Extension function to apply a background color using a theme color
     */
    fun Modifier.backgroundColor(colorName: String): Modifier {
        return this.style("background-color", get(colorName))
    }

    /**
     * Extension function to apply a text color using a theme color
     */
    fun Modifier.textColor(colorName: String): Modifier {
        return this.style("color", get(colorName))
    }

    /**
     * Extension function to apply a border color using a theme color
     */
    fun Modifier.borderColor(colorName: String): Modifier {
        return this.style("border-color", get(colorName))
    }

    /**
     * Get the color palette for the given theme mode
     * @param mode Theme mode
     * @return The appropriate color palette
     */
    fun ColorSystem.ColorPalette.forMode(mode: ColorSystem.ThemeMode): Map<String, String> {
        return when (mode) {
            ColorSystem.ThemeMode.LIGHT -> light
            ColorSystem.ThemeMode.DARK -> dark
            ColorSystem.ThemeMode.SYSTEM -> {
                // Use the ColorSystem's implementation to detect system preference
                if (ColorSystem.isSystemInDarkMode()) dark else light
            }
        }
    }
} 
