@file:Suppress("UNCHECKED_CAST")

package codes.yousef.summon.routing

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.runtime.Composable
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
 * JVM implementation of RouterContext
 */
actual object RouterContext {
    private val threadLocalRouter = ThreadLocal<Router?>()

    /**
     * The current router instance, scoped to the executing thread.
     */
    actual var current: Router?
        get() = threadLocalRouter.get()
        internal set(value) {
            if (value == null) {
                threadLocalRouter.remove()
            } else {
                threadLocalRouter.set(value)
            }
        }

    /**
     * Clears the current router instance for this thread.
     */
    actual fun clear() {
        threadLocalRouter.remove()
    }

    /**
     * Executes a block with the specified router as the current router.
     *
     * @param router The router to use for the block
     * @param block The block to execute
     * @return The result of the block
     */
    actual fun <T> withRouter(router: Router, block: () -> T): T {
        val previous = current
        current = router
        try {
            return block()
        } finally {
            current = previous
        }
    }
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
    /**
     * Composes the supplied content.
     *
     * @param receiver The receiver value.
     * @return The resulting value.
     */
    fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            // Get the current router instance
            val router = RouterContext.current
            val isActive = router is Router && router.currentPath == to

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

    /**
     * The current path of the router.
     * This helps components like NavLink determine their active state.
     */
    actual val currentPath: String
}

/**
 * Represents JVM router.
 *
 * @property routes The routes value.
 * @property notFound The not found value.
 * @property guardFallback The guard fallback value.
 */
class JvmRouter(
    private val routes: List<RouteDefinition>,
    private val notFound: @Composable (RouteParams) -> Unit,
    private val guardFallback: @Composable (GuardResult) -> Unit = { result -> Text(result.safeReason) }
) : Router, NavigationControl {

    // Store the current path and params
    private var _currentPath: String = ""
    /** The null value. */
    override var interceptor: NavigationInterceptor? = null
    /** The null value. */
    override var pendingPath: String? = null
        private set
    private var currentParams: Map<String, String> = emptyMap()

    // Implement the currentPath property from the Router interface
    /** The property declaration value. */
    override val currentPath: String
        get() = _currentPath

    /**
     * Navigate to a different route
     */
    override fun navigate(path: String, pushState: Boolean) {
        val safePath = requireNotNull(InternalRoutePath.parse(path)) {
            "Router navigation accepts same-origin paths without query strings or fragments"
        }
        if (interceptor?.beforeNavigate(_currentPath, safePath.encodedPath) == NavigationDecision.CANCEL) {
            pendingPath = safePath.encodedPath
            return
        }
        performNavigation(safePath)
    }

    /** Executes the continue pending operation. */
    override fun continuePending() {
        val safePath = pendingPath?.let(InternalRoutePath::parse) ?: return
        pendingPath = null
        performNavigation(safePath)
    }

    /** Cancels pending. */
    override fun cancelPending() {
        pendingPath = null
    }

    private fun performNavigation(path: InternalRoutePath) {
        _currentPath = path.encodedPath
        currentParams = findMatchingRoute(path)?.params ?: emptyMap()
    }

    /**
     * Creates the operation.
     *
     * @param initialPath The initial path value.
     */
    @Composable
    override fun create(initialPath: String) {
        // Set the initial path and find matching route
        navigate(initialPath, false)

        // Find the route for the current path
        val match = findMatchingRoute(InternalRoutePath.parse(_currentPath))
        if (match == null) {
            notFound(RouteParams(emptyMap()))
            return
        }
        val params = RouteParams(match.params)
        when (val guardResult = match.route.evaluateGuards(params)) {
            GuardResult.Allow -> RouteContentHandler(match)
            is GuardResult.Redirect -> {
                val redirect = InternalRoutePath.parse(guardResult.path)
                if (redirect == null) guardFallback(GuardResult.Deny) else {
                    navigate(redirect.encodedPath, false)
                    guardFallback(GuardResult.Loading)
                }
            }
            else -> guardFallback(guardResult)
        }
    }

    private fun findMatchingRoute(path: InternalRoutePath?): RouteMatchResult? {
        path ?: return null
        for (route in routes) {
            val params = matchInternalRoute(route.path, path)
            if (params != null) return RouteMatchResult(route, params)
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
    return JvmRouter(builderImpl.routes, builderImpl.notFoundPage, builderImpl.guardFallbackPage)
}
