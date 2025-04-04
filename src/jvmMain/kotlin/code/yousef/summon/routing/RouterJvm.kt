package code.yousef.summon.routing

import code.yousef.summon.Composable
import kotlinx.html.TagConsumer
import kotlinx.html.a
import kotlinx.html.unsafe
import java.util.concurrent.ConcurrentHashMap

/**
 * Extends the Router class with JVM-specific functionality.
 */

// Map to store server-side router instances by a session ID
private val routerRegistry = ConcurrentHashMap<String, Router>()

/**
 * Sets up the router for server-side rendering.
 *
 * @param sessionId A unique session identifier
 * @return The router instance
 */
fun Router.setupForServer(sessionId: String): Router {
    // Register the router instance
    routerRegistry[sessionId] = this

    // Set as current router in context
    RouterContext.setCurrent(this)

    return this
}

/**
 * Retrieves a router instance for a specific session.
 *
 * @param sessionId The session identifier
 * @return The router instance or null if not found
 */
fun getRouterForSession(sessionId: String): Router? {
    return routerRegistry[sessionId]
}

/**
 * Removes a router instance for a specific session.
 *
 * @param sessionId The session identifier
 */
fun removeRouterForSession(sessionId: String) {
    routerRegistry.remove(sessionId)
}

/**
 * Creates a router for server-side rendering.
 *
 * @param sessionId A unique session identifier
 * @param routes The routes to include
 * @param notFoundComponent The component to display for 404 errors
 * @return A router configured for server-side use
 */
fun createServerRouter(
    sessionId: String,
    vararg routes: Route,
    notFoundComponent: ((RouteParams) -> Composable)? = null
): Router {
    val router = Router.create(*routes, notFoundComponent = notFoundComponent)
    return router.setupForServer(sessionId)
}

/**
 * Creates a router for server-side rendering using a DSL.
 *
 * @param sessionId A unique session identifier
 * @param init The DSL initialization function
 * @return A router configured for server-side use
 */
fun createServerRouter(
    sessionId: String,
    init: Router.RouterBuilder.() -> Unit
): Router {
    val router = Router.create(init)
    return router.setupForServer(sessionId)
}

/**
 * A server-specific NavLink implementation that works without JavaScript.
 *
 * @param to Path to navigate to
 * @param text Text to display in the link
 * @param className Optional CSS class name
 * @param activeClassName CSS class to apply when this link is active
 */
class ServerNavLink(
    val to: String,
    val text: String,
    val className: String = "",
    val activeClassName: String = "active"
) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            // Get the current router instance
            val router = RouterContext.current
            val isActive = router?.getCurrentPath() == to

            return consumer.a(href = to) {
                // Set up the classes
                val classNames = mutableSetOf<String>()

                // Apply regular class name
                if (className.isNotEmpty()) {
                    classNames.add(className)
                }

                // Apply active class if this link is active
                if (isActive && activeClassName.isNotEmpty()) {
                    classNames.add(activeClassName)
                }

                // Apply the classes
                if (classNames.isNotEmpty()) {
                    attributes["class"] = classNames.joinToString(" ")
                }

                unsafe {
                    +text
                }
            } as T
        }

        return receiver
    }
} 