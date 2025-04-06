package code.yousef.summon

/**
 * A registry for remembered values.
 * This registry stores values that need to be remembered across recompositions.
 */
private object RememberRegistry {
    private val values = mutableMapOf<String, Any?>()
    private var currentScope: String = "global"
    private var keyCounter: Int = 0

    /**
     * Sets the current composition scope.
     * @param scope The scope identifier
     */
    fun setScope(scope: String) {
        currentScope = scope
    }

    /**
     * Generates a unique key for the remember call.
     * @return A new unique key
     */
    fun generateKey(): String {
        return "$currentScope:${keyCounter++}"
    }

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
     * Clears all values for a specific scope.
     * @param scope The scope to clear
     */
    fun clearScope(scope: String) {
        values.keys.removeAll { it.startsWith("$scope:") }
    }
}

/**
 * Remembers a value across recompositions.
 * This implementation will preserve the value between calls to the same composable function.
 *
 * @param calculation A function that produces the value to be remembered
 * @return The remembered value
 */
fun <T> remember(calculation: () -> T): T {
    val key = RememberRegistry.generateKey()
    val existing = RememberRegistry.get<T>(key)
    
    return if (existing != null) {
        existing
    } else {
        val value = calculation()
        RememberRegistry.set(key, value)
        value
    }
}

/**
 * Remembers a value across recompositions, recalculating it if the key changes.
 *
 * @param key A value that, when changed, triggers recalculation
 * @param calculation A function that produces the value to be remembered
 * @return The remembered value
 */
fun <T, A> remember(key: A, calculation: (A) -> T): T {
    val registryKey = "${RememberRegistry.generateKey()}:${key.hashCode()}"
    val existing = RememberRegistry.get<Pair<A, T>>(registryKey)
    
    return if (existing != null && existing.first == key) {
        existing.second
    } else {
        val value = calculation(key)
        RememberRegistry.set(registryKey, key to value)
        value
    }
}

/**
 * Remembers a value across recompositions, recalculating it if any of the keys change.
 *
 * @param key1 First key that, when changed, triggers recalculation
 * @param key2 Second key that, when changed, triggers recalculation
 * @param calculation A function that produces the value to be remembered
 * @return The remembered value
 */
fun <T, A, B> remember(key1: A, key2: B, calculation: (A, B) -> T): T {
    val registryKey = "${RememberRegistry.generateKey()}:${key1.hashCode()}:${key2.hashCode()}"
    val existing = RememberRegistry.get<Triple<A, B, T>>(registryKey)
    
    return if (existing != null && existing.first == key1 && existing.second == key2) {
        existing.third
    } else {
        val value = calculation(key1, key2)
        RememberRegistry.set(registryKey, Triple(key1, key2, value))
        value
    }
}

/**
 * Remembers a value across recompositions, recalculating it if any of the keys change.
 *
 * @param key1 First key that, when changed, triggers recalculation
 * @param key2 Second key that, when changed, triggers recalculation
 * @param key3 Third key that, when changed, triggers recalculation
 * @param calculation A function that produces the value to be remembered
 * @return The remembered value
 */
fun <T, A, B, C> remember(key1: A, key2: B, key3: C, calculation: (A, B, C) -> T): T {
    val registryKey = "${RememberRegistry.generateKey()}:${key1.hashCode()}:${key2.hashCode()}:${key3.hashCode()}"
    
    data class Keys<A, B, C, T>(val key1: A, val key2: B, val key3: C, val value: T)
    
    val existing = RememberRegistry.get<Keys<A, B, C, T>>(registryKey)
    
    return if (existing != null && existing.key1 == key1 && existing.key2 == key2 && existing.key3 == key3) {
        existing.value
    } else {
        val value = calculation(key1, key2, key3)
        RememberRegistry.set(registryKey, Keys(key1, key2, key3, value))
        value
    }
} 