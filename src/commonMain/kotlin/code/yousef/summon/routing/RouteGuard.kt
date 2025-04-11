package code.yousef.summon.routing

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Interface for route guards.
 * Route guards can be used to protect routes based on certain conditions.
 */
interface RouteGuard {
    /**
     * Determines if the route can be activated.
     * @param route The route being activated
     * @param params The route parameters
     * @return A GuardResult indicating whether the route can be activated
     */
    fun canActivate(route: Route, params: RouteParams): GuardResult
}

/**
 * Result of a route guard check.
 */
sealed class GuardResult {
    /**
     * Allow access to the route.
     */
    object Allow : GuardResult()

    /**
     * Redirect to another route.
     * @param path The path to redirect to
     */
    class Redirect(val path: String) : GuardResult()

    /**
     * Deny access to the route.
     * This will typically show a not-found or unauthorized page.
     */
    object Deny : GuardResult()
} 
