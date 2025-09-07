package code.yousef.summon.state

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Creates a simple derived state that recomputes when accessed.
 * This is a simplified implementation without automatic dependency tracking.
 */
fun <T> simpleDerivedStateOf(
    calculation: () -> T
): State<T> = SimpleDerivedStateImpl(calculation)

/**
 * Simple implementation of derived state.
 */
private class SimpleDerivedStateImpl<T>(
    private val calculation: () -> T
) : State<T> {
    override val value: T
        get() = calculation()
}

/**
 * Creates a state from a suspend function that produces values.
 */
@Composable
fun <T> produceState(
    initialValue: T,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit
): State<T> {
    val result = remember(*keys) { mutableStateOf(initialValue) }

    LaunchedEffect(keys.firstOrNull() ?: Unit) {
        ProduceStateScopeImpl(result).producer()
    }

    return result
}

/**
 * Scope for producing state values.
 */
interface ProduceStateScope<T> {
    var value: T
}

private class ProduceStateScopeImpl<T>(
    private val state: MutableState<T>
) : ProduceStateScope<T> {
    override var value: T
        get() = state.value
        set(value) {
            state.value = value
        }
}

/**
 * Collects values from a Flow as State.
 */
@Composable
fun <T> Flow<T>.collectAsState(initial: T): State<T> {
    val state = remember { mutableStateOf(initial) }

    LaunchedEffect(this) {
        collect { value ->
            state.value = value
        }
    }

    return state
}

/**
 * Collects values from a StateFlow as State.
 */
@Composable
fun <T> StateFlow<T>.collectAsState(): State<T> {
    return collectAsState(value)
}