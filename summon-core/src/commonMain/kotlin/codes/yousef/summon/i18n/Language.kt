package codes.yousef.summon.i18n

/**
 * Represents a supported language in the application

 * @property code The code value.
 * @property name Human-readable name.
 * @property direction The direction value.
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
    /** The ltr layout direction option. */
    LTR,
    /** The rtl layout direction option. */
    RTL
}