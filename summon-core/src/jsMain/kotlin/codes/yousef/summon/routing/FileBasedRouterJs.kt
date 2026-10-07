package codes.yousef.summon.routing

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.rememberMutableStateOf
import codes.yousef.summon.state.SummonMutableState
import kotlinx.browser.window

/** Represents file based router. */
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
            (registry.getNotFoundPage() ?: { Text("Page not found") })(RouteParams(emptyMap()))
        } else {
            RouteContentHandler(route)
        }
    }
}

/**
 * Creates file based router.
 *
 * @return The resulting value.
 */
actual fun createFileBasedRouter(): Router = FileBasedRouter()

/**
 * Creates file based server router.
 *
 * @param path Target path.
 * @return The resulting value.
 */
actual fun createFileBasedServerRouter(path: String): Router =
    FileBasedRouter().also { it.navigate(path, false) }
