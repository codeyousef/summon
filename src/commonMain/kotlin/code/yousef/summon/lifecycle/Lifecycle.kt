package code.yousef.summon.lifecycle

/**
 * Expected declarations for platform-specific lifecycle management.
 */

expect enum class LifecycleState {
    INITIALIZED,
    CREATED,
    STARTED,
    RESUMED,
    PAUSED,
    STOPPED,
    DESTROYED
}

expect interface LifecycleObserver {
    fun onCreate()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
}

expect interface LifecycleOwner {
    val currentState: LifecycleState
    fun addObserver(observer: LifecycleObserver)
    fun removeObserver(observer: LifecycleObserver)
}

/**
 * Provides access to the current platform-specific LifecycleOwner.
 * Changed to function signature to match compiler expectations.
 */
expect fun currentLifecycleOwner(): LifecycleOwner? 