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
