package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable


// Typealias for the new composable page factory signature
typealias PageFactory = @Composable (params: RouteParams) -> Unit

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
    fun registerPage(pagePath: String, pageFactory: PageFactory)

    /**
     * Register a special 404 Not Found page.
     *
     * @param pageFactory Function that creates the not found page component
     */
    fun registerNotFoundPage(pageFactory: PageFactory)

    /**
     * Get all registered page routes and their factories.
     *
     * @return Map of page paths to page component factories
     */
    fun getPages(): Map<String, PageFactory>

    /**
     * Get the registered not found page factory if available.
     *
     * @return The not found page factory or null if not registered
     */
    fun getNotFoundPage(): PageFactory?

    /**
     * Normalize a file path to a route path.
     *
     * @param path The file path to normalize
     * @return The normalized route path
     */
    fun normalizePath(path: String): String
}

/**
 * Default implementation of PageRegistry.
 */
class DefaultPageRegistry : PageRegistry {
    private val pages = mutableMapOf<String, PageFactory>()
    private var notFoundPage: PageFactory? = null

    override fun registerPage(pagePath: String, pageFactory: PageFactory) {
        val normalizedPath = normalizePath(pagePath)
        pages[normalizedPath] = pageFactory
    }

    override fun registerNotFoundPage(pageFactory: PageFactory) {
        notFoundPage = pageFactory
    }

    override fun getPages(): Map<String, PageFactory> = pages

    override fun getNotFoundPage(): PageFactory? = notFoundPage

    /**
     * Normalize a path following Next.js conventions.
     */
    override fun normalizePath(path: String): String {
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
    fun register(pagePath: String, pageFactory: PageFactory) {
        registry.registerPage(pagePath, pageFactory)
    }

    /**
     * Register a 404 page.
     */
    fun registerNotFound(pageFactory: PageFactory) {
        registry.registerNotFoundPage(pageFactory)
    }

    /**
     * Get all registered pages.
     */
    fun getRegisteredPages(): Map<String, PageFactory> = registry.getPages()

    /**
     * Get the not found page handler.
     */
    fun getNotFoundHandler(): PageFactory? = registry.getNotFoundPage()

    /**
     * Normalize a path.
     */
    fun normalizePath(path: String): String = registry.normalizePath(path)
} 
