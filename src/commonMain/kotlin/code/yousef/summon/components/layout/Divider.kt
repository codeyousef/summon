package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A composable that displays a horizontal or vertical divider line for visual separation.
 *
 * @param isVertical Whether the divider should be vertical (true) or horizontal (false)
 * @param thickness The thickness of the divider in pixels or other CSS units
 * @param color The color of the divider
 * @param length The length of the divider (width for horizontal, height for vertical)
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun Divider(
    isVertical: Boolean = false,
    thickness: String = "1px",
    color: String = "#CCCCCC",
    length: String = "100%",
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderDivider(modifier)
}
