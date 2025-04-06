package code.yousef.summon.runtime

/**
 * Manages the composition context for rendering.
 * This class helps maintain the current composer during composition.
 */
class CompositionContext(val composer: Composer) {
    /**
     * Indicates if this composition context has been disposed.
     */
    private var disposed = false
    
    /**
     * Disposes of this composition context.
     */
    fun dispose() {
        disposed = true
    }
    
    /**
     * Check if this context has been disposed.
     */
    fun isDisposed(): Boolean = disposed
    
    companion object {
        /**
         * Executes a block within the specified composition context.
         * This sets the current composer for the duration of the block execution.
         */
        fun withContext(context: CompositionContext, block: () -> Unit) {
            val previousComposer = CompositionLocal.currentComposer
            CompositionLocal.setCurrentComposer(context.composer)
            try {
                block()
            } finally {
                CompositionLocal.setCurrentComposer(previousComposer)
            }
        }
    }
} 