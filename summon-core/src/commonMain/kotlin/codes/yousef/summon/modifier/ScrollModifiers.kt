/**
 * # Scroll Modifiers
 *
 * Scroll behavior and event handling modifiers for the Summon framework.
 * This module provides helpers for scroll event handling and programmatic scrolling.
 *
 * ## Features
 *
 * - **Scroll Events**: React to scroll events on elements
 * - **Scroll Behavior**: Control smooth vs instant scrolling
 * - **Overflow Control**: Manage scroll overflow behavior
 * - **Scroll Snapping**: Enable CSS scroll snap points
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Handle scroll events
 * Box(
 *     modifier = Modifier()
 *         .height("400px")
 *         .overflowY("scroll")
 *         .onScroll("handleScroll(event)")
 * ) {
 *     // Scrollable content
 * }
 *
 * // Smooth scrolling container
 * Box(
 *     modifier = Modifier()
 *         .scrollBehavior(ScrollBehavior.SMOOTH)
 *         .overflowY("auto")
 * ) {
 *     // Content
 * }
 *
 * // Scroll snapping
 * Box(
 *     modifier = Modifier()
 *         .scrollSnapType(ScrollSnapType.Y_MANDATORY)
 *         .overflowY("scroll")
 * ) {
 *     // Each child with scroll-snap-align
 * }
 * ```
 *
 * @see Modifier for the core modifier system
 * @see OverflowModifiers for overflow control
 * @since 1.0.0
 */
package codes.yousef.summon.modifier

/**
 * Scroll behavior types.
 */
enum class ScrollBehavior(val value: String) {
    /** Instant scrolling without animation */
    AUTO("auto"),

    /** Smooth animated scrolling */
    SMOOTH("smooth")
}

/**
 * Scroll snap types.
 */
enum class ScrollSnapType(val value: String) {
    /** No scroll snapping */
    NONE("none"),

    /** Mandatory snapping on X axis */
    X_MANDATORY("x mandatory"),

    /** Mandatory snapping on Y axis */
    Y_MANDATORY("y mandatory"),

    /** Proximity snapping on X axis */
    X_PROXIMITY("x proximity"),

    /** Proximity snapping on Y axis */
    Y_PROXIMITY("y proximity"),

    /** Mandatory snapping on both axes */
    BOTH_MANDATORY("both mandatory")
}

/**
 * Scroll snap alignment.
 */
enum class ScrollSnapAlign(val value: String) {
    /** No snap alignment */
    NONE("none"),

    /** Snap to start of container */
    START("start"),

    /** Snap to center of container */
    CENTER("center"),

    /** Snap to end of container */
    END("end")
}

/**
 * Adds a scroll event listener to the element.
 *
 * @param handler The JavaScript code to execute when scrolling occurs
 * @return A new [Modifier] with the onscroll attribute
 *
 * Example:
 * ```kotlin
 * Modifier().onScroll("console.log('scrolled', event.target.scrollTop)")
 * ```
 */
fun Modifier.onScroll(handler: String): Modifier {
    return attribute("onscroll", handler)
}

/**
 * Sets the scroll behavior for programmatic scrolling.
 *
 * @param behavior The scroll behavior (auto or smooth)
 * @return A new [Modifier] with scroll-behavior style
 *
 * Example:
 * ```kotlin
 * Modifier().scrollBehavior(ScrollBehavior.SMOOTH)
 * ```
 */
fun Modifier.scrollBehavior(behavior: ScrollBehavior): Modifier {
    return style("scroll-behavior", behavior.value)
}

/**
 * Sets the scroll snap type for the container.
 *
 * @param type The scroll snap type
 * @return A new [Modifier] with scroll-snap-type style
 *
 * Example:
 * ```kotlin
 * Modifier().scrollSnapType(ScrollSnapType.Y_MANDATORY)
 * ```
 */
fun Modifier.scrollSnapType(type: ScrollSnapType): Modifier {
    return style("scroll-snap-type", type.value)
}

/**
 * Sets the scroll snap alignment for a child element.
 *
 * @param align The scroll snap alignment
 * @return A new [Modifier] with scroll-snap-align style
 *
 * Example:
 * ```kotlin
 * Modifier().scrollSnapAlign(ScrollSnapAlign.START)
 * ```
 */
fun Modifier.scrollSnapAlign(align: ScrollSnapAlign): Modifier {
    return style("scroll-snap-align", align.value)
}

/**
 * Sets scroll snap stop behavior.
 *
 * @param always Whether to always stop on snap points
 * @return A new [Modifier] with scroll-snap-stop style
 *
 * Example:
 * ```kotlin
 * Modifier().scrollSnapStop(true)
 * ```
 */
fun Modifier.scrollSnapStop(always: Boolean): Modifier {
    return style("scroll-snap-stop", if (always) "always" else "normal")
}

/**
 * Sets the scroll margin for snap points.
 *
 * @param value The scroll margin value
 * @return A new [Modifier] with scroll-margin style
 *
 * Example:
 * ```kotlin
 * Modifier().scrollMargin("20px")
 * ```
 */
fun Modifier.scrollMargin(value: String): Modifier {
    return style("scroll-margin", value)
}

/**
 * Sets the scroll padding for the container.
 *
 * @param value The scroll padding value
 * @return A new [Modifier] with scroll-padding style
 *
 * Example:
 * ```kotlin
 * Modifier().scrollPadding("20px")
 * ```
 */
fun Modifier.scrollPadding(value: String): Modifier {
    return style("scroll-padding", value)
}

// Note: overflow, overflowX, and overflowY are defined in LayoutModifiers.kt
