package com.summon.routing

import com.summon.Composable

/**
 * Registry for pages that can be automatically discovered and loaded.
 *
 * This is a factory interface for creating a mapping between page routes and components.
 */
interface PageRegistry {
    /**
     * Register a page for a specific path.
     *
     * @param pagePath The route path (e.g., "/users/profile")
     * @param pageFactory Function that creates the page component
     */
    fun registerPage(pagePath: String, pageFactory: (RouteParams) -> Composable)

    /**
     * Register a special 404 Not Found page.
     *
     * @param pageFactory Function that creates the not found page component
     */
    fun registerNotFoundPage(pageFactory: (RouteParams) -> Composable)

    /**
     * Get all registered page routes and their factories.
     *
     * @return Map of page paths to page component factories
     */
    fun getPages(): Map<String, (RouteParams) -> Composable>

    /**
     * Get the registered not found page factory if available.
     *
     * @return The not found page factory or null if not registered
     */
    fun getNotFoundPage(): ((RouteParams) -> Composable)?

    /**
     * Create a router from the registered pages.
     *
     * @return A configured Router instance
     */
    fun createRouter(): Router = Router(
        routes = getPages().map { (path, factory) -> Route(path, factory) },
        notFoundComponent = getNotFoundPage()
    )
}

/**
 * Default implementation of PageRegistry.
 */
class DefaultPageRegistry : PageRegistry {
    private val pages = mutableMapOf<String, (RouteParams) -> Composable>()
    private var notFoundPage: ((RouteParams) -> Composable)? = null

    override fun registerPage(pagePath: String, pageFactory: (RouteParams) -> Composable) {
        val normalizedPath = normalizePath(pagePath)
        pages[normalizedPath] = pageFactory
    }

    override fun registerNotFoundPage(pageFactory: (RouteParams) -> Composable) {
        notFoundPage = pageFactory
    }

    override fun getPages(): Map<String, (RouteParams) -> Composable> = pages

    override fun getNotFoundPage(): ((RouteParams) -> Composable)? = notFoundPage

    /**
     * Normalize a path following Next.js conventions.
     */
    private fun normalizePath(path: String): String {
        // Remove file extension if present (.kt, .page.kt, etc.)
        val withoutExtension = path.replace(Regex("\\.\\w+(\\.\\w+)*$"), "")

        // Convert to lowercase path with hyphens
        val kebabCase = withoutExtension
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        // Convert special file names to routes
        val routePath = when {
            kebabCase.endsWith("/index") -> kebabCase.removeSuffix("index")
            kebabCase.endsWith("/home") && !kebabCase.endsWith("/users/home") -> "/"
            else -> kebabCase
        }

        // Convert [param] syntax to :param for route parameters
        val parameterized = routePath
            .replace(Regex("\\[([^]]+)]"), ":$1")
            .replace(Regex("\\[\\.\\.\\.(\\w+)]"), "*")

        // Ensure the path starts with /
        return if (parameterized.startsWith("/")) parameterized else "/$parameterized"
    }
}

/**
 * Global page registry to use throughout the application.
 */
object Pages {
    private val registry = DefaultPageRegistry()

    /**
     * Register a page.
     */
    fun register(pagePath: String, pageFactory: (RouteParams) -> Composable) {
        registry.registerPage(pagePath, pageFactory)
    }

    /**
     * Register a 404 page.
     */
    fun registerNotFound(pageFactory: (RouteParams) -> Composable) {
        registry.registerNotFoundPage(pageFactory)
    }

    /**
     * Create a router from all registered pages.
     */
    fun createRouter(): Router = registry.createRouter()
} 