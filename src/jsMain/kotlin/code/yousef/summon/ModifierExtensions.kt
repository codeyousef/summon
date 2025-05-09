package code.yousef.summon

import code.yousef.summon.modifier.Modifier

/**
 * Extension functions for Modifier to help with JS implementation.
 */

/**
 * Extracts onClick handler from a modifier if present.
 * This looks for a special onClick attribute or event handler in the modifier.
 * 
 * @return The onClick handler function or null if none exists
 */
fun Modifier.extractOnClick(): (() -> Unit)? {
    // Look for onClick in the events map (if implemented)
    val events = this.getEventHandlers()
    if (events != null && events.containsKey("click")) {
        val handler = events["click"]
        if (handler is Function<*>) {
            @Suppress("UNCHECKED_CAST")
            return handler as? (() -> Unit)
        }
    }
    
    // Fallback: check custom attributes for click handler
    // This is a placeholder - actual implementation would depend on how click handlers
    // are stored in the modifier system
    return null
}

/**
 * Gets all event handlers stored in this modifier.
 * This is a placeholder method - the actual implementation would depend on
 * how events are stored in the Modifier class.
 * 
 * @return A map of event names to handler functions
 */
private fun Modifier.getEventHandlers(): Map<String, Any>? {
    // This would access the event handlers stored in the Modifier class
    // For now, return an empty map as a placeholder
    return emptyMap()
}

/**
 * Convert modifier styles to a camelCase style string for JavaScript.
 * NOTE: This is only appropriate for DOM object style property access, not for HTML style attributes.
 * For HTML style attributes, use toStyleString() instead which outputs kebab-case properties.
 */
fun Modifier.toStyleStringCamelCase(): String {
    if (this.styles.isEmpty()) {
        return ""
    }

    // Converts e.g. {"background-color": "red", "font-size": "12px"}
    // to "backgroundColor: red; fontSize: 12px;"
    // The format is "key1: value1; key2: value2;".
    return this.styles
        .map { (key, value) -> "${key.kebabToCamelCase()}: $value" }
        .joinToString(separator = "; ", postfix = ";")
}

/**
 * Convert modifier styles to a valid CSS style string that can be used in HTML style attributes.
 * This ensures all property names are in kebab-case format as required by browsers.
 */
fun Modifier.toStyleString(): String {
    if (this.styles.isEmpty()) {
        return ""
    }
    
    // Determine if the key is already in kebab-case or needs conversion from camelCase
    return this.styles
        .map { (key, value) -> 
            val cssPropertyName = if (key.contains('-')) {
                // Already in kebab-case
                key
            } else {
                // Convert from camelCase to kebab-case
                key.camelToKebabCase()
            }
            "$cssPropertyName: $value"
        }
        .joinToString(separator = "; ", postfix = ";")
}

/**
 * Converts a kebab-case string to camelCase.
 * For example: "background-color" becomes "backgroundColor"
 */
private fun String.kebabToCamelCase(): String {
    if (!this.contains('-')) {
        // Return as-is if empty or no hyphens (already camelCase or single word)
        return this
    }

    val parts = this.split('-')
    val firstPart = parts.first()

    val remainingParts = parts.drop(1).joinToString("") { part ->
        if (part.isEmpty()) {
            "" // Handle potential empty parts from consecutive hyphens (e.g., "foo--bar")
        } else {
            // Capitalize the first letter of the part
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecaseChar() else it }
        }
    }

    return firstPart + remainingParts
}

/**
 * Converts a camelCase string to kebab-case.
 * For example: "backgroundColor" becomes "background-color"
 */
private fun String.camelToKebabCase(): String {
    if (this.isEmpty()) {
        return this
    }
    
    // Replace each uppercase letter with a hyphen followed by the lowercase letter
    return this.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
}
