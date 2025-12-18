package portfolio.ui.sections

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.components.navigation.AnchorLink
import codes.yousef.summon.components.navigation.LinkNavigationMode
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.gap

import portfolio.theme.PortfolioTheme

/**
 * Footer section with copyright and links.
 */
@Composable
fun Footer() {
    Box(
        modifier = Modifier()
            .width(100.percent)
            .style("border-top", "1px solid ${PortfolioTheme.Colors.BORDER}")
            .marginTop(PortfolioTheme.Spacing.xxl)
    ) {
        Box(
            modifier = Modifier()
                .maxWidth(1200.px)
                .width(100.percent)
                .margin("0 auto")
                .padding(PortfolioTheme.Spacing.xl, PortfolioTheme.Spacing.lg)
        ) {
            Column(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(PortfolioTheme.Spacing.lg)
            ) {
                // Tech stack callout
                Text(
                    text = "Built with Kotlin, Summon, Materia & Sigil",
                    modifier = Modifier()
                        .fontSize("0.9rem")
                        .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                )

                Row(
                    modifier = Modifier()
                        .display(Display.Flex)
                        .alignItems(AlignItems.Center)
                        .justifyContent(JustifyContent.SpaceBetween)
                        .width(100.percent)
                        .flexWrap(FlexWrap.Wrap)
                        .gap(PortfolioTheme.Spacing.md)
                ) {
                    // Copyright
                    Text(
                        text = "© 2025 Portfolio. All rights reserved.",
                        modifier = Modifier()
                            .fontSize("0.85rem")
                            .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    )

                    // Source link
                    AnchorLink(
                        label = "View Source",
                        href = "https://github.com/codeyousef/summon",
                        modifier = Modifier()
                            .color(PortfolioTheme.Colors.LINK)
                            .textDecoration(TextDecoration.None)
                            .fontSize("0.85rem")
                            .hover(
                                Modifier()
                                    .color(PortfolioTheme.Colors.LINK_HOVER)
                                    .textDecoration(TextDecoration.Underline)
                            ),
                        navigationMode = LinkNavigationMode.Native,
                        target = "_blank",
                        rel = "noopener noreferrer",
                        title = null,
                        id = null,
                        ariaLabel = null,
                        ariaDescribedBy = null,
                        dataHref = null,
                        dataAttributes = emptyMap()
                    )
                }
            }
        }
    }
}
