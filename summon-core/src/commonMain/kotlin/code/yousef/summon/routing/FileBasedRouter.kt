/**
 * # Summon Routing Package
 *
 * This package provides file-based routing inspired by Next.js for Summon applications.
 *
 * ## Overview
 *
 * The routing package implements a convention-over-configuration approach to navigation:
 *
 * - **File-Based Routing**: URL structure mirrors file system organization
 * - **Automatic Route Discovery**: Pages are automatically registered based on file structure
 * - **Cross-Platform Support**: Works in browser and server-side rendering contexts
 * - **Dynamic Routing**: Support for dynamic route parameters and nested routes
 * - **Type Safety**: Compile-time route validation and navigation helpers
 *
 * ## Key Components
 *
 * ### Core Router
 * - [FileBasedRouter] - Main router implementation using file-based conventions
 * - [Router] - Router interface for navigation and rendering
 * - [createFileBasedRouter] - Factory function for client-side routing
 * - [createFileBasedServerRouter] - Factory function for server-side routing
 *
 * ### Navigation
 * - [Link] - Declarative navigation component
 * - [NavLink] - Navigation with active state styling
 * - [History] - Browser history management
 * - [Location] - Current location and URL parameters
 *
 * ### Advanced Features
 * - [DeepLinking] - Deep link handling and URL parsing
 * - **Route Parameters** - Dynamic URL segments and query parameters
 * - **Nested Routing** - Hierarchical route organization
 * - **Route Guards** - Authentication and authorization checks
 *
 * ## File-Based Routing Convention
 *
 * Routes are defined by creating `.kt` files in the pages directory:
 *
 * ```
 * src/commonMain/kotlin/pages/
 * ├── Index.kt          → /
 * ├── About.kt          → /about
 * ├── Contact.kt        → /contact
 * ├── users/
 * │   ├── Index.kt      → /users
 * │   └── Profile.kt    → /users/profile
 * └── 404.kt           → Not found page
 * ```
 *
 * ## Usage Examples
 *
 * ### Basic Page Definition
 * ```kotlin
 * // src/commonMain/kotlin/pages/About.kt
 * @Composable
 * fun About() {
 *     Column {
 *         Text("About Us", style = Typography.h1)
 *         Text("Learn more about our company...")
 *     }
 * }
 * ```
 *
 * ### Navigation Between Pages
 * ```kotlin
 * @Composable
 * fun Navigation() {
 *     Row {
 *         Link(href = "/", label = "Home")
 *         Link(href = "/about", label = "About")
 *         Link(href = "/contact", label = "Contact")
 *     }
 * }
 * ```
 *
 * ### Router Setup
 * ```kotlin
 * @Composable
 * fun App() {
 *     val router = remember { createFileBasedRouter() }
 *
 *     router.create(initialPath = "/")
 * }
 * ```
 *
 * @since 1.0.0
 */
package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable

/**
 * File-based router that implements Next.js-style routing conventions.
 *
 * FileBasedRouter provides a declarative approach to routing where the file system
 * structure directly maps to URL routes. This eliminates the need for manual route
 * registration and provides a predictable, scalable routing system.
 *
 * ## File System Mapping
 *
 * The router automatically maps files to routes based on their location:
 * - `Index.kt` files map to the directory route
 * - File names become route segments
 * - Directory structure creates nested routes
 * - Special files like `404.kt` handle error cases
 *
 * ## Route Discovery
 *
 * Routes are discovered automatically during compilation:
 * - Source files are scanned for page components
 * - Route metadata is generated at build time
 * - Components are lazy-loaded for optimal performance
 * - Route mappings are cached for fast lookup
 *
 * ## Platform Integration
 *
 * The router adapts to different platform contexts:
 * - **Browser**: Uses History API for client-side navigation
 * - **Server**: Renders appropriate page for incoming requests
 * - **Hybrid**: Supports server-side rendering with client hydration
 *
 * ## Navigation Features
 *
 * - **Programmatic Navigation**: Navigate via [navigate] method
 * - **Declarative Navigation**: Use [Link] components for navigation
 * - **History Management**: Supports browser back/forward buttons
 * - **Deep Linking**: Direct URL access to any page
 * - **Route Parameters**: Dynamic segments in URLs
 *
 * ## Example Implementation
 *
 * ```kotlin
 * // Application setup
 * @Composable
 * fun App() {
 *     val router = remember { createFileBasedRouter() }
 *
 *     ThemeProvider {
 *         Layout {
 *             router.create(initialPath = window.location.pathname)
 *         }
 *     }
 * }
 *
 * // Page component
 * @Composable
 * fun UserProfile() {
 *     val router = LocalRouter.current
 *     val userId = router.getParameter("id")
 *
 *     Column {
 *         Text("User Profile: $userId")
 *         Button(
 *             onClick = { router.navigate("/users") },
 *             label = "Back to Users"
 *         )
 *     }
 * }
 * ```
 *
 * ## Performance Characteristics
 *
 * - **Code Splitting**: Pages are loaded on-demand
 * - **Route Caching**: Discovered routes are cached
 * - **Minimal Bundle**: Only current page code is loaded initially
 * - **Fast Navigation**: Client-side transitions are immediate
 *
 * @see Router
 * @see createFileBasedRouter
 * @see createFileBasedServerRouter
 * @since 1.0.0
 */
expect class FileBasedRouter() : Router {
    /**
     * Navigates to the specified path.
     *
     * Performs client-side navigation to the given path. The navigation
     * can optionally update the browser history stack.
     *
     * @param path Target URL path to navigate to
     * @param pushState Whether to add entry to browser history (default: true)
     * @see Router.navigate
     */
    override fun navigate(path: String, pushState: Boolean)

    /**
     * Creates and renders the router UI for the specified initial path.
     *
     * This composable function sets up the router and renders the appropriate
     * page component based on the current or initial path.
     *
     * @param initialPath Starting path for the router
     * @see Router.create
     */
    @Composable
    override fun create(initialPath: String)

    /**
     * The current active path in the router.
     *
     * This property reflects the current URL path being displayed by the router.
     * It updates automatically when navigation occurs.
     *
     * @return Current URL path
     * @see Router.currentPath
     */
    override val currentPath: String

    /**
     * Loads and registers pages from the file system structure.
     *
     * This method scans the pages directory and builds the route mapping
     * based on the file structure. It's typically called automatically
     * during router initialization.
     *
     * ## Discovery Process
     *
     * 1. Scans the pages directory for Kotlin files
     * 2. Maps file paths to URL routes
     * 3. Registers page components for lazy loading
     * 4. Builds route parameter extraction rules
     */
    fun loadPages()
}

/**
 * Creates a file-based router for client-side use.
 *
 * This factory function creates a router instance configured for browser
 * environments with client-side navigation capabilities.
 *
 * The returned router:
 * - Automatically discovers routes from the file system
 * - Integrates with browser History API
 * - Supports client-side navigation
 * - Handles deep linking and URL changes
 *
 * ## Usage
 *
 * ```kotlin
 * @Composable
 * fun App() {
 *     val router = remember { createFileBasedRouter() }
 *
 *     router.create(initialPath = getCurrentPath())
 * }
 * ```
 *
 * @return Router instance configured for client-side use
 * @see FileBasedRouter
 * @since 1.0.0
 */
expect fun createFileBasedRouter(): Router

/**
 * Creates a file-based router for server-side rendering.
 *
 * This factory function creates a router instance specifically configured
 * for server-side rendering scenarios where the target path is known
 * at render time.
 *
 * The returned router:
 * - Renders the specific page for the given path
 * - Does not include client-side navigation features
 * - Optimized for server-side rendering performance
 * - Supports static generation scenarios
 *
 * ## Usage
 *
 * ```kotlin
 * // In server endpoint
 * fun handleRequest(request: HttpRequest): String {
 *     val router = createFileBasedServerRouter(request.path)
 *     return renderToString {
 *         router.create(request.path)
 *     }
 * }
 * ```
 *
 * @param path The request path to render
 * @return Router instance configured for server-side rendering
 * @see FileBasedRouter
 * @since 1.0.0
 */
expect fun createFileBasedServerRouter(path: String): Router 