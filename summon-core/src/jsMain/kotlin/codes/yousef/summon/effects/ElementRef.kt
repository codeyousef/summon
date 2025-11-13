package codes.yousef.summon.effects

import org.w3c.dom.Element
import kotlin.random.Random

/**
 * Platform-specific implementation of ElementRef for JavaScript.
 * Provides access to DOM elements and manages their lifecycle.
 */
actual class ElementRef {
    private var element: Element? = null
    private val id: String = "element-${Random.nextInt(100000)}"

    /**
     * Gets the DOM element associated with this reference.
     * @return The DOM element or null if not yet attached
     */
    fun getElement(): Element? = element

    /**
     * Sets the DOM element for this reference.
     * @param el The DOM element to associate with this reference
     */
    fun setElement(el: Element) {
        element = el
        // Set an ID on the element if it doesn't have one
        if (el.id.isEmpty()) {
            el.id = id
        }
    }

    /**
     * Gets the ID of the element.
     * @return The element ID
     */
    fun getId(): String = element?.id ?: id

    /**
     * Clears the element reference.
     */
    fun clear() {
        element = null
    }

    /**
     * Checks if the element is currently attached to the DOM.
     * @return true if the element is attached, false otherwise
     */
    fun isAttached(): Boolean {
        return element != null && kotlinx.browser.document.contains(element)
    }
}