package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

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

    // For now, we'll use a simple approach without JavaScript event handlers
    // This will be enhanced in a future implementation
    val finalModifier = modifier.then(
        Modifier()
            .style("overflow-x", "auto")
            .style("display", "flex")
            .style("flex-direction", "row")
            .style("max-width", "100%")
    )

    // Now render the container, passing a content lambda that 
    // will compose the collected items (or a subset in a real impl).
    val renderer = LocalPlatformRenderer.current

    // Calculate the visible range based on the current scroll position
    // In a real implementation, this would be updated when the scroll position changes
    val containerSize = 600f
    val visibleRange = state.getVisibleItemRange(containerSize, scope.items.size)

    renderer.renderLazyRow(
        modifier = finalModifier,
        content = { // 'this' is FlowContent scope from the renderer
            // Only render items that are in the visible range
            for (i in visibleRange) {
                if (i >= 0 && i < scope.items.size) {
                    // Execute the composable lambda provided for the visible item
                    scope.items[i]()
                }
            }
        }
    )
}
