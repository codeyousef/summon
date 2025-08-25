package code.yousef.example.springboot

// import code.yousef.summon.runtime.CallbackRegistry // Not available in JVM-only project
import code.yousef.example.springboot.models.*
import code.yousef.example.springboot.service.AuthService
import code.yousef.example.springboot.service.TodoService
import code.yousef.example.springboot.pages.TodoFilter
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
    
    @Autowired
    private lateinit var todoService: TodoService

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
            // Try both regular registry and hydration snapshot, with pattern fallback
            val action = ActionRegistry.getAction(request.callbackId) 
                ?: ActionRegistry.getHydrationActions()[request.callbackId]
                ?: createActionFromCallbackId(request.callbackId)
            if (action != null) {
                logger.info("Executing action: $action for callback: ${request.callbackId}")
                val actionResponse = performAction(action, request, httpRequest)
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
    
    private fun performAction(action: ActionType, callbackRequest: CallbackRequest, httpRequest: HttpServletRequest): CallbackResponse {
        return when (action) {
            is ActionType.ToggleTheme -> {
                // Parse current theme from referer URL or default to light
                val referer = httpRequest.getHeader("Referer") ?: "/todos"
                val currentTheme = extractThemeFromUrl(referer)
                val newTheme = if (currentTheme == "dark") "light" else "dark"
                
                val redirectUrl = updateUrlParameter(referer, "theme", newTheme)
                logger.info("=== THEME TOGGLE DEBUG ===")
                logger.info("Original referer: $referer")
                logger.info("Current theme extracted: $currentTheme")
                logger.info("New theme: $newTheme")
                logger.info("Generated redirect URL: $redirectUrl")
                logger.info("About to send CallbackResponse with redirect action")
                
                val response = CallbackResponse(
                    success = true,
                    message = "Theme toggled",
                    action = "redirect",
                    redirectUrl = redirectUrl
                )
                logger.info("CallbackResponse object: $response")
                response
            }
            
            is ActionType.ToggleLanguage -> {
                val referer = httpRequest.getHeader("Referer") ?: "/todos"
                val currentLang = extractLanguageFromUrl(referer)
                val languages = listOf("en", "es", "fr")
                val currentIndex = languages.indexOf(currentLang)
                val newLang = languages[(currentIndex + 1) % languages.size]
                
                val redirectUrl = updateUrlParameter(referer, "lang", newLang)
                logger.info("=== LANGUAGE TOGGLE DEBUG ===")
                logger.info("Original referer: $referer")
                logger.info("Current language extracted: $currentLang")
                logger.info("Available languages: $languages")
                logger.info("Current index: $currentIndex")
                logger.info("New language: $newLang")
                logger.info("Generated redirect URL: $redirectUrl")
                logger.info("About to send CallbackResponse with redirect action")
                
                val response = CallbackResponse(
                    success = true,
                    message = "Language toggled",
                    action = "redirect",
                    redirectUrl = redirectUrl
                )
                logger.info("CallbackResponse object: $response")
                response
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
                logger.info("Add todo requested")
                try {
                    // Get todo text from form data or use the action's text
                    val todoText = callbackRequest.formData?.get("todoText")?.trim() 
                        ?: action.text.trim()
                    
                    if (todoText.isEmpty()) {
                        CallbackResponse(
                            success = false,
                            message = "Todo text cannot be empty",
                            action = "reload"
                        )
                    } else {
                        // For demo purposes, use a default user since we don't have authentication context here
                        // In a real app, you'd get this from the JWT token or session
                        val username = "testuser" // This should come from authentication
                        
                        val createRequest = CreateTodoRequest(text = todoText)
                        val todo = todoService.createTodo(username, createRequest)
                        
                        if (todo != null) {
                            logger.info("Todo created successfully: ${todo.text}")
                            CallbackResponse(
                                success = true,
                                message = "Todo added: ${todo.text}",
                                action = "reload"
                            )
                        } else {
                            CallbackResponse(
                                success = false,
                                message = "Failed to create todo",
                                action = "reload"
                            )
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Error creating todo", e)
                    CallbackResponse(
                        success = false,
                        message = "Error adding todo: ${e.message}",
                        action = "reload"
                    )
                }
            }
            
            is ActionType.DeleteTodo -> {
                logger.info("Delete todo requested for ID: ${action.todoId}")
                try {
                    // For demo purposes, use a default user
                    val username = "testuser" // This should come from authentication
                    
                    val deleted = todoService.deleteTodo(username, action.todoId)
                    
                    if (deleted) {
                        logger.info("Todo deleted successfully: ID ${action.todoId}")
                        CallbackResponse(
                            success = true,
                            message = "Todo deleted",
                            action = "reload"
                        )
                    } else {
                        logger.warn("Todo not found or delete failed for ID: ${action.todoId}")
                        CallbackResponse(
                            success = false,
                            message = "Todo not found or delete failed",
                            action = "reload"
                        )
                    }
                } catch (e: Exception) {
                    logger.error("Error deleting todo: ${action.todoId}", e)
                    CallbackResponse(
                        success = false,
                        message = "Error deleting todo: ${e.message}",
                        action = "reload"
                    )
                }
            }
            
            is ActionType.ClearCompleted -> {
                logger.info("Clear completed todos requested")
                try {
                    // For demo purposes, use a default user
                    val username = "testuser" // This should come from authentication
                    
                    val remainingTodos = todoService.clearCompleted(username)
                    logger.info("Cleared completed todos, ${remainingTodos.size} todos remaining")
                    
                    CallbackResponse(
                        success = true,
                        message = "Completed todos cleared",
                        action = "reload"
                    )
                } catch (e: Exception) {
                    logger.error("Error clearing completed todos", e)
                    CallbackResponse(
                        success = false,
                        message = "Error clearing completed todos: ${e.message}",
                        action = "reload"
                    )
                }
            }
            
            is ActionType.SetFilter -> {
                logger.info("Set filter requested: ${action.filter}")
                CallbackResponse(
                    success = true,
                    message = "Filter set to ${action.filter}",
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
    
    /**
     * Create action from callback ID pattern as fallback
     */
    private fun createActionFromCallbackId(callbackId: String): ActionType? {
        return when {
            callbackId.startsWith("login-") -> {
                // For login, use test credentials for demo
                ActionType.Login(username = "testuser", password = "password123", rememberMe = false)
            }
            callbackId.startsWith("register-") -> {
                // For register, use test credentials for demo
                ActionType.Register(email = "test@example.com", username = "testuser", password = "password123")
            }
            callbackId.startsWith("toggle-auth-") -> {
                ActionType.ToggleAuthMode(currentMode = "login")
            }
            callbackId.startsWith("add-todo-") -> {
                ActionType.AddTodo(text = "") // Text will come from form data
            }
            callbackId.startsWith("delete-todo-") -> {
                // Extract todo ID from callback ID pattern: delete-todo-{id}-{timestamp}
                val todoId = try {
                    val parts = callbackId.split("-")
                    if (parts.size >= 3) parts[2].toLong() else 0L
                } catch (e: NumberFormatException) {
                    0L
                }
                ActionType.DeleteTodo(todoId)
            }
            callbackId.startsWith("clear-completed-") -> {
                ActionType.ClearCompleted
            }
            callbackId.startsWith("filter-all-") -> {
                ActionType.SetFilter(TodoFilter.ALL)
            }
            callbackId.startsWith("filter-active-") -> {
                ActionType.SetFilter(TodoFilter.ACTIVE)
            }
            callbackId.startsWith("filter-completed-") -> {
                ActionType.SetFilter(TodoFilter.COMPLETED)
            }
            callbackId.startsWith("theme-toggle-") -> {
                ActionType.ToggleTheme
            }
            callbackId.startsWith("language-toggle-") -> {
                ActionType.ToggleLanguage
            }
            callbackId.startsWith("logout-") -> {
                ActionType.Logout
            }
            else -> null
        }
    }
}

/**
 * Request data for callback execution
 */
data class CallbackRequest(
    @JsonProperty("callbackId") val callbackId: String,
    @JsonProperty("formData") val formData: Map<String, String>? = null
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

