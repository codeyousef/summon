package code.yousef.summon.runtime

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.MutableState
import code.yousef.summon.runtime.State

/**
 * Remembers a value across recompositions.
 * 
 * The [calculation] lambda will only be executed during the first composition.
 * In subsequent recompositions, the remembered value will be returned without 
 * re-executing the calculation.
 * 
 * @param calculation A function that creates the value to be remembered
 * @return The remembered value
 */
@Composable
fun <T> remember(calculation: () -> T): T {
    val composer = CompositionLocal.currentComposer ?: return calculation()
    
    // Get the current slot index
    composer.nextSlot()
    
    // Check if we already have a value
    val existing = composer.getSlot() as? T
    
    return if (existing != null) {
        // Return the existing value
        existing
    } else {
        // Calculate and store a new value
        val value = calculation()
        composer.setSlot(value)
        value
    }
}

/**
 * Remembers a value across recompositions, recalculating it when any of the [keys] change.
 * 
 * @param keys Array of objects that will be checked for changes
 * @param calculation A function that creates the value to be remembered
 * @return The remembered value, recalculated when keys change
 */
@Composable
fun <T> remember(vararg keys: Any?, calculation: () -> T): T {
    val composer = CompositionLocal.currentComposer ?: return calculation()
    
    // Get the current slot index for the keys
    composer.nextSlot()
    
    // Check if we have stored keys
    val storedInputs = composer.getSlot() as? Array<*>
    
    // Get the next slot for the value
    composer.nextSlot()
    
    // Check if we already have a value
    val existing = composer.getSlot() as? T
    
    // See if keys have changed
    val inputsChanged = storedInputs == null || !keys.contentEquals(storedInputs)
    
    return if (!inputsChanged && existing != null) {
        // Return the existing value
        existing
    } else {
        // Store the new keys
        composer.setSlot(keys)
        
        // Calculate and store a new value
        val value = calculation()
        composer.setSlot(value)
        value
    }
}

/**
 * Creates and remembers a mutable state with the given initial value.
 * 
 * @param initial The initial value for the state
 * @return A remembered MutableState
 */
@Composable
fun <T> rememberMutableStateOf(initial: T): MutableState<T> {
    return remember { mutableStateOf(initial) }
}

/**
 * Creates and remembers a derivation of some [MutableState].
 * 
 * @param calculation The function to derive a state from another state.
 * @return A [State] that updates when the source state changes.
 */
@Composable
fun <T> derivedStateOf(calculation: () -> T): State<T> {
    val state = rememberMutableStateOf(calculation())
    
    // In a real implementation, this would set up a side effect to update the derived state
    // when dependencies change. This is a simplified placeholder.
    // TODO: Implement proper dependency tracking
    
    return state
} 