package code.yousef.summon.runtime

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit

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

            // Hydrate all interactive elements
            hydrateClickHandlers(hydrationData)
            hydrateFormInputs()

            // Verify hydration worked
            val hydratedButtons = document.querySelectorAll("button[data-onclick-action=\"true\"]")
            SummonLogger.log("Post-hydration: buttons with click handlers: ${hydratedButtons.length}")

            SummonLogger.log("Summon hydration completed successfully")
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
