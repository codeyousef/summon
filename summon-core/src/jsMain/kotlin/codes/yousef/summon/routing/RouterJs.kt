package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.rememberMutableStateOf
import codes.yousef.summon.state.SummonMutableState
import kotlinx.browser.window
import kotlin.js.js

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
/**
 * Executes the JS operation.
 *
 * @param code The code value.
 */
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
/**
 * Sets up router for browser.
 *
 * @param router The router value.
 */
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
/**
 * Executes the summon router navigate operation.
 *
 * @param path Target path.
 * @param pushState The push state value.
 */
fun summonRouterNavigate(path: String, pushState: Boolean = true) {
    globalRouter?.navigate(path, pushState)
}

/**
 * Creates a router with browser navigation support.
 */
@JsName("createBrowserRouter")
/**
 * Creates browser router.
 *
 * @param routes The routes value.
 * @param notFoundComponent The not found component value.
 * @return The resulting value.
 */
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
    val router = RouterJs(routerBuilder.routes, routerBuilder.notFoundPage, routerBuilder.guardFallbackPage)
    setupRouterForBrowser(router)
    return router
}

/**
 * Creates a router with browser navigation support using a DSL.
 */
fun createBrowserRouter(init: RouterBuilder.() -> Unit): Router {
    val routerBuilder = RouterBuilderImpl()
    routerBuilder.apply(init)

    val router = RouterJs(routerBuilder.routes, routerBuilder.notFoundPage, routerBuilder.guardFallbackPage)
    setupRouterForBrowser(router)
    return router
}

/**
 * Updates the browser history with the new path.
 * This is called automatically by the Router.navigate method.
 */
fun Router.updateBrowserUrl(path: String, pushState: Boolean) {
    val safePath = requireNotNull(InternalRoutePath.parse(path)) {
        "Browser history accepts same-origin paths without query strings or fragments"
    }
    if (pushState) {
        window.history.pushState(null, "", safePath.encodedPath)
    } else {
        window.history.replaceState(null, "", safePath.encodedPath)
    }
}

/**
 * Extension function for Router.navigate that also updates the browser URL.
 */
fun Router.navigateAndUpdateBrowser(path: String, pushState: Boolean = true) {
    navigate(path, pushState)
}

internal fun browserBootPath(initialPath: String): String {
    val browserPath = window.location.pathname
    return if (browserPath == "/" || !browserPath.startsWith('/')) initialPath else browserPath
}

/**
 * Browser History implementation
 */
class BrowserHistory {
    /**
     * Executes the push operation.
     *
     * @param path Target path.
     */
    fun push(path: InternalRoutePath) {
        window.history.pushState(null, "", path.encodedPath)
    }

    /**
     * Executes the replace operation.
     *
     * @param path Target path.
     */
    fun replace(path: InternalRoutePath) {
        window.history.replaceState(null, "", path.encodedPath)
    }

    /**
     * Returns current path.
     *
     * @return The resulting value.
     */
    fun getCurrentPath(): String = window.location.pathname
}

/**
 * Implementation of the Router interface for JavaScript.
 */
internal class RouterJs(
    private val routes: List<RouteDefinition>,
    private val notFoundPage: @Composable (RouteParams) -> Unit,
    private val guardFallback: @Composable (GuardResult) -> Unit
) : Router, NavigationControl {

    private val history = BrowserHistory()
    private var _currentPath = window.location.pathname
    private var renderedRoute: SummonMutableState<String>? = null
    override var interceptor: NavigationInterceptor? = null
    override var pendingPath: String? = null
        private set
    private var pendingPushState: Boolean = true

    // Implement the currentPath property from the Router interface
    override val currentPath: String
        get() = _currentPath

    @Composable
    override fun create(initialPath: String) {
        val bootPath = browserBootPath(initialPath)
        val currentRoute = rememberMutableStateOf(bootPath)
        renderedRoute = currentRoute

        // Set up effect to listen for browser history changes
        DisposableEffect(Unit) {
            // Create a popstate event listener
            val listener: (dynamic) -> Unit = { _ ->
                val target = InternalRoutePath.parse(window.location.pathname)
                if (target == null) {
                    _currentPath = "/"
                    currentRoute.value = ""
                } else if (interceptor?.beforeNavigate(_currentPath, target.encodedPath) == NavigationDecision.CANCEL) {
                    pendingPath = target.encodedPath
                    pendingPushState = false
                    window.history.pushState(null, "", _currentPath)
                } else {
                    _currentPath = target.encodedPath
                    currentRoute.value = target.encodedPath
                }
            }

            // Add the event listener
            window.addEventListener("popstate", listener)

            // Cleanup function to remove the listener
            return@DisposableEffect {
                window.removeEventListener("popstate", listener)
                if (renderedRoute === currentRoute) {
                    renderedRoute = null
                }
            }
        }

        LaunchedEffect(currentRoute.value) {
            _currentPath = InternalRoutePath.parse(currentRoute.value)?.encodedPath ?: "/"
        }

        val matchResult = findMatchingRoute(currentRoute.value)
        if (matchResult != null) {
            val (route, params) = matchResult
            when (val guardResult = route.evaluateGuards(RouteParams(params))) {
                GuardResult.Allow -> RouteContentHandler(matchResult)
                is GuardResult.Redirect -> {
                    val redirect = InternalRoutePath.parse(guardResult.path)
                    if (redirect == null) {
                        guardFallback(GuardResult.Deny)
                    } else {
                        navigate(redirect.encodedPath, false)
                        guardFallback(GuardResult.Loading)
                    }
                }
                else -> guardFallback(guardResult)
            }
        } else {
            notFoundPage(RouteParams(emptyMap()))
        }
    }

    override fun navigate(path: String, pushState: Boolean) {
        val safePath = requireNotNull(InternalRoutePath.parse(path)) {
            "Router navigation accepts same-origin paths without query strings or fragments"
        }
        if (interceptor?.beforeNavigate(_currentPath, safePath.encodedPath) == NavigationDecision.CANCEL) {
            pendingPath = safePath.encodedPath
            pendingPushState = pushState
            return
        }
        performNavigation(safePath, pushState)
    }

    override fun continuePending() {
        val safePath = pendingPath?.let(InternalRoutePath::parse) ?: return
        val pushState = pendingPushState
        pendingPath = null
        performNavigation(safePath, pushState)
    }

    override fun cancelPending() {
        pendingPath = null
    }

    private fun performNavigation(path: InternalRoutePath, pushState: Boolean) {
        _currentPath = path.encodedPath
        renderedRoute?.value = path.encodedPath
        if (pushState) history.push(path) else history.replace(path)
    }

    /**
     * Find the matching route for a given path.
     */
    private fun findMatchingRoute(path: String): RouteMatchResult? {
        val internalPath = InternalRoutePath.parse(path) ?: return null
        for (route in routes) {
            val params = matchInternalRoute(route.path, internalPath)
            if (params != null) return RouteMatchResult(route, params)
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
    return RouterJs(routerBuilder.routes, routerBuilder.notFoundPage, routerBuilder.guardFallbackPage)
}
