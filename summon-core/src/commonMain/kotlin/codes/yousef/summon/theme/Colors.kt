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
    /** The property declaration value. */
    val primary get() = get("primary")
    /** The property declaration value. */
    val primaryVariant get() = get("primaryVariant")
    /** The property declaration value. */
    val onPrimary get() = get("onPrimary")

    // Secondary colors
    /** The property declaration value. */
    val secondary get() = get("secondary")
    /** The property declaration value. */
    val secondaryVariant get() = get("secondaryVariant")
    /** The property declaration value. */
    val onSecondary get() = get("onSecondary")

    // Background colors
    /** The property declaration value. */
    val background get() = get("background")
    /** The property declaration value. */
    val surface get() = get("surface")
    /** The property declaration value. */
    val surfaceVariant get() = get("surfaceVariant")

    // On colors (text and icons)
    /** The property declaration value. */
    val onBackground get() = get("onBackground")
    /** The property declaration value. */
    val onSurface get() = get("onSurface")
    /** The property declaration value. */
    val onSurfaceVariant get() = get("onSurfaceVariant")

    // Status colors
    /** The property declaration value. */
    val success get() = get("success")
    /** The property declaration value. */
    val onSuccess get() = get("onSuccess")
    /** The property declaration value. */
    val info get() = get("info")
    /** The property declaration value. */
    val onInfo get() = get("onInfo")
    /** The property declaration value. */
    val warning get() = get("warning")
    /** The property declaration value. */
    val onWarning get() = get("onWarning")
    /** The property declaration value. */
    val error get() = get("error")
    /** The property declaration value. */
    val onError get() = get("onError")

    // Other colors
    /** The property declaration value. */
    val disabled get() = get("disabled")
    /** The property declaration value. */
    val border get() = get("border")
    /** The property declaration value. */
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
