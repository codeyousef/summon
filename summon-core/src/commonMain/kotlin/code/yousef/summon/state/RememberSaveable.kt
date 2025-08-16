package code.yousef.summon.state

/**
 * A registry for persisted state values.
 * This registry stores state values that need to be persisted across recompositions.
 */
object SaveableStateRegistry {
    private val values = mutableMapOf<String, Any?>()

    /**
     * Retrieves a value from the registry or null if not found.
     * @param key The key to identify the stored value
     * @return The stored value or null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? = values[key] as? T

    /**
     * Stores a value in the registry.
     * @param key The key to identify the stored value
     * @param value The value to store
     */
    fun set(key: String, value: Any?) {
        values[key] = value
    }

    /**
     * Clears all stored values.
     */
    fun clear() {
        values.clear()
    }
}

/**
 * Creates a SummonMutableState that persists its value across recompositions.
 * This is a simplified implementation that doesn't depend on compose runtime.
 *
 * @param key A unique key to identify this state
 * @param initialValue The initial value if no persisted value exists
 * @return A SummonMutableState that will persist its value
 */
fun <T> rememberSaveable(
    key: String,
    initialValue: T
): SummonMutableState<T> {
    // Try to retrieve a previously saved value
    val savedValue = SaveableStateRegistry.get<T>(key) ?: initialValue
    val state = mutableStateOf(savedValue)

    // Register a listener to persist value changes
    if (state is MutableStateImpl<T>) {
        state.addListener { newValue ->
            SaveableStateRegistry.set(key, newValue)
        }
    }

    return state
}

/**
 * Similar to rememberSaveable but uses an identifier as the key.
 * This is useful when the key needs to be generated from multiple values.
 *
 * @param identifier The identifier to use as the key
 * @param initialValue The initial value if no persisted value exists
 */
fun <T> rememberWithIdentifier(
    identifier: String,
    initialValue: T
): SummonMutableState<T> {
    // Generate a key based on the identifier
    return rememberSaveable(identifier, initialValue)
}

/**
 * Clears all persisted states.
 * This can be used when navigating away from a page or component.
 */
fun clearSaveableStates() {
    SaveableStateRegistry.clear()
}

/**
 * Checks if a state with the given key exists in the registry.
 * @param key The key to check
 * @return true if a state with the given key exists, false otherwise
 */
fun hasSaveableState(key: String): Boolean {
    return SaveableStateRegistry.get<Any?>(key) != null
} 
