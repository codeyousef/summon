package code.yousef.summon.routing


import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

// --- Expected Router Interface ---

/**
 * Expected interface for the Router.
 * Platform implementations (JVM, JS) will provide the actual class.
 */
expect interface Router {
    /**
     * Navigates to the specified path.
     * Platform implementations handle history updates (e.g., browser pushState).
     */
    fun navigate(path: String, pushState: Boolean = true)

    /**
     * Composes the UI for the router at the given initial path.
     * This might be specific to certain platform renderers.
     */
    @Composable
    fun create(initialPath: String)
    
    /**
     * The current path of the router.
     * This helps components like NavLink determine their active state.
     */
    val currentPath: String
}

// --- Router Definition and Matching (Defined ONCE) ---

data class RouteDefinition(
    val path: String,
    val content: @Composable (RouteParams) -> Unit,
    val title: String? = null,
    val description: String? = null,
    val canonicalUrl: String? = null
)

data class RouteParams(val params: Map<String, String>) {
    fun get(key: String): String? = params[key]
    fun asMap(): Map<String, String> = params
}

data class RouteMatchResult(
    val route: RouteDefinition,
    val params: Map<String, String>
)

// --- CompositionLocal for Router ---
private val localRouter = CompositionLocal.compositionLocalOf<Router?>(null)

// Helper property to access the current router
val LocalRouter: Router?
    @Composable
    get() = localRouter.current

// --- Root Router Composable (Revised to use expect interface) ---

/**
 * Root composable that provides the Router instance via CompositionLocal
 * and delegates rendering to the platform-specific `router.create()`.
 * 
 * This component uses the modern @Composable function pattern instead of
 * the class-based Composable interface approach, allowing for better integration
 * with the reactive composition system and proper handling of side effects.
 * 
 * @param router The Router instance to use for navigation and rendering
 * @param initialPath The initial path to render
 * @param modifier Optional modifier for styling the container
 */
@Composable
fun RouterComponent(
    router: Router,
    initialPath: String,
    modifier: Modifier = Modifier()
) {
    // Store the previous Router from CompositionLocal
    val previousRouter = localRouter.current

    try {
        // Provide the new Router via CompositionLocal
        // This makes the router accessible to all child composables
        // through the LocalRouter property
        localRouter.provides(router)
        
        // Delegate the actual content rendering to the 
        // platform-specific Router implementation
        router.create(initialPath)
        
        // Note: Platform-specific implementations may use LaunchedEffect
        // to handle browser history changes (for JS) or server-side
        // state changes (for JVM).
    } finally {
        // Restore the previous Router when this composable leaves the composition
        localRouter.provides(previousRouter)
    }
}

// --- Router Builder DSL (Remains similar, but uses the interface) ---

interface RouterBuilder {
    fun route(path: String, content: @Composable (RouteParams) -> Unit)
    fun setNotFound(content: @Composable (RouteParams) -> Unit)
}

// Internal implementation for the builder
internal class RouterBuilderImpl : RouterBuilder {
    val routes = mutableListOf<RouteDefinition>()
    var notFoundPage: @Composable (RouteParams) -> Unit =
        { params -> Text("Default Not Found - Path: ${params.get("path")}") }

    override fun route(path: String, content: @Composable (RouteParams) -> Unit) {
        routes.add(RouteDefinition(path, content))
    }

    fun route(
        path: String,
        content: @Composable (RouteParams) -> Unit,
        title: String? = null,
        description: String? = null,
        canonicalUrl: String? = null
    ) {
        routes.add(RouteDefinition(path, content, title, description, canonicalUrl))
    }

    override fun setNotFound(content: @Composable (RouteParams) -> Unit) {
        notFoundPage = content
    }
}

// Helper function to create a router using the DSL
expect fun createRouter(builder: RouterBuilder.() -> Unit): Router 
