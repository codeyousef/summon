package code.yousef.summon.runtime

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
        SummonLogger.log("=== SUMMON HYDRATION CLIENT INITIALIZING ===")
        SummonLogger.log("Browser user agent: ${js("navigator.userAgent")}")
        SummonLogger.log("Current URL: ${window.location.href}")
        SummonLogger.log("Document ready state: ${document.readyState}")
        
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
            SummonLogger.log("Starting Summon component hydration...")
            SummonLogger.log("Document ready state: ${document.readyState}")
            SummonLogger.log("Document body exists: ${document.body != null}")
            
            // Load hydration data
            val hydrationData = loadHydrationData()
            SummonLogger.log("Hydration data loaded: ${hydrationData != null}")

            // Legacy hydration used data-onclick-action markers. Those collectors broke interactive
            // buttons by preventing default behaviour. We now defer to the client runtime to attach
            // real listeners during composition, so this pass only reports what was generated.
            val interactiveElements = document.querySelectorAll("[data-onclick-action=\"true\"]")
            SummonLogger.log("Found ${interactiveElements.length} legacy data-onclick nodes; leaving them untouched.")

            hydrateFormInputs()

            SummonLogger.log("Summon hydration observation completed")
        } catch (e: Exception) {
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
        
        val jsonText = dataElement.textContent ?: ""
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
            timestamp = js("parsed.timestamp") as Long
        )
    }

    private fun hydrateClickHandlers(@Suppress("UNUSED_PARAMETER") hydrationData: HydrationData?) {
        // Intentionally blank: interactive wiring now happens inside the JS/WASM renderer during
        // composition. Leaving this in place avoids breaking binary compatibility for older apps.
    }
    
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
    val timestamp: Long
)

/**
 * Entry point for the Summon hydration client.
 * This function is called when the script loads.
 */
@JsExport
fun main() {
    SummonHydrationClient.initialize()
}
