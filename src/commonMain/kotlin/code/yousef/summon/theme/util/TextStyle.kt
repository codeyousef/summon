package code.yousef.summon.theme.util

/**
 * Represents the style properties for text components.
 */
data class TextStyle(
    val fontFamily: String = "system-ui, sans-serif",
    val fontSize: String = "16px",
    val fontWeight: String = "normal",
    val fontStyle: String? = null,
    val letterSpacing: String = "normal",
    val lineHeight: String? = null,
    val color: String? = null,
    val textDecoration: String? = null
) 