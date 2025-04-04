package com.summon

import kotlinx.html.TagConsumer

/**
 * A layout composable that displays a vertical scrollable list with lazy loading.
 * LazyColumn only renders the items that are visible in the viewport, improving performance
 * for large lists. This is similar to RecyclerView in Android or a virtualized list in JS frameworks.
 *
 * @param items The list of items to be displayed
 * @param itemContent A function that produces a Composable for each item
 * @param modifier The modifier to apply to this composable
 */
class LazyColumn<T>(
    val items: List<T>,
    val itemContent: (T) -> Composable,
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent, ScrollableComponent {
    /**
     * Renders this LazyColumn composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T2> compose(receiver: T2): T2 {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderLazyColumn(this, receiver as TagConsumer<T2>)
        }
        return receiver
    }
} 