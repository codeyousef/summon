package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable

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
        composable: @Composable () -> Unit,
        strategy: HydrationStrategy
    ): String {
        // Create a hydration context to track components
        val context = HydrationContext()

        // Track the composition to gather component information
        context.trackComposition { composable() }

        // Use the context's generateHydrationData method to generate the hydration data
        return context.generateHydrationData(strategy)
    }

    /**
     * Add hydration markers to rendered HTML
     *
     * @param html The rendered HTML
     * @param hydrationData The hydration data as a string
     * @return HTML with hydration markers
     */
    override fun addHydrationMarkers(html: String, hydrationData: String): String {
        // Parse the hydration data to identify components
        val hydrationJson = hydrationData.trim()

        // Create a container with hydration attributes
        val markedHtml = StringBuilder()

        // Add the root container with hydration attributes
        markedHtml.append("<div data-summon-hydration=\"root\" data-summon-component=\"root\">\n")

        // Add the HTML content
        markedHtml.append(html)

        // Add the hydration data script
        markedHtml.append("""
            <script id="summon-hydration-data" type="application/json" style="display:none">
                $hydrationJson
            </script>
        """.trimIndent())

        // Add the hydration initialization script
        markedHtml.append("""
            <script>
                // Initialize hydration when the DOM is ready
                document.addEventListener('DOMContentLoaded', function() {
                    if (window.SummonHydration) {
                        window.SummonHydration.hydrate();
                        console.log('Summon hydration initialized');
                    } else {
                        console.warn('Summon hydration script not loaded');
                    }
                });
            </script>
        """.trimIndent())

        // Close the container
        markedHtml.append("\n</div>")

        return markedHtml.toString()
    }

    /**
     * Get the current timestamp in a platform-independent way
     */
    private fun getCurrentTimestamp(): Long {
        // Using kotlin.time would be better, but this is a simple solution
        return kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds
    }
} 
