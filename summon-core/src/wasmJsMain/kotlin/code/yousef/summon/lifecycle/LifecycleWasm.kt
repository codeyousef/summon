package codes.yousef.summon.lifecycle

import codes.yousef.summon.runtime.safeWasmConsoleLog
import codes.yousef.summon.runtime.safeWasmConsoleWarn

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

/**
 * WASM-specific implementation of LifecycleOwner.
 * Provides lifecycle management for WASM test environment.
 */
actual class LifecycleOwner {
    private val observers = mutableListOf<LifecycleObserver>()

    actual var currentState: LifecycleState = LifecycleState.INITIALIZED
        set(value) {
            if (field != value) {
                safeWasmConsoleLog("LifecycleOwner.currentState transitioning: ${field} -> $value")
                field = value
                notifyObservers(value)
            }
        }

    init {
        // Initialize lifecycle state progression for WASM test environment
        setState(LifecycleState.CREATED)
        setState(LifecycleState.STARTED)
        setState(LifecycleState.RESUMED)
    }

    private fun setState(newState: LifecycleState) {
        if (newState != currentState) {
            currentState = newState
        }
    }

    private fun notifyObservers(newState: LifecycleState) {
        try {
            // Create a copy to avoid concurrent modification
            val observersCopy = ArrayList(observers)

            observersCopy.forEach { observer ->
                try {
                    when (newState) {
                        LifecycleState.CREATED -> observer.onCreate()
                        LifecycleState.STARTED -> observer.onStart()
                        LifecycleState.RESUMED -> observer.onResume()
                        LifecycleState.PAUSED -> observer.onPause()
                        LifecycleState.STOPPED -> observer.onStop()
                        LifecycleState.DESTROYED -> observer.onDestroy()
                        else -> { /* No action for INITIALIZED */
                        }
                    }
                } catch (e: Exception) {
                    safeWasmConsoleWarn("Error notifying lifecycle observer: ${e.message}")
                }
            }
        } catch (e: Exception) {
            safeWasmConsoleWarn("Error during lifecycle notification: ${e.message}")
        }
    }

    actual fun addObserver(observer: LifecycleObserver) {
        try {
            if (!observers.contains(observer)) {
                safeWasmConsoleLog("Adding lifecycle observer")
                observers.add(observer)

                // Notify new observer of current state
                if (currentState >= LifecycleState.CREATED) {
                    when (currentState) {
                        LifecycleState.RESUMED -> {
                            observer.onCreate()
                            observer.onStart()
                            observer.onResume()
                        }

                        LifecycleState.STARTED -> {
                            observer.onCreate()
                            observer.onStart()
                        }

                        LifecycleState.CREATED -> {
                            observer.onCreate()
                        }

                        else -> { /* No action needed */
                        }
                    }
                }
            }
        } catch (e: Exception) {
            safeWasmConsoleWarn("Failed to add lifecycle observer: ${e.message}")
        }
    }

    actual fun removeObserver(observer: LifecycleObserver) {
        try {
            safeWasmConsoleLog("Removing lifecycle observer")
            observers.remove(observer)
        } catch (e: Exception) {
            safeWasmConsoleWarn("Failed to remove lifecycle observer: ${e.message}")
        }
    }
}

// Provide a single instance for the WASM environment
private val wasmLifecycleOwnerInstance = LifecycleOwner()

/**
 * Gets the current WASM-specific lifecycle owner.
 * Returns a proper instance instead of null to support tests.
 */
actual fun currentLifecycleOwner(): LifecycleOwner? {
    safeWasmConsoleLog("Getting current WASM lifecycle owner")
    return wasmLifecycleOwnerInstance
}