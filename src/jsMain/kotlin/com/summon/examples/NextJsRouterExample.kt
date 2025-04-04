@file:Suppress("UNCHECKED_CAST")

package com.summon.examples

import com.summon.Composable
import com.summon.routing.RouteParams
import com.summon.routing.createPageRouter
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.p

/**
 * Example showing how to use the Next.js style router in a Summon application.
 */
class NextJsRouterExample : Composable {
    // Create a page router
    private val router = createPageRouter {
        // Register pages with paths
        page("/", HomePageComponent::create)
        page("/about", AboutPageComponent::create)
        page("/users/profile", ProfilePageComponent::create)
        page("/blog/:id", BlogPostComponent::create)

        // Register a 404 page
        notFound(NotFoundPageComponent::create)
    }

    override fun <T> compose(receiver: T): T {
        // Just forward to the router's compose method
        return router.compose(receiver)
    }

    // Example page components
    class HomePageComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 { +"Home Page" }
                    p { +"This is the home page." }
                } as T
            }
            return receiver
        }

        companion object {
            fun create(params: RouteParams): Composable = HomePageComponent()
        }
    }

    class AboutPageComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 { +"About Page" }
                    p { +"This is the about page." }
                } as T
            }
            return receiver
        }

        companion object {
            fun create(params: RouteParams): Composable = AboutPageComponent()
        }
    }

    class ProfilePageComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 { +"User Profile" }
                    p { +"This is the user profile page." }
                } as T
            }
            return receiver
        }

        companion object {
            fun create(params: RouteParams): Composable = ProfilePageComponent()
        }
    }

    class BlogPostComponent(private val postId: String?) : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 { +"Blog Post #$postId" }
                    p { +"This is blog post #$postId." }
                } as T
            }
            return receiver
        }

        companion object {
            fun create(params: RouteParams): Composable {
                val postId = params["id"] ?: "unknown"
                return BlogPostComponent(postId)
            }
        }
    }

    class NotFoundPageComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 { +"404 - Page Not Found" }
                    p { +"The page you're looking for doesn't exist." }
                } as T
            }
            return receiver
        }

        companion object {
            fun create(params: RouteParams): Composable = NotFoundPageComponent()
        }
    }
} 