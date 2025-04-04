package com.summon

import java.util.concurrent.CopyOnWriteArrayList

/**
 * JVM implementation for State management.
 */

/**
 * Provides JVM-specific implementation for state observation.
 * This implementation uses thread-safe collections for observers.
 */
class StateObserver<T>(private val state: MutableState<T>) {
    private val observers = CopyOnWriteArrayList<(T) -> Unit>()
    
    init {
        if (state is MutableStateImpl) {
            state.addListener { value ->
                notifyObservers(value)
            }
        }
    }
    
    /**
     * Adds an observer that will be notified when the state changes.
     */
    fun addObserver(observer: (T) -> Unit) {
        observers.add(observer)
        // Immediately notify with current value
        observer(state.value)
    }
    
    /**
     * Removes an observer.
     */
    fun removeObserver(observer: (T) -> Unit) {
        observers.remove(observer)
    }
    
    private fun notifyObservers(value: T) {
        observers.forEach { it(value) }
    }
}

/**
 * Creates a state observer for this state.
 */
fun <T> MutableState<T>.createObserver(): StateObserver<T> = StateObserver(this)

/**
 * Formats this state value as a string for debugging.
 */
fun <T> State<T>.toJvmString(): String = "State(value=${value})" 