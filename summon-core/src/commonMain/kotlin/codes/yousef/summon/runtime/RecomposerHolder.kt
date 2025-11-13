package codes.yousef.summon.runtime

/**
 * Global holder for the Recomposer instance.
 * This allows accessing the Recomposer from anywhere in the application.
 */
object RecomposerHolder {
    /**
     * The global Recomposer instance.
     */
    private var _recomposer: Recomposer? = null

    val recomposer: Recomposer
        get() = _recomposer ?: Recomposer().also { _recomposer = it }

    /**
     * Gets the current Recomposer instance.
     * This is a convenience function to make the code more readable.
     */
    fun current(): Recomposer = recomposer

    /**
     * Sets the Recomposer instance.
     * This is primarily for testing purposes.
     */
    fun setRecomposer(instance: Recomposer?) {
        _recomposer = instance
    }

    /**
     * Sets the scheduler for the current recomposer.
     * This is primarily for testing purposes.
     */
    fun setScheduler(scheduler: RecompositionScheduler) {
        recomposer.setScheduler(scheduler)
    }

    /**
     * Creates a new Composer instance backed by the global Recomposer.
     */
    fun createComposer(): Composer {
        return recomposer.createComposer()
    }
} 