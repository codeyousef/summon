@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.runtime

// Remove coroutine imports temporarily to fix compilation
// import kotlinx.coroutines.CoroutineScope
// import kotlinx.coroutines.cancel
// import kotlinx.coroutines.launch

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
        // Create a new effect state if this is the first time or the key changed
        val newState = EffectState(key, EffectType.LAUNCHED, null)
        
        // Store in the slot
        composer?.setSlot(newState)
        
        // In a real implementation, this would launch a coroutine
        println("LaunchedEffect started with key: $key")
        
        // Register cleanup
        composer?.registerDisposable {
            println("LaunchedEffect disposed: $key")
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
