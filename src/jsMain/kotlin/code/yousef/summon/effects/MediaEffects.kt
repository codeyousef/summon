package code.yousef.summon.effects.js

import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.onMountWithCleanup
import code.yousef.summon.runtime.Composable
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.theme.MediaQuery
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
    val MOBILE = "(max-width: ${MediaQuery.Breakpoints.sm - 1}px)"
    val TABLET = "(min-width: ${MediaQuery.Breakpoints.sm}px) and (max-width: ${MediaQuery.Breakpoints.md - 1}px)"
    val DESKTOP = "(min-width: ${MediaQuery.Breakpoints.md}px)"
    val LARGE_DESKTOP = "(min-width: ${MediaQuery.Breakpoints.lg}px)"
    
    // Orientation queries
    const val PORTRAIT = "(orientation: portrait)"
    const val LANDSCAPE = "(orientation: landscape)"
    
    // Color scheme queries
    const val DARK_MODE = "(prefers-color-scheme: dark)"
    const val LIGHT_MODE = "(prefers-color-scheme: light)"
    
    // Motion preference queries
    const val REDUCED_MOTION = "(prefers-reduced-motion: reduce)"
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
 */
data class ResponsiveBreakpoints(
    val isMobile: SummonMutableState<Boolean>,
    val isTablet: SummonMutableState<Boolean>,
    val isDesktop: SummonMutableState<Boolean>,
    val isLargeDesktop: SummonMutableState<Boolean>,
    val isPortrait: SummonMutableState<Boolean>,
    val isLandscape: SummonMutableState<Boolean>
) 