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
     * Sets the text-align property of the element.
     */
    fun Modifier.textAlign(value: String): Modifier =
        style("text-align", value)
        
    /**
     * Sets the text-decoration property of the element.
     */
    fun Modifier.textDecoration(value: String): Modifier =
        style("text-decoration", value)
        
    /**
     * Sets the box-shadow property of the element.
     */
    fun Modifier.boxShadow(value: String): Modifier =
        style("box-shadow", value)
        
    /**
     * Sets the transition property of the element.
     */
    fun Modifier.transition(value: String): Modifier =
        style("transition", value)
        
    /**
     * Sets the transform property of the element.
     */
    fun Modifier.transform(value: String): Modifier =
        style("transform", value)
} 