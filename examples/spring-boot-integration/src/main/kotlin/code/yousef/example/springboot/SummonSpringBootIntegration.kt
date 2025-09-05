package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import code.yousef.summon.ssr.HydrationUtils
import code.yousef.summon.ssr.HydrationStrategy
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.stereotype.Component
import jakarta.annotation.PostConstruct

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
     * Initialize the global PlatformRenderer for use by components
     */
    @PostConstruct
    fun initializeRenderer() {
        // Set the global renderer so that components can access it
        setPlatformRenderer(renderer)
    }
    
    /**
     * Renders a Summon composable function to HTML string.
     * This enables server-side rendering of Summon components in Spring Boot.
     */
    fun renderComponent(content: @Composable () -> Unit): String {
        // Use the hydration-enabled renderer to support interactive components
        val fullHtml = renderer.renderComposableRootWithHydration(content)
        
        // Return the complete HTML document with hydration support
        return fullHtml
    }
    
    /**
     * Renders an interactive component that will be prepared for client-side hydration.
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
        // Use hydration-enabled rendering for interactive components
        val fullHtml = renderer.renderComposableRootWithHydration(content)
        
        // Return the complete HTML document with hydration support
        return fullHtml
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