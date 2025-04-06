package code.yousef.summon.components.layout

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.ComposableDsl

/**
 * A scope for LazyColumn and LazyRow content.
 * Allows defining items and item content.
 */
@ComposableDsl
interface LazyListScope {
    /**
     * Add a single item to the list.
     */
    fun item(content: @Composable () -> Unit)
    
    /**
     * Add multiple items to the list.
     */
    fun <T> items(items: List<T>, itemContent: @Composable (item: T) -> Unit)
    
    /**
     * Add multiple indexed items to the list.
     */
    fun <T> itemsIndexed(items: List<T>, itemContent: @Composable (index: Int, item: T) -> Unit)
}

/**
 * Implementation of LazyListScope.
 */
private class LazyListScopeImpl : LazyListScope {
    val items = mutableListOf<@Composable () -> Unit>()
    
    override fun item(content: @Composable () -> Unit) {
        items.add(content)
    }
    
    override fun <T> items(items: List<T>, itemContent: @Composable (item: T) -> Unit) {
        for (item in items) {
            this.items.add { itemContent(item) }
        }
    }
    
    override fun <T> itemsIndexed(items: List<T>, itemContent: @Composable (index: Int, item: T) -> Unit) {
        for (i in items.indices) {
            this.items.add { itemContent(i, items[i]) }
        }
    }
}

/**
 * A vertically scrolling list that only composes and lays out the currently visible items.
 * 
 * @param modifier The modifier to be applied to the LazyColumn
 * @param content The content to be displayed inside the LazyColumn using LazyListScope
 */
@Composable
fun LazyColumn(
    modifier: Modifier = Modifier(),
    content: LazyListScope.() -> Unit
) {
    val renderer = getPlatformRenderer()
    renderer.renderLazyColumn(modifier)
    
    // Create a scope and collect items
    val scope = LazyListScopeImpl()
    scope.content()
    
    // Render all items (in a real implementation, only visible items would be rendered)
    scope.items.forEach { item ->
        item()
    }
}

// The old LazyColumn class and its methods are removed. 