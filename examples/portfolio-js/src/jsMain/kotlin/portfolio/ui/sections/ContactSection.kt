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

import portfolio.data.Profile
import portfolio.theme.PortfolioTheme

/**
 * Contact section with email and social links.
 */
@Composable
fun ContactSection(profile: Profile) {
    SectionContainer(id = "contact") {
        Column(
            modifier = Modifier()
                .display(Display.Flex)
                .gap(PortfolioTheme.Spacing.xl)
                .width(100.percent)
                .textAlign(TextAlign.Center)
                .alignItems(AlignItems.Center)
        ) {
            SectionTitle("Get in Touch")

            Text(
                text = "I'm always open to discussing new opportunities, interesting projects, or just having a chat about technology.",
                modifier = Modifier()
                    .fontSize("1.1rem")
                    .color(PortfolioTheme.Colors.TEXT_SECONDARY)
                    .lineHeight("1.6")
                    .maxWidth(600.px)
            )

            // Email button
            AnchorLink(
                label = profile.email,
                href = "mailto:${profile.email}",
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
                    .fontSize("1.1rem")
                    .transition(PortfolioTheme.Motion.DEFAULT)
                    .hover(
                        Modifier()
                            .backgroundColor(PortfolioTheme.Colors.ACCENT_HOVER)
                            .transform(TransformFunction.TranslateY to "-2px")
                    ),
                navigationMode = LinkNavigationMode.Native,
                target = null,
                rel = null,
                title = "Send me an email",
                id = null,
                ariaLabel = null,
                ariaDescribedBy = null,
                dataHref = null,
                dataAttributes = emptyMap()
            )

            // Social links
            Row(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(PortfolioTheme.Spacing.lg)
                    .marginTop(PortfolioTheme.Spacing.lg)
            ) {
                SocialLink("LinkedIn", profile.linkedin)
                SocialLink("Twitter", profile.twitter)
                SocialLink("GitHub", profile.github)
            }
        }
    }
}

@Composable
private fun SocialLink(label: String, href: String) {
    AnchorLink(
        label = label,
        href = href,
        modifier = Modifier()
            .color(PortfolioTheme.Colors.TEXT_SECONDARY)
            .textDecoration(TextDecoration.None)
            .fontWeight("500")
            .transition(PortfolioTheme.Motion.DEFAULT)
            .hover(
                Modifier()
                    .color(PortfolioTheme.Colors.ACCENT)
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
