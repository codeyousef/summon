package com.summon.routing

/**
 * Loads pages from the file system and registers them with the PageRegistry.
 *
 * In a real implementation, this would use reflection or code generation
 * to automatically scan the file system. For this example, we'll manually
 * register the pages we've created.
 */
object PageLoader {
    /**
     * Manually register all pages.
     * This simulates what would be done automatically by scanning the file system.
     */
    fun registerPages(registry: PageRegistry) {
        // In a real implementation, this would be done automatically by scanning
        // the file system and using reflection or code generation to register pages.

        // For demonstration purposes, we would register pages like this:
        // registry.registerPage("/", IndexPage::create)
        // registry.registerPage("/about", AboutPage::create)
        // registry.registerPage("/users/profile", ProfilePage::create)
        // registry.registerPage("/blog/:id", BlogPostPage::create)
        // registry.registerNotFoundPage(NotFoundPage::create)
    }

    /**
     * Create a router with all registered pages.
     */
    fun createRouter(): Router {
        val registry = DefaultPageRegistry()
        registerPages(registry)
        return registry.createRouter()
    }

    /**
     * In a real Next.js style implementation, this would scan the file system.
     * This is a placeholder for what would be implemented in a real application.
     */
    private fun scanPagesDirectory(): List<String> {
        // This would actually scan the file system
        // For this example, we'll just return the paths we know about
        return listOf(
            "/pages/index.kt",
            "/pages/about.kt",
            "/pages/404.kt",
            "/pages/users/profile.kt",
            "/pages/blog/[id].kt"
        )
    }
} 