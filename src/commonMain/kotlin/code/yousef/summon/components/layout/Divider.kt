package code.yousef.summon.components.layout

import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * A composable that displays a horizontal or vertical divider line for visual separation.
 *
 * @param isVertical Whether the divider should be vertical (true) or horizontal (false)
 * @param thickness The thickness of the divider in pixels or other CSS units
 * @param color The color of the divider
 * @param length The length of the divider (width for horizontal, height for vertical)
 * @param modifier The modifier to apply to this composable
 */
class Divider(
    val isVertical: Boolean = false,
    val thickness: String = "1px",
    val color: String = "#CCCCCC",
    val length: String = "100%",
    val modifier: Modifier = Modifier()
) : Composable {
    /**
     * Renders this Divider composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderDivider(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 