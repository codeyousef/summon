package codes.yousef.summon.theme

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

    /**
     * Whether dark mode is enabled.
     */
    var isDarkMode: Boolean = false
        private set

    /**
     * Initializes the theme manager with the given dark mode setting.
     */
    fun initialize(isDarkMode: Boolean) {
        this.isDarkMode = isDarkMode
    }

    /**
     * Toggles between light and dark mode.
     */
    fun toggleTheme() {
        isDarkMode = !isDarkMode
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
