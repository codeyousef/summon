package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable

/** Strict-CSP hydration metadata implementation. */
class StandardHydrationSupport : HydrationSupport {
    /**
     * Describes the hydration mode without executing the component tree on the server.
     * Component effects remain client-owned and can run only after the application unlocks.
     */
    override fun generateHydrationData(
        composable: @Composable () -> Unit,
        strategy: HydrationStrategy
    ): String = """{"version":1,"strategy":"${strategy.name}","components":[]}"""

    /**
     * Adds hydration markers.
     *
     * @param html The html value.
     * @param hydrationData The hydration data value.
     * @return The resulting value.
     */
    override fun addHydrationMarkers(html: String, hydrationData: String): String {
        val safeHydrationData = scriptSafeJson(hydrationData.trim())
        return buildString {
            append("<div data-summon-hydration=\"root\" data-summon-component=\"root\">\n")
            append(html)
            append("\n<script id=\"summon-hydration-data\" type=\"application/json\">")
            append(safeHydrationData)
            append("</script>")
            append("\n<script src=\"/summon-hydration.js\" defer></script>")
            append("\n</div>")
        }
    }
}
