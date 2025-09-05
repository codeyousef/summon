package code.yousef.example.springboot

import code.yousef.example.springboot.pages.TodoFilter
import code.yousef.example.springboot.theme.AppTheme
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Div
import code.yousef.summon.effects.*
import code.yousef.summon.extensions.*
import code.yousef.summon.modifier.LayoutModifierExtras.display
import code.yousef.summon.modifier.LayoutModifierExtras.flexDirection
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.minHeight
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.state.mutableStateOf
import code.yousef.example.springboot.components.ThemedNavigationComponent
import code.yousef.example.springboot.components.ThemedFooterComponent

/**
 * Full page layout component that generates a complete HTML document
 * without any raw HTML/CSS/JS - pure Summon only.
 */
@Composable
fun FullPageLayout(
    title: String = "Spring Boot + Summon",
    currentPage: String = "",
    content: @Composable () -> Unit
) {
    // Remove direct renderer access - not needed in layout components

    // Global styles using Summon's GlobalStyle component
    GlobalStyles()

    // Themed Navigation
    ThemedNavigationComponent(currentPage = currentPage)

    // Main content
    Div(
        modifier = Modifier()
            .minHeight(100.vh)
            .display("flex")
            .flexDirection("column")
    ) {
        content()
    }

    // Themed Footer
    ThemedFooterComponent()
}

/**
 * Global styles component - replaces CSS reset and global styles
 */
@Composable
fun GlobalStyles() {
    // This would ideally use a GlobalStyle component from Summon
    // For now, we'll apply styles to a root container
    Div(
        modifier = Modifier()
            .attribute(
                "style", """
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    margin: 0;
                    padding: 0;
                }
                html { scroll-behavior: smooth; }
            """.trimIndent()
            )
            .display("none") // Hidden style container
    ) {
        // Empty content
    }
}

/**
 * Flash message component with auto-dismiss functionality
 * Replaces the raw HTML/JS alert system
 */
@Composable
fun FlashMessage(
    message: String?,
    type: String = "info",
    autoDismiss: Boolean = true,
    dismissDelay: Int = 5000
) {
    if (message == null) return

    val isVisible = mutableStateOf(true)

    if (autoDismiss) {
        // Simple auto-dismiss (would need proper effect system)
        // For now, just show the message without auto-dismiss
    }

    if (isVisible.value) {
        Div(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .padding(1.rem)
        ) {
            Div(
                modifier = Modifier()
                    .padding(1.rem)
                    .margin("${1.rem} 0")
                    .borderRadius(4.px)
                    .backgroundColor(
                        when (type) {
                            "success" -> "#d4edda"
                            "danger", "error" -> "#f8d7da"
                            "warning" -> "#fff3cd"
                            else -> "#d1ecf1" // info
                        }
                    )
                    .color(
                        when (type) {
                            "success" -> "#155724"
                            "danger", "error" -> "#721c24"
                            "warning" -> "#856404"
                            else -> "#0c5460" // info
                        }
                    )
                    .border(
                        "1px",
                        "solid",
                        when (type) {
                            "success" -> "#c3e6cb"
                            "danger", "error" -> "#f5c6cb"
                            "warning" -> "#ffeeba"
                            else -> "#bee5eb" // info
                        }
                    )
                    .cursor("pointer")
                    .onClick { isVisible.value = false }
            ) {
                Text(message)
            }
        }
    }
}

/**
 * Complete page wrapper that includes all necessary structure
 * This replaces the need for HTML templates
 */
@Composable
fun SummonPage(
    title: String,
    message: String? = null,
    messageType: String = "info",
    currentPage: String = "",
    content: @Composable () -> Unit
) {
    // This would be the root of our HTML document
    // In server-side rendering, this generates the complete HTML

    // Flash messages (if any)
    FlashMessage(message, messageType)

    // Page content
    FullPageLayout(title, currentPage) {
        content()
    }
}

/**
 * Extract callback information from rendered HTML patterns (independent of ActionRegistry)
 */
fun extractCallbackInfoFromPatterns(html: String): Map<String, ActionType> {
    val callbackInfo = mutableMapOf<String, ActionType>()
    val regex = """data-onclick-id="([^"]+)"""".toRegex()
    
    // println("DEBUG: Extracting callback info from HTML patterns...")
    
    regex.findAll(html).forEach { match ->
        val callbackId = match.groupValues[1]
        if (callbackId.isNotEmpty()) {
            // println("DEBUG: Found callback ID: $callbackId")
            
            // Determine action type based on callback ID pattern and context
            val action = when {
                callbackId.startsWith("login-") -> {
                    // Extract form data from HTML context (for demo, use default values)
                    ActionType.Login(
                        username = extractFormValue(html, "username") ?: "user",
                        password = extractFormValue(html, "password") ?: "pass", 
                        rememberMe = true
                    )
                }
                callbackId.startsWith("register-") -> {
                    ActionType.Register(
                        email = extractFormValue(html, "email") ?: "user@example.com",
                        username = extractFormValue(html, "username") ?: "user",
                        password = extractFormValue(html, "password") ?: "pass"
                    )
                }
                callbackId.startsWith("toggle-auth-") -> {
                    // Determine current mode from URL or context
                    val currentMode = if (html.contains("Welcome back!")) "login" else "register"
                    ActionType.ToggleAuthMode(currentMode = currentMode)
                }
                callbackId.startsWith("language-toggle-") -> {
                    ActionType.ToggleLanguage
                }
                callbackId.startsWith("theme-toggle-") -> {
                    ActionType.ToggleTheme
                }
                callbackId.startsWith("logout-") -> {
                    ActionType.Logout
                }
                callbackId.startsWith("add-todo-") -> {
                    ActionType.AddTodo(text = "")
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
                else -> {
                    // println("DEBUG: Unknown callback pattern for ID: $callbackId")
                    null
                }
            }
            
            if (action != null) {
                // println("DEBUG: Created action for $callbackId: $action")
                callbackInfo[callbackId] = action
            }
        }
    }
    
    // println("DEBUG: Final callback info size: ${callbackInfo.size}")
    return callbackInfo
}

/**
 * Extract form field values from HTML (simple implementation for demonstration)
 */
fun extractFormValue(html: String, fieldName: String): String? {
    val valueRegex = """name="$fieldName"[^>]*value="([^"]*)"?""".toRegex()
    return valueRegex.find(html)?.groupValues?.get(1)
}

/**
 * Generate hydration data with callback information
 */
fun generateHydrationData(callbackInfo: Map<String, ActionType>): String {
    val callbacksJson = callbackInfo.map { (id, action) ->
        val actionJson = when (action) {
            is ActionType.Login -> """{"type":"login","username":"${action.username}","password":"${action.password}","rememberMe":${action.rememberMe}}"""
            is ActionType.Register -> """{"type":"register","email":"${action.email}","username":"${action.username}","password":"${action.password}"}"""
            is ActionType.ToggleAuthMode -> """{"type":"toggleAuth","currentMode":"${action.currentMode}"}"""
            is ActionType.ToggleTheme -> """{"type":"toggleTheme"}"""
            is ActionType.ToggleLanguage -> """{"type":"toggleLanguage"}"""
            is ActionType.Logout -> """{"type":"logout"}"""
            is ActionType.AddTodo -> """{"type":"addTodo","text":"${action.text}"}"""
            is ActionType.DeleteTodo -> """{"type":"deleteTodo","todoId":${action.todoId}}"""
            is ActionType.ClearCompleted -> """{"type":"clearCompleted"}"""
            is ActionType.SetFilter -> """{"type":"setFilter","filter":"${action.filter}"}"""
        }
        """"$id":$actionJson"""
    }.joinToString(",")
    
    return """{"version":1,"callbacks":{$callbacksJson},"timestamp":${System.currentTimeMillis()}}"""
}

/**
 * Generate CSS variables string from theme configuration
 */
fun generateThemeCssVariables(theme: String): String {
    val themeConfig = AppTheme.getTheme(theme == "dark")
    val cssVariables = themeConfig.designTokens?.map { (key, value) ->
        "$key: $value;"
    }?.joinToString("\n    ") ?: ""
    
    return ":root {\n    $cssVariables\n}"
}

/**
 * Renders a complete HTML document with proper structure
 * This is what the Spring Boot controller will use
 */
fun renderFullPage(
    renderer: PlatformRenderer,
    title: String,
    message: String? = null,
    messageType: String = "info",
    includeJavaScript: Boolean = false,
    theme: String = "light",
    currentPage: String = "",
    additionalScripts: List<String> = emptyList(),
    content: @Composable () -> Unit
): String {
    // FIXED: Since renderComposableRootWithHydration is broken, manually create proper HTML
    val bodyContent = renderer.renderComposableRoot {
        SummonPage(title, message, messageType, currentPage, content)
    }
    
    // Create complete HTML document with hydration support
    // Use a simpler approach - create actions based on callback patterns in HTML
    val callbackInfo = extractCallbackInfoFromPatterns(bodyContent)
    val hydrationData = generateHydrationData(callbackInfo)
    
    // Generate theme CSS variables
    val themeCssVariables = generateThemeCssVariables(theme)
    
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title</title>
            
            <!-- Theme CSS variables -->
            <style>
                $themeCssVariables
            </style>
            
            <!-- Application styles -->
            <link rel="stylesheet" href="/styles.css">
        </head>
        <body class="${if (theme == "dark") "dark-theme" else "light-theme"}">
            $bodyContent
            
            <!-- Hydration data for Summon components -->
            <script type="application/json" id="summon-hydration-data">
                $hydrationData
            </script>
            
            <!-- Summon hydration client (Kotlin/JS compiled) -->
            <script src="/summon-hydration.js?v=${System.currentTimeMillis()}"></script>
            
            <!-- Additional JavaScript files -->
            ${additionalScripts.joinToString("\n            ") { script ->
                "<script src=\"$script?v=${System.currentTimeMillis()}\"></script>"
            }}
        </body>
        </html>
    """.trimIndent()
}