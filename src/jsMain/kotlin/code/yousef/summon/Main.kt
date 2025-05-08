package code.yousef.summon

import code.yousef.summon.annotation.App
import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.SummonApp
import code.yousef.summon.init.initSummon
import code.yousef.summon.routing.PageFactory
import code.yousef.summon.routing.Pages
import code.yousef.summon.routing.Router
import code.yousef.summon.routing.RouteParams
import code.yousef.summon.routing.createBrowserRouter
import code.yousef.summon.routing.setupRouterForBrowser
import code.yousef.summon.runtime.JsPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

/**
 * Entry point for Summon applications in the JS target.
 * This is automatically called when the page loads.
 */
fun main() {
    // Wait for the DOM to be loaded
    document.addEventListener("DOMContentLoaded", { _: Event ->
        // Create a router
        val router = createBrowserRouter()

        // Initialize Summon with the router
        initSummon(router) { ctx ->
            // Register pages from the Pages registry
            Pages.getRegisteredPages().forEach { (path, pageFactory) ->
                ctx.router.navigate(path, false)
                // TODO: Implement proper page registration
            }

            // Register the not found handler
            Pages.getNotFoundHandler()?.let { notFoundHandler ->
                // TODO: Implement proper not found handler registration
            }
        }

        // Get the root element or create one if it doesn't exist
        val rootElement = document.getElementById("root") as? HTMLElement
            ?: createRootElement()

        // Clear the root element
        rootElement.innerHTML = ""

        // Find the app entry point
        val appEntry = findAppEntry()

        // Render the app
        renderApp(rootElement, appEntry, router)
    })
}

/**
 * Creates a root element for the application if it doesn't exist.
 */
private fun createRootElement(): HTMLElement {
    val rootElement = document.createElement("div") as HTMLElement
    rootElement.id = "root"
    document.body?.appendChild(rootElement)
    return rootElement
}

/**
 * Finds the app entry point.
 * If a custom @App is defined, it will be used.
 * Otherwise, the default SummonApp will be used.
 */
private fun findAppEntry(): @Composable ((@Composable () -> Unit) -> Unit) {
    // TODO: Implement scanning for @App annotation
    // For now, return the default SummonApp
    return { content -> SummonApp(content) }
}

/**
 * Renders the application with the given app entry point and router.
 */
private fun renderApp(
    rootElement: HTMLElement,
    appEntry: @Composable ((@Composable () -> Unit) -> Unit),
    router: Router
) {
    // Create a platform renderer
    val renderer = JsPlatformRenderer()

    // Set the renderer as the platform renderer to be used by composables
    setPlatformRenderer(renderer)

    // Render the app
    renderer.renderComposable {
        appEntry {
            // Render the active page from the router
            router.create(window.location.pathname + window.location.search)
        }
    }
}
