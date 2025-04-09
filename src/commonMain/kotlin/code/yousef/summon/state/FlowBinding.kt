package code.yousef.summon.state

import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.MutableStateImpl
import code.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CancellationException

/**
 * A registry for managing Flow collection scopes.
 */
object FlowCollectionRegistry {
    private val scopes = mutableMapOf<String, CoroutineScope>()

    /**
     * Gets or creates a CoroutineScope for the given key.
     * @param key A unique identifier for the scope
     * @return A CoroutineScope for collecting flows
     */
    fun getScope(key: String): CoroutineScope {
        return scopes.getOrPut(key) {
            CoroutineScope(Dispatchers.Default + Job())
        }
    }

    /**
     * Cancels and removes the CoroutineScope for the given key.
     * @param key The key of the scope to cancel
     */
    fun cancelScope(key: String) {
        scopes[key]?.coroutineContext?.cancel()
        scopes.remove(key)
    }

    /**
     * Cancels and removes all CoroutineScopes.
     */
    fun cancelAll() {
        scopes.values.forEach { scope -> 
            scope.coroutineContext.cancel() 
        }
        scopes.clear()
    }
}

/**
 * Converts a Flow to a SummonMutableState.
 * This allows reactively connecting to flows from Kotlin coroutines.
 * @param flow The flow to connect to
 * @param initialValue The initial value before the flow emits
 * @return A SummonMutableState that updates when the flow emits new values
 */
fun <T> flowToState(
    flow: Flow<T>,
    initialValue: T
): SummonMutableState<T> {
    val state = mutableStateOf(initialValue)
    
    val scope = CoroutineScope(Dispatchers.Default)
    flow.onEach { newValue ->
        state.value = newValue
    }.launchIn(scope)
    
    return state
}

/**
 * Converts a Flow to a SummonMutableState and associates it with a component.
 * @param flow The flow to connect to
 * @param initialValue The initial value before the flow emits
 * @return A SummonMutableState that updates when the flow emits new values
 */
fun <T> componentFlowToState(
    flow: Flow<T>,
    initialValue: T
): SummonMutableState<T> {
    val state = mutableStateOf(initialValue)
    
    val scope = CoroutineScope(Dispatchers.Default)
    flow.onEach { newValue ->
        state.value = newValue
    }.launchIn(scope)
    
    return state
}

/**
 * Converts a StateFlow to a SummonMutableState.
 * @param stateFlow The StateFlow to connect to
 * @return A SummonMutableState that updates when the StateFlow emits new values
 */
fun <T> stateFlowToState(stateFlow: StateFlow<T>): SummonMutableState<T> {
    return flowToState(stateFlow, stateFlow.value)
}

/**
 * Cancels all Flow collections associated with a component.
 * @param componentId The unique identifier for the component
 */
fun cancelComponentFlows(componentId: String) {
    FlowCollectionRegistry.cancelScope(componentId)
} 
