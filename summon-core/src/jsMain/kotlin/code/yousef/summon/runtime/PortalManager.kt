package codes.yousef.summon.runtime

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

/**
 * Manages portal (teleport) rendering by moving DOM elements to different containers.
 */
object PortalManager {
    private val portalContainers = mutableMapOf<String, HTMLElement>()
    private val portaledElements = mutableMapOf<Element, PortalInfo>()

    data class PortalInfo(
        val originalParent: Element,
        val targetSelector: String,
        val targetContainer: HTMLElement
    )

    /**
     * Moves an element to a portal target
     */
    fun portal(element: Element, targetSelector: String) {
        // If already portaled, remove from old location
        unportal(element)

        // Find or create target container
        val targetContainer = getOrCreatePortalContainer(targetSelector)

        // Store original parent
        val originalParent = element.parentElement
        if (originalParent != null) {
            portaledElements[element] = PortalInfo(originalParent, targetSelector, targetContainer)

            // Move element to portal container
            targetContainer.appendChild(element)
        }
    }

    /**
     * Removes an element from portal and returns it to original parent
     */
    fun unportal(element: Element) {
        val portalInfo = portaledElements.remove(element) ?: return

        // Return element to original parent
        portalInfo.targetContainer.removeChild(element)
        portalInfo.originalParent.appendChild(element)
    }

    /**
     * Gets or creates a portal container for the given selector
     */
    private fun getOrCreatePortalContainer(selector: String): HTMLElement {
        return portalContainers.getOrPut(selector) {
            val container = if (selector == "body") {
                document.body as HTMLElement
            } else {
                document.querySelector(selector) as? HTMLElement
                    ?: createPortalContainer(selector)
            }
            container
        }
    }

    /**
     * Creates a new portal container if one doesn't exist
     */
    private fun createPortalContainer(selector: String): HTMLElement {
        // For ID selectors like #modal-root, create a div with that ID
        val container = document.createElement("div") as HTMLElement

        if (selector.startsWith("#")) {
            val id = selector.substring(1)
            container.id = id
            document.body?.appendChild(container)
        } else if (selector.startsWith(".")) {
            val className = selector.substring(1)
            container.className = className
            document.body?.appendChild(container)
        } else {
            // For other selectors, try to query or append to body
            container.setAttribute("data-portal-container", selector)
            document.body?.appendChild(container)
        }

        return container
    }

    /**
     * Checks if an element is currently portaled
     */
    fun isPortaled(element: Element): Boolean {
        return portaledElements.containsKey(element)
    }

    /**
     * Gets the target selector for a portaled element
     */
    fun getPortalTarget(element: Element): String? {
        return portaledElements[element]?.targetSelector
    }
}

