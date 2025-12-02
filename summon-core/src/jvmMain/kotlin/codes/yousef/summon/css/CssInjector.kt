package codes.yousef.summon.css

/**
 * JVM implementation of CssInjector.
 *
 * On the server side, CSS cannot be directly injected into a browser.
 * This implementation stores CSS content that can be rendered during SSR.
 */
actual object CssInjector {
    private val styleBlocks = mutableMapOf<String, String>()
    
    /**
     * Stores a CSS style block with the given ID.
     *
     * On JVM, the CSS is stored in memory and can be retrieved
     * for SSR via [getAllCss] or [generateStyleBlocks].
     */
    actual fun injectUserCss(id: String, css: String): Boolean {
        return try {
            val sanitizedCss = CssSanitizer.sanitize(css)
            styleBlocks[id] = sanitizedCss
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Removes a stored style block.
     */
    actual fun removeUserCss(id: String): Boolean {
        return styleBlocks.remove(id) != null
    }
    
    /**
     * Gets the content of a stored style block.
     */
    actual fun getUserCss(id: String): String? {
        return styleBlocks[id]
    }
    
    /**
     * Checks if a style block with the given ID is stored.
     */
    actual fun hasUserCss(id: String): Boolean {
        return styleBlocks.containsKey(id)
    }
    
    /**
     * Returns all stored CSS blocks.
     *
     * @return Map of ID to CSS content
     */
    fun getAllCss(): Map<String, String> {
        return styleBlocks.toMap()
    }
    
    /**
     * Generates HTML `<style>` blocks for all stored CSS.
     *
     * @return HTML string containing all style blocks
     */
    fun generateStyleBlocks(): String {
        return styleBlocks.entries.joinToString("\n") { (id, css) ->
            """<style id="summon-user-css-$id" data-summon-css="$id">$css</style>"""
        }
    }
    
    /**
     * Clears all stored style blocks.
     */
    fun clear() {
        styleBlocks.clear()
    }
}
