package routing

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Interface for route guards.
 * Route guards can be used to protect routes based on certain conditions.
 */
interface RouteGuard {
    /**
     * Determines if the route can be activated.
     * @return true if the route can be activated, false otherwise.
     */
    fun canActivate(): Boolean
} 
