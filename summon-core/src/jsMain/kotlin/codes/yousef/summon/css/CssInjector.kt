package codes.yousef.summon.css

import codes.yousef.summon.components.foundation.TrustedCss

import kotlinx.browser.document
import org.w3c.dom.HTMLStyleElement

/**
 * JavaScript/Browser implementation of CssInjector.
 *
 * Manages `<style>` elements in the document `<head>` for runtime CSS injection.
 */
actual object CssInjector {
    /**
     * Injects or updates a CSS style block with the given ID.
     */
    actual fun injectTrustedCss(id: String, css: TrustedCss): Boolean {
        if (!isValidCssBlockId(id)) return false
        return try {
            val styleId = "summon-trusted-css-$id"
            
            var styleElement = document.getElementById(styleId) as? HTMLStyleElement
            
            if (styleElement == null) {
                // Create new style element
                styleElement = document.createElement("style") as HTMLStyleElement
                styleElement.id = styleId
                styleElement.setAttribute("data-summon-css", id)
                document.head?.appendChild(styleElement)
            }
            
            // Update the content
            styleElement.textContent = css.value
            true
        } catch (e: Exception) {
            console.error("Failed to inject CSS for id '$id': ${e.message}")
            false
        }
    }
    
    /**
     * Removes a previously injected style block.
     */
    actual fun removeTrustedCss(id: String): Boolean {
        if (!isValidCssBlockId(id)) return false
        return try {
            val styleId = "summon-trusted-css-$id"
            val styleElement = document.getElementById(styleId)
            
            if (styleElement != null) {
                styleElement.parentNode?.removeChild(styleElement)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            console.error("Failed to remove CSS for id '$id': ${e.message}")
            false
        }
    }
    
    /**
     * Gets the current content of a style block.
     */
    actual fun getTrustedCss(id: String): String? {
        if (!isValidCssBlockId(id)) return null
        return try {
            val styleId = "summon-trusted-css-$id"
            val styleElement = document.getElementById(styleId) as? HTMLStyleElement
            styleElement?.textContent
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Checks if a style block with the given ID exists.
     */
    actual fun hasTrustedCss(id: String): Boolean {
        if (!isValidCssBlockId(id)) return false
        val styleId = "summon-trusted-css-$id"
        return document.getElementById(styleId) != null
    }
}
