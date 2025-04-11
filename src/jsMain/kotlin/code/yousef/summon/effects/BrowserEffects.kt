package code.yousef.summon.effects.js

import code.yousef.summon.effects.ClipboardAPI
import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.ElementRef
import code.yousef.summon.effects.onMountWithCleanup
import code.yousef.summon.runtime.Composable
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import kotlinx.browser.window
import org.w3c.dom.events.Event

/**
 * Browser history interface
 */
class History {
    fun back() {
        // Implementation would call window.history.back()
    }
    
    fun forward() {
        // Implementation would call window.history.forward()
    }
    
    fun push(path: String) {
        // Implementation would update history and location
    }
    
    fun replace(path: String) {
        // Implementation would replace current history entry
    }
}

/**
 * Browser navigator information
 */
class Navigator {
    val language: String = "en-US" // Default value
    val userAgent: String = "Summon" // Default value
    val onLine: Boolean = true // Default value
    
    // Other navigator properties would be added here
}

/**
 * IntersectionObserver options
 */
class IntersectionObserverOptions(
    val root: ElementRef? = null,
    val rootMargin: String = "0px",
    val threshold: Float = 0f
)

/**
 * IntersectionObserver state
 */
class IntersectionState(
    val isIntersecting: Boolean,
    val intersectionRatio: Float,
    val boundingClientRect: DOMRect
)

/**
 * Simple DOM rectangle representation
 */
class DOMRect(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val left: Double
)

/**
 * Effect for browser history
 *
 * @return History object for navigating the browser history
 */
@Composable
fun CompositionScope.useHistory(): History {
    // This would be implemented with actual browser history API integration
    return History()
}

/**
 * Effect for browser navigator
 *
 * @return Navigator object with browser information
 */
@Composable
fun CompositionScope.useNavigator(): Navigator {
    // This would be implemented with actual browser navigator API integration
    return Navigator()
}

/**
 * Effect for IntersectionObserver
 *
 * @param elementRef Reference to the element to observe
 * @param options Options for the IntersectionObserver
 * @return IntersectionState that updates when the intersection changes
 */
@Composable
fun CompositionScope.useIntersectionObserver(
    elementRef: ElementRef,
    options: IntersectionObserverOptions = IntersectionObserverOptions()
): SummonMutableState<IntersectionState> {
    val initialState = IntersectionState(
        isIntersecting = false,
        intersectionRatio = 0f,
        boundingClientRect = DOMRect(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    )
    
    val state = mutableStateOf(initialState)
    
    onMountWithCleanup {
        // This would create an actual IntersectionObserver in JS
        // and update the state when intersection changes
        
        // Return cleanup function
        {
            // This would disconnect the IntersectionObserver
        }
    }
    
    return state
}

/**
 * ResizeObserver entry representation
 */
class ResizeObserverEntry(
    val contentRect: DOMRect,
    val target: Any
)

/**
 * ResizeObserver cleanup function type
 */
typealias ResizeObserverCleanup = () -> Unit

/**
 * Effect for ResizeObserver
 *
 * @param elementRef Reference to the element to observe
 * @param callback Function to call when the element is resized
 * @return Cleanup function to disconnect the observer
 */
@Composable
fun CompositionScope.useResizeObserver(
    elementRef: ElementRef,
    callback: (ResizeObserverEntry) -> Unit
): ResizeObserverCleanup {
    var cleanupFn: (() -> Unit)? = null
    
    onMountWithCleanup {
        // This would create an actual ResizeObserver in JS
        
        // Return cleanup function
        cleanupFn = {
            // This would disconnect the ResizeObserver
        }
        
        cleanupFn
    }
    
    return cleanupFn ?: { /* No-op if null */ }
}

/**
 * Effect for online/offline status
 *
 * @return Boolean state that updates when the online status changes
 */
@Composable
fun CompositionScope.useOnlineStatus(): SummonMutableState<Boolean> {
    // Initialize with the current online status from the browser
    val online = mutableStateOf(js("navigator.onLine") as Boolean)
    
    onMountWithCleanup {
        // Create event handlers
        val handleOnline = { _: Event -> 
            online.value = true 
        }
        
        val handleOffline = { _: Event -> 
            online.value = false 
        }
        
        // Add event listeners
        window.addEventListener("online", handleOnline)
        window.addEventListener("offline", handleOffline)
        
        // Return cleanup function
        return@onMountWithCleanup {
            // Remove event listeners
            window.removeEventListener("online", handleOnline)
            window.removeEventListener("offline", handleOffline)
        }
    }
    
    return online
}

/**
 * Geolocation options
 */
class GeolocationOptions(
    val enableHighAccuracy: Boolean = false,
    val timeout: Int = 0,
    val maximumAge: Int = 0
)

/**
 * Geolocation state
 */
data class GeolocationState(
    val position: Position?,
    val error: String?,
    val loading: Boolean
)

/**
 * Geolocation position
 */
data class Position(
    val coords: Coordinates,
    val timestamp: Long
)

/**
 * Geolocation coordinates
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double?,
    val accuracy: Double,
    val altitudeAccuracy: Double?,
    val heading: Double?,
    val speed: Double?
)

/**
 * Effect for geolocation
 *
 * @param options Options for the geolocation request
 * @return GeolocationState that updates when the position changes
 */
@Composable
fun CompositionScope.useGeolocation(
    options: GeolocationOptions = GeolocationOptions()
): SummonMutableState<GeolocationState> {
    val state = mutableStateOf(
        GeolocationState(
            position = null,
            error = null,
            loading = true
        )
    )
    
    onMountWithCleanup {
        // Set up success callback
        val successCallback = { jsPosition: dynamic ->
            // Extract coordinates from JS position object
            val coords = Coordinates(
                latitude = jsPosition.coords.latitude as Double,
                longitude = jsPosition.coords.longitude as Double,
                altitude = jsPosition.coords.altitude as? Double,
                accuracy = jsPosition.coords.accuracy as Double,
                altitudeAccuracy = jsPosition.coords.altitudeAccuracy as? Double,
                heading = jsPosition.coords.heading as? Double,
                speed = jsPosition.coords.speed as? Double
            )
            
            // Create Position object
            val position = Position(
                coords = coords,
                timestamp = (jsPosition.timestamp as Number).toLong()
            )
            
            // Update state
            state.value = GeolocationState(
                position = position,
                error = null,
                loading = false
            )
        }
        
        // Set up error callback
        val errorCallback = { jsError: dynamic ->
            state.value = GeolocationState(
                position = null,
                error = jsError.message as String,
                loading = false
            )
        }
        
        // Geolocation watch ID for cleanup
        var watchId: dynamic = null
        
        // Check if geolocation is available
        if (js("'geolocation' in navigator") as Boolean) {
            // Create geolocation options
            val jsOptions = js("({})")
            js("jsOptions.enableHighAccuracy = true")
            js("jsOptions.timeout = 5000")
            js("jsOptions.maximumAge = 0")
            
            // Start watching position
            js("""
                watchId = navigator.geolocation.watchPosition(
                    successCallback,
                    errorCallback,
                    jsOptions
                );
                
                // Initial position request
                navigator.geolocation.getCurrentPosition(
                    successCallback,
                    errorCallback,
                    jsOptions
                );
            """)
        } else {
            // Geolocation not available
            state.value = GeolocationState(
                position = null,
                error = "Geolocation not supported in this browser",
                loading = false
            )
        }
        
        // Return cleanup function
        return@onMountWithCleanup {
            // Clear the geolocation watch if it was set
            if (js("watchId !== null") as Boolean) {
                js("navigator.geolocation.clearWatch(watchId)")
            }
        }
    }
    
    return state
}

/**
 * Web animation keyframe
 */
class Keyframe(
    val offset: Double? = null,
    val easing: String? = null,
    val properties: Map<String, Any>
)

/**
 * Web animation options
 */
class AnimationOptions(
    val duration: Int,
    val iterations: Int = 1,
    val delay: Int = 0,
    val easing: String = "linear",
    val direction: String = "normal",
    val fill: String = "none"
)

/**
 * Web animation API interface
 */
class WebAnimationAPI {
    fun play() {
        // This would play the animation
    }
    
    fun pause() {
        // This would pause the animation
    }
    
    fun cancel() {
        // This would cancel the animation
    }
    
    fun finish() {
        // This would finish the animation
    }
}

/**
 * Effect for web animation API
 *
 * @param elementRef Reference to the element to animate
 * @param keyframes Array of keyframes for the animation
 * @param options Options for the animation
 * @return WebAnimationAPI object for controlling the animation
 */
@Composable
fun CompositionScope.useWebAnimation(
    elementRef: ElementRef,
    keyframes: Array<Keyframe>,
    options: AnimationOptions
): WebAnimationAPI {
    val api = WebAnimationAPI()
    
    onMountWithCleanup {
        // This would create and start the animation
        
        // Return cleanup function
        {
            // This would cancel the animation
        }
    }
    
    return api
} 