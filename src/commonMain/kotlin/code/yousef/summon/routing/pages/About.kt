@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.routing.pages

import code.yousef.summon.core.Composable
import code.yousef.summon.routing.NavLink
import code.yousef.summon.routing.RouteParams
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

/**
 * About page component that maps to the /about route in our Next.js style router.
 */
class About : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            return consumer.div {
                h1 {
                    +"About Summon Router"
                }

                p {
                    +"This is the about page located at /pages/About.kt"
                }

                p {
                    +"Summon Router is a Next.js style file-based routing system for Kotlin Multiplatform."
                }

                p {
                    +"It automatically maps files in the pages directory to routes based on file names and structure."
                }

                div {
                    // Navigation links to other pages
                    NavLink("/", "Home", "nav-link").compose(this)
                    NavLink("/about", "About", "nav-link", "active").compose(this)
                    NavLink("/users/profile", "User Profile", "nav-link").compose(this)
                }
            } as T
        }

        return receiver
    }

    companion object {
        /**
         * Factory function to create an instance of this page.
         * This will be registered with the router.
         */
        fun create(params: RouteParams): Composable {
            return About()
        }
    }
} 