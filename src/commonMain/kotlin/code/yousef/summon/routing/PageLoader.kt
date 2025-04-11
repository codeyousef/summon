package code.yousef.summon.routing

import code.yousef.summon.routing.pages.AboutPage
import code.yousef.summon.routing.pages.HomePage
import code.yousef.summon.routing.pages.NotFoundPage
import code.yousef.summon.routing.pages.blog.BlogPostPage
import code.yousef.summon.routing.pages.users.UserProfilePage

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
     * Scan for pages in the codebase.
     * This function is provided for debugging purposes only.
     * The actual scanning is done at build time by the PageLoaderGenerator.
     *
     * @return List of file paths that would be found by scanning the pages directory
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
     * Maps a file path to its corresponding route path using Next.js conventions.
     * This function is primarily used at build time by the PageLoaderGenerator.
     *
     * @param filePath The file path relative to the project root
     * @return The route path this file represents
     */
    fun filePathToRoutePath(filePath: String): String {
        return DefaultPageRegistry().normalizePath(filePath)
    }
} 
