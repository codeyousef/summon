package code.yousef.summon

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
        scopes[key]?.cancel()
        scopes.remove(key)
    }

    /**
     * Cancels and removes all CoroutineScopes.
     */
    fun cancelAll() {
        scopes.values.forEach { it.cancel() }
        scopes.clear()
    }
}

/**
 * Converts a Flow to a MutableState.
 * @param scope The CoroutineScope to collect the flow in, or null to use a default scope
 * @param initialValue The initial value of the state
 * @return A MutableState that updates when the flow emits new values
 */
fun <T> Flow<T>.toState(
    scope: CoroutineScope? = null,
    initialValue: T
): MutableState<T> {
    val state = mutableStateOf(initialValue)
    val collectionScope = scope ?: CoroutineScope(Dispatchers.Default)

    onEach { newValue ->
        state.value = newValue
    }.launchIn(collectionScope)

    return state
}

/**
 * Converts a Flow to a MutableState and associates it with a component.
 * @param componentId A unique identifier for the component
 * @param initialValue The initial value of the state
 * @return A MutableState that updates when the flow emits new values
 */
fun <T> Flow<T>.toComponentState(
    componentId: String,
    initialValue: T
): MutableState<T> {
    val state = mutableStateOf(initialValue)
    val scope = FlowCollectionRegistry.getScope(componentId)

    onEach { newValue ->
        state.value = newValue
    }.launchIn(scope)

    return state
}

/**
 * Converts a StateFlow to a MutableState.
 * @param scope The CoroutineScope to collect the flow in, or null to use a default scope
 * @return A MutableState that updates when the StateFlow emits new values
 */
fun <T> StateFlow<T>.toState(
    scope: CoroutineScope? = null
): MutableState<T> {
    return toState(scope, value)
}

/**
 * Converts a MutableState to a MutableStateFlow.
 * @return A MutableStateFlow that updates when the MutableState changes
 */
fun <T> MutableState<T>.toStateFlow(): MutableStateFlow<T> {
    val stateFlow = MutableStateFlow(value)

    if (this is MutableStateImpl<T>) {
        addListener { newValue ->
            stateFlow.value = newValue
        }
    }

    return stateFlow
}

/**
 * Cancels all Flow collections associated with a component.
 * @param componentId The unique identifier for the component
 */
fun cancelComponentFlows(componentId: String) {
    FlowCollectionRegistry.cancelScope(componentId)
} 
