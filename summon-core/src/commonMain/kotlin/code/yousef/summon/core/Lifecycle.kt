package code.yousef.summon

import code.yousef.summon.lifecycle.LifecycleObserver
import code.yousef.summon.lifecycle.LifecycleOwner
import code.yousef.summon.lifecycle.LifecycleState
import code.yousef.summon.lifecycle.currentLifecycleOwner
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.DisposableEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Interface for objects that need to be aware of lifecycle changes.
 * NOTE: This might need adjustment if it relies heavily on the old LifecycleOwner implementation.
 */
interface LifecycleAware {
    /**
     * Called when the lifecycle state changes.
     * @param state The new lifecycle state (using the expect enum)
     */
    fun onLifecycleStateChanged(state: LifecycleState)
}

/**
 * Component that responds to lifecycle events and executes callbacks based on the current state.
 * This is particularly useful for implementing effects that need to react to lifecycle events.
 *
 * In the context of Summon, this can manage different phases of static site generation and client-side hydration.
 * NOTE: Updated to use the expect LifecycleOwner interface.
 */
class LifecycleAwareComponent(
    private val lifecycleOwner: LifecycleOwner,
    private val onCreate: (() -> Unit)? = null,
    private val onStart: (() -> Unit)? = null,
    private val onResume: (() -> Unit)? = null,
    private val onPause: (() -> Unit)? = null,
    private val onStop: (() -> Unit)? = null,
    private val onDestroy: (() -> Unit)? = null
) : LifecycleAware, @Suppress("DEPRECATION") code.yousef.summon.core.Composable {

    override fun onLifecycleStateChanged(state: LifecycleState) {
        when (state) {
            LifecycleState.CREATED -> onCreate?.invoke()
            LifecycleState.STARTED -> onStart?.invoke()
            LifecycleState.RESUMED -> onResume?.invoke()
            LifecycleState.PAUSED -> onPause?.invoke()
            LifecycleState.STOPPED -> onStop?.invoke()
            LifecycleState.DESTROYED -> onDestroy?.invoke()
            else -> { /* Handle INITIALIZED if necessary */
            }
        }
    }

    override fun <T> compose(receiver: T): T {
        // This component doesn't render anything; it just hooks into the lifecycle
        return receiver
    }
}

/**
 * Utility function to create a LifecycleAwareComponent.
 *
 * Example usage for page initialization:
 * ```
 * // Usage might change based on how LifecycleAwareComponent is adapted
 * lifecycleAware(currentLifecycleOwner) { // Use expect val
 *     onResume {
 *         // Initialize page when it becomes visible
 *         // analytics.logPageView(currentPage)
 *     }
 *
 *     onDestroy {
 *         // Clean up resources when the page is destroyed
 *         // cleanup()
 *     }
 * }
 * ```
 *
 * @param lifecycleOwner The lifecycle owner to observe (expect interface)
 * @param builder Lambda to configure lifecycle callbacks
 * @return A LifecycleAwareComponent
 * NOTE: This function might need removal or significant refactoring if LifecycleAwareComponent changes.
 */
fun lifecycleAware(
    lifecycleOwnerInput: LifecycleOwner? = currentLifecycleOwner(),
    builder: LifecycleAwareComponentBuilder.() -> Unit
): LifecycleAwareComponent? {
    val lifecycleOwner = lifecycleOwnerInput ?: return null

    val componentBuilder = LifecycleAwareComponentBuilder()
    builder(componentBuilder)

    val component = LifecycleAwareComponent(
        lifecycleOwner = lifecycleOwner,
        onCreate = componentBuilder.onCreateCallback,
        onStart = componentBuilder.onStartCallback,
        onResume = componentBuilder.onResumeCallback,
        onPause = componentBuilder.onPauseCallback,
        onStop = componentBuilder.onStopCallback,
        onDestroy = componentBuilder.onDestroyCallback
    )

    return component
}

/**
 * Builder class for configuring lifecycle callbacks.
 */
class LifecycleAwareComponentBuilder {
    var onCreateCallback: (() -> Unit)? = null
    var onStartCallback: (() -> Unit)? = null
    var onResumeCallback: (() -> Unit)? = null
    var onPauseCallback: (() -> Unit)? = null
    var onStopCallback: (() -> Unit)? = null
    var onDestroyCallback: (() -> Unit)? = null

    /**
     * Sets the callback for the CREATED lifecycle state.
     */
    fun onCreate(callback: () -> Unit) {
        onCreateCallback = callback
    }

    /**
     * Sets the callback for the STARTED lifecycle state.
     */
    fun onStart(callback: () -> Unit) {
        onStartCallback = callback
    }

    /**
     * Sets the callback for the RESUMED lifecycle state.
     */
    fun onResume(callback: () -> Unit) {
        onResumeCallback = callback
    }

    /**
     * Sets the callback for the PAUSED lifecycle state.
     */
    fun onPause(callback: () -> Unit) {
        onPauseCallback = callback
    }

    /**
     * Sets the callback for the STOPPED lifecycle state.
     */
    fun onStop(callback: () -> Unit) {
        onStopCallback = callback
    }

    /**
     * Sets the callback for the DESTROYED lifecycle state.
     */
    fun onDestroy(callback: () -> Unit) {
        onDestroyCallback = callback
    }
}

/**
 * Executes the given block when the lifecycle owner enters the active state
 * (STARTED or RESUMED) and cancels it when the lifecycle owner transitions to
 * an inactive state (PAUSED, STOPPED, or DESTROYED).
 *
 * This is similar to LaunchedEffect but integrated with the lifecycle.
 *
 * @param lifecycleOwner The lifecycle owner to observe. Defaults to the current owner.
 * @param key A key that determines when the effect should be restarted
 * @param block The suspend function to execute as a side effect
 * @return A LifecycleAwareComponent that can be disposed, or null if no owner is available.
 */
fun whenActive(
    lifecycleOwnerInput: LifecycleOwner? = currentLifecycleOwner(),
    key: Any,
    block: suspend CoroutineScope.() -> Unit
): LifecycleAwareComponent? {
    val lifecycleOwner = lifecycleOwnerInput ?: return null

    var currentKey: Any = key
    var activeJob: Job? = null
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    return lifecycleAware(lifecycleOwner) {
        val executeEffect = {
            if (activeJob == null || key != currentKey) {
                activeJob?.cancel()
                currentKey = key
                activeJob = coroutineScope.launch(block = block)
            }
        }

        val cancelEffect = {
            activeJob?.cancel()
            activeJob = null
        }

        onStart {
            executeEffect()
        }

        onPause {
            cancelEffect()
        }

        onDestroy {
            cancelEffect()
        }
    }
}

// --- Potentially keep or refactor CoroutineScope integration --- 
// Need to evaluate if this is still the right approach
// Commenting out for now as it causes issues with nullable LifecycleOwner
/*
/**
 * Scope associated with a LifecycleOwner that cancels when DESTROYED.
 */
val LifecycleOwner.coroutineScope: CoroutineScope
    get() {
        // Implementation needs review - depends on how LifecycleOwner is managed
        // This might need to become an extension on the specific LifecycleOwner implementations
        // or use a different mechanism.
        return CoroutineScope(Dispatchers.Main + Job())
    }

/**
 * Launches a coroutine tied to the LifecycleOwner's scope.
 */
fun LifecycleOwner.launchWhenCreated(block: suspend CoroutineScope.() -> Unit): Job {
    // Needs review
    return coroutineScope.launch { /* ... */ }
}

fun LifecycleOwner.launchWhenStarted(block: suspend CoroutineScope.() -> Unit): Job {
    // Needs review
    return coroutineScope.launch { /* ... */ }
}

fun LifecycleOwner.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job {
    // Needs review
    return coroutineScope.launch { /* ... */ }
}
*/

/**
 * A composable function that observes lifecycle events and invokes callbacks accordingly.
 * This replaces the non-composable LifecycleAwareComponent implementation.
 *
 * @param lifecycleOwner The lifecycle owner to observe. Defaults to currentLifecycleOwner().
 * @param key An optional key to force the effect to restart when changed.
 * @param onCreate Callback invoked when lifecycle state is CREATED.
 * @param onStart Callback invoked when lifecycle state is STARTED.
 * @param onResume Callback invoked when lifecycle state is RESUMED.
 * @param onPause Callback invoked when lifecycle state is PAUSED.
 * @param onStop Callback invoked when lifecycle state is STOPPED.
 * @param onDestroy Callback invoked when lifecycle state is DESTROYED.
 */
@Composable
fun LifecycleEffect(
    lifecycleOwner: LifecycleOwner? = currentLifecycleOwner(),
    key: Any? = null,
    onCreate: (() -> Unit)? = null,
    onStart: (() -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onPause: (() -> Unit)? = null,
    onStop: (() -> Unit)? = null,
    onDestroy: (() -> Unit)? = null
) {
    // If no lifecycle owner is available, do nothing
    lifecycleOwner ?: return

    // Use DisposableEffect to register and unregister the observer
    DisposableEffect(key) {
        // Create a lifecycle observer
        val observer = object : LifecycleObserver {
            override fun onCreate() {
                onCreate?.invoke()
            }

            override fun onStart() {
                onStart?.invoke()
            }

            override fun onResume() {
                onResume?.invoke()
            }

            override fun onPause() {
                onPause?.invoke()
            }

            override fun onStop() {
                onStop?.invoke()
            }

            override fun onDestroy() {
                onDestroy?.invoke()
            }
        }

        // Register the observer
        lifecycleOwner.addObserver(observer)

        // Immediately trigger callbacks based on current state
        when (lifecycleOwner.currentState) {
            LifecycleState.CREATED -> {
                onCreate?.invoke()
            }

            LifecycleState.STARTED -> {
                onCreate?.invoke()
                onStart?.invoke()
            }

            LifecycleState.RESUMED -> {
                onCreate?.invoke()
                onStart?.invoke()
                onResume?.invoke()
            }

            LifecycleState.PAUSED -> {
                onCreate?.invoke()
                onStart?.invoke()
                onResume?.invoke()
                onPause?.invoke()
            }

            LifecycleState.STOPPED -> {
                onCreate?.invoke()
                onStart?.invoke()
                onResume?.invoke()
                onPause?.invoke()
                onStop?.invoke()
            }

            LifecycleState.DESTROYED -> {
                onCreate?.invoke()
                onStart?.invoke()
                onResume?.invoke()
                onPause?.invoke()
                onStop?.invoke()
                onDestroy?.invoke()
            }

            else -> {} // INITIALIZED state doesn't trigger any callbacks
        }

        // Return a dispose function to clean up
        {
            lifecycleOwner.removeObserver(observer)
        }
    }
} 