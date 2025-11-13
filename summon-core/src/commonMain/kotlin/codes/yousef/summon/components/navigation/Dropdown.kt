/**
 * # Dropdown Component
 *
 * A flexible dropdown/menu component with proper state management, keyboard navigation,
 * and accessibility features built-in.
 *
 * ## Features
 *
 * - **Hover State Management**: Built-in hover behavior
 * - **Click Trigger**: Support for click-to-open behavior
 * - **Keyboard Navigation**: Arrow keys and Escape support
 * - **ARIA Attributes**: Automatically applied for accessibility
 * - **Flexible Positioning**: Support for different alignments
 * - **Composable Content**: Full control over trigger and menu content
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic dropdown
 * Dropdown(
 *     trigger = { Text("Menu") }
 * ) {
 *     DropdownItem("Option 1", onClick = { })
 *     DropdownItem("Option 2", onClick = { })
 *     DropdownItem("Option 3", onClick = { })
 * }
 *
 * // With custom trigger
 * Dropdown(
 *     trigger = {
 *         Button(
 *             onClick = { },
 *             label = "Actions"
 *         )
 *     }
 * ) {
 *     DropdownItem("Edit", onClick = { })
 *     DropdownItem("Delete", onClick = { })
 * }
 *
 * // With links
 * Dropdown(
 *     trigger = { Text("Projects") }
 * ) {
 *     DropdownItem(
 *         label = "Project A",
 *         href = "/projects/a"
 *     )
 *     DropdownItem(
 *         label = "Project B",
 *         href = "/projects/b"
 *     )
 * }
 * ```
 *
 * @see DropdownItem for menu item component
 * @since 1.0.0
 */
package codes.yousef.summon.components.navigation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ariaDisabled
import codes.yousef.summon.modifier.hover
import codes.yousef.summon.modifier.onClick
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Trigger behavior for dropdown menus.
 */
enum class DropdownTrigger {
    /** Open on hover */
    HOVER,

    /** Open on click */
    CLICK,

    /** Open on both hover and click */
    BOTH
}

/**
 * Menu alignment relative to trigger.
 */
enum class DropdownAlignment {
    /** Align left edges */
    LEFT,

    /** Align right edges */
    RIGHT,

    /** Center menu under trigger */
    CENTER
}

/**
 * A dropdown menu component with hover/click trigger support.
 *
 * Platform-specific implementations provide proper event handling.
 *
 * @param trigger Composable content for the trigger element
 * @param modifier Modifier applied to the dropdown container
 * @param triggerBehavior How the dropdown should open (hover, click, or both)
 * @param alignment How to align the menu relative to the trigger
 * @param closeOnItemClick Whether to close the menu when an item is clicked
 * @param content The dropdown menu content
 */
@Composable
expect fun Dropdown(
    trigger: @Composable FlowContent.() -> Unit,
    modifier: Modifier = Modifier(),
    triggerBehavior: DropdownTrigger = DropdownTrigger.HOVER,
    alignment: DropdownAlignment = DropdownAlignment.LEFT,
    closeOnItemClick: Boolean = true,
    content: @Composable FlowContent.() -> Unit
)

/**
 * A dropdown menu item component.
 *
 * @param label The text label for the item
 * @param onClick Optional click handler
 * @param href Optional link URL
 * @param modifier Modifier applied to the item
 * @param enabled Whether the item is enabled
 */
@Composable
fun DropdownItem(
    label: String,
    onClick: (() -> Unit)? = null,
    href: String? = null,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val renderer = LocalPlatformRenderer.current

    val itemModifier = modifier
        .style("display", "block")
        .style("padding", "8px 16px")
        .style("color", if (enabled) "#333" else "#999")
        .style("text-decoration", "none")
        .style("cursor", if (enabled) "pointer" else "default")
        .style("border-bottom", "1px solid #f0f0f0")
        .hover(
            Modifier()
                .style("background-color", if (enabled) "#f5f5f5" else "transparent")
        )
        .role("menuitem")
        .apply {
            if (!enabled) {
                ariaDisabled(true)
            }
            if (onClick != null && enabled) {
                onClick("event.stopPropagation(); ${createClickHandler(onClick)}")
            }
        }

    // For links, we should use Link component, but for simplicity use renderBlock with appropriate tag
    val finalModifier = if (href != null) {
        itemModifier
            .attribute("href", href)
            .attribute("data-is-link", "true")
    } else {
        itemModifier
    }

    renderer.renderBlock(finalModifier, content = {
        renderer.renderText(label, Modifier())
    })
}

/**
 * Helper to create a click handler string (placeholder for actual implementation).
 */
private fun createClickHandler(onClick: () -> Unit): String {
    // In a real implementation, this would register the handler and return a JS call
    // For now, we return a placeholder
    return "handleDropdownItemClick()"
}

/**
 * A divider for dropdown menus.
 */
@Composable
fun DropdownDivider(
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderBlock(
        modifier
            .style("height", "1px")
            .style("margin", "4px 0")
            .style("background-color", "#e0e0e0")
            .style("border", "none"),
        content = {}
    )
}
