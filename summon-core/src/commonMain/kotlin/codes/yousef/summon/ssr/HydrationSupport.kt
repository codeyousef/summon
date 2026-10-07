package codes.yousef.summon.ssr

import codes.yousef.summon.security.PublicHydrationState

/** Utilities for producing inert hydration metadata plus the first-party client bundle. */
object HydrationUtils {
    /**
     * Generates bounded hydration markup without executing the component tree on the server.
     * Only explicitly classified public state may cross the SSR boundary.
     */
    fun generateHydrationMarkup(
        publicState: PublicHydrationState? = null,
        strategy: HydrationStrategy = HydrationStrategy.FULL
    ): String {
        val stateJson = publicState?.json?.let(::scriptSafeJson) ?: "null"
        val hydrationData = scriptSafeJson(
            """{"version":1,"strategy":"${strategy.name}","components":[],"publicState":$stateJson}"""
        )
        return buildString {
            append("<script id=\"__SUMMON_HYDRATION_DATA__\" type=\"application/json\">")
            append(hydrationData)
            append("</script>\n")
            append("<script src=\"/summon-hydration.js\" defer></script>")
        }
    }
}
