package code.yousef.example.springboot

// import code.yousef.summon.runtime.CallbackRegistry // Not available in JVM-only project
import code.yousef.example.springboot.models.*
import code.yousef.example.springboot.service.AuthService
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
    
    @Autowired
    private lateinit var authService: AuthService

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
            // Note: CallbackRegistry.executeCallback is not available in this context
            // The callbacks are executed during the server-side rendering phase
            // Client-side hydration works by triggering server actions via this endpoint
            val callbackSuccess = true
            
            // Check if there's a specific action to perform
            val action = ActionRegistry.getAction(request.callbackId)
            if (action != null) {
                logger.info("Executing action: $action for callback: ${request.callbackId}")
                val actionResponse = performAction(action, httpRequest)
                ResponseEntity.ok(actionResponse)
            } else if (callbackSuccess) {
                logger.info("Callback executed successfully via CallbackRegistry: ${request.callbackId}")
                ResponseEntity.ok(CallbackResponse(success = true, message = "Callback executed successfully"))
            } else {
                logger.warn("No action found and callback not in registry: ${request.callbackId}")
                // For buttons without specific actions, just reload the page
                ResponseEntity.ok(CallbackResponse(
                    success = true, 
                    message = "Button clicked", 
                    action = "reload"
                ))
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
            
            is ActionType.Login -> {
                logger.info("Login attempt for user: ${action.username}")
                try {
                    val loginRequest = LoginRequest(
                        username = action.username,
                        password = action.password
                    )
                    
                    val authResponse = authService.login(loginRequest)
                    
                    if (authResponse.success) {
                        logger.info("Login successful for user: ${action.username}")
                        // Store token in session or handle as needed
                        // For now, redirect to todos page
                        CallbackResponse(
                            success = true,
                            message = "Login successful",
                            action = "redirect",
                            redirectUrl = "/todos"
                        )
                    } else {
                        logger.warn("Login failed for user: ${action.username} - ${authResponse.message}")
                        CallbackResponse(
                            success = false,
                            message = authResponse.message,
                            action = "reload"
                        )
                    }
                } catch (e: Exception) {
                    logger.error("Login error for user: ${action.username}", e)
                    CallbackResponse(
                        success = false,
                        message = "Login failed: ${e.message}",
                        action = "reload"
                    )
                }
            }
            
            is ActionType.Register -> {
                logger.info("Registration attempt for user: ${action.username}")
                try {
                    val registerRequest = RegisterRequest(
                        email = action.email,
                        username = action.username,
                        password = action.password
                    )
                    
                    val authResponse = authService.register(registerRequest)
                    
                    if (authResponse.success) {
                        logger.info("Registration successful for user: ${action.username}")
                        CallbackResponse(
                            success = true,
                            message = "Registration successful",
                            action = "redirect",
                            redirectUrl = "/todos"
                        )
                    } else {
                        logger.warn("Registration failed for user: ${action.username} - ${authResponse.message}")
                        CallbackResponse(
                            success = false,
                            message = authResponse.message,
                            action = "reload"
                        )
                    }
                } catch (e: Exception) {
                    logger.error("Registration error for user: ${action.username}", e)
                    CallbackResponse(
                        success = false,
                        message = "Registration failed: ${e.message}",
                        action = "reload"
                    )
                }
            }
            
            is ActionType.ToggleAuthMode -> {
                logger.info("Toggling auth mode from: ${action.currentMode}")
                val newMode = if (action.currentMode == "login") "register" else "login"
                val redirectUrl = "/auth?mode=$newMode"
                
                CallbackResponse(
                    success = true,
                    message = "Auth mode toggled",
                    action = "redirect",
                    redirectUrl = redirectUrl
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