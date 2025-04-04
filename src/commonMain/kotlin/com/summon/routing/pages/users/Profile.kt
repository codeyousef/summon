@file:Suppress("UNCHECKED_CAST")

package com.summon.routing.pages.users

import com.summon.Composable
import com.summon.routing.NavLink
import com.summon.routing.RouteParams
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

/**
 * User Profile page component that maps to the /users/profile route in our Next.js style router.
 * The nested directory structure is reflected in the URL.
 */
class Profile : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            return consumer.div {
                h1 {
                    +"User Profile"
                }

                p {
                    +"This is the user profile page located at /pages/users/Profile.kt"
                }

                p {
                    +"The nested directory structure (/pages/users/) is reflected in the URL (/users/profile)"
                }

                div {
                    // Navigation links to other pages
                    NavLink("/", "Home", "nav-link").compose(this)
                    NavLink("/about", "About", "nav-link").compose(this)
                    NavLink("/users/profile", "User Profile", "nav-link", "active").compose(this)
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
            return Profile()
        }
    }
} 