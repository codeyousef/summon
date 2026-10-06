package codes.yousef.summon.routing

/** Decision returned before an internal route transition. */
enum class NavigationDecision {
    PROCEED,
    CANCEL
}

/**
 * Intercepts route transitions that could discard local work. Returning [NavigationDecision.CANCEL]
 * leaves the destination pending. The application may encrypt and persist its draft, then call
 * [NavigationControl.continuePending], or keep editing and call [NavigationControl.cancelPending].
 */
fun interface NavigationInterceptor {
    fun beforeNavigate(from: String, to: String): NavigationDecision
}

interface NavigationControl {
    var interceptor: NavigationInterceptor?
    val pendingPath: String?
    fun continuePending()
    fun cancelPending()
}

/** Returns navigation control for routers that support transition interception. */
fun Router.navigationControl(): NavigationControl =
    this as? NavigationControl
        ?: error("This router does not support controlled navigation")
