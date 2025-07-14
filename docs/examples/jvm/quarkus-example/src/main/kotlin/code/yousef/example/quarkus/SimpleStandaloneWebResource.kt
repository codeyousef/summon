package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Simplified Web Resource using only the standalone Summon components.
 * This demonstrates the core concepts without the complexity of the full Quarkus integration.
 */
@Path("/standalone")
class SimpleStandaloneWebResource {

    /**
     * Home page using standalone Summon components
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return createHtmlPage("Standalone Summon with Quarkus") {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(16.px)
            ) {
                StandaloneText(
                    text = "Welcome to Standalone Summon + Quarkus",
                    modifier = StandaloneModifier()
                        .fontSize(32.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .color("#333333")
                        .marginBottom(16.px)
                ) +
                
                StandaloneCard(
                    modifier = StandaloneModifier()
                        .backgroundColor("#f8f9fa")
                        .marginBottom(20.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(12.px)
                    ) {
                        StandaloneText(
                            text = "✓ Standalone Implementation",
                            modifier = StandaloneModifier()
                                .fontSize(18.px)
                                .fontWeight(StandaloneFontWeight.SemiBold)
                        ) +
                        StandaloneText("✓ No external dependencies required") +
                        StandaloneText("✓ Type-safe CSS styling") +
                        StandaloneText("✓ Works with Quarkus out of the box") +
                        
                        StandaloneRow(
                            modifier = StandaloneModifier()
                                .gap(8.px)
                                .marginTop(16.px)
                        ) {
                            StandaloneButton(
                                label = "View Examples",
                                modifier = StandaloneModifier()
                                    .backgroundColor("#0077cc")
                                    .color("white")
                                    .padding(8.px, 16.px)
                                    .borderRadius(4.px)
                                    .cursor(StandaloneCursor.Pointer)
                                    .onClick("window.location.href='/standalone/examples'")
                            ) +
                            
                            StandaloneButton(
                                label = "API Demo",
                                modifier = StandaloneModifier()
                                    .backgroundColor("#28a745")
                                    .color("white")
                                    .padding(8.px, 16.px)
                                    .borderRadius(4.px)
                                    .cursor(StandaloneCursor.Pointer)
                                    .onClick("window.location.href='/standalone/api-demo'")
                            )
                        }
                    }
                } +
                
                // Current time display
                StandaloneCard {
                    StandaloneColumn(
                        modifier = StandaloneModifier()
                            .textAlign(StandaloneTextAlign.Center)
                            .gap(8.px)
                    ) {
                        StandaloneText(
                            text = "Server Time",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.SemiBold)
                        ) +
                        StandaloneText(
                            text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            modifier = StandaloneModifier()
                                .fontSize(28.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .color("#0077cc")
                        ) +
                        StandaloneText(
                            text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                            modifier = StandaloneModifier()
                                .fontSize(14.px)
                                .color("#666666")
                        )
                    }
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
        return createHtmlPage("Standalone Examples") {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                StandaloneText(
                    text = "Component Examples",
                    modifier = StandaloneModifier()
                        .fontSize(28.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                // Typography examples
                StandaloneCard(
                    modifier = StandaloneModifier().marginBottom(16.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(8.px)
                    ) {
                        StandaloneText(
                            text = "Typography",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .marginBottom(8.px)
                        ) +
                        StandaloneText(
                            text = "Heading Text",
                            modifier = StandaloneModifier()
                                .fontSize(24.px)
                                .fontWeight(StandaloneFontWeight.SemiBold)
                        ) +
                        StandaloneText(
                            text = "Regular paragraph text with normal weight",
                            modifier = StandaloneModifier()
                                .fontSize(16.px)
                                .fontWeight(StandaloneFontWeight.Normal)
                        ) +
                        StandaloneText(
                            text = "Small text for descriptions",
                            modifier = StandaloneModifier()
                                .fontSize(14.px)
                                .color("#666666")
                        )
                    }
                } +
                
                // Button examples
                StandaloneCard(
                    modifier = StandaloneModifier().marginBottom(16.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(8.px)
                    ) {
                        StandaloneText(
                            text = "Buttons",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .marginBottom(8.px)
                        ) +
                        
                        StandaloneRow(
                            modifier = StandaloneModifier().gap(8.px)
                        ) {
                            StandaloneButton(
                                label = "Primary",
                                modifier = StandaloneModifier()
                                    .backgroundColor("#0077cc")
                                    .color("white")
                                    .padding(8.px, 16.px)
                                    .borderRadius(4.px)
                                    .cursor(StandaloneCursor.Pointer)
                            ) +
                            
                            StandaloneButton(
                                label = "Success",
                                modifier = StandaloneModifier()
                                    .backgroundColor("#28a745")
                                    .color("white")
                                    .padding(8.px, 16.px)
                                    .borderRadius(4.px)
                                    .cursor(StandaloneCursor.Pointer)
                            ) +
                            
                            StandaloneButton(
                                label = "Danger",
                                modifier = StandaloneModifier()
                                    .backgroundColor("#dc3545")
                                    .color("white")
                                    .padding(8.px, 16.px)
                                    .borderRadius(4.px)
                                    .cursor(StandaloneCursor.Pointer)
                            )
                        }
                    }
                } +
                
                // Layout examples
                StandaloneCard {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(8.px)
                    ) {
                        StandaloneText(
                            text = "Layout Examples",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .marginBottom(8.px)
                        ) +
                        
                        StandaloneText(
                            text = "Three Column Layout:",
                            modifier = StandaloneModifier()
                                .fontWeight(StandaloneFontWeight.SemiBold)
                                .marginBottom(8.px)
                        ) +
                        
                        StandaloneRow(
                            modifier = StandaloneModifier().gap(12.px)
                        ) {
                            StandaloneBox(
                                modifier = StandaloneModifier()
                                    .backgroundColor("#e3f2fd")
                                    .padding(16.px)
                                    .borderRadius(4.px)
                                    .textAlign(StandaloneTextAlign.Center)
                                    .width("33%")
                            ) {
                                StandaloneText("Column 1")
                            } +
                            
                            StandaloneBox(
                                modifier = StandaloneModifier()
                                    .backgroundColor("#f3e5f5")
                                    .padding(16.px)
                                    .borderRadius(4.px)
                                    .textAlign(StandaloneTextAlign.Center)
                                    .width("33%")
                            ) {
                                StandaloneText("Column 2")
                            } +
                            
                            StandaloneBox(
                                modifier = StandaloneModifier()
                                    .backgroundColor("#e8f5e8")
                                    .padding(16.px)
                                    .borderRadius(4.px)
                                    .textAlign(StandaloneTextAlign.Center)
                                    .width("33%")
                            ) {
                                StandaloneText("Column 3")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * API demo page with interactive elements
     */
    @GET
    @Path("/api-demo")
    @Produces(MediaType.TEXT_HTML)
    fun apiDemo(): String {
        return createHtmlPage("API Demo", includeHtmx = true) {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                StandaloneText(
                    text = "Interactive API Demo",
                    modifier = StandaloneModifier()
                        .fontSize(28.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                // Live clock demo
                StandaloneCard(
                    modifier = StandaloneModifier().marginBottom(16.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(8.px)
                    ) {
                        StandaloneText(
                            text = "Live Server Time",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .marginBottom(8.px)
                        ) +
                        
                        StandaloneText(
                            text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            modifier = StandaloneModifier()
                                .fontSize(32.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .color("#0077cc")
                                .textAlign(StandaloneTextAlign.Center)
                                .hxGet("/standalone/api/time")
                                .hxTrigger("every 1s")
                                .hxSwap("innerHTML")
                        )
                    }
                } +
                
                // Simple message API demo
                StandaloneCard {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(12.px)
                    ) {
                        StandaloneText(
                            text = "Simple API Call",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .marginBottom(8.px)
                        ) +
                        
                        StandaloneButton(
                            label = "Get Message",
                            modifier = StandaloneModifier()
                                .backgroundColor("#28a745")
                                .color("white")
                                .padding(10.px, 20.px)
                                .borderRadius(4.px)
                                .cursor(StandaloneCursor.Pointer)
                                .hxGet("/standalone/api/message")
                                .hxTarget("#message-output")
                                .hxSwap("innerHTML")
                        ) +
                        
                        StandaloneBox(
                            modifier = StandaloneModifier()
                                .id("message-output")
                                .padding(12.px)
                                .border("1px solid #ddd")
                                .borderRadius(4.px)
                                .backgroundColor("#f8f9fa")
                                .minHeight(40.px)
                        ) {
                            StandaloneText(
                                text = "Click the button to get a message from the API",
                                modifier = StandaloneModifier().color("#666666")
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * API endpoint for live time updates
     */
    @GET
    @Path("/api/time")
    @Produces(MediaType.TEXT_PLAIN)
    fun getCurrentTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    /**
     * API endpoint for message demo
     */
    @GET
    @Path("/api/message")
    @Produces(MediaType.TEXT_HTML)
    fun getMessage(): String {
        val messages = listOf(
            "Hello from the Quarkus API!",
            "Standalone Summon is working great!",
            "Server time: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))}",
            "This message was generated at ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}",
            "API call successful ✓"
        )
        
        val randomMessage = messages.random()
        
        return StandaloneText(
            text = randomMessage,
            modifier = StandaloneModifier()
                .color("#28a745")
                .fontWeight(StandaloneFontWeight.SemiBold)
        )
    }

    /**
     * Helper function to create a complete HTML page
     */
    private fun createHtmlPage(title: String, includeHtmx: Boolean = false, content: () -> String): String {
        val htmxScript = if (includeHtmx) {
            """<script src="https://unpkg.com/htmx.org@1.9.10"></script>"""
        } else ""
        
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$title</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f8fafc;
                        min-height: 100vh;
                    }
                    
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                </style>
                $htmxScript
            </head>
            <body>
                <div class="container">
                    ${content()}
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}