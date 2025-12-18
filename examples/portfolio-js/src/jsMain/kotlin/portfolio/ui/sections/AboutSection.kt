package portfolio.ui.sections

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.gap

import portfolio.data.Skill
import portfolio.theme.PortfolioTheme
import portfolio.ui.components.SkillCard

/**
 * About section with skills.
 */
@Composable
fun AboutSection(skills: List<Skill>) {
    SectionContainer(id = "about") {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.xl)
                .width(100.percent)
        ) {
            SectionTitle("About")

            // Bio text
            Text(
                text = "I'm a software engineer passionate about building elegant, performant applications. " +
                    "I specialize in Kotlin Multiplatform development, creating tools and libraries that help " +
                    "developers build better software faster.",
                modifier = Modifier()
                    .fontSize("1.1rem")
                    .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    .lineHeight("1.7")
                    .maxWidth(700.px)
            )

            // Skills heading
            Text(
                text = "Skills & Technologies",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("600")
                    .color(PortfolioTheme.Colors.TEXT_PRIMARY)
                    .marginTop(PortfolioTheme.Spacing.md)
            )

            // Skills grid
            Box(
                modifier = Modifier()
                    .display(Display.Grid)
                    .style("grid-template-columns", "repeat(auto-fill, minmax(250px, 1fr))")
                    .gap(PortfolioTheme.Spacing.md)
                    .width(100.percent)
            ) {
                skills.forEach { skill ->
                    SkillCard(skill)
                }
            }
        }
    }
}
