package code.yousef.summon.examples.js

import code.yousef.summon.renderComposable
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

// Access console through js
private val console: dynamic = js("console")

fun main() {
    console.log("Starting Summon Todo App...")
    console.log("To clear authentication and start fresh, run: localStorage.clear()")
    
    // Ensure English is default if no language is set
    if (kotlinx.browser.localStorage.getItem("language") == null) {
        kotlinx.browser.localStorage.setItem("language", "en")
    }
    
    // Create a PlatformRenderer instance
    val renderer = PlatformRenderer()
    
    try {
        // Get the root element
        val rootElement = document.getElementById("root") as? HTMLElement
        
        if (rootElement != null) {
            console.log("Root element found, rendering TodoApp...")
            
            // Render the TodoApp with the renderer
            renderComposable(renderer, {
                TodoApp()
            }, rootElement)
            
            console.log("TodoApp rendered successfully!")
        } else {
            console.error("Root element not found!")
            
            // Create root element if it doesn't exist
            val root = document.createElement("div") as HTMLElement
            root.id = "root"
            document.body?.appendChild(root)
            
            renderComposable(renderer, {
                SummonErrorDisplay("Root element was missing but has been created. Please refresh the page.")
            }, root)
        }
    } catch (e: Exception) {
        console.error("Error starting TodoApp:", e.message)
        
        try {
            val body = document.body
            if (body != null) {
                renderComposable(renderer, {
                    SummonErrorDisplay("Failed to initialize TodoApp: ${e.message}")
                }, body)
            } else {
                console.error("Document body is null!")
            }
        } catch (renderError: Exception) {
            console.error("Failed to render error message:", renderError.message)
        }
    }
}
