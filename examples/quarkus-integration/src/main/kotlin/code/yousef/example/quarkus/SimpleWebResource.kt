package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.display.*
import code.yousef.summon.components.input.*
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Simple Web Resource using the official Summon library components.
 * This demonstrates the core concepts of the Summon UI framework.
 */
@Path("/simple")
class SimpleWebResource {

    /**
     * Home page using Summon library components
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        val renderer = PlatformRenderer()
        return renderer.renderComposableRoot {
            Column(
                modifier = Modifier()
                    .style("padding", "20px")
                    .style("gap", "16px")
                    .style("display", "flex")
                    .style("flex-direction", "column")
                    .style("min-height", "100vh")
                    .style("background-color", "#f8f9fa")
            ) {
                Text(
                    text = "Simple Summon + Quarkus Demo",
                    modifier = Modifier()
                        .style("font-size", "32px")
                        .style("font-weight", "bold")
                        .style("color", "#333333")
                        .style("margin-bottom", "16px")
                )
                
                Card(
                    modifier = Modifier()
                        .style("background-color", "#ffffff")
                        .style("margin-bottom", "20px")
                        .style("padding", "20px")
                ) {
                    Text(
                        text = "✓ Using Official Summon Library",
                        modifier = Modifier()
                            .style("font-size", "18px")
                            .style("font-weight", "600")
                            .style("margin-bottom", "8px")
                    )
                    Text(
                        text = "✓ No external dependencies required for UI components",
                        modifier = Modifier().style("margin-bottom", "8px")
                    )
                    Text(
                        text = "✓ Type-safe CSS styling",
                        modifier = Modifier().style("margin-bottom", "8px")
                    )
                    Text(
                        text = "✓ Works with Quarkus out of the box",
                        modifier = Modifier().style("margin-bottom", "16px")
                    )
                    
                    Row(
                        modifier = Modifier()
                            .style("gap", "8px")
                            .style("display", "flex")
                            .style("flex-direction", "row")
                            .style("margin-top", "16px")
                    ) {
                        Button(
                            onClick = { }, // Note: onClick handlers need JS integration
                            label = "View Examples",
                            modifier = Modifier()
                                .style("background-color", "#0077cc")
                                .style("color", "white")
                                .style("padding", "8px 16px")
                                .style("border-radius", "4px")
                                .style("border", "none")
                                .style("cursor", "pointer")
                        )
                        
                        Button(
                            onClick = { }, // Note: onClick handlers need JS integration
                            label = "API Demo",
                            modifier = Modifier()
                                .style("background-color", "#28a745")
                                .style("color", "white")
                                .style("padding", "8px 16px")
                                .style("border-radius", "4px")
                                .style("border", "none")
                                .style("cursor", "pointer")
                        )
                    }
                }
                
                // Current time display
                Card(modifier = Modifier().style("text-align", "center")) {
                    Text(
                        text = "Server Time",
                        modifier = Modifier()
                            .style("font-size", "20px")
                            .style("font-weight", "600")
                            .style("margin-bottom", "8px")
                    )
                    Text(
                        text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        modifier = Modifier()
                            .style("font-size", "28px")
                            .style("font-weight", "bold")
                            .style("color", "#0077cc")
                            .style("margin-bottom", "4px")
                    )
                    Text(
                        text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                        modifier = Modifier()
                            .style("font-size", "14px")
                            .style("color", "#666666")
                    )
                }
            }
        }
    }

    /**
     * Examples page showcasing different components
     */
    @GET
    @Path("/examples")
    @Produces(MediaType.TEXT_HTML)
    fun examples(): String {
        val renderer = PlatformRenderer()
        return renderer.renderComposableRoot {
            Column(
                modifier = Modifier()
                    .style("padding", "20px")
                    .style("gap", "20px")
                    .style("display", "flex")
                    .style("flex-direction", "column")
            ) {
                Text(
                    text = "Component Examples",
                    modifier = Modifier()
                        .style("font-size", "28px")
                        .style("font-weight", "bold")
                        .style("margin-bottom", "16px")
                )
                
                // Typography examples
                Card(
                    modifier = Modifier()
                        .style("margin-bottom", "16px")
                        .style("padding", "16px")
                ) {
                    Text(
                        text = "Typography",
                        modifier = Modifier()
                            .style("font-size", "20px")
                            .style("font-weight", "bold")
                            .style("margin-bottom", "8px")
                    )
                    Text(
                        text = "Heading Text",
                        modifier = Modifier()
                            .style("font-size", "24px")
                            .style("font-weight", "600")
                            .style("margin-bottom", "8px")
                    )
                    Text(
                        text = "Regular paragraph text with normal weight",
                        modifier = Modifier()
                            .style("font-size", "16px")
                            .style("font-weight", "normal")
                            .style("margin-bottom", "8px")
                    )
                    Text(
                        text = "Small text for descriptions",
                        modifier = Modifier()
                            .style("font-size", "14px")
                            .style("color", "#666666")
                    )
                }
                
                // Button examples
                Card(
                    modifier = Modifier()
                        .style("margin-bottom", "16px")
                        .style("padding", "16px")
                ) {
                    Text(
                        text = "Buttons",
                        modifier = Modifier()
                            .style("font-size", "20px")
                            .style("font-weight", "bold")
                            .style("margin-bottom", "8px")
                    )
                    
                    Row(
                        modifier = Modifier()
                            .style("gap", "8px")
                            .style("display", "flex")
                            .style("flex-direction", "row")
                    ) {
                        Button(
                            onClick = { },
                            label = "Primary",
                            modifier = Modifier()
                                .style("background-color", "#0077cc")
                                .style("color", "white")
                                .style("padding", "8px 16px")
                                .style("border-radius", "4px")
                                .style("border", "none")
                        )
                        
                        Button(
                            onClick = { },
                            label = "Success",
                            modifier = Modifier()
                                .style("background-color", "#28a745")
                                .style("color", "white")
                                .style("padding", "8px 16px")
                                .style("border-radius", "4px")
                                .style("border", "none")
                        )
                        
                        Button(
                            onClick = { },
                            label = "Danger",
                            modifier = Modifier()
                                .style("background-color", "#dc3545")
                                .style("color", "white")
                                .style("padding", "8px 16px")
                                .style("border-radius", "4px")
                                .style("border", "none")
                        )
                    }
                }
                
                // Layout examples
                Card(modifier = Modifier().style("padding", "16px")) {
                    Text(
                        text = "Layout Examples",
                        modifier = Modifier()
                            .style("font-size", "20px")
                            .style("font-weight", "bold")
                            .style("margin-bottom", "8px")
                    )
                    
                    Text(
                        text = "Three Column Layout:",
                        modifier = Modifier()
                            .style("font-weight", "600")
                            .style("margin-bottom", "8px")
                    )
                    
                    Row(
                        modifier = Modifier()
                            .style("gap", "12px")
                            .style("display", "flex")
                            .style("flex-direction", "row")
                    ) {
                        Box(
                            modifier = Modifier()
                                .style("background-color", "#e3f2fd")
                                .style("padding", "16px")
                                .style("border-radius", "4px")
                                .style("text-align", "center")
                                .style("flex", "1")
                        ) {
                            Text("Column 1")
                        }
                        
                        Box(
                            modifier = Modifier()
                                .style("background-color", "#f3e5f5")
                                .style("padding", "16px")
                                .style("border-radius", "4px")
                                .style("text-align", "center")
                                .style("flex", "1")
                        ) {
                            Text("Column 2")
                        }
                        
                        Box(
                            modifier = Modifier()
                                .style("background-color", "#e8f5e8")
                                .style("padding", "16px")
                                .style("border-radius", "4px")
                                .style("text-align", "center")
                                .style("flex", "1")
                        ) {
                            Text("Column 3")
                        }
                    }
                }
            }
        }
    }

    /**
     * API endpoint for current time
     */
    @GET
    @Path("/api/time")
    @Produces(MediaType.TEXT_PLAIN)
    fun getCurrentTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }
}