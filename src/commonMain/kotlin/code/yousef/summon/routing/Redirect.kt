package code.yousef.summon.routing

import code.yousef.summon.Composable
import code.yousef.summon.routing.Redirect.Companion.to

/**
 * A component that redirects to another route.
 * Useful for declarative redirection based on conditions.
 *
 * @param to Path to redirect to
 * @param replace Whether to replace the current history entry (default: false)
 */
class Redirect(
    val to: String,
    val replace: Boolean = false
) : Composable {
    override fun <T> compose(receiver: T): T {
        // Get the current router instance
        val router = RouterContext.current

        // Perform the redirect if a router is available
        router?.navigate(to, !replace)

        // Just return the receiver without rendering anything
        return receiver
    }

    companion object {
        /**
         * Creates a redirect component.
         *
         * @param to Path to redirect to
         * @param replace Whether to replace the current history entry
         * @return A new Redirect instance
         */
        fun to(to: String, replace: Boolean = false): Redirect {
            return Redirect(to, replace)
        }
    }
} 