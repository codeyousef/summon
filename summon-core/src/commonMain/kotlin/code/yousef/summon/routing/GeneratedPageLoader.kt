package code.yousef.summon.routing

// This file is generated at build time - do not edit

import code.yousef.summon.routing.pages.AboutPage
import code.yousef.summon.routing.pages.HomePage
import code.yousef.summon.routing.pages.NotFoundPage
import code.yousef.summon.routing.pages.blog.BlogPostPage
import code.yousef.summon.routing.pages.users.UserProfilePage

object GeneratedPageLoader {
    /**
     * Register all discovered pages with the given registry.
     */
    fun registerPages(registry: PageRegistry) {
        // Register 404 page
        registry.registerNotFoundPage({ NotFoundPage() })

        // Register regular pages
        registry.registerPage("/", { HomePage() })
        registry.registerPage("/about", { AboutPage() })
        registry.registerPage("/users/:id", { params -> UserProfilePage(params["id"]) })
        registry.registerPage("/blog/:id", { params -> BlogPostPage(params["id"]) })
        registry.registerPage("/users/profile", { UserProfilePage("profile") })
    }
} 