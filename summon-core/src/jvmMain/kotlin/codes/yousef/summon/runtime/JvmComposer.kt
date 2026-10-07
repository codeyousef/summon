package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * JVM implementation of the Composer interface.
 * This provides JVM-specific functionality for the composition system.
 */
class JvmComposer : Composer {
    /** The true value. */
    override val inserting: Boolean = true

    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    private var currentNodeIndex = 0
    private val nodeStack = mutableListOf<Int>()
    private val groupStack = mutableListOf<Any?>()
    private val stateReads = mutableSetOf<Any>()
    private val stateWriteListeners = ConcurrentHashMap<Any, CopyOnWriteArrayList<() -> Unit>>()
    private val disposables = mutableListOf<() -> Unit>()

    /** Starts node. */
    override fun startNode() {
        nodeStack.add(currentNodeIndex++)
    }

    /** Executes the end node operation. */
    override fun endNode() {
        if (nodeStack.isNotEmpty()) {
            nodeStack.removeAt(nodeStack.size - 1)
        }
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
        if (groupStack.isNotEmpty()) {
            groupStack.removeAt(groupStack.size - 1)
            // Restore the slot index from when the group started
            slotIndex = (slots[slotIndex - 1] as? Int) ?: slotIndex
        }
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
        // Trigger recomposition for all composers that depend on this state
        stateWriteListeners[state]?.forEach { it() }
        reportChanged()
    }

    /** Executes the report changed operation. */
    override fun reportChanged() {
        // Schedule recomposition on the JVM UI thread
        try {
            // Try to use SwingUtilities if available
            val swingUtilitiesClass = Class.forName("javax.swing.SwingUtilities")
            val invokeLaterMethod = swingUtilitiesClass.getMethod("invokeLater", Runnable::class.java)

            invokeLaterMethod.invoke(null, Runnable {
                // Trigger recomposition by notifying any registered listeners
                stateWriteListeners.values.forEach { listeners ->
                    listeners.forEach { it() }
                }
            })
        } catch (e: ClassNotFoundException) {
            // Swing not available, try to use JavaFX Platform.runLater if available
            try {
                val platformClass = Class.forName("javafx.application.Platform")
                val runLaterMethod = platformClass.getMethod("runLater", Runnable::class.java)

                runLaterMethod.invoke(null, Runnable {
                    // Trigger recomposition by notifying any registered listeners
                    stateWriteListeners.values.forEach { listeners ->
                        listeners.forEach { it() }
                    }
                })
            } catch (e: ClassNotFoundException) {
                // Neither Swing nor JavaFX available, use a simple direct call
                // This is not ideal for UI thread safety but provides a fallback
                stateWriteListeners.values.forEach { listeners ->
                    listeners.forEach { it() }
                }
            }
        }
    }

    /**
     * Registers disposable.
     *
     * @param disposable The disposable value.
     */
    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }

    /**
     * Registers a callback to be triggered when a specific state changes.
     * This is used for implementing recomposition.
     *
     * @param state The state to watch for changes
     * @param callback The callback to trigger when the state changes
     */
    fun registerStateListener(state: Any, callback: () -> Unit) {
        stateWriteListeners.computeIfAbsent(state) { CopyOnWriteArrayList() }.add(callback)
    }

    /**
     * Tracks dependencies between this composer and state objects.
     * Called after a composition to set up change listeners.
     */
    fun trackStateDependencies() {
        // Clear previous listeners related to this composer
        clearStateListeners()

        // Set up listeners for each state that was read during composition
        for (state in stateReads) {
            registerStateListener(state) {
                // Schedule recomposition
                reportChanged()
            }
        }
    }

    /**
     * Clears all state listeners associated with this composer.
     */
    private fun clearStateListeners() {
        // Remove this composer's callbacks from state write listeners
        stateWriteListeners.values.forEach { listeners -> listeners.clear() }
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
        clearStateListeners()
        nodeStack.clear()
        groupStack.clear()
    }

    /**
     * Start composing a composable
     */
    override fun startCompose() {
        // Reset state for a new composition
        currentNodeIndex = 0
        slotIndex = 0
        stateReads.clear()
        startNode()
    }

    /**
     * End composing a composable
     */
    override fun endCompose() {
        endNode()
        // After composition ends, set up change tracking for recomposition
        trackStateDependencies()
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
     * Factory method to create a JvmComposer.
     */
    companion object {
        /**
         * Creates the operation.
         *
         * @return The resulting value.
         */
        fun create(): JvmComposer {
            return JvmComposer()
        }
    }

    /**
     * Implementation of renderComposable for the JVM platform
     */
    @Suppress("DEPRECATION")
    fun <T> renderComposable(composable: codes.yousef.summon.core.Composable, consumer: T): T {
        // Call compose on the composable with the provided consumer
        return composable.compose(consumer)
    }
}
