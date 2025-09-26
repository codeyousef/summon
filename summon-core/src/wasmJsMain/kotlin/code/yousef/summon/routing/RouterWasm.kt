package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.wasmConsoleLog

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
    actual fun navigate(path: String, pushState: Boolean)

    @Composable
    actual fun create(initialPath: String)

    actual val currentPath: String
}

/**
 * WASM implementation of the FileBasedRouter.
 */
actual class FileBasedRouter actual constructor() : Router {
    private val registry = DefaultPageRegistry()
    private var _currentPath = "/"
    private var currentRouteParams = RouteParams(emptyMap())

    actual override val currentPath: String
        get() = _currentPath

    init {
        // Load pages automatically when created
        loadPages()
    }

    actual fun loadPages() {
        wasmConsoleLog("FileBasedRouter loadPages - WASM stub")
        // TODO: Implement page loading for WASM
        // For now, register a simple default page
        PageLoader.registerPages(registry)
    }

    actual override fun navigate(path: String, pushState: Boolean) {
        wasmConsoleLog("FileBasedRouter navigate to: $path - WASM stub")
        navigateInternal(path)
    }

    private fun navigateInternal(path: String) {
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
        wasmConsoleLog("FileBasedRouter create with path: $initialPath - WASM stub")

        // Initialize with correct path if provided
        if (initialPath != _currentPath) {
            navigateInternal(initialPath)
        }

        // Find the component to render
        val route = findMatchingRoute(_currentPath)
        val content = route?.route?.content ?: registry.getNotFoundPage() ?: { NotFoundPage() }

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
    return WasmDSLRouter(routerBuilder.routes, routerBuilder.notFoundPage)
}

/**
 * Basic WASM Router implementation for DSL-based routing.
 */
internal class WasmDSLRouter(
    private val routes: List<RouteDefinition>,
    private val notFoundPage: @Composable (RouteParams) -> Unit
) : Router {

    private var _currentPath = "/"
    private var currentRouteParams = RouteParams(emptyMap())

    override val currentPath: String
        get() = _currentPath

    override fun navigate(path: String, pushState: Boolean) {
        wasmConsoleLog("WasmDSLRouter navigate to: $path (pushState: $pushState) - WASM stub")
        _currentPath = path

        // Find matching route
        val matchResult = findMatchingRoute(path)
        currentRouteParams = RouteParams(matchResult?.params ?: mapOf("path" to path))
    }

    @Composable
    override fun create(initialPath: String) {
        wasmConsoleLog("WasmDSLRouter create with initialPath: $initialPath - WASM stub")

        // Initialize with the initial path
        if (initialPath != _currentPath) {
            navigate(initialPath, false)
        }

        // Find matching route and render content
        val matchResult = findMatchingRoute(_currentPath)

        if (matchResult != null) {
            // Render the matched route's content
            val (route, params) = matchResult
            route.content(RouteParams(params))
        } else {
            // Render the not found page
            notFoundPage(currentRouteParams)
        }
    }

    private fun findMatchingRoute(path: String): RouteMatchResult? {
        for (route in routes) {
            val params = tryMatchRoute(route.path, path)
            if (params != null) {
                return RouteMatchResult(route, params)
            }
        }
        return null
    }

    private fun tryMatchRoute(pattern: String, path: String): Map<String, String>? {
        // Handle exact matches first
        if (pattern == path) {
            return emptyMap()
        }

        // Handle dynamic parameters
        val patternParts = pattern.split("/").filter { it.isNotEmpty() }
        val pathParts = path.split("/").filter { it.isNotEmpty() }

        if (patternParts.size != pathParts.size) {
            return null
        }

        val params = mutableMapOf<String, String>()

        for (i in patternParts.indices) {
            val patternPart = patternParts[i]
            val pathPart = pathParts[i]

            when {
                patternPart.startsWith(":") -> {
                    val paramName = patternPart.substring(1)
                    params[paramName] = pathPart
                }

                patternPart == "*" -> {
                    // Accept any value
                }

                patternPart != pathPart -> {
                    return null
                }
            }
        }

        return params
    }
}