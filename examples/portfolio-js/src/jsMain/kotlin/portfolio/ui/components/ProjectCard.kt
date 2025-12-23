package portfolio.ui.components

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

import portfolio.data.Project
import portfolio.theme.PortfolioTheme

/**
 * A card component for displaying a project.
 */
@Composable
fun ProjectCard(project: Project) {
    Box(
        modifier = Modifier()
            .background(PortfolioTheme.Gradients.CARD)
            .borderRadius(PortfolioTheme.Radii.md)
            .borderWidth(1)
            .borderStyle(BorderStyle.Solid)
            .borderColor(PortfolioTheme.Colors.BORDER)
            .padding(PortfolioTheme.Spacing.lg)
            .transition(PortfolioTheme.Motion.DEFAULT)
            .hover(
                Modifier()
                    .borderColor(PortfolioTheme.Colors.ACCENT)
                    .transform(TransformFunction.TranslateY to "-4px")
                    .boxShadow(PortfolioTheme.Shadows.MEDIUM)
            )
    ) {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.md)
        ) {
            // Title with link
            AnchorLink(
                label = project.title,
                href = project.link,
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("700")
                    .color(PortfolioTheme.Colors.TEXT_PRIMARY)
                    .textDecoration(TextDecoration.None)
                    .hover(Modifier().color(PortfolioTheme.Colors.ACCENT)),
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

            // Description
            Text(
                text = project.description,
                modifier = Modifier()
                    .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    .fontSize("0.95rem")
                    .lineHeight("1.6")
            )

            // Tags
            Row(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(PortfolioTheme.Spacing.sm)
                    .flexWrap(FlexWrap.Wrap)
            ) {
                project.tags.forEach { tag ->
                    Tag(tag)
                }
            }
        }
    }
}

@Composable
private fun Tag(label: String) {
    Box(
        modifier = Modifier()
            .backgroundColor(PortfolioTheme.Colors.SURFACE_STRONG)
            .borderRadius(PortfolioTheme.Radii.sm)
            .padding(PortfolioTheme.Spacing.xs, PortfolioTheme.Spacing.sm)
    ) {
        Text(
            text = label,
            modifier = Modifier()
                .fontSize("0.75rem")
                .fontWeight("500")
                .color(PortfolioTheme.Colors.LINK)
        )
    }
}
