package code.yousef.summon.ssr

import code.yousef.summon.Composable

/**
 * Standard implementation of hydration support for client-side reactivation of server-rendered HTML
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
        // In a real implementation, this would analyze the composable and its state
        // For this example, we'll return a placeholder
        return when (strategy) {
            HydrationStrategy.NONE -> "{}"
            HydrationStrategy.FULL -> """{"type":"full","components":[]}"""
            HydrationStrategy.PARTIAL -> """{"type":"partial","components":[]}"""
            HydrationStrategy.PROGRESSIVE -> """{"type":"progressive","components":[]}"""
        }
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
        // For this example, we'll add a hidden script tag with the hydration data
        return """
            <div data-summon-hydration="container">
                $html
                <script type="application/json" id="summon-hydration-data" style="display:none">
                    $hydrationData
                </script>
            </div>
        """.trimIndent()
    }
}

/**
 * Implementation of partial hydration support
 * Allows selective hydration of only interactive elements
 */
class PartialHydrationSupport : HydrationSupport {
    /**
     * Generate hydration data focusing only on interactive components
     *
     * @param composable The composable to generate hydration data for
     * @param strategy The hydration strategy to use (defaults to PARTIAL)
     * @return Client-side hydration data as a string (typically JSON)
     */
    override fun generateHydrationData(
        composable: Composable,
        strategy: HydrationStrategy
    ): String {
        // Force the strategy to PARTIAL
        val actualStrategy = HydrationStrategy.PARTIAL

        // In a real implementation, this would analyze the composable to find interactive elements
        // For this example, we'll return a placeholder
        return """
            {
                "type": "partial",
                "components": [
                    {"id": "button-1", "type": "Button", "listeners": ["click"]},
                    {"id": "input-1", "type": "TextField", "listeners": ["input", "focus", "blur"]}
                ]
            }
        """.trimIndent()
    }

    /**
     * Add hydration markers specifically to interactive elements
     *
     * @param html The rendered HTML
     * @param hydrationData The hydration data as a string
     * @return HTML with hydration markers on interactive elements
     */
    override fun addHydrationMarkers(html: String, hydrationData: String): String {
        // In a real implementation, this would parse the HTML and add data attributes
        // specifically to interactive elements

        // For this example, we'll add a hidden script tag with the hydration data
        // and pretend we've added the necessary attributes to the elements
        return """
            <div data-summon-hydration="container" data-hydration-strategy="partial">
                $html
                <script type="application/json" id="summon-hydration-data" style="display:none">
                    $hydrationData
                </script>
            </div>
        """.trimIndent()
    }
}

/**
 * Implementation of progressive hydration support
 * Allows hydration based on visibility or other triggers
 */
class ProgressiveHydrationSupport : HydrationSupport {
    /**
     * Generate hydration data with visibility thresholds
     *
     * @param composable The composable to generate hydration data for
     * @param strategy The hydration strategy to use (defaults to PROGRESSIVE)
     * @return Client-side hydration data as a string (typically JSON)
     */
    override fun generateHydrationData(
        composable: Composable,
        strategy: HydrationStrategy
    ): String {
        // Force the strategy to PROGRESSIVE
        val actualStrategy = HydrationStrategy.PROGRESSIVE

        // In a real implementation, this would analyze the composable to set up progressive hydration
        // For this example, we'll return a placeholder
        return """
            {
                "type": "progressive",
                "chunks": [
                    {"id": "header", "priority": "high", "trigger": "visible"},
                    {"id": "main-content", "priority": "medium", "trigger": "visible"},
                    {"id": "comments", "priority": "low", "trigger": "visible"},
                    {"id": "footer", "priority": "low", "trigger": "visible"}
                ]
            }
        """.trimIndent()
    }

    /**
     * Add progressive hydration markers to HTML
     *
     * @param html The rendered HTML
     * @param hydrationData The hydration data as a string
     * @return HTML with progressive hydration markers
     */
    override fun addHydrationMarkers(html: String, hydrationData: String): String {
        // In a real implementation, this would parse the HTML and add data attributes for progressive hydration

        // For this example, we'll add a hidden script tag with the hydration data
        // and pretend we've added the necessary attributes to the elements
        return """
            <div data-summon-hydration="container" data-hydration-strategy="progressive">
                $html
                <script type="application/json" id="summon-hydration-data" style="display:none">
                    $hydrationData
                </script>
            </div>
        """.trimIndent()
    }
} 