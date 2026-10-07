package codes.yousef.summon.components.navigation

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.foundation.TrustedCss
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ID generation for Dropdown using random numbers to avoid counter synchronization issues.
// During SSR without a composer, composable functions may be invoked multiple times,
// and a global counter would get out of sync. Random IDs ensure each dropdown gets a unique ID.
private fun generateDropdownId(): String = "dropdown-menu-${kotlin.random.Random.nextInt(100000, 999999)}"

/**
 * JVM-specific dropdown implementation (SSR-compatible)
 * Uses data-action for client-side toggle via GlobalEventListener/ClientDispatcher
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

    // Generate unique ID for this dropdown instance.
    // We use remember to ensure stability across recompositions when a composer is available.
    val menuId = remember { generateDropdownId() }

    // Serialize the toggle action for client-side handling
    // Use the polymorphic serializer to include the type discriminator so ClientDispatcher can decode it
    val toggleAction: UiAction = UiAction.ToggleVisibility(menuId)
    val actionJson = Json.encodeToString(UiAction.serializer(), toggleAction)

    if (triggerBehavior == DropdownTrigger.HOVER || triggerBehavior == DropdownTrigger.BOTH) {
        renderer.renderGlobalStyle(
            TrustedCss.fromAuthorCode(
                "#$menuId:hover, [data-dropdown-container=\"true\"]:hover #$menuId { display: block; }"
            )
        )
    }

    val containerModifier = modifier
        .style("position", "relative")
        .style("display", "inline-block")
        .ariaHasPopup(true)
        .dataAttribute("dropdown-container", "true")

    renderer.renderBlock(containerModifier) {
        // Trigger element
        Box(
            modifier = Modifier()
                .style("cursor", "pointer")
                .ariaControls(menuId)
                .role("button")
                .tabIndex(0)
                .dataAttribute("dropdown-trigger", "true")
                .let { mod ->
                    if (triggerBehavior == DropdownTrigger.CLICK || triggerBehavior == DropdownTrigger.BOTH) {
                        // Use data-action for client-side toggle via GlobalEventListener
                        mod.attribute("data-action", actionJson)
                    } else {
                        mod
                    }
                }
        ) {
            trigger()
        }

        // Dropdown menu
        var menuModifier = Modifier()
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
        menuModifier = when (alignment) {
            DropdownAlignment.LEFT -> menuModifier.style("left", "0")
            DropdownAlignment.RIGHT -> menuModifier.style("right", "0")
            DropdownAlignment.CENTER -> menuModifier
                .style("left", "50%")
                .style("transform", "translateX(-50%)")
        }
        if (closeOnItemClick) {
            val closeAction: UiAction = UiAction.ToggleVisibility(menuId)
            menuModifier = menuModifier.attribute(
                "data-action",
                Json.encodeToString(UiAction.serializer(), closeAction)
            )
        }
        Box(modifier = menuModifier.role("menu")) {
            content()
        }
    }
}

