@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.runtime

import kotlinx.coroutines.*

/**
 * Side effect that launches a coroutine when the composition enters the scene and cancels it when it leaves.
 * This is useful for running asynchronous operations in a composable function.
 *
 * @param key The key used to identify this side effect. If the key changes, the side effect will be re-executed.
 * @param block The code to execute in the coroutine.
 */
@Composable
fun LaunchedEffect(key: Any? = null, block: suspend () -> Unit) {
    val composer = CompositionLocal.currentComposer

    // Get the current slot for this effect
    composer?.nextSlot()
    val effectState = composer?.getSlot() as? EffectState

    if (effectState == null || hasKeyChanged(effectState.key, key)) {
        // Clean up previous effect if any
        if (effectState?.cleanup != null) {
            (effectState.cleanup as Job).cancel()
        }

        // Create a coroutine scope for this effect
        val coroutineScope = CoroutineScope(Dispatchers.Default)

        // Launch the coroutine
        val job = coroutineScope.launch {
            try {
                block()
            } catch (e: Exception) {
                // Log the exception but don't crash the app
                println("Exception in LaunchedEffect: ${e.message}")
                e.printStackTrace()
            }
        }

        // Create a new effect state with the job for cleanup
        val newState = EffectState(key, EffectType.LAUNCHED, job)

        // Store in the slot
        composer?.setSlot(newState)

        // Register cleanup to cancel the coroutine when the effect is disposed
        composer?.registerDisposable {
            job.cancel()
            coroutineScope.cancel()
        }
    }
}

/**
 * Side effect that performs an action when the composition enters the scene and when it leaves.
 * The [effect] function should return a cleanup function that will be called when the side effect is
 * disposed or when the key changes.
 *
 * @param key The key used to identify this side effect. If the key changes, the side effect will be re-executed.
 * @param effect The side effect function that returns a cleanup function.
 */
@Composable
fun DisposableEffect(key: Any? = null, effect: () -> (() -> Unit)) {
    val composer = CompositionLocal.currentComposer

    // Get the current slot for this effect
    composer?.nextSlot()
    val effectState = composer?.getSlot() as? EffectState

    if (effectState == null || hasKeyChanged(effectState.key, key)) {
        // Clean up previous effect if any
        if (effectState?.cleanup != null) {
            (effectState.cleanup as () -> Unit).invoke()
        }

        // Call the effect function to get the cleanup function
        val cleanup = effect()

        // Create a new effect state with the cleanup function
        val newState = EffectState(key, EffectType.DISPOSABLE, cleanup)

        // Store in the slot
        composer?.setSlot(newState)

        // Register the cleanup with the composer
        composer?.registerDisposable(cleanup)
    }
}

/**
 * Side effect that is executed after every successful composition.
 * @param effect The effect to execute.
 */
@Composable
fun SideEffect(effect: () -> Unit) {
    val composer = CompositionLocal.currentComposer

    // Execute the effect on every composition
    if (composer?.inserting == true) {
        effect()
    }
}

/**
 * Enum defining the types of effects.
 */
private enum class EffectType {
    LAUNCHED,
    DISPOSABLE
}

/**
 * State object for tracking effects.
 */
private data class EffectState(
    val key: Any?,
    val type: EffectType,
    val cleanup: Any?
)

/**
 * Check if the key has changed.
 */
private fun hasKeyChanged(oldKey: Any?, newKey: Any?): Boolean {
    return oldKey != newKey
} 
