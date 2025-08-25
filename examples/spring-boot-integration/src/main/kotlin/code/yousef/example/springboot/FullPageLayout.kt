package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Div
import code.yousef.summon.effects.*
import code.yousef.summon.extensions.*
import code.yousef.summon.modifier.LayoutModifierExtras.display
import code.yousef.summon.modifier.LayoutModifierExtras.flexDirection
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.minHeight
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.state.mutableStateOf

/**
 * Full page layout component that generates a complete HTML document
 * without any raw HTML/CSS/JS - pure Summon only.
 */
@Composable
fun FullPageLayout(
    title: String = "Spring Boot + Summon",
    content: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Global styles using Summon's GlobalStyle component
    GlobalStyles()

    // Navigation
    NavigationComponent()

    // Main content
    Div(
        modifier = Modifier()
            .minHeight(100.vh)
            .display("flex")
            .flexDirection("column")
    ) {
        content()
    }

    // Footer
    FooterComponent()
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
    content: @Composable () -> Unit
) {
    // This would be the root of our HTML document
    // In server-side rendering, this generates the complete HTML

    // Flash messages (if any)
    FlashMessage(message, messageType)

    // Page content
    FullPageLayout(title) {
        content()
    }
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
    content: @Composable () -> Unit
): String {
    // FIXED: Since renderComposableRootWithHydration is broken, manually create proper HTML
    val bodyContent = renderer.renderComposableRoot {
        SummonPage(title, message, messageType, content)
    }
    
    // Create complete HTML document with hydration support
    val hydrationData = """{"version":1,"callbacks":[],"timestamp":${System.currentTimeMillis()}}"""
    
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title</title>
        </head>
        <body>
            $bodyContent
            
            <!-- Hydration data for Summon components -->
            <script type="application/json" id="summon-hydration-data">
                $hydrationData
            </script>
            
            <!-- Summon hydration client (Kotlin/JS compiled) -->
            <script src="/summon-hydration.js"></script>
        </body>
        </html>
    """.trimIndent()
}