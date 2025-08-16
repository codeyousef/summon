package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.*
import code.yousef.summon.state.*
import code.yousef.summon.renderComposable
import kotlinx.browser.document
import kotlinx.browser.console
import code.yousef.summon.state.SummonMutableState
import org.w3c.dom.HTMLElement

/**
 * Error component using pure Summon - no raw DOM manipulation
 */
@Composable
fun SummonErrorDisplay(errorMessage: String) {
    Column(
        modifier = Modifier()
            .style("padding", "40px")
            .style("text-align", "center")
            .style("font-family", "system-ui, sans-serif")
            .style("background-color", "#f8fafc")
            .style("min-height", "100vh")
            .style("color", "#dc2626")
    ) {
        Text(
            text = "Summon Initialization Error",
            modifier = Modifier()
                .style("font-size", "24px")
                .style("font-weight", "700")
                .style("margin-bottom", "16px")
        )
        
        Text(
            text = errorMessage,
            modifier = Modifier()
                .style("font-size", "16px")
                .style("line-height", "1.6")
                .style("max-width", "600px")
                .style("margin", "0 auto")
        )
    }
}

/**
 * Pure Summon JavaScript example application
 */
@Composable
fun SummonJSApp() {
    val count: SummonMutableState<Int> = remember { code.yousef.summon.state.mutableStateOf(0) }
    val message: SummonMutableState<String> = remember { code.yousef.summon.state.mutableStateOf("") }
    
    Column(
        modifier = Modifier()
            .style("padding", "20px")
            .style("max-width", "800px")
            .style("margin", "0 auto")
            .style("gap", "20px")
            .style("min-height", "100vh")
            .style("background-color", "#f8fafc")
    ) {
        // Header Card
        Card(
            modifier = Modifier()
                .style("background-color", "#4f46e5")
                .style("color", "white")
                .style("text-align", "center")
                .style("padding", "24px")
                .style("border-radius", "12px")
        ) {
            Text(
                text = "🚀 Summon JavaScript Example",
                modifier = Modifier()
                    .style("font-size", "28px")
                    .style("font-weight", "700")
                    .style("margin-bottom", "8px")
            )
            Text(
                text = "Pure Kotlin/JS with Summon Framework - No Raw HTML/CSS/JS!",
                modifier = Modifier()
                    .style("font-size", "16px")
                    .style("opacity", "0.9")
            )
        }

        // Interactive Counter Section
        Card(
            modifier = Modifier()
                .style("padding", "24px")
                .style("border-radius", "12px")
                .style("background-color", "white")
                .style("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", "16px")
            ) {
                Text(
                    text = "Interactive Counter",
                    modifier = Modifier()
                        .style("font-size", "20px")
                        .style("font-weight", "600")
                        .style("margin-bottom", "8px")
                )
                
                Text(
                    text = "Count: ${count.value}",
                    modifier = Modifier()
                        .style("font-size", "24px")
                        .style("font-weight", "700")
                        .style("color", "#059669")
                        .style("text-align", "center")
                        .style("padding", "16px")
                        .style("background-color", "#f0fdf4")
                        .style("border-radius", "8px")
                )
                
                Row(
                    modifier = Modifier()
                        .style("justify-content", "center")
                        .style("gap", "12px")
                ) {
                    Button(
                        onClick = { count.value = count.value + 1 },
                        label = "Increment",
                        variant = ButtonVariant.PRIMARY,
                        modifier = Modifier()
                            .style("padding", "8px 16px")
                            .style("border-radius", "6px")
                    )
                    
                    Button(
                        onClick = { count.value-- },
                        label = "Decrement",
                        variant = ButtonVariant.SECONDARY,
                        modifier = Modifier()
                            .style("padding", "8px 16px")
                            .style("border-radius", "6px")
                    )
                    
                    Button(
                        onClick = { count.value = 0 },
                        label = "Reset",
                        modifier = Modifier()
                            .style("background-color", "#dc2626")
                            .style("color", "white")
                            .style("border", "none")
                            .style("padding", "8px 16px")
                            .style("border-radius", "6px")
                            .style("cursor", "pointer")
                    )
                }
            }
        }

        // Message Input Section
        Card(
            modifier = Modifier()
                .style("padding", "24px")
                .style("border-radius", "12px")
                .style("background-color", "white")
                .style("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", "16px")
            ) {
                Text(
                    text = "Message Input Demo",
                    modifier = Modifier()
                        .style("font-size", "20px")
                        .style("font-weight", "600")
                )
                
                TextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    placeholder = "Enter a message...",
                    modifier = Modifier()
                        .style("width", "100%")
                        .style("padding", "12px")
                        .style("border", "1px solid #d1d5db")
                        .style("border-radius", "6px")
                )
                
                Text(
                    text = if (message.value.isEmpty()) "No message entered" else "You typed: ${message.value}",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("color", "#6b7280")
                        .style("padding", "12px")
                        .style("background-color", "#f9fafb")
                        .style("border-radius", "6px")
                )
            }
        }

        // Features List
        Card(
            modifier = Modifier()
                .style("background-color", "#f8fafc")
                .style("padding", "24px")
                .style("border-radius", "12px")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", "12px")
            ) {
                Text(
                    text = "🎯 Features Demonstrated",
                    modifier = Modifier()
                        .style("font-size", "18px")
                        .style("font-weight", "600")
                        .style("margin-bottom", "8px")
                )
                
                Text(
                    text = "✅ Pure Kotlin/JS with Summon components",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                )
                Text(
                    text = "✅ Reactive state management with mutableStateOf",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                )
                Text(
                    text = "✅ Type-safe styling with modifiers",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                )
                Text(
                    text = "✅ Interactive button onClick handling",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                )
                Text(
                    text = "✅ TextField input handling",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                )
                Text(
                    text = "✅ Cross-platform compatibility",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                )
                Text(
                    text = "✅ No raw HTML/CSS/JavaScript!",
                    modifier = Modifier()
                        .style("font-size", "14px")
                        .style("line-height", "1.5")
                        .style("color", "#059669")
                        .style("font-weight", "600")
                )
            }
        }
    }
}

fun main() {
    console.log("Summon JS Example starting...")
    
    // Wait for DOM to be ready
    if (document.readyState.toString() == "loading") {
        document.addEventListener("DOMContentLoaded", {
            initializeApp()
        })
    } else {
        initializeApp()
    }
}

fun initializeApp() {
    try {
        console.log("Initializing Summon JS application...")
        
        // Get the root element
        val rootElement = document.getElementById("root") as? HTMLElement
            ?: throw IllegalStateException("Root element not found")
        
        // Create platform renderer
        val renderer = PlatformRenderer()
        
        // Render the Summon application using the proper runtime
        renderComposable(renderer, { SummonJSApp() }, rootElement)
        
        console.log("Summon JS application initialized successfully!")
    } catch (e: Exception) {
        console.error("Error during initialization: ${e.message}")
        
        // Show error using pure Summon components - no raw DOM manipulation
        val rootElement = document.getElementById("root") as? HTMLElement
        if (rootElement != null) {
            val renderer = PlatformRenderer()
            renderComposable(renderer, { 
                SummonErrorDisplay("Failed to initialize Summon application: ${e.message}")
            }, rootElement)
        }
    }
}