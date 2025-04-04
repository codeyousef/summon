package com.summon

import kotlinx.html.TagConsumer

/**
 * Badge types for different semantic meanings
 */
enum class BadgeType {
    PRIMARY,    // Primary color badge
    SECONDARY,  // Secondary color badge
    SUCCESS,    // Success indicator
    WARNING,    // Warning indicator
    ERROR,      // Error indicator
    INFO,       // Information indicator
    NEUTRAL     // Neutral badge without specific semantic meaning
}

/**
 * Badge shapes for different visual styles
 */
enum class BadgeShape {
    SQUARE,     // Square badge with minor border radius
    ROUNDED,    // Rounded badge
    PILL,       // Pill-shaped badge (fully rounded)
    DOT         // Dot badge (small circular indicator)
}

/**
 * A composable that displays a badge, typically used for status indicators, counters, or labels.
 *
 * @param content The text content of the badge
 * @param modifier The modifier to apply to this composable
 * @param type The semantic type of the badge
 * @param shape The shape of the badge
 * @param isOutlined Whether the badge has an outlined style (vs filled)
 * @param size The size of the badge (small, medium, large)
 * @param onClick Optional click handler for the badge
 */
data class Badge(
    val content: String,
    val modifier: Modifier = Modifier(),
    val type: BadgeType = BadgeType.PRIMARY,
    val shape: BadgeShape = BadgeShape.ROUNDED,
    val isOutlined: Boolean = false,
    val size: String = "medium",
    val onClick: (() -> Unit)? = null
) : Composable, TextComponent {
    /**
     * Renders this Badge composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderBadge(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Gets type-specific styles for the badge.
     */
    internal fun getTypeStyles(): Map<String, String> {
        val baseStyle = when (type) {
            BadgeType.PRIMARY -> Pair("#2196f3", "#ffffff")
            BadgeType.SECONDARY -> Pair("#9c27b0", "#ffffff")
            BadgeType.SUCCESS -> Pair("#4caf50", "#ffffff")
            BadgeType.WARNING -> Pair("#ff9800", "#ffffff")
            BadgeType.ERROR -> Pair("#f44336", "#ffffff")
            BadgeType.INFO -> Pair("#03a9f4", "#ffffff")
            BadgeType.NEUTRAL -> Pair("#9e9e9e", "#ffffff")
        }

        val (bgColor, textColor) = baseStyle

        return if (isOutlined) {
            mapOf(
                "background-color" to "transparent",
                "color" to bgColor,
                "border" to "1px solid $bgColor"
            )
        } else {
            mapOf(
                "background-color" to bgColor,
                "color" to textColor
            )
        }
    }

    /**
     * Gets shape-specific styles for the badge.
     */
    internal fun getShapeStyles(): Map<String, String> {
        return when (shape) {
            BadgeShape.SQUARE -> mapOf(
                "border-radius" to "2px"
            )

            BadgeShape.ROUNDED -> mapOf(
                "border-radius" to "4px"
            )

            BadgeShape.PILL -> mapOf(
                "border-radius" to "9999px"
            )

            BadgeShape.DOT -> mapOf(
                "border-radius" to "50%",
                "width" to getSizeValue(),
                "height" to getSizeValue(),
                "min-width" to getSizeValue(),
                "min-height" to getSizeValue(),
                "padding" to "0"
            )
        }
    }

    /**
     * Gets size-specific styles for the badge.
     */
    internal fun getSizeStyles(): Map<String, String> {
        return when (size) {
            "small" -> mapOf(
                "font-size" to "0.75rem",
                "padding" to if (shape != BadgeShape.DOT) "0.125rem 0.375rem" else "0"
            )

            "large" -> mapOf(
                "font-size" to "1rem",
                "padding" to if (shape != BadgeShape.DOT) "0.375rem 0.75rem" else "0"
            )

            else -> mapOf( // medium (default)
                "font-size" to "0.875rem",
                "padding" to if (shape != BadgeShape.DOT) "0.25rem 0.5rem" else "0"
            )
        }
    }

    /**
     * Gets the size value for dot badges.
     */
    private fun getSizeValue(): String {
        return when (size) {
            "small" -> "0.5rem"
            "large" -> "1rem"
            else -> "0.75rem" // medium
        }
    }

    /**
     * Gets accessibility attributes for the badge.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // If clickable, make it accessible as a button
        if (onClick != null) {
            attributes["role"] = "button"
            attributes["tabindex"] = "0"
        } else {
            // Otherwise, it's just a status indicator
            attributes["role"] = "status"
            attributes["aria-label"] = content
        }

        return attributes
    }
}

/**
 * Creates a status badge with appropriate styling for status indicators.
 * @param status The status text to display
 * @param type The type of status
 * @param modifier The modifier to apply to this composable
 */
fun statusBadge(
    status: String,
    type: BadgeType,
    modifier: Modifier = Modifier()
): Badge = Badge(
    content = status,
    modifier = modifier,
    type = type,
    shape = BadgeShape.PILL
)

/**
 * Creates a counter badge, typically used for notifications or counts.
 * @param count The count to display
 * @param modifier The modifier to apply to this composable
 */
fun counterBadge(
    count: Int,
    modifier: Modifier = Modifier()
): Badge = Badge(
    content = count.toString(),
    modifier = modifier,
    type = BadgeType.PRIMARY,
    shape = BadgeShape.PILL,
    size = "small"
)

/**
 * Creates a simple dot badge, typically used to indicate status without text.
 * @param type The type of status
 * @param modifier The modifier to apply to this composable
 */
fun dotBadge(
    type: BadgeType,
    modifier: Modifier = Modifier()
): Badge = Badge(
    content = "",
    modifier = modifier,
    type = type,
    shape = BadgeShape.DOT,
    size = "small"
) 