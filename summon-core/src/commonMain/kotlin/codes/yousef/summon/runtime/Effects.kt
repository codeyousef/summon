@file:Suppress("UNCHECKED_CAST")

package codes.yousef.summon.runtime

import kotlinx.coroutines.*

/**
 * Launches a coroutine tied to the composition lifecycle.
 *
 * LaunchedEffect is a composable function that executes suspending side effects within the
 * composition scope. The coroutine is automatically managed by the composition system:
 * launched when the effect enters composition, cancelled when it leaves or when keys change.
 *
 * ## Lifecycle Management
 *
 * The effect follows the composition lifecycle:
 * - **Enter Composition**: Coroutine is launched with the provided block
 * - **Key Change**: Previous coroutine is cancelled, new one launched
 * - **Leave Composition**: Coroutine is cancelled and cleaned up
 * - **Exception Handling**: Exceptions are caught and logged without crashing
 *
 * ## Key-Based Re-execution
 *
 * When keys change, the effect is restarted:
 * - Previous coroutine is cancelled immediately
 * - New coroutine is launched with the same block
 * - This enables reactive side effects based on state changes
 *
 * ## Thread Safety
 *
 * LaunchedEffect uses [Dispatchers.Default] by default, but the block can switch
 * dispatchers as needed. The cancellation mechanism ensures proper cleanup
 * regardless of the dispatcher used.
 *
 * ## Common Use Cases
 *
 * ### API Calls
 * ```kotlin
 * @Composable
 * fun UserProfile(userId: String) {
 *     var user by remember { mutableStateOf<User?>(null) }
 *
 *     LaunchedEffect(userId) {
 *         user = userRepository.getUser(userId)
 *     }
 *
 *     user?.let { UserCard(it) }
 * }
 * ```
 *
 * ### Periodic Updates
 * ```kotlin
 * @Composable
 * fun Clock() {
 *     var time by remember { mutableStateOf(getCurrentTime()) }
 *
 *     LaunchedEffect(Unit) {
 *         while (true) {
 *             delay(1000)
 *             time = getCurrentTime()
 *         }
 *     }
 *
 *     Text("Current time: $time")
 * }
 * ```
 *
 * ### Event Listeners
 * ```kotlin
 * @Composable
 * fun LocationTracker() {
 *     var location by remember { mutableStateOf<Location?>(null) }
 *
 *     LaunchedEffect(Unit) {
 *         locationService.subscribe { newLocation ->
 *             location = newLocation
 *         }
 *     }
 *
 *     location?.let { DisplayLocation(it) }
 * }
 * ```
 *
 * ## Error Handling
 *
 * Exceptions in the block are automatically caught and logged to prevent
 * crashes. For custom error handling, wrap your code in try-catch:
 *
 * ```kotlin
 * LaunchedEffect(key) {
 *     try {
 *         riskyOperation()
 *     } catch (e: Exception) {
 *         errorState.value = e.message
 *     }
 * }
 * ```
 *
 * ## Performance Considerations
 *
 * - Use specific keys to avoid unnecessary re-execution
 * - Prefer [remember] for expensive computations that don't need suspension
 * - Use [DisposableEffect] for non-suspending cleanup operations
 * - Avoid creating new objects as keys to prevent constant re-execution
 *
 * @param key Identifier for this effect - changes trigger re-execution
 * @param block Suspending function to execute in the launched coroutine
 * @see DisposableEffect
 * @see SideEffect
 * @see remember
 * @since 1.0.0
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
