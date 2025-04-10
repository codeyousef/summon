package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LaunchedEffect

// Simple router implementation for testing
private val defaultRouter = object {
    fun navigate(path: String, pushState: Boolean = true) {
        println("Navigating to: $path (pushState: $pushState)")
    }
}

/**
 * A composable that performs a client-side redirect when it enters the composition.
 *
 * @param to The target path to redirect to.
 * @param permanent If true, indicates a permanent redirect (may influence browser history or SEO).
 */
@Composable
fun Redirect(
    to: String,
    permanent: Boolean = false
) {
    // Get current router - in real implementation would use CompositionLocal
    val router = defaultRouter

    // Use LaunchedEffect to perform the redirect when the component is composed
    LaunchedEffect(key = to to permanent) {
        println("Redirect composable launched effect: Redirecting to '$to' (Permanent: $permanent)")
        
        // Use the router instance to perform navigation if available
        router.navigate(to, pushState = !permanent)
    }

    // This component renders no UI directly.
}

// Simple LaunchedEffect implementation for testing
private fun LaunchedEffect(key: Any, block: () -> Unit) {
    // In a real implementation, this would track key and execute the block
    block()
}

// Removed old Redirect class that implemented Composable:
// class Redirect(val to: String, val permanent: Boolean = false) : Composable {
//     override fun <T> compose(receiver: T): T {
//         // Logic was likely in Router/Platform implementation
//         return receiver
//     }
// } 
