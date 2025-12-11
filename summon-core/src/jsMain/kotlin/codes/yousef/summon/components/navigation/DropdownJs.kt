package codes.yousef.summon.components.navigation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * JS-specific dropdown implementation with inline JS for event handling
 */
@Composable
actual fun Dropdown(
    trigger: @Composable FlowContent.() -> Unit,
    modifier: Modifier,
    triggerBehavior: DropdownTrigger,
    alignment: DropdownAlignment,
    closeOnItemClick: Boolean,
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    val menuId = "dropdown-menu-${js("Math.random().toString(36).substring(2, 10)") as String}"

    // Container gets hover events to avoid the "gap" problem when moving from trigger to menu
    val containerModifier = modifier
        .style("position", "relative")
        .style("display", "inline-block")
        .ariaHasPopup(true)
        .dataAttribute("dropdown-container", "true")
        .apply {
            if (triggerBehavior == DropdownTrigger.HOVER || triggerBehavior == DropdownTrigger.BOTH) {
                onMouseEnter("document.getElementById('$menuId').style.display='block'")
                onMouseLeave("document.getElementById('$menuId').style.display='none'")
            }
        }

    renderer.renderBlock(containerModifier) {
        // Trigger element
        Box(
            modifier = Modifier()
                .style("cursor", "pointer")
                .ariaControls(menuId)
                .role("button")
                .tabIndex(0)
                .dataAttribute("dropdown-trigger", "true")
                .apply {
                    if (triggerBehavior == DropdownTrigger.CLICK || triggerBehavior == DropdownTrigger.BOTH) {
                        onClick(
                            """
                            const menu = document.getElementById('$menuId');
                            menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
                            event.stopPropagation();
                        """.trimIndent()
                        )
                    }
                }
        ) {
            trigger()
        }

        // Dropdown menu
        Box(
            modifier = Modifier()
                .id(menuId)
                .dataAttribute("dropdown-menu", "true")
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
                    // Note: Hover events are on the container, not here, to avoid gap issues
                    if (closeOnItemClick) {
                        onClick("this.style.display='none'")
                    }
                }
                .role("menu")
        ) {
            content()
        }
    }
}

