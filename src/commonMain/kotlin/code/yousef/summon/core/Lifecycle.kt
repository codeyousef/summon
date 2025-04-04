package code.yousef.summon

import code.yousef.summon.core.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Represents different states in a component's lifecycle.
 */
enum class LifecycleState {
    CREATED,
    STARTED,
    RESUMED,
    PAUSED,
    STOPPED,
    DESTROYED
}

/**
 * Interface for objects that need to be aware of lifecycle changes.
 */
interface LifecycleAware {
    /**
     * Called when the lifecycle state changes.
     * @param state The new lifecycle state
     */
    fun onLifecycleStateChanged(state: LifecycleState)
}

/**
 * LifecycleOwner manages lifecycle state and notifies observers when state changes.
 * In the context of Summon, this represents the lifecycle of a page or section during rendering.
 */
open class LifecycleOwner {
    private val observers = mutableListOf<LifecycleAware>()

    /**
     * Current lifecycle state
     */
    var state: LifecycleState = LifecycleState.CREATED
        protected set(value) {
            if (field != value) {
                field = value
                notifyObservers()
            }
        }

    /**
     * Adds a lifecycle observer
     * @param observer The observer to add
     */
    fun addObserver(observer: LifecycleAware) {
        observers.add(observer)
        // Immediately notify the observer of the current state
        observer.onLifecycleStateChanged(state)
    }

    /**
     * Removes a lifecycle observer
     * @param observer The observer to remove
     */
    fun removeObserver(observer: LifecycleAware) {
        observers.remove(observer)
    }

    /**
     * Moves the lifecycle to the CREATED state
     */
    fun onCreate() {
        state = LifecycleState.CREATED
    }

    /**
     * Moves the lifecycle to the STARTED state
     */
    fun onStart() {
        state = LifecycleState.STARTED
    }

    /**
     * Moves the lifecycle to the RESUMED state
     */
    fun onResume() {
        state = LifecycleState.RESUMED
    }

    /**
     * Moves the lifecycle to the PAUSED state
     */
    fun onPause() {
        state = LifecycleState.PAUSED
    }

    /**
     * Moves the lifecycle to the STOPPED state
     */
    fun onStop() {
        state = LifecycleState.STOPPED
    }

    /**
     * Moves the lifecycle to the DESTROYED state
     */
    fun onDestroy() {
        state = LifecycleState.DESTROYED
    }

    private fun notifyObservers() {
        observers.forEach { it.onLifecycleStateChanged(state) }
    }
}

/**
 * Component that responds to lifecycle events and executes callbacks based on the current state.
 * This is particularly useful for implementing effects that need to react to lifecycle events.
 *
 * In the context of Summon, this can manage different phases of static site generation and client-side hydration.
 */
class LifecycleAwareComponent(
    private val lifecycleOwner: LifecycleOwner,
    private val onCreate: (() -> Unit)? = null,
    private val onStart: (() -> Unit)? = null,
    private val onResume: (() -> Unit)? = null,
    private val onPause: (() -> Unit)? = null,
    private val onStop: (() -> Unit)? = null,
    private val onDestroy: (() -> Unit)? = null
) : LifecycleAware, Composable {

    init {
        lifecycleOwner.addObserver(this)
    }

    override fun onLifecycleStateChanged(state: LifecycleState) {
        when (state) {
            LifecycleState.CREATED -> onCreate?.invoke()
            LifecycleState.STARTED -> onStart?.invoke()
            LifecycleState.RESUMED -> onResume?.invoke()
            LifecycleState.PAUSED -> onPause?.invoke()
            LifecycleState.STOPPED -> onStop?.invoke()
            LifecycleState.DESTROYED -> onDestroy?.invoke()
        }
    }

    override fun <T> compose(receiver: T): T {
        // This component doesn't render anything; it just hooks into the lifecycle
        return receiver
    }

    /**
     * Removes this component from the lifecycle owner's observers
     */
    fun dispose() {
        lifecycleOwner.removeObserver(this)
    }
}

/**
 * Utility function to create a LifecycleAwareComponent.
 *
 * Example usage for page initialization:
 * ```
 * lifecycleAware(currentLifecycleOwner()) {
 *     onResume {
 *         // Initialize page when it becomes visible
 *         analytics.logPageView(currentPage)
 *     }
 *
 *     onDestroy {
 *         // Clean up resources when the page is destroyed
 *         cleanup()
 *     }
 * }
 * ```
 *
 * @param lifecycleOwner The lifecycle owner to observe
 * @param builder Lambda to configure lifecycle callbacks
 * @return A LifecycleAwareComponent
 */
fun lifecycleAware(
    lifecycleOwner: LifecycleOwner = currentLifecycleOwner(),
    builder: LifecycleAwareComponentBuilder.() -> Unit
): LifecycleAwareComponent {
    val componentBuilder = LifecycleAwareComponentBuilder()
    builder(componentBuilder)

    return LifecycleAwareComponent(
        lifecycleOwner = lifecycleOwner,
        onCreate = componentBuilder.onCreateCallback,
        onStart = componentBuilder.onStartCallback,
        onResume = componentBuilder.onResumeCallback,
        onPause = componentBuilder.onPauseCallback,
        onStop = componentBuilder.onStopCallback,
        onDestroy = componentBuilder.onDestroyCallback
    )
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
 * LifecycleCoroutineScope provides a coroutine scope tied to a component's lifecycle.
 * Coroutines launched in this scope are automatically cancelled when the lifecycle
 * transitions to the DESTROYED state.
 */
class LifecycleCoroutineScope(private val lifecycleOwner: LifecycleOwner) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    init {
        lifecycleOwner.addObserver(object : LifecycleAware {
            override fun onLifecycleStateChanged(state: LifecycleState) {
                if (state == LifecycleState.DESTROYED) {
                    job?.cancel()
                    job = null
                }
            }
        })
    }

    /**
     * Launches a coroutine in this scope.
     */
    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        job?.cancel()
        job = coroutineScope.launch {
            block()
        }
        return job!!
    }
}

/**
 * Returns the lifecycle coroutine scope for the given lifecycle owner.
 *
 * @param lifecycleOwner The lifecycle owner to create a scope for
 * @return A LifecycleCoroutineScope tied to the lifecycle owner
 */
fun lifecycleCoroutineScope(lifecycleOwner: LifecycleOwner = currentLifecycleOwner()): LifecycleCoroutineScope {
    return LifecycleCoroutineScope(lifecycleOwner)
}

/**
 * Gets the current platform-specific lifecycle owner.
 * Each platform (JVM, JS) provides its own implementation.
 *
 * @return The current lifecycle owner
 */
expect fun currentLifecycleOwner(): LifecycleOwner

/**
 * Executes the given block when the lifecycle owner enters the active state
 * (STARTED or RESUMED) and cancels it when the lifecycle owner transitions to
 * an inactive state (PAUSED, STOPPED, or DESTROYED).
 *
 * This is similar to LaunchedEffect but integrated with the lifecycle.
 *
 * @param lifecycleOwner The lifecycle owner to observe
 * @param key A key that determines when the effect should be restarted
 * @param block The suspend function to execute as a side effect
 * @return A LifecycleAwareComponent that can be disposed
 */
fun whenActive(
    lifecycleOwner: LifecycleOwner = currentLifecycleOwner(),
    key: Any,
    block: suspend CoroutineScope.() -> Unit
): LifecycleAwareComponent {
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