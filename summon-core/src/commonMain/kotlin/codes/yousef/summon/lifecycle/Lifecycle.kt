package codes.yousef.summon.lifecycle

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

/**
 * Owns lifecycle observers and one lazily created [LifecycleCoroutineScope].
 *
 * Assigning [currentState] dispatches the corresponding event. Browser owners also map document
 * visibility and page teardown to lifecycle transitions. Set [LifecycleState.DESTROYED] when a
 * manually created owner is retired.
 */
expect class LifecycleOwner {
    var currentState: LifecycleState
    fun addObserver(observer: LifecycleObserver)
    fun removeObserver(observer: LifecycleObserver)
    internal fun lifecycleScopeOrCreate(factory: () -> LifecycleCoroutineScope): LifecycleCoroutineScope
    internal fun clearLifecycleScope(scope: LifecycleCoroutineScope)
}

/**
 * Provides access to the current platform-specific LifecycleOwner.
 * Changed to function signature to match compiler expectations.
 */
expect fun currentLifecycleOwner(): LifecycleOwner? 