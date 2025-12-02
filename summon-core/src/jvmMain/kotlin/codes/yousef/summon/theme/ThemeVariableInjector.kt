package codes.yousef.summon.theme

/**
 * JVM implementation of ThemeVariableInjector.
 *
 * On the JVM/server side, CSS variables cannot be directly injected into a browser.
 * Instead, this implementation collects variables that can be:
 * 1. Rendered as a `<style>` block in the HTML `<head>`
 * 2. Used to generate CSS custom property declarations
 *
 * @see ThemeVariableInjector for common API
 */
actual object ThemeVariableInjector {
    private val variables = mutableMapOf<String, String>()
    
    /**
     * Stores theme CSS variables for later rendering.
     *
     * On JVM, variables are stored in memory and can be rendered
     * as CSS during SSR via [generateCssBlock].
     *
     * @param variables Map of CSS variable names (with -- prefix) to values
     */
    actual fun injectVariables(variables: Map<String, String>) {
        this.variables.clear()
        this.variables.putAll(variables)
    }
    
    /**
     * Gets a stored CSS variable value.
     *
     * @param name The CSS variable name (with -- prefix)
     * @return The stored value, or empty string if not set
     */
    actual fun getVariable(name: String): String {
        return variables[name] ?: ""
    }
    
    /**
     * Removes a CSS variable from the stored collection.
     *
     * @param name The CSS variable name (with -- prefix)
     */
    actual fun removeVariable(name: String) {
        variables.remove(name)
    }
    
    /**
     * Clears all stored theme CSS variables.
     */
    actual fun clearVariables() {
        variables.clear()
    }
    
    /**
     * Generates a CSS block containing all stored variables.
     *
     * Returns a `<style>` block suitable for inclusion in `<head>`:
     *
     * ```html
     * <style id="summon-theme-variables">
     *   :root {
     *     --colors-primary-main: #1976d2;
     *     --spacing-md: 16px;
     *     ...
     *   }
     * </style>
     * ```
     *
     * @return CSS style block string, or empty string if no variables
     */
    fun generateCssBlock(): String {
        if (variables.isEmpty()) return ""
        
        val cssVars = variables.entries.joinToString("\n    ") { (name, value) ->
            "$name: $value;"
        }
        
        return """
            |<style id="summon-theme-variables">
            |  :root {
            |    $cssVars
            |  }
            |</style>
        """.trimMargin()
    }
    
    /**
     * Generates just the CSS content (without `<style>` tags).
     *
     * @return CSS content for :root selector
     */
    fun generateCssContent(): String {
        if (variables.isEmpty()) return ""
        
        val cssVars = variables.entries.joinToString("\n  ") { (name, value) ->
            "$name: $value;"
        }
        
        return ":root {\n  $cssVars\n}"
    }
    
    /**
     * Returns a copy of all stored variables.
     *
     * @return Map of CSS variable names to values
     */
    fun getAllVariables(): Map<String, String> {
        return variables.toMap()
    }
}
