package code.yousef.summon.effects.js

import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.onMountWithCleanup
import code.yousef.summon.runtime.Composable
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
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
 */
object Breakpoints {
    const val MOBILE = "(max-width: 767px)"
    const val TABLET = "(min-width: 768px) and (max-width: 1023px)"
    const val DESKTOP = "(min-width: 1024px)"
    const val LARGE_DESKTOP = "(min-width: 1440px)"
    
    const val PORTRAIT = "(orientation: portrait)"
    const val LANDSCAPE = "(orientation: landscape)"
    
    const val DARK_MODE = "(prefers-color-scheme: dark)"
    const val LIGHT_MODE = "(prefers-color-scheme: light)"
    
    const val REDUCED_MOTION = "(prefers-reduced-motion: reduce)"
    const val ALLOWS_MOTION = "(prefers-reduced-motion: no-preference)"
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