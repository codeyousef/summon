package code.yousef.summon.theme

import code.yousef.summon.theme.Typography

/**
 * Manages theme resources for the application.
 */
object ThemeManager {
    private var currentTypography: Typography = Typography()
    
    /**
     * Returns the current typography settings.
     */
    fun getTypography(): Typography = currentTypography
    
    /**
     * Sets the typography settings.
     */
    fun setTypography(typography: Typography) {
        currentTypography = typography
    }
}

/**
 * Interface representing a theme configuration with various styling components.
 */
interface ThemeConfiguration {
    val typography: Typography
    // Other theme components like colors, shapes, etc. can be added here
}

/**
 * Default implementation of ThemeConfiguration.
 */
class DefaultThemeConfiguration : ThemeConfiguration {
    override val typography: Typography = Typography()
} 