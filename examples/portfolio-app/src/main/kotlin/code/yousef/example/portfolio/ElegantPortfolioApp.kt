package code.yousef.example.portfolio

import code.yousef.example.portfolio.models.*
import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.display.*
import code.yousef.summon.components.input.*
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.navigation.*
import code.yousef.summon.components.style.*
import code.yousef.summon.modifier.*
import code.yousef.summon.state.*
import code.yousef.summon.effects.*
import code.yousef.example.portfolio.components.*
import code.yousef.example.portfolio.theme.ElegantTheme

/**
 * Elegant Portfolio with refined design inspired by Apple and Stripe
 * Built entirely with Summon framework - no manual HTML/CSS/JS
 * 
 * This file contains the main portfolio UI components.
 * The routing is now handled in routes/PortfolioRoutes.kt
 */
    
    
    /**
     * Renders a page with the elegant layout and navigation
     */
    private fun renderPage(currentPath: String, content: @Composable () -> Unit): String {
        return renderer.renderComposableRoot {
            // Add elegant CSS keyframes and global styles
            renderer.renderHtml(
                htmlContent = ElegantTheme.getElegantKeyframes(),
                modifier = Modifier()
            )
            ElegantPortfolioLayout(currentPath, content)
        }
    }
    
    @Composable
    private fun ElegantPortfolioLayout(
        currentPath: String,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = Modifier()
                .style("width", "100%")
                .style("min-height", "100vh")
                .style("background", "linear-gradient(135deg, ${ElegantTheme.NAVY_DARK} 0%, ${ElegantTheme.NAVY_MEDIUM} 100%)")
                .style("color", ElegantTheme.BLUE_50)
                .style("font-family", "'Inter', -apple-system, BlinkMacSystemFont, sans-serif")
                .style("position", "relative")
        ) {
            // Elegant navigation
            ElegantNavigation(currentPath)
            
            // Main content
            Box(
                modifier = Modifier()
                    .style("width", "100%")
                    .style("min-height", "100vh")
            ) {
                content()
            }
        }
    }
}

/**
 * Elegant Navigation Component
 */
@Composable
fun ElegantNavigation(currentPath: String) {
    Box(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "0")
            .style("left", "0")
            .style("right", "0")
            .style("background", "rgba(15, 23, 42, 0.95)")
            .style("backdrop-filter", "blur(20px) saturate(1.8)")
            .style("-webkit-backdrop-filter", "blur(20px) saturate(1.8)")
            .style("border-bottom", "1px solid ${ElegantTheme.BLUE_200}")
            .style("z-index", ElegantTheme.Z_NAVIGATION.toString())
    ) {
        Row(
            modifier = Modifier()
                .style("max-width", "1200px")
                .style("margin", "0 auto")
                .style("padding", "${ElegantTheme.SPACING_MD} ${ElegantTheme.SPACING_XXL}")
                .style("display", "flex")
                .style("justify-content", "space-between")
                .style("align-items", "center")
        ) {
            // Logo/Brand
            Text(
                text = "YB",
                modifier = Modifier()
                    .style("font-size", "24px")
                    .style("font-weight", "600")
                    .style("color", ElegantTheme.BLUE_50)
                    .style("letter-spacing", "-1px")
            )
            
            // Navigation items
            Row(
                modifier = Modifier()
                    .style("display", "flex")
                    .style("gap", ElegantTheme.SPACING_LG)
                    .style("align-items", "center")
            ) {
                ElegantNavLink("Home", "#home", currentPath)
                ElegantNavLink("Projects", "#projects", currentPath)
                ElegantNavLink("About", "#about", currentPath)
                ElegantNavLink("Contact", "#contact", currentPath)
                
                // CTA Button
                val renderer = LocalPlatformRenderer.current
                renderer.renderHtml(
                    htmlContent = """
                        <a href="#contact" style="
                            background: ${ElegantTheme.ACCENT_BLUE};
                            color: ${ElegantTheme.WHITE};
                            padding: 10px 20px;
                            border-radius: ${ElegantTheme.RADIUS_SMALL};
                            text-decoration: none;
                            font-weight: 500;
                            font-size: 14px;
                            transition: all ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                            display: inline-block;
                            box-shadow: 0 2px 4px rgba(0, 102, 204, 0.2);
                        "
                        onmouseover="
                            this.style.transform = 'translateY(-1px)';
                            this.style.boxShadow = '0 4px 8px rgba(0, 102, 204, 0.3)';
                        "
                        onmouseout="
                            this.style.transform = 'translateY(0)';
                            this.style.boxShadow = '0 2px 4px rgba(0, 102, 204, 0.2)';
                        ">Get in Touch</a>
                    """.trimIndent(),
                    modifier = Modifier()
                )
            }
        }
    }
}

@Composable
fun ElegantNavLink(text: String, path: String, currentPath: String) {
    val renderer = LocalPlatformRenderer.current
    
    renderer.renderHtml(
        htmlContent = """
            <a href="$path" style="
                color: ${ElegantTheme.BLUE_100};
                text-decoration: none;
                font-weight: 400;
                font-size: 15px;
                transition: color ${ElegantTheme.ANIMATION_SWIFT} ease-out;
                position: relative;
            "
            onmouseover="
                this.style.color = '${ElegantTheme.ACCENT_BLUE}';
            "
            onmouseout="
                this.style.color = '${ElegantTheme.BLUE_100}';
            ">$text</a>
        """.trimIndent(),
        modifier = Modifier()
    )
}

/**
 * Single Page Layout
 */
@Composable
fun ElegantSinglePage() {
    Column(
        modifier = Modifier()
            .style("width", "100%")
            .style("padding-top", "80px") // Account for fixed nav
    ) {
        // Hero section
        Box(
            modifier = Modifier()
                .style("padding", "${ElegantTheme.SPACING_XXXL} ${ElegantTheme.SPACING_XXL}")
                .style("max-width", "1200px")
                .style("margin", "0 auto")
                .style("animation", "fadeIn 0.8s ease-out")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", ElegantTheme.SPACING_XL)
            ) {
                // Headline
                Text(
                    text = "Building the future of development tools",
                    modifier = Modifier()
                        .style("font-size", "72px")
                        .style("font-weight", "200")
                        .style("line-height", "1.1")
                        .style("letter-spacing", "-2px")
                        .style("color", ElegantTheme.BLUE_50)
                        .style("max-width", "900px")
                        .style("margin-bottom", ElegantTheme.SPACING_MD)
                )
                
                // Subtitle
                Text(
                    text = "Creating innovative solutions in UI frameworks, operating systems, and programming languages",
                    modifier = Modifier()
                        .style("font-size", "20px")
                        .style("font-weight", "400")
                        .style("line-height", "1.6")
                        .style("color", ElegantTheme.BLUE_100)
                        .style("max-width", "700px")
                        .style("margin-bottom", ElegantTheme.SPACING_XL)
                )
            }
        }
        
        // Project grid section
        ElegantProjectGrid()
        
        // About section
        ElegantAboutSection()
        
        // Contact section
        ElegantContactSection()
    }
}


/**
 * Elegant Project Grid Section
 */
@Composable
fun ElegantProjectGrid() {
    Box(
        modifier = Modifier()
            .style("background", "transparent")
            .style("padding", "${ElegantTheme.SPACING_XXL} 0")
            .style("margin-top", ElegantTheme.SPACING_MD)
    ) {
        Column(
            modifier = Modifier()
                .style("max-width", "1200px")
                .style("margin", "0 auto")
                .style("padding", "0 ${ElegantTheme.SPACING_XXL}")
        ) {
            // Section header
            Text(
                text = "Projects",
                modifier = Modifier()
                    .style("font-size", "48px")
                    .style("font-weight", "300")
                    .style("color", ElegantTheme.BLUE_50)
                    .style("margin-bottom", ElegantTheme.SPACING_MD)
                    .style("text-align", "center")
            )
            
            Text(
                text = "Innovative solutions that push the boundaries of technology",
                modifier = Modifier()
                    .style("font-size", "18px")
                    .style("color", ElegantTheme.BLUE_100)
                    .style("margin-bottom", ElegantTheme.SPACING_XL)
                    .style("text-align", "center")
                    .style("max-width", "600px")
                    .style("margin-left", "auto")
                    .style("margin-right", "auto")
            )
            
            // Clean project grid
            val renderer = LocalPlatformRenderer.current
            renderer.renderHtml(
                htmlContent = """
                    <div style="
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
                        gap: ${ElegantTheme.SPACING_LG};
                        margin-top: ${ElegantTheme.SPACING_XL};
                    ">
                        <!-- Summon Card -->
                        <div class="project-card" style="
                            background: ${ElegantTheme.NAVY_DARK};
                            border: 1px solid ${ElegantTheme.BLUE_400};
                            border-radius: ${ElegantTheme.RADIUS_LARGE};
                            padding: ${ElegantTheme.SPACING_LG};
                            transition: all ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                            cursor: pointer;
                            box-shadow: ${ElegantTheme.SHADOW_SMALL};
                            text-decoration: none;
                            color: inherit;
                            display: block;
                        " 
                        onclick="window.location.href='/portfolio/summon'"
                        onmouseover="
                            this.style.transform = 'translateY(-8px)';
                            this.style.boxShadow = '${ElegantTheme.SHADOW_XLARGE}';
                            this.style.borderColor = '${ElegantTheme.SAPPHIRE}';
                        "
                        onmouseout="
                            this.style.transform = 'translateY(0)';
                            this.style.boxShadow = '${ElegantTheme.SHADOW_SMALL}';
                            this.style.borderColor = '${ElegantTheme.BLUE_400}';
                        ">
                            <div style="
                                width: 60px;
                                height: 4px;
                                background: ${ElegantTheme.SAPPHIRE};
                                border-radius: 2px;
                                margin-bottom: ${ElegantTheme.SPACING_MD};
                            "></div>
                            <h3 style="
                                font-size: 28px;
                                font-weight: 600;
                                color: ${ElegantTheme.BLUE_50};
                                margin: 0 0 ${ElegantTheme.SPACING_SM} 0;
                            ">Summon</h3>
                            <p style="
                                font-size: 16px;
                                line-height: 1.6;
                                color: ${ElegantTheme.BLUE_100};
                                margin: 0 0 ${ElegantTheme.SPACING_MD} 0;
                            ">Type-safe Kotlin Multiplatform UI Framework that brings declarative UI to multiple platforms with elegant APIs.</p>
                            <div style="
                                display: flex;
                                gap: ${ElegantTheme.SPACING_MD};
                                flex-wrap: wrap;
                            ">
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Kotlin</span>
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Multiplatform</span>
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">UI Framework</span>
                            </div>
                        </div>
                        
                        <!-- HorizonOS Card -->
                        <div class="project-card" style="
                            background: ${ElegantTheme.NAVY_DARK};
                            border: 1px solid ${ElegantTheme.BLUE_400};
                            border-radius: ${ElegantTheme.RADIUS_LARGE};
                            padding: ${ElegantTheme.SPACING_LG};
                            transition: all ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                            cursor: pointer;
                            box-shadow: ${ElegantTheme.SHADOW_SMALL};
                            text-decoration: none;
                            color: inherit;
                            display: block;
                        " 
                        onclick="window.location.href='/portfolio/horizonos'"
                        onmouseover="
                            this.style.transform = 'translateY(-8px)';
                            this.style.boxShadow = '${ElegantTheme.SHADOW_XLARGE}';
                            this.style.borderColor = '${ElegantTheme.EMERALD}';
                        "
                        onmouseout="
                            this.style.transform = 'translateY(0)';
                            this.style.boxShadow = '${ElegantTheme.SHADOW_SMALL}';
                            this.style.borderColor = '${ElegantTheme.BLUE_400}';
                        ">
                            <div style="
                                width: 60px;
                                height: 4px;
                                background: ${ElegantTheme.EMERALD};
                                border-radius: 2px;
                                margin-bottom: ${ElegantTheme.SPACING_MD};
                            "></div>
                            <h3 style="
                                font-size: 28px;
                                font-weight: 600;
                                color: ${ElegantTheme.BLUE_50};
                                margin: 0 0 ${ElegantTheme.SPACING_SM} 0;
                            ">HorizonOS</h3>
                            <p style="
                                font-size: 16px;
                                line-height: 1.6;
                                color: ${ElegantTheme.BLUE_100};
                                margin: 0 0 ${ElegantTheme.SPACING_MD} 0;
                            ">Revolutionary graph-based operating system that reimagines how we interact with computing environments.</p>
                            <div style="
                                display: flex;
                                gap: ${ElegantTheme.SPACING_MD};
                                flex-wrap: wrap;
                            ">
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">System Design</span>
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Graph Theory</span>
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Operating System</span>
                            </div>
                        </div>
                        
                        <!-- SeenLang Card -->
                        <div class="project-card" style="
                            background: ${ElegantTheme.NAVY_DARK};
                            border: 1px solid ${ElegantTheme.BLUE_400};
                            border-radius: ${ElegantTheme.RADIUS_LARGE};
                            padding: ${ElegantTheme.SPACING_LG};
                            transition: all ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                            cursor: pointer;
                            box-shadow: ${ElegantTheme.SHADOW_SMALL};
                            text-decoration: none;
                            color: inherit;
                            display: block;
                        " 
                        onclick="window.location.href='/portfolio/seenlang'"
                        onmouseover="
                            this.style.transform = 'translateY(-8px)';
                            this.style.boxShadow = '${ElegantTheme.SHADOW_XLARGE}';
                            this.style.borderColor = '${ElegantTheme.AMETHYST}';
                        "
                        onmouseout="
                            this.style.transform = 'translateY(0)';
                            this.style.boxShadow = '${ElegantTheme.SHADOW_SMALL}';
                            this.style.borderColor = '${ElegantTheme.BLUE_400}';
                        ">
                            <div style="
                                width: 60px;
                                height: 4px;
                                background: ${ElegantTheme.AMETHYST};
                                border-radius: 2px;
                                margin-bottom: ${ElegantTheme.SPACING_MD};
                            "></div>
                            <h3 style="
                                font-size: 28px;
                                font-weight: 600;
                                color: ${ElegantTheme.BLUE_50};
                                margin: 0 0 ${ElegantTheme.SPACING_SM} 0;
                            ">SeenLang</h3>
                            <p style="
                                font-size: 16px;
                                line-height: 1.6;
                                color: ${ElegantTheme.BLUE_100};
                                margin: 0 0 ${ElegantTheme.SPACING_MD} 0;
                            ">Multilingual programming language that breaks down language barriers in global software development.</p>
                            <div style="
                                display: flex;
                                gap: ${ElegantTheme.SPACING_MD};
                                flex-wrap: wrap;
                            ">
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Language Design</span>
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Compiler</span>
                                <span style="
                                    font-size: 14px;
                                    color: ${ElegantTheme.BLUE_200};
                                    font-weight: 500;
                                    padding: 4px 12px;
                                    background: ${ElegantTheme.NAVY_MEDIUM};
                                    border-radius: ${ElegantTheme.RADIUS_SMALL};
                                ">Multilingual</span>
                            </div>
                        </div>
                    </div>
                """.trimIndent(),
                modifier = Modifier()
            )
        }
    }
}

/**
 * Elegant Project Card
 */
@Composable
fun ElegantProjectCard(
    title: String,
    description: String,
    metrics: List<String>,
    color: String
) {
    val renderer = LocalPlatformRenderer.current
    val cardId = "card-${title.replace(" ", "-").lowercase()}"
    
    renderer.renderHtml(
        htmlContent = """
            <div id="$cardId" style="
                background: ${ElegantTheme.WHITE};
                border: 1px solid ${ElegantTheme.BLUE_200};
                border-radius: ${ElegantTheme.RADIUS_LARGE};
                padding: ${ElegantTheme.SPACING_LG};
                transition: all ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                cursor: pointer;
                box-shadow: ${ElegantTheme.SHADOW_SMALL};
            "
            onmouseover="
                this.style.transform = 'translateY(-6px)';
                this.style.boxShadow = '${ElegantTheme.SHADOW_XLARGE}';
                this.style.borderColor = '$color';
            "
            onmouseout="
                this.style.transform = 'translateY(0)';
                this.style.boxShadow = '${ElegantTheme.SHADOW_SMALL}';
                this.style.borderColor = '${ElegantTheme.BLUE_200}';
            ">
                <div style="display: flex; flex-direction: column; gap: ${ElegantTheme.SPACING_MD};">
                    <!-- Color accent bar -->
                    <div style="
                        width: 60px;
                        height: 4px;
                        background: $color;
                        border-radius: 2px;
                        margin-bottom: ${ElegantTheme.SPACING_SM};
                        transition: width ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                    " id="${cardId}-accent"></div>
                    
                    <h3 style="
                        font-size: 24px;
                        font-weight: 600;
                        color: ${ElegantTheme.BLUE_50};
                        margin: 0;
                    ">$title</h3>
                    
                    <p style="
                        font-size: 16px;
                        line-height: 1.6;
                        color: ${ElegantTheme.BLUE_100};
                        margin: 0;
                    ">$description</p>
                    
                    <div style="
                        display: flex;
                        gap: ${ElegantTheme.SPACING_MD};
                        margin-top: ${ElegantTheme.SPACING_SM};
                    ">
                        ${metrics.joinToString("") { metric ->
                            """<span style="
                                font-size: 14px;
                                color: ${ElegantTheme.BLUE_200};
                                font-weight: 500;
                            ">$metric</span>"""
                        }}
                    </div>
                </div>
            </div>
            <script>
                document.getElementById('$cardId').addEventListener('mouseenter', function() {
                    document.getElementById('${cardId}-accent').style.width = '100px';
                });
                document.getElementById('$cardId').addEventListener('mouseleave', function() {
                    document.getElementById('${cardId}-accent').style.width = '60px';
                });
            </script>
        """.trimIndent(),
        modifier = Modifier()
    )
}

/**
 * About Section
 */
@Composable
fun ElegantAboutSection() {
    Box(
        modifier = Modifier()
            .style("background", "transparent")
            .style("padding", "${ElegantTheme.SPACING_XXL} 0")
            .style("margin-top", ElegantTheme.SPACING_MD)
    ) {
        Column(
            modifier = Modifier()
                .style("max-width", "800px")
                .style("margin", "0 auto")
                .style("padding", "0 ${ElegantTheme.SPACING_XXL}")
                .style("text-align", "center")
        ) {
            Text(
                text = "About",
                modifier = Modifier()
                    .style("font-size", "48px")
                    .style("font-weight", "300")
                    .style("color", ElegantTheme.BLUE_50)
                    .style("margin-bottom", ElegantTheme.SPACING_MD)
            )
            
            Text(
                text = "I'm a developer passionate about creating tools that empower other developers. My work spans across UI frameworks, operating systems, and programming languages, always with a focus on simplicity, elegance, and performance.",
                modifier = Modifier()
                    .style("font-size", "20px")
                    .style("color", ElegantTheme.BLUE_100)
                    .style("margin-bottom", ElegantTheme.SPACING_LG)
                    .style("line-height", "1.8")
            )
            
            Text(
                text = "Each project represents a step towards a more intuitive and powerful development ecosystem. From Summon's type-safe UI components to HorizonOS's revolutionary graph-based architecture, and SeenLang's multilingual approach to programming, I strive to challenge conventions and push boundaries.",
                modifier = Modifier()
                    .style("font-size", "20px")
                    .style("color", ElegantTheme.BLUE_100)
                    .style("line-height", "1.8")
            )
        }
    }
}

/**
 * Elegant Contact Section
 */
@Composable
fun ElegantContactSection() {
    Box(
        modifier = Modifier()
            .style("background", "transparent")
            .style("padding", "${ElegantTheme.SPACING_XXL} 0")
            .style("margin-top", ElegantTheme.SPACING_MD)
    ) {
        Column(
            modifier = Modifier()
                .style("max-width", "600px")
                .style("margin", "0 auto")
                .style("padding", "0 ${ElegantTheme.SPACING_XXL}")
                .style("text-align", "center")
        ) {
            Text(
                text = "Let's Connect",
                modifier = Modifier()
                    .style("font-size", "48px")
                    .style("font-weight", "300")
                    .style("color", ElegantTheme.BLUE_50)
                    .style("margin-bottom", ElegantTheme.SPACING_MD)
            )
            
            Text(
                text = "Have a project in mind? I'd love to hear from you.",
                modifier = Modifier()
                    .style("font-size", "18px")
                    .style("color", ElegantTheme.BLUE_100)
                    .style("margin-bottom", ElegantTheme.SPACING_XL)
            )
            
            val renderer = LocalPlatformRenderer.current
            renderer.renderHtml(
                htmlContent = """
                    <a href="mailto:yousef@example.com" style="
                        display: inline-block;
                        background: ${ElegantTheme.ACCENT_BLUE};
                        color: ${ElegantTheme.WHITE};
                        padding: 16px 32px;
                        border-radius: ${ElegantTheme.RADIUS_MEDIUM};
                        text-decoration: none;
                        font-weight: 500;
                        font-size: 16px;
                        transition: all ${ElegantTheme.ANIMATION_SMOOTH} ease-out;
                        box-shadow: ${ElegantTheme.SHADOW_MEDIUM};
                    "
                    onmouseover="
                        this.style.transform = 'translateY(-2px)';
                        this.style.boxShadow = '${ElegantTheme.SHADOW_LARGE}';
                    "
                    onmouseout="
                        this.style.transform = 'translateY(0)';
                        this.style.boxShadow = '${ElegantTheme.SHADOW_MEDIUM}';
                    ">Get in Touch</a>
                """.trimIndent(),
                modifier = Modifier()
            )
        }
    }
}

