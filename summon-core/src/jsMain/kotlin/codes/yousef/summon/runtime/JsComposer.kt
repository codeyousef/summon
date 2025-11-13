package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable

// Console access provided by WebDOMUtils console object

/**
 * JS implementation of the Composer interface.
 * This provides JS-specific functionality for the composition system.
 */
class JsComposer : Composer {
    override val inserting: Boolean = true

    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    private var currentNodeIndex = 0
    private val nodeStack = mutableListOf<Int>()
    private val groupStack = mutableListOf<Any?>()
    private val stateReads = mutableSetOf<Any>()
    private val stateWrites = mutableSetOf<Any>()
    private val disposables = mutableListOf<() -> Unit>()

    override fun startNode() {
        nodeStack.add(currentNodeIndex++)
    }

    override fun endNode() {
        nodeStack.removeAt(nodeStack.size - 1)
    }

    override fun startGroup(key: Any?) {
        groupStack.add(key)
        // Save the current slot index so we can restore it when the group ends
        slots[slotIndex] = slotIndex
        slotIndex++
    }

    override fun endGroup() {
        groupStack.removeAt(groupStack.size - 1)
        // Restore the slot index from when the group started
        slotIndex = (slots[slotIndex - 1] as? Int) ?: slotIndex
    }

    override fun changed(value: Any?): Boolean {
        val slotValue = getSlot()
        val hasChanged = slotValue != value
        if (hasChanged) {
            setSlot(value)
        }
        return hasChanged
    }

    override fun updateValue(value: Any?) {
        setSlot(value)
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
        stateWrites.add(state)
        reportChanged()
    }

    override fun reportChanged() {
        // Schedule recomposition in the JS environment
        // In a real implementation, this would schedule a recomposition
        // using requestAnimationFrame and dispatch a custom event

        // For now, we'll simply log that a change occurred
        js("console.log('State changed, recomposition needed')")

        // In a real implementation, we would do something like:
        // 1. Schedule recomposition for the next animation frame
        // 2. Notify all components that depend on the changed state
        // 3. Trigger the actual recomposition process
    }

    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }

    override fun recompose() {
        reportChanged()
    }

    override fun rememberedValue(key: Any): Any? {
        return slots[key.hashCode()]
    }

    override fun updateRememberedValue(key: Any, value: Any?) {
        slots[key.hashCode()] = value
    }

    /**
     * Disposes all registered disposables.
     * This would be called when the composition is destroyed.
     */
    override fun dispose() {
        disposables.forEach { it() }
        disposables.clear()
        slots.clear()
        stateReads.clear()
        stateWrites.clear()
    }

    /**
     * Start composing a composable
     */
    override fun startCompose() {
        // Implementation delegates to startNode
        startNode()
    }

    /**
     * End composing a composable
     */
    override fun endCompose() {
        // Implementation delegates to endNode
        endNode()
    }

    /**
     * Execute a composable within this composer's context
     */
    override fun <T> compose(composable: @Composable () -> T): T {
        startCompose()
        try {
            return composable()
        } finally {
            endCompose()
        }
    }

    /**
     * Factory method to create a JsComposer.
     */
    companion object {
        fun create(): JsComposer {
            return JsComposer()
        }
    }
} 
