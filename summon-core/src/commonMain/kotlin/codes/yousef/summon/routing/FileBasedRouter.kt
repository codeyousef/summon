package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable

/**
 * FileBasedRouter implements the Router interface but uses a Next.js style file-based
 * approach for routing rather than programmatic route registration.
 */
expect class FileBasedRouter() : Router {
    /**
     * Navigates to the specified path.
     */
    override fun navigate(path: String, pushState: Boolean)

    /**
     * Composes the UI for the router at the given initial path.
     */
    @Composable
    override fun create(initialPath: String)

    /**
     * The current path of the router.
     */
    override val currentPath: String

    /**
     * Loads pages from the pages directory structure.
     * This is automatically called during initialization.
     */
    fun loadPages()
}

/**
 * Creates a file-based router that uses the pages directory structure for routing.
 * This follows Next.js conventions where the file structure maps to URL routes.
 *
 * @return A Router instance that uses file-based routing
 */
expect fun createFileBasedRouter(): Router

/**
 * Creates a server-side file-based router for the given path.
 *
 * @param path The current request path
 * @return A Router instance that can render the page for the given path
 */
expect fun createFileBasedServerRouter(path: String): Router 