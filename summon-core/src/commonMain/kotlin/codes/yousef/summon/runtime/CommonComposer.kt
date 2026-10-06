package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable

/**
 * A simple implementation of Composer that can be used on any platform
 */
class CommonComposer : Composer {
    override val inserting: Boolean = true

    private val slots = CompositionSlots()
    private var compositionDepth = 0

    override fun startCompose() {
        startNode()
    }

    override fun endCompose() {
        endNode()
    }

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

    override fun startNode() {
        // Simple implementation
    }

    override fun endNode() {
        // Simple implementation
    }

    override fun startGroup(key: Any?) {
        slots.startGroup(key)
    }

    override fun endGroup() {
        slots.endGroup()
    }

    override fun changed(value: Any?): Boolean {
        // Simple implementation
        return true
    }

    override fun updateValue(value: Any?) {
        // Simple implementation
    }

    override fun nextSlot() {
        slots.nextSlot()
    }

    override fun getSlot(): Any? {
        return slots.getSlot()
    }

    override fun setSlot(value: Any?) {
        slots.setSlot(value)
    }

    override fun recordRead(state: Any) {
        // Simple implementation
    }

    override fun recordWrite(state: Any) {
        // Simple implementation
    }

    override fun reportChanged() {
        // Simple implementation
    }

    override fun registerDisposable(disposable: () -> Unit) {
        // Simple implementation
    }

    override fun recompose() {
        // Simple implementation - trigger recomposition
    }

    override fun rememberedValue(key: Any): Any? {
        return slots.rememberedValue(key)
    }

    override fun updateRememberedValue(key: Any, value: Any?) {
        slots.updateRememberedValue(key, value)
    }

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
