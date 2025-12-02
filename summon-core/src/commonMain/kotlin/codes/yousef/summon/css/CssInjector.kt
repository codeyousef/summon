package codes.yousef.summon.css

/**
 * CSS Injection utilities with sanitization.
 *
 * Provides secure injection of user-provided CSS into the document,
 * with basic sanitization to prevent injection attacks.
 *
 * ## Features
 *
 * - **ID-based Style Management**: Each style block has a unique ID for updates
 * - **Basic Sanitization**: Strips dangerous patterns like `</style>` tags
 * - **Safe Updates**: Updates existing style blocks or creates new ones
 *
 * ## Usage
 *
 * ```kotlin
 * // Inject custom styles
 * CssInjector.injectUserCss("custom-theme", """
 *     body { background: #f5f5f5; }
 *     .card { border-radius: 8px; }
 * """)
 *
 * // Update the same style block
 * CssInjector.injectUserCss("custom-theme", """
 *     body { background: #ffffff; }
 * """)
 * ```
 *
 * @since 1.0.0
 */
expect object CssInjector {
    /**
     * Injects or updates a CSS style block with the given ID.
     *
     * If a `<style>` element with the given ID already exists, its content
     * is replaced. Otherwise, a new `<style>` element is created and appended
     * to `<head>`.
     *
     * Basic sanitization is applied to prevent injection attacks:
     * - Removes `</style>` and `<style>` tags from the CSS content
     * - Removes `@import` rules that could load external resources
     *
     * @param id Unique identifier for this style block
     * @param css CSS content to inject
     * @return true if injection succeeded, false otherwise
     */
    fun injectUserCss(id: String, css: String): Boolean
    
    /**
     * Removes a previously injected style block.
     *
     * @param id The ID of the style block to remove
     * @return true if the element was found and removed, false otherwise
     */
    fun removeUserCss(id: String): Boolean
    
    /**
     * Gets the current content of a style block.
     *
     * @param id The ID of the style block
     * @return The CSS content, or null if not found
     */
    fun getUserCss(id: String): String?
    
    /**
     * Checks if a style block with the given ID exists.
     *
     * @param id The ID to check
     * @return true if the style block exists
     */
    fun hasUserCss(id: String): Boolean
}

/**
 * Common CSS sanitization utilities.
 */
object CssSanitizer {
    // Patterns that could be used for injection attacks
    private val dangerousPatterns = listOf(
        Regex("""</\s*style\s*>""", RegexOption.IGNORE_CASE),
        Regex("""<\s*style[^>]*>""", RegexOption.IGNORE_CASE),
        Regex("""<\s*script[^>]*>""", RegexOption.IGNORE_CASE),
        Regex("""</\s*script\s*>""", RegexOption.IGNORE_CASE),
        Regex("""javascript\s*:""", RegexOption.IGNORE_CASE),
        Regex("""expression\s*\(""", RegexOption.IGNORE_CASE),
        Regex("""behavior\s*:""", RegexOption.IGNORE_CASE),
        Regex("""-moz-binding\s*:""", RegexOption.IGNORE_CASE)
    )
    
    // @import rules that could load external malicious resources
    private val importPattern = Regex("""@import\s+[^;]+;?""", RegexOption.IGNORE_CASE)
    
    /**
     * Sanitizes CSS content by removing potentially dangerous patterns.
     *
     * @param css Raw CSS content
     * @return Sanitized CSS content
     */
    fun sanitize(css: String): String {
        var sanitized = css
        
        // Remove @import rules
        sanitized = importPattern.replace(sanitized, "/* @import removed for security */")
        
        // Remove dangerous patterns
        dangerousPatterns.forEach { pattern ->
            sanitized = pattern.replace(sanitized, "/* removed */")
        }
        
        return sanitized
    }
    
    /**
     * Validates CSS content without modification.
     *
     * @param css CSS content to validate
     * @return List of security concerns found, empty if none
     */
    fun validate(css: String): List<String> {
        val concerns = mutableListOf<String>()
        
        if (importPattern.containsMatchIn(css)) {
            concerns.add("Contains @import rule")
        }
        
        dangerousPatterns.forEachIndexed { index, pattern ->
            if (pattern.containsMatchIn(css)) {
                concerns.add("Contains potentially dangerous pattern #$index")
            }
        }
        
        return concerns
    }
}
