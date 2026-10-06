package codes.yousef.summon.routing

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.Composable

actual class FileBasedRouter actual constructor() : Router, NavigationControl {
    private val registry = DefaultPageRegistry()
    private var _currentPath = "/"

    override var interceptor: NavigationInterceptor? = null
    override var pendingPath: String? = null
        private set

    actual override val currentPath: String
        get() = _currentPath

    init {
        loadPages()
    }

    actual fun loadPages() {
        PageLoader.registerPages(registry)
    }

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

    override fun continuePending() {
        val safePath = pendingPath?.let(InternalRoutePath::parse) ?: return
        pendingPath = null
        _currentPath = safePath.encodedPath
    }

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

actual fun createFileBasedRouter(): Router = FileBasedRouter()

actual fun createFileBasedServerRouter(path: String): Router =
    FileBasedRouter().also { it.navigate(path, false) }
