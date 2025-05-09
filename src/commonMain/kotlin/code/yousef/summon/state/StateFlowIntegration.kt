package code.yousef.summon.state

import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.MutableStateImpl
import code.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Creates a SummonMutableState that is backed by a MutableStateFlow.
 * This is useful for connecting to existing StateFlow-based architecture.
 * @param stateFlow The MutableStateFlow to connect to
 * @return A SummonMutableState that reflects and updates the StateFlow
 */
fun <T> stateFromStateFlow(stateFlow: MutableStateFlow<T>): SummonMutableState<T> {
    val state = mutableStateOf(stateFlow.value)

    // Update the state when the flow changes
    val scope = CoroutineScope(Dispatchers.Default)
    stateFlow.onEach { newValue ->
        state.value = newValue
    }.launchIn(scope)

    // Update the flow when the state changes
    if (state is MutableStateImpl<T>) {
        state.addListener { newValue: T ->
            stateFlow.value = newValue
        }
    }

    return state
}

/**
 * Creates a SummonMutableState that is backed by a StateFlow (read-only connection).
 * @param stateFlow The StateFlow to connect to
 * @return A SummonMutableState that reflects the StateFlow (changes to the State won't affect the Flow)
 */
fun <T> stateFromReadOnlyStateFlow(stateFlow: StateFlow<T>): SummonMutableState<T> {
    val state = mutableStateOf(stateFlow.value)

    // Update the state when the flow changes
    val scope = CoroutineScope(Dispatchers.Default)
    stateFlow.onEach { newValue ->
        state.value = newValue
    }.launchIn(scope)

    return state
}

/**
 * Creates a SummonMutableState that collects from a SharedFlow.
 * @param sharedFlow The SharedFlow to collect from
 * @param initialValue The initial value of the state
 * @return A SummonMutableState that reflects values from the SharedFlow
 */
fun <T> stateFromSharedFlow(
    sharedFlow: SharedFlow<T>,
    initialValue: T
): SummonMutableState<T> {
    val state = mutableStateOf(initialValue)

    // Update the state when the flow emits
    val scope = CoroutineScope(Dispatchers.Default)
    sharedFlow.onEach { newValue ->
        state.value = newValue
    }.launchIn(scope)

    return state
}

/**
 * Creates a MutableSharedFlow from a SummonMutableState.
 * @param replay The number of values to replay to new collectors
 * @param extraBufferCapacity Additional buffer capacity for the flow
 * @return A MutableSharedFlow that emits when the state changes
 */
fun <T> SummonMutableState<T>.toSharedFlow(
    replay: Int = 1,
    extraBufferCapacity: Int = 0
): MutableSharedFlow<T> {
    val sharedFlow = MutableSharedFlow<T>(
        replay = replay,
        extraBufferCapacity = extraBufferCapacity
    )

    // Emit the current value
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        sharedFlow.emit(value)
    }

    // Update the flow when the state changes
    if (this is MutableStateImpl<T>) {
        addListener { newValue: T ->
            scope.launch {
                sharedFlow.emit(newValue)
            }
        }
    }

    return sharedFlow
}

/**
 * Extension property to convert a SummonMutableState to a StateFlow
 */
val <T> SummonMutableState<T>.asStateFlow: StateFlow<T>
    get() = this.toStateFlow().asStateFlow()

/**
 * Extension property to convert a SummonMutableState to a SharedFlow
 */
val <T> SummonMutableState<T>.asSharedFlow: SharedFlow<T>
    get() = this.toSharedFlow().asSharedFlow()

/**
 * Extension function to convert a SummonMutableState to a StateFlow
 */
fun <T> SummonMutableState<T>.toStateFlow(): MutableStateFlow<T> {
    val stateFlow = MutableStateFlow(value)

    // Update the flow when the state changes
    if (this is MutableStateImpl<T>) {
        addListener { newValue: T ->
            stateFlow.value = newValue
        }
    }

    return stateFlow
} 
