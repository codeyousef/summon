package codes.yousef.summon.lifecycle

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