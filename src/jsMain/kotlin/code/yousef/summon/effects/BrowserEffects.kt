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
        js("window.history.back()")
    }
    
    fun forward() {
        js("window.history.forward()")
    }
    
    fun push(path: String) {
        js("window.history.pushState({}, '', path)")
    }
    
    fun replace(path: String) {
        js("window.history.replaceState({}, '', path)")
    }
    
    fun getLength(): Int {
        return js("window.history.length") as Int
    }
    
    fun getState(): dynamic {
        return js("window.history.state")
    }
}

/**
 * Browser navigator information
 */
class Navigator {
    val language: String
    val userAgent: String
    val onLine: Boolean
    val platform: String
    val cookieEnabled: Boolean
    val hardwareConcurrency: Int
    val maxTouchPoints: Int
    val pdfViewerEnabled: Boolean
    
    init {
        // Initialize with actual navigator properties
        language = js("navigator.language || 'en-US'") as String
        userAgent = js("navigator.userAgent || ''") as String
        onLine = js("navigator.onLine || true") as Boolean
        platform = js("navigator.platform || ''") as String
        cookieEnabled = js("navigator.cookieEnabled || false") as Boolean
        hardwareConcurrency = js("navigator.hardwareConcurrency || 1") as Int
        maxTouchPoints = js("navigator.maxTouchPoints || 0") as Int
        pdfViewerEnabled = js("navigator.pdfViewerEnabled || false") as Boolean
    }
    
    /**
     * Check if the given MIME type is supported
     */
    fun mimeTypeSupported(mimeType: String): Boolean {
        return js("navigator.mimeTypes && navigator.mimeTypes.length > 0 && navigator.mimeTypes[mimeType] !== undefined") as Boolean
    }
    
    /**
     * Check if the browser has vibration support
     */
    fun hasVibrationSupport(): Boolean {
        return js("'vibrate' in navigator") as Boolean
    }
    
    /**
     * Check if the browser has geolocation support
     */
    fun hasGeolocationSupport(): Boolean {
        return js("'geolocation' in navigator") as Boolean
    }
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
data class IntersectionState(
    val isIntersecting: Boolean,
    val intersectionRatio: Float,
    val boundingClientRect: DOMRect
)

/**
 * Simple DOM rectangle representation
 */
data class DOMRect(
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
    val history = History()
    
    onMountWithCleanup {
        // Set up a popstate event listener to handle browser navigation
        val handlePopState = { event: Event ->
            // You can add additional handling for popstate events if needed
            // For example, notify a state manager that navigation occurred
        }
        
        window.addEventListener("popstate", handlePopState)
        
        return@onMountWithCleanup {
            window.removeEventListener("popstate", handlePopState)
        }
    }
    
    return history
}

/**
 * Effect for browser navigator
 *
 * @return Navigator object with browser information
 */
@Composable
fun CompositionScope.useNavigator(): Navigator {
    // Create and return Navigator with up-to-date browser information
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
        // Define the intersection callback
        val intersectionCallback = { entries: dynamic, _: dynamic ->
            // Get the first entry (we're only observing one element)
            val entry = entries[0]
            
            // Extract DOMRect from the entry
            val rect = entry.boundingClientRect
            val domRect = DOMRect(
                x = rect.x as Double,
                y = rect.y as Double,
                width = rect.width as Double,
                height = rect.height as Double,
                top = rect.top as Double,
                right = rect.right as Double,
                bottom = rect.bottom as Double,
                left = rect.left as Double
            )
            
            // Update the state with intersection information
            state.value = IntersectionState(
                isIntersecting = entry.isIntersecting as Boolean,
                intersectionRatio = entry.intersectionRatio.toFloat(),
                boundingClientRect = domRect
            )
        }
        
        // Create IntersectionObserver options
        val jsOptions = js("({})")
        
        if (options.root != null) {
            js("jsOptions.root = options.root")
        }
        
        // Set rootMargin
        val rootMargin = options.rootMargin
        js("jsOptions.rootMargin = rootMargin")
        
        // Set threshold
        val threshold = options.threshold
        js("jsOptions.threshold = threshold")
        
        // Create and initialize the IntersectionObserver
        val observer = js("new IntersectionObserver(intersectionCallback, jsOptions)")
        // TODO: provide a real implementation
        // Get the DOM element from elementRef and start observing
        val elementId = "element-id" // In a real implementation, elementRef would have an id property
        js("var domElement = document.getElementById(elementId)")
        js("if (domElement !== null) { observer.observe(domElement) }")
        
        // Return cleanup function
        return@onMountWithCleanup {
            // Disconnect the IntersectionObserver
            js("observer.disconnect()")
        }
    }
    
    return state
}

/**
 * ResizeObserver entry representation
 */
data class ResizeObserverEntry(
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
    // Function to disconnect the observer
    var disconnectObserver: (() -> Unit)? = null
    
    onMountWithCleanup {
        // Define the resize callback
        val resizeCallback = { entries: dynamic ->
            // Get the first entry (we're only observing one element)
            val entry = entries[0]
            
            // Extract the content rectangle
            val rect = entry.contentRect
            val domRect = DOMRect(
                x = rect.x as Double,
                y = rect.y as Double,
                width = rect.width as Double,
                height = rect.height as Double,
                top = rect.top as Double,
                right = rect.right as Double,
                bottom = rect.bottom as Double,
                left = rect.left as Double
            )
            
            // Create ResizeObserverEntry and invoke the callback
            val resizeEntry = ResizeObserverEntry(
                contentRect = domRect,
                target = entry.target
            )
            
            callback(resizeEntry)
        }
        
        // Create and initialize the ResizeObserver
        val observer = js("new ResizeObserver(resizeCallback)")
        // TODO: provide a real implementation
        // Get the DOM element from elementRef and start observing
        val elementId = "element-id" // In a real implementation, elementRef would have an id property
        js("var domElement = document.getElementById(elementId)")
        js("if (domElement !== null) { observer.observe(domElement) }")
        
        // Set up disconnect function
        disconnectObserver = {
            js("observer.disconnect()")
        }
        
        // Return cleanup function
        return@onMountWithCleanup {
            disconnectObserver?.invoke()
        }
    }
    
    // Return a function that will disconnect the observer when called
    return {
        disconnectObserver?.invoke()
    }
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
data class Keyframe(
    val offset: Double? = null,
    val easing: String? = null,
    val properties: Map<String, Any>
)

/**
 * Web animation options
 */
data class AnimationOptions(
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
    private var animation: dynamic = null
    
    fun play() {
        js("if (this.animation) this.animation.play()")
    }
    
    fun pause() {
        js("if (this.animation) this.animation.pause()")
    }
    
    fun cancel() {
        js("if (this.animation) this.animation.cancel()")
    }
    
    fun finish() {
        js("if (this.animation) this.animation.finish()")
    }
    
    fun reverse() {
        js("if (this.animation) this.animation.reverse()")
    }
    
    fun setAnimation(anim: dynamic) {
        animation = anim
    }
    
    fun getCurrentTime(): Double {
        return js("this.animation ? this.animation.currentTime : 0") as Double
    }
    
    fun setCurrentTime(time: Double) {
        js("if (this.animation) this.animation.currentTime = time")
    }
    
    fun getPlaybackRate(): Double {
        return js("this.animation ? this.animation.playbackRate : 1.0") as Double
    }
    
    fun setPlaybackRate(rate: Double) {
        js("if (this.animation) this.animation.playbackRate = rate")
    }
    
    fun isPaused(): Boolean {
        return js("this.animation && this.animation.playState === 'paused'") as Boolean
    }
    
    fun isRunning(): Boolean {
        return js("this.animation && this.animation.playState === 'running'") as Boolean
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
        // Convert Keyframes to JS objects
        js("var keyframesArray = []")
        
        for (keyframe in keyframes) {
            js("var frame = {}")
            
            // Set offset if provided
            if (keyframe.offset != null) {
                val offset = keyframe.offset
                js("frame.offset = offset")
            }
            
            // Set easing if provided
            if (keyframe.easing != null) {
                val easing = keyframe.easing
                js("frame.easing = easing")
            }
            
            // Add all properties from the properties map
            for ((key, value) in keyframe.properties) {
                js("frame[key] = value")
            }
            
            js("keyframesArray.push(frame)")
        }
        
        // Create animation options
        js("var animOptions = {}")
        
        val duration = options.duration
        js("animOptions.duration = duration")
        
        val iterations = options.iterations
        js("animOptions.iterations = iterations")
        
        val delay = options.delay
        js("animOptions.delay = delay")
        
        val easing = options.easing
        js("animOptions.easing = easing")
        
        val direction = options.direction
        js("animOptions.direction = direction")
        
        val fill = options.fill
        js("animOptions.fill = fill")
        // TODO: provide a real implementation
        // Get the DOM element from elementRef
        val elementId = "element-id" // In a real implementation, elementRef would have an id property
        
        // Create and start the animation
        js("""
            var element = document.getElementById(elementId);
            if (element) {
                var animation = element.animate(keyframesArray, animOptions);
                api.setAnimation(animation);
            }
        """)
        
        // Return cleanup function
        return@onMountWithCleanup {
            // Cancel the animation on cleanup
            api.cancel()
        }
    }
    
    return api
} 