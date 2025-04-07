package code.yousef.summon.ssr

import code.yousef.summon.runtime.Composable

/**
 * Standard implementation of hydration support for server-side rendering.
 * This implementation provides basic hydration functionality for Summon components.
 */
class StandardHydrationSupport : HydrationSupport {
    /**
     * Generate hydration data for client-side use
     *
     * @param composable The composable to generate hydration data for
     * @param strategy The hydration strategy to use
     * @return Client-side hydration data as a string (typically JSON)
     */
    override fun generateHydrationData(
        composable: Composable,
        strategy: HydrationStrategy
    ): String {
        // In a real implementation, this would analyze the composable and generate
        // serialized state data for client-side hydration
        
        // For now, we return a simple JSON object with the strategy
        return """
            {
                "strategy": "${strategy.name}",
                "timestamp": ${getCurrentTimestamp()},
                "componentId": "root"
            }
        """.trimIndent()
    }

    /**
     * Add hydration markers to rendered HTML
     *
     * @param html The rendered HTML
     * @param hydrationData The hydration data as a string
     * @return HTML with hydration markers
     */
    override fun addHydrationMarkers(html: String, hydrationData: String): String {
        // In a real implementation, this would parse the HTML and add data attributes
        // for hydration at specific points
        
        // For now, we simply wrap the HTML in a div with a data attribute
        return """
            <div data-summon-hydration="root">
                $html
                <script id="summon-hydration-data" type="application/json" style="display:none">
                    $hydrationData
                </script>
            </div>
        """.trimIndent()
    }
    
    /**
     * Get the current timestamp in a platform-independent way
     */
    private fun getCurrentTimestamp(): Long {
        // Using kotlin.time would be better, but this is a simple solution
        return kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds
    }
} 