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
 * - **Exception Handling**: Cancellation propagates normally; failures emit a generic diagnostic
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
 * Handle expected failures inside the block. Unhandled failures reach the coroutine
 * exception handler emits a generic diagnostic without exception payloads.
 * Rethrow CancellationException when catching exceptions:
 *
 * ```kotlin
 * LaunchedEffect(key) {
 *     try {
 *         riskyOperation()
 *     } catch (e: CancellationException) {
 *         throw e
 *     } catch (e: Exception) {
 *         errorState.value = "Operation unavailable"
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
    val composer = requireNotNull(CompositionLocal.currentComposer) {
        "LaunchedEffect requires an active composition"
    }
    val previous = composer.getSlot() as? EffectState
    if (previous != null && previous.type == EffectType.LAUNCHED && previous.key == key) {
        composer.nextSlot()
        return
    }
    previous?.dispose()
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineExceptionHandler { _, _ ->
        println("Summon launched effect failed")
    })
    val state = EffectState(key, EffectType.LAUNCHED) { scope.cancel() }
    composer.setSlot(state)
    composer.nextSlot()
    if (!Recomposer.isComposerImpl(composer)) composer.registerDisposable(state::dispose)
    scope.launch { block() }

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
    val composer = requireNotNull(CompositionLocal.currentComposer) {
        "DisposableEffect requires an active composition"
    }
    val previous = composer.getSlot() as? EffectState
    if (previous != null && previous.type == EffectType.DISPOSABLE && previous.key == key) {
        composer.nextSlot()
        return
    }
    previous?.dispose()
    val state = EffectState(key, EffectType.DISPOSABLE, effect())
    composer.setSlot(state)
    composer.nextSlot()
    if (!Recomposer.isComposerImpl(composer)) composer.registerDisposable(state::dispose)

}

/**
 * Side effect that is executed after every successful composition.
 * @param effect The effect to execute.
 */
@Composable
fun SideEffect(effect: () -> Unit) {
    val composer = CompositionLocal.currentComposer ?: return
    if (!Recomposer.enqueueSideEffect(composer, effect) && composer.inserting) effect()

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
private class EffectState(
    val key: Any?,
    val type: EffectType,
    cleanup: () -> Unit
) : CompositionResource {
    private var cleanup: (() -> Unit)? = cleanup

    override fun dispose() {
        val action = cleanup ?: return
        cleanup = null
        action()
    }
}
