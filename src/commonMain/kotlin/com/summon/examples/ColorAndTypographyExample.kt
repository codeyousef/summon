package com.summon.examples

import com.summon.*

/**
 * Example demonstrating the ColorSystem and Typography components.
 */
object ColorAndTypographyExample {
    /**
     * Creates a styled heading section with different typography presets
     */
    fun createTypographyShowcase(): Column {
        return Column(
            modifier = Modifier()
                .padding("24px")
                .backgroundColor("background")
                .spacingPadding(Spacing.lg),
            content = listOf(
                // Header section with theme-aware styling
                Text(
                    text = "Typography Showcase",
                    modifier = Modifier()
                        .textColor("primary")
                        .marginBottom("24px")
                        .fontSize("32px")
                        .fontWeight("bold")
                ),

                // Typography presets
                h1Text("Heading 1", Modifier().marginBottom(Spacing.md).textColor("onBackground")),
                h2Text("Heading 2", Modifier().marginBottom(Spacing.md).textColor("onBackground")),
                h3Text("Heading 3", Modifier().marginBottom(Spacing.md).textColor("onBackground")),
                h4Text("Heading 4", Modifier().marginBottom(Spacing.md).textColor("onBackground")),
                h5Text("Heading 5", Modifier().marginBottom(Spacing.md).textColor("onBackground")),
                h6Text("Heading 6", Modifier().marginBottom(Spacing.md).textColor("onBackground")),

                Divider(
                    modifier = Modifier()
                        .marginTop(Spacing.md)
                        .marginBottom(Spacing.md)
                        .backgroundColor("divider")
                ),

                typographyText(
                    text = "Subtitle style for introductory text",
                    style = Typography.subtitle,
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("onSurfaceVariant")
                ),

                bodyText(
                    text = "This is the default body text style used for most content. It provides optimal readability with a balanced line height and font size.",
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("onSurface")
                ),

                typographyText(
                    text = "Body large text for emphasizing important paragraphs",
                    style = Typography.bodyLarge,
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("onSurface")
                ),

                typographyText(
                    text = "Body small text is great for secondary information that needs less prominence",
                    style = Typography.bodySmall,
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("onSurfaceVariant")
                ),

                captionText(
                    text = "Caption text used for image captions and supplementary information",
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("disabled")
                ),

                typographyText(
                    text = "OVERLINE TEXT USED ABOVE CONTENT",
                    style = Typography.overline,
                    modifier = Modifier()
                        .marginBottom(Spacing.sm)
                        .textColor("primary")
                ),

                buttonText(
                    text = "Button Text Style",
                    modifier = Modifier()
                        .padding(Spacing.sm)
                        .backgroundColor("primary")
                        .textColor("onPrimary")
                        .borderRadius("4px")
                ),

                createSpacer(Spacing.md),

                typographyText(
                    text = "This is a link style text",
                    style = Typography.link,
                    modifier = Modifier()
                        .textColor("primary")
                        .then(Modifier(mapOf("text-decoration" to "underline")))
                ),

                createSpacer(Spacing.md),

                codeText(
                    text = "fun main() { println(\"Hello, World!\") }",
                    modifier = Modifier()
                        .padding(Spacing.md)
                        .backgroundColor("surfaceVariant")
                        .borderRadius("4px")
                )
            )
        )
    }

    /**
     * Creates a color system showcase displaying the various color palettes
     */
    fun createColorShowcase(): Column {
        return Column(
            modifier = Modifier()
                .padding("24px")
                .backgroundColor("background"),
            content = listOf(
                Text(
                    text = "Color System Showcase",
                    modifier = Modifier()
                        .textColor("primary")
                        .marginBottom("24px")
                        .fontSize("32px")
                        .fontWeight("bold")
                ),

                // Light mode section
                h3Text(
                    text = "Light Mode Colors",
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("onBackground", ColorSystem.ThemeMode.LIGHT)
                ),

                createColorPalette(ColorSystem.ThemeMode.LIGHT),

                createSpacer(Spacing.lg),

                // Dark mode section
                Box(
                    modifier = Modifier()
                        .padding(Spacing.md)
                        .backgroundColor("background", ColorSystem.ThemeMode.DARK),
                    content = listOf(
                        Column(
                            modifier = Modifier()
                                .padding(Spacing.md),
                            content = listOf(
                                h3Text(
                                    text = "Dark Mode Colors",
                                    modifier = Modifier()
                                        .marginBottom(Spacing.md)
                                        .textColor("onBackground", ColorSystem.ThemeMode.DARK)
                                ),

                                createColorPalette(ColorSystem.ThemeMode.DARK)
                            )
                        )
                    )
                ),

                createSpacer(Spacing.lg),

                // Theme color schemes
                h3Text(
                    text = "Color Schemes",
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .textColor("onBackground")
                ),

                createColorSchemeButtons()
            )
        )
    }

    /**
     * Creates a grid showing the color palette for a specific theme mode
     */
    private fun createColorPalette(mode: ColorSystem.ThemeMode): Grid {
        val colorCards = mutableListOf<Composable>()

        // Background colors
        colorCards.add(createColorCard("background", mode))
        colorCards.add(createColorCard("surface", mode))
        colorCards.add(createColorCard("surfaceVariant", mode))

        // Text colors
        colorCards.add(createColorCard("onBackground", mode))
        colorCards.add(createColorCard("onSurface", mode))
        colorCards.add(createColorCard("onSurfaceVariant", mode))

        // Brand colors
        colorCards.add(createColorCard("primary", mode))
        colorCards.add(createColorCard("primaryVariant", mode))
        colorCards.add(createColorCard("onPrimary", mode))
        colorCards.add(createColorCard("secondary", mode))
        colorCards.add(createColorCard("secondaryVariant", mode))
        colorCards.add(createColorCard("onSecondary", mode))

        // Status colors
        colorCards.add(createColorCard("success", mode))
        colorCards.add(createColorCard("error", mode))
        colorCards.add(createColorCard("warning", mode))
        colorCards.add(createColorCard("info", mode))

        return Grid(
            content = colorCards,
            columns = "repeat(auto-fill, minmax(220px, 1fr))",
            gap = Spacing.md
        )
    }

    /**
     * Creates a card displaying a color sample with its name and value
     */
    private fun createColorCard(colorName: String, mode: ColorSystem.ThemeMode): Card {
        val colorValue = ColorSystem.getColor(colorName, mode)
        val textColor = if (colorName.startsWith("on") || colorName == "background" || colorName == "surface") {
            // Use contrasting color for text when the background is light or needs visibility
            ColorSystem.getColor("primary", mode)
        } else {
            ColorSystem.getColor("onPrimary", mode)
        }

        return Card(
            modifier = Modifier()
                .backgroundColor(colorName, mode)
                .borderRadius("4px")
                .border("1px", "solid", ColorSystem.getColor("border", mode)),
            content = listOf(
                Column(
                    modifier = Modifier()
                        .padding(Spacing.md),
                    content = listOf(
                        Text(
                            text = colorName,
                            modifier = Modifier()
                                .color(textColor)
                                .fontWeight("bold")
                                .marginBottom(Spacing.sm)
                        ),
                        Text(
                            text = colorValue,
                            modifier = Modifier()
                                .color(textColor)
                                .fontSize("14px")
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates buttons showing different color schemes
     */
    private fun createColorSchemeButtons(): Row {
        return Row(
            modifier = Modifier()
                .fillMaxWidth()
                .flexWrap("wrap"),
            content = listOf(
                // Default theme button
                createSchemeButton("Default Theme", "primary", "onPrimary"),

                // Blue theme button
                createSchemeButton(
                    "Blue Theme",
                    ColorSystem.blue.light["primary"] ?: "#2196f3",
                    ColorSystem.blue.light["onPrimary"] ?: "#ffffff"
                ),

                // Green theme button
                createSchemeButton(
                    "Green Theme",
                    ColorSystem.green.light["primary"] ?: "#4caf50",
                    ColorSystem.green.light["onPrimary"] ?: "#ffffff"
                ),

                // Purple theme button
                createSchemeButton(
                    "Purple Theme",
                    ColorSystem.purple.light["primary"] ?: "#9c27b0",
                    ColorSystem.purple.light["onPrimary"] ?: "#ffffff"
                )
            )
        )
    }

    /**
     * Creates a button with a specific color scheme
     */
    private fun createSchemeButton(text: String, backgroundColor: String, textColor: String): Box {
        return Box(
            modifier = Modifier()
                .padding(Spacing.md)
                .margin(Spacing.sm)
                .backgroundColor(backgroundColor)
                .color(textColor)
                .borderRadius("4px")
                .padding(Spacing.md),
            content = listOf(
                Text(
                    text = text,
                    modifier = Modifier()
                        .fontWeight("bold")
                )
            )
        )
    }

    /**
     * Creates a complete page showcasing both color system and typography
     */
    fun createShowcase(): Column {
        return Column(
            modifier = Modifier()
                .backgroundColor("background"),
            content = listOf(
                createTypographyShowcase(),
                Divider(
                    modifier = Modifier()
                        .margin("48px 24px")
                        .backgroundColor("divider")
                ),
                createColorShowcase()
            )
        )
    }
} 