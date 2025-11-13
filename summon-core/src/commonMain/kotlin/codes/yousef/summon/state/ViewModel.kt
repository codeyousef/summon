package codes.yousef.summon.state

import kotlin.reflect.KClass

/**
 * Base ViewModel class that manages state using SummonMutableState.
 */
abstract class ViewModel {

    // Map of state objects to their keys
    private val states = mutableMapOf<String, SummonMutableState<*>>()

    /**
     * Get or create a mutable state object with the given key and initial value.
     */
    protected fun <T> state(key: String, initialValue: T): SummonMutableState<T> {
        @Suppress("UNCHECKED_CAST")
        return states.getOrPut(key) { mutableStateOf(initialValue) } as SummonMutableState<T>
    }

    /**
     * Retrieve a state object by key.
     */
    fun <T> getState(key: String): SummonMutableState<T>? {
        @Suppress("UNCHECKED_CAST")
        return states[key] as? SummonMutableState<T>
    }

    /**
     * Clear all actions and prepare for disposal.
     */
    open fun clear() {
        states.clear()
    }

    companion object Factory {
        // Map of ViewModel instances
        private val viewModels = mutableMapOf<KClass<out ViewModel>, ViewModel>()

        /**
         * Internal implementation that handles getting or creating a ViewModel instance.
         * This is a non-inline function that can access private properties.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : ViewModel> getInternal(kClass: KClass<T>, factory: () -> T): T {
            return viewModels.getOrPut(kClass) { factory() } as T
        }

        /**
         * Get or create a ViewModel instance with the provided factory function.
         */
        inline fun <reified T : ViewModel> get(noinline factory: () -> T): T {
            return getInternal(T::class, factory)
        }

        /**
         * Clear all ViewModel instances.
         */
        fun clearAll() {
            viewModels.values.forEach { it.clear() }
            viewModels.clear()
        }
    }
}

/**
 * A ViewModel factory for creating ViewModels of different types.
 */
object ViewModelFactory {
    private val viewModels = mutableMapOf<String, ViewModel>()

    /**
     * Gets or creates a ViewModel of the specified type.
     * @param key A unique key to identify this ViewModel instance
     * @param factory A factory function to create the ViewModel if it doesn't exist
     * @return The ViewModel instance
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ViewModel> getOrCreate(key: String, factory: () -> T): T {
        return viewModels.getOrPut(key) { factory() } as T
    }

    /**
     * Clears a specific ViewModel.
     * @param key The key of the ViewModel to clear
     */
    fun clear(key: String) {
        viewModels[key]?.clear()
        viewModels.remove(key)
    }

    /**
     * Clears all ViewModels.
     */
    fun clearAll() {
        viewModels.values.forEach { it.clear() }
        viewModels.clear()
    }
}

/**
 * Creates or retrieves a ViewModel of the specified type.
 * @param key A unique key to identify this ViewModel instance
 * @param factory A factory function to create the ViewModel if it doesn't exist
 * @return The ViewModel instance
 */
inline fun <reified T : ViewModel> viewModel(
    key: String = T::class.simpleName ?: "ViewModel",
    noinline factory: () -> T
): T {
    return ViewModelFactory.getOrCreate(key, factory)
}

/**
 * A utility for creating a component-scoped ViewModel.
 * This is typically used within a composable function.
 * @param identifier A unique identifier for this component
 * @param factory A factory function to create the ViewModel
 * @return The ViewModel instance
 */
inline fun <reified T : ViewModel> componentViewModel(
    identifier: String,
    noinline factory: () -> T
): T {
    val key = "${T::class.simpleName}:$identifier"
    return viewModel(key, factory)
} 
