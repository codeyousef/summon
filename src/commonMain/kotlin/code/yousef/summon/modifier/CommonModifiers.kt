package code.yousef.summon.modifier

/**
 * Common modifiers that are frequently used across components.
 * This helps resolve ambiguity issues by providing a consistent 
 * implementation that can be specifically imported.
 */
object CommonModifiers {
    /**
     * Sets the background color of the element.
     */
    fun Modifier.backgroundColor(value: String): Modifier =
        style("background-color", value)
} 