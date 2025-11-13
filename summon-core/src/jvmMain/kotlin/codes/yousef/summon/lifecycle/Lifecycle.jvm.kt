package codes.yousef.summon.lifecycle

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