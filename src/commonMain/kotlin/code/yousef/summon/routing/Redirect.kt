package code.yousef.summon.routing

import code.yousef.summon.annotation.Composable

/**
 * A composable that performs a client-side redirect when composed.
 * Useful for declarative redirection based on application state or logic.
 * This composable does not render any UI itself.
 *
 * @param to The target path to navigate to.
 * @param replace If true, replaces the current entry in the history stack instead of pushing a new one.
 */
@Composable
fun Redirect(
    to: String,
    replace: Boolean = false
) {
    // Get the current router instance from the composition context
    // This requires RouterContext to be available, likely as a CompositionLocal.
    // val router = RouterContext.current ?: error("RouterContext not found. Redirect must be used within a Router.")
    val router = RouterContext.current // Assuming RouterContext.current works here

    // Perform the navigation side-effect
    // Consider using LaunchedEffect or similar for side effects in Compose?
    // For now, direct call matches old behavior.
    router?.navigate(to, !replace) 

    // This composable emits nothing to the UI tree.
} 