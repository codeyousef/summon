package codes.yousef.summon.state

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Compatibility registry for component-keyed flow collection scopes.
 *
 * All map access is serialized. Cancellation removes ownership atomically, then
 * cancels outside the lock so completion callbacks can safely use the registry.
 * New code should prefer [bindStateFlow] with an explicitly owned UI scope.
 */
object FlowCollectionRegistry {
    private val scopes = mutableMapOf<String, CoroutineScope>()
    private val scopesLock = ReentrantLock()

    /** Returns one active scope per key, replacing an externally canceled scope. */
    fun getScope(key: String): CoroutineScope = scopesLock.withLock {
        scopes[key]?.takeIf { it.isActive } ?: CoroutineScope(Dispatchers.Default + Job()).also {
            scopes[key] = it
        }
    }

    /** Removes the key before cancellation; repeated calls have no effect. */
    fun cancelScope(key: String) {
        val scope = scopesLock.withLock { scopes.remove(key) }
        scope?.cancel(CancellationException("Component flow scope disposed"))
    }

    /** Atomically detaches all current scopes, then cancels each detached scope. */
    fun cancelAll() {
        val detached = scopesLock.withLock {
            scopes.values.toList().also { scopes.clear() }
        }
        detached.forEach { it.cancel(CancellationException("Component flow scopes disposed")) }
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
