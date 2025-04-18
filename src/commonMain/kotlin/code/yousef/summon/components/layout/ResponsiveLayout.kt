package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.runtime.getPlatformRenderer
import kotlinx.html.FlowContent
import kotlinx.html.div

/**
 * Screen size breakpoints for responsive layouts.
 */
enum class ScreenSize {
    SMALL,  // Mobile phones (< 600px)
    MEDIUM, // Tablets (600px - 959px)
    LARGE,  // Desktop (960px - 1279px)
    XLARGE  // Large desktop (>= 1280px)
}

// Breakpoint values in pixels
object ResponsiveBreakpoints {
    const val SMALL_BREAKPOINT = 600
    const val MEDIUM_BREAKPOINT = 960
    const val LARGE_BREAKPOINT = 1280
}

/**
 * A layout composable that displays different content based on the screen size.
 * ResponsiveLayout allows developers to create adaptive UI layouts that respond to
 * different device sizes and orientations.
 *
 * @param content Map of screen sizes to the composable functions that should be displayed at those sizes
 * @param defaultContent The default composable function to display if no specific content is provided for the current screen size
 * @param modifier The modifier to apply to this composable
 * @param detectScreenSizeClient Whether to use client-side detection for screen size (default true)
 * @param serverSideScreenSize The screen size to use for server-side rendering (default LARGE)
 */
@Composable
fun ResponsiveLayout(
    content: Map<ScreenSize, @Composable () -> Unit>,
    defaultContent: @Composable () -> Unit,
    modifier: Modifier = Modifier(),
    detectScreenSizeClient: Boolean = true,
    serverSideScreenSize: ScreenSize = ScreenSize.LARGE
) {
    // Create a modified modifier with responsive layout data
    val responsiveModifier = modifier
        .style("position", "relative") // Ensure proper positioning
        .style("display", "block") // Default display
        .attribute("data-responsive", "true")
        .attribute("data-small-breakpoint", ResponsiveBreakpoints.SMALL_BREAKPOINT.toString())
        .attribute("data-medium-breakpoint", ResponsiveBreakpoints.MEDIUM_BREAKPOINT.toString())
        .attribute("data-large-breakpoint", ResponsiveBreakpoints.LARGE_BREAKPOINT.toString())
        .attribute("data-server-size", serverSideScreenSize.name)
        .attribute("data-client-detection", detectScreenSizeClient.toString())

    getPlatformRenderer().renderResponsiveLayout(
        modifier = responsiveModifier,
        content = { // 'this' is FlowContent scope
            if (detectScreenSizeClient) {
                // Client-side approach: render all content options with visibility controlled by CSS/JS
                for ((screenSize, composable) in content) {
                    // Wrap each screen size's content in a div with appropriate data attributes
                    div {
                        attributes["data-screen-size"] = screenSize.name
                        attributes["class"] = "responsive-content ${screenSize.name.lowercase()}-content"
                        attributes["style"] = "display: none;" // Hidden by default, shown by JS

                        // Render the content for this screen size
                        composable()
                    }
                }

                // Render default content in its own container
                div {
                    attributes["data-screen-size"] = "DEFAULT"
                    attributes["class"] = "responsive-content default-content"
                    attributes["style"] = "display: none;" // Hidden by default, shown by JS if no match

                    // Render the default content
                    defaultContent()
                }
            } else {
                // Server-side approach: only render content for the specified screen size
                val contentToRender = content[serverSideScreenSize] ?: defaultContent
                contentToRender()
            }
        }
    )
}
