package com.summon

import kotlinx.html.TagConsumer

/**
 * Tooltip placement options
 */
enum class TooltipPlacement {
    TOP,      // Above the target element
    RIGHT,    // To the right of the target element
    BOTTOM,   // Below the target element
    LEFT      // To the left of the target element
}

/**
 * A composable that displays a tooltip with additional information when hovering over a target element.
 *
 * @param content The content to display inside the tooltip
 * @param trigger The target component that triggers the tooltip display
 * @param modifier The modifier to apply to this composable
 * @param placement The preferred placement of the tooltip relative to the trigger
 * @param showArrow Whether to show an arrow pointing to the trigger
 * @param showOnClick Whether to also show the tooltip on click (for mobile support)
 * @param showDelay Delay in milliseconds before showing the tooltip (default: 200ms)
 * @param hideDelay Delay in milliseconds before hiding the tooltip (default: 0ms)
 */
data class Tooltip(
    val content: String,
    val trigger: Composable,
    val modifier: Modifier = Modifier(),
    val placement: TooltipPlacement = TooltipPlacement.TOP,
    val showArrow: Boolean = true,
    val showOnClick: Boolean = false,
    val showDelay: Int = 200,
    val hideDelay: Int = 0
) : Composable {
    /**
     * Renders this Tooltip composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getPlatformRenderer().renderTooltip(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

    /**
     * Gets placement-specific styles for the tooltip.
     */
    internal fun getPlacementStyles(): Map<String, String> {
        return when (placement) {
            TooltipPlacement.TOP -> mapOf(
                "bottom" to "100%",
                "margin-bottom" to "10px",
                "left" to "50%",
                "transform" to "translateX(-50%)"
            )

            TooltipPlacement.RIGHT -> mapOf(
                "left" to "100%",
                "margin-left" to "10px",
                "top" to "50%",
                "transform" to "translateY(-50%)"
            )

            TooltipPlacement.BOTTOM -> mapOf(
                "top" to "100%",
                "margin-top" to "10px",
                "left" to "50%",
                "transform" to "translateX(-50%)"
            )

            TooltipPlacement.LEFT -> mapOf(
                "right" to "100%",
                "margin-right" to "10px",
                "top" to "50%",
                "transform" to "translateY(-50%)"
            )
        }
    }

    /**
     * Gets arrow-specific styles for the tooltip arrow.
     */
    internal fun getArrowStyles(): Map<String, String> {
        val base = mapOf(
            "position" to "absolute",
            "width" to "0",
            "height" to "0",
            "border-style" to "solid"
        )

        return when (placement) {
            TooltipPlacement.TOP -> base + mapOf(
                "bottom" to "-6px",
                "left" to "50%",
                "transform" to "translateX(-50%)",
                "border-width" to "6px 6px 0 6px",
                "border-color" to "#333 transparent transparent transparent"
            )

            TooltipPlacement.RIGHT -> base + mapOf(
                "left" to "-6px",
                "top" to "50%",
                "transform" to "translateY(-50%)",
                "border-width" to "6px 6px 6px 0",
                "border-color" to "transparent #333 transparent transparent"
            )

            TooltipPlacement.BOTTOM -> base + mapOf(
                "top" to "-6px",
                "left" to "50%",
                "transform" to "translateX(-50%)",
                "border-width" to "0 6px 6px 6px",
                "border-color" to "transparent transparent #333 transparent"
            )

            TooltipPlacement.LEFT -> base + mapOf(
                "right" to "-6px",
                "top" to "50%",
                "transform" to "translateY(-50%)",
                "border-width" to "6px 0 6px 6px",
                "border-color" to "transparent transparent transparent #333"
            )
        }
    }

    /**
     * Gets accessibility attributes for the tooltip.
     */
    internal fun getAccessibilityAttributes(): Map<String, String> {
        return mapOf(
            "role" to "tooltip",
            "aria-hidden" to "true"  // Initially hidden until shown
        )
    }

    /**
     * Gets attributes for the trigger element.
     */
    internal fun getTriggerAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        attributes["aria-describedby"] = "tooltip-content"
        attributes["data-summon-tooltip"] = "true"

        // Set data attributes for tooltip configuration
        attributes["data-tooltip-placement"] = placement.name.lowercase()
        attributes["data-tooltip-show-arrow"] = showArrow.toString()
        attributes["data-tooltip-show-on-click"] = showOnClick.toString()
        attributes["data-tooltip-show-delay"] = showDelay.toString()
        attributes["data-tooltip-hide-delay"] = hideDelay.toString()

        return attributes
    }
}

/**
 * Creates an info tooltip with informational content.
 * @param content The content to display inside the tooltip
 * @param trigger The target component that triggers the tooltip display
 * @param placement The preferred placement of the tooltip
 * @param modifier The modifier to apply to this composable
 */
fun infoTooltip(
    content: String,
    trigger: Composable,
    placement: TooltipPlacement = TooltipPlacement.TOP,
    modifier: Modifier = Modifier()
): Tooltip = Tooltip(
    content = content,
    trigger = trigger,
    modifier = modifier,
    placement = placement
)

/**
 * Creates a help tooltip, typically displayed next to form fields or UI elements
 * that might need explanation.
 * @param content The help text to display inside the tooltip
 * @param placement The preferred placement of the tooltip
 * @param modifier The modifier to apply to this composable
 */
fun helpTooltip(
    content: String,
    placement: TooltipPlacement = TooltipPlacement.RIGHT,
    modifier: Modifier = Modifier()
): Tooltip {
    // Create a help icon as the trigger
    val helpIcon = Icon(
        name = "help",
        type = IconType.FONT,
        fontFamily = "Material Icons",
        size = "16px",
        color = "#777777",
        modifier = Modifier().cursor("help")
    )

    return Tooltip(
        content = content,
        trigger = helpIcon,
        modifier = modifier,
        placement = placement
    )
} 