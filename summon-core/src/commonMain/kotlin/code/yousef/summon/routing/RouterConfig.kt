package codes.yousef.summon.routing

/**
 * Configuration options for the router.
 */
class RouterConfig {
    /**
     * Base path for all routes. Useful when your app is not hosted at the root URL.
     * For example, if your app is at "example.com/my-app/", set basePath to "/my-app".
     */
    var basePath: String = ""

    /**
     * If true, uses hash-based URLs (e.g., "/#/about" instead of "/about").
     * This is useful for static hosting where server-side URL rewriting is not available.
     */
    var useHashMode: Boolean = false

    /**
     * Defines the scroll behavior when navigating between routes.
     */
    var scrollBehavior: ScrollBehavior = ScrollBehavior.AUTO

    /**
     * If true, route matching is case-sensitive.
     * For example, "/Users" and "/users" would be treated as different routes.
     */
    var caseSensitive: Boolean = false
}

/**
 * Scroll behavior options when navigating between routes.
 */
enum class ScrollBehavior {
    /**
     * Automatically scrolls to the top on navigation.
     */
    AUTO,

    /**
     * Leaves scroll control to the application.
     */
    MANUAL,

    /**
     * Preserves the scroll position when navigating.
     */
    PRESERVE,

    /**
     * Smoothly scrolls to the top on navigation.
     */
    SMOOTH
} 