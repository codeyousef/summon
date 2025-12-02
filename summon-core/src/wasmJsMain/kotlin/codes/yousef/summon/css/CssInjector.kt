package codes.yousef.summon.css

import kotlinx.browser.document
import org.w3c.dom.HTMLStyleElement

/**
 * WebAssembly/Browser implementation of CssInjector.
 *
 * Manages `<style>` elements in the document `<head>` for runtime CSS injection.
 */
actual object CssInjector {
    /**
     * Injects or updates a CSS style block with the given ID.
     */
    actual fun injectUserCss(id: String, css: String): Boolean {
        return try {
            val sanitizedCss = CssSanitizer.sanitize(css)
            val styleId = "summon-user-css-$id"
            
            var styleElement = document.getElementById(styleId) as? HTMLStyleElement
            
            if (styleElement == null) {
                // Create new style element
                styleElement = document.createElement("style") as HTMLStyleElement
                styleElement.id = styleId
                styleElement.setAttribute("data-summon-css", id)
                document.head?.appendChild(styleElement)
            }
            
            // Update the content
            styleElement.textContent = sanitizedCss
            true
        } catch (e: Exception) {
            println("Failed to inject CSS for id '$id': ${e.message}")
            false
        }
    }
    
    /**
     * Removes a previously injected style block.
     */
    actual fun removeUserCss(id: String): Boolean {
        return try {
            val styleId = "summon-user-css-$id"
            val styleElement = document.getElementById(styleId)
            
            if (styleElement != null) {
                styleElement.parentNode?.removeChild(styleElement)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Failed to remove CSS for id '$id': ${e.message}")
            false
        }
    }
    
    /**
     * Gets the current content of a style block.
     */
    actual fun getUserCss(id: String): String? {
        return try {
            val styleId = "summon-user-css-$id"
            val styleElement = document.getElementById(styleId) as? HTMLStyleElement
            styleElement?.textContent
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Checks if a style block with the given ID exists.
     */
    actual fun hasUserCss(id: String): Boolean {
        val styleId = "summon-user-css-$id"
        return document.getElementById(styleId) != null
    }
}
