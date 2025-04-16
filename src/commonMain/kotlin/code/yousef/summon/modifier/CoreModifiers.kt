package code.yousef.summon.modifier

/**
 * Extension functions that add core functionality to the Modifier class
 * This adapts the simple Modifier class to support the features expected by tests
 */

/**
 * Simulates attributes and events with special style keys
 */
typealias ModifierHandler = () -> Unit

// Extension properties to simulate attributes and events from the styles map
val Modifier.attributes: Map<String, String>
    get() = styles.filterKeys { it.startsWith("__attr:") }
        .mapKeys { (k, _) -> k.removePrefix("__attr:") }

val Modifier.events: Map<String, ModifierHandler>
    get() = emptyMap() // Not implemented in the actual Modifier class

/**
 * Adds a style attribute to the modifier
 */
fun Modifier.style(name: String, value: String): Modifier {
    // If it's an attribute or event, prefix accordingly
    val key = when {
        name == "style" -> name
        name == "class" -> name
        name == "id" -> name
        name.startsWith("hx-") -> name // HTMX attributes
        name.startsWith("on") -> "__event:${name.substring(2)}" // Events like onClick -> click
        else -> "__attr:$name" // Regular attributes
    }
    return Modifier(styles + (key to value))
}

/**
 * Applies a code block conditionally to modify this Modifier
 */
inline fun Modifier.applyIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        block()
    } else {
        this
    }
}

/**
 * Creates a clone of this modifier
 */
fun Modifier.clone(): Modifier {
    return Modifier(styles.toMap())
}

/**
 * Combines two modifiers
 */
fun Modifier.combine(other: Modifier): Modifier = this.then(other)

/**
 * Adds an event handler to the modifier.
 * This is implemented by storing a serialized representation in the styles map
 * as the actual handler function can't be stored in styles.
 */
fun Modifier.event(eventName: String, handler: ModifierHandler): Modifier {
    // Since we can't store functions in the map, we just add a marker style
    // TODO: Implement a real implementation
    // In a real implementation, this would need to register the handler elsewhere
    return style("__event:$eventName", "true")
} 
