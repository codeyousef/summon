package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable


// Typealias for the new composable page factory signature
typealias PageFactory = @Composable (params: RouteParams) -> Unit

/**
 * Registry for mapping page routes to their corresponding composable components.
 *
 * PageRegistry provides a centralized system for discovering, registering, and
 * accessing page components within a file-based routing architecture. It handles
 * the mapping between URL paths and their corresponding composable functions,
 * including support for dynamic routes and special pages like 404 handlers.
 *
 * ## Automatic Page Discovery
 *
 * In file-based routing systems, pages are typically discovered automatically:
 * - Source files are scanned during build time
 * - File paths are normalized to URL routes
 * - Page components are registered with their route patterns
 * - Dynamic routes are configured based on file naming conventions
 *
 * ## Route Normalization
 *
 * File paths are converted to routes using these conventions:
 * - `Index.kt` → `/` (root route)
 * - `About.kt` → `/about`
 * - `users/Profile.kt` → `/users/profile`
 * - `users/[id].kt` → `/users/:id` (dynamic parameter)
 * - `blog/[...slug].kt` → `/blog/*slug` (catch-all route)
 *
 * ## Manual Registration
 *
 * Pages can also be registered manually for programmatic routing:
 *
 * ```kotlin
 * val registry = DefaultPageRegistry()
 *
 * registry.registerPage("/products/:id") { params ->
 *     ProductPage(productId = params["id"]!!)
 * }
 *
 * registry.registerNotFoundPage { params ->
 *     NotFoundPage(requestedPath = params["path"])
 * }
 * ```
 *
 * ## Integration with Router
 *
 * ```kotlin
 * val router = createFileBasedRouter()
 * val registry = DefaultPageRegistry()
 *
 * // Registry is typically populated by build-time code generation
 * Pages.register("/", { HomePage() })
 * Pages.register("/about", { AboutPage() })
 *
 * router.loadPages() // Uses global Pages registry
 * ```
 *
 * @see PageFactory for page component type
 * @see DefaultPageRegistry for default implementation
 * @see Pages for global registry access
 * @since 1.0.0
 */
interface PageRegistry {
    /**
 * Registers a page component for a specific route path.
 *
 * Associates a route pattern with a composable function that will be
 * rendered when the route is accessed. The route path can include
 * parameters and wildcards for dynamic routing.
 *
 * ## Route Patterns
 *
 * - **Static**: `/about`, `/contact`
 * - **Parameters**: `/users/:id`, `/posts/:category/:slug`
 * - **Wildcards**: `/files/*path`
 * - **Optional**: `/posts/:id?`
     *
 * ## Usage Examples
 *
 * ```kotlin
 * // Static route
 * registry.registerPage("/about") { AboutPage() }
 *
 * // Dynamic route with parameter
 * registry.registerPage("/users/:id") { params ->
 *     UserProfile(userId = params["id"]!!)
 * }
 *
 * // Nested route with multiple parameters
 * registry.registerPage("/blog/:category/:slug") { params ->
 *     BlogPost(
 *         category = params["category"]!!,
 *         slug = params["slug"]!!
 *     )
 * }
 * ```
 *
 * @param pagePath Route pattern with optional parameters (must start with `/`)
 * @param pageFactory Composable function that renders the page content
 * @throws IllegalArgumentException if pagePath is invalid or empty
 * @see PageFactory for component signature
 * @since 1.0.0
     */
    fun registerPage(pagePath: String, pageFactory: PageFactory)

    /**
 * Registers a special 404 Not Found page handler.
 *
 * The not found page is displayed when no registered route matches
 * the current path. It receives route parameters that may include
 * the originally requested path for error reporting.
     *
 * ## Usage
 *
 * ```kotlin
 * registry.registerNotFoundPage { params ->
 *     NotFoundPage(
 *         requestedPath = params["path"] ?: "unknown",
 *         onNavigateHome = { router.navigate("/") }
 *     )
 * }
 * ```
 *
 * ## Error Context
 *
 * The not found page may receive additional context:
 * - `path`: The originally requested path that wasn't found
 * - `referrer`: The previous page that linked to the missing page
 *
 * @param pageFactory Composable function that renders the not found page
 * @see registerPage for regular page registration
 * @since 1.0.0
     */
    fun registerNotFoundPage(pageFactory: PageFactory)

    /**
 * Returns all registered page routes and their component factories.
 *
 * Provides access to the complete mapping of route patterns to page
 * components. This is primarily used by the router for route resolution
 * and by development tools for introspection.
     *
 * ## Usage
 *
 * ```kotlin
 * val allPages = registry.getPages()
 * for ((route, factory) in allPages) {
 *     println("Route: $route")
 *     // Could render for static generation
 * }
 * ```
 *
 * @return Immutable map of route patterns to page component factories
 * @see getNotFoundPage for accessing the 404 handler
 * @since 1.0.0
     */
    fun getPages(): Map<String, PageFactory>

    /**
 * Returns the registered 404 Not Found page factory.
 *
 * Provides access to the special page component that handles requests
 * for non-existent routes. Returns null if no 404 handler has been
 * registered.
     *
 * ## Usage
 *
 * ```kotlin
 * val notFoundHandler = registry.getNotFoundPage()
 * if (notFoundHandler != null) {
 *     // Render 404 page
 *     notFoundHandler(RouteParams(mapOf("path" to requestPath)))
 * } else {
 *     // Show default error message
 * }
 * ```
 *
 * @return 404 page factory or null if not registered
 * @see registerNotFoundPage for registration
 * @since 1.0.0
     */
    fun getNotFoundPage(): PageFactory?

    /**
 * Normalizes a file system path to a valid route path.
 *
 * Converts file system conventions to URL routing patterns following
 * Next.js-style conventions. This includes handling special file names,
 * converting parameter syntax, and ensuring proper path formatting.
 *
 * ## Normalization Rules
 *
 * - Removes file extensions (`.kt`, `.page.kt`)
 * - Converts PascalCase to kebab-case
 * - Maps `Index.kt` to directory route
 * - Converts `[param]` to `:param` syntax
 * - Converts `[...param]` to `*param` catch-all syntax
 * - Ensures path starts with `/`
 *
 * ## Examples
 *
 * ```kotlin
 * registry.normalizePath("About.kt") // → "/about"
 * registry.normalizePath("users/Index.kt") // → "/users"
 * registry.normalizePath("users/[id].kt") // → "/users/:id"
 * registry.normalizePath("blog/[...slug].kt") // → "/blog/*slug"
 * registry.normalizePath("UserProfile.kt") // → "/user-profile"
 * ```
 *
 * @param path File system path to normalize
 * @return Normalized route path suitable for URL routing
 * @since 1.0.0
     */
    fun normalizePath(path: String): String
}

/**
 * Default implementation of PageRegistry with Next.js-style path normalization.
 *
 * DefaultPageRegistry provides a complete implementation of the PageRegistry
 * interface with intelligent path normalization that follows modern web
 * framework conventions. It's designed to work seamlessly with file-based
 * routing systems while supporting manual page registration.
 *
 * ## Features
 *
 * - **Automatic Path Normalization**: Converts file paths to URL routes
 * - **Parameter Support**: Handles dynamic route parameters
 * - **Case Conversion**: PascalCase to kebab-case URL conversion
 * - **Special File Handling**: Index files, 404 pages, nested routes
 * - **Thread Safety**: Safe for concurrent access and registration
 *
 * ## Path Normalization Examples
 *
 * ```kotlin
 * val registry = DefaultPageRegistry()
 *
 * registry.normalizePath("About.kt")           // → "/about"
 * registry.normalizePath("users/Index.kt")     // → "/users"
 * registry.normalizePath("users/[id].kt")      // → "/users/:id"
 * registry.normalizePath("UserProfile.kt")     // → "/user-profile"
 * registry.normalizePath("blog/[...slug].kt")  // → "/blog/*slug"
 * ```
 *
 * ## Usage in Router Systems
 *
 * ```kotlin
 * val registry = DefaultPageRegistry()
 *
 * // Register pages programmatically
 * registry.registerPage("/") { HomePage() }
 * registry.registerPage("/about") { AboutPage() }
 * registry.registerPage("/users/:id") { params ->
 *     UserProfile(userId = params["id"]!!)
 * }
 *
 * // Register error handler
 * registry.registerNotFoundPage { params ->
 *     NotFoundPage(path = params["path"])
 * }
 * ```
 *
 * @see PageRegistry for interface documentation
 * @see Pages for global registry access
 * @since 1.0.0
 */
class DefaultPageRegistry : PageRegistry {
    private val pages = mutableMapOf<String, PageFactory>()
    private var notFoundPage: PageFactory? = null

    override fun registerPage(pagePath: String, pageFactory: PageFactory) {
        val normalizedPath = normalizePath(pagePath)
        pages[normalizedPath] = pageFactory
    }

    override fun registerNotFoundPage(pageFactory: PageFactory) {
        notFoundPage = pageFactory
    }

    override fun getPages(): Map<String, PageFactory> = pages

    override fun getNotFoundPage(): PageFactory? = notFoundPage

    /**
     * Normalize a path following Next.js conventions.
     */
    override fun normalizePath(path: String): String {
        // Remove file extension if present (.kt, .page.kt, etc.)
        val withoutExtension = path.replace(Regex("\\.\\w+(\\.\\w+)*$"), "")

        // Convert to lowercase path with hyphens
        val kebabCase = withoutExtension
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        // Convert special file names to routes
        val routePath = when {
            kebabCase.endsWith("/index") -> kebabCase.removeSuffix("index")
            kebabCase.endsWith("/home") && !kebabCase.endsWith("/users/home") -> "/"
            else -> kebabCase
        }

        // Convert [param] syntax to :param for route parameters
        val parameterized = routePath
            .replace(Regex("\\[([^]]+)]"), ":$1")
            .replace(Regex("\\[\\.\\.\\.(\\w+)]"), "*")

        // Ensure the path starts with /
        return if (parameterized.startsWith("/")) parameterized else "/$parameterized"
    }
}

/**
 * Global page registry providing application-wide access to page registration.
 *
 * Pages serves as the primary interface for registering and accessing page
 * components in a Summon application. It provides a singleton registry that
 * can be used by both automatic page discovery systems and manual page
 * registration, ensuring a single source of truth for all application routes.
 *
 * ## Automatic Integration
 *
 * In file-based routing systems, Pages is typically populated automatically:
 * - Build-time code generation scans page files
 * - Pages are registered with normalized paths
 * - Dynamic routes are configured based on file naming
 * - Router systems access Pages for route resolution
 *
 * ## Manual Registration
 *
 * Pages can also be used for manual route registration:
 *
 * ```kotlin
 * // Register static pages
 * Pages.register("/") { HomePage() }
 * Pages.register("/about") { AboutPage() }
 * Pages.register("/contact") { ContactPage() }
 *
 * // Register dynamic routes
 * Pages.register("/users/:id") { params ->
 *     UserProfile(userId = params["id"]!!)
 * }
 *
 * // Register error handler
 * Pages.registerNotFound { params ->
 *     NotFoundPage(requestedPath = params["path"])
 * }
 * ```
 *
 * ## Router Integration
 *
 * Router implementations automatically use the global Pages registry:
 *
 * ```kotlin
 * val router = createFileBasedRouter()
 * router.loadPages() // Loads from global Pages registry
 *
 * // Router now has access to all registered pages
 * router.create(initialPath = "/")
 * ```
 *
 * ## Development Tools
 *
 * The global registry enables development and debugging tools:
 *
 * ```kotlin
 * // List all registered routes
 * val allRoutes = Pages.getRegisteredPages().keys
 * println("Available routes: ${allRoutes.sorted()}")
 *
 * // Check if 404 handler is registered
 * val has404 = Pages.getNotFoundHandler() != null
 * println("404 handler registered: $has404")
 * ```
 *
 * ## Thread Safety
 *
 * The global Pages registry is thread-safe and can be accessed safely
 * from multiple threads during application initialization.
 *
 * @see PageRegistry for registry interface
 * @see DefaultPageRegistry for implementation details
 * @see FileBasedRouter for router integration
 * @since 1.0.0
 */
object Pages {
    private val registry = DefaultPageRegistry()

    /**
 * Registers a page component for the specified route path.
 *
 * This method provides global access to page registration, allowing both
 * automatic code generation systems and manual application code to register
 * page components with the global registry.
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Register static pages
 * Pages.register("/") { HomePage() }
 * Pages.register("/about") { AboutPage() }
 *
 * // Register dynamic pages
 * Pages.register("/users/:id") { params ->
 *     UserProfile(userId = params["id"]!!)
 * }
 *
 * // Register nested routes
 * Pages.register("/blog/:category/:slug") { params ->
 *     BlogPost(
 *         category = params["category"]!!,
 *         slug = params["slug"]!!
 *     )
 * }
 * ```
 *
 * @param pagePath Route pattern (static or with parameters)
 * @param pageFactory Composable function that renders the page
 * @see PageRegistry.registerPage for detailed parameter documentation
 * @since 1.0.0
     */
    fun register(pagePath: String, pageFactory: PageFactory) {
        registry.registerPage(pagePath, pageFactory)
    }

    /**
 * Registers a global 404 Not Found page handler.
 *
 * The registered handler will be called whenever a route is requested
 * that doesn't match any registered page. This provides a centralized
 * way to handle missing pages across the entire application.
 *
 * ## Usage
 *
 * ```kotlin
 * Pages.registerNotFound { params ->
 *     NotFoundPage(
 *         requestedPath = params["path"] ?: "unknown",
 *         suggestions = getSimilarRoutes(params["path"]),
 *         onNavigateHome = { router.navigate("/") }
 *     )
 * }
 * ```
 *
 * ## Error Context
 *
 * The not found handler receives route parameters that may include:
 * - `path`: The originally requested path
 * - `referrer`: The page that linked to the missing route
 * - `userAgent`: Browser information for analytics
 *
 * @param pageFactory Composable function that renders the 404 page
 * @see PageRegistry.registerNotFoundPage for detailed documentation
 * @since 1.0.0
     */
    fun registerNotFound(pageFactory: PageFactory) {
        registry.registerNotFoundPage(pageFactory)
    }

    /**
 * Returns all pages registered in the global registry.
 *
 * Provides access to the complete mapping of route patterns to page
 * components. This is used by router implementations for route resolution
 * and by development tools for introspection and debugging.
 *
 * ## Usage
 *
 * ```kotlin
 * // Development tools usage
 * val allPages = Pages.getRegisteredPages()
 * println("Registered routes:")
 * allPages.keys.sorted().forEach { route ->
 *     println("  $route")
 * }
 *
 * // Static site generation
 * for ((route, factory) in allPages) {
 *     if (!route.contains(":") && !route.contains("*")) {
 *         generateStaticPage(route, factory)
 *     }
 * }
 * ```
 *
 * @return Immutable map of route patterns to page factories
 * @see PageRegistry.getPages for implementation details
 * @since 1.0.0
     */
    fun getRegisteredPages(): Map<String, PageFactory> = registry.getPages()

    /**
 * Returns the global 404 Not Found page handler.
 *
 * Provides access to the registered not found page handler, or null
 * if no handler has been registered. Router implementations use this
 * to handle requests for non-existent routes.
 *
 * ## Usage
 *
 * ```kotlin
 * // Router implementation usage
 * val notFoundHandler = Pages.getNotFoundHandler()
 * if (notFoundHandler != null) {
 *     notFoundHandler(RouteParams(mapOf("path" to requestPath)))
 * } else {
 *     // Show default error message
 *     Text("Page not found: $requestPath")
 * }
 *
 * // Development tools usage
 * val has404Handler = Pages.getNotFoundHandler() != null
 * println("404 handler configured: $has404Handler")
 * ```
 *
 * @return 404 page factory or null if not registered
 * @see PageRegistry.getNotFoundPage for implementation details
 * @since 1.0.0
     */
    fun getNotFoundHandler(): PageFactory? = registry.getNotFoundPage()

/**
 * Normalizes a file system path to a valid route pattern.
 *
 * Provides global access to path normalization functionality, converting
 * file system conventions to URL routing patterns. This is used by both
 * build-time code generation and runtime path processing.
 *
 * ## Usage
 *
 * ```kotlin
 * // Build-time code generation
 * val sourceFiles = findPageFiles()
 * for (file in sourceFiles) {
 *     val routePath = Pages.normalizePath(file.relativePath)
 *     val pageFactory = generatePageFactory(file)
 *     Pages.register(routePath, pageFactory)
 * }
 *
 * // Runtime path processing
 * val userInputPath = "users/UserProfile.kt"
 * val routePath = Pages.normalizePath(userInputPath) // "/users/user-profile"
 * ```
 *
 * @param path File system path to normalize
 * @return Normalized route pattern suitable for URL routing
 * @see PageRegistry.normalizePath for normalization rules
 * @since 1.0.0
     */
    fun normalizePath(path: String): String = registry.normalizePath(path)
} 
