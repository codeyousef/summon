package codes.yousef.summon.hydration

/**
 * Priority levels for component hydration order.
 *
 * Components with higher priority (lower ordinal value) are hydrated first,
 * enabling critical interactive elements to become functional before non-critical ones.
 *
 * ## Priority Levels
 *
 * - **CRITICAL (0)**: Developer-marked critical components via `data-hydration-priority="critical"`
 *   Use for primary CTAs, navigation, and essential interactive elements.
 *
 * - **VISIBLE (1)**: Components currently visible in the viewport (detected via IntersectionObserver)
 *   Automatically assigned to above-the-fold content.
 *
 * - **NEAR (2)**: Components within 200px of the viewport edge
 *   Will likely be visible soon as user scrolls.
 *
 * - **DEFERRED (3)**: Below-fold components that can hydrate during idle time
 *   Hydrated on scroll or when browser is idle.
 *
 * ## Usage
 *
 * ```kotlin
 * // In composables, mark critical components:
 * Button(
 *     onClick = { checkout() },
 *     modifier = Modifier().hydrationPriority(HydrationPriority.CRITICAL)
 * )
 * ```
 *
 * The hydration scheduler will process tasks in priority order, ensuring
 * the best user experience on slow connections and devices.
 */
enum class HydrationPriority(val value: Int) {
    /**
     * Developer-marked critical components.
     * Hydrated first, before any other components.
     */
    CRITICAL(0),

    /**
     * Components currently visible in the viewport.
     * Automatically detected via IntersectionObserver.
     */
    VISIBLE(1),

    /**
     * Components near the viewport (within 200px).
     * Hydrated after visible components.
     */
    NEAR(2),

    /**
     * Below-fold components.
     * Hydrated during idle time or on scroll.
     */
    DEFERRED(3);

    companion object {
        /**
         * Parse a priority from a string value (from data-hydration-priority attribute).
         *
         * @param value The string value to parse
         * @return The corresponding HydrationPriority, or DEFERRED if not recognized
         */
        fun fromString(value: String?): HydrationPriority {
            return when (value?.lowercase()) {
                "critical" -> CRITICAL
                "visible" -> VISIBLE
                "near" -> NEAR
                "deferred" -> DEFERRED
                else -> DEFERRED
            }
        }

        /**
         * Attribute name used in HTML for priority hints.
         */
        const val ATTRIBUTE_NAME = "data-hydration-priority"
    }
}
