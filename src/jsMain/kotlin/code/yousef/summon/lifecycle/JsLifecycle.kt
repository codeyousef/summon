package code.yousef.summon.lifecycle

// Remove unused imports
// import code.yousef.summon.runtime.PlatformRendererProvider
// import code.yousef.summon.runtime.PlatformRenderer

import code.yousef.summon.lifecycle.LifecycleState // Import expect from correct package
import code.yousef.summon.lifecycle.LifecycleOwner // Import expect from correct package
import code.yousef.summon.lifecycle.LifecycleObserver // Import expect from correct package
import kotlinx.browser.window
import kotlinx.browser.document // Explicitly import document
import org.w3c.dom.Document
import org.w3c.dom.events.Event

/**
 * Extension property to access document.visibilityState which is not defined in the Kotlin/JS DOM API
 */
private val Document.visibilityState: String
    get() = asDynamic().visibilityState as String

/**
 * JS-specific implementation of LifecycleOwner.
 * This implementation is browser agnostic and works with any JS environment.
 */
// Class itself is not actual, it implements the actual interface
class JsLifecycleOwner : LifecycleOwner {
    private val observers = mutableListOf<LifecycleObserver>()
    // This property implements the one from the actual interface, so use override, not actual
    override var currentState: LifecycleState = LifecycleState.INITIALIZED
        private set

    init {
        // Initial state transition
        setState(LifecycleState.CREATED)
        setState(LifecycleState.STARTED)
        setState(LifecycleState.RESUMED)

        // Listen to browser visibility changes for basic lifecycle
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
        
        // Removed potential TODO for state transition validation
        currentState = newState
        notifyObservers(newState)
    }

    private fun notifyObservers(state: LifecycleState) {
        // Copy observers list to avoid concurrent modification issues
        val currentObservers = observers.toList()
        currentObservers.forEach { observer ->
            when (state) {
                LifecycleState.CREATED -> observer.onCreate()
                LifecycleState.STARTED -> observer.onStart()
                LifecycleState.RESUMED -> observer.onResume()
                LifecycleState.PAUSED -> observer.onPause()
                LifecycleState.STOPPED -> observer.onStop()
                LifecycleState.DESTROYED -> observer.onDestroy()
                else -> { /* No specific event for INITIALIZED */ }
            }
        }
    }

    // These methods implement the ones from the actual interface, so use override, not actual
    override fun addObserver(observer: LifecycleObserver) {
        observers.add(observer)
        // Notify observer of current state immediately?
        // Depends on desired behavior, omitted for simplicity
    }

    // Use override, not actual
    override fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }
}

// Provide a single instance for the JS environment
private val jsLifecycleOwnerInstance = JsLifecycleOwner()

/**
 * Gets the current JS-specific lifecycle owner. Actual fun matches expect fun.
 */
actual fun currentLifecycleOwner(): LifecycleOwner? = jsLifecycleOwnerInstance

/**
 * Actual implementation of LifecycleState for JS. Actual enum matches expect enum.
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
 * Actual implementation of LifecycleObserver for JS. Actual interface matches expect interface.
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
 * Actual implementation of LifecycleOwner for JS. Actual interface matches expect interface.
 */
actual interface LifecycleOwner {
    actual val currentState: LifecycleState
    actual fun addObserver(observer: LifecycleObserver)
    actual fun removeObserver(observer: LifecycleObserver)
} 
