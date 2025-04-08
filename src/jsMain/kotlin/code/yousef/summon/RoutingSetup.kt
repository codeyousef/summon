package code.yousef.summon

import code.yousef.summon.routing.Pages
import code.yousef.summon.routing.Router
import code.yousef.summon.routing.pages.MySimplePage
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Sets up the routing system for the application.
 * This registers all pages and initializes the router.
 */
fun setupRouting() {
    // Register all pages
    Pages.register("/", Index::create)
    Pages.register("/about", About::create)
    Pages.register("/users/profile", Profile::create)
    Pages.register("/my-simple-page", MySimplePage::create)

    // Create the router
    val router = Pages.createRouter()

    // Set up the router in the browser
    setupRouterInBrowser(router)
}

/**
 * Sets up the router in the browser environment.
 */
private fun setupRouterInBrowser(router: Router) {
    // Get the root element where our app will be rendered
    val root = document.getElementById("app") ?: createRootElement()

    // Render the router to the root element
    router.compose(root)

    // Set up browser history integration
    window.onpopstate = { _ ->
        val path = window.location.pathname
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
