package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.rememberMutableStateOf
import codes.yousef.summon.state.SummonMutableState
import kotlinx.browser.window
/**
 * WASM implementation of the RouterContext object
 */
actual object RouterContext {
    /**
     * The current router instance.
     */
    actual var current: Router? = null
        internal set

    /**
     * Clears the current router instance.
     */
    actual fun clear() {
        current = null
    }

    /**
     * Executes a block with the specified router as the current router.
     *
     * @param router The router to use for the block
     * @param block The block to execute
     * @return The result of the block
     */
    actual fun <T> withRouter(router: Router, block: () -> T): T {
        val previous = current
        current = router
        try {
            return block()
        } finally {
            current = previous
        }
    }
}

/**
 * WASM actual interface for Router.
 */
actual interface Router {
    /**
     * Executes the navigate operation.
     *
     * @param path Target path.
     * @param pushState The push state value.
     */
    actual fun navigate(path: String, pushState: Boolean)

    /**
     * Creates the operation.
     *
     * @param initialPath The initial path value.
     */
    @Composable
    actual fun create(initialPath: String)

    /** The property declaration value. */
    actual val currentPath: String
}

private fun browserBootPath(initialPath: String): String {
    val browserPath = window.location.pathname
    return if (browserPath == "/" || !browserPath.startsWith('/')) initialPath else browserPath
}

/**
 * WASM implementation of the FileBasedRouter.
 */
actual class FileBasedRouter actual constructor() : Router, NavigationControl {
    private val registry = DefaultPageRegistry()
    private var _currentPath = window.location.pathname
    private var renderedRoute: SummonMutableState<String>? = null
    private var pendingPushState = true

    /** The null value. */
    override var interceptor: NavigationInterceptor? = null
    /** The null value. */
    override var pendingPath: String? = null
        private set

    /** The property declaration value. */
    actual override val currentPath: String
        get() = _currentPath

    init {
        loadPages()
    }

    /** Loads pages. */
    actual fun loadPages() {
        PageLoader.registerPages(registry)
    }

    /**
     * Executes the navigate operation.
     *
     * @param path Target path.
     * @param pushState The push state value.
     */
    actual override fun navigate(path: String, pushState: Boolean) {
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

    /** Executes the continue pending operation. */
    override fun continuePending() {
        val safePath = pendingPath?.let(InternalRoutePath::parse) ?: return
        val pushState = pendingPushState
        pendingPath = null
        performNavigation(safePath, pushState)
    }

    /** Cancels pending. */
    override fun cancelPending() {
        pendingPath = null
    }

    private fun performNavigation(path: InternalRoutePath, pushState: Boolean) {
        _currentPath = path.encodedPath
        renderedRoute?.value = path.encodedPath
        if (pushState) {
            window.history.pushState(null, "", path.encodedPath)
        } else {
            window.history.replaceState(null, "", path.encodedPath)
        }
    }

    private fun findMatchingRoute(path: String): RouteMatchResult? {
        val internalPath = InternalRoutePath.parse(path) ?: return null
        for ((routePath, pageFactory) in registry.getPages()) {
            val params = matchInternalRoute(routePath, internalPath)
            if (params != null) return RouteMatchResult(RouteDefinition(routePath, pageFactory), params)
        }
        return null
    }

    /**
     * Creates the operation.
     *
     * @param initialPath The initial path value.
     */
    @Composable
    actual override fun create(initialPath: String) {
        val bootPath = browserBootPath(initialPath)
        val currentRoute = rememberMutableStateOf(bootPath)
        renderedRoute = currentRoute
        DisposableEffect(Unit) {
            val listener: (org.w3c.dom.events.Event) -> Unit = {
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
            window.addEventListener("popstate", listener)
            return@DisposableEffect {
                window.removeEventListener("popstate", listener)
                if (renderedRoute === currentRoute) renderedRoute = null
            }
        }
        LaunchedEffect(currentRoute.value) {
            _currentPath = InternalRoutePath.parse(currentRoute.value)?.encodedPath ?: "/"
        }
        val route = findMatchingRoute(currentRoute.value)
        if (route == null) {
            (registry.getNotFoundPage()
                ?: { codes.yousef.summon.components.display.Text("Page not found") })(RouteParams(emptyMap()))
        } else {
            RouteContentHandler(route)
        }
    }
}

/**
 * Creates a file-based router for WASM.
 */
actual fun createFileBasedRouter(): Router {
    return FileBasedRouter()
}

/**
 * Creates a file-based router for the server with a specific path.
 * In WASM, this just calls the normal createFileBasedRouter and then navigates.
 */
actual fun createFileBasedServerRouter(path: String): Router {
    val router = FileBasedRouter()
    router.navigate(path, false)
    return router
}

/**
 * WASM implementation for creating a router using a DSL.
 */
actual fun createRouter(builder: RouterBuilder.() -> Unit): Router {
    val routerBuilder = RouterBuilderImpl()
    routerBuilder.apply(builder)
    return WasmDSLRouter(routerBuilder.routes, routerBuilder.notFoundPage, routerBuilder.guardFallbackPage)
}

/**
 * Basic WASM Router implementation for DSL-based routing.
 */
internal class WasmDSLRouter(
    private val routes: List<RouteDefinition>,
    private val notFoundPage: @Composable (RouteParams) -> Unit,
    private val guardFallback: @Composable (GuardResult) -> Unit
) : Router, NavigationControl {

    private var _currentPath = window.location.pathname
    private var renderedRoute: SummonMutableState<String>? = null
    override var interceptor: NavigationInterceptor? = null
    override var pendingPath: String? = null
        private set
    private var pendingPushState: Boolean = true

    override val currentPath: String
        get() = _currentPath

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
        if (pushState) {
            window.history.pushState(null, "", path.encodedPath)
        } else {
            window.history.replaceState(null, "", path.encodedPath)
        }
    }

    @Composable
    override fun create(initialPath: String) {
        val bootPath = browserBootPath(initialPath)
        val currentRoute = rememberMutableStateOf(bootPath)
        renderedRoute = currentRoute

        DisposableEffect(Unit) {
            val listener: (org.w3c.dom.events.Event) -> Unit = {
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
            window.addEventListener("popstate", listener)
            return@DisposableEffect {
                window.removeEventListener("popstate", listener)
                if (renderedRoute === currentRoute) renderedRoute = null
            }
        }

        LaunchedEffect(currentRoute.value) {
            _currentPath = InternalRoutePath.parse(currentRoute.value)?.encodedPath ?: "/"
        }
        val matchResult = findMatchingRoute(currentRoute.value)
        if (matchResult == null) {
            notFoundPage(RouteParams(emptyMap()))
            return
        }
        val params = RouteParams(matchResult.params)
        when (val guardResult = matchResult.route.evaluateGuards(params)) {
            GuardResult.Allow -> RouteContentHandler(matchResult)
            is GuardResult.Redirect -> {
                val redirect = InternalRoutePath.parse(guardResult.path)
                if (redirect == null) guardFallback(GuardResult.Deny) else {
                    navigate(redirect.encodedPath, false)
                    guardFallback(GuardResult.Loading)
                }
            }
            else -> guardFallback(guardResult)
        }
    }

    private fun findMatchingRoute(path: String): RouteMatchResult? {
        val internalPath = InternalRoutePath.parse(path) ?: return null
        for (route in routes) {
            val params = matchInternalRoute(route.path, internalPath)
            if (params != null) return RouteMatchResult(route, params)
        }
        return null
    }

}