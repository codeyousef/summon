package code.yousef.summon

import code.yousef.summon.components.display.Text
import code.yousef.summon.routing.Pages
import code.yousef.summon.routing.RouteParams
import code.yousef.summon.routing.RouteDefinition
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.NodeList
import org.w3c.dom.events.Event

// External JS interfaces
external class Document {
    val body: HTMLElement?
    fun getElementById(id: String): HTMLElement?
    fun createElement(tagName: String): HTMLElement
    fun querySelectorAll(selectors: String): NodeList
}

external class HTMLElement {
    var id: String
    var className: String
    var innerHTML: String
    var textContent: String?
    var style: CSSStyleDeclaration
    fun appendChild(node: HTMLElement): HTMLElement
    fun addEventListener(type: String, listener: (Event) -> Unit)
}

external class CSSStyleDeclaration {
    var display: String
    var marginBottom: String
    var borderBottom: String
    var padding: String
    var cursor: String
    var color: String
    var fontWeight: String
    var margin: String
    var maxWidth: String
}

external class NodeList {
    val length: Int
    fun item(index: Int): HTMLElement?
}

external class Event {
    val currentTarget: HTMLElement?
    fun preventDefault()
}

external class Console {
    fun log(message: String)
}

external class Window {
    val document: Document
    val console: Console
}

// Global JS objects
external val window: Window
external val document: Document

/**
 * JS example demonstrating the Summon library.
 */
fun main() {
    // Set up the app when the DOM is loaded
    document.addEventListener("DOMContentLoaded") { setupApp() }
}

/**
 * Provides event listener functionality for Document
 */
private fun Document.addEventListener(type: String, listener: (Event) -> Unit) {
    // This is just a stub for the external function
}

/**
 * Registers the application pages with the global Pages registry.
 * Imports now point to commonMain example composables.
 */
private fun registerAppPages() {
    // Register the example pages from commonMain
    // Pages.register("/basic-ui") { BasicUIDemo() }
    // Pages.register("/form") { FormExample() }
    // Pages.register("/text") { TextExample() }
    // Pages.register("/image") { ImageExample() }
    // Pages.register("/divider") { DividerExample() }
    // Pages.register("/simple") { MySimplePage() }
    // Pages.register("/") { BasicUIDemo() }
    
    // Register a placeholder page for the root
    Pages.register("/") { Text("Welcome to Summon! (Root Page)") }

    // Register a simple Not Found page
    Pages.registerNotFound { params ->
        Text("Page Not Found: ${params.asMap()["path"] ?: "Unknown"}")
    }
}

/**
 * Sets up the application once the DOM is loaded.
 */
private fun setupApp() {
    // Register pages first
    registerAppPages()

    // Get the app container element or create one if it doesn't exist
    val appContainer = document.getElementById("app") as? HTMLElement ?: createAppContainer()

    // Convert PageFactory map to List<RouteDefinition>
    val routes = Pages.getRegisteredPages().map { (path, pageFactory) ->
        RouteDefinition(path) { params -> // Adapt the signature
            pageFactory(params) // Use params directly since it's already a RouteParams object
        }
    }
    val notFoundHandler = Pages.getNotFoundHandler()

    // Placeholder: Get initial path from browser URL (requires more setup)
    val initialPath = window.location.pathname

    // Clear container before mounting composable
    appContainer.innerHTML = ""

    // TODO: Define and call renderComposable for JS
    // renderComposable(appContainer) { // Hypothetical JS render function
    //     RouterComponent(
    //         initialPath = initialPath, 
    //         routes = routes,
    //         notFound = notFoundHandler // RouterComponent needs adaptation for notFound
    //     )
    // }
    
    // Temporary placeholder content
    appContainer.innerHTML = "<h1>Summon App</h1><p>Router setup needed (path: $initialPath)...</p>"
}

/**
 * Creates a container div for the application if it doesn't exist.
 * @return The created container element
 */
private fun createAppContainer(): HTMLElement {
    val container = document.createElement("div") as HTMLElement
    container.id = "app"
    container.style.maxWidth = "1200px"
    container.style.margin = "0 auto"
    container.style.padding = "20px"
    document.body?.appendChild(container)
    return container
} 
