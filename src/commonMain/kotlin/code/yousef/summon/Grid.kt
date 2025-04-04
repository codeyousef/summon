package code.yousef.summon

import kotlinx.html.TagConsumer

/**
 * A layout composable that arranges its children in a grid using CSS Grid.
 * Grid provides a powerful way to create responsive two-dimensional layouts.
 *
 * @param content The composables to display inside the grid
 * @param columns The number of columns or a template string (e.g. "1fr 2fr 1fr" or "repeat(3, 1fr)")
 * @param rows The number of rows or a template string (e.g. "auto 1fr auto" or "repeat(2, minmax(100px, auto))")
 * @param gap The gap between grid items (e.g. "10px" or "10px 20px" for row/column gaps)
 * @param areas Optional grid template areas definition
 * @param modifier The modifier to apply to this composable
 */
class Grid(
    val content: List<Composable>,
    val columns: String,
    val rows: String = "auto",
    val gap: String = "0",
    val areas: String? = null,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent, ScrollableComponent {
    /**
     * Renders this Grid composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderGrid(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 