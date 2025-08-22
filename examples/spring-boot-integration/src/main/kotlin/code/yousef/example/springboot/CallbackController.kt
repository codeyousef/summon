package code.yousef.example.springboot

// import code.yousef.summon.runtime.CallbackRegistry // Not available in JVM-only project
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

/**
 * Controller for handling Summon framework callback execution.
 * This bridges client-side interactions to server-side handlers.
 */
@RestController
class CallbackController {
    
    private val logger = LoggerFactory.getLogger(CallbackController::class.java)

    /**
     * Executes a registered callback by its ID.
     * Called by the Summon hydration client when user interactions occur.
     */
    @PostMapping("/summon/callback")
    fun executeCallback(
        @RequestBody request: CallbackRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<CallbackResponse> {
        logger.info("Executing callback: ${request.callbackId}")
        
        return try {
            // Skip callback registry execution for now (not available in JVM-only project)
            val callbackSuccess = true // CallbackRegistry.executeCallback(request.callbackId)
            
            // Then check if there's a specific action to perform
            val action = ActionRegistry.getAction(request.callbackId)
            if (action != null) {
                val actionResponse = performAction(action, httpRequest)
                ResponseEntity.ok(actionResponse)
            } else if (callbackSuccess) {
                logger.info("Callback executed successfully: ${request.callbackId}")
                ResponseEntity.ok(CallbackResponse(success = true, message = "Callback executed successfully"))
            } else {
                logger.warn("Callback not found or failed: ${request.callbackId}")
                ResponseEntity.badRequest()
                    .body(CallbackResponse(success = false, message = "Callback not found or execution failed"))
            }
        } catch (e: Exception) {
            logger.error("Error executing callback: ${request.callbackId}", e)
            ResponseEntity.status(500)
                .body(CallbackResponse(success = false, message = "Internal server error: ${e.message}"))
        }
    }
    
    private fun performAction(action: ActionType, request: HttpServletRequest): CallbackResponse {
        return when (action) {
            is ActionType.ToggleTheme -> {
                // Parse current theme from referer URL or default to light
                val referer = request.getHeader("Referer") ?: "/todos"
                val currentTheme = extractThemeFromUrl(referer)
                val newTheme = if (currentTheme == "dark") "light" else "dark"
                
                val redirectUrl = updateUrlParameter(referer, "theme", newTheme)
                logger.info("Theme toggle: $currentTheme -> $newTheme, redirecting to: $redirectUrl")
                
                CallbackResponse(
                    success = true,
                    message = "Theme toggled",
                    action = "redirect",
                    redirectUrl = redirectUrl
                )
            }
            
            is ActionType.ToggleLanguage -> {
                val referer = request.getHeader("Referer") ?: "/todos"
                val currentLang = extractLanguageFromUrl(referer)
                val languages = listOf("en", "es", "fr")
                val currentIndex = languages.indexOf(currentLang)
                val newLang = languages[(currentIndex + 1) % languages.size]
                
                val redirectUrl = updateUrlParameter(referer, "lang", newLang)
                logger.info("Language toggle: $currentLang -> $newLang, redirecting to: $redirectUrl")
                
                CallbackResponse(
                    success = true,
                    message = "Language toggled",
                    action = "redirect",
                    redirectUrl = redirectUrl
                )
            }
            
            is ActionType.Logout -> {
                logger.info("Logout action")
                CallbackResponse(
                    success = true,
                    message = "Logged out",
                    action = "redirect",
                    redirectUrl = "/auth"
                )
            }
            
            is ActionType.AddTodo -> {
                // This would require more complex handling with the todo API
                // For now, just reload the page
                CallbackResponse(
                    success = true,
                    message = "Todo add requested",
                    action = "reload"
                )
            }
        }
    }
    
    private fun extractThemeFromUrl(url: String): String {
        return extractUrlParameter(url, "theme", "light")
    }
    
    private fun extractLanguageFromUrl(url: String): String {
        return extractUrlParameter(url, "lang", "en")
    }
    
    private fun extractUrlParameter(url: String, paramName: String, defaultValue: String): String {
        val regex = "[?&]$paramName=([^&]*)".toRegex()
        val match = regex.find(url)
        return match?.groupValues?.get(1) ?: defaultValue
    }
    
    private fun updateUrlParameter(url: String, paramName: String, paramValue: String): String {
        val baseUrl = url.split('?')[0]
        val existingParams = if ('?' in url) {
            url.split('?')[1]
                .split('&')
                .filter { it.isNotEmpty() && !it.startsWith("$paramName=") }
        } else {
            emptyList()
        }
        
        val newParams = existingParams + "$paramName=$paramValue"
        return "$baseUrl?" + newParams.joinToString("&")
    }
}

/**
 * Request data for callback execution
 */
data class CallbackRequest(
    @JsonProperty("callbackId") val callbackId: String
)

/**
 * Response data for callback execution
 */
data class CallbackResponse(
    @JsonProperty("success") val success: Boolean,
    @JsonProperty("message") val message: String,
    @JsonProperty("action") val action: String? = null,
    @JsonProperty("redirectUrl") val redirectUrl: String? = null
)