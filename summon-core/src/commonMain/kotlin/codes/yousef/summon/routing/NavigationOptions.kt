package codes.yousef.summon.routing

/**
 * Options for router navigation.
 *
 * @param replaceState If true, replaces the current history entry instead of pushing a new one
 * @param scrollToTop If true, scrolls the window to the top after navigation
 * @param state Additional state data to associate with the navigation
 */
data class NavigationOptions(
    val replaceState: Boolean = false,
    val scrollToTop: Boolean = true,
    val state: Map<String, Any> = emptyMap()
) 