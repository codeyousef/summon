package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import kotlinx.browser.window
import routing.RouterContext

/**
 * JavaScript actual implementation of the Router interface
 */
actual interface Router {
    /**
     * Navigates to the specified path.
     */
    actual fun navigate(path: String, pushState: Boolean)

    /**
     * Composes the UI for the router at the given initial path.
     */
    @Composable
    actual fun create(initialPath: String)

    /**
     * The current path of the router.
     */
    actual val currentPath: String
}

/**
 * Extends the Router class with JavaScript-specific functionality.
 */

/**
 * External JS function interface
 */
@JsName("js")
external fun js(code: String): dynamic

/**
 * Global router instance that is accessible from JavaScript.
 */
private var globalRouter: Router? = null

/**
 * Sets up the router for use with the browser.
 * Exposes navigation functions to JavaScript.
 */
@JsName("setupRouterForBrowser")
fun setupRouterForBrowser(router: Router) {
    // Store in global variable
    globalRouter = router

    // Set as current router in context
    RouterContext.setCurrent(router)

    // Expose the navigation function to JavaScript
    js("window.summonRouterNavigate = function(path, pushState) { summonRouterNavigate(path, pushState !== false); }")
}

/**
 * Navigate to a path using the global router.
 * This function is called from JavaScript.
 */
@JsName("summonRouterNavigate")
fun summonRouterNavigate(path: String, pushState: Boolean = true) {
    globalRouter?.navigate(path, pushState)
}

/**
 * Creates a router with browser navigation support.
 */
@JsName("createBrowserRouter")
fun createBrowserRouter(
    vararg routes: String,
    notFoundComponent: (@Composable (RouteParams) -> Unit)? = null
): Router {
    val routerBuilder = RouterBuilderImpl()
    
    // Add routes
    routes.forEach { path ->
        routerBuilder.route(path) { params ->
            // Default empty content, will be replaced when routes are registered
        }
    }
    
    // Set not found page if provided
    notFoundComponent?.let { routerBuilder.setNotFound(it) }
    
    // Create router
    val router = RouterJs(routerBuilder.routes, routerBuilder.notFoundPage)
    setupRouterForBrowser(router)
    return router
}

/**
 * Creates a router with browser navigation support using a DSL.
 */
fun createBrowserRouter(init: RouterBuilder.() -> Unit): Router {
    val routerBuilder = RouterBuilderImpl()
    routerBuilder.apply(init)
    
    val router = RouterJs(routerBuilder.routes, routerBuilder.notFoundPage)
    setupRouterForBrowser(router)
    return router
}

/**
 * Updates the browser history with the new path.
 * This is called automatically by the Router.navigate method.
 */
fun Router.updateBrowserUrl(path: String, pushState: Boolean) {
    if (pushState) {
        window.history.pushState(null, "", path)
    } else {
        window.history.replaceState(null, "", path)
    }
}

/**
 * Extension function for Router.navigate that also updates the browser URL.
 */
fun Router.navigateAndUpdateBrowser(path: String, pushState: Boolean = true) {
    navigate(path, pushState)
    updateBrowserUrl(path, pushState)
}

/**
 * Browser History implementation
 */
class BrowserHistory {
    fun push(path: String) {
        window.history.pushState(null, "", path)
    }
    
    fun replace(path: String) {
        window.history.replaceState(null, "", path)
    }
    
    fun getCurrentPath(): String {
        return window.location.pathname + window.location.search
    }
}

/**
 * Implementation of the Router interface for JavaScript.
 */
internal class RouterJs(
    private val routes: List<RouteDefinition>,
    private val notFoundPage: @Composable (RouteParams) -> Unit
) : Router {

    private val history = BrowserHistory()
    private var _currentPath = window.location.pathname + window.location.search
    
    // Implement the currentPath property from the Router interface
    override val currentPath: String
        get() = _currentPath

    @Composable
    override fun create(initialPath: String) {
        // Update the current path
        _currentPath = initialPath
        
        // Set up effect to listen for browser history changes
        // In a real implementation, this would be a LaunchedEffect
        
        // Find matching route for the initial path
        val matchResult = findMatchingRoute(initialPath)
        
        if (matchResult != null) {
            // Render the matched route's content
            val (route, params) = matchResult
            route.content(params)
        } else {
            // Render the not found page
            notFoundPage(RouteParams(mapOf("path" to initialPath)))
        }
    }
    
    override fun navigate(path: String, pushState: Boolean) {
        // Update the current path
        _currentPath = path
        
        // Handle browser history if needed
        if (pushState) {
            history.push(path)
        } else {
            history.replace(path)
        }
    }
    
    /**
     * Find the matching route for a given path.
     */
    private fun findMatchingRoute(path: String): Pair<RouteDefinition, RouteParams>? {
        for (route in routes) {
            val params = tryMatchRoute(route.path, path)
            if (params != null) {
                return Pair(route, RouteParams(params))
            }
        }
        return null
    }
    
    /**
     * Try to match a route path pattern against an actual path.
     * Returns a map of parameters if match is successful, null otherwise.
     */
    private fun tryMatchRoute(pattern: String, path: String): Map<String, String>? {
        // Simple implementation for now
        // Would need to handle path parameters like "/users/:id" etc.
        if (pattern == path) {
            return emptyMap()
        }
        return null
    }
}

/**
 * Function for creating a router instance in JS.
 */
actual fun createRouter(builder: RouterBuilder.() -> Unit): Router {
    val routerBuilder = RouterBuilderImpl()
    routerBuilder.apply(builder)
    return RouterJs(routerBuilder.routes, routerBuilder.notFoundPage)
}
