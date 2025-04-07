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
    
    // Potentially add state for currentPath if needed by consumers
    // val currentPathState: State<String> 
}

// --- Router Definition and Matching (Defined ONCE) ---

data class RouteDefinition(
    val path: String,
    val content: @Composable (RouteParams) -> Unit
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
 */
@Composable
fun RouterComponent(
    router: Router, // Accept the actual Router instance
    initialPath: String,
    modifier: Modifier = Modifier() // Keep modifier for potential container
) {
    // Store the previous Router
    val previousRouter = localRouter.current
    
    // Provide the new Router
    localRouter.provides(router)
    
    try {
        // Delegate the actual composition and path handling to the 
        // platform-specific Router implementation.
        router.create(initialPath)
        
        // Platform implementations might need effects to listen for external navigation
        // e.g., browser back/forward buttons.
    } finally {
        // Restore the previous Router
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
    var notFoundPage: @Composable (RouteParams) -> Unit = { params -> Text("Default Not Found - Path: ${params.get("path")}") }

    override fun route(path: String, content: @Composable (RouteParams) -> Unit) {
        routes.add(RouteDefinition(path, content))
    }

    override fun setNotFound(content: @Composable (RouteParams) -> Unit) {
        notFoundPage = content
    }
}

// Helper function to create a router using the DSL
expect fun createRouter(builder: RouterBuilder.() -> Unit): Router 
