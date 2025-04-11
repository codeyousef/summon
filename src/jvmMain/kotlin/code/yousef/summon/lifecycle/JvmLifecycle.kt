package code.yousef.summon.lifecycle

import code.yousef.summon.lifecycle.LifecycleState
import code.yousef.summon.lifecycle.LifecycleOwner
import code.yousef.summon.lifecycle.LifecycleObserver

/**
 * JVM implementation of LifecycleOwner.
 * Simplified to only implement the expect interface.
 */
class JvmLifecycleOwner : LifecycleOwner { 
    override val currentState: LifecycleState = LifecycleState.RESUMED
    override fun addObserver(observer: LifecycleObserver) { /* No-op */ }
    override fun removeObserver(observer: LifecycleObserver) { /* No-op */ }
}

private val jvmLifecycleOwnerInstance = JvmLifecycleOwner()

/**
 * Provides the JVM actual implementation for the expect fun.
 */
actual fun currentLifecycleOwner(): LifecycleOwner? = jvmLifecycleOwnerInstance

// Add back explicit actual declarations for enum and interfaces

/**
 * Actual implementation for JVM (matches expect enum).
 */
actual enum class LifecycleState {
    INITIALIZED,
    CREATED,
    STARTED,
    RESUMED,
    PAUSED,
    STOPPED,
    DESTROYED
}

/**
 * Actual implementation for JVM (matches expect interface).
 */
actual interface LifecycleObserver {
    actual fun onCreate()
    actual fun onStart()
    actual fun onResume()
    actual fun onPause()
    actual fun onStop()
    actual fun onDestroy()
}

/**
 * Actual implementation for JVM (matches expect interface).
 */
actual interface LifecycleOwner {
    actual val currentState: LifecycleState
    actual fun addObserver(observer: LifecycleObserver)
    actual fun removeObserver(observer: LifecycleObserver)
} 