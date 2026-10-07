package codes.yousef.summon.effects

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf
import kotlinx.browser.window
import org.w3c.dom.events.Event

/**
 * External interface for browser history
 */
external interface BrowserHistory {
    /** Executes the back operation. */
    fun back()
    /** Executes the forward operation. */
    fun forward()
    /**
     * Executes the push state operation.
     *
     * @param data The data value.
     * @param title The title value.
     * @param url Target URL.
     */
    fun pushState(data: dynamic, title: String, url: String? = definedExternally)
    /**
     * Executes the replace state operation.
     *
     * @param data The data value.
     * @param title The title value.
     * @param url Target URL.
     */
    fun replaceState(data: dynamic, title: String, url: String? = definedExternally)
    /** The property declaration value. */
    val length: Int
    /** The property declaration value. */
    val state: dynamic
}

/**
 * Browser history wrapper
 */
class History {
    @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val browserHistory = window.asDynamic().history as BrowserHistory

    /** Executes the back operation. */
    fun back() {
        browserHistory.back()
    }

    /** Executes the forward operation. */
    fun forward() {
        browserHistory.forward()
    }

    /**
     * Executes the push operation.
     *
     * @param path Target path.
     */
    fun push(path: String) {
        browserHistory.pushState(js("{}"), "", path)
    }

    /**
     * Executes the replace operation.
     *
     * @param path Target path.
     */
    fun replace(path: String) {
        browserHistory.replaceState(js("{}"), "", path)
    }

    /**
     * Returns length.
     *
     * @return The resulting value.
     */
    fun getLength(): Int {
        return browserHistory.length
    }

    /** Returns state. */
    fun getState(): dynamic {
        return browserHistory.state
    }
}

/**
 * External interface for browser navigator
 */
external interface BrowserNavigator {
    /** The property declaration value. */
    val language: String
    /** The property declaration value. */
    val userAgent: String
    /** The property declaration value. */
    val onLine: Boolean
    /** The property declaration value. */
    val platform: String
    /** The property declaration value. */
    val cookieEnabled: Boolean
    /** The property declaration value. */
    val hardwareConcurrency: Int
    /** The property declaration value. */
    val maxTouchPoints: Int
    /** The property declaration value. */
    val pdfViewerEnabled: Boolean
    /** The property declaration value. */
    val mimeTypes: dynamic
    /** The property declaration value. */
    val geolocation: dynamic
    /** The property declaration value. */
    val vibrate: dynamic
}

/**
 * Browser navigator information
 */
class Navigator {
    @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val browserNavigator = window.asDynamic().navigator as BrowserNavigator

    /** The property declaration value. */
    val language: String = browserNavigator.language
    /** The property declaration value. */
    val userAgent: String = browserNavigator.userAgent
    /** The property declaration value. */
    val onLine: Boolean = browserNavigator.onLine
    /** The property declaration value. */
    val platform: String = browserNavigator.platform
    /** The property declaration value. */
    val cookieEnabled: Boolean = browserNavigator.cookieEnabled
    /** The property declaration value. */
    val hardwareConcurrency: Int = browserNavigator.hardwareConcurrency
    /** The property declaration value. */
    val maxTouchPoints: Int = browserNavigator.maxTouchPoints
    /** The property declaration value. */
    val pdfViewerEnabled: Boolean = browserNavigator.pdfViewerEnabled

    /**
     * Check if the given MIME type is supported
     */
    fun mimeTypeSupported(mimeType: String): Boolean {
        val mimeTypes = browserNavigator.mimeTypes
        return mimeTypes != null &&
                js("mimeTypes.length > 0 && mimeTypes[mimeType] !== undefined") as Boolean
    }

    /**
     * Check if the browser has vibration support
     */
    fun hasVibrationSupport(): Boolean {
        return browserNavigator.vibrate != null
    }

    /**
     * Check if the browser has geolocation support
     */
    fun hasGeolocationSupport(): Boolean {
        return browserNavigator.geolocation != null
    }
}

/**
 * External interface for IntersectionObserver
 */
external class IntersectionObserver(
    callback: (Array<IntersectionObserverEntry>, IntersectionObserver) -> Unit,
    options: dynamic = definedExternally
) {
    /**
     * Executes the observe operation.
     *
     * @param target The target value.
     */
    fun observe(target: dynamic)
    /**
     * Executes the unobserve operation.
     *
     * @param target The target value.
     */
    fun unobserve(target: dynamic)
    /** Executes the disconnect operation. */
    fun disconnect()
}

/**
 * External interface for IntersectionObserverEntry
 */
external interface IntersectionObserverEntry {
    /** The property declaration value. */
    val boundingClientRect: DOMRectReadOnly
    /** The property declaration value. */
    val intersectionRatio: Double
    /** The property declaration value. */
    val isIntersecting: Boolean
    /** The property declaration value. */
    val target: dynamic
}

/**
 * External interface for DOMRectReadOnly
 */
external interface DOMRectReadOnly {
    /** The property declaration value. */
    val x: Double
    /** The property declaration value. */
    val y: Double
    /** The property declaration value. */
    val width: Double
    /** The property declaration value. */
    val height: Double
    /** The property declaration value. */
    val top: Double
    /** The property declaration value. */
    val right: Double
    /** The property declaration value. */
    val bottom: Double
    /** The property declaration value. */
    val left: Double
}

/**
 * IntersectionObserver options

 * @property root The root value.
 * @property rootMargin The root margin value.
 * @property threshold The threshold value.
 */
class IntersectionObserverOptions(
    val root: ElementRef? = null,
    val rootMargin: String = "0px",
    val threshold: Float = 0f
)

/**
 * IntersectionObserver state

 * @property isIntersecting The is intersecting value.
 * @property intersectionRatio The intersection ratio value.
 * @property boundingClientRect The bounding client rect value.
 */
data class IntersectionState(
    val isIntersecting: Boolean,
    val intersectionRatio: Float,
    val boundingClientRect: DOMRect
)

/**
 * Simple DOM rectangle representation

 * @property x The x value.
 * @property y The y value.
 * @property width The width value.
 * @property height The height value.
 * @property top The top value.
 * @property right The right value.
 * @property bottom The bottom value.
 * @property left The left value.
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
        val intersectionCallback = { entries: Array<IntersectionObserverEntry>, _: IntersectionObserver ->
            // Get the first entry (we're only observing one element)
            val entry = entries[0]

            // Extract DOMRect from the entry
            val rect = entry.boundingClientRect
            val domRect = DOMRect(
                x = rect.x,
                y = rect.y,
                width = rect.width,
                height = rect.height,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                left = rect.left
            )

            // Update the state with intersection information
            state.value = IntersectionState(
                isIntersecting = entry.isIntersecting,
                intersectionRatio = entry.intersectionRatio.toFloat(),
                boundingClientRect = domRect
            )
        }

        // Create IntersectionObserver options
        val jsOptions = js("({})")

        if (options.root != null) {
            jsOptions.root = options.root
        }

        // Set rootMargin
        jsOptions.rootMargin = options.rootMargin

        // Set threshold
        jsOptions.threshold = options.threshold

        // Create and initialize the IntersectionObserver
        val observer = IntersectionObserver(intersectionCallback, jsOptions)

        // Get the DOM element from elementRef and start observing
        val domElement = elementRef.getElement()
        if (domElement != null) {
            observer.observe(domElement)
        }

        // Return cleanup function
        return@onMountWithCleanup {
            // Disconnect the IntersectionObserver
            observer.disconnect()
        }
    }

    return state
}

/**
 * External interface for ResizeObserver
 */
external class ResizeObserver(callback: (Array<ResizeObserverEntryJS>) -> Unit) {
    /**
     * Executes the observe operation.
     *
     * @param target The target value.
     */
    fun observe(target: dynamic)
    /**
     * Executes the unobserve operation.
     *
     * @param target The target value.
     */
    fun unobserve(target: dynamic)
    /** Executes the disconnect operation. */
    fun disconnect()
}

/**
 * External interface for ResizeObserverEntry
 */
external interface ResizeObserverEntryJS {
    /** The property declaration value. */
    val contentRect: DOMRectReadOnly
    /** The property declaration value. */
    val target: dynamic
}

/**
 * ResizeObserver entry representation

 * @property contentRect The content rect value.
 * @property target The target value.
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
        val resizeCallback = { entries: Array<ResizeObserverEntryJS> ->
            // Get the first entry (we're only observing one element)
            val entry = entries[0]

            // Extract the content rectangle
            val rect = entry.contentRect
            val domRect = DOMRect(
                x = rect.x,
                y = rect.y,
                width = rect.width,
                height = rect.height,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                left = rect.left
            )

            // Create ResizeObserverEntry and invoke the callback
            val resizeEntry = ResizeObserverEntry(
                contentRect = domRect,
                target = entry.target
            )

            callback(resizeEntry)
        }

        // Create and initialize the ResizeObserver
        val observer = ResizeObserver(resizeCallback)

        // Get the DOM element from elementRef and start observing
        val domElement = elementRef.getElement()
        if (domElement != null) {
            observer.observe(domElement)
        }

        // Set up disconnect function
        disconnectObserver = {
            observer.disconnect()
        }

        // Return cleanup function
        return@onMountWithCleanup {
            disconnectObserver.invoke()
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
    @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    val browserNavigator = window.asDynamic().navigator as BrowserNavigator
    val online = mutableStateOf(browserNavigator.onLine)

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
 * External interface for Geolocation
 */
external interface Geolocation {
    /**
     * Returns current position.
     *
     * @param successCallback The success callback value.
     * @param errorCallback The error callback value.
     * @param options The options value.
     */
    fun getCurrentPosition(
        successCallback: (GeolocationPosition) -> Unit,
        errorCallback: (GeolocationPositionError) -> Unit,
        options: dynamic = definedExternally
    )

    /**
     * Executes the watch position operation.
     *
     * @param successCallback The success callback value.
     * @param errorCallback The error callback value.
     * @param options The options value.
     * @return The resulting value.
     */
    fun watchPosition(
        successCallback: (GeolocationPosition) -> Unit,
        errorCallback: (GeolocationPositionError) -> Unit,
        options: dynamic = definedExternally
    ): Int

    /**
     * Clears watch.
     *
     * @param watchId The watch id value.
     */
    fun clearWatch(watchId: Int)
}

/**
 * External interface for GeolocationPosition
 */
external interface GeolocationPosition {
    /** The property declaration value. */
    val coords: GeolocationCoordinates
    /** The property declaration value. */
    val timestamp: Number
}

/**
 * External interface for GeolocationCoordinates
 */
external interface GeolocationCoordinates {
    /** The property declaration value. */
    val latitude: Double
    /** The property declaration value. */
    val longitude: Double
    /** The property declaration value. */
    val altitude: Double?
    /** The property declaration value. */
    val accuracy: Double
    /** The property declaration value. */
    val altitudeAccuracy: Double?
    /** The property declaration value. */
    val heading: Double?
    /** The property declaration value. */
    val speed: Double?
}

/**
 * External interface for GeolocationPositionError
 */
external interface GeolocationPositionError {
    /** The property declaration value. */
    val code: Int
    /** The property declaration value. */
    val message: String
}

/**
 * Geolocation options

 * @property enableHighAccuracy The enable high accuracy value.
 * @property timeout Timeout in milliseconds.
 * @property maximumAge The maximum age value.
 */
class GeolocationOptions(
    val enableHighAccuracy: Boolean = false,
    val timeout: Int = 0,
    val maximumAge: Int = 0
)

/**
 * Geolocation state

 * @property position The position value.
 * @property error The error value.
 * @property loading The loading value.
 */
data class GeolocationState(
    val position: Position?,
    val error: String?,
    val loading: Boolean
)

/**
 * Geolocation position

 * @property coords The coords value.
 * @property timestamp The timestamp value.
 */
data class Position(
    val coords: Coordinates,
    val timestamp: Long
)

/**
 * Geolocation coordinates

 * @property latitude The latitude value.
 * @property longitude The longitude value.
 * @property altitude The altitude value.
 * @property accuracy The accuracy value.
 * @property altitudeAccuracy The altitude accuracy value.
 * @property heading The heading value.
 * @property speed The speed value.
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
        val successCallback = { jsPosition: GeolocationPosition ->
            // Extract coordinates from JS position object
            val coords = Coordinates(
                latitude = jsPosition.coords.latitude,
                longitude = jsPosition.coords.longitude,
                altitude = jsPosition.coords.altitude,
                accuracy = jsPosition.coords.accuracy,
                altitudeAccuracy = jsPosition.coords.altitudeAccuracy,
                heading = jsPosition.coords.heading,
                speed = jsPosition.coords.speed
            )

            // Create Position object
            val position = Position(
                coords = coords,
                timestamp = jsPosition.timestamp.toLong()
            )

            // Update state
            state.value = GeolocationState(
                position = position,
                error = null,
                loading = false
            )
        }

        // Set up error callback
        val errorCallback = { jsError: GeolocationPositionError ->
            state.value = GeolocationState(
                position = null,
                error = jsError.message,
                loading = false
            )
        }

        // Geolocation watch ID for cleanup
        var watchId: Int? = null

        // Get navigator
        @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val browserNavigator = window.asDynamic().navigator as BrowserNavigator

        // Check if geolocation is available
        if (browserNavigator.geolocation != null) {
            @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val geolocation = browserNavigator.geolocation as Geolocation

            // Create geolocation options
            val jsOptions = js("({})")
            jsOptions.enableHighAccuracy = options.enableHighAccuracy
            jsOptions.timeout = options.timeout
            jsOptions.maximumAge = options.maximumAge

            // Start watching position
            watchId = geolocation.watchPosition(
                successCallback,
                errorCallback,
                jsOptions
            )

            // Initial position request
            geolocation.getCurrentPosition(
                successCallback,
                errorCallback,
                jsOptions
            )
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
            if (watchId != null) {
                @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
                val geolocation = browserNavigator.geolocation as? Geolocation
                geolocation?.clearWatch(watchId)
            }
        }
    }

    return state
}

/**
 * Web animation keyframe

 * @property offset The offset value.
 * @property easing The easing value.
 * @property properties The properties value.
 */
data class Keyframe(
    val offset: Double? = null,
    val easing: String? = null,
    val properties: Map<String, Any>
)

/**
 * Web animation options

 * @property duration Duration in milliseconds.
 * @property iterations The iterations value.
 * @property delay Delay in milliseconds.
 * @property easing The easing value.
 * @property direction The direction value.
 * @property fill The fill value.
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
 * External interface for Web Animation
 */
external interface WebAnimation {
    /** Executes the play operation. */
    fun play()
    /** Pauses the operation. */
    fun pause()
    /** Cancels the operation. */
    fun cancel()
    /** Executes the finish operation. */
    fun finish()
    /** Executes the reverse operation. */
    fun reverse()
    /** The property declaration value. */
    var currentTime: Double?
    /** The property declaration value. */
    var playbackRate: Double
    /** The property declaration value. */
    val playState: String
}

/**
 * Web animation API interface
 */
class WebAnimationAPI {
    private var animation: WebAnimation? = null

    /** Executes the play operation. */
    fun play() {
        animation?.play()
    }

    /** Pauses the operation. */
    fun pause() {
        animation?.pause()
    }

    /** Cancels the operation. */
    fun cancel() {
        animation?.cancel()
    }

    /** Executes the finish operation. */
    fun finish() {
        animation?.finish()
    }

    /** Executes the reverse operation. */
    fun reverse() {
        animation?.reverse()
    }

    /**
     * Sets animation.
     *
     * @param anim The anim value.
     */
    fun setAnimation(anim: WebAnimation) {
        animation = anim
    }

    /**
     * Returns current time.
     *
     * @return The resulting value.
     */
    fun getCurrentTime(): Double {
        return animation?.currentTime ?: 0.0
    }

    /**
     * Sets current time.
     *
     * @param time The time value.
     */
    fun setCurrentTime(time: Double) {
        animation?.currentTime = time
    }

    /**
     * Returns playback rate.
     *
     * @return The resulting value.
     */
    fun getPlaybackRate(): Double {
        return animation?.playbackRate ?: 1.0
    }

    /**
     * Sets playback rate.
     *
     * @param rate The rate value.
     */
    fun setPlaybackRate(rate: Double) {
        animation?.let { it.playbackRate = rate }
    }

    /**
     * Returns whether paused.
     *
     * @return The resulting value.
     */
    fun isPaused(): Boolean {
        return animation?.playState == "paused"
    }

    /**
     * Returns whether running.
     *
     * @return The resulting value.
     */
    fun isRunning(): Boolean {
        return animation?.playState == "running"
    }
}

/**
 * External interface for HTMLElement with animate method
 */
external interface AnimatableElement {
    /**
     * Executes the animate operation.
     *
     * @param keyframes The keyframes value.
     * @param options The options value.
     * @return The resulting value.
     */
    fun animate(keyframes: dynamic, options: dynamic): WebAnimation
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
        val keyframesArray = js("[]")

        for (keyframe in keyframes) {
            val frame = js("{}")

            // Set offset if provided
            if (keyframe.offset != null) {
                frame.offset = keyframe.offset
            }

            // Set easing if provided
            if (keyframe.easing != null) {
                frame.easing = keyframe.easing
            }

            // Add all properties from the properties map
            for ((key, value) in keyframe.properties) {
                frame[key] = value
            }

            keyframesArray.push(frame)
        }

        // Create animation options
        val animOptions = js("{}")

        animOptions.duration = options.duration
        animOptions.iterations = options.iterations
        animOptions.delay = options.delay
        animOptions.easing = options.easing
        animOptions.direction = options.direction
        animOptions.fill = options.fill

        // Get the DOM element from elementRef
        val element = elementRef.getElement()

        // Create and start the animation
        if (element != null) {
            @Suppress("UNCHECKED_CAST", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val animatableElement = element.asDynamic() as AnimatableElement
            val animation = animatableElement.animate(keyframesArray, animOptions)
            api.setAnimation(animation)
        }

        // Return cleanup function
        return@onMountWithCleanup {
            // Cancel the animation on cleanup
            api.cancel()
        }
    }

    return api
}
