package codes.yousef.summon.routing

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.Composable

/** Represents file based router. */
actual class FileBasedRouter actual constructor() : Router, NavigationControl {
    private val registry = DefaultPageRegistry()
    private var _currentPath = "/"

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
            return
        }
        _currentPath = safePath.encodedPath
    }

    /** Executes the continue pending operation. */
    override fun continuePending() {
        val safePath = pendingPath?.let(InternalRoutePath::parse) ?: return
        pendingPath = null
        _currentPath = safePath.encodedPath
    }

    /** Cancels pending. */
    override fun cancelPending() {
        pendingPath = null
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
        navigate(initialPath, false)
        val route = findMatchingRoute(_currentPath)
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
