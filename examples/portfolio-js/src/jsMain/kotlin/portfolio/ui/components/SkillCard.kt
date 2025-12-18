package portfolio.ui.components

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.gap

import portfolio.data.Skill
import portfolio.theme.PortfolioTheme

/**
 * A card component for displaying a skill.
 */
@Composable
fun SkillCard(skill: Skill) {
    Box(
        modifier = Modifier()
            .background(PortfolioTheme.Gradients.GLASS)
            .borderRadius(PortfolioTheme.Radii.md)
            .borderWidth(1)
            .borderStyle(BorderStyle.Solid)
            .borderColor(PortfolioTheme.Colors.BORDER)
            .padding(PortfolioTheme.Spacing.lg)
    ) {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.sm)
        ) {
            Row(
                modifier = Modifier()
                    .display(Display.Flex)
                    .alignItems(AlignItems.Center)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .width(100.percent)
            ) {
                Text(
                    text = skill.name,
                    modifier = Modifier()
                        .fontSize("1.1rem")
                        .fontWeight("600")
                        .color(PortfolioTheme.Colors.TEXT_PRIMARY)
                )
                Text(
                    text = skill.level,
                    modifier = Modifier()
                        .fontSize("0.85rem")
                        .fontWeight("500")
                        .color(PortfolioTheme.Colors.ACCENT)
                )
            }

            // Skill tags
            Row(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(PortfolioTheme.Spacing.xs)
                    .flexWrap(FlexWrap.Wrap)
                    .marginTop(PortfolioTheme.Spacing.xs)
            ) {
                skill.tags.forEach { tag ->
                    Text(
                        text = tag,
                        modifier = Modifier()
                            .fontSize("0.75rem")
                            .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    )
                    // Separator (except for last)
                    if (tag != skill.tags.last()) {
                        Text(
                            text = "·",
                            modifier = Modifier()
                                .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                                .paddingLeft(PortfolioTheme.Spacing.xs)
                                .paddingRight(PortfolioTheme.Spacing.xs)
                        )
                    }
                }
            }
        }
    }
}
