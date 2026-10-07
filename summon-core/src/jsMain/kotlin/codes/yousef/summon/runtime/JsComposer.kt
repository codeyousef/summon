package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable

// Console access provided by WebDOMUtils console object

/**
 * JS implementation of the Composer interface.
 * This provides JS-specific functionality for the composition system.
 */
class JsComposer : Composer {
    /** The true value. */
    override val inserting: Boolean = true

    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    private var currentNodeIndex = 0
    private val nodeStack = mutableListOf<Int>()
    private val groupStack = mutableListOf<Any?>()
    private val stateReads = mutableSetOf<Any>()
    private val stateWrites = mutableSetOf<Any>()
    private val disposables = mutableListOf<() -> Unit>()

    /** Starts node. */
    override fun startNode() {
        nodeStack.add(currentNodeIndex++)
    }

    /** Executes the end node operation. */
    override fun endNode() {
        nodeStack.removeAt(nodeStack.size - 1)
    }

    /**
     * Starts group.
     *
     * @param key Lookup key.
     */
    override fun startGroup(key: Any?) {
        groupStack.add(key)
        // Save the current slot index so we can restore it when the group ends
        slots[slotIndex] = slotIndex
        slotIndex++
    }

    /** Executes the end group operation. */
    override fun endGroup() {
        groupStack.removeAt(groupStack.size - 1)
        // Restore the slot index from when the group started
        slotIndex = (slots[slotIndex - 1] as? Int) ?: slotIndex
    }

    /**
     * Executes the changed operation.
     *
     * @param value Value to process.
     * @return The resulting value.
     */
    override fun changed(value: Any?): Boolean {
        val slotValue = getSlot()
        val hasChanged = slotValue != value
        if (hasChanged) {
            setSlot(value)
        }
        return hasChanged
    }

    /**
     * Updates value.
     *
     * @param value Value to process.
     */
    override fun updateValue(value: Any?) {
        setSlot(value)
    }

    /** Executes the next slot operation. */
    override fun nextSlot() {
        slotIndex++
    }

    /**
     * Returns slot.
     *
     * @return The resulting value.
     */
    override fun getSlot(): Any? {
        return slots[slotIndex]
    }

    /**
     * Sets slot.
     *
     * @param value Value to process.
     */
    override fun setSlot(value: Any?) {
        slots[slotIndex] = value
    }

    /**
     * Executes the record read operation.
     *
     * @param state The state value.
     */
    override fun recordRead(state: Any) {
        stateReads.add(state)
    }

    /**
     * Executes the record write operation.
     *
     * @param state The state value.
     */
    override fun recordWrite(state: Any) {
        stateWrites.add(state)
        reportChanged()
    }

    /** Executes the report changed operation. */
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

    /**
     * Registers disposable.
     *
     * @param disposable The disposable value.
     */
    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }

    /** Executes the recompose operation. */
    override fun recompose() {
        reportChanged()
    }

    /**
     * Executes the remembered value operation.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    override fun rememberedValue(key: Any): Any? {
        return slots[key.hashCode()]
    }

    /**
     * Updates remembered value.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
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
        /**
         * Creates the operation.
         *
         * @return The resulting value.
         */
        fun create(): JsComposer {
            return JsComposer()
        }
    }
}
