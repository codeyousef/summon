package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable

/**
 * A simple implementation of Composer that can be used on any platform
 */
class CommonComposer : Composer {
    /** The true value. */
    override val inserting: Boolean = true

    private val slots = CompositionSlots()
    private var compositionDepth = 0

    /** Starts compose. */
    override fun startCompose() {
        startNode()
    }

    /** Executes the end compose operation. */
    override fun endCompose() {
        endNode()
    }

    /**
     * Composes the supplied content.
     *
     * @param composable The composable value.
     * @return The resulting value.
     */
    override fun <T> compose(composable: @Composable () -> T): T {
        val outermost = compositionDepth == 0
        if (outermost) slots.beginPass()
        compositionDepth++
        return CompositionLocal.provideComposer(this) {
            startCompose()
            try {
                val result = composable()
                if (outermost) slots.endPass()
                result
            } catch (error: Throwable) {
                if (outermost) {
                    try { slots.dispose() } catch (cleanupError: Throwable) { error.addSuppressed(cleanupError) }
                }
                throw error
            } finally {
                endCompose()
                compositionDepth--
            }
        }
    }

    /** Starts node. */
    override fun startNode() {
        // Simple implementation
    }

    /** Executes the end node operation. */
    override fun endNode() {
        // Simple implementation
    }

    /**
     * Starts group.
     *
     * @param key Lookup key.
     */
    override fun startGroup(key: Any?) {
        slots.startGroup(key)
    }

    /** Executes the end group operation. */
    override fun endGroup() {
        slots.endGroup()
    }

    /**
     * Executes the changed operation.
     *
     * @param value Value to process.
     * @return The resulting value.
     */
    override fun changed(value: Any?): Boolean {
        // Simple implementation
        return true
    }

    /**
     * Updates value.
     *
     * @param value Value to process.
     */
    override fun updateValue(value: Any?) {
        // Simple implementation
    }

    /** Executes the next slot operation. */
    override fun nextSlot() {
        slots.nextSlot()
    }

    /**
     * Returns slot.
     *
     * @return The resulting value.
     */
    override fun getSlot(): Any? {
        return slots.getSlot()
    }

    /**
     * Sets slot.
     *
     * @param value Value to process.
     */
    override fun setSlot(value: Any?) {
        slots.setSlot(value)
    }

    /**
     * Executes the record read operation.
     *
     * @param state The state value.
     */
    override fun recordRead(state: Any) {
        // Simple implementation
    }

    /**
     * Executes the record write operation.
     *
     * @param state The state value.
     */
    override fun recordWrite(state: Any) {
        // Simple implementation
    }

    /** Executes the report changed operation. */
    override fun reportChanged() {
        // Simple implementation
    }

    /**
     * Registers disposable.
     *
     * @param disposable The disposable value.
     */
    override fun registerDisposable(disposable: () -> Unit) {
        // Simple implementation
    }

    /** Executes the recompose operation. */
    override fun recompose() {
        // Simple implementation - trigger recomposition
    }

    /**
     * Executes the remembered value operation.
     *
     * @param key Lookup key.
     * @return The resulting value.
     */
    override fun rememberedValue(key: Any): Any? {
        return slots.rememberedValue(key)
    }

    /**
     * Updates remembered value.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    override fun updateRememberedValue(key: Any, value: Any?) {
        slots.updateRememberedValue(key, value)
    }

    /** Disposes the operation. */
    override fun dispose() {
        // Simple implementation
        slots.dispose()
    }
}

/**
 * Utility object for managing the composition context
 */
object ComposerContext {
    // Current composer, using platform-independent storage
    private var currentComposer: Composer? = null

    /**
     * Get the current composer, defaulting to CommonComposer if none set
     */
    val current: Composer
        get() = currentComposer ?: CommonComposer()

    /**
     * Execute a block with the given composer
     */
    fun <T> withComposer(composer: Composer, block: () -> T): T {
        val previous = currentComposer
        currentComposer = composer

        try {
            return block()
        } finally {
            currentComposer = previous
        }
    }
}
