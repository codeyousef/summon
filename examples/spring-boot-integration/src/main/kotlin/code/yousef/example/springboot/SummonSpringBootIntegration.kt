package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.ssr.HydrationUtils
import code.yousef.summon.ssr.HydrationStrategy
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.stereotype.Component

/**
 * Integration layer between Summon UI framework and Spring Boot.
 * Provides server-side rendering of Summon components for Spring MVC with hydration support.
 */
@Component
class SummonRenderer {
    
    /**
     * The platform renderer instance used for all rendering operations.
     */
    val renderer = PlatformRenderer()
    
    /**
     * Renders a Summon composable function to HTML string.
     * This enables server-side rendering of Summon components in Spring Boot.
     */
    fun renderComponent(content: @Composable () -> Unit): String {
        // For now, use the standard renderer and strip the HTML wrapper
        val fullHtml = renderer.renderComposableRoot(content)
        
        // Extract just the body content without the complete HTML document
        return fullHtml
            .substringAfter("<body>")
            .substringBefore("</body>")
            .trim()
    }
    
    /**
     * Renders an interactive component that will be prepared for client-side hydration.
     * For now, this just renders the component with hydration markers.
     * @param componentId Unique identifier for the component (used for hydration)
     * @param componentType Type of component (e.g., "counter", "form")
     * @param initialState Initial state data for the component
     * @param content The composable function to render
     */
    fun renderInteractiveComponent(
        componentId: String,
        componentType: String,
        initialState: Map<String, Any?> = emptyMap(),
        content: @Composable () -> Unit
    ): String {
        // For now, just render the component normally
        // The hydration system will be enhanced in future iterations
        val fullHtml = renderer.renderComposableRoot(content)
        
        // Extract just the body content without the complete HTML document
        return fullHtml
            .substringAfter("<body>")
            .substringBefore("</body>")
            .trim()
    }
    
    /**
     * Generates hydration script for client-side component reactivation.
     * Uses Summon's built-in HydrationUtils for proper state management.
     */
    fun generateHydrationScript(
        rootComposable: @Composable () -> Unit = {},
        initialState: Map<String, Any?> = emptyMap()
    ): String {
        return HydrationUtils.generateHydrationScript(
            rootComposable = rootComposable,
            initialState = initialState,
            strategy = HydrationStrategy.FULL
        )
    }
}