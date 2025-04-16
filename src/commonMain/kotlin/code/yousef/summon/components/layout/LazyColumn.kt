package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

/**
 * State holder for lazy lists that tracks scroll position and visible items.
 */
class LazyListState {
    // Current scroll position in pixels
    private val _scrollPosition = mutableStateOf(0f)
    val scrollPosition: Float
        get() = _scrollPosition.value

    // Estimated height/width of each item in pixels (can be refined later)
    private val _itemSize = mutableStateOf(50f)
    val itemSize: Float
        get() = _itemSize.value

    // Number of items to render above and below the visible area for smooth scrolling
    private val _overscrollItems = mutableStateOf(2)
    val overscrollItems: Int
        get() = _overscrollItems.value

    // Calculate which items should be visible based on container size and scroll position
    fun getVisibleItemRange(containerSize: Float, totalItems: Int): IntRange {
        val visibleItemCount = (containerSize / itemSize).toInt() + 1 // +1 to handle partial items
        val firstVisibleItem = (scrollPosition / itemSize).toInt() - overscrollItems
        val lastVisibleItem = firstVisibleItem + visibleItemCount + (2 * overscrollItems)

        return IntRange(
            firstVisibleItem.coerceAtLeast(0),
            lastVisibleItem.coerceAtMost(totalItems - 1)
        )
    }

    // Update scroll position (would be called from JS)
    fun updateScrollPosition(newPosition: Float) {
        _scrollPosition.value = newPosition
    }

    // Set item size
    fun setItemSize(size: Float) {
        _itemSize.value = size
    }

    // Set overscroll items
    fun setOverscrollItems(count: Int) {
        _overscrollItems.value = count
    }
}

/**
 * A vertically scrolling list that only composes and lays out the currently visible items.
 *
 * @param modifier The modifier to be applied to the LazyColumn
 * @param state Optional state object that controls and observes scrolling
 * @param content The content lambda defining the children, scoped to `LazyListScope`.
 */
@Composable
fun LazyColumn(
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
            .style("overflow-y", "auto")
            .style("display", "flex")
            .style("flex-direction", "column")
            .style("max-height", "100%")
    )

    // Now render the container, passing a content lambda that 
    // will compose the collected items (or a subset in a real impl).
    val renderer = LocalPlatformRenderer.current

    // Calculate the visible range based on the current scroll position
    // In a real implementation, this would be updated when the scroll position changes
    val containerSize = 600f
    val visibleRange = state.getVisibleItemRange(containerSize, scope.items.size)

    renderer.renderLazyColumn(
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

// The old LazyColumn class and its methods are removed. 
