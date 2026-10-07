package codes.yousef.summon.lifecycle

/**
 * Expected declarations for platform-specific lifecycle management.
 */

expect enum class LifecycleState {
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
expect interface LifecycleObserver {
    /** Handles create. */
    fun onCreate()
    /** Handles start. */
    fun onStart()
    /** Handles resume. */
    fun onResume()
    /** Handles pause. */
    fun onPause()
    /** Handles stop. */
    fun onStop()
    /** Handles destroy. */
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
    /** The property declaration value. */
    var currentState: LifecycleState
    /**
     * Adds observer.
     *
     * @param observer The observer value.
     */
    fun addObserver(observer: LifecycleObserver)
    /**
     * Removes observer.
     *
     * @param observer The observer value.
     */
    fun removeObserver(observer: LifecycleObserver)
    internal fun lifecycleScopeOrCreate(factory: () -> LifecycleCoroutineScope): LifecycleCoroutineScope
    internal fun clearLifecycleScope(scope: LifecycleCoroutineScope)
}

/**
 * Provides access to the current platform-specific LifecycleOwner.
 * Changed to function signature to match compiler expectations.
 */
expect fun currentLifecycleOwner(): LifecycleOwner? 