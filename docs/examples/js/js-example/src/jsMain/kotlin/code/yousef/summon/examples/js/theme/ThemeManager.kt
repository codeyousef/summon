package code.yousef.summon.examples.js.theme

/**
 * Manages theme resources for the application.
 */
object ThemeManager {
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