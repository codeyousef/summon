package portfolio.ui.sections

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px
import codes.yousef.summon.modifier.*

import portfolio.data.Experience
import portfolio.theme.PortfolioTheme

/**
 * Experience section with work history.
 */
@Composable
fun ExperienceSection(experiences: List<Experience>) {
    SectionContainer(id = "experience") {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.xl)
                .width(100.percent)
        ) {
            SectionTitle("Experience")

            Column(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(PortfolioTheme.Spacing.lg)
                    .width(100.percent)
            ) {
                experiences.forEach { experience ->
                    ExperienceCard(experience)
                }
            }
        }
    }
}

@Composable
private fun ExperienceCard(experience: Experience) {
    Box(
        modifier = Modifier()
            .width(100.percent)
            .padding(PortfolioTheme.Spacing.lg)
            .style("border-left", "4px solid ${PortfolioTheme.Colors.ACCENT}")
            .background(PortfolioTheme.Gradients.GLASS)
            .borderRadius("0 ${PortfolioTheme.Radii.md} ${PortfolioTheme.Radii.md} 0")
    ) {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.sm)
        ) {
            // Company and role
            Text(
                text = experience.role,
                modifier = Modifier()
                    .fontSize("1.2rem")
                    .fontWeight("600")
                    .color(PortfolioTheme.Colors.TEXT_PRIMARY)
            )

            Text(
                text = "${experience.company} • ${experience.period}",
                modifier = Modifier()
                    .fontSize("0.95rem")
                    .color(PortfolioTheme.Colors.ACCENT)
                    .fontWeight("500")
            )

            // Description
            Text(
                text = experience.description,
                modifier = Modifier()
                    .fontSize("1rem")
                    .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    .lineHeight("1.6")
                    .marginTop(PortfolioTheme.Spacing.xs)
            )
        }
    }
}
