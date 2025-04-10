@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
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
 * Object to store the current router in a context
 */
object RouterContext {
    var current: Router? = null
}

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
    RouterContext.current = this

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
) {
    fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            // Get the current router instance
            val router = RouterContext.current
            val isActive = router is JvmRouter && router.getCurrentPath() == to

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

/**
 * JVM actual implementation for the Router interface.
 * This provides a basic, non-functional router for JVM targets.
 */
actual interface Router {
    /**
     * Navigates to the specified path.
     * Platform implementations handle history updates (e.g., browser pushState).
     */
    actual fun navigate(path: String, pushState: Boolean)

    /**
     * Composes the UI for the router at the given initial path.
     * This might be specific to certain platform renderers.
     */
    @Composable
    actual fun create(initialPath: String)
}

// Basic JVM Router implementation (needs more logic for actual use)
class JvmRouter(private val routes: List<RouteDefinition>, private val notFound: (RouteParams) -> Unit) : Router {

    private var currentPath: String = ""
    private var currentParams: Map<String, String> = emptyMap()

    // Public method to get the current path for NavLink active state
    fun getCurrentPath(): String = currentPath

    override fun navigate(path: String, pushState: Boolean) {
        println("JVM Router: Navigating to $path (pushState=$pushState) - Needs Compose HTML update.")
        val match = findMatchingRoute(path)
        if (match != null) {
            currentPath = path
            currentParams = match.params
        } else {
            currentPath = path
            currentParams = mapOf("path" to path)
        }
    }

    @Composable
    override fun create(initialPath: String) {
        navigate(initialPath, false)

        println("JvmRouter.create called for $initialPath. Rendering logic needs update for Compose HTML.")
        val match = findMatchingRoute(currentPath)

        if (match != null) {
            println("Rendering route: ${match.route.path}")
            match.route.content(RouteParams(match.params))
        } else {
            println("Rendering Not Found page for path: $currentPath")
            notFound(RouteParams(currentParams))
        }
    }

    private fun findMatchingRoute(path: String): RouteMatchResult? {
        val exactMatch = routes.firstOrNull { it.path == path }
        if (exactMatch != null) return RouteMatchResult(exactMatch, emptyMap())

        for (route in routes) {
            val routeParts = route.path.split("/").filter { it.isNotEmpty() }
            val pathParts = path.split("/").filter { it.isNotEmpty() }

            if (routeParts.size == pathParts.size) {
                val params = mutableMapOf<String, String>()
                var match = true
                for (i in routeParts.indices) {
                    if (routeParts[i].startsWith("{") && routeParts[i].endsWith("}")) {
                        val paramName = routeParts[i].removeSurrounding("{", "}")
                        params[paramName] = pathParts[i]
                    } else if (routeParts[i] != pathParts[i]) {
                        match = false
                        break
                    }
                }
                if (match) {
                    return RouteMatchResult(route, params)
                }
            }
        }
        return null
    }
}

/**
 * JVM actual implementation for the createRouter function.
 */
actual fun createRouter(builder: RouterBuilder.() -> Unit): Router {
    val builderImpl = RouterBuilderImpl()
    builderImpl.builder()
    val notFoundComposable: (RouteParams) -> Unit = { params ->
        println("Default Not Found page for JVM Router called with params: $params. Needs Compose HTML implementation.")
    }
    return JvmRouter(builderImpl.routes, builderImpl.notFoundPage ?: notFoundComposable)
} 
