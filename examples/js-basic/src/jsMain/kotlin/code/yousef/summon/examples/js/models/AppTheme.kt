package code.yousef.summon.examples.js.models

/**
 * Available themes for the application
 */
enum class AppTheme(val displayName: String) {
    LIGHT("Light Mode"),
    DARK("Dark Mode");
    
    companion object {
        fun toggle(current: AppTheme): AppTheme {
            return when (current) {
                LIGHT -> DARK
                DARK -> LIGHT
            }
        }
    }
}