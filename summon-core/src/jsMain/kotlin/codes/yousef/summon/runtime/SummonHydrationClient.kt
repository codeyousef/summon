package codes.yousef.summon.runtime

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import codes.yousef.summon.hydration.Bootloader
import codes.yousef.summon.hydration.GlobalEventListener
import codes.yousef.summon.hydration.HydrationScheduler
import codes.yousef.summon.hydration.HydrationTask
import codes.yousef.summon.hydration.HydrationPriority
import codes.yousef.summon.hydration.SimpleHydrationTask

/**
 * Client-side hydration for Summon components.
 * This script runs in the browser and activates server-rendered Summon components.
 *
 * Uses HydrationScheduler for non-blocking hydration that doesn't block the main thread.
 */
object SummonHydrationClient {

    private val scheduler = HydrationScheduler.instance

    /**
     * Enable/disable verbose logging.
     */
    var enableLogging = true

    /**
     * Initializes Summon hydration when the page loads.
     * This function is automatically called when the JS bundle loads.
     */
    fun initialize() {
        SummonLogger.log("=== SUMMON HYDRATION CLIENT INITIALIZING ===")
        SummonLogger.log("Browser user agent: ${js("navigator.userAgent")}")
        SummonLogger.log("Current URL: ${window.location.href}")
        SummonLogger.log("Document ready state: ${document.readyState}")

        // Configure scheduler logging based on our logging setting
        scheduler.enableLogging = enableLogging

        if (js("document.readyState === 'loading'") as Boolean) {
            SummonLogger.log("Document still loading, waiting for DOMContentLoaded...")
            document.addEventListener("DOMContentLoaded", {
                SummonLogger.log("DOMContentLoaded event fired, starting hydration...")
                startHydration()
            })
        } else {
            SummonLogger.log("Document already loaded, starting hydration immediately...")
            startHydration()
        }
    }

    private fun startHydration() {
        try {
            SummonLogger.log("Starting Summon component hydration (non-blocking)...")
            val startTime = js("performance.now()") as Double

            // Initialize Global Event Listener (The Ears) - CRITICAL priority
            scheduler.scheduleTask(SimpleHydrationTask(
                id = "global-event-listener",
                priority = HydrationPriority.CRITICAL
            ) {
                GlobalEventListener.init()
                true
            })

            // Process Bootloader Queue - CRITICAL priority
            scheduler.scheduleTask(SimpleHydrationTask(
                id = "bootloader-queue",
                priority = HydrationPriority.CRITICAL
            ) {
                Bootloader.processQueue()
                true
            })

            // Check for parked state
            val state = window.asDynamic().__SUMMON_STATE__
            if (state != null) {
                SummonLogger.log("Hydrated with state object")
            }

            SummonLogger.log("Document ready state: ${document.readyState}")
            SummonLogger.log("Document body exists: ${document.body != null}")

            // Load hydration data - this is synchronous and fast
            val hydrationData = loadHydrationData()
            SummonLogger.log("Hydration data loaded: ${hydrationData != null}")
            if (hydrationData != null) {
                SummonLogger.log("Hydration data version: ${hydrationData.version}")
                SummonLogger.log("Callbacks available: ${hydrationData.callbacks.size}")
                hydrationData.callbacks.forEach { callbackId ->
                    SummonLogger.log("Available callback: $callbackId")
                }
            }

            // Check for button elements BEFORE hydration
            val allButtons = document.querySelectorAll("button")
            SummonLogger.log("Total buttons found on page: ${allButtons.length}")

            val clickableButtons = document.querySelectorAll("button[data-onclick-action=\"true\"]")
            SummonLogger.log("Buttons with data-onclick-action='true': ${clickableButtons.length}")

            for (i in 0 until clickableButtons.length) {
                val button = clickableButtons.item(i) as? org.w3c.dom.Element ?: continue
                val callbackId = button.getAttribute("data-onclick-id")
                val hasAction = button.getAttribute("data-onclick-action")
                SummonLogger.log("Button $i: callbackId='$callbackId', hasAction='$hasAction'")
                SummonLogger.log("Button $i HTML: ${button.outerHTML}")
            }

            // Discover SSR root for proper hydration
            val rootElement: Element? =
                document.getElementById("summon-app")
                    ?: document.querySelector("[data-summon-hydration=\"root\"]") as? Element

            if (rootElement != null) {
                SummonLogger.log(
                    "Found SSR root element for hydration: id='${rootElement.id}', data-summon-hydration='${
                        rootElement.getAttribute(
                            "data-summon-hydration"
                        )
                    }'"
                )
            } else {
                SummonLogger.warn("No SSR root element found (id='summon-app' or [data-summon-hydration=\"root\"]). Hydration will still attach handlers to existing DOM but without a known root container.")
            }

            // Schedule click handler hydration with priorities based on visibility
            scheduleClickHandlerHydration(hydrationData)

            // Schedule form input hydration - NEAR priority (usually lower in page)
            scheduler.scheduleTask(SimpleHydrationTask(
                id = "form-inputs",
                priority = HydrationPriority.NEAR
            ) {
                hydrateFormInputs()
                true
            })

            // Set up completion callback
            scheduler.onAllTasksComplete = {
                val elapsed = js("performance.now()") as Double - startTime
                SummonLogger.log("Summon hydration completed successfully in ${elapsed.toInt()}ms (non-blocking)")

                // Verify hydration worked
                val hydratedButtons = document.querySelectorAll("button[data-onclick-action=\"true\"]")
                SummonLogger.log("Post-hydration: buttons with click handlers: ${hydratedButtons.length}")
            }

            // Start the scheduler (it auto-starts on first task, but explicit start is clearer)
            scheduler.start()

        } catch (e: Exception) {
            SummonLogger.error("Summon hydration failed: ${e.message}")
            SummonLogger.error("Exception: $e")
        }
    }

    /**
     * Schedule click handler hydration with priorities based on element visibility.
     * Elements in the viewport get VISIBLE priority, others get NEAR or DEFERRED.
     */
    private fun scheduleClickHandlerHydration(hydrationData: HydrationData?) {
        val clickableElements = document.querySelectorAll("[data-onclick-action=\"true\"]")
        SummonLogger.log("Scheduling hydration for ${clickableElements.length} clickable elements")

        for (i in 0 until clickableElements.length) {
            val element = clickableElements.item(i) as? Element ?: continue
            val callbackId = element.getAttribute("data-onclick-id") ?: continue
            val tagName = element.tagName.lowercase()

            // Determine priority based on element position
            val priority = determineElementPriority(element)

            scheduler.scheduleTask(SimpleHydrationTask(
                id = "click-handler-$callbackId",
                priority = priority
            ) {
                hydrateClickHandler(element, callbackId, hydrationData)
                true
            })

            SummonLogger.log("Scheduled click handler hydration: $callbackId (priority: $priority)")
        }
    }

    /**
     * Determine hydration priority based on element's viewport visibility.
     */
    private fun determineElementPriority(element: Element): HydrationPriority {
        // Check for explicit priority attribute
        val explicitPriority = element.getAttribute(HydrationPriority.ATTRIBUTE_NAME)
        if (explicitPriority != null) {
            return HydrationPriority.fromString(explicitPriority)
        }

        // Check if element is in viewport
        try {
            val rect = element.getBoundingClientRect()
            val viewportHeight = window.innerHeight.toDouble()
            val viewportWidth = window.innerWidth.toDouble()

            // Element is visible if any part is in viewport
            val isInViewport = rect.top < viewportHeight &&
                    rect.bottom > 0 &&
                    rect.left < viewportWidth &&
                    rect.right > 0

            if (isInViewport) {
                return HydrationPriority.VISIBLE
            }

            // Element is near viewport (within 200px)
            val nearThreshold = 200.0
            val isNearViewport = rect.top < viewportHeight + nearThreshold &&
                    rect.bottom > -nearThreshold

            if (isNearViewport) {
                return HydrationPriority.NEAR
            }

            // Element is far from viewport
            return HydrationPriority.DEFERRED
        } catch (e: Exception) {
            // If we can't determine position, use VISIBLE as safe default
            return HydrationPriority.VISIBLE
        }
    }

    /**
     * Hydrate a single click handler for an element.
     */
    private fun hydrateClickHandler(element: Element, callbackId: String, hydrationData: HydrationData?): Boolean {
        val tagName = element.tagName.lowercase()

        // Verify the callback exists in our hydration data
        if (hydrationData?.callbacks?.contains(callbackId) == true) {
            SummonLogger.log("Callback $callbackId found in hydration data, adding event listener...")

            element.addEventListener("click", { event ->
                SummonLogger.log("CLICK EVENT TRIGGERED for callbackId: $callbackId")
                event.preventDefault()
                handleClick(callbackId)
            })

            SummonLogger.log("Event listener added successfully for: $callbackId")
            return true
        } else {
            SummonLogger.warn("Callback not found in hydration data: $callbackId")
            if (hydrationData == null) {
                SummonLogger.warn("  - Hydration data is null")
            } else {
                SummonLogger.warn("  - Available callbacks: ${hydrationData.callbacks}")
            }
            return false
        }
    }

    private fun loadHydrationData(): HydrationData? {
        val dataElement = document.getElementById("summon-hydration-data")
        if (dataElement == null) {
            SummonLogger.warn("No Summon hydration data found")
            return null
        }

        val jsonText = (dataElement.textContent ?: "").trim()
        return try {
            parseHydrationData(jsonText)
        } catch (e: Exception) {
            SummonLogger.error("Failed to parse hydration data: ${e.message}")
            null
        }
    }

    private fun parseHydrationData(jsonText: String): HydrationData {
        val parsed = js("JSON.parse(jsonText)")
        val callbacks = js("parsed.callbacks")

        // Handle both formats: simple array (core) or complex object (Spring Boot)
        val callbackIds = if (js("Array.isArray(callbacks)") as Boolean) {
            // Simple array format: ["callback-id-1", "callback-id-2"]
            callbacks as Array<String>
        } else {
            // Complex object format: {"callback-id": {"type": "login", ...}}
            js("Object.keys(callbacks)") as Array<String>
        }

        return HydrationData(
            version = js("parsed.version") as Int,
            callbacks = callbackIds.toList(),
            timestamp = (js("parsed.timestamp") as Number).toDouble()
        )
    }

    @Deprecated("Use GlobalEventListener instead.")
    private fun hydrateClickHandlers(hydrationData: HydrationData?) {
        SummonLogger.log("Starting to hydrate click handlers...")

        // Find all elements with click actions
        val clickableElements = document.querySelectorAll("[data-onclick-action=\"true\"]")
        SummonLogger.log("Found ${clickableElements.length} elements with data-onclick-action='true'")

        var successCount = 0
        var errorCount = 0

        for (i in 0 until clickableElements.length) {
            val element = clickableElements.item(i) as? Element ?: continue
            val callbackId = element.getAttribute("data-onclick-id")
            val tagName = element.tagName.lowercase()

            SummonLogger.log("Processing element $i: <$tagName> with callbackId='$callbackId'")

            if (callbackId != null) {
                // Verify the callback exists in our hydration data
                if (hydrationData?.callbacks?.contains(callbackId) == true) {
                    SummonLogger.log("Callback $callbackId found in hydration data, adding event listener...")

                    element.addEventListener("click", { event ->
                        SummonLogger.log("CLICK EVENT TRIGGERED for callbackId: $callbackId")
                        event.preventDefault()
                        handleClick(callbackId)
                    })

                    // Test that the event listener was added
                    SummonLogger.log("Event listener added successfully for: $callbackId")
                    successCount++
                } else {
                    SummonLogger.warn("Callback not found in hydration data: $callbackId")
                    if (hydrationData == null) {
                        SummonLogger.warn("  - Hydration data is null")
                    } else {
                        SummonLogger.warn("  - Available callbacks: ${hydrationData.callbacks}")
                    }
                    errorCount++
                }
            } else {
                SummonLogger.warn("Element missing data-onclick-id attribute: <$tagName>")
                errorCount++
            }
        }

        SummonLogger.log("Click handler hydration complete:")
        SummonLogger.log("  - Successfully hydrated: $successCount elements")
        SummonLogger.log("  - Errors: $errorCount elements")
        SummonLogger.log("  - Total processed: ${clickableElements.length} elements")
    }

    @Deprecated("Use ClientDispatcher instead.")
    private fun handleClick(callbackId: String) {
        SummonLogger.log("=== BUTTON CLICK DETECTED ===")
        SummonLogger.log("Executing callback: $callbackId")
        SummonLogger.log("Current URL: ${window.location.href}")
        SummonLogger.log("About to make server request...")

        // For SSR hydration, we need to trigger a callback execution on the server
        // This could be done via various methods:
        // 1. POST request to a callback endpoint
        // 2. WebSocket message
        // 3. Server-Sent Events

        // For now, we'll make a POST request to execute the callback
        executeCallbackOnServer(callbackId)
    }

    @Deprecated("Use ClientDispatcher instead.")
    private fun executeCallbackOnServer(callbackId: String) {
        SummonLogger.log("=== MAKING SERVER REQUEST ===")

        try {
            val headers = Headers()
            headers.append("Cache-Control", "no-cache")

            val requestInit = RequestInit(
                method = "POST",
                headers = headers
            )

            SummonLogger.log("Request URL: /summon/callback/$callbackId")
            SummonLogger.log("Request method: POST")

            window.fetch("/summon/callback/$callbackId", requestInit)
                .then { response ->
                    SummonLogger.log("Server response received - Status: ${response.status}")
                    SummonLogger.log("Response headers: ${response.headers}")

                    if (response.ok) {
                        SummonLogger.log("Response OK, parsing JSON...")

                        // Parse response to check for actions
                        response.json().then { data ->
                            SummonLogger.log("Callback executed successfully: $callbackId")
                            SummonLogger.log("Response data: $data")

                            val action = js("data.action") as? String
                            val redirectUrl = js("data.redirectUrl") as? String

                            SummonLogger.log("Response action: $action")
                            SummonLogger.log("Response redirectUrl: $redirectUrl")

                            when (action) {
                                "redirect" -> {
                                    if (redirectUrl != null) {
                                        SummonLogger.log("Redirecting to: $redirectUrl")
                                        window.location.href = redirectUrl
                                    } else {
                                        SummonLogger.warn("Redirect action but no redirectUrl provided")
                                        window.location.reload()
                                    }
                                }

                                "reload" -> {
                                    SummonLogger.log("Reloading page")
                                    window.location.reload()
                                }

                                else -> {
                                    // Default action: reload page
                                    SummonLogger.log("Default action: reloading page")
                                    window.location.reload()
                                }
                            }
                        }.catch { jsonError ->
                            SummonLogger.error("Error parsing response JSON: $jsonError")
                        }
                    } else {
                        SummonLogger.error("Server returned error status: ${response.status}")

                        response.text().then { responseText ->
                            SummonLogger.error("Error response body: $responseText")
                        }.catch { textError ->
                            SummonLogger.error("Could not read error response: $textError")
                        }
                    }
                }
                .catch { error ->
                    SummonLogger.error("Network error executing callback: $callbackId")
                    SummonLogger.error("Error details: $error")
                    SummonLogger.error("Error type: ${js("typeof error")}")
                    SummonLogger.error("Error message: ${js("error.message")}")
                }

            SummonLogger.log("Fetch request sent for callback: $callbackId")

        } catch (e: Exception) {
            SummonLogger.error("Exception in executeCallbackOnServer: ${e.message}")
            SummonLogger.error("Exception: $e")
        }
    }

    @Deprecated("Use GlobalEventListener instead.")
    private fun hydrateFormInputs() {
        // Add enhanced interactions to form inputs
        val inputs = document.querySelectorAll("input, textarea, select")

        for (i in 0 until inputs.length) {
            val input = inputs.item(i) as? Element ?: continue

            // Add focus styling
            input.addEventListener("focus", {
                input.setAttribute("data-summon-focused", "true")
            })

            input.addEventListener("blur", {
                input.removeAttribute("data-summon-focused")
            })
        }
    }
}

/**
 * Data structure for hydration information.
 */
data class HydrationData(
    val version: Int,
    val callbacks: List<String>,
    val timestamp: Double
)

/**
 * Entry point for the Summon hydration client.
 * This function is called when the script loads.
 */
@JsExport
fun main() {
    SummonHydrationClient.initialize()
}
