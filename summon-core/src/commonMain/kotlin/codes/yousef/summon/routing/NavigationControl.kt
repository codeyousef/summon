package codes.yousef.summon.routing

/** Decision returned before an internal route transition. */
enum class NavigationDecision {
    /** The proceed navigation decision option. */
    PROCEED,
    /** The cancel navigation decision option. */
    CANCEL
}

/**
 * Intercepts route transitions that could discard local work. Returning [NavigationDecision.CANCEL]
 * leaves the destination pending. The application may encrypt and persist its draft, then call
 * [NavigationControl.continuePending], or keep editing and call [NavigationControl.cancelPending].
 */
fun interface NavigationInterceptor {
    /**
     * Executes the before navigate operation.
     *
     * @param from The from value.
     * @param to The to value.
     * @return The resulting value.
     */
    fun beforeNavigate(from: String, to: String): NavigationDecision
}

/** Contract for navigation control. */
interface NavigationControl {
    /** The property declaration value. */
    var interceptor: NavigationInterceptor?
    /** The property declaration value. */
    val pendingPath: String?
    /** Executes the continue pending operation. */
    fun continuePending()
    /** Cancels pending. */
    fun cancelPending()
}

/** Returns navigation control for routers that support transition interception. */
fun Router.navigationControl(): NavigationControl =
    this as? NavigationControl
        ?: error("This router does not support controlled navigation")
