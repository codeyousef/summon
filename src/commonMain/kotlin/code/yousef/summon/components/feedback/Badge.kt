package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.attribute
import code.yousef.summon.modifier.minHeight
import code.yousef.summon.modifier.minWidth
import code.yousef.summon.modifier.onClick
import code.yousef.summon.runtime.LocalPlatformRenderer

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
@Composable
fun Badge(
    content: String,
    modifier: Modifier = Modifier(),
    type: BadgeType = BadgeType.PRIMARY,
    shape: BadgeShape = BadgeShape.ROUNDED,
    isOutlined: Boolean = false,
    size: String = "medium",
    onClick: (() -> Unit)? = null
) {
    // Apply type-specific styling
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

    var finalModifier = if (isOutlined) {
        modifier
            .background("transparent")
            .color(bgColor)
            .border("1px", "solid", bgColor)
    } else {
        modifier
            .background(bgColor)
            .color(textColor)
    }

    // Apply shape-specific styling
    finalModifier = when (shape) {
        BadgeShape.SQUARE -> finalModifier.borderRadius("2px")
        BadgeShape.ROUNDED -> finalModifier.borderRadius("4px")
        BadgeShape.PILL -> finalModifier.borderRadius("9999px")
        BadgeShape.DOT -> {
            val sizeValue = when (size) {
                "small" -> "0.5rem"
                "large" -> "1rem"
                else -> "0.75rem" // medium
            }
            finalModifier
                .borderRadius("50%")
                .width(sizeValue)
                .height(sizeValue)
                .minWidth(sizeValue)
                .minHeight(sizeValue)
                .padding("0")
        }
    }

    // Apply size-specific styling
    finalModifier = when (size) {
        "small" -> finalModifier
            .fontSize("0.75rem")
            .padding(if (shape != BadgeShape.DOT) "0.125rem 0.375rem" else "0")
        "large" -> finalModifier
            .fontSize("1rem")
            .padding(if (shape != BadgeShape.DOT) "0.375rem 0.75rem" else "0")
        else -> finalModifier
            .fontSize("0.875rem")
            .padding(if (shape != BadgeShape.DOT) "0.25rem 0.5rem" else "0")
    }

    // Add accessibility attributes
    finalModifier = if (onClick != null) {
        finalModifier
            .attribute("role", "button")
            .attribute("tabindex", "0")
            .onClick(onClick)
    } else {
        finalModifier
            .attribute("role", "status")
            .attribute("aria-label", content)
    }

    // Add the content text as a data attribute for the renderer to use
    finalModifier = finalModifier.attribute("data-content", content)

    // Render the badge
    val renderer = LocalPlatformRenderer.current
    renderer.renderBadge(finalModifier)
}

/**
 * Creates a status badge with appropriate styling for status indicators.
 *
 * @param status The status text to display
 * @param type The type of status
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun StatusBadge(
    status: String,
    type: BadgeType,
    modifier: Modifier = Modifier()
) {
    Badge(
        content = status,
        modifier = modifier,
        type = type,
        shape = BadgeShape.PILL
    )
}

/**
 * Creates a counter badge, typically used for notifications or counts.
 *
 * @param count The count to display
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun CounterBadge(
    count: Int,
    modifier: Modifier = Modifier()
) {
    Badge(
        content = count.toString(),
        modifier = modifier,
        type = BadgeType.PRIMARY,
        shape = BadgeShape.PILL,
        size = "small"
    )
}

/**
 * Creates a simple dot badge, typically used to indicate status without text.
 *
 * @param type The type of status
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun DotBadge(
    type: BadgeType,
    modifier: Modifier = Modifier()
) {
    Badge(
        content = "",
        modifier = modifier,
        type = type,
        shape = BadgeShape.DOT,
        size = "small"
    )
}