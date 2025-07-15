package code.yousef.summon.modifier

/**
 * Provides styling-related extension functions for the Modifier class,
 * organized in a way that allows for explicit imports
 * to resolve ambiguity issues.
 */
object StylingModifierExtras {
    /**
     * Sets the font-style property of the element.
     */
    fun Modifier.fontStyle(value: String): Modifier =
        style("font-style", value)

    /**
     * Sets the text-decoration property of the element.
     */
    fun Modifier.textDecoration(value: String): Modifier =
        style("text-decoration", value)

    /**
     * Sets the transform property of the element.
     */
    fun Modifier.transform(value: String): Modifier =
        style("transform", value)
} 
