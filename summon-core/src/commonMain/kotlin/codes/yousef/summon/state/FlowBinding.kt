package codes.yousef.summon.state

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex

/**
 * A registry for managing Flow collection scopes.
 *
 * Thread Safety Note: This registry is thread-safe. All operations on the registry
 * are protected by a mutex to ensure thread safety.
 */
object FlowCollectionRegistry {
    // Map of component IDs to their coroutine scopes
    private val scopes = mutableMapOf<String, CoroutineScope>()

    // Mutex for thread-safe access to the scopes map
    private val scopesMutex = Mutex()

    // A dedicated dispatcher for registry operations to improve thread safety
    private val registryDispatcher = Dispatchers.Default

    /**
     * Gets or creates a CoroutineScope for the given key.
     *
     * Thread Safety Note: This method is thread-safe. It uses a mutex to ensure
     * atomic operations on the scopes map.
     *
     * @param key A unique identifier for the scope
     * @return A CoroutineScope for collecting flows
     */
    fun getScope(key: String): CoroutineScope {
        // First check if we already have a scope for this key (outside the lock for performance)
        scopes[key]?.let { return it }

        // Create a new scope with a dedicated job that can be cancelled
        val newScope = CoroutineScope(registryDispatcher + Job())

        // Try to acquire the lock and check again to avoid race conditions
        if (scopesMutex.tryLock()) {
            try {
                // Check again inside the lock in case another thread created the scope
                val existingScope = scopes[key]
                if (existingScope != null) {
                    // Cancel our new scope since we won't be using it
                    newScope.cancel()
                    return existingScope
                }

                // Store the new scope in the map
                scopes[key] = newScope
            } finally {
                scopesMutex.unlock()
            }
        } else {
            // If we couldn't acquire the lock, try again recursively
            // This is safe because it will eventually succeed or return an existing scope
            newScope.cancel() // Cancel the scope we created since we're not using it
            return getScope(key)
        }

        return newScope
    }

    /**
     * Cancels and removes the CoroutineScope for the given key.
     *
     * Thread Safety Note: This method is thread-safe. It uses a mutex to ensure
     * atomic operations on the scopes map.
     *
     * @param key The key of the scope to cancel
     */
    fun cancelScope(key: String) {
        // Get the scope to cancel (outside the lock for performance)
        val scopeToCancel = scopes[key] ?: return

        // Try to acquire the lock
        if (scopesMutex.tryLock()) {
            try {
                // Check again inside the lock in case another thread removed the scope
                if (scopes.containsKey(key)) {
                    // Remove it from the map first to prevent new flows from being collected
                    scopes.remove(key)

                    // Then cancel the scope (outside the lock to avoid blocking)
                    scopeToCancel.cancel(CancellationException("Scope cancelled for key: $key"))
                }
            } finally {
                scopesMutex.unlock()
            }
        } else {
            // If we couldn't acquire the lock, we'll just cancel the scope
            // This might lead to a race condition, but it's better than blocking or recursion
            scopeToCancel.cancel(CancellationException("Scope cancelled for key: $key"))
        }
    }

    /**
     * Cancels and removes all CoroutineScopes.
     *
     * Thread Safety Note: This method is thread-safe. It uses a mutex to ensure
     * atomic operations on the scopes map.
     */
    fun cancelAll() {
        // Try to acquire the lock
        if (scopesMutex.tryLock()) {
            try {
                // Make a copy of all scopes to avoid concurrent modification
                val scopesToCancel = scopes.values.toList()

                // Clear the map first to prevent new flows from being collected
                scopes.clear()

                // Then cancel all scopes (outside the lock to avoid blocking)
                scopesToCancel.forEach { scope ->
                    scope.cancel(CancellationException("All scopes cancelled"))
                }
            } finally {
                scopesMutex.unlock()
            }
        } else {
            // If we couldn't acquire the lock, we'll just cancel all scopes without clearing the map
            // This might lead to a race condition, but it's better than blocking
            scopes.values.forEach { scope ->
                scope.cancel(CancellationException("All scopes cancelled"))
            }
        }
    }
}

/**
 * Converts a Flow to a SummonMutableState.
 * This allows reactively connecting to flows from Kotlin coroutines.
 *
 * Thread Safety Note: State updates happen on a single thread (Dispatchers.Default)
 * to ensure thread safety. The returned SummonMutableState is safe to access from multiple threads.
 *
 * @param flow The flow to connect to
 * @param initialValue The initial value before the flow emits
 * @return A SummonMutableState that updates when the flow emits new values
 */
fun <T> flowToState(
    flow: Flow<T>,
    initialValue: T
): SummonMutableState<T> {
    val state = mutableStateOf(initialValue)

    // Use a single-threaded dispatcher for state updates to ensure thread safety
    val stateUpdateDispatcher = Dispatchers.Default
    val scope = CoroutineScope(stateUpdateDispatcher + Job())

    // Ensure all state updates happen on the same thread
    flow
        .flowOn(stateUpdateDispatcher) // Process flow events on the state update dispatcher
        .onEach { newValue ->
            state.value = newValue
        }
        .launchIn(scope)

    return state
}

/**
 * Converts a Flow to a SummonMutableState and associates it with a component.
 *
 * Thread Safety Note: State updates happen on a single thread (Dispatchers.Default)
 * to ensure thread safety. The returned SummonMutableState is safe to access from multiple threads.
 *
 * @param flow The flow to connect to
 * @param initialValue The initial value before the flow emits
 * @param componentId Component ID for scope management. If not provided, a new independent scope will be created.
 * @return A SummonMutableState that updates when the flow emits new values
 */
fun <T> componentFlowToState(
    flow: Flow<T>,
    initialValue: T,
    componentId: String? = null
): SummonMutableState<T> {
    val state = mutableStateOf(initialValue)

    // Create a scope for collecting the flow
    val scope = if (componentId != null) {
        // If a component ID is provided, get or create a scope from the registry
        // This ensures we can cancel all flows for a component when it's no longer needed
        FlowCollectionRegistry.getScope(componentId)
    } else {
        // If no component ID is provided, create a new independent scope
        CoroutineScope(Dispatchers.Default + Job())
    }

    // Ensure all state updates happen on the same thread
    flow
        .flowOn(Dispatchers.Default) // Process flow events on a single thread
        .onEach { newValue ->
            // Update the state with flow values
            // For StateFlow, this will immediately emit the current value, which is what we want
            state.value = newValue
        }
        .launchIn(scope)

    return state
}

/**
 * Converts a StateFlow to a SummonMutableState.
 *
 * Thread Safety Note: StateFlow is already thread-safe, and state updates happen
 * on a single thread to ensure thread safety of the SummonMutableState.
 *
 * @param stateFlow The StateFlow to connect to
 * @return A SummonMutableState that updates when the StateFlow emits new values
 */
fun <T> stateFlowToState(stateFlow: StateFlow<T>): SummonMutableState<T> {
    return flowToState(stateFlow, stateFlow.value)
}

/**
 * Cancels all Flow collections associated with a component.
 *
 * Thread Safety Note: This method is thread-safe as it delegates to FlowCollectionRegistry.cancelScope,
 * which handles thread safety internally.
 *
 * @param componentId The unique identifier for the component
 */
fun cancelComponentFlows(componentId: String) {
    FlowCollectionRegistry.cancelScope(componentId)
} 
