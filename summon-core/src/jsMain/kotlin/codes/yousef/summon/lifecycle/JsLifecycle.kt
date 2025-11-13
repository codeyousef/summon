package codes.yousef.summon.lifecycle


import codes.yousef.summon.lifecycle.LifecycleOwner
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.events.Event
import codes.yousef.summon.lifecycle.LifecycleOwner as ExpectedLifecycleOwner

/**
 * Extension property to access document.visibilityState which is not defined in the Kotlin/JS DOM API
 */
private val Document.visibilityState: String
    get() = asDynamic().visibilityState as String

/**
 * JS-specific implementation of LifecycleOwner.
 * This implementation is browser-agnostic and works with any JS environment.
 */
actual class LifecycleOwner {
    private val observers = mutableListOf<LifecycleObserver>() // Ensure this is uncommented
    actual var currentState: LifecycleState = LifecycleState.INITIALIZED // Ensure this is uncommented
    // Consider if `private set` is appropriate given direct assignments in setState/notifyObservers.
    // If set directly only within the class, `private set` is fine.
    // private set

    init {
        // Initial state transition
        setState(LifecycleState.CREATED)
        setState(LifecycleState.STARTED)
        setState(LifecycleState.RESUMED)

        // Listen to browser visibility changes for the basic lifecycle
        window.addEventListener("visibilitychange", ::handleVisibilityChange)
        window.addEventListener("pagehide", ::handlePageHide) // For stop/destroy
        window.addEventListener("beforeunload", ::handlePageHide) // Alternative for unload
    }

    private fun handleVisibilityChange(event: Event) {
        // Use the extension property
        if (document.visibilityState == "hidden") {
            setState(LifecycleState.PAUSED)
            setState(LifecycleState.STOPPED)
        } else {
            setState(LifecycleState.STARTED)
            setState(LifecycleState.RESUMED)
        }
    }

    private fun handlePageHide(event: Event) {
        setState(LifecycleState.STOPPED)
        setState(LifecycleState.DESTROYED)
        // Clean up listeners
        window.removeEventListener("visibilitychange", ::handleVisibilityChange)
        window.removeEventListener("pagehide", ::handlePageHide)
        window.removeEventListener("beforeunload", ::handlePageHide)
    }

    private fun setState(newState: LifecycleState) {
        if (newState == currentState) return

        // It's crucial to store the old state if your observer notification logic
        // needs to know the previous state to call the correct observer methods (e.g., onStart vs onResume).
        // val oldState = currentState
        currentState = newState
        notifyObservers(newState /*, oldState */) // Pass oldState if needed for complex notifications
    }

    private fun notifyObservers(newState: LifecycleState) {
        // Update current state first - This is already done in setState before calling notifyObservers.
        // currentState = newState // This line is redundant if setState already updated it.

        // Create a copy of the observers list to avoid ConcurrentModificationException
        // if an observer tries to remove itself during notification.
        val observersCopy = ArrayList(observers) // Defensive copy
        observersCopy.forEach { observer ->
            // CRITICAL: Replace this with logic that calls specific observer methods
            // e.g., observer.onCreate(), observer.onStart() based on state transitions.
            // observer.onStateChanged(newState) // THIS WILL NOT COMPILE

            // Example (simplified, needs proper transition logic):
            // This is where you'd map newState (and potentially oldState) to specific calls:
            // when (newState) {
            //    LifecycleState.CREATED -> observer.onCreate() // Potentially check if not already created
            //    LifecycleState.STARTED -> observer.onStart()
            //    LifecycleState.RESUMED -> observer.onResume()
            //    LifecycleState.PAUSED -> observer.onPause()
            //    LifecycleState.STOPPED -> observer.onStop()
            //    LifecycleState.DESTROYED -> observer.onDestroy()
            //    else -> {}
            // }
        }
    }

    actual fun addObserver(observer: LifecycleObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
            // If the lifecycle is already created or started, immediately notify the new observer.
            // This also needs to be updated to call specific methods, not onStateChanged.
            if (currentState >= LifecycleState.CREATED) { // Covers CREATED, STARTED, RESUMED
                // CRITICAL: Replace this with logic that calls appropriate observer methods
                // for the current state.
                // observer.onStateChanged(currentState) // THIS WILL NOT COMPILE

                // Example (simplified):
                // when (currentState) {
                //    LifecycleState.RESUMED -> { observer.onCreate(); observer.onStart(); observer.onResume(); }
                //    LifecycleState.STARTED -> { observer.onCreate(); observer.onStart(); }
                //    LifecycleState.CREATED -> observer.onCreate()
                //    else -> {}
                // }
            }
        }
    }

    actual fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }
}

// Provide a single instance for the JS environment
private val jsLifecycleOwnerInstance = LifecycleOwner()

/**
 * Gets the current JS-specific lifecycle owner. Actual fun matches expect fun.
 */
actual fun currentLifecycleOwner(): ExpectedLifecycleOwner? = jsLifecycleOwnerInstance