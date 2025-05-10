package code.yousef.summon.lifecycle

// Expect declarations are in this same package (commonMain/kotlin/code/yousef/summon/lifecycle)
// So, direct usage of LifecycleState, LifecycleObserver, LifecycleOwner will refer to them.
import java.util.concurrent.CopyOnWriteArrayList

/**
 * JVM actual implementation of LifecycleOwner.
 * This class provides a basic lifecycle management for JVM environments,
 * simulating state transitions and notifying observers.
 */
actual class LifecycleOwner  {
    private val observers = CopyOnWriteArrayList<LifecycleObserver>() // Thread-safe list
    actual var currentState: LifecycleState = LifecycleState.INITIALIZED

    init {
        // Simulate lifecycle progression on JVM (basic)
        setState(LifecycleState.CREATED)
        setState(LifecycleState.STARTED)
        setState(LifecycleState.RESUMED)
        // JVM apps don't typically pause/resume like mobile/web,
        // but we need the states. Add shutdown hook for destroy.
        // Note: This shutdown hook might not cover all JVM exit scenarios.
        Runtime.getRuntime().addShutdownHook(Thread {
            setState(LifecycleState.PAUSED) // Simulate PAUSED before STOPPED
            setState(LifecycleState.STOPPED)
            setState(LifecycleState.DESTROYED)
        })
    }

    private fun setState(newState: LifecycleState) {
        // Ensure states are progressed in order and not reverted (simplistic check)
        if (newState < currentState && newState != LifecycleState.INITIALIZED) {
            // This simple check might need refinement for more complex lifecycle scenarios
            // For now, allowing progression or reset to INITIALIZED
            // println("Warning: Attempting to move lifecycle state backwards from $currentState to $newState")
            // return // Or handle as an error, or allow specific reversions
        }
        if (newState == currentState) return

        currentState = newState
        notifyObservers(newState)
    }

    private fun notifyObservers(newState: LifecycleState) {
        // Update current state first
        currentState = newState
        observers.forEach { it.onStateChanged(newState) }
    }

    actual override fun addObserver(observer: LifecycleObserver) {
        if (observers.addIfAbsent(observer)) {
            // If the lifecycle is already created or started, immediately notify the new observer.
            if (currentState >= LifecycleState.CREATED) { // Covers CREATED, STARTED, RESUMED
                observer.onStateChanged(currentState)
            }
        }
    }

    actual override fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }
}

private val lifecycleOwnerInstance = LifecycleOwner()

/**
 * Provides the JVM actual implementation for the expect fun currentLifecycleOwner.
 */
actual fun currentLifecycleOwner(): LifecycleOwner? = lifecycleOwnerInstance

// Redundant 'actual enum class LifecycleState', 'actual interface LifecycleObserver',
// and 'actual interface LifecycleOwner' that were previously in this file have been removed.
// The 'expect' declarations in commonMain/lifecycle/Lifecycle.kt are the single source of truth for these types.