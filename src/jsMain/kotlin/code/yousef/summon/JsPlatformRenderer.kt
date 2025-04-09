package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement
import kotlin.js.Date

actual class JsPlatformRenderer actual constructor() : PlatformRenderer {
    actual override fun <T> renderComposable(composable: @Composable () -> Unit, consumer: T) {
        // Basic implementation - just a stub for now
    }

    actual override fun renderText(value: String, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderButton(onClick: () -> Unit, enabled: Boolean, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderImage(src: String, alt: String, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderIcon(name: String, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderRow(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderColumn(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderSpacer(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderBox(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderCard(modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier) {
        // Basic implementation - no actual rendering yet
    }

    actual override fun renderLink(href: String, modifier: Modifier) {
        // Create an anchor element
        val element = document.createElement("a") as HTMLAnchorElement
        
        // Set the href attribute
        element.href = href
        
        // Apply styles and attributes from the modifier
        applyModifierToElement(element, modifier)
        
        // Generate a unique ID for potential event handlers
        val linkId = "link-${Date.now().toInt()}-${(js("Math.random()").toString()).substring(2, 8)}"
        element.id = linkId
        
        // Extract onClick handler if present in the modifier
        val onClick = modifier.extractOnClick()
        
        // Set up click handler if provided
        if (onClick != null) {
            setupJsClickHandler(linkId, LinkJsExtension(onClick, href))
        }
        
        // TODO: Add the element to the current composition context/parent node
    }
    
    /**
     * Applies a modifier to an HTML element, setting up styles and attributes
     */
    private fun applyModifierToElement(element: HTMLElement, modifier: Modifier) {
        // Apply style properties
        for (key in modifier.styles.keys) {
            if (key != "__hover") {  // Special case for hover styling
                val value = modifier.styles[key]
                if (value != null) {
                    element.style.setProperty(key, value)
                }
            }
        }
        
        // In the actual implementation, we would likely have a way to get attributes from a modifier
        // For now, just handle style attributes
        
        // Handle hover styles if present
        if (modifier.styles.containsKey("__hover")) {
            // Store hover styles in a data attribute for later processing
            element.setAttribute("data-summon-hover", modifier.styles["__hover"] ?: "")
        }
    }
} 