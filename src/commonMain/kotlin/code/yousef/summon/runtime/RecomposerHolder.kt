package code.yousef.summon.runtime

/**
 * Global holder for the Recomposer instance.
 * This allows accessing the Recomposer from anywhere in the application.
 */
object RecomposerHolder {
    /**
     * The global Recomposer instance.
     */
    val recomposer = Recomposer()
    
    /**
     * Gets the current Recomposer instance.
     * This is a convenience function to make the code more readable.
     */
    fun current(): Recomposer = recomposer
    
    /**
     * Creates a new Composer instance backed by the global Recomposer.
     */
    fun createComposer(): Composer {
        return recomposer.createComposer()
    }
} 