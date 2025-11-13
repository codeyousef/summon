package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable
import kotlinx.browser.window

/**
 * JavaScript implementation of the FileBasedRouter.
 */
actual class FileBasedRouter actual constructor() : Router {
    private val registry = DefaultPageRegistry()
    private var _currentPath = window.location.pathname
    private var currentRouteParams = RouteParams(emptyMap())

    actual override val currentPath: String
        get() = _currentPath

    init {
        // Load pages automatically when created
        loadPages()

        // Set up browser history listener
        window.onpopstate = { event ->
            val newPath = window.location.pathname
            navigateInternal(newPath, false)
            event
        }
    }

    actual fun loadPages() {
        // Register all pages from the file system
        PageLoader.registerPages(registry)
    }

    actual override fun navigate(path: String, pushState: Boolean) {
        navigateInternal(path, pushState)

        // Update browser URL
        if (pushState) {
            window.history.pushState(null, "", path)
        } else {
            window.history.replaceState(null, "", path)
        }
    }

    private fun navigateInternal(path: String, pushState: Boolean) {
        _currentPath = path

        // Find matching route and extract parameters
        val routeMatch = findMatchingRoute(path)
        currentRouteParams = RouteParams(routeMatch?.params ?: mapOf("path" to path))
    }

    private fun findMatchingRoute(path: String): RouteMatchResult? {
        val pages = registry.getPages()

        // Check each registered route for a match
        return pages.entries.firstOrNull { (routePath, _) ->
            val paramMap = matchRoute(routePath, path)
            paramMap != null
        }?.let { (routePath, pageFactory) ->
            val params = matchRoute(routePath, path) ?: emptyMap()
            RouteMatchResult(RouteDefinition(routePath, pageFactory), params)
        }
    }

    /**
     * Match a route pattern against a path, extracting parameters.
     */
    private fun matchRoute(pattern: String, path: String): Map<String, String>? {
        // Split pattern and path into segments
        val patternSegments = pattern.trim('/').split('/')
        val pathSegments = path.trim('/').split('/')

        // Quick check for catchall routes
        if (patternSegments.lastOrNull() == "*") {
            val paramName = patternSegments.last().removePrefix("*")
            val params = mutableMapOf<String, String>()
            // Capture everything after the prefix as a single parameter
            val prefixSegments = patternSegments.dropLast(1)
            if (pathSegments.size >= prefixSegments.size) {
                val prefixPath = prefixSegments.joinToString("/")
                val paramValue = pathSegments.drop(prefixSegments.size).joinToString("/")
                params[paramName] = paramValue
                return params
            }
            return null
        }

        // If segment counts don't match (and not a catchall), it's not a match
        if (patternSegments.size != pathSegments.size) {
            return null
        }

        val params = mutableMapOf<String, String>()

        // Match each segment
        for (i in patternSegments.indices) {
            val patternSegment = patternSegments[i]
            val pathSegment = pathSegments[i]

            if (patternSegment.startsWith(":")) {
                // Parameter segment
                val paramName = patternSegment.drop(1)
                params[paramName] = pathSegment
            } else if (patternSegment != pathSegment) {
                // Static segment doesn't match
                return null
            }
        }

        return params
    }

    @Composable
    actual override fun create(initialPath: String) {
        // Initialize with correct path if provided
        if (initialPath != _currentPath) {
            navigateInternal(initialPath, false)
        }

        // Find the component to render
        val route = findMatchingRoute(_currentPath)
        val content = route?.route?.content ?: registry.getNotFoundPage() ?: { NotFoundPage() }

        // Update the route params in the RouteParams companion object's storage
        // This makes it accessible via RouteParams.current
        // Use a different approach to provide route params

        // Call the content with the current route parameters
        content(currentRouteParams)
    }

    @Composable
    private fun NotFoundPage() {
        // Simple not found page as a fallback
        val path = currentRouteParams["path"] ?: "Unknown"
        code.yousef.summon.components.display.Text("Page not found: $path")
    }
}

/**
 * Creates a file-based router for JS.
 */
actual fun createFileBasedRouter(): Router {
    return FileBasedRouter()
}

/**
 * Creates a file-based router for the server with a specific path.
 * In JS, this just calls the normal createFileBasedRouter and then navigates.
 */
actual fun createFileBasedServerRouter(path: String): Router {
    val router = FileBasedRouter()
    router.navigate(path, false)
    return router
} 
