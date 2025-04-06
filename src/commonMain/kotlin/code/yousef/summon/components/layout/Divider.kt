package code.yousef.summon.components.layout

import code.yousef.summon.runtime.Composable
import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier

/**
 * A horizontal or vertical divider line that separates content.
 *
 * @param modifier Modifier to be applied to the divider
 * @param vertical Whether the divider is vertical (true) or horizontal (false)
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier(),
    vertical: Boolean = false
) {
    // Apply default styling that can be overridden by modifier
    val finalModifier = modifier
        // Apply vertical styling if needed
        .let { if (vertical) it.height("100%").width("1px") else it.width("100%").height("1px") }
    
    val renderer = getPlatformRenderer()
    renderer.renderDivider(finalModifier)
} 