package portfolio.ui.sections

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.gap

import portfolio.data.Project
import portfolio.theme.PortfolioTheme
import portfolio.ui.components.ProjectCard

/**
 * Projects section displaying featured and all projects.
 */
@Composable
fun ProjectsSection(projects: List<Project>) {
    SectionContainer(id = "projects") {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.xl)
                .width(100.percent)
        ) {
            SectionTitle("Projects")

            // Project grid
            Box(
                modifier = Modifier()
                    .display(Display.Grid)
                    .style("grid-template-columns", "repeat(auto-fill, minmax(320px, 1fr))")
                    .gap(PortfolioTheme.Spacing.lg)
                    .width(100.percent)
            ) {
                projects.forEach { project ->
                    ProjectCard(project)
                }
            }
        }
    }
}

/**
 * Reusable section container.
 */
@Composable
fun SectionContainer(
    id: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier()
            .id(id)
            .width(100.percent)
            .maxWidth(1200.px)
            .margin("0 auto")
            .padding(PortfolioTheme.Spacing.xxl, PortfolioTheme.Spacing.lg)
    ) {
        content()
    }
}

/**
 * Reusable section title.
 */
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier()
            .fontSize("2rem")
            .fontWeight("700")
            .color(PortfolioTheme.Colors.TEXT_PRIMARY)
            .marginBottom(PortfolioTheme.Spacing.lg)
    )
}
