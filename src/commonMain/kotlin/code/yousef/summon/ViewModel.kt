package code.yousef.summon

/**
 * A simplified ViewModel base class for Summon.
 * This provides a common base for platform-specific ViewModel implementations.
 */
abstract class ViewModel {
    private val states = mutableMapOf<String, MutableState<*>>()
    private val clearActions = mutableListOf<() -> Unit>()

    /**
     * Creates or retrieves a MutableState with the given key and initial value.
     * @param key The key to identify this state
     * @param initialValue The initial value if the state doesn't exist yet
     * @return A MutableState that is managed by this ViewModel
     */
    fun <T> state(key: String, initialValue: T): MutableState<T> {
        @Suppress("UNCHECKED_CAST")
        return states.getOrPut(key) {
            mutableStateOf(initialValue)
        } as MutableState<T>
    }

    /**
     * Registers a clear action that will be executed when the ViewModel is cleared.
     * @param action The action to execute on clear
     */
    fun onClear(action: () -> Unit) {
        clearActions.add(action)
    }

    /**
     * Clears this ViewModel, releasing any resources.
     * This is typically called when the associated UI component is destroyed.
     */
    fun clear() {
        clearActions.forEach { it() }
        clearActions.clear()
        states.clear()
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