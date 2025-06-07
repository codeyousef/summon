package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.DisposableEffect
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.rememberMutableStateOf
import kotlinx.browser.window

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
    RouterContext.current = router

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
        // Remember the current route state
        val currentRoute = rememberMutableStateOf(initialPath)

        // Set up effect to listen for browser history changes
        DisposableEffect(Unit) {
            // Create a popstate event listener
            val listener: (dynamic) -> Unit = { _ ->
                val newPath = window.location.pathname + window.location.search
                _currentPath = newPath
                currentRoute.value = newPath
            }

            // Add the event listener
            window.addEventListener("popstate", listener)

            // Cleanup function to remove the listener
            return@DisposableEffect {
                window.removeEventListener("popstate", listener)
            }
        }

        // Update internal state when route changes
        LaunchedEffect(currentRoute.value) {
            _currentPath = currentRoute.value
        }

        // Find matching route for the current path
        val matchResult = findMatchingRoute(currentRoute.value)

        if (matchResult != null) {
            // Render the matched route's content
            val (route, params) = matchResult
            LocalRouteParams.provides(params)
            route.content(params)
        } else {
            // Render the not found page
            val params = RouteParams(mapOf("path" to currentRoute.value))
            LocalRouteParams.provides(params)
            notFoundPage(params)
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
        // Handle exact matches first
        if (pattern == path) {
            return emptyMap()
        }

        // Handle dynamic parameters (e.g., /user/:id)
        val patternParts = pattern.split("/").filter { it.isNotEmpty() }
        val pathParts = path.split("/").filter { it.isNotEmpty() }

        // Different number of segments means no match
        if (patternParts.size != pathParts.size) {
            return null
        }

        val params = mutableMapOf<String, String>()

        for (i in patternParts.indices) {
            val patternPart = patternParts[i]
            val pathPart = pathParts[i]

            when {
                // Dynamic parameter
                patternPart.startsWith(":") -> {
                    val paramName = patternPart.substring(1)
                    params[paramName] = pathPart
                }
                // Wildcard
                patternPart == "*" -> {
                    // Accept any value
                }
                // Exact match required
                patternPart != pathPart -> {
                    return null
                }
            }
        }

        return params
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
