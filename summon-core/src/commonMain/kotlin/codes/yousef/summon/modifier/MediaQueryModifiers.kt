/**
 * # Media Query Modifiers
 *
 * Type-safe media query support for responsive design in the Summon framework.
 * This module provides declarative helpers for applying styles based on viewport
 * dimensions, device characteristics, and user preferences.
 *
 * ## Features
 *
 * - **Responsive Breakpoints**: Width and height-based queries
 * - **Device Queries**: Orientation, hover capability, pointer type
 * - **User Preferences**: Dark mode, reduced motion, high contrast
 * - **Type Safety**: Kotlin DSL for media query construction
 * - **Composable Queries**: Combine multiple conditions with and/or logic
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic responsive design
 * Box(
 *     modifier = Modifier()
 *         .display(Display.None)
 *         .mediaQuery(MediaQuery.MinWidth(960)) {
 *             display(Display.Block)
 *         }
 * )
 *
 * // Mobile-first approach
 * Box(
 *     modifier = Modifier()
 *         .padding("8px")
 *         .mediaQuery(MediaQuery.MinWidth(768)) {
 *             padding("16px")
 *         }
 *         .mediaQuery(MediaQuery.MinWidth(1024)) {
 *             padding("24px")
 *         }
 * )
 *
 * // Dark mode support
 * Box(
 *     modifier = Modifier()
 *         .backgroundColor("#ffffff")
 *         .mediaQuery(MediaQuery.PrefersDarkScheme) {
 *             backgroundColor("#1a1a1a")
 *         }
 * )
 *
 * // Reduced motion support
 * Box(
 *     modifier = Modifier()
 *         .transition("all", 300)
 *         .mediaQuery(MediaQuery.PrefersReducedMotion) {
 *             transition("none")
 *         }
 * )
 * ```
 *
 * ## Common Breakpoints
 *
 * ```kotlin
 * // Mobile: < 640px
 * // Tablet: 640px - 1024px
 * // Desktop: >= 1024px
 *
 * Box(
 *     modifier = Modifier()
 *         .fontSize("14px")
 *         .mediaQuery(MediaQuery.MinWidth(640)) {
 *             fontSize("16px")
 *         }
 *         .mediaQuery(MediaQuery.MinWidth(1024)) {
 *             fontSize("18px")
 *         }
 * )
 * ```
 *
 * @see Modifier for the core modifier system
 * @see ResponsiveLayout for component-level responsive design
 * @since 1.0.0
 */
package codes.yousef.summon.modifier

/**
 * Media query types supported by the framework.
 */
sealed class MediaQuery {
    /**
     * Minimum viewport width query.
     * Applies styles when viewport width is at least the specified value.
     */
    data class MinWidth(val pixels: Int) : MediaQuery() {
        override fun toString() = "(min-width: ${pixels}px)"
    }

    /**
     * Maximum viewport width query.
     * Applies styles when viewport width is at most the specified value.
     */
    data class MaxWidth(val pixels: Int) : MediaQuery() {
        override fun toString() = "(max-width: ${pixels}px)"
    }

    /**
     * Minimum viewport height query.
     * Applies styles when viewport height is at least the specified value.
     */
    data class MinHeight(val pixels: Int) : MediaQuery() {
        override fun toString() = "(min-height: ${pixels}px)"
    }

    /**
     * Maximum viewport height query.
     * Applies styles when viewport height is at most the specified value.
     */
    data class MaxHeight(val pixels: Int) : MediaQuery() {
        override fun toString() = "(max-height: ${pixels}px)"
    }

    /**
     * Portrait orientation query.
     * Applies styles when device is in portrait mode (height >= width).
     */
    object Portrait : MediaQuery() {
        override fun toString() = "(orientation: portrait)"
    }

    /**
     * Landscape orientation query.
     * Applies styles when device is in landscape mode (width > height).
     */
    object Landscape : MediaQuery() {
        override fun toString() = "(orientation: landscape)"
    }

    /**
     * Dark color scheme preference query.
     * Applies styles when user prefers dark color scheme.
     */
    object PrefersDarkScheme : MediaQuery() {
        override fun toString() = "(prefers-color-scheme: dark)"
    }

    /**
     * Light color scheme preference query.
     * Applies styles when user prefers light color scheme.
     */
    object PrefersLightScheme : MediaQuery() {
        override fun toString() = "(prefers-color-scheme: light)"
    }

    /**
     * Reduced motion preference query.
     * Applies styles when user prefers reduced motion.
     */
    object PrefersReducedMotion : MediaQuery() {
        override fun toString() = "(prefers-reduced-motion: reduce)"
    }

    /**
     * Hover capability query.
     * Applies styles on devices that support hover (typically desktops).
     */
    object CanHover : MediaQuery() {
        override fun toString() = "(hover: hover)"
    }

    /**
     * No hover capability query.
     * Applies styles on devices without hover support (typically touch devices).
     */
    object NoHover : MediaQuery() {
        override fun toString() = "(hover: none)"
    }

    /**
     * Fine pointer query (mouse).
     * Applies styles on devices with precise pointer input.
     */
    object FinePointer : MediaQuery() {
        override fun toString() = "(pointer: fine)"
    }

    /**
     * Coarse pointer query (touch).
     * Applies styles on devices with imprecise pointer input (touch screens).
     */
    object CoarsePointer : MediaQuery() {
        override fun toString() = "(pointer: coarse)"
    }

    /**
     * Custom media query for advanced use cases.
     */
    data class Custom(val query: String) : MediaQuery() {
        override fun toString() = query
    }

    /**
     * Combine multiple queries with AND logic.
     * All conditions must be true for styles to apply.
     */
    data class And(val queries: List<MediaQuery>) : MediaQuery() {
        constructor(vararg queries: MediaQuery) : this(queries.toList())

        override fun toString() = queries.joinToString(" and ")
    }

    /**
     * Combine multiple queries with OR logic.
     * At least one condition must be true for styles to apply.
     */
    data class Or(val queries: List<MediaQuery>) : MediaQuery() {
        constructor(vararg queries: MediaQuery) : this(queries.toList())

        override fun toString() = queries.joinToString(", ")
    }
}

/**
 * Applies styles conditionally based on a media query.
 * The styles will only be applied when the media query condition is met.
 *
 * Note: This implementation uses data attributes to store media query styles.
 * Platform-specific renderers should generate appropriate CSS @media rules.
 *
 * @param query The media query condition
 * @param builder DSL builder for styles to apply when query matches
 * @return A new [Modifier] with media query styles
 *
 * Example:
 * ```kotlin
 * Modifier()
 *     .fontSize("14px")
 *     .mediaQuery(MediaQuery.MinWidth(768)) {
 *         fontSize("16px")
 *     }
 * ```
 */
fun Modifier.mediaQuery(query: MediaQuery, builder: Modifier.() -> Modifier): Modifier {
    val mediaModifier = builder(Modifier())
    if (mediaModifier.styles.isEmpty()) return this

    val stylesString = mediaModifier.styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    val queryString = query.toString()

    // Store media query styles as data attribute for platform renderer to process
    val existingQueries = attributes["data-media-queries"] ?: ""
    val newQueries = if (existingQueries.isEmpty()) {
        "$queryString{$stylesString}"
    } else {
        "$existingQueries|$queryString{$stylesString}"
    }

    return attribute("data-media-queries", newQueries)
}

/**
 * Common responsive breakpoints as constants.
 */
object Breakpoints {
    /** Small mobile devices (320px) */
    val XS = 320

    /** Mobile devices (640px) */
    val SM = 640

    /** Tablets (768px) */
    val MD = 768

    /** Small desktops (1024px) */
    val LG = 1024

    /** Large desktops (1280px) */
    val XL = 1280

    /** Extra large screens (1536px) */
    val XXL = 1536
}
