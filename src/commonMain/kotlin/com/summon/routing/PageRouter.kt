package com.summon.routing

import com.summon.Composable

/**
 * A Next.js style router that automatically generates routes from page files in a pages directory.
 *
 * Routes are created based on the file path structure:
 * - `/pages/Home.kt` → `/` (index route)
 * - `/pages/About.kt` → `/about`
 * - `/pages/users/Profile.kt` → `/users/profile`
 * - `/pages/blog/[id].kt` → `/blog/:id` (dynamic parameter)
 * - `/pages/blog/[...slug].kt` → `/blog/*` (catch-all route)
*/
 **/
class PageRouter(
    private val pages: Map<String, (RouteParams) -> Composable>,
    private val notFoundPage: ((RouteParams) -> Composable)? = null
) : Composable {
    // The actual router that will handle navigation
    private val router = Router(
        routes = pages.map { (path, page) -> Route(path, page) },
        notFoundComponent = notFoundPage
    )

    /**
     * Navigate to a specific page path.
     */
    fun navigate(path: String, pushState: Boolean = true) {
        router.navigate(path, pushState)
    }

    /**
     * Get the current path.
     */
    fun getCurrentPath(): String = router.getCurrentPath()

    /**
     * Get the current route parameters.
     */
    fun getCurrentParams(): RouteParams = router.getCurrentParams()

    /**
     * Check if a path exists in the router.
     */
    fun hasPath(path: String): Boolean = router.hasMatchingRoute(path)

    /**
     * Render the current page based on the current path.
     */
    override fun <T> compose(receiver: T): T {
        return router.compose(receiver)
    }

    companion object {
        /**
         * Creates a PageRouter from a map of page paths to page factories.
         */
        fun create(
            pages: Map<String, (RouteParams) -> Composable>,
            notFoundPage: ((RouteParams) -> Composable)? = null
        ): PageRouter {
            return PageRouter(pages, notFoundPage)
        }
    }
}

/**
 * A builder for PageRouter that allows adding pages in a DSL-like manner.
 */
class PageRouterBuilder {
    private val pages = mutableMapOf<String, (RouteParams) -> Composable>()
    var notFoundPage: ((RouteParams) -> Composable)? = null
        private set

    /**
     * Add a page to the router.
     */
    fun page(path: String, pageFactory: (RouteParams) -> Composable) {
        pages[normalizePath(path)] = pageFactory
    }

    /**
     * Set the 404 page component.
     */
    fun notFound(pageFactory: (RouteParams) -> Composable) {
        notFoundPage = pageFactory
    }

    /**
     * Build the PageRouter with the configured pages.
     */
    fun build(): PageRouter {
        return PageRouter(pages, notFoundPage)
    }

    /**
     * Normalize a path to ensure consistent formatting.
     */
    private fun normalizePath(path: String): String {
        // Replace square brackets with route parameters
        val parameterizedPath = path
            .replace(Regex("\\[([^]]+)]"), ":$1")  // [id] -> :id
            .replace(Regex("\\[\\.\\.\\.(\\w+)]"), "*")  // [...slug] -> *

        // Ensure path starts with /
        val normalizedPath = if (parameterizedPath.startsWith("/")) {
            parameterizedPath
        } else {
            "/$parameterizedPath"
        }

        // Handle index pages
        return if (normalizedPath.endsWith("/index")) {
            normalizedPath.removeSuffix("index")
        } else {
            normalizedPath
        }
    }
}

/**
 * Create a PageRouter with a DSL-like builder.
 */
fun createPageRouter(init: PageRouterBuilder.() -> Unit): PageRouter {
    val builder = PageRouterBuilder()
    builder.init()
    return builder.build()
} 