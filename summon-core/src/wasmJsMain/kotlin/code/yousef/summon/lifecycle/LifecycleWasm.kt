package code.yousef.summon.lifecycle

import code.yousef.summon.runtime.wasmConsoleLog

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

actual class LifecycleOwner {
    actual var currentState: LifecycleState = LifecycleState.INITIALIZED
        set(value) {
            wasmConsoleLog("LifecycleOwner.currentState set to: $value - WASM stub")
            field = value
        }

    private val observers = mutableListOf<LifecycleObserver>()

    actual fun addObserver(observer: LifecycleObserver) {
        wasmConsoleLog("LifecycleOwner.addObserver - WASM stub")
        observers.add(observer)
    }

    actual fun removeObserver(observer: LifecycleObserver) {
        wasmConsoleLog("LifecycleOwner.removeObserver - WASM stub")
        observers.remove(observer)
    }
}

actual fun currentLifecycleOwner(): LifecycleOwner? {
    wasmConsoleLog("Getting current lifecycle owner - WASM stub")
    return null
}