package codes.yousef.summon.lifecycle

import java.util.concurrent.CopyOnWriteArrayList

/** JVM lifecycle owner. Assigning [currentState] dispatches the matching lifecycle event. */
actual class LifecycleOwner {
    private val observers = CopyOnWriteArrayList<LifecycleObserver>()
    private var ownedScope: LifecycleCoroutineScope? = null

    actual var currentState: LifecycleState = LifecycleState.INITIALIZED
        set(value) {
            if (field == LifecycleState.DESTROYED) return
            if (field == value) return
            field = value
            notifyObservers(value)
        }

    init {
        currentState = LifecycleState.CREATED
        currentState = LifecycleState.STARTED
        currentState = LifecycleState.RESUMED
    }

    private fun notifyObservers(state: LifecycleState) {
        var failure: Throwable? = null
        observers.forEach { observer ->
            try {
                notifyObserver(observer, state)
            } catch (error: Throwable) {
                if (failure == null) failure = error
            }
        }
        failure?.let { throw it }
    }

    actual fun addObserver(observer: LifecycleObserver) {
        if (!observers.addIfAbsent(observer)) return
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
    @Synchronized
    actual internal fun lifecycleScopeOrCreate(
        factory: () -> LifecycleCoroutineScope
    ): LifecycleCoroutineScope = ownedScope ?: factory().also { ownedScope = it }

    @Synchronized
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

private val lifecycleOwnerInstance = LifecycleOwner()

actual fun currentLifecycleOwner(): LifecycleOwner? = lifecycleOwnerInstance

// Redundant 'actual enum class LifecycleState', 'actual interface LifecycleObserver',
// and 'actual interface LifecycleOwner' that were previously in this file have been removed.
// The 'expect' declarations in commonMain/lifecycle/Lifecycle.kt are the single source of truth for these types.