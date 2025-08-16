package code.yousef.example.portfolio.routes

import code.yousef.example.portfolio.models.PortfolioRepository
import code.yousef.example.portfolio.components.*
import code.yousef.example.portfolio.theme.ElegantTheme
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.ThemeProvider
import code.yousef.summon.components.display.*
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.navigation.*
import code.yousef.summon.components.style.*
import code.yousef.summon.effects.*
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.*
import code.yousef.summon.state.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.portfolioRoutes() {
    val renderer by inject<PlatformRenderer>()
    val portfolioRepository by inject<PortfolioRepository>()
    
    get("/") {
        val html = renderer.renderComposableRoot {
            ElegantSinglePage(portfolioRepository)
        }
        call.respondText(html, io.ktor.http.ContentType.Text.Html)
    }
}

@Composable
private fun ElegantSinglePage(portfolioRepository: PortfolioRepository) {
    ThemeProvider(theme = ElegantTheme.toTheme()) {
        GlobalStyle(
            css = """
                @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
                
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                html {
                    scroll-behavior: smooth;
                }
                
                body {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    background: ${ElegantTheme.WHITE};
                    color: ${ElegantTheme.NAVY_DARK};
                    overflow-x: hidden;
                }
                
                /* Custom scrollbar */
                ::-webkit-scrollbar {
                    width: 12px;
                }
                
                ::-webkit-scrollbar-track {
                    background: ${ElegantTheme.BLUE_50};
                }
                
                ::-webkit-scrollbar-thumb {
                    background: ${ElegantTheme.BLUE_300};
                    border-radius: 6px;
                }
                
                ::-webkit-scrollbar-thumb:hover {
                    background: ${ElegantTheme.BLUE_400};
                }
                
                /* Smooth scroll snap for sections */
                .section {
                    scroll-margin-top: 80px;
                }
            """.trimIndent()
        )
        
        Box(
            modifier = Modifier()
                .style("min-height", "100vh")
                .style("position", "relative")
        ) {
            // Navigation Bar
            NavigationBar()
            
            // Main Content
            Column(
                modifier = Modifier()
                    .style("padding-top", "80px")
            ) {
                // Hero Section
                HeroSection()
                
                // About Section
                AboutSection()
                
                // Work Section
                val projects = portfolioRepository.getPublishedProjects()
                WorkSection(projects)
                
                // Journal Section
                val journalEntries = portfolioRepository.getPublishedJournalEntries()
                JournalSection(journalEntries)
                
                // Contact Section
                ContactSection()
            }
            
            // Footer
            Footer()
        }
    }
}