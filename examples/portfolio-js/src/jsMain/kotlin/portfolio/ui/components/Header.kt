package portfolio.ui.components

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.components.navigation.AnchorLink
import codes.yousef.summon.components.navigation.LinkNavigationMode
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.gap
import portfolio.theme.PortfolioTheme

/**
 * Fixed header navigation bar.
 */
@Composable
fun Header() {
    Box(
        modifier = Modifier()
            .width(100.percent)
            .backgroundColor(PortfolioTheme.Colors.SURFACE)
            .padding(PortfolioTheme.Spacing.md)
            .position(Position.Fixed)
            .style("top", "0")
            .style("left", "0")
            .style("right", "0")
            .zIndex(50)
            .style("backdrop-filter", "blur(12px)")
    ) {
        Row(
            modifier = Modifier()
                .maxWidth(1200.px)
                .width(100.percent)
                .margin("0 auto")
                .display(Display.Flex)
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.SpaceBetween)
        ) {
            // Logo/Name
            Text(
                text = "Portfolio",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("700")
                    .color(PortfolioTheme.Colors.TEXT_PRIMARY)
            )

            // Nav Links
            Row(
                modifier = Modifier()
                    .display(Display.Flex)
                    .alignItems(AlignItems.Center)
                    .gap(PortfolioTheme.Spacing.lg)
            ) {
                NavLink("About", "#about")
                NavLink("Projects", "#projects")
                NavLink("Experience", "#experience")
                NavLink("Contact", "#contact")
            }
        }
    }
}

@Composable
private fun NavLink(label: String, href: String) {
    AnchorLink(
        label = label,
        href = href,
        modifier = Modifier()
            .color(PortfolioTheme.Colors.TEXT_SECONDARY)
            .textDecoration(TextDecoration.None)
            .fontSize("0.9rem")
            .fontWeight("500")
            .hover(
                Modifier()
                    .color(PortfolioTheme.Colors.ACCENT)
            ),
        navigationMode = LinkNavigationMode.Client,
        target = null,
        rel = null,
        title = null,
        id = null,
        ariaLabel = null,
        ariaDescribedBy = null,
        dataHref = null,
        dataAttributes = emptyMap()
    )
}
