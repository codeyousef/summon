package code.yousef.summon.components.layout

import code.yousef.summon.core.Composable
import code.yousef.summon.LayoutComponent
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.ScrollableComponent
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * A layout composable that positions its children using absolute positioning.
 * Box can be used for stacking elements, overlays, and precise positioning.
 *
 * @param content The composables to display inside the Box
 * @param modifier The modifier to apply to this composable
 */
class Box(
    val content: List<Composable>,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent, ScrollableComponent {
    /**
     * Convenience constructor to create a Box with a single child.
     */
    constructor(
        content: Composable,
        modifier: Modifier = Modifier()
    ) : this(listOf(content), modifier)

    /**
     * Renders this Box composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderBox(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 