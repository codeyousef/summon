package code.yousef.summon.modifier

/**
 * Extension functions that add core functionality to the Modifier class
 * This adapts the simple Modifier class to support the features expected by tests
 */

/**
 * Simulates attributes and events with special style keys
 */
typealias ModifierHandler = () -> Unit

val Modifier.events: Map<String, ModifierHandler>
    get() = eventHandlers

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
    return Modifier(styles.toMap(), attributes.toMap(), eventHandlers.toMap(), pseudoElements.toList())
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
    return copy(eventHandlers = eventHandlers + (eventName to handler))
}
