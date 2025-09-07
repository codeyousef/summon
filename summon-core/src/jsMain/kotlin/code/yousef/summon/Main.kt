package code.yousef.summon

import code.yousef.summon.annotation.AppRegistry
import code.yousef.summon.annotation.Composable
import code.yousef.summon.routing.createRouter
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Entry point for Summon applications in the JS target.
 * This is automatically called when the page loads.
 */
fun main() {
    // Initialize the platform renderer
    val renderer = PlatformRenderer()

    // Wait for the DOM to be ready
    if (document.readyState.asDynamic() == "complete" || document.readyState.asDynamic() == "interactive") {
        renderApp(renderer)
    } else {
        document.addEventListener("DOMContentLoaded", {
            renderApp(renderer)
        })
    }
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
    // Get the registered app entry from the AppRegistry
    return AppRegistry.getAppEntry()
}

/**
 * Renders the application with the given app entry point and router.
 */
private fun renderApp(renderer: PlatformRenderer) {
    // Create router with routes
    val router = createRouter {
        // Register routes as needed for your application
        // Example:
        // route("/") { params ->
        //     MyHomeComponent()
        // }
    }

    // Get the root element or create one if it doesn't exist
    val rootElement = document.getElementById("root") as? HTMLElement
        ?: createRootElement()

    // Clear the root element
    rootElement.innerHTML = ""

    // Find the app entry point
    val appEntry = findAppEntry()

    // Set the renderer as the platform renderer to be used by composables
    setPlatformRenderer(renderer)

    // Provide LocalPlatformRenderer in the composition context
    LocalPlatformRenderer.provides(renderer)

    // Render the app using the extension function that takes container and composable
    renderComposable(renderer, {
        appEntry {
            // Render the active page from the router
            router.create(window.location.pathname + window.location.search)
        }
    }, rootElement)
}
