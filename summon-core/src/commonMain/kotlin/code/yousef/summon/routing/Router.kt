package code.yousef.summon.routing


import code.yousef.summon.runtime.Composable

// --- Expected Router Interface ---

/**
 * Core router interface for navigation and page rendering across platforms.
 *
 * The Router interface provides a unified abstraction for navigation that works
 * seamlessly across browser and server environments. It supports both declarative
 * and programmatic navigation patterns with full type safety.
 *
 * ## Platform Implementations
 *
 * - **JavaScript**: Integrates with browser History API for client-side navigation
 * - **JVM**: Supports server-side rendering and static page generation
 * - **Multiplatform**: Enables code sharing between client and server routing logic
 *
 * ## Navigation Patterns
 *
 * ### Programmatic Navigation
 * ```kotlin
 * val router = LocalRouter.current
 * router.navigate("/users/profile")
 * ```
 *
 * ### Declarative Navigation
 * ```kotlin
 * Link(href = "/about", label = "About Us")
 * ```
 *
 * ### History Management
 * ```kotlin
 * // Navigate with history entry
 * router.navigate("/contact", pushState = true)
 *
 * // Replace current entry
 * router.navigate("/contact", pushState = false)
 * ```
 *
 * ## Route Composition
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     val router = remember { createFileBasedRouter() }
 *
 *     RouterComponent(
 *         router = router,
 *         initialPath = "/",
 *         modifier = Modifier.fillMaxSize()
 *     )
 * }
 * ```
 *
 * @see FileBasedRouter for file-based routing implementation
 * @see RouterComponent for router composition
 * @see LocalRouter for accessing router in components
 * @since 1.0.0
 */
expect interface Router {
    /**
     * Navigates to the specified path with optional history management.
     *
     * Performs navigation to the target path using platform-appropriate mechanisms.
     * On browsers, this integrates with the History API; on servers, it updates
     * internal routing state.
     *
     * ## Navigation Behavior
     *
     * - **pushState = true**: Adds new entry to browser history stack
     * - **pushState = false**: Replaces current history entry (no back button entry)
     *
     * ## Path Format
     *
     * Paths should be absolute and start with `/`:
     * - `/` - Root page
     * - `/users` - Users listing
     * - `/users/profile` - Nested route
     * - `/users/123` - Dynamic route with parameter
     *
     * ## Usage Examples
     *
     * ```kotlin
     * // Navigate to new page (adds history entry)
     * router.navigate("/contact")
     *
     * // Replace current page (no history entry)
     * router.navigate("/login", pushState = false)
     *
     * // Navigate with query parameters
     * router.navigate("/search?q=kotlin")
     * ```
     *
     * @param path Target URL path to navigate to (must start with `/`)
     * @param pushState Whether to add entry to browser history (default: true)
     * @throws IllegalArgumentException if path is invalid or empty
     * @see currentPath for accessing current location
     * @since 1.0.0
     */
    fun navigate(path: String, pushState: Boolean = true)

    /**
     * Creates and renders the router's UI tree for the specified initial path.
     *
     * This composable function establishes the router's rendering context and
     * displays the appropriate page component based on the current or initial path.
     * It manages route matching, parameter extraction, and component lifecycle.
     *
     * ## Rendering Process
     *
     * 1. **Route Resolution**: Matches the path against registered routes
     * 2. **Parameter Extraction**: Extracts route parameters from URL segments
     * 3. **Component Loading**: Loads and instantiates the target page component
     * 4. **Context Provision**: Provides routing context to child components
     *
     * ## Integration with Composition
     *
     * ```kotlin
     * @Composable
     * fun App() {
     *     val router = remember { createFileBasedRouter() }
     *
     *     ThemeProvider {
     *         RouterComponent(
     *             router = router,
     *             initialPath = getCurrentPath()
     *         )
     *     }
     * }
     * ```
     *
     * ## Error Handling
     *
     * - **Unknown Routes**: Renders 404 page if registered
     * - **Component Errors**: Provides error boundaries for failed page loads
     * - **Navigation Errors**: Handles invalid navigation attempts gracefully
     *
     * @param initialPath Starting path for router initialization
     * @see RouterComponent for composition helper
     * @see RouteParams for parameter access
     * @since 1.0.0
     */
    @Composable
    fun create(initialPath: String)

    /**
     * The current active path being displayed by the router.
     *
     * This property reflects the router's current location and updates automatically
     * when navigation occurs. It's used by navigation components to determine
     * active states and by application logic to respond to route changes.
     *
     * ## Path Format
     *
     * The current path includes:
     * - **Base Path**: The route portion (e.g., `/users/profile`)
     * - **Parameters**: Embedded in the path structure (e.g., `/users/123`)
     * - **Query String**: May or may not be included depending on platform
     *
     * ## Usage Examples
     *
     * ```kotlin
     * @Composable
     * fun NavLink(href: String, label: String) {
     *     val router = LocalRouter.current
     *     val isActive = router.currentPath == href
     *
     *     Link(
     *         href = href,
     *         label = label,
     *         modifier = Modifier.color(
     *             if (isActive) Theme.colors.primary else Theme.colors.onSurface
     *         )
     *     )
     * }
     * ```
     *
     * ## Reactivity
     *
     * This property is reactive and will trigger recomposition when the route changes:
     *
     * ```kotlin
     * @Composable
     * fun RouteAwareComponent() {
     *     val router = LocalRouter.current
     *     val currentPath = router.currentPath
     *
     *     LaunchedEffect(currentPath) {
     *         // React to route changes
     *         analytics.trackPageView(currentPath)
     *     }
     * }
     * ```
     *
     * @return Current URL path (always starts with `/`)
     * @see navigate for changing the current path
     * @see RouteParams.current for accessing route parameters
     * @since 1.0.0
     */
    val currentPath: String
}

// --- Router Definition and Matching (Defined ONCE) ---

/**
 * Definition of a route with its path pattern and associated content.
 *
 * RouteDefinition encapsulates all metadata needed to register and render
 * a route within the routing system. It combines the path pattern with
 * the composable content and optional SEO metadata.
 *
 * ## Path Patterns
 *
 * RouteDefinition supports various path pattern formats:
 * - **Static**: `/about`, `/contact`
 * - **Parameters**: `/users/:id`, `/posts/:slug`
 * - **Wildcards**: `/files/*path`
 * - **Optional**: `/posts/:id?`
 *
 * ## SEO Integration
 *
 * Optional metadata enhances SEO and social sharing:
 * - **title**: Page title for `<title>` tag
 * - **description**: Meta description for search engines
 * - **canonicalUrl**: Canonical URL for duplicate content management
 *
 * ## Example Usage
 *
 * ```kotlin
 * val userProfileRoute = RouteDefinition(
 *     path = "/users/:id",
 *     content = { params ->
 *         UserProfile(userId = params["id"]!!)
 *     },
 *     title = "User Profile",
 *     description = "View user profile and activity"
 * )
 * ```
 *
 * @property path URL pattern for this route (supports parameters and wildcards)
 * @property content Composable function that renders the route content
 * @property title Optional page title for SEO and browser tab
 * @property description Optional meta description for search engines
 * @property canonicalUrl Optional canonical URL for this route
 * @see RouteParams for parameter access within content
 * @see Router for route registration
 * @since 1.0.0
*/
data class RouteDefinition(
    val path: String,
    val content: @Composable (RouteParams) -> Unit,
    val title: String? = null,
    val description: String? = null,
    val canonicalUrl: String? = null
)

/**
 * Container for route parameters extracted from URL path segments.
 *
 * RouteParams provides type-safe access to dynamic segments in URL routes.
 * It supports parameter extraction from various route patterns and includes
 * convenience methods for type conversion and safe access.
 *
 * ## Parameter Extraction
 *
 * Parameters are extracted from route patterns:
 * - `/users/:id` → `params["id"]` from `/users/123`
 * - `/posts/:category/:slug` → `params["category"]`, `params["slug"]`
 * - `/files/*path` → `params["path"]` contains remaining path segments
 *
 * ## Type-Safe Access
 *
 * RouteParams provides typed accessors for common parameter types:
 *
 * ```kotlin
 * @Composable
 * fun UserProfile(params: RouteParams) {
 *     val userId = params.getInt("id") ?: return ErrorPage()
 *     val showDetails = params.getBoolean("details") ?: false
 *     val rating = params.getFloat("rating") ?: 0.0f
 *
 *     // Render user profile...
 * }
 * ```
 *
 * ## Current Route Context
 *
 * Access current route parameters from any composable:
 *
 * ```kotlin
 * @Composable
 * fun UserActions() {
 *     val params = RouteParams.current
 *     val userId = params["id"]
 *
 *     if (userId != null) {
 *         Button(
 *             onClick = { editUser(userId) },
 *             label = "Edit User"
 *         )
 *     }
 * }
 * ```
 *
 * ## Safe Parameter Handling
 *
 * ```kotlin
 * @Composable
 * fun ProductPage(params: RouteParams) {
 *     val productId = params.getInt("id")
 *     val variant = params.getOrDefault("variant", "default")
 *     val discounted = params.getBoolean("sale") ?: false
 *
 *     when {
 *         productId == null -> NotFoundPage()
 *         productId <= 0 -> InvalidProductPage()
 *         else -> ProductDisplay(productId, variant, discounted)
 *     }
 * }
 * ```
 *
 * @property params Underlying map of parameter names to string values
 * @see RouteDefinition for route pattern definition
 * @see LocalRouteParams for composition local access
 * @since 1.0.0
 */
data class RouteParams(val params: Map<String, String>) {
    companion object {
        /**
 * Provides access to the current route parameters within any composable.
 *
 * This property uses CompositionLocal to access the route parameters
 * for the currently active route. It returns an empty RouteParams
 * instance if no route context is available.
 *
 * ## Usage in Components
 *
 * ```kotlin
 * @Composable
 * fun UserActions() {
 *     val params = RouteParams.current
 *     val userId = params["id"]
 *
 *     if (userId != null) {
 *         EditUserButton(userId)
 *     }
 * }
 * ```
 *
 * @return Current route parameters or empty if no route context
 * @see LocalRouteParams for direct CompositionLocal access
 * @since 1.0.0
         */
        val current: RouteParams
            @Composable
            get() = LocalRouteParams.current ?: RouteParams(emptyMap())
    }

    /**
 * Gets a parameter value by key.
 *
 * Returns the string value of the parameter if present, or null if the
 * parameter doesn't exist in the current route.
 *
 * ## Usage
 *
 * ```kotlin
 * val userId = params["id"]  // String? type
 * val category = params["category"] ?: "general"
 * ```
 *
 * @param key Parameter name to retrieve
 * @return Parameter value as string, or null if not found
 * @see getInt for typed integer access
 * @see getOrDefault for access with fallback values
 * @since 1.0.0
     */
    operator fun get(key: String): String? = params[key]

    /**
 * Gets a parameter value with a fallback default.
 *
 * Returns the parameter value if present, otherwise returns the
 * specified default value. This is useful for optional parameters
 * with sensible defaults.
 *
 * ## Usage
 *
 * ```kotlin
 * val pageSize = params.getOrDefault("limit", "10")
 * val sortOrder = params.getOrDefault("sort", "asc")
 * val theme = params.getOrDefault("theme", "light")
 * ```
 *
 * @param key Parameter name to retrieve
 * @param defaultValue Value to return if parameter is not found
 * @return Parameter value or default value
 * @since 1.0.0
     */
    fun getOrDefault(key: String, defaultValue: String): String = params[key] ?: defaultValue

    /**
 * Returns the complete parameter map.
 *
 * Provides access to the underlying map containing all route parameters.
 * This is useful for scenarios requiring iteration over all parameters
 * or forwarding parameters to other components.
 *
 * ## Usage
 *
 * ```kotlin
 * val allParams = params.asMap()
 * for ((key, value) in allParams) {
 *     println("$key: $value")
 * }
 * ```
 *
 * @return Immutable map of all parameter names to values
 * @since 1.0.0
     */
    fun asMap(): Map<String, String> = params

    /**
 * Converts a parameter to an integer value.
 *
 * Attempts to parse the parameter as an integer. Returns null if the
 * parameter doesn't exist or cannot be parsed as a valid integer.
 *
 * ## Usage
 *
 * ```kotlin
 * val userId = params.getInt("id")
 * if (userId != null && userId > 0) {
 *     loadUser(userId)
 * }
 * ```
 *
 * @param key Parameter name to convert
 * @return Integer value or null if not found or invalid
 * @see getLong for 64-bit integer values
 * @since 1.0.0
     */
    fun getInt(key: String): Int? = params[key]?.toIntOrNull()

    /**
 * Converts a parameter to a long integer value.
 *
 * Attempts to parse the parameter as a 64-bit long integer. Returns
 * null if the parameter doesn't exist or cannot be parsed.
 *
 * ## Usage
 *
 * ```kotlin
 * val timestamp = params.getLong("ts")
 * if (timestamp != null) {
 *     val date = Date(timestamp)
 * }
 * ```
 *
 * @param key Parameter name to convert
 * @return Long value or null if not found or invalid
 * @see getInt for 32-bit integer values
 * @since 1.0.0
     */
    fun getLong(key: String): Long? = params[key]?.toLongOrNull()

    /**
 * Converts a parameter to a boolean value.
 *
 * Recognizes "true" and "false" (case-insensitive) as valid boolean
 * values. Returns null for any other string values or if the parameter
 * doesn't exist.
 *
 * ## Usage
 *
 * ```kotlin
 * val isEnabled = params.getBoolean("enabled") ?: false
 * val showDetails = params.getBoolean("details") ?: true
 * ```
 *
 * ## Recognized Values
 * - `"true"` → `true`
 * - `"false"` → `false`
 * - Any other value → `null`
 *
 * @param key Parameter name to convert
 * @return Boolean value or null if not found or not "true"/"false"
 * @since 1.0.0
     */
    fun getBoolean(key: String): Boolean? = params[key]?.let {
        when (it.lowercase()) {
            "true" -> true
            "false" -> false
            else -> null // Return null for other strings
        }
    }

    /**
 * Converts a parameter to a floating-point value.
 *
 * Attempts to parse the parameter as a 32-bit float. Returns null
 * if the parameter doesn't exist or cannot be parsed as a valid float.
 *
 * ## Usage
 *
 * ```kotlin
 * val rating = params.getFloat("rating") ?: 0.0f
 * val price = params.getFloat("price")
 * ```
 *
 * @param key Parameter name to convert
 * @return Float value or null if not found or invalid
 * @see getDouble for double-precision values
 * @since 1.0.0
     */
    fun getFloat(key: String): Float? = params[key]?.toFloatOrNull()

    /**
 * Converts a parameter to a double-precision floating-point value.
 *
 * Attempts to parse the parameter as a 64-bit double. Returns null
 * if the parameter doesn't exist or cannot be parsed as a valid double.
 *
 * ## Usage
 *
 * ```kotlin
 * val latitude = params.getDouble("lat")
 * val longitude = params.getDouble("lng")
 * if (latitude != null && longitude != null) {
 *     showLocation(latitude, longitude)
 * }
 * ```
 *
 * @param key Parameter name to convert
 * @return Double value or null if not found or invalid
 * @see getFloat for single-precision values
 * @since 1.0.0
     */
    fun getDouble(key: String): Double? = params[key]?.toDoubleOrNull()
}

// CompositionLocal for RouteParams
private val localRouteParams = CompositionLocal.compositionLocalOf<RouteParams?>(null)

/**
 * Access to the current route parameters.
 */
val LocalRouteParams = localRouteParams

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
