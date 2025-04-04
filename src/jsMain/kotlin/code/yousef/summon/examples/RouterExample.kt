package code.yousef.summon.examples

import code.yousef.summon.Composable
import code.yousef.summon.routing.NavLink
import code.yousef.summon.routing.Route
import code.yousef.summon.routing.RouteParams
import code.yousef.summon.routing.Router
import kotlinx.html.*

/**
 * Example of using the router system.
 */
class RouterExample : Composable {
    // Create routes directly
    private val homeRoute = Route("/") { _: RouteParams -> HomeComponent() }
    private val aboutRoute = Route("/about") { _: RouteParams -> AboutComponent() }
    private val userRoute = Route("/user/:id") { params: RouteParams -> UserProfileComponent(params["id"]) }

    // Create router with routes
    private val router = Router(
        routes = listOf(homeRoute, aboutRoute, userRoute),
        notFoundComponent = { _: RouteParams -> NotFoundComponent() }
    )

    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            return consumer.div {
                // App header with navigation
                div {
                    attributes["style"] =
                        "background-color: #f0f0f0; padding: 20px; display: flex; justify-content: space-between; align-items: center;"
                    h1 {
                        +"Router Example"
                    }

                    // Navigation links
                    div {
                        NavLink("/", "Home", "nav-link").compose(this)
                        NavLink("/about", "About", "nav-link").compose(this)
                        NavLink("/user/123", "User Profile", "nav-link").compose(this)
                        NavLink("/does-not-exist", "Not Found Example", "nav-link").compose(this)
                    }
                }

                // Main content area
                div {
                    attributes["style"] = "padding: 20px;"
                    router.compose(this)
                }
            } as T
        }

        return receiver
    }

    /**
     * Home page component.
     */
    class HomeComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 {
                        +"Welcome to Summon Router!"
                    }
                    p {
                        +"This is a simple example of how to use the router system in Summon."
                    }
                    p {
                        +"Try clicking on the navigation links above to see how the router handles different routes."
                    }
                } as T
            }

            return receiver
        }
    }

    /**
     * About page component.
     */
    class AboutComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 {
                        +"About"
                    }
                    p {
                        +"Summon is a Kotlin Multiplatform UI library that allows you to build user interfaces for web and desktop applications."
                    }
                    p {
                        +"The router system provides a way to handle navigation in your application."
                    }
                } as T
            }

            return receiver
        }
    }

    /**
     * User profile component that displays a user's profile based on the ID parameter.
     */
    class UserProfileComponent(private val userId: String?) : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    h1 {
                        +"User Profile"
                    }

                    if (userId != null) {
                        h2 {
                            +"User ID: $userId"
                        }
                        p {
                            +"This is the profile page for user $userId."
                        }
                    } else {
                        p {
                            +"No user ID provided."
                        }
                    }
                } as T
            }

            return receiver
        }
    }

    /**
     * Custom 404 Not Found component.
     */
    class NotFoundComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {
                @Suppress("UNCHECKED_CAST")
                val consumer = receiver as TagConsumer<Any?>

                return consumer.div {
                    attributes["style"] = "text-align: center; padding: 40px;"
                    h1 {
                        attributes["style"] = "color: #d32f2f;"
                        +"404 - Page Not Found"
                    }
                    p {
                        +"The page you are looking for doesn't exist or has been moved."
                    }
                    p {
                        +"Please use the navigation menu to find what you're looking for."
                    }
                } as T
            }

            return receiver
        }
    }
} 