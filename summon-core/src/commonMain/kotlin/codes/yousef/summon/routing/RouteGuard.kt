package codes.yousef.summon.routing

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
 * Result of a client-side presentation guard. Guards prevent private UI from mounting, but they
 * never replace server-side authorization.
 */
sealed class GuardResult {
    /** Provides allow operations. */
    data object Allow : GuardResult()
    /** Provides loading operations. */
    data object Loading : GuardResult()
    /** Provides locked operations. */
    data object Locked : GuardResult()
    /** Provides feature disabled operations. */
    data object FeatureDisabled : GuardResult()
    /** Provides permission denied operations. */
    data object PermissionDenied : GuardResult()
    /**
     * Represents redirect.
     *
     * @property path Target path.
     */
    data class Redirect(val path: String) : GuardResult()
    /** Provides deny operations. */
    data object Deny : GuardResult()

    /** The property declaration value. */
    val safeReason: String
        get() = when (this) {
            Allow -> "allowed"
            Loading -> "loading"
            Locked -> "locked"
            FeatureDisabled -> "feature unavailable"
            PermissionDenied, Deny -> "permission denied"
            is Redirect -> "redirecting"
        }
}

internal fun RouteDefinition.evaluateGuards(params: RouteParams): GuardResult {
    if (guards.isEmpty()) return GuardResult.Allow
    val route = Route(path) { routeParams -> { content(routeParams) } }
    for (guard in guards) {
        val result = guard.canActivate(route, params)
        if (result != GuardResult.Allow) return result
    }
    return GuardResult.Allow
}
