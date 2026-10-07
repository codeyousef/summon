package codes.yousef.summon.lifecycle

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