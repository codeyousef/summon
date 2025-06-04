package code.yousef.summon.modifier

/**
 * Provides extension functions for the Modifier class,
 * organized in a way that allows for explicit imports 
 * to resolve ambiguity issues.
 */
object ModifierExtras {
    /**
     * Sets an onClick handler for the element.
     * @param handler The handler function
     * @return A new Modifier with the added event handler
     */
    fun Modifier.onClick(handler: () -> Unit): Modifier =
        style("onclick", handler.toString())
        
    /**
     * Sets the pointer-events property of the element.
     * @param value The CSS pointer-events value (e.g., "none", "auto", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.pointerEvents(value: String): Modifier =
        style("pointer-events", value)
        
    /**
     * Gets an attribute value or null if not present
     * @param name The attribute name
     * @return The attribute value or null if not found
     */
    fun Modifier.getAttribute(name: String): String? = 
        attributes[name]
        
    /**
     * Creates a copy of the Modifier with an added HTML attribute.
     * 
     * @param name The attribute name
     * @param value The attribute value
     * @return A new Modifier with the added attribute
     */
    fun Modifier.attribute(name: String, value: String): Modifier =
        Modifier(styles, attributes + (name to value))
} 