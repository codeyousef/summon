package code.yousef.summon.examples

import code.yousef.summon.routing.RouterContext
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.HTMLElement
import kotlin.js.JsName

/**
 * Entry point for the router example.
 * In a real application, this would initialize the router and
 * attach it to the DOM.
 */
@JsName("main")
fun main() {
    // In a real application with the proper browser dependencies, 
    // the code would look something like this:
    //
    // window.onload = {
    //     val rootElement = document.getElementById("root")
    //     if (rootElement != null) {
    //         val router = RouterExample()
    //         router.compose(rootElement)
    //         
    //         // Set up router with browser history
    //         RouterJs.setupForBrowser(router)
    //     }
    // }
    
    // For now, we just print a message
    println("Router example is ready to be initialized")
}

/**
 * This function shows how to set up a router in a real application.
 * It's included here as a reference but is not actually called.
 */
private fun exampleSetup() {
    // This is just a reference implementation
    // It won't be executed in this simplified version
    
    // 1. Create the router example
    val routerExample = RouterExample()
    
    // 2. Get a reference to the root element
    // val rootElement = document.getElementById("root")
    
    // 3. Render the router to the root element
    // rootElement?.let { routerExample.compose(it) }
}

/**
 * Initializes the router and attaches it to the DOM.
 */
private fun initializeRouter() {
    // Get the root element where our app will be rendered
    val rootElement = document.getElementById("root")
    
    if (rootElement == null) {
        console.log("Root element not found! Make sure there's a div with id='root' in your HTML.")
        return
    }
    
    try {
        // Create router example
        val routerExample = RouterExample()
        
        // Render the router example to the root element
        routerExample.compose(rootElement)
        
        // Set up global click handler for navigation
        document.addEventListener("click", { event: Event ->
            // Handle link clicks for navigation
            val target = event.target
            if (target is HTMLElement && target.tagName.lowercase() == "a") {
                // Check if this is an internal link (not external)
                val href = target.getAttribute("href")
                if (href != null && !href.startsWith("http") && !href.startsWith("//")) {
                    // Get the current router from context
                    val router = RouterContext.current
                    if (router != null) {
                        // Prevent default link behavior
                        event.preventDefault()
                        
                        // Navigate using the router
                        router.navigate(href)
                    }
                }
            }
        })
        
        // Handle browser back/forward buttons
        window.onpopstate = { _ ->
            val path = window.location.pathname + window.location.search
            RouterContext.current?.navigate(path, false)
        }
        
        console.log("Router example successfully initialized!")
    } catch (e: Exception) {
        console.log("Error initializing router: ${e.message}")
    }
} 