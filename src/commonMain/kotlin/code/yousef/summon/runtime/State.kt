package code.yousef.summon.runtime

import code.yousef.summon.state.State
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf as statePackageMutableStateOf

/**
 * Type alias for backward compatibility with existing code.
 * Use SummonMutableState from the state package for new code.
 */
typealias RuntimeMutableState<T> = SummonMutableState<T>

/**
 * Creates a new mutable state with the specified initial value.
 * This delegates to the implementation in the state package.
 */
fun <T> mutableStateOf(initialValue: T): RuntimeMutableState<T> = statePackageMutableStateOf(initialValue)

/**
 * Extension property to make accessing a State's value more ergonomic.
 * This allows writing `state()` instead of `state.value`.
 */
operator fun <T> State<T>.invoke(): T = this.value

/**
 * Extension function to update a RuntimeMutableState's value.
 * This allows writing `state(newValue)` instead of `state.value = newValue`.
 */
operator fun <T> RuntimeMutableState<T>.invoke(newValue: T) {
    this.value = newValue
}

