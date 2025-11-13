package codes.yousef.summon.effects

/**
 * Platform-specific implementation of ElementRef for JVM.
 * On the server side, this is mostly a placeholder as there are no real DOM elements.
 * It can be used to track element IDs for server-side rendering.
 */
actual class ElementRef {
    private var elementId: String? = null

    /**
     * Gets the element ID.
     * @return The element ID or null if not set
     */
    fun getId(): String? = elementId

    /**
     * Sets the element ID.
     * @param id The element ID
     */
    fun setId(id: String) {
        elementId = id
    }

    /**
     * Clears the element reference.
     */
    fun clear() {
        elementId = null
    }
}