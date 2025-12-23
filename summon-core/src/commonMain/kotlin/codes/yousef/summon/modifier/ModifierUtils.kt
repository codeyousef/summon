package codes.yousef.summon.modifier

/**
 * This file contains modifier functions that don't have their own dedicated files.
 * Layout modifiers are in LayoutModifiers.kt
 * Styling modifiers are in StylingModifiers.kt
 */

// Event Modifiers

// onClick moved to PointerEventModifiers.kt




// Attribute Modifiers

/**
 * Gets an attribute value or null if not present.
 */
fun Modifier.getAttribute(name: String): String? =
    attributes[name]

/**
 * Sets the HTML button type attribute (`type="button|submit|reset"`).
 */
fun Modifier.buttonType(value: ButtonType): Modifier =
    attribute("type", value.value)

// Deprecated typealiases for backward compatibility
// These might not work if the objects are gone, but we can try to alias to something else or just remove them.
// Since LayoutModifiers is now a file with top-level functions, we can't alias it as a type easily.
// But we can create empty objects for namespace compatibility if needed.

@Deprecated("Use top-level functions instead")
object LayoutModifiers
@Deprecated("Use top-level functions instead")
object StylingModifiers
@Deprecated("Use top-level functions instead")
object EventModifiers
@Deprecated("Use top-level functions instead")
object AttributeModifiers

typealias Layout = LayoutModifiers
typealias Styling = StylingModifiers
typealias Events = EventModifiers
typealias Attributes = AttributeModifiers
