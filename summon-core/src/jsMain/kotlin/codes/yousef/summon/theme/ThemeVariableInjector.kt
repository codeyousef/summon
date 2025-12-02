package codes.yousef.summon.theme

import kotlinx.browser.document

/**
 * JavaScript/Browser implementation of ThemeVariableInjector.
 *
 * Injects CSS custom properties directly into document.documentElement.style
 * for instant theme switching without tree re-render.
 */
actual object ThemeVariableInjector {
    /**
     * Injects all theme CSS variables into the document root.
     *
     * Uses `document.documentElement.style.setProperty()` for each variable.
     *
     * @param variables Map of CSS variable names (with -- prefix) to values
     */
    actual fun injectVariables(variables: Map<String, String>) {
        val root = document.documentElement ?: return
        val style = root.asDynamic().style
        
        variables.forEach { (name, value) ->
            style.setProperty(name, value)
        }
    }
    
    /**
     * Gets a CSS variable value from the document root.
     *
     * Uses `getComputedStyle(document.documentElement).getPropertyValue()`.
     *
     * @param name The CSS variable name (with -- prefix)
     * @return The current value, or empty string if not set
     */
    actual fun getVariable(name: String): String {
        val root = document.documentElement ?: return ""
        val computedStyle = kotlinx.browser.window.getComputedStyle(root)
        return computedStyle.getPropertyValue(name).trim()
    }
    
    /**
     * Removes a CSS variable from the document root.
     *
     * Uses `document.documentElement.style.removeProperty()`.
     *
     * @param name The CSS variable name (with -- prefix)
     */
    actual fun removeVariable(name: String) {
        val root = document.documentElement ?: return
        val style = root.asDynamic().style
        style.removeProperty(name)
    }
    
    /**
     * Clears all theme CSS variables from the document root.
     * Only removes variables that start with common theme prefixes.
     */
    actual fun clearVariables() {
        val root = document.documentElement ?: return
        val style = root.asDynamic().style
        
        val prefixes = listOf(
            "--colors-",
            "--typography-",
            "--spacing-",
            "--border-radius-",
            "--shadow-",
            "--custom-"
        )
        
        // Get all CSS custom properties and remove theme-related ones
        val length = style.length as Int
        val propsToRemove = mutableListOf<String>()
        
        for (i in 0 until length) {
            val prop = style.item(i) as String
            if (prefixes.any { prop.startsWith(it) }) {
                propsToRemove.add(prop)
            }
        }
        
        propsToRemove.forEach { prop ->
            style.removeProperty(prop)
        }
    }
}
