package code.yousef.summon.runtime

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Headers

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
        SummonLogger.log("Summon hydration client initializing...")
        
        if (js("document.readyState === 'loading'") as Boolean) {
            document.addEventListener("DOMContentLoaded", {
                startHydration()
            })
        } else {
            startHydration()
        }
    }
    
    private fun startHydration() {
        try {
            SummonLogger.log("Starting Summon component hydration...")
            
            // Load hydration data
            val hydrationData = loadHydrationData()
            
            // Hydrate all interactive elements
            hydrateClickHandlers(hydrationData)
            hydrateFormInputs()
            
            SummonLogger.log("Summon hydration completed successfully")
        } catch (e: Exception) {
            SummonLogger.error("Summon hydration failed: ${e.message}")
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
        val callbacks = js("parsed.callbacks") as Array<String>
        return HydrationData(
            version = js("parsed.version") as Int,
            callbacks = callbacks.toList(),
            timestamp = js("parsed.timestamp") as Long
        )
    }
    
    private fun hydrateClickHandlers(hydrationData: HydrationData?) {
        // Find all elements with click actions
        val clickableElements = document.querySelectorAll("[data-onclick-action=\"true\"]")
        
        for (i in 0 until clickableElements.length) {
            val element = clickableElements.item(i) as? Element ?: continue
            val callbackId = element.getAttribute("data-onclick-id")
            
            if (callbackId != null) {
                // Verify the callback exists in our hydration data
                if (hydrationData?.callbacks?.contains(callbackId) == true) {
                    element.addEventListener("click", { event ->
                        event.preventDefault()
                        handleClick(callbackId)
                    })
                    SummonLogger.log("Hydrated button with callback: $callbackId")
                } else {
                    SummonLogger.warn("Callback not found in hydration data: $callbackId")
                }
            } else {
                SummonLogger.warn("Button missing data-onclick-id attribute")
            }
        }
        
        SummonLogger.log("Hydrated ${clickableElements.length} clickable elements")
    }
    
    private fun handleClick(callbackId: String) {
        SummonLogger.log("Executing callback: $callbackId")
        
        // For SSR hydration, we need to trigger a callback execution on the server
        // This could be done via various methods:
        // 1. POST request to a callback endpoint
        // 2. WebSocket message
        // 3. Server-Sent Events
        
        // For now, we'll make a POST request to execute the callback
        executeCallbackOnServer(callbackId)
    }
    
    private fun executeCallbackOnServer(callbackId: String) {
        // Use Kotlin/JS fetch API instead of raw JavaScript
        val headers = Headers()
        headers.append("Content-Type", "application/json")
        
        val requestData = CallbackRequest(callbackId)
        val json = Json.encodeToString(requestData)
        
        val requestInit = RequestInit(
            method = "POST",
            headers = headers,
            body = json
        )
        
        window.fetch("/summon/callback", requestInit)
            .then { response ->
                if (response.ok) {
                    // Parse response to check for actions
                    response.json().then { data ->
                        SummonLogger.log("Callback executed successfully: $callbackId")
                        
                        val action = js("data.action") as? String
                        val redirectUrl = js("data.redirectUrl") as? String
                        
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
                                window.location.reload()
                            }
                        }
                    }
                } else {
                    SummonLogger.error("Failed to execute callback: $callbackId (${response.status})")
                }
            }
            .catch { error ->
                SummonLogger.error("Error executing callback: $callbackId - $error")
            }
        
        SummonLogger.log("Sent callback execution request for: $callbackId")
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
 * Request data for executing callbacks on the server.
 */
@Serializable
data class CallbackRequest(
    val callbackId: String
)

/**
 * Entry point for the Summon hydration client.
 * This function is called when the script loads.
 */
@JsExport
fun main() {
    SummonHydrationClient.initialize()
}