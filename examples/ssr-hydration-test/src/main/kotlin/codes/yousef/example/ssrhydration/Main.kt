package codes.yousef.example.ssrhydration

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.navigation.HamburgerMenu
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.respondSummonHydrated
import codes.yousef.summon.integration.ktor.KtorRenderer.Companion.summonStaticAssets
import codes.yousef.summon.modifier.Modifier
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

/**
 * SSR Hydration Test Application
 * 
 * This app tests the hydration system for JVM SSR applications.
 * It includes a hamburger menu that should toggle client-side
 * after the summon-hydration.js script loads.
 */

@Composable
fun App() {
    Column(
        modifier = Modifier()
            .padding("20px")
            .style("width", "100%")
            .style("font-family", "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif")
    ) {
        // Title
        Text(
            text = "SSR Hydration Test App",
            modifier = Modifier()
                .style("font-size", "24px")
                .style("font-weight", "bold")
                .style("margin-bottom", "20px")
        )

        // Hamburger Menu - shows on mobile (viewport width < 768px typically)
        Box(
            modifier = Modifier()
                .style("width", "100%")
                .background("#f0f0f0")
                .borderRadius("8px")
                .padding("10px")
                .style("margin-bottom", "20px")
        ) {
            HamburgerMenu(
                modifier = Modifier()
                    .style("width", "100%"),
                iconColor = "#333"
            ) {
                // Menu content
                Column(
                    modifier = Modifier()
                        .padding("10px")
                        .background("#fff")
                        .borderRadius("4px")
                        .style("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                ) {
                    Text(
                        text = "Menu Item 1",
                        modifier = Modifier()
                            .padding("10px")
                            .style("cursor", "pointer")
                    )
                    Text(
                        text = "Menu Item 2",
                        modifier = Modifier()
                            .padding("10px")
                            .style("cursor", "pointer")
                    )
                    Text(
                        text = "Menu Item 3",
                        modifier = Modifier()
                            .padding("10px")
                            .style("cursor", "pointer")
                    )
                }
            }
        }

        // Info section
        Box(
            modifier = Modifier()
                .style("width", "100%")
                .background("#e8f4e8")
                .borderRadius("8px")
                .padding("20px")
        ) {
            Column {
                Text(
                    text = "Hydration Test Instructions",
                    modifier = Modifier()
                        .style("font-size", "18px")
                        .style("font-weight", "bold")
                        .style("margin-bottom", "10px")
                )
                Text(
                    text = "1. Open browser dev tools and resize to mobile width (< 768px)",
                    modifier = Modifier()
                        .style("margin-bottom", "5px")
                )
                Text(
                    text = "2. Click the hamburger menu icon (☰)",
                    modifier = Modifier()
                        .style("margin-bottom", "5px")
                )
                Text(
                    text = "3. Menu should toggle open/closed without page refresh",
                    modifier = Modifier()
                        .style("margin-bottom", "5px")
                )
                Text(
                    text = "4. Check console for [Summon] log messages",
                    modifier = Modifier()
                )
            }
        }
    }
}

fun Application.configureRoutes() {
    routing {
        // Serve Summon hydration assets (summon-hydration.js, etc.)
        summonStaticAssets()
        
        // Main page with hydration
        get("/") {
            call.respondSummonHydrated {
                App()
            }
        }
    }
}

fun main() {
    println("🚀 Starting SSR Hydration Test App...")
    println("📍 Server will be available at http://localhost:8080")
    println("📱 Resize browser to mobile width to see hamburger menu")
    
    embeddedServer(Netty, port = 8080) {
        configureRoutes()
    }.start(wait = true)
}
