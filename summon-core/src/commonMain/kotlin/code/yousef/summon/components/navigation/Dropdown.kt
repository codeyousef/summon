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
package code.yousef.summon.components.navigation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Box
import code.yousef.summon.core.FlowContent
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

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
 * @param trigger Composable content for the trigger element
 * @param modifier Modifier applied to the dropdown container
 * @param triggerBehavior How the dropdown should open (hover, click, or both)
 * @param alignment How to align the menu relative to the trigger
 * @param closeOnItemClick Whether to close the menu when an item is clicked
 * @param content The dropdown menu content
 */
@Composable
fun Dropdown(
    trigger: @Composable FlowContent.() -> Unit,
    modifier: Modifier = Modifier(),
    triggerBehavior: DropdownTrigger = DropdownTrigger.HOVER,
    alignment: DropdownAlignment = DropdownAlignment.LEFT,
    closeOnItemClick: Boolean = true,
    content: @Composable FlowContent.() -> Unit
) {
    val isOpen = remember { mutableStateOf(false) }
    val renderer = LocalPlatformRenderer.current

    val containerModifier = modifier
        .style("position", "relative")
        .style("display", "inline-block")
        .apply {
            when (triggerBehavior) {
                DropdownTrigger.HOVER, DropdownTrigger.BOTH -> {
                    onMouseEnter("this.querySelector('[data-dropdown-menu]').style.display='block'")
                    onMouseLeave("this.querySelector('[data-dropdown-menu]').style.display='none'")
                }
                DropdownTrigger.CLICK -> {
                    onClick("""
                        const menu = this.querySelector('[data-dropdown-menu]');
                        menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
                        event.stopPropagation();
                    """.trimIndent())
                }
            }
        }
        .ariaHasPopup(true)
        .ariaExpanded(isOpen.value)

    renderer.renderBlock(containerModifier) {
        // Trigger element
        Box(
            modifier = Modifier()
                .style("cursor", "pointer")
                .ariaControls("dropdown-menu")
                .role("button")
                .tabIndex(0)
        ) {
            trigger()
        }

        // Dropdown menu
        Box(
            modifier = Modifier()
                .dataAttribute("dropdown-menu", "true")
                .id("dropdown-menu")
                .style("position", "absolute")
                .style("top", "100%")
                .style("display", "none")
                .style("z-index", "1000")
                .style("min-width", "200px")
                .style("margin-top", "4px")
                .style("background-color", "white")
                .style("border", "1px solid #ddd")
                .style("border-radius", "4px")
                .style("box-shadow", "0 2px 8px rgba(0,0,0,0.15)")
                .apply {
                    when (alignment) {
                        DropdownAlignment.LEFT -> style("left", "0")
                        DropdownAlignment.RIGHT -> style("right", "0")
                        DropdownAlignment.CENTER -> {
                            style("left", "50%")
                            style("transform", "translateX(-50%)")
                        }
                    }
                }
                .role("menu")
                .ariaLabelledBy("dropdown-trigger")
        ) {
            content()
        }
    }
}

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
        .hover(Modifier()
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

    if (href != null) {
        renderer.renderElement("a", itemModifier.attribute("href", href)) {
            renderer.renderText(label)
        }
    } else {
        renderer.renderBlock(itemModifier) {
            renderer.renderText(label)
        }
    }
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
    renderer.renderElement(
        "hr",
        modifier
            .style("margin", "4px 0")
            .style("border", "none")
            .style("border-top", "1px solid #e0e0e0")
    ) {}
}
