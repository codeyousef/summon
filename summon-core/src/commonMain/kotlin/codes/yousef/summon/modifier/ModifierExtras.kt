package codes.yousef.summon.modifier

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
        if (this is ModifierImpl) copy(eventHandlers = this.eventHandlers + ("click" to handler))
        else ModifierImpl(eventHandlers = mapOf("click" to handler))

    /**
     * Adds a single attribute to the Modifier by creating a new Modifier instance.
     * Leverages the existing `attribute` method on the Modifier class.
     * @return A new Modifier instance with the added attribute.
     */
    fun Modifier.withAttribute(key: String, value: String): Modifier {
        return this.attribute(key, value) // Use the existing 'attribute' method from Modifier.kt
    }

    /**
     * Adds multiple attributes to the Modifier by creating a new Modifier instance.
     * @return A new Modifier instance with the added attributes.
     */
    fun Modifier.withAttributes(attrs: Map<String, String>): Modifier {
        return if (this is ModifierImpl) copy(attributes = this.attributes + attrs)
        else ModifierImpl(attributes = attrs)
    }

    /**
     * Sets the pointer-events property of the element.
     * @param value The CSS pointer-events value (e.g., "none", "auto", etc.)
     * @return A new Modifier with the added style
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.pointerEvents(value: String): Modifier =
        style("pointer-events", value)

    /**
     * Sets the pointer-events property of the element using type-safe values.
     * @param value The PointerEvents enum value
     * @return A new Modifier with the added style
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.pointerEvents(value: PointerEvents): Modifier =
        style("pointer-events", value.toString())

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
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.attribute(name: String, value: String): Modifier =
        if (this is ModifierImpl) copy(attributes = attributes + (name to value))
        else ModifierImpl(attributes = mapOf(name to value))
} 
