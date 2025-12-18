package portfolio.ui.sections

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.components.navigation.AnchorLink
import codes.yousef.summon.components.navigation.ButtonLink
import codes.yousef.summon.components.navigation.LinkNavigationMode
import codes.yousef.summon.extensions.percent
import codes.yousef.summon.extensions.px
import codes.yousef.summon.extensions.vw
import codes.yousef.summon.modifier.*
import codes.yousef.summon.modifier.LayoutModifiers.gap

import portfolio.data.Profile
import portfolio.theme.PortfolioTheme

/**
 * Hero section with name, title, and CTAs.
 */
@Composable
fun HeroSection(profile: Profile) {
    Box(
        modifier = Modifier()
            .id("hero")
            .width(100.percent)
            .minHeight("100vh")
            .display(Display.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .padding(PortfolioTheme.Spacing.xxl)
    ) {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.lg)
                .maxWidth(800.px)
                .textAlign(TextAlign.Center)
        ) {
            // Eyebrow
            Text(
                text = "👋 Hello, I'm",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .color(PortfolioTheme.Colors.TEXT_SECONDARY)
            )

            // Name with gradient
            Text(
                text = profile.name,
                modifier = Modifier()
                    .fontSize(cssClamp(48.px, 8.vw, 96.px))
                    .fontWeight("900")
                    .fontFamily(PortfolioTheme.Typography.FONT_SERIF)
                    .backgroundLayers {
                        linearGradient {
                            direction("90deg")
                            colorStop("#ffffff", "0%")
                            colorStop("#aeefff", "100%")
                        }
                    }
                    .backgroundClipText()
                    .color("transparent")
                    .letterSpacing("-0.02em")
            )

            // Title
            Text(
                text = profile.title,
                modifier = Modifier()
                    .fontSize(cssClamp(24.px, 4.vw, 36.px))
                    .fontWeight("600")
                    .color(PortfolioTheme.Colors.TEXT_PRIMARY)
            )

            // Subtitle
            Text(
                text = profile.subtitle,
                modifier = Modifier()
                    .fontSize(cssClamp(16.px, 2.5.vw, 22.px))
                    .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    .lineHeight("1.6")
                    .maxWidth(600.px)
            )

            // CTAs
            Row(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(PortfolioTheme.Spacing.md)
                    .justifyContent(JustifyContent.Center)
                    .marginTop(PortfolioTheme.Spacing.lg)
                    .flexWrap(FlexWrap.Wrap)
            ) {
                // Primary CTA
                ButtonLink(
                    label = "View Projects",
                    href = "#projects",
                    modifier = Modifier()
                        .display(Display.InlineFlex)
                        .alignItems(AlignItems.Center)
                        .justifyContent(JustifyContent.Center)
                        .padding(PortfolioTheme.Spacing.md, PortfolioTheme.Spacing.xl)
                        .borderRadius(PortfolioTheme.Radii.pill)
                        .backgroundColor(PortfolioTheme.Colors.ACCENT)
                        .color("#ffffff")
                        .textDecoration(TextDecoration.None)
                        .fontWeight("600")
                        .transition(PortfolioTheme.Motion.DEFAULT)
                        .hover(
                            Modifier()
                                .backgroundColor(PortfolioTheme.Colors.ACCENT_HOVER)
                                .transform(TransformFunction.TranslateY to "-2px")
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

                // Secondary CTA
                ButtonLink(
                    label = "GitHub",
                    href = profile.github,
                    modifier = Modifier()
                        .display(Display.InlineFlex)
                        .alignItems(AlignItems.Center)
                        .justifyContent(JustifyContent.Center)
                        .padding(PortfolioTheme.Spacing.md, PortfolioTheme.Spacing.xl)
                        .borderRadius(PortfolioTheme.Radii.pill)
                        .borderWidth(1)
                        .borderStyle(BorderStyle.Solid)
                        .borderColor(PortfolioTheme.Colors.TEXT_SECONDARY)
                        .backgroundColor("transparent")
                        .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                        .textDecoration(TextDecoration.None)
                        .fontWeight("600")
                        .transition(PortfolioTheme.Motion.DEFAULT)
                        .hover(
                            Modifier()
                                .borderColor(PortfolioTheme.Colors.ACCENT)
                                .color(PortfolioTheme.Colors.ACCENT)
                        ),
                    navigationMode = LinkNavigationMode.Native,
                    target = "_blank",
                    rel = "noopener",
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

/**
 * CSS clamp helper for fluid typography.
 */
fun cssClamp(min: String, preferred: String, max: String): String =
    "clamp($min, $preferred, $max)"
