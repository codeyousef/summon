package codes.yousef.summon

import codes.yousef.summon.js.console
import codes.yousef.summon.routing.Router
import codes.yousef.summon.routing.createRouter
import codes.yousef.summon.runtime.Composable
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Sets up the routing system for the application.
 * This registers all pages and initializes the router.
 */
fun setupRouting() {
    // Create the router using the DSL
    val router = createRouter {
        // Define routes with their composable content
        route("/") { params ->
            // Home page content
            renderHomePage()
        }

        route("/about") { params ->
            // About page content
            renderAboutPage()
        }

        route("/users/profile") { params ->
            // Profile page content with parameters
            renderProfilePage(params.get("userId") ?: "")
        }

        // Set a not found page
        setNotFound { params ->
            renderNotFoundPage(params.get("path") ?: "")
        }
    }

    // Set up the router in the browser
    setupRouterInBrowser(router)
}

/**
 * Renders the home page
 */
@Composable
private fun renderHomePage() {
    // Placeholder for the home page content
}

/**
 * Renders the about page
 */
@Composable
private fun renderAboutPage() {
    // Placeholder for the about page content
}

/**
 * Renders the profile page
 */
@Composable
private fun renderProfilePage(userId: String) {
    // Placeholder for the profile page content
}

/**
 * Renders the not found page
 */
@Composable
private fun renderNotFoundPage(path: String) {
    // Placeholder for the not found page content
}

/**
 * Sets up the router in the browser environment.
 */
private fun setupRouterInBrowser(router: Router) {
    // Get the root element where our app will be rendered
    val root = document.getElementById("app") ?: createRootElement()

    // Render the router to the root element using the new API
    val initialPath = window.location.pathname + window.location.search
    router.create(initialPath)

    // Set up browser history integration
    window.onpopstate = { _ ->
        val path = window.location.pathname + window.location.search
        router.navigate(path, false)
    }

    console.log("Router set up successfully")
}

/**
 * Creates a root element if it doesn't exist.
 */
private fun createRootElement(): HTMLElement {
    val element = document.createElement("div") as HTMLElement
    element.id = "app"
    document.body?.appendChild(element)
    return element
} 
