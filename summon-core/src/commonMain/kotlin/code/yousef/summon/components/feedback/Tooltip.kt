package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier

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
 * A composable that wraps its content and displays a tooltip when the content is interacted with (e.g., hovered).
 *
 * @param tooltipContent Composable content to display inside the actual tooltip popup.
 * @param modifier Modifier applied to the wrapper element containing the trigger content.
 * @param placement Preferred placement of the tooltip popup relative to the trigger content.
 * @param showArrow Whether to display a small arrow pointing from the tooltip to the trigger.
 * @param showDelay Delay in milliseconds before showing the tooltip after hover/focus (default: 200ms).
 * @param hideDelay Delay in milliseconds before hiding the tooltip after hover/focus is lost (default: 100ms).
 * @param showOnClick Whether the tooltip should also appear when the trigger is clicked.
 * @param trigger The composable content that triggers the tooltip.
 */
@Composable
fun Tooltip(
    tooltipContent: @Composable () -> Unit, // Content for the popup
    modifier: Modifier = Modifier(),
    placement: TooltipPlacement = TooltipPlacement.TOP,
    showArrow: Boolean = true,
    showDelay: Int = 200,
    hideDelay: Int = 100,
    showOnClick: Boolean = false,
    trigger: @Composable () -> Unit // The content that triggers the tooltip
) {
    // Implementation will be provided by the code generator
}

/**
 * Gets placement-specific styles for the tooltip.
 */
internal fun getPlacementStyles(placement: TooltipPlacement): Map<String, String> {
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
internal fun getArrowStyles(placement: TooltipPlacement): Map<String, String> {
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
internal fun getTriggerAttributes(
    placement: TooltipPlacement,
    showArrow: Boolean,
    showOnClick: Boolean = false,
    showDelay: Int = 200,
    hideDelay: Int = 100
): Map<String, String> {
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
