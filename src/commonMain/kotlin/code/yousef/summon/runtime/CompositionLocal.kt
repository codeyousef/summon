package code.yousef.summon.runtime

/**
 * Provides access to composition-local values within a composition.
 */
object CompositionLocal {
    /**
     * The currently active composer, or null if no composition is in progress.
     */
    private var _currentComposer: Composer? = null

    /**
     * Gets the current composer instance.
     * This is used by composable functions to manage their lifecycle.
     */
    val currentComposer: Composer?
        get() = _currentComposer

    /**
     * Sets the current composer.
     * This should only be called by the composition framework.
     */
    internal fun setCurrentComposer(composer: Composer?) {
        _currentComposer = composer
        
        // Also update the recomposer
        if (composer != null && Recomposer.isComposerImpl(composer)) {
            RecomposerHolder.recomposer.setActiveComposer(Recomposer.asComposerImpl(composer))
        } else {
            RecomposerHolder.recomposer.setActiveComposer(null)
        }
    }
    
    /**
     * Temporarily sets the current composer to the provided composer and executes the block.
     * After the block is executed, restores the previous composer.
     * 
     * @param composer The composer to set as current during the execution of the block.
     * @param block The block to execute with the temporary composer.
     * @return The result of the block.
     */
    fun <R> provideComposer(composer: Composer, block: () -> R): R {
        val previous = _currentComposer
        setCurrentComposer(composer)
        try {
            return block()
        } finally {
            setCurrentComposer(previous)
        }
    }
    
    /**
     * Creates a CompositionLocal with a default value.
     * 
     * @param defaultValue The default value to return when no provider exists.
     * @return A new CompositionLocalProvider.
     */
    fun <T> compositionLocalOf(defaultValue: T): CompositionLocalProvider<T> =
        CompositionLocalProviderImpl(defaultValue)
    
    /**
     * Creates a static CompositionLocal that requires a provider.
     * 
     * @return A new CompositionLocal that throws if no provider exists.
     */
    fun <T> staticCompositionLocalOf(): CompositionLocalProvider<T> =
        StaticCompositionLocalProviderImpl()
}

/**
 * Base interface for a composition local provider.
 */
interface CompositionLocalProvider<T> {
    /**
     * The current value of this composition local.
     */
    val current: T
    
    /**
     * Creates a new provider with the specified value.
     * 
     * @param value The value to provide.
     * @return A new provider providing the value.
     */
    fun provides(value: T): CompositionLocalProvider<T>
}

/**
 * Default implementation of CompositionLocalProvider.
 */
private class CompositionLocalProviderImpl<T>(private val defaultValue: T) : CompositionLocalProvider<T> {
    private var value: T = defaultValue
    
    override val current: T
        get() = value
    
    override fun provides(value: T): CompositionLocalProvider<T> {
        this.value = value
        return this
    }
}

/**
 * Implementation of CompositionLocalProvider for static values that must be provided.
 */
private class StaticCompositionLocalProviderImpl<T> : CompositionLocalProvider<T> {
    private var value: T? = null
    
    override val current: T
        get() = value ?: throw IllegalStateException("CompositionLocal not provided")
    
    override fun provides(value: T): CompositionLocalProvider<T> {
        this.value = value
        return this
    }
} 
