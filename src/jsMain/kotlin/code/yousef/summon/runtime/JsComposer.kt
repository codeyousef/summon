package code.yousef.summon.runtime

/**
 * JS implementation of the Composer interface.
 * This provides JS-specific functionality for the composition system.
 */
class JsComposer : Composer {
    override val inserting: Boolean = true
    
    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    private val stateReads = mutableSetOf<Any>()
    private val disposables = mutableListOf<() -> Unit>()
    
    override fun startNode() {
        // JS implementation of start node
    }
    
    override fun endNode() {
        // JS implementation of end node
    }
    
    override fun startGroup(key: Any?) {
        // JS implementation of start group
    }
    
    override fun endGroup() {
        // JS implementation of end group
    }
    
    override fun changed(value: Any?): Boolean {
        // JS implementation would compare with previous value
        return true
    }
    
    override fun updateValue(value: Any?) {
        // JS implementation would store the value
    }
    
    override fun nextSlot() {
        slotIndex++
    }
    
    override fun getSlot(): Any? {
        return slots[slotIndex]
    }
    
    override fun setSlot(value: Any?) {
        slots[slotIndex] = value
    }
    
    override fun recordRead(state: Any) {
        stateReads.add(state)
    }
    
    override fun recordWrite(state: Any) {
        // JS implementation would trigger recomposition for affected compositions
    }
    
    override fun reportChanged() {
        // JS implementation would trigger recomposition
    }
    
    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }
    
    /**
     * Disposes all registered disposables.
     * This would be called when the composition is destroyed.
     */
    override fun dispose() {
        disposables.forEach { it() }
        disposables.clear()
    }
    
    /**
     * Factory method to create a JsComposer.
     */
    companion object {
        fun create(): JsComposer {
            return JsComposer()
        }
    }
    
    /**
     * Implementation of renderComposable for the JS platform
     */
    override fun <T> renderComposable(composable: Composable, consumer: T) {
        // Call compose on the composable with the provided consumer
        composable.compose(consumer)
    }
} 
