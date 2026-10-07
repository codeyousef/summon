package codes.yousef.summon.routing

/**
 * Loads pages from the file system and registers them with the PageRegistry.
 *
 * This implementation uses code generation at build time
 * to scan for page files and automatically register them with the router.
 */
object PageLoader {
    /**
     * Automatically register all pages from the pages directory structure.
     * The registration is handled by generated code that maps file paths to route paths.
     *
     * @param registry The page registry to register pages with
     */
    fun registerPages(registry: PageRegistry) {
        // Delegate to the generated code
        GeneratedPageLoader.registerPages(registry)
    }

    /**
     * Create a router with all registered pages.
     * This keeps the old router API working with our file-based approach.
     */
    fun createRouter(): Router {
        return createFileBasedRouter()
    }

    /**
     * Returns the built-in page manifest for diagnostics.
     *
     * @return page paths registered by [GeneratedPageLoader]
     */
    fun scanPagesDirectory(): List<String> {
        // This would be generated at build time
        return listOf(
            "/pages/Index.kt",
            "/pages/About.kt",
            "/pages/404.kt",
            "/pages/users/[id].kt",
            "/pages/users/Profile.kt",
            "/pages/blog/[id].kt"
        )
    }

    /**
     * Maps a page manifest path to its route path using Next.JS conventions.
     *
     * @param filePath The page path in a manifest
     * @return The route path this file represents
     */
    fun filePathToRoutePath(filePath: String): String {
        return DefaultPageRegistry().normalizePath(filePath)
    }
}
