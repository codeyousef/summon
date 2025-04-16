package code.yousef.summon.components.layout

import code.yousef.summon.components.LayoutComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.runtime.getPlatformRenderer
import kotlinx.html.TagConsumer
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

/**
 * A layout composable that displays different content based on the screen size.
 * ResponsiveLayout allows developers to create adaptive UI layouts that respond to
 * different device sizes and orientations.
 *
 * @param content Map of screen sizes to the composables that should be displayed at those sizes
 * @param defaultContent The default composable to display if no specific content is provided for the current screen size
 * @param modifier The modifier to apply to this composable
 * @param detectScreenSizeClient Whether to use client-side detection for screen size (default true)
 * @param serverSideScreenSize The screen size to use for server-side rendering (default LARGE)
 */
class ResponsiveLayout(
    val content: Map<ScreenSize, Composable>,
    val defaultContent: Composable,
    val modifier: Modifier = Modifier(),
    val detectScreenSizeClient: Boolean = true,
    val serverSideScreenSize: ScreenSize = ScreenSize.LARGE
) : Composable, LayoutComponent {

    // Breakpoint values in pixels
    companion object {
        const val SMALL_BREAKPOINT = 600
        const val MEDIUM_BREAKPOINT = 960
        const val LARGE_BREAKPOINT = 1280
    }

    /**
     * Renders this ResponsiveLayout composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            // Create a modified modifier with responsive layout data
            val responsiveModifier = modifier
                .style("position", "relative") // Ensure proper positioning
                .style("display", "block") // Default display
                .attribute("data-responsive", "true")
                .attribute("data-small-breakpoint", SMALL_BREAKPOINT.toString())
                .attribute("data-medium-breakpoint", MEDIUM_BREAKPOINT.toString())
                .attribute("data-large-breakpoint", LARGE_BREAKPOINT.toString())
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
                                composable.compose(this)
                            }
                        }

                        // Render default content in its own container
                        div {
                            attributes["data-screen-size"] = "DEFAULT"
                            attributes["class"] = "responsive-content default-content"
                            attributes["style"] = "display: none;" // Hidden by default, shown by JS if no match

                            // Render the default content
                            defaultContent.compose(this)
                        }
                    } else {
                        // Server-side approach: only render content for the specified screen size
                        val contentToRender = content[serverSideScreenSize] ?: defaultContent
                        contentToRender.compose(this)
                    }
                }
            )
        }
        return receiver
    }
}
