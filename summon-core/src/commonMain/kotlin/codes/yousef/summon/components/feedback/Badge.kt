package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.ModifierExtras.onClick
import codes.yousef.summon.runtime.LocalPlatformRenderer

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
 * @param displayStart Optional composable lambda to render content before the main text (e.g., an icon)
 * @param displayEnd Optional composable lambda to render content after the main text (e.g., an icon or dismiss button)
 * @param iconEnd DEPRECATED: Use displayEnd instead for clarity. Kept for backward compatibility.
 */
@Composable
fun Badge(
    content: String,
    modifier: Modifier = Modifier(),
    type: BadgeType = BadgeType.PRIMARY,
    shape: BadgeShape = BadgeShape.ROUNDED,
    isOutlined: Boolean = false,
    size: String = "medium",
    onClick: (() -> Unit)? = null,
    displayStart: (@Composable () -> Unit)? = null,
    displayEnd: (@Composable () -> Unit)? = null,
    iconEnd: (@Composable () -> Unit)? = null
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

    // Determine start content
    val effectiveStart = displayStart

    // Determine end content, prioritizing displayEnd over iconEnd
    val effectiveEnd = displayEnd ?: iconEnd

    // Apply the passed modifier *after* the base styling
    finalModifier = finalModifier.then(modifier)

    // Render the badge container using the renderer, passing the internal layout as the content
    val renderer = LocalPlatformRenderer.current
    renderer.renderBadge(modifier = finalModifier) { // Pass the internal layout as the lambda
        // Use a Row to arrange icon, text, and end icon/action horizontally
        Row(
            modifier = Modifier()
                .padding("0px 4px")
                .alignItems(AlignItems.Center)
        ) {
            // Render start icon if provided
            if (effectiveStart != null) {
                Box(Modifier().padding("0px 4px 0px 0px")) {
                    effectiveStart()
                }
            }

            // Render the main content text (only if not a dot)
            if (shape != BadgeShape.DOT) {
                Text(text = content)
            }

            // Render end icon or dismiss action if provided
            if (effectiveEnd != null) {
                Box(Modifier().padding("0px 0px 0px 4px")) {
                    effectiveEnd()
                }
            }
        }
    }
}