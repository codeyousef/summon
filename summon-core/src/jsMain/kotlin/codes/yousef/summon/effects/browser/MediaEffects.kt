package codes.yousef.summon.effects.browser

import codes.yousef.summon.effects.CompositionScope
import codes.yousef.summon.effects.onMountWithCleanup
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.theme.MediaQuery
import kotlinx.browser.window
import org.w3c.dom.MediaQueryList
import org.w3c.dom.events.Event

/**
 * Effect for responsive design using media queries
 *
 * @param query CSS media query string (e.g. "(max-width: 768px)")
 * @return Boolean state that is true when the media query matches
 */
@Composable
fun CompositionScope.useMediaQuery(query: String): SummonMutableState<Boolean> {
    // Create a media query list
    val mediaQuery = window.matchMedia(query)

    // Initialize with the current match state
    val matches = mutableStateOf(mediaQuery.matches)

    onMountWithCleanup {
        // Create the change handler
        val handleChange = { event: Event ->
            // Update the state when the match state changes
            val mediaQueryEvent = event.target as MediaQueryList
            matches.value = mediaQueryEvent.matches
        }

        // Add event listener
        mediaQuery.addListener(handleChange)

        // Return cleanup function
        return@onMountWithCleanup {
            // Remove event listener
            mediaQuery.removeListener(handleChange)
        }
    }

    return matches
}

/**
 * Common breakpoints for responsive design
 * Uses MediaQuery.Breakpoints values for consistency
 */
object Breakpoints {
    // Media query strings using standard breakpoint values
    /** The property declaration value. */
    val MOBILE = "(max-width: ${MediaQuery.Breakpoints.sm - 1}px)"
    /** The property declaration value. */
    val TABLET = "(min-width: ${MediaQuery.Breakpoints.sm}px) and (max-width: ${MediaQuery.Breakpoints.md - 1}px)"
    /** The property declaration value. */
    val DESKTOP = "(min-width: ${MediaQuery.Breakpoints.md}px)"
    /** The property declaration value. */
    val LARGE_DESKTOP = "(min-width: ${MediaQuery.Breakpoints.lg}px)"

    // Orientation queries
    /** The property declaration value. */
    const val PORTRAIT = "(orientation: portrait)"
    /** The property declaration value. */
    const val LANDSCAPE = "(orientation: landscape)"

    // Color scheme queries
    /** The property declaration value. */
    const val DARK_MODE = "(prefers-color-scheme: dark)"
    /** The property declaration value. */
    const val LIGHT_MODE = "(prefers-color-scheme: light)"

    // Motion preference queries
    /** The property declaration value. */
    const val REDUCED_MOTION = "(prefers-reduced-motion: reduce)"
    /** The property declaration value. */
    const val ALLOWS_MOTION = "(prefers-reduced-motion: no-preference)"

    /**
     * Helper function to create a media query string from breakpoint values
     */
    fun fromBreakpoint(minWidth: Int? = null, maxWidth: Int? = null): String {
        return when {
            minWidth != null && maxWidth != null -> "(min-width: ${minWidth}px) and (max-width: ${maxWidth}px)"
            minWidth != null -> "(min-width: ${minWidth}px)"
            maxWidth != null -> "(max-width: ${maxWidth}px)"
            else -> ""
        }
    }

    /**
     * Create media query from MediaQuery.Breakpoints constants
     */
    fun fromBreakpointName(breakpoint: String): String {
        return when (breakpoint) {
            "xs" -> "(max-width: ${MediaQuery.Breakpoints.sm - 1}px)"
            "sm" -> fromBreakpoint(MediaQuery.Breakpoints.sm, MediaQuery.Breakpoints.md - 1)
            "md" -> fromBreakpoint(MediaQuery.Breakpoints.md, MediaQuery.Breakpoints.lg - 1)
            "lg" -> fromBreakpoint(MediaQuery.Breakpoints.lg, MediaQuery.Breakpoints.xl - 1)
            "xl" -> "(min-width: ${MediaQuery.Breakpoints.xl}px)"
            else -> ""
        }
    }
}

/**
 * Effect for detecting dark mode preference
 *
 * @return Boolean state that is true when the user prefers dark mode
 */
@Composable
fun CompositionScope.useDarkMode(): SummonMutableState<Boolean> {
    return useMediaQuery(Breakpoints.DARK_MODE)
}

/**
 * Effect for detecting reduced motion preference
 *
 * @return Boolean state that is true when the user prefers reduced motion
 */
@Composable
fun CompositionScope.useReducedMotion(): SummonMutableState<Boolean> {
    return useMediaQuery(Breakpoints.REDUCED_MOTION)
}

/**
 * Effect for responsive design with common breakpoints
 *
 * @return Object with boolean states for each breakpoint
 */
@Composable
fun CompositionScope.useResponsive(): ResponsiveBreakpoints {
    val isMobile = useMediaQuery(Breakpoints.MOBILE)
    val isTablet = useMediaQuery(Breakpoints.TABLET)
    val isDesktop = useMediaQuery(Breakpoints.DESKTOP)
    val isLargeDesktop = useMediaQuery(Breakpoints.LARGE_DESKTOP)
    val isPortrait = useMediaQuery(Breakpoints.PORTRAIT)
    val isLandscape = useMediaQuery(Breakpoints.LANDSCAPE)

    return ResponsiveBreakpoints(
        isMobile = isMobile,
        isTablet = isTablet,
        isDesktop = isDesktop,
        isLargeDesktop = isLargeDesktop,
        isPortrait = isPortrait,
        isLandscape = isLandscape
    )
}

/**
 * Responsive breakpoints state

 * @property isMobile The is mobile value.
 * @property isTablet The is tablet value.
 * @property isDesktop The is desktop value.
 * @property isLargeDesktop The is large desktop value.
 * @property isPortrait The is portrait value.
 * @property isLandscape The is landscape value.
 */
data class ResponsiveBreakpoints(
    val isMobile: SummonMutableState<Boolean>,
    val isTablet: SummonMutableState<Boolean>,
    val isDesktop: SummonMutableState<Boolean>,
    val isLargeDesktop: SummonMutableState<Boolean>,
    val isPortrait: SummonMutableState<Boolean>,
    val isLandscape: SummonMutableState<Boolean>
)
