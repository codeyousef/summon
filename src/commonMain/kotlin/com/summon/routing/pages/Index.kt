@file:Suppress("UNCHECKED_CAST")

package com.summon.routing.pages

import com.summon.Composable
import com.summon.routing.NavLink
import com.summon.routing.RouteParams
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

/**
 * Home page component that acts as the index page (/) in our Next.js style router.
 */
class Index : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            return consumer.div {
                h1 {
                    +"Welcome to Summon Next.js Style Router"
                }

                p {
                    +"This is the index page located at /pages/Index.kt"
                }

                div {
                    // Navigation links to other pages
                    NavLink("/", "Home", "nav-link", "active").compose(this)
                    NavLink("/about", "About", "nav-link").compose(this)
                    NavLink("/users/profile", "User Profile", "nav-link").compose(this)
                    NavLink("/blog/123", "Blog Post", "nav-link").compose(this)
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
            return Index()
        }
    }
} 