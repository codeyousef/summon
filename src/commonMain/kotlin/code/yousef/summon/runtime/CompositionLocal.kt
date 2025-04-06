package code.yousef.summon.runtime

/**
 * Provides a thread-local mechanism for accessing the current composer.
 * This is crucial for the composable function system to work properly.
 */
object CompositionLocal {
    /**
     * ThreadLocal storage for the current composer.
     */
    private val currentComposerThreadLocal = ThreadLocal<Composer?>()
    
    /**
     * Get the current composer from thread-local storage.
     */
    val currentComposer: Composer?
        get() = currentComposerThreadLocal.get()
    
    /**
     * Set the current composer in thread-local storage.
     */
    fun setCurrentComposer(composer: Composer?) {
        currentComposerThreadLocal.set(composer)
    }
} 