package codes.yousef.summon.lifecycle

import kotlinx.browser.window
import org.w3c.dom.events.Event

@JsFun("() => document.visibilityState")
private external fun documentVisibilityState(): String
/** Supported lifecycle state values. */
actual enum class LifecycleState {
    /** The initialized lifecycle state option. */
    INITIALIZED,
    /** The created lifecycle state option. */
    CREATED,
    /** The started lifecycle state option. */
    STARTED,
    /** The resumed lifecycle state option. */
    RESUMED,
    /** The paused lifecycle state option. */
    PAUSED,
    /** The stopped lifecycle state option. */
    STOPPED,
    /** The destroyed lifecycle state option. */
    DESTROYED
}

/** Contract for lifecycle observer. */
actual interface LifecycleObserver {
    /** Handles create. */
    actual fun onCreate()
    /** Handles start. */
    actual fun onStart()
    /** Handles resume. */
    actual fun onResume()
    /** Handles pause. */
    actual fun onPause()
    /** Handles stop. */
    actual fun onStop()
    /** Handles destroy. */
    actual fun onDestroy()
}

/** Browser lifecycle owner for WASM with stable host listeners and deterministic replay. */
actual class LifecycleOwner {
    private val observers = mutableListOf<LifecycleObserver>()
    private var ownedScope: LifecycleCoroutineScope? = null
    private var hostListenersAttached = false
    private val visibilityListener: (Event) -> Unit = { handleVisibilityChange() }
    private val pageHideListener: (Event) -> Unit = { destroy() }

    /** The property declaration value. */
    actual var currentState: LifecycleState = LifecycleState.INITIALIZED
        set(value) {
            if (field == LifecycleState.DESTROYED) return
            if (field == value) return
            field = value
            try {
                notifyObservers(value)
            } finally {
                if (value == LifecycleState.DESTROYED) releaseHostListeners()
            }
        }

    init {
        currentState = LifecycleState.CREATED
        currentState = LifecycleState.STARTED
        currentState = LifecycleState.RESUMED
        window.addEventListener("visibilitychange", visibilityListener)
        window.addEventListener("pagehide", pageHideListener)
        window.addEventListener("beforeunload", pageHideListener)
        hostListenersAttached = true
    }

    private fun handleVisibilityChange() {
        if (currentState == LifecycleState.DESTROYED) return
        if (documentVisibilityState() == "hidden") {
            currentState = LifecycleState.PAUSED
            currentState = LifecycleState.STOPPED
        } else {
            currentState = LifecycleState.STARTED
            currentState = LifecycleState.RESUMED
        }
    }

    private fun destroy() {
        if (currentState == LifecycleState.DESTROYED) return
        if (currentState != LifecycleState.STOPPED) currentState = LifecycleState.STOPPED
        currentState = LifecycleState.DESTROYED
    }

    private fun releaseHostListeners() {
        if (!hostListenersAttached) return
        hostListenersAttached = false
        window.removeEventListener("visibilitychange", visibilityListener)
        window.removeEventListener("pagehide", pageHideListener)
        window.removeEventListener("beforeunload", pageHideListener)
    }

    private fun notifyObservers(state: LifecycleState) {
        var failure: Throwable? = null
        ArrayList(observers).forEach { observer ->
            try {
                notifyObserver(observer, state)
            } catch (error: Throwable) {
                if (failure == null) failure = error
            }
        }
        failure?.let { throw it }
    }

    /**
     * Adds observer.
     *
     * @param observer The observer value.
     */
    actual fun addObserver(observer: LifecycleObserver) {
        if (observers.contains(observer)) return
        observers.add(observer)
        try {
            replayCurrentState(observer)
        } catch (error: Throwable) {
            observers.remove(observer)
            throw error
        }
    }

    /**
     * Removes observer.
     *
     * @param observer The observer value.
     */
    actual fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }
    actual internal fun lifecycleScopeOrCreate(
        factory: () -> LifecycleCoroutineScope
    ): LifecycleCoroutineScope = ownedScope ?: factory().also { ownedScope = it }

    actual internal fun clearLifecycleScope(scope: LifecycleCoroutineScope) {
        if (ownedScope === scope) ownedScope = null
    }

    private fun replayCurrentState(observer: LifecycleObserver) {
        when (currentState) {
            LifecycleState.INITIALIZED -> Unit
            LifecycleState.CREATED -> observer.onCreate()
            LifecycleState.STARTED -> {
                observer.onCreate()
                observer.onStart()
            }
            LifecycleState.RESUMED -> {
                observer.onCreate()
                observer.onStart()
                observer.onResume()
            }
            LifecycleState.PAUSED -> observer.onPause()
            LifecycleState.STOPPED -> observer.onStop()
            LifecycleState.DESTROYED -> observer.onDestroy()
        }
    }

    private fun notifyObserver(observer: LifecycleObserver, state: LifecycleState) {
        when (state) {
            LifecycleState.INITIALIZED -> Unit
            LifecycleState.CREATED -> observer.onCreate()
            LifecycleState.STARTED -> observer.onStart()
            LifecycleState.RESUMED -> observer.onResume()
            LifecycleState.PAUSED -> observer.onPause()
            LifecycleState.STOPPED -> observer.onStop()
            LifecycleState.DESTROYED -> observer.onDestroy()
        }
    }
}

private val wasmLifecycleOwnerInstance = LifecycleOwner()

/**
 * Executes the current lifecycle owner operation.
 *
 * @return The resulting value.
 */
actual fun currentLifecycleOwner(): LifecycleOwner? = wasmLifecycleOwnerInstance