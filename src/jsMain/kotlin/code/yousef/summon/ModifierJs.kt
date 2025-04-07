package code.yousef.summon

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import modifier.Modifier
import kotlinx.browser.document
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.id
import kotlinx.html.style
import org.w3c.dom.HTMLElement

/**
 * JS-specific extension functions for Modifier.
 */

/**
 * Applies all styles from the modifier to the HTML element, including setting up hover effects.
 * For JS, we'll register event handlers for hover.
 */
fun Modifier.applyStyles(element: CommonAttributeGroupFacade): String? {
    // Apply regular styles
    element.style = this.toStyleString()

    // For JS, we'll use data attribute to handle hover effects later
    val hoverStyles = this.styles["__hover"]
    if (hoverStyles != null) {
        val id = "summon-element-${hashCode()}-${element.hashCode()}"
        element.id = id
        element.attributes["data-summon-hover"] = hoverStyles
        return id
    }

    return null
}

/**
 * Sets up hover effects for elements that have the data-summon-hover attribute.
 * This should be called after the DOM has been updated.
 */
fun setupHoverEffects() {
    val elements = document.querySelectorAll("[data-summon-hover]")
    for (i in 0 until elements.length) {
        val element = elements.item(i) as? HTMLElement ?: continue
        val hoverStyles = element.getAttribute("data-summon-hover") ?: continue

        // Parse the hover styles
        val styles = hoverStyles.split(";").associate {
            val parts = it.split(":")
            if (parts.size == 2) parts[0].trim() to parts[1].trim() else "" to ""
        }.filter { it.key.isNotEmpty() }

        // Store the original styles
        val originalStyles = styles.keys.associate { styleName ->
            styleName to element.style.getPropertyValue(styleName)
        }

        // Set up mouse events
        element.onmouseenter = {
            styles.forEach { (name, value) ->
                element.style.setProperty(name, value)
            }
        }

        element.onmouseleave = {
            originalStyles.forEach { (name, value) ->
                element.style.setProperty(name, value)
            }
        }
    }
} 
