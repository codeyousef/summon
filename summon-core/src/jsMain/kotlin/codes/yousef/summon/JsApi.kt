package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.hydration.GlobalEventListener
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.SummonConstants
import codes.yousef.summon.runtime.setPlatformRenderer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Renders a composable function to the DOM element with the specified ID.
 *
 * If the element with [rootElementId] is not found, this function will try
 * well-known fallbacks (`SummonConstants.DEFAULT_ROOT_ELEMENT_ID`, then
 * `[data-summon-hydration="root"]`) before throwing. This resilient lookup
 * prevents a hard crash when the CLI-generated client code and the server
 * renderer use different IDs (see GitHub issue #35).
 */
fun renderComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
    val rootElement = findRootElement(rootElementId)
        
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    LocalPlatformRenderer.provides(renderer)
    
    // Initialize global event listener for data-action handling
    GlobalEventListener.init()
    
    // Use the proper renderComposable function that sets up recomposition
    renderComposable(renderer, composable, rootElement)
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

/**
 * Attempts to locate the root DOM element by [requestedId], falling back to
 * well-known Summon IDs and data attributes when the exact ID is not present.
 */
private fun findRootElement(requestedId: String): HTMLElement {
    // 1. Try the exact ID the caller asked for
    (document.getElementById(requestedId) as? HTMLElement)?.let { return it }

    // 2. Fallback: the canonical Summon root ID
    if (requestedId != SummonConstants.DEFAULT_ROOT_ELEMENT_ID) {
        (document.getElementById(SummonConstants.DEFAULT_ROOT_ELEMENT_ID) as? HTMLElement)?.let {
            window.asDynamic().console.warn(
                "[Summon] Root element with ID '$requestedId' not found. " +
                        "Falling back to '${SummonConstants.DEFAULT_ROOT_ELEMENT_ID}'. " +
                        "Update your code to use renderComposableRoot(\"${SummonConstants.DEFAULT_ROOT_ELEMENT_ID}\")."
            )
            return it
        }
    }

    // 3. Fallback: data-attribute used by the SSR renderer
    (document.querySelector("[data-summon-hydration='root']") as? HTMLElement)?.let {
        window.asDynamic().console.warn(
            "[Summon] Root element with ID '$requestedId' not found. " +
                    "Falling back to element with [data-summon-hydration='root']. " +
                    "Update your code to use renderComposableRoot(\"${SummonConstants.DEFAULT_ROOT_ELEMENT_ID}\")."
        )
        return it
    }

    throw Exception(
        "Root element with ID '$requestedId' not found. " +
                "Ensure your HTML contains <div id=\"${SummonConstants.DEFAULT_ROOT_ELEMENT_ID}\"></div>."
    )
}
