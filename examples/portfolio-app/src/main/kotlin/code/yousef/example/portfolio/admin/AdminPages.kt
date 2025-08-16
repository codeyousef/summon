package code.yousef.example.portfolio.admin

import code.yousef.example.portfolio.theme.ElegantTheme
import code.yousef.example.portfolio.components.LocalDivider
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.*
import code.yousef.summon.components.feedback.*
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.navigation.*
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.LocalPlatformRenderer

@Composable
fun AdminLoginPage(error: String?) {
    Box(
        modifier = Modifier()
            .style("width", "100%")
            .style("min-height", "100vh")
            .style(
                "background",
                "linear-gradient(135deg, ${ElegantTheme.NAVY_DARK} 0%, ${ElegantTheme.NAVY_MEDIUM} 100%)"
            )
            .style("display", "flex")
            .style("justify-content", "center")
            .style("align-items", "center")
            .style("font-family", "'Inter', -apple-system, BlinkMacSystemFont, sans-serif")
    ) {
        Card(
            modifier = Modifier()
                .style("width", "400px")
                .style("padding", "2rem")
                .style("background", ElegantTheme.WHITE)
                .style("border-radius", "8px")
                .style("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.1)")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", "1.5rem")
            ) {
                Text(
                    text = "Admin Login",
                    modifier = Modifier()
                        .style("font-size", "24px")
                        .style("font-weight", "600")
                        .style("color", ElegantTheme.NAVY_DARK)
                        .style("text-align", "center")
                )

                if (error != null) {
                    Alert(
                        message = error,
                        variant = AlertVariant.ERROR,
                        modifier = Modifier()
                            .style("margin-bottom", "1rem")
                    )
                }

                // Login form
                val renderer = LocalPlatformRenderer.current
                renderer.renderHtml(
                    htmlContent = """
                        <form method="post" action="/admin/login" style="display: flex; flex-direction: column; gap: 1rem;">
                            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                                <label for="username" style="font-size: 14px; font-weight: 500; color: ${ElegantTheme.NAVY_DARK};">Username</label>
                                <input type="text" id="username" name="username" required 
                                    style="padding: 0.75rem; border: 1px solid ${ElegantTheme.BLUE_200}; border-radius: 4px; font-size: 16px;">
                            </div>
                            
                            <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                                <label for="password" style="font-size: 14px; font-weight: 500; color: ${ElegantTheme.NAVY_DARK};">Password</label>
                                <input type="password" id="password" name="password" required 
                                    style="padding: 0.75rem; border: 1px solid ${ElegantTheme.BLUE_200}; border-radius: 4px; font-size: 16px;">
                            </div>
                            
                            <button type="submit" 
                                style="
                                    margin-top: 1rem;
                                    padding: 0.75rem;
                                    background: ${ElegantTheme.ACCENT_BLUE};
                                    color: white;
                                    border: none;
                                    border-radius: 4px;
                                    font-size: 16px;
                                    font-weight: 500;
                                    cursor: pointer;
                                    transition: background 0.2s;
                                "
                                onmouseover="this.style.background = '${ElegantTheme.SAPPHIRE}';"
                                onmouseout="this.style.background = '${ElegantTheme.ACCENT_BLUE}';"
                            >
                                Login
                            </button>
                        </form>
                    """.trimIndent(),
                    modifier = Modifier()
                )

                LocalDivider(
                    modifier = Modifier()
                        .style("margin", "0.5rem 0")
                )

                Link(
                    href = "/",
                    modifier = Modifier()
                        .style("text-align", "center")
                        .style("display", "block")
                        .style("color", ElegantTheme.ACCENT_BLUE)
                        .style("text-decoration", "none")
                        .style("font-size", "14px")
                ) {
                    Text("Back to Portfolio")
                }
            }
        }
    }
}

@Composable
fun AdminDashboardPage() {
    Box(
        modifier = Modifier()
            .style("width", "100%")
            .style("min-height", "100vh")
            .style("background", ElegantTheme.BLUE_50)
            .style("font-family", "'Inter', -apple-system, BlinkMacSystemFont, sans-serif")
    ) {
        // Admin header
        Box(
            modifier = Modifier()
                .style("position", "fixed")
                .style("top", "0")
                .style("left", "0")
                .style("right", "0")
                .style("background", ElegantTheme.NAVY_DARK)
                .style("color", ElegantTheme.WHITE)
                .style("padding", "1rem 2rem")
                .style("z-index", "100")
                .style("box-shadow", "0 2px 10px rgba(0, 0, 0, 0.1)")
        ) {
            Row(
                modifier = Modifier()
                    .style("display", "flex")
                    .style("justify-content", "space-between")
                    .style("align-items", "center")
            ) {
                Text(
                    text = "Portfolio Admin",
                    modifier = Modifier()
                        .style("font-size", "20px")
                        .style("font-weight", "600")
                )

                Link(
                    href = "/admin/logout",
                    modifier = Modifier()
                        .style("color", ElegantTheme.BLUE_100)
                        .style("text-decoration", "none")
                        .style("font-size", "14px")
                        .style("padding", "0.5rem 1rem")
                        .style("border-radius", "4px")
                        .style("transition", "background 0.2s")
                        .style("hover:background", "rgba(255, 255, 255, 0.1)")
                ) {
                    Text("Logout")
                }
            }
        }

        // Main content
        Box(
            modifier = Modifier()
                .style("padding-top", "64px")
                .style("padding", "2rem")
                .style("max-width", "1200px")
                .style("margin", "0 auto")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", "2rem")
            ) {
                Text(
                    text = "Dashboard",
                    modifier = Modifier()
                        .style("font-size", "28px")
                        .style("font-weight", "600")
                        .style("color", ElegantTheme.NAVY_DARK)
                        .style("margin-bottom", "1.5rem")
                )

                // Stats cards
                Row(
                    modifier = Modifier()
                        .style("display", "grid")
                        .style("grid-template-columns", "repeat(auto-fit, minmax(250px, 1fr))")
                        .style("gap", "1.5rem")
                        .style("margin-bottom", "2rem")
                ) {
                    StatCard("Users", "0", ElegantTheme.ACCENT_BLUE)
                    StatCard("Projects", "0", ElegantTheme.SAPPHIRE)
                    StatCard("Blog Posts", "0", ElegantTheme.EMERALD)
                    StatCard("Categories", "0", ElegantTheme.AMETHYST)
                }

                // Quick actions
                Card(
                    modifier = Modifier()
                        .style("padding", "1.5rem")
                        .style("background", ElegantTheme.WHITE)
                        .style("border-radius", "8px")
                        .style("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.05)")
                ) {
                    Column(
                        modifier = Modifier()
                            .style("gap", "1rem")
                    ) {
                        Text(
                            text = "Quick Actions",
                            modifier = Modifier()
                                .style("font-size", "18px")
                                .style("font-weight", "600")
                                .style("color", ElegantTheme.NAVY_DARK)
                                .style("margin-bottom", "0.5rem")
                        )

                        Link(
                            href = "/admin/projects/new",
                            modifier = Modifier()
                                .style("display", "inline-block")
                                .style("padding", "0.75rem 1.5rem")
                                .style("background", ElegantTheme.ACCENT_BLUE)
                                .style("color", ElegantTheme.WHITE)
                                .style("text-decoration", "none")
                                .style("border-radius", "4px")
                                .style("font-weight", "500")
                                .style("transition", "background 0.2s")
                        ) {
                            Text("Add New Project")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: String) {
    Card(
        modifier = Modifier()
            .style("padding", "1.5rem")
            .style("background", ElegantTheme.WHITE)
            .style("border-radius", "8px")
            .style("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.05)")
            .style("border-top", "3px solid $color")
    ) {
        Column(
            modifier = Modifier()
                .style("gap", "0.5rem")
        ) {
            Text(
                text = title,
                modifier = Modifier()
                    .style("font-size", "16px")
                    .style("color", ElegantTheme.BLUE_200)
            )
            Text(
                text = value,
                modifier = Modifier()
                    .style("font-size", "28px")
                    .style("font-weight", "600")
                    .style("color", ElegantTheme.NAVY_DARK)
            )
        }
    }
}