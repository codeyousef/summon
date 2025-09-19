@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.runtime

import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf

/**
 * Remembers a value across recompositions.
 *
 * The `remember` function is fundamental to state management in Summon. It provides
 * a way to store values that persist across recomposition cycles, enabling
 * efficient management of expensive computations and stateful objects.
 *
 * ## Behavior
 *
 * - **First Composition**: [calculation] is executed and the result is stored
 * - **Subsequent Recompositions**: Stored value is returned without recalculation
 * - **Key Changes**: When keys change, [calculation] is re-executed
 * - **Disposal**: Stored values are cleaned up when composition is disposed
 *
 * ## Use Cases
 *
 * ### Expensive Computations
 * ```kotlin
 * @Composable
 * fun ExpensiveComponent(data: List<String>) {
 *     val processedData = remember {
 *         // Only computed once, unless data changes
 *         data.map { processExpensively(it) }
 *     }
 *
 *     DisplayData(processedData)
 * }
 * ```
 *
 * ### Object Creation
 * ```kotlin
 * @Composable
 * fun AnimatedComponent() {
 *     val animator = remember {
 *         // Create animator once and reuse
 *         ComplexAnimator()
 *     }
 *
 *     LaunchedEffect(Unit) {
 *         animator.start()
 *     }
 * }
 * ```
 *
 * ### State Management
 * ```kotlin
 * @Composable
 * fun Counter() {
 *     var count by remember { mutableStateOf(0) }
 *
 *     Button(
 *         onClick = { count++ },
 *         label = "Count: $count"
 *     )
 * }
 * ```
 *
 * ## Memory Management
 *
 * Values stored by `remember` are automatically cleaned up when:
 * - The composable leaves the composition tree
 * - The containing composition is disposed
 * - Keys change and new values are calculated
 *
 * ## Performance Considerations
 *
 * - Use `remember` for expensive operations that don't need re-execution
 * - Prefer keyed `remember` when the result depends on parameters
 * - Avoid storing large objects unnecessarily
 * - Use `remember` with delegation for ergonomic state access
 *
 * @param T The type of value to remember
 * @param calculation Function that creates the value to be remembered
 * @return The remembered value (calculated once, then cached)
 * @see remember with keys for conditional recalculation
 * @see rememberMutableStateOf for state creation
 * @since 1.0.0
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
 * @return A remembered SummonMutableState
 */
@Composable
fun <T> rememberMutableStateOf(initial: T): SummonMutableState<T> {
    return remember { mutableStateOf(initial) }
}

/**
 * Creates and remembers a derivation of some [SummonMutableState].
 *
 * @param calculation The function to derive a state from another state.
 * @return A [SummonMutableState] that updates when the source state changes.
 */
@Composable
fun <T> derivedStateOf(calculation: () -> T): SummonMutableState<T> {
    // Create a mutableState to hold the derived value
    val state = rememberMutableStateOf(calculation())

    // Track the calculation in an effect that will re-execute when any observed state changes
    // This approach leverages the Effect system's dependency tracking
    LaunchedEffect(calculation) {
        // Update the derived state with the new calculation result
        state.value = calculation()
    }

    return state
}

/**
 * Creates and remembers a derivation of some [SummonMutableState] with explicit dependencies.
 *
 * @param vararg dependencies Objects that will trigger recalculation when they change
 * @param calculation The function to derive a state from the dependencies.
 * @return A [SummonMutableState] that updates when any dependency changes.
 */
@Composable
fun <T> derivedStateOf(vararg dependencies: Any?, calculation: () -> T): SummonMutableState<T> {
    // Create a mutableState to hold the derived value
    val state = rememberMutableStateOf(calculation())

    // Use dependencies array to trigger recalculation when any dependency changes
    // Pass dependencies directly as they are already a vararg array
    LaunchedEffect(dependencies) {
        // Update the derived state with the new calculation result
        state.value = calculation()
    }

    return state
} 