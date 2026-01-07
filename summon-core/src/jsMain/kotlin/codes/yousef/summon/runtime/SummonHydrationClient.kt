package codes.yousef.summon.runtime

import codes.yousef.summon.hydration.Bootloader
import codes.yousef.summon.hydration.GlobalEventListener
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element

/**
 * Client-side hydration for Summon components.
 * This script runs in the browser and activates server-rendered Summon components.
 */
object SummonHydrationClient {

    /**
     * Initializes Summon hydration when the page loads.
     * This function is automatically called when the JS bundle loads.
     */
    fun initialize() {
        // Check for performance metrics opt-in and initialize if enabled
        PerformanceMetrics.checkAndInitialize()

        perfMarkStart("initialize", HydrationPhase.INITIALIZATION)

        SummonLogger.log("=== SUMMON HYDRATION CLIENT INITIALIZING ===")
        SummonLogger.log("Browser user agent: ${js("navigator.userAgent")}")
        SummonLogger.log("Current URL: ${window.location.href}")
        SummonLogger.log("Document ready state: ${document.readyState}")

        if (js("document.readyState === 'loading'") as Boolean) {
            SummonLogger.log("Document still loading, waiting for DOMContentLoaded...")
            perfMarkStart("dom-wait", HydrationPhase.DOM_READY)
            document.addEventListener("DOMContentLoaded", {
                perfMarkEnd("dom-wait")
                SummonLogger.log("DOMContentLoaded event fired, starting hydration...")
                startHydration()
            })
        } else {
            SummonLogger.log("Document already loaded, starting hydration immediately...")
            startHydration()
        }

        perfMarkEnd("initialize")
    }

    private fun startHydration() {
        perfMarkStart("startHydration", HydrationPhase.INITIALIZATION)
        try {
            SummonLogger.log("Starting Summon component hydration...")

            // Initialize Global Event Listener (The Ears)
            withPerfMetrics("global-event-listener-init", HydrationPhase.EVENT_SYSTEM) {
                GlobalEventListener.init()
            }

            // Process Bootloader Queue
            withPerfMetrics("bootloader-process-queue", HydrationPhase.EVENT_REPLAY) {
                Bootloader.processQueue()
            }
            
            // Check for parked state
            val state = window.asDynamic().__SUMMON_STATE__
            if (state != null) {
                SummonLogger.log("Hydrated with state object")
            }

            SummonLogger.log("Document ready state: ${document.readyState}")
            SummonLogger.log("Document body exists: ${document.body != null}")

            // Load hydration data
            val hydrationData = loadHydrationData()
            SummonLogger.log("Hydration data loaded: ${hydrationData != null}")
            if (hydrationData != null) {
                SummonLogger.log("Hydration data version: ${hydrationData.version}")
                SummonLogger.log("Callbacks available: ${hydrationData.callbacks.size}")
                hydrationData.callbacks.forEach { callbackId ->
                    SummonLogger.log("Available callback: $callbackId")
                }
            }

            // Discover SSR root for proper hydration
            val rootElement: Element? =
                document.getElementById("summon-app")
                    ?: document.querySelector("[data-summon-hydration=\"root\"]")

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

            // Note: GlobalEventListener handles all event binding via event delegation
            // Deprecated hydrateClickHandlers/hydrateFormInputs removed for TBT optimization

            perfMarkEnd("startHydration")
            SummonLogger.log("Summon hydration completed successfully")

            // Mark hydration complete for performance metrics
            PerformanceMetrics.markHydrationComplete()
        } catch (e: Exception) {
            perfMarkEnd("startHydration")
            SummonLogger.error("Summon hydration failed: ${e.message}")
            SummonLogger.error("Exception: $e")
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

    // Deprecated methods removed


    // Deprecated methods removed
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
