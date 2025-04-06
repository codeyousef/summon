package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.IconData
import code.yousef.summon.components.display.IconType
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
 * Internal data class holding parameters for the Tooltip composable.
 */
internal data class TooltipData(
    // trigger and content lambdas are handled by composition
    val modifier: Modifier,
    val placement: TooltipPlacement,
    val showArrow: Boolean,
    val showOnClick: Boolean,
    val showDelay: Int,
    val hideDelay: Int,
    val role: String,
    val interactive: Boolean,
    val contentString: String? = null // Keep original string content if needed, but prefer lambda
) {
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
 * A composable that displays a tooltip with additional information when hovering over or interacting with a trigger element.
 *
 * @param trigger The composable lambda for the element that triggers the tooltip.
 * @param modifier The modifier to apply to the tooltip wrapper/container.
 * @param placement The preferred placement of the tooltip relative to the trigger.
 * @param showArrow Whether to show an arrow pointing to the trigger.
 * @param showOnClick Whether to also show the tooltip on click.
 * @param showDelay Delay in milliseconds before showing.
 * @param hideDelay Delay in milliseconds before hiding.
 * @param role The role attribute for the tooltip content.
 * @param interactive If true, allows interaction within the tooltip content.
 * @param content The composable lambda for the content to display inside the tooltip.
 */
@Composable
fun Tooltip(
    trigger: @Composable () -> Unit,
    modifier: Modifier = Modifier(),
    placement: TooltipPlacement = TooltipPlacement.TOP,
    showArrow: Boolean = true,
    showOnClick: Boolean = false,
    showDelay: Int = 200,
    hideDelay: Int = 0,
    role: String = "tooltip",
    interactive: Boolean = false,
    content: @Composable () -> Unit // Changed from String to Composable lambda
) {
    val tooltipData = TooltipData(
        modifier = modifier,
        placement = placement,
        showArrow = showArrow,
        showOnClick = showOnClick,
        showDelay = showDelay,
        hideDelay = hideDelay,
        role = role,
        interactive = interactive
    )

    // For now, use a simple placeholder similar to other composable functions
    println("Composable Tooltip function called with placement: ${placement.name}")
    
    // TODO: Implement rendering logic. Tooltip requires wrapping the trigger
    // and conditionally rendering the positioned content based on hover/focus/click state.
    // This needs JS interop and careful state management.
    
    // Render the trigger directly for now
    trigger()
    
    // The actual tooltip content rendering needs to happen elsewhere,
    // managed by the renderer/JS based on interaction with the trigger.
    // This will be implemented when the renderer integration is complete
}

/**
 * Creates an info tooltip composable.
 * @param trigger The composable lambda for the element that triggers the tooltip.
 * @param placement The preferred placement of the tooltip.
 * @param modifier The modifier to apply to this composable.
 * @param content The composable lambda for the content to display inside the tooltip.
 */
@Composable
fun InfoTooltip(
    trigger: @Composable () -> Unit,
    placement: TooltipPlacement = TooltipPlacement.TOP,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    Tooltip(
        trigger = trigger,
        modifier = modifier,
        placement = placement,
        content = content
    )
}

/**
 * Creates a help tooltip composable, using a default help icon as the trigger.
 * @param placement The preferred placement of the tooltip.
 * @param modifier The modifier to apply to this composable.
 * @param content The composable lambda for the help text/content.
 */
@Composable
fun HelpTooltip(
    placement: TooltipPlacement = TooltipPlacement.RIGHT,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    Tooltip(
        modifier = modifier,
        placement = placement,
        trigger = { // Define the default icon trigger within the composable lambda
            Icon(
                name = "help",
                type = IconType.FONT,
                fontFamily = "Material Icons",
                size = "16px",
                color = "#777777",
                modifier = Modifier().style("cursor", "help")
            )
        },
        content = content
    )
} 