package code.yousef.summon.examples.js

import code.yousef.summon.renderComposable
import kotlinx.browser.document
import kotlinx.browser.console

fun main() {
    console.log("Starting Summon Todo App...")
    
    try {
        // Get the root element
        val rootElement = document.getElementById("root")
        
        if (rootElement != null) {
            console.log("Root element found, rendering TodoApp...")
            
            // Render the TodoApp
            renderComposable(rootElement) {
                TodoApp()
            }
            
            console.log("TodoApp rendered successfully!")
        } else {
            console.error("Root element not found!")
            
            // Create root element if it doesn't exist
            val root = document.createElement("div")
            root.id = "root"
            document.body?.appendChild(root)
            
            renderComposable(root) {
                SummonErrorDisplay("Root element was missing but has been created. Please refresh the page.")
            }
        }
    } catch (e: Exception) {
        console.error("Error starting TodoApp:", e.message)
        
        try {
            val body = document.body
            if (body != null) {
                renderComposable(body) {
                    SummonErrorDisplay("Failed to initialize TodoApp: ${e.message}")
                }
            } else {
                console.error("Document body is null!")
            }
        } catch (renderError: Exception) {
            console.error("Failed to render error message:", renderError.message)
        }
    }
}