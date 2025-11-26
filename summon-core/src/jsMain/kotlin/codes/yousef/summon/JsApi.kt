package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.hydration.GlobalEventListener
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Renders a composable function to the DOM element with the specified ID.
 */
fun renderComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
    val rootElement = document.getElementById(rootElementId) as? HTMLElement
        ?: throw Exception("Root element with ID $rootElementId not found")
        
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    LocalPlatformRenderer.provides(renderer)
    
    // Initialize global event listener for data-action handling
    GlobalEventListener.init()
    
    // Use the extension function to render
    renderer.renderComposable(composable, rootElement)
}

/**
 * Hydrates a composable function to the DOM element with the specified ID.
 * This also initializes the GlobalEventListener for handling data-action
 * events like HamburgerMenu toggle.
 */
fun hydrateComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    LocalPlatformRenderer.provides(renderer)
    
    // Initialize global event listener for data-action handling
    // This is crucial for HamburgerMenu and other components that use
    // client-side only actions via data-action attribute
    GlobalEventListener.init()
    
    renderer.hydrateComposableRoot(rootElementId, composable)
}
