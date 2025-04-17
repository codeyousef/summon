package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.remember

/**
 * A composable that performs a client-side redirect when it enters the composition.
 *
 * @param to The target path to redirect to.
 * @param permanent If true, indicates a permanent redirect (may influence browser history or SEO).
 * @param condition Optional condition that must be true for the redirect to occur.
 * @param onRedirect Optional callback that is invoked when the redirect occurs.
 */
@Composable
fun Redirect(
    to: String,
    permanent: Boolean = false,
    condition: Boolean = true,
    onRedirect: (() -> Unit)? = null
) {
    // Get current router from CompositionLocal
    // In the actual implementation, we would use LocalRouter.current
    // but for now we'll use a simple approach
    val router = getDefaultRouter()

    // Remember the redirect parameters to avoid unnecessary recompositions
    val redirectParams = remember(to, permanent, condition) {
        RedirectParams(to, permanent, condition)
    }

    // Use LaunchedEffect to perform the redirect when the component is composed
    // and the condition is met
    LaunchedEffect(redirectParams) {
        if (redirectParams.condition) {
            // Log the redirect
            println("Redirecting to '${redirectParams.to}' (Permanent: ${redirectParams.permanent})")

            // Use the router instance to perform navigation if available
            router?.navigate(redirectParams.to, pushState = !redirectParams.permanent)

            // Call the onRedirect callback if provided
            onRedirect?.invoke()

            // For permanent redirects, we might want to update metadata for SEO
            if (redirectParams.permanent) {
                updateRedirectMetadata(redirectParams.to)
            }
        }
    }

    // This component renders no UI directly.
}

/**
 * Parameters for a redirect operation.
 * Used to avoid unnecessary recompositions.
 */
private data class RedirectParams(
    val to: String,
    val permanent: Boolean,
    val condition: Boolean
)

/**
 * Server-side redirect that can be used outside of composable functions.
 * This is useful for programmatic redirects in response to events.
 *
 * @param to The target path to redirect to.
 * @param permanent If true, indicates a permanent redirect (HTTP 301 vs 302).
 * @param router Optional router instance to use for navigation.
 * @return True if the redirect was successful, false otherwise.
 */
fun redirectTo(
    to: String,
    permanent: Boolean = false,
    router: Router? = null
): Boolean {
    // Get the router instance
    val routerInstance = router ?: getDefaultRouter()

    // Perform the redirect
    return try {
        routerInstance?.navigate(to, pushState = !permanent)
        true
    } catch (e: Exception) {
        println("Error during redirect: ${e.message}")
        false
    }
}

/**
 * Conditional redirect that only redirects if the condition is met.
 * This is useful for programmatic redirects with conditions.
 *
 * @param to The target path to redirect to.
 * @param condition The condition that must be true for the redirect to occur.
 * @param permanent If true, indicates a permanent redirect.
 * @param router Optional router instance to use for navigation.
 * @return True if the redirect was successful, false otherwise.
 */
fun redirectIf(
    to: String,
    condition: Boolean,
    permanent: Boolean = false,
    router: Router? = null
): Boolean {
    return if (condition) {
        redirectTo(to, permanent, router)
    } else {
        false
    }
}

/**
 * Get the default router instance.
 * This is used when no router is provided to redirectTo or redirectIf.
 */
private fun getDefaultRouter(): Router? {
    // TODO: provide a real implementation
    // In a real implementation, this would get the current router from a global context
    // For now, we'll return null and let the caller handle it
    return null
}

/**
 * Update metadata for SEO when performing a permanent redirect.
 * This is used to inform search engines about the redirect.
 */
private fun updateRedirectMetadata(to: String) {
    // TODO: provide a real implementation
    // In a real implementation, this would add canonical link and meta refresh tags
    // For now, we'll just log the action
    println("Updating redirect metadata for SEO: $to")
}
