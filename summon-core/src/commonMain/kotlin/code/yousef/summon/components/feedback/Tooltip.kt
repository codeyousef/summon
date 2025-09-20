package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.mapOfCompat
import code.yousef.summon.modifier.Modifier

/**
 * Tooltip placement options that determine where the tooltip appears relative to its trigger element.
 *
 * Choose placement based on the trigger element's position in your layout and available space:
 * - **TOP**: Best when there's space above and you want to avoid covering content below
 * - **BOTTOM**: Most common placement, follows natural reading flow
 * - **LEFT**: Good for elements near the right edge of the screen
 * - **RIGHT**: Good for elements near the left edge of the screen
 *
 * ## Automatic Positioning
 * The tooltip system automatically adjusts placement when the preferred position would
 * cause the tooltip to overflow the viewport, ensuring optimal user experience.
 *
 * ## Accessibility Guidelines
 * - Choose placements that don't interfere with screen reader navigation
 * - Ensure tooltips remain visible and readable in all positions
 * - Consider touch device interactions when selecting placement
 *
 * @see Tooltip for implementation details
 * @since 1.0.0
 */
enum class TooltipPlacement {
    /** Above the target element. Good when space is available above and content below should remain visible. */
    TOP,

    /** To the right of the target element. Ideal for left-aligned content and when space is available on the right. */
    RIGHT,

    /** Below the target element. Most natural placement following reading flow. */
    BOTTOM,

    /** To the left of the target element. Good for right-aligned content and when space is available on the left. */
    LEFT
}

/**
 * A Tooltip component that displays contextual information when users interact with trigger elements.
 *
 * Tooltips provide helpful contextual information that appears on hover or focus, enhancing
 * user experience by offering additional details without cluttering the interface. They're
 * perfect for explaining UI elements, providing definitions, or offering helpful hints.
 *
 * ## Features
 * - **Multiple trigger modes**: Hover, focus, and optional click activation
 * - **Flexible positioning**: Automatic placement with collision detection
 * - **Customizable timing**: Configurable show/hide delays for optimal UX
 * - **Arrow indicators**: Optional visual arrows pointing to trigger elements
 * - **Accessibility**: Full keyboard navigation and screen reader support
 * - **Rich content**: Support for text, icons, and complex tooltip content
 * - **Touch support**: Optimized behavior for touch devices
 *
 * ## Basic Usage
 * ```kotlin
 * // Simple text tooltip
 * Tooltip(
 *     tooltipContent = { Text("Click to edit this field") },
 *     placement = TooltipPlacement.TOP
 * ) {
 *     Button(onClick = { editField() }) {
 *         Text("Edit")
 *     }
 * }
 * ```
 *
 * ## Advanced Usage
 * ```kotlin
 * // Rich tooltip with custom styling
 * Tooltip(
 *     tooltipContent = {
 *         Column(
 *             modifier = Modifier()
 *                 .padding(Spacing.MD)
 *                 .style("max-width", "200px")
 *         ) {
 *             Text(
 *                 text = "Advanced Settings",
 *                 modifier = Modifier()
 *                     .fontSize(FontSize.LG)
 *                     .fontWeight(FontWeight.BOLD)
 *                     .margin(Margin.bottom(Spacing.XS))
 *             )
 *             Text(
 *                 text = "Configure advanced options for this feature. Changes will apply immediately.",
 *                 modifier = Modifier().fontSize(FontSize.SM)
 *             )
 *         }
 *     },
 *     placement = TooltipPlacement.RIGHT,
 *     showArrow = true,
 *     showDelay = 500,
 *     hideDelay = 200,
 *     modifier = Modifier().style("cursor", "help")
 * ) {
 *     Icon(
 *         name = "help-circle",
 *         modifier = Modifier()
 *             .width(Width.of("20px"))
 *             .height(Height.of("20px"))
 *             .color(Color.Muted)
 *     )
 * }
 * ```
 *
 * ## Form Field Help Pattern
 * ```kotlin
 * @Composable
 * fun FormFieldWithHelp() {
 *     Row(
 *         modifier = Modifier()
 *             .style("align-items", "center")
 *             .style("gap", "8px")
 *     ) {
 *         TextField(
 *             value = value,
 *             onValueChange = { value = it },
 *             label = "Email Address",
 *             modifier = Modifier().flexGrow(1)
 *         )
 *
 *         Tooltip(
 *             tooltipContent = {
 *                 Text(
 *                     text = "We'll never share your email address with third parties.",
 *                     modifier = Modifier()
 *                         .style("max-width", "200px")
 *                         .fontSize(FontSize.SM)
 *                 )
 *             },
 *             placement = TooltipPlacement.TOP
 *         ) {
 *             Icon(
 *                 name = "info",
 *                 modifier = Modifier()
 *                     .width(Width.of("16px"))
 *                     .height(Height.of("16px"))
 *                     .style("cursor", "help")
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * ## Interactive Tooltip Pattern
 * ```kotlin
 * // Tooltip that appears on click for touch devices
 * Tooltip(
 *     tooltipContent = {
 *         Column(modifier = Modifier().padding(Spacing.MD)) {
 *             Text("Quick Actions")
 *             Button(
 *                 onClick = { performAction() },
 *                 modifier = Modifier().margin(Margin.top(Spacing.XS))
 *             ) {
 *                 Text("Perform Action")
 *             }
 *         }
 *     },
 *     placement = TooltipPlacement.BOTTOM,
 *     showOnClick = true,
 *     showDelay = 0
 * ) {
 *     Button(
 *         onClick = { /* primary action */ },
 *         modifier = Modifier().style("position", "relative")
 *     ) {
 *         Text("Options")
 *     }
 * }
 * ```
 *
 * ## Accessibility Features
 * - **Keyboard navigation**: Tooltips appear on focus and are keyboard accessible
 * - **Screen reader support**: Proper ARIA attributes and announcements
 * - **High contrast**: Adapts to system high contrast modes
 * - **Focus management**: Doesn't interfere with natural tab order
 * - **Touch support**: Appropriate behavior for touch interactions
 * - **ESC key support**: Tooltips can be dismissed with escape key
 *
 * ## Design Guidelines
 * - Keep tooltip content concise and helpful
 * - Use appropriate placement to avoid covering important content
 * - Consider delay timing for optimal user experience
 * - Provide tooltips for icons and unfamiliar interface elements
 * - Ensure tooltips work well on both desktop and mobile
 * - Use consistent styling across your application
 *
 * ## Performance Considerations
 * - Tooltips are rendered on-demand when shown
 * - Positioning calculations are optimized for performance
 * - Automatic cleanup prevents memory leaks
 * - Minimal DOM impact when not visible
 *
 * @param tooltipContent Composable content to display inside the tooltip popup. Can include text, icons, and complex layouts.
 * @param modifier Modifier applied to the wrapper element containing the trigger content.
 * @param placement Preferred placement of the tooltip popup relative to the trigger content.
 * @param showArrow Whether to display a visual arrow pointing from the tooltip to the trigger element.
 * @param showDelay Delay in milliseconds before showing the tooltip after hover/focus begins.
 * @param hideDelay Delay in milliseconds before hiding the tooltip after hover/focus ends.
 * @param showOnClick Whether the tooltip should also appear when the trigger is clicked (useful for touch devices).
 * @param trigger The composable content that triggers the tooltip when interacted with.
 *
 * @see TooltipPlacement for positioning options
 * @sample code.yousef.summon.samples.feedback.TooltipSamples.basicUsage
 * @sample code.yousef.summon.samples.feedback.TooltipSamples.formFieldHelp
 * @sample code.yousef.summon.samples.feedback.TooltipSamples.richContent
 * @since 1.0.0
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
        TooltipPlacement.TOP -> mapOfCompat(
            "bottom" to "100%",
            "margin-bottom" to "10px",
            "left" to "50%",
            "transform" to "translateX(-50%)"
        )

        TooltipPlacement.RIGHT -> mapOfCompat(
            "left" to "100%",
            "margin-left" to "10px",
            "top" to "50%",
            "transform" to "translateY(-50%)"
        )

        TooltipPlacement.BOTTOM -> mapOfCompat(
            "top" to "100%",
            "margin-top" to "10px",
            "left" to "50%",
            "transform" to "translateX(-50%)"
        )

        TooltipPlacement.LEFT -> mapOfCompat(
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
    val base = mapOfCompat(
        "position" to "absolute",
        "width" to "0",
        "height" to "0",
        "border-style" to "solid"
    )

    return when (placement) {
        TooltipPlacement.TOP -> base + mapOfCompat(
            "bottom" to "-6px",
            "left" to "50%",
            "transform" to "translateX(-50%)",
            "border-width" to "6px 6px 0 6px",
            "border-color" to "#333 transparent transparent transparent"
        )

        TooltipPlacement.RIGHT -> base + mapOfCompat(
            "left" to "-6px",
            "top" to "50%",
            "transform" to "translateY(-50%)",
            "border-width" to "6px 6px 6px 0",
            "border-color" to "transparent #333 transparent transparent"
        )

        TooltipPlacement.BOTTOM -> base + mapOfCompat(
            "top" to "-6px",
            "left" to "50%",
            "transform" to "translateX(-50%)",
            "border-width" to "0 6px 6px 6px",
            "border-color" to "transparent transparent #333 transparent"
        )

        TooltipPlacement.LEFT -> base + mapOfCompat(
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
    return mapOfCompat(
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
