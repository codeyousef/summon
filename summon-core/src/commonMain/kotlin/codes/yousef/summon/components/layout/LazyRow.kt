package codes.yousef.summon.components.layout

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember

/**
 * A horizontally scrolling list that only composes and lays out the currently visible items.
 *
 * @param modifier The modifier to be applied to the LazyRow
 * @param state Optional state object that controls and observes scrolling
 * @param content The content lambda defining the children, scoped to `LazyListScope`.
 */
@Composable
fun LazyRow(
    modifier: Modifier = Modifier(),
    state: LazyListState = remember { LazyListState() },
    content: LazyListScope.() -> Unit
) {
    val composer = CompositionLocal.currentComposer

    // Create the scope first to collect item definitions
    val scope = LazyListScopeImpl()
    scope.content() // Execute user lambda to populate scope.items

    // Add data attributes and styles to the modifier
    val dataAttributes = state.getDataAttributes(scope.items.size)
    var finalModifier = modifier.then(
        Modifier()
            .style("overflow-x", "auto")
            .style("display", "flex")
            .style("flex-direction", "row")
            .style("max-width", "100%")
            .dataAttribute("direction", "row")
            .dataAttributes(dataAttributes)
    )

    val renderer = LocalPlatformRenderer.current

    // Calculate the visible range based on the current scroll position
    val visibleRange = state.getVisibleItemRange(state.containerSize, scope.items.size)

    renderer.renderLazyRow(
        modifier = finalModifier,
        onScroll = { scrollPosition, containerSize ->
            state.updateScrollPosition(scrollPosition)
            state.setContainerSize(containerSize)
        },
        content = { // 'this' is FlowContent scope from the renderer
            // Only render items that are in the visible range
            for (i in visibleRange) {
                if (i >= 0 && i < scope.items.size) {
                    // Create a wrapper for the item with appropriate attributes
                    val key = scope.keys.getOrNull(i)
                    val isHeader = scope.isHeader.getOrNull(i) ?: false
                    val isDivider = scope.isDivider.getOrNull(i) ?: false
                    val spacing = scope.spacings.getOrNull(i) ?: (0f to 0f)

                    // Add data attributes for the item
                    val itemModifier = Modifier()
                        .dataAttribute("item-index", i.toString())
                        .dataAttribute("item-key", key?.toString() ?: "")
                        .style("margin-top", "${spacing.first}px")
                        .style("margin-left", "${spacing.second}px")

                    // Add sticky header styling if needed
                    if (isHeader) {
                        itemModifier
                            .style("position", "sticky")
                            .style("left", "0")
                            .style("z-index", "1")
                            .style("background-color", "inherit")
                    }

                    // Add section divider styling if needed
                    if (isDivider) {
                        itemModifier
                            .style("height", "100%")
                            .style("data-section-divider", "true")
                    }

                    // Render the item with its wrapper
                    renderer.renderDiv(
                        modifier = itemModifier,
                        content = {
                            // Execute the composable lambda provided for the visible item
                            scope.items[i]()
                        }
                    )
                }
            }
        }
    )
}
