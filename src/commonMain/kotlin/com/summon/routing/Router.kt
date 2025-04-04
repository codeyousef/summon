package com.summon.routing

import com.summon.Composable
import kotlinx.html.TagConsumer
import kotlinx.html.div

/**
 * Main routing component that manages navigation between different routes.
 *
 * @param routes List of routes to be handled by the router
 * @param notFoundComponent Component to display when no route matches (optional)
 */
class Router(
    private val routes: List<Route>,
    private val notFoundComponent: ((RouteParams) -> Composable)? = null
) : Composable {
    private var currentPath = "/"
    private var currentRoute: Route? = null
    private var currentParams: RouteParams = RouteParams(emptyMap())

    /**
     * Navigates to the specified path.
     *
     * @param path The path to navigate to
     * @param pushState Whether to add the navigation to browser history
     */
    fun navigate(path: String, pushState: Boolean = true) {
        if (path == currentPath) return

        currentPath = path
        updateCurrentRoute()

        // Platform-specific history handling will be implemented in JS and JVM extensions
        if (pushState) {
            updateBrowserHistory(path)
        }
    }

    /**
     * Gets the current path.
     *
     * @return The current path
     */
    fun getCurrentPath(): String = currentPath

    /**
     * Gets the current route parameters.
     *
     * @return The current route parameters
     */
    fun getCurrentParams(): RouteParams = currentParams

    /**
     * Checks if a path matches any of the routes.
     *
     * @param path The path to check
     * @return True if the path matches any route
     */
    fun hasMatchingRoute(path: String): Boolean {
        return routes.any { it.matches(path) }
    }

    /**
     * Updates the current route based on the current path.
     */
    private fun updateCurrentRoute() {
        val matchingRoute = routes.find { it.matches(currentPath) }
        currentRoute = matchingRoute
        currentParams = matchingRoute?.extractParams(currentPath) ?: RouteParams(emptyMap())
    }

    /**
     * Platform-specific history handling.
     * This will be implemented differently for JS and JVM.
     */
    private fun updateBrowserHistory(path: String) {
        // Will be implemented in platform-specific extensions
    }

    /**
     * Renders the current route's component.
     */
    override fun <T> compose(receiver: T): T {
        // If receiver is a TagConsumer, use it directly
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getCurrentComponent().compose(receiver as TagConsumer<Any?>) as T
        }

        // Otherwise, pass through the receiver
        return receiver
    }

    /**
     * Gets the component to render for the current path.
     *
     * @return The component to render for the current path
     */
    fun getCurrentComponent(): Composable {
        if (currentRoute == null) {
            return notFoundComponent?.invoke(currentParams) ?: NotFoundComponent()
        }

        return currentRoute!!.component(currentParams)
    }

    /**
     * Default 404 Not Found component.
     */
    @Suppress("UNCHECKED_CAST")
    private class NotFoundComponent : Composable {
        override fun <T> compose(receiver: T): T {
            if (receiver is TagConsumer<*>) {

                val consumer = receiver as TagConsumer<Any?>

                // Create a simple not found message using a div tag instead of onFinalize
                consumer.div {
                    attributes["style"] = "padding: 20px; text-align: center;"
                    +"404 - Page Not Found"
                }
                return consumer as T
            }
            return receiver
        }
    }

    companion object {
        /**
         * Creates a router with the given routes.
         *
         * @param routes List of routes to be handled by the router
         * @param notFoundComponent Component to display when no route matches
         * @return A new Router instance
         */
        fun create(
            vararg routes: Route,
            notFoundComponent: ((RouteParams) -> Composable)? = null
        ): Router {
            return Router(routes.toList(), notFoundComponent)
        }

        /**
         * Creates a router with a builder DSL.
         *
         * @param init Builder DSL function
         * @return A new Router instance
         */
        fun create(init: RouterBuilder.() -> Unit): Router {
            val builder = RouterBuilder()
            builder.init()
            return Router(builder.routes, builder.notFoundComponent)
        }
    }

    /**
     * Builder for creating a router with a DSL.
     */
    class RouterBuilder {
        val routes = mutableListOf<Route>()
        var notFoundComponent: ((RouteParams) -> Composable)? = null

        /**
         * Adds a route to the router.
         *
         * @param pattern The URL pattern
         * @param component The component factory
         */
        fun route(pattern: String, component: (RouteParams) -> Composable) {
            routes.add(Route(pattern, component))
        }

        /**
         * Sets the component to display when no route matches.
         *
         * @param component The component factory
         */
        fun notFound(component: (RouteParams) -> Composable) {
            notFoundComponent = component
        }
    }
} 