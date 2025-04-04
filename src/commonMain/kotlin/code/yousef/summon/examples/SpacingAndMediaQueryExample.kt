package code.yousef.summon.examples

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Grid
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.theme.MediaQuery
import code.yousef.summon.theme.Spacing
import code.yousef.summon.theme.createSpacer
import code.yousef.summon.theme.responsive
import code.yousef.summon.theme.spacingPadding

/**
 * Example showing how to use the Spacing and MediaQuery systems together.
 * This example creates a responsive card that adapts to different screen sizes.
 */
fun createResponsiveCard(): Card {
    // Create a card with responsive styling using MediaQuery and Spacing
    return Card(
        modifier = Modifier()
            .maxWidth("800px")
            .background("#ffffff")
            .spacingPadding(Spacing.md)
            .borderRadius("8px")
            .shadow()
            // Apply different padding based on screen size
            .responsive(
                MediaQuery.mobile(
                    Modifier()
                        .spacingPadding(Spacing.sm)
                        .maxWidth("100%")
                ),
                MediaQuery.tablet(
                    Modifier()
                        .spacingPadding(Spacing.md)
                        .maxWidth("600px")
                ),
                MediaQuery.desktop(
                    Modifier()
                        .spacingPadding(Spacing.lg)
                )
            ),
        content = listOf(
            // Card title with responsive font size
            Text(
                text = "Responsive Card Example",
                modifier = Modifier()
                    .fontSize("24px")
                    .fontWeight("bold")
                    .marginBottom(Spacing.md)
                    // Change font size based on screen size
                    .responsive(
                        MediaQuery.mobile(
                            Modifier().fontSize("18px")
                        )
                    )
            ),

            // Card content with consistent spacing
            Text(
                text = "This card demonstrates how to use the Spacing and MediaQuery systems together to create responsive layouts.",
                modifier = Modifier()
                    .marginBottom(Spacing.md)
            ),

            // Use a spacer with consistent spacing
            createSpacer(Spacing.sm),

            // Responsive link instead of button
            Link(
                text = "Click Me",
                href = "#",
                modifier = Modifier()
                    .spacingPadding(
                        top = Spacing.sm,
                        right = Spacing.md,
                        bottom = Spacing.sm,
                        left = Spacing.md
                    )
                    .borderRadius("4px")
                    .background("#4CAF50")
                    .color("#ffffff")
                    .then(Modifier(mapOf("display" to "inline-block", "text-decoration" to "none")))
                    // Make the link full width on mobile
                    .responsive(
                        MediaQuery.mobile(
                            Modifier()
                                .fillMaxWidth()
                                .then(Modifier(mapOf("text-align" to "center")))
                        )
                    )
            )
        )
    )
}

/**
 * Example of creating a responsive layout with consistent spacing throughout.
 */
fun createResponsiveLayout(): Column {
    return Column(
        modifier = Modifier()
            .fillMaxWidth()
            .spacingPadding(Spacing.md)
            // Adjust padding based on screen size
            .responsive(
                MediaQuery.mobile(
                    Modifier().spacingPadding(Spacing.sm)
                ),
                MediaQuery.desktop(
                    Modifier().spacingPadding(Spacing.lg)
                )
            ),
        content = listOf(
            // Header with responsive text size
            Text(
                text = "Responsive Layout Example",
                modifier = Modifier()
                    .fontSize("32px")
                    .fontWeight("bold")
                    .marginBottom(Spacing.lg)
                    .responsive(
                        MediaQuery.mobile(
                            Modifier()
                                .fontSize("24px")
                                .marginBottom(Spacing.md)
                        )
                    )
            ),

            // Create a responsive grid that changes columns based on screen size
            Grid(
                content = listOf(
                    // Add three cards to the grid
                    createResponsiveCard(),
                    createResponsiveCard(),
                    createResponsiveCard()
                ),
                columns = "repeat(3, 1fr)",
                gap = Spacing.md,
                modifier = Modifier()
                    .fillMaxWidth()
                    // On tablet: 2 columns
                    .responsive(
                        MediaQuery.tablet(
                            Modifier(
                                mapOf(
                                    "grid-template-columns" to "repeat(2, 1fr)",
                                    "gap" to Spacing.sm
                                )
                            )
                        )
                    )
                    // On mobile: 1 column
                    .responsive(
                        MediaQuery.mobile(
                            Modifier(
                                mapOf(
                                    "grid-template-columns" to "1fr",
                                    "gap" to Spacing.sm
                                )
                            )
                        )
                    )
            ),

            // Add consistent spacing between sections
            createSpacer(Spacing.lg),

            // Footer with consistent spacing
            Text(
                text = "© 2023 Summon Example",
                modifier = Modifier()
                    .then(Modifier(mapOf("text-align" to "center")))
                    .marginTop(Spacing.lg)
                    .fontSize("14px")
                    .color("#666666")
            )
        )
    )
} 