package codes.yousef.summon.lifecycle

import kotlinx.browser.window
import org.w3c.dom.events.Event

@JsFun("() => document.visibilityState")
private external fun documentVisibilityState(): String
actual enum class LifecycleState {
    INITIALIZED,
    CREATED,
    STARTED,
    RESUMED,
    PAUSED,
    STOPPED,
    DESTROYED
}

actual interface LifecycleObserver {
    actual fun onCreate()
    actual fun onStart()
    actual fun onResume()
    actual fun onPause()
    actual fun onStop()
    actual fun onDestroy()
}

/** Browser lifecycle owner for WASM with stable host listeners and deterministic replay. */
actual class LifecycleOwner {
    private val observers = mutableListOf<LifecycleObserver>()
    private var ownedScope: LifecycleCoroutineScope? = null
    private var hostListenersAttached = false
    private val visibilityListener: (Event) -> Unit = { handleVisibilityChange() }
    private val pageHideListener: (Event) -> Unit = { destroy() }

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

actual fun currentLifecycleOwner(): LifecycleOwner? = wasmLifecycleOwnerInstance