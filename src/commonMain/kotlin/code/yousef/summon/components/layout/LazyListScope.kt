package code.yousef.summon.components.layout

import code.yousef.summon.runtime.Composable
import runtime.ComposableDsl

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
 * Internal implementation of LazyListScope.
 * Accessible within the commonMain module.
 */
internal class LazyListScopeImpl : LazyListScope {
    // Changed from val to internal val to allow access within the module
    internal val items = mutableListOf<@Composable () -> Unit>() 
    
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
