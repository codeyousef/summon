package code.yousef.summon.i18n

/**
 * Represents a supported language in the application
 */
data class Language(
    val code: String,
    val name: String,
    val direction: LayoutDirection
)

/**
 * Layout direction for text and UI elements
 */
enum class LayoutDirection {
    LTR, RTL
} 