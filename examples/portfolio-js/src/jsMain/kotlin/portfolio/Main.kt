package portfolio

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.styles.GlobalStyle
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.hydrateComposableRoot

import portfolio.data.PortfolioContent
import portfolio.theme.PortfolioTheme
import portfolio.ui.components.Header
import portfolio.ui.effects.AuroraBackground
import portfolio.ui.sections.*

/**
 * Main portfolio application.
 */
@Composable
fun PortfolioApp() {
    // Global styles
    GlobalStyle(
        """
        *, *::before, *::after {
            box-sizing: border-box;
        }
        
        html, body {
            margin: 0;
            padding: 0;
            overflow-x: hidden;
            background-color: ${PortfolioTheme.Colors.BACKGROUND};
        }
        
        html {
            scroll-behavior: smooth;
        }
        
        body {
            font-family: ${PortfolioTheme.Typography.FONT_SANS};
            color: ${PortfolioTheme.Colors.TEXT_PRIMARY};
            line-height: 1.6;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
        }
        
        a {
            color: inherit;
            text-decoration: none;
        }
        
        /* Custom scrollbar */
        ::-webkit-scrollbar {
            width: 8px;
        }
        
        ::-webkit-scrollbar-track {
            background: ${PortfolioTheme.Colors.BACKGROUND};
        }
        
        ::-webkit-scrollbar-thumb {
            background: ${PortfolioTheme.Colors.SURFACE_STRONG};
            border-radius: 4px;
        }
        
        ::-webkit-scrollbar-thumb:hover {
            background: ${PortfolioTheme.Colors.BORDER_STRONG};
        }
        """
    )

    // Main layout with aurora background
    Box(
        modifier = Modifier()
            .minHeight("100vh")
            .backgroundColor("transparent")
            .color(PortfolioTheme.Colors.TEXT_PRIMARY)
            .fontFamily(PortfolioTheme.Typography.FONT_SANS)
            .position(Position.Relative)
    ) {
        // Aurora background effect (fixed, behind everything)
        AuroraBackground()

        // Content layer (above aurora)
        Box(
            modifier = Modifier()
                .position(Position.Relative)
                .zIndex(1)
                .width(100.percent)
        ) {
            // Fixed header
            Header()

            // Main content
            Column(
                modifier = Modifier()
                    .width(100.percent)
                    .paddingTop("72px") // Account for fixed header
            ) {
                HeroSection(PortfolioContent.profile)
                AboutSection(PortfolioContent.skills)
                ProjectsSection(PortfolioContent.projects)
                ExperienceSection(PortfolioContent.experience)
                ContactSection(PortfolioContent.profile)
                Footer()
            }
        }
    }
}

/**
 * Entry point for the JS application.
 */
fun main() {
    hydrateComposableRoot("root") {
        PortfolioApp()
    }
}
