package code.yousef.summon.components.layout

import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * A horizontal or vertical divider line that separates content.
 *
 * @param modifier Modifier to be applied to the divider
 * @param color The color of the divider
 * @param thickness The thickness of the divider
 * @param vertical Whether the divider is vertical (true) or horizontal (false)
 */
@Composable
fun Divider(
    modifier: Modifier = Modifier(),
    color: Color = Color.LightGray,
    thickness: Int = 1,
    vertical: Boolean = false
) {
    val platformRenderer = PlatformRendererProvider.getPlatformRenderer()
    
    platformRenderer.renderDivider(
        modifier = modifier,
        color = color,
        thickness = thickness,
        vertical = vertical
    )
} 
