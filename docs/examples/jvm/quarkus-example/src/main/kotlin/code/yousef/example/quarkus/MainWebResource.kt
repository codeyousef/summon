package code.yousef.example.quarkus

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Main Web Resource handling the primary application routes.
 * Uses standalone Summon components to provide working examples.
 */
@Path("/")
class MainWebResource {

    /**
     * Home page - main landing page for the application
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        return createMainAppPage("Summon + Quarkus - Home") {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                // Hero section
                StandaloneCard(
                    modifier = StandaloneModifier()
                        .backgroundColor("#e3f2fd")
                        .marginBottom(20.px)
                        .padding(30.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier()
                            .textAlign(StandaloneTextAlign.Center)
                            .gap(12.px)
                    ) {
                        StandaloneText(
                            text = "Welcome to Summon + Quarkus",
                            modifier = StandaloneModifier()
                                .fontSize(36.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .color("#1976d2")
                        ) +
                        StandaloneText(
                            text = "A demonstration of the Summon UI framework with Quarkus backend",
                            modifier = StandaloneModifier()
                                .fontSize(18.px)
                                .color("#666666")
                        ) +
                        StandaloneText(
                            text = "✓ Type-safe UI components ✓ Server-side rendering ✓ HTMX integration",
                            modifier = StandaloneModifier()
                                .fontSize(16.px)
                                .color("#333333")
                                .marginTop(12.px)
                        )
                    }
                } +
                
                // Feature showcase
                StandaloneText(
                    text = "Key Features",
                    modifier = StandaloneModifier()
                        .fontSize(24.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                StandaloneRow(
                    modifier = StandaloneModifier().gap(16.px)
                ) {
                    FeatureCard(
                        title = "Reactive UI",
                        description = "Build reactive user interfaces with state management",
                        icon = "🔄",
                        color = "#4caf50"
                    ) +
                    FeatureCard(
                        title = "Component-Based",
                        description = "Create reusable UI components with Compose-like syntax",
                        icon = "🧩", 
                        color = "#2196f3"
                    ) +
                    FeatureCard(
                        title = "Server Integration",
                        description = "Seamless integration with Quarkus server features",
                        icon = "🖥️",
                        color = "#ff9800"
                    )
                } +
                
                // Live server time demo
                StandaloneCard(
                    modifier = StandaloneModifier().marginTop(20.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier()
                            .textAlign(StandaloneTextAlign.Center)
                            .gap(8.px)
                    ) {
                        StandaloneText(
                            text = "Live Server Time",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.SemiBold)
                                .marginBottom(8.px)
                        ) +
                        StandaloneText(
                            text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                            modifier = StandaloneModifier()
                                .fontSize(32.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .color("#0077cc")
                                .id("live-time")
                                .hxGet("/api/current-time")
                                .hxTrigger("every 1s")
                                .hxSwap("innerHTML")
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
     * Users page - user management demonstration
     */
    @GET
    @Path("/users")
    @Produces(MediaType.TEXT_HTML)
    fun users(): String {
        // Sample user data for demonstration
        val sampleUsers = listOf(
            SampleUser(1, "John Doe", "john@example.com", "Admin", true),
            SampleUser(2, "Jane Smith", "jane@example.com", "User", true),
            SampleUser(3, "Bob Johnson", "bob@example.com", "Editor", false),
            SampleUser(4, "Alice Brown", "alice@example.com", "User", true)
        )
        
        return createMainAppPage("User Management") {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                StandaloneText(
                    text = "User Management",
                    modifier = StandaloneModifier()
                        .fontSize(28.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                StandaloneCard {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(16.px)
                    ) {
                        StandaloneText(
                            text = "User Directory",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.SemiBold)
                        ) +
                        
                        // User table header
                        StandaloneRow(
                            modifier = StandaloneModifier()
                                .backgroundColor("#f5f5f5")
                                .padding(12.px)
                                .gap(16.px)
                        ) {
                            StandaloneText("ID", StandaloneModifier().fontWeight(StandaloneFontWeight.Bold).width("60px")) +
                            StandaloneText("Name", StandaloneModifier().fontWeight(StandaloneFontWeight.Bold).width("150px")) +
                            StandaloneText("Email", StandaloneModifier().fontWeight(StandaloneFontWeight.Bold).width("200px")) +
                            StandaloneText("Role", StandaloneModifier().fontWeight(StandaloneFontWeight.Bold).width("100px")) +
                            StandaloneText("Status", StandaloneModifier().fontWeight(StandaloneFontWeight.Bold).width("80px"))
                        } +
                        
                        // User rows
                        sampleUsers.joinToString("") { user ->
                            StandaloneRow(
                                modifier = StandaloneModifier()
                                    .padding(12.px)
                                    .gap(16.px)
                                    .border("0 0 1px 0", StandaloneBorderStyle.Solid, "#eee")
                            ) {
                                StandaloneText(user.id.toString(), StandaloneModifier().width("60px")) +
                                StandaloneText(user.name, StandaloneModifier().width("150px")) +
                                StandaloneText(user.email, StandaloneModifier().width("200px")) +
                                StandaloneBox(
                                    modifier = StandaloneModifier()
                                        .backgroundColor(getRoleColor(user.role))
                                        .color("white")
                                        .padding(4.px, 8.px)
                                        .borderRadius(4.px)
                                        .width("100px")
                                        .textAlign(StandaloneTextAlign.Center)
                                ) {
                                    StandaloneText(user.role)
                                } +
                                StandaloneText(
                                    if (user.active) "✓ Active" else "✗ Inactive",
                                    StandaloneModifier()
                                        .color(if (user.active) "#4caf50" else "#f44336")
                                        .fontWeight(StandaloneFontWeight.SemiBold)
                                        .width("80px")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Dashboard page - metrics and analytics demo
     */
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_HTML)
    fun dashboard(): String {
        return createMainAppPage("Dashboard") {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                StandaloneText(
                    text = "Dashboard",
                    modifier = StandaloneModifier()
                        .fontSize(28.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                // Stats cards row
                StandaloneRow(
                    modifier = StandaloneModifier().gap(16.px)
                ) {
                    StatCard("Users", "1,234", "👥", "#4caf50") +
                    StatCard("Revenue", "$8,345", "💰", "#2196f3") +
                    StatCard("Orders", "567", "📦", "#ff9800") +
                    StatCard("Growth", "+12%", "📈", "#9c27b0")
                } +
                
                // Activity feed
                StandaloneCard(
                    modifier = StandaloneModifier().marginTop(20.px)
                ) {
                    StandaloneColumn(
                        modifier = StandaloneModifier().gap(12.px)
                    ) {
                        StandaloneText(
                            text = "Recent Activity",
                            modifier = StandaloneModifier()
                                .fontSize(20.px)
                                .fontWeight(StandaloneFontWeight.Bold)
                                .marginBottom(8.px)
                        ) +
                        
                        ActivityItem("John Doe completed order #12345", "2 hours ago", "✅") +
                        ActivityItem("Jane Smith updated product inventory", "4 hours ago", "🔄") +
                        ActivityItem("New user Alice Brown registered", "6 hours ago", "👤") +
                        ActivityItem("System backup completed successfully", "8 hours ago", "💾") +
                        ActivityItem("Mike Johnson added new product", "Yesterday", "➕")
                    }
                }
            }
        }
    }

    /**
     * Contact page - contact form demonstration
     */
    @GET
    @Path("/contact")
    @Produces(MediaType.TEXT_HTML)
    fun contact(): String {
        return createMainAppPage("Contact Us", includeHtmx = true) {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                StandaloneText(
                    text = "Contact Us",
                    modifier = StandaloneModifier()
                        .fontSize(28.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                StandaloneRow(
                    modifier = StandaloneModifier().gap(20.px)
                ) {
                    // Contact form
                    StandaloneCard(
                        modifier = StandaloneModifier().width("60%")
                    ) {
                        StandaloneColumn(
                            modifier = StandaloneModifier().gap(16.px)
                        ) {
                            StandaloneText(
                                text = "Send us a message",
                                modifier = StandaloneModifier()
                                    .fontSize(20.px)
                                    .fontWeight(StandaloneFontWeight.SemiBold)
                                    .marginBottom(8.px)
                            ) +
                            
                            // Simple contact form demo
                            StandaloneColumn(
                                modifier = StandaloneModifier().gap(12.px)
                            ) {
                                StandaloneText("Name:") +
                                StandaloneBox(
                                    modifier = StandaloneModifier()
                                        .border("1px", StandaloneBorderStyle.Solid, "#ddd")
                                        .borderRadius(4.px)
                                        .padding(8.px)
                                        .backgroundColor("#f9f9f9")
                                ) {
                                    StandaloneText("[Input field would be here]")
                                } +
                                
                                StandaloneText("Email:") +
                                StandaloneBox(
                                    modifier = StandaloneModifier()
                                        .border("1px", StandaloneBorderStyle.Solid, "#ddd")
                                        .borderRadius(4.px)
                                        .padding(8.px)
                                        .backgroundColor("#f9f9f9")
                                ) {
                                    StandaloneText("[Input field would be here]")
                                } +
                                
                                StandaloneText("Message:") +
                                StandaloneBox(
                                    modifier = StandaloneModifier()
                                        .border("1px", StandaloneBorderStyle.Solid, "#ddd")
                                        .borderRadius(4.px)
                                        .padding(8.px)
                                        .backgroundColor("#f9f9f9")
                                        .minHeight(80.px)
                                ) {
                                    StandaloneText("[Textarea would be here]")
                                } +
                                
                                StandaloneButton(
                                    label = "Send Message",
                                    modifier = StandaloneModifier()
                                        .backgroundColor("#4caf50")
                                        .color("white")
                                        .padding(10.px, 20.px)
                                        .borderRadius(4.px)
                                        .cursor(StandaloneCursor.Pointer)
                                        .marginTop(8.px)
                                )
                            }
                        }
                    } +
                    
                    // Contact info
                    StandaloneCard(
                        modifier = StandaloneModifier().width("35%")
                    ) {
                        StandaloneColumn(
                            modifier = StandaloneModifier().gap(16.px)
                        ) {
                            StandaloneText(
                                text = "Get in touch",
                                modifier = StandaloneModifier()
                                    .fontSize(20.px)
                                    .fontWeight(StandaloneFontWeight.SemiBold)
                                    .marginBottom(8.px)
                            ) +
                            
                            ContactInfoItem("📧", "Email", "hello@summon.dev") +
                            ContactInfoItem("📞", "Phone", "+1 (555) 123-4567") +
                            ContactInfoItem("📍", "Address", "123 Framework St, Code City") +
                            ContactInfoItem("🌐", "Website", "summon.dev")
                        }
                    }
                }
            }
        }
    }

    /**
     * Theme page - theme customization demonstration
     */
    @GET
    @Path("/theme") 
    @Produces(MediaType.TEXT_HTML)
    fun theme(): String {
        return createMainAppPage("Theme Customization") {
            StandaloneColumn(
                modifier = StandaloneModifier()
                    .padding(20.px)
                    .gap(20.px)
            ) {
                StandaloneText(
                    text = "Theme Customization",
                    modifier = StandaloneModifier()
                        .fontSize(28.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .marginBottom(16.px)
                ) +
                
                StandaloneRow(
                    modifier = StandaloneModifier().gap(20.px)
                ) {
                    // Theme options
                    StandaloneCard(
                        modifier = StandaloneModifier().width("50%")
                    ) {
                        StandaloneColumn(
                            modifier = StandaloneModifier().gap(12.px)
                        ) {
                            StandaloneText(
                                text = "Predefined Themes",
                                modifier = StandaloneModifier()
                                    .fontSize(20.px)
                                    .fontWeight(StandaloneFontWeight.SemiBold)
                                    .marginBottom(8.px)
                            ) +
                            
                            ThemeOption("Light", "#f5f5f5", "#333333") +
                            ThemeOption("Dark", "#333333", "#ffffff") +
                            ThemeOption("Blue", "#1976d2", "#ffffff") +
                            ThemeOption("Green", "#4caf50", "#ffffff")
                        }
                    } +
                    
                    // Custom theme builder
                    StandaloneCard(
                        modifier = StandaloneModifier().width("45%")
                    ) {
                        StandaloneColumn(
                            modifier = StandaloneModifier().gap(12.px)
                        ) {
                            StandaloneText(
                                text = "Custom Theme Builder",
                                modifier = StandaloneModifier()
                                    .fontSize(20.px)
                                    .fontWeight(StandaloneFontWeight.SemiBold)
                                    .marginBottom(8.px)
                            ) +
                            
                            StandaloneText("Primary Color:") +
                            StandaloneBox(
                                modifier = StandaloneModifier()
                                    .backgroundColor("#1976d2")
                                    .height(40.px)
                                    .borderRadius(4.px)
                                    .border("1px", StandaloneBorderStyle.Solid, "#ddd")
                            ) { "" } +
                            
                            StandaloneText("Background Color:") +
                            StandaloneBox(
                                modifier = StandaloneModifier()
                                    .backgroundColor("#ffffff")
                                    .height(40.px)
                                    .borderRadius(4.px)
                                    .border("1px", StandaloneBorderStyle.Solid, "#ddd")
                            ) { "" } +
                            
                            StandaloneText("Text Color:") +
                            StandaloneBox(
                                modifier = StandaloneModifier()
                                    .backgroundColor("#333333")
                                    .height(40.px)
                                    .borderRadius(4.px)
                                    .border("1px", StandaloneBorderStyle.Solid, "#ddd")
                            ) { "" } +
                            
                            StandaloneButton(
                                label = "Apply Custom Theme",
                                modifier = StandaloneModifier()
                                    .backgroundColor("#ff9800")
                                    .color("white")
                                    .padding(10.px, 20.px)
                                    .borderRadius(4.px)
                                    .cursor(StandaloneCursor.Pointer)
                                    .marginTop(8.px)
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
    @Path("/api/current-time")
    @Produces(MediaType.TEXT_PLAIN)
    fun getCurrentTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    }

    // Helper functions

    private fun createMainAppPage(title: String, includeHtmx: Boolean = false, content: () -> String): String {
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
                        padding: 0 20px;
                    }
                </style>
                $htmxScript
            </head>
            <body>
                ${createNavigation()}
                <div class="container">
                    ${content()}
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun createNavigation(): String {
        return StandaloneBox(
            modifier = StandaloneModifier()
                .backgroundColor("#1976d2")
                .color("white")
                .padding(12.px, 0.px)
                .marginBottom(0.px)
        ) {
            StandaloneBox(
                modifier = StandaloneModifier()
                    .maxWidth("1200px")
                    .margin("0 auto")
                    .padding(0.px, 20.px)
            ) {
                StandaloneRow(
                    modifier = StandaloneModifier()
                        .justifyContent(StandaloneJustifyContent.SpaceBetween)
                        .alignItems(StandaloneAlignItems.Center)
                ) {
                    StandaloneText(
                        text = "Summon + Quarkus Demo",
                        modifier = StandaloneModifier()
                            .fontSize(20.px)
                            .fontWeight(StandaloneFontWeight.Bold)
                    ) +
                    
                    StandaloneRow(
                        modifier = StandaloneModifier().gap(20.px)
                    ) {
                        NavLink("Home", "/") +
                        NavLink("Users", "/users") +
                        NavLink("Dashboard", "/dashboard") +
                        NavLink("Contact", "/contact") +
                        NavLink("Theme", "/theme") +
                        NavLink("Standalone Demo", "/standalone")
                    }
                }
            }
        }
    }

    private fun NavLink(text: String, href: String): String {
        return StandaloneBox(
            modifier = StandaloneModifier()
                .color("white")
                .textDecoration("none")
                .padding(8.px, 12.px)
                .borderRadius(4.px)
                .cursor(StandaloneCursor.Pointer)
                .onClick("window.location.href='$href'")
                .style("text-decoration", "none")
                .style(":hover", "background-color: rgba(255,255,255,0.1)")
        ) {
            StandaloneText(text)
        }
    }

    private fun FeatureCard(title: String, description: String, icon: String, color: String): String {
        return StandaloneCard(
            modifier = StandaloneModifier()
                .width("32%")
                .textAlign(StandaloneTextAlign.Center)
                .padding(20.px)
        ) {
            StandaloneColumn(
                modifier = StandaloneModifier().gap(8.px)
            ) {
                StandaloneText(
                    text = icon,
                    modifier = StandaloneModifier()
                        .fontSize(48.px)
                        .marginBottom(8.px)
                ) +
                StandaloneText(
                    text = title,
                    modifier = StandaloneModifier()
                        .fontSize(18.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .color(color)
                        .marginBottom(4.px)
                ) +
                StandaloneText(
                    text = description,
                    modifier = StandaloneModifier()
                        .fontSize(14.px)
                        .color("#666666")
                )
            }
        }
    }

    private fun StatCard(title: String, value: String, icon: String, color: String): String {
        return StandaloneCard(
            modifier = StandaloneModifier()
                .width("24%")
                .padding(20.px)
                .textAlign(StandaloneTextAlign.Center)
        ) {
            StandaloneColumn(
                modifier = StandaloneModifier().gap(8.px)
            ) {
                StandaloneText(
                    text = icon,
                    modifier = StandaloneModifier()
                        .fontSize(36.px)
                        .marginBottom(8.px)
                ) +
                StandaloneText(
                    text = value,
                    modifier = StandaloneModifier()
                        .fontSize(24.px)
                        .fontWeight(StandaloneFontWeight.Bold)
                        .color(color)
                ) +
                StandaloneText(
                    text = title,
                    modifier = StandaloneModifier()
                        .fontSize(14.px)
                        .color("#666666")
                )
            }
        }
    }

    private fun ActivityItem(text: String, time: String, icon: String): String {
        return StandaloneRow(
            modifier = StandaloneModifier()
                .alignItems(StandaloneAlignItems.Center)
                .gap(12.px)
                .padding(8.px, 0.px)
                .border("0 0 1px 0", StandaloneBorderStyle.Solid, "#eee")
        ) {
            StandaloneText(
                text = icon,
                modifier = StandaloneModifier().fontSize(20.px)
            ) +
            StandaloneColumn(
                modifier = StandaloneModifier().width("70%")
            ) {
                StandaloneText(
                    text = text,
                    modifier = StandaloneModifier().fontSize(14.px)
                )
            } +
            StandaloneText(
                text = time,
                modifier = StandaloneModifier()
                    .fontSize(12.px)
                    .color("#999999")
                    .textAlign(StandaloneTextAlign.Right)
            )
        }
    }

    private fun ContactInfoItem(icon: String, label: String, value: String): String {
        return StandaloneRow(
            modifier = StandaloneModifier()
                .alignItems(StandaloneAlignItems.Center)
                .gap(8.px)
                .marginBottom(8.px)
        ) {
            StandaloneText(
                text = icon,
                modifier = StandaloneModifier().fontSize(18.px)
            ) +
            StandaloneColumn {
                StandaloneText(
                    text = label,
                    modifier = StandaloneModifier()
                        .fontSize(12.px)
                        .color("#666666")
                        .fontWeight(StandaloneFontWeight.SemiBold)
                ) +
                StandaloneText(
                    text = value,
                    modifier = StandaloneModifier().fontSize(14.px)
                )
            }
        }
    }

    private fun ThemeOption(name: String, bgColor: String, textColor: String): String {
        return StandaloneRow(
            modifier = StandaloneModifier()
                .alignItems(StandaloneAlignItems.Center)
                .gap(12.px)
                .marginBottom(8.px)
                .padding(8.px)
                .borderRadius(4.px)
                .cursor(StandaloneCursor.Pointer)
                .style(":hover", "background-color: #f5f5f5")
        ) {
            StandaloneBox(
                modifier = StandaloneModifier()
                    .width(40.px)
                    .height(30.px)
                    .backgroundColor(bgColor)
                    .borderRadius(4.px)
                    .border("1px", StandaloneBorderStyle.Solid, "#ddd")
            ) { "" } +
            StandaloneText(
                text = name,
                modifier = StandaloneModifier()
                    .fontSize(16.px)
                    .fontWeight(StandaloneFontWeight.Medium)
            )
        }
    }

    private fun getRoleColor(role: String): String {
        return when (role.lowercase()) {
            "admin" -> "#f44336"
            "editor" -> "#ff9800"
            "user" -> "#4caf50"
            else -> "#2196f3"
        }
    }

    // Sample data class for users
    data class SampleUser(
        val id: Long,
        val name: String,
        val email: String,
        val role: String,
        val active: Boolean
    )
}

// Extension function for CSS hover states (simulated)
private fun StandaloneModifier.textDecoration(value: String): StandaloneModifier = style("text-decoration", value)