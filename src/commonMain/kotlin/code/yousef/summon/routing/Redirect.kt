package code.yousef.summon.routing

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LaunchedEffect

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
    // Assume Router.current provides access to the router instance via CompositionLocal
    // val router = Router.current

    // Correct LaunchedEffect syntax: use a single key (e.g., a Pair)
    // and then the suspending lambda block.
    LaunchedEffect(key = to to permanent) { // Keyed on a pair
        println("Redirect composable launched effect: Redirecting to '$to' (Permanent: $permanent)")
        // TODO: Get actual router instance and call navigate with replace option.
        // router.navigateTo(to, replace = permanent)
        // Router.navigateTo(to) // Commented out placeholder call
    }

    // This component renders no UI directly.
}

// Removed old Redirect class that implemented Composable:
// class Redirect(val to: String, val permanent: Boolean = false) : Composable {
//     override fun <T> compose(receiver: T): T {
//         // Logic was likely in Router/Platform implementation
//         return receiver
//     }
// } 
