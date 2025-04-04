package com.summon.examples

import com.summon.*

/**
 * Example demonstrating the integration of ColorSystem, Typography, Spacing, and MediaQuery systems.
 * This example creates a responsive web page that uses all the styling systems together.
 */
object IntegratedStylingExample {
    /**
     * Creates the header section of a web page
     */
    private fun createHeader(): Column {
        return Column(
            modifier = Modifier()
                .fillMaxWidth()
                .backgroundColor("primary")
                .spacingPadding(
                    top = Spacing.lg,
                    right = Spacing.lg,
                    bottom = Spacing.lg,
                    left = Spacing.lg
                )
                .responsive(
                    MediaQuery.mobile(
                        Modifier().spacingPadding(Spacing.md)
                    )
                ),
            content = listOf(
                Row(
                    modifier = Modifier()
                        .fillMaxWidth(),
                    content = listOf(
                        // Logo
                        Text(
                            text = "SUMMON",
                            modifier = Typography.h4.applyTo(
                                Modifier()
                                    .textColor("onPrimary")
                                    .fontWeight(Typography.FontWeights.bold)
                            )
                        ),

                        // Spacer to push nav to the right
                        Box(
                            modifier = Modifier()
                                .fillMaxWidth(),
                            content = listOf()
                        ),

                        // Navigation - hidden on mobile
                        Row(
                            modifier = Modifier()
                                .responsive(
                                    MediaQuery.mobile(
                                        Modifier(mapOf("display" to "none"))
                                    )
                                ),
                            content = listOf(
                                navLink("Home"),
                                navLink("Features"),
                                navLink("Documentation"),
                                navLink("Examples")
                            )
                        ),

                        // Mobile menu button - shown only on mobile
                        Box(
                            modifier = Modifier()
                                .padding(Spacing.sm)
                                .responsive(
                                    MediaQuery.tablet(
                                        Modifier(mapOf("display" to "none"))
                                    ),
                                    MediaQuery.desktop(
                                        Modifier(mapOf("display" to "none"))
                                    )
                                ),
                            content = listOf(
                                materialIcon(
                                    name = "menu",
                                    size = "24px",
                                    color = ColorSystem.getColor("onPrimary")
                                )
                            )
                        )
                    )
                ),

                // Hero section
                Column(
                    modifier = Modifier()
                        .spacingMargin(
                            top = Spacing.xl,
                            bottom = Spacing.xl
                        )
                        .responsive(
                            MediaQuery.mobile(
                                Modifier().spacingMargin(
                                    top = Spacing.lg,
                                    bottom = Spacing.lg
                                )
                            )
                        ),
                    content = listOf(
                        h1Text(
                            text = "Build beautiful UIs with Summon",
                            modifier = Modifier()
                                .textColor("onPrimary")
                                .responsive(
                                    MediaQuery.mobile(
                                        Typography.h2.applyTo(Modifier().textColor("onPrimary"))
                                    )
                                )
                        ),

                        typographyText(
                            text = "A Kotlin Multiplatform UI library with a Compose-like API",
                            style = Typography.subtitle,
                            modifier = Modifier()
                                .textColor("onPrimary")
                                .marginTop(Spacing.md)
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates a navigation link
     */
    private fun navLink(text: String): Box {
        return Box(
            modifier = Modifier()
                .padding(
                    top = Spacing.sm,
                    right = Spacing.md,
                    bottom = Spacing.sm,
                    left = Spacing.md
                )
                .hover(
                    Modifier(
                        mapOf(
                            "background-color" to ColorSystem.withAlpha(
                                ColorSystem.getColor("onPrimary"),
                                0.1f
                            ),
                            "border-radius" to "4px"
                        )
                    )
                ),
            content = listOf(
                typographyText(
                    text = text,
                    style = Typography.button,
                    modifier = Modifier()
                        .textColor("onPrimary")
                )
            )
        )
    }

    /**
     * Creates a feature card
     */
    private fun createFeatureCard(title: String, description: String, iconName: String): Card {
        return Card(
            modifier = Modifier()
                .backgroundColor("surface")
                .borderRadius("8px")
                .shadow()
                .responsive(
                    MediaQuery.mobile(
                        Modifier()
                            .marginBottom(Spacing.md)
                            .fillMaxWidth()
                    )
                ),
            content = listOf(
                Box(
                    modifier = Modifier()
                        .padding(Spacing.md)
                        .backgroundColor("primary")
                        .borderRadius("8px 8px 0 0"),
                    content = listOf(
                        materialIcon(
                            name = iconName,
                            size = "32px",
                            color = ColorSystem.getColor("onPrimary")
                        )
                    )
                ),

                Column(
                    modifier = Modifier()
                        .padding(Spacing.md),
                    content = listOf(
                        h4Text(
                            text = title,
                            modifier = Modifier()
                                .textColor("onSurface")
                                .marginBottom(Spacing.sm)
                        ),

                        bodyText(
                            text = description,
                            modifier = Modifier()
                                .textColor("onSurfaceVariant")
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates the features section
     */
    private fun createFeaturesSection(): Column {
        return Column(
            modifier = Modifier()
                .fillMaxWidth()
                .backgroundColor("background")
                .spacingPadding(
                    top = Spacing.xl,
                    right = Spacing.lg,
                    bottom = Spacing.xl,
                    left = Spacing.lg
                )
                .responsive(
                    MediaQuery.mobile(
                        Modifier().spacingPadding(Spacing.md)
                    )
                ),
            content = listOf(
                h2Text(
                    text = "Features",
                    modifier = Modifier()
                        .textColor("onBackground")
                        .marginBottom(Spacing.lg)
                        .text().textAlign("center").toModifier()
                ),

                Grid(
                    columns = "repeat(3, 1fr)",
                    gap = Spacing.lg,
                    modifier = Modifier()
                        .responsive(
                            MediaQuery.tablet(
                                Modifier(mapOf("grid-template-columns" to "repeat(2, 1fr)"))
                            ),
                            MediaQuery.mobile(
                                Modifier(mapOf("grid-template-columns" to "1fr"))
                            )
                        ),
                    content = listOf(
                        createFeatureCard(
                            "Responsive Design",
                            "Create adaptive layouts with MediaQuery that look great on any device size.",
                            "devices"
                        ),

                        createFeatureCard(
                            "Consistent Spacing",
                            "Maintain visual harmony with the Spacing system for predictable layouts.",
                            "space_dashboard"
                        ),

                        createFeatureCard(
                            "Typography Presets",
                            "Use predefined text styles for consistent text appearance across your application.",
                            "text_format"
                        ),

                        createFeatureCard(
                            "Color System",
                            "Implement light and dark themes with semantic color naming for better UX.",
                            "palette"
                        ),

                        createFeatureCard(
                            "Accessibility",
                            "Built-in support for creating accessible web applications.",
                            "accessibility"
                        ),

                        createFeatureCard(
                            "Multi-Platform",
                            "Write once, deploy anywhere with Kotlin Multiplatform.",
                            "devices_other"
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates a testimonial card
     */
    private fun createTestimonial(quote: String, author: String, role: String): Card {
        return Card(
            modifier = Modifier()
                .backgroundColor("surfaceVariant")
                .borderRadius("8px")
                .border("1px", "solid", ColorSystem.getColor("border"))
                .padding(Spacing.lg)
                .responsive(
                    MediaQuery.mobile(
                        Modifier()
                            .marginBottom(Spacing.md)
                            .padding(Spacing.md)
                    )
                ),
            content = listOf(
                Text(
                    text = "\"", // Opening quote mark
                    modifier = Modifier()
                        .fontSize("64px")
                        .color(
                            ColorSystem.withAlpha(
                                ColorSystem.getColor("primary"),
                                0.7f
                            )
                        )
                        .text().lineHeight("0").toModifier()
                        .marginBottom("-10px")
                ),

                typographyText(
                    text = quote,
                    style = Typography.body,
                    modifier = Modifier()
                        .textColor("onSurfaceVariant")
                        .marginBottom(Spacing.md)
                        .then(Modifier(mapOf("font-style" to "italic")))
                ),

                Divider(
                    modifier = Modifier()
                        .marginBottom(Spacing.md)
                        .backgroundColor("divider")
                ),

                bodyText(
                    text = author,
                    modifier = Modifier()
                        .textColor("onSurface")
                        .fontWeight(Typography.FontWeights.bold)
                ),

                captionText(
                    text = role,
                    modifier = Modifier()
                        .textColor("disabled")
                )
            )
        )
    }

    /**
     * Creates the testimonials section
     */
    private fun createTestimonialsSection(): Box {
        return Box(
            modifier = Modifier()
                .fillMaxWidth()
                .backgroundColor("surface")
                .spacingPadding(
                    top = Spacing.xl,
                    right = Spacing.lg,
                    bottom = Spacing.xl,
                    left = Spacing.lg
                )
                .responsive(
                    MediaQuery.mobile(
                        Modifier().spacingPadding(Spacing.md)
                    )
                ),
            content = listOf(
                Column(
                    modifier = Modifier(),
                    content = listOf(
                        h2Text(
                            text = "What Developers Say",
                            modifier = Modifier()
                                .textColor("onSurface")
                                .marginBottom(Spacing.lg)
                                .text().textAlign("center").toModifier()
                        ),

                        Grid(
                            columns = "repeat(2, 1fr)",
                            gap = Spacing.lg,
                            modifier = Modifier()
                                .responsive(
                                    MediaQuery.mobile(
                                        Modifier(mapOf("grid-template-columns" to "1fr"))
                                    )
                                ),
                            content = listOf(
                                createTestimonial(
                                    "Summon has transformed how we build UIs in Kotlin. The consistency between platforms is amazing.",
                                    "Alex Johnson",
                                    "Senior Developer at TechCorp"
                                ),

                                createTestimonial(
                                    "The typography and color systems have saved us countless hours in design implementation and consistency.",
                                    "Maya Rodriguez",
                                    "UI Engineer at DesignWorks"
                                ),

                                createTestimonial(
                                    "I love how Summon makes responsive design so intuitive with the MediaQuery system.",
                                    "Jamal Edwards",
                                    "Freelance Web Developer"
                                ),

                                createTestimonial(
                                    "The spacing system in Summon helped us standardize our layout spacing across our entire application suite.",
                                    "Sarah Chen",
                                    "Product Lead at AppX"
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates a call to action section
     */
    private fun createCallToAction(): Box {
        return Box(
            modifier = Modifier()
                .fillMaxWidth()
                .backgroundColor("primary")
                .spacingPadding(
                    top = Spacing.xl,
                    right = Spacing.lg,
                    bottom = Spacing.xl,
                    left = Spacing.lg
                )
                .responsive(
                    MediaQuery.mobile(
                        Modifier().spacingPadding(Spacing.lg)
                    )
                ),
            content = listOf(
                Column(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .text().textAlign("center").toModifier(),
                    content = listOf(
                        h2Text(
                            text = "Ready to Get Started?",
                            modifier = Modifier()
                                .textColor("onPrimary")
                                .marginBottom(Spacing.md)
                        ),

                        bodyText(
                            text = "Try Summon today and transform how you build UIs with Kotlin.",
                            modifier = Modifier()
                                .textColor("onPrimary")
                                .marginBottom(Spacing.lg)
                        ),

                        Box(
                            modifier = Modifier()
                                .padding(Spacing.md)
                                .backgroundColor("background")
                                .borderRadius("4px")
                                .shadow(),
                            content = listOf(
                                Row(
                                    modifier = Modifier()
                                        .padding(Spacing.md),
                                    content = listOf(
                                        Text(
                                            text = "dependencies {",
                                            modifier = Typography.code.applyTo(
                                                Modifier().textColor("onBackground")
                                            )
                                        )
                                    )
                                ),
                                Row(
                                    modifier = Modifier()
                                        .padding(Spacing.md),
                                    content = listOf(
                                        createSpacer(Spacing.md, false),
                                        Text(
                                            text = "implementation(\"com.summon:core:1.0.0\")",
                                            modifier = Typography.code.applyTo(
                                                Modifier().textColor("primary")
                                            )
                                        )
                                    )
                                ),
                                Row(
                                    modifier = Modifier()
                                        .padding(Spacing.md),
                                    content = listOf(
                                        Text(
                                            text = "}",
                                            modifier = Typography.code.applyTo(
                                                Modifier().textColor("onBackground")
                                            )
                                        )
                                    )
                                )
                            )
                        ),

                        Link(
                            text = "Get Started with Documentation",
                            href = "#documentation",
                            modifier = Modifier()
                                .marginTop(Spacing.lg)
                                .padding(Spacing.md)
                                .backgroundColor("onPrimary")
                                .textColor("primary")
                                .borderRadius("4px")
                                .border("1px", "solid", "transparent")
                                .then(Modifier(mapOf("text-decoration" to "none")))
                                .hover(
                                    Modifier()
                                        .backgroundColor("background")
                                        .border("1px", "solid", ColorSystem.getColor("primary"))
                                )
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates a footer section
     */
    private fun createFooter(): Box {
        return Box(
            modifier = Modifier()
                .fillMaxWidth()
                .backgroundColor("background")
                .spacingPadding(
                    top = Spacing.lg,
                    right = Spacing.lg,
                    bottom = Spacing.lg,
                    left = Spacing.lg
                )
                .responsive(
                    MediaQuery.mobile(
                        Modifier().spacingPadding(Spacing.md)
                    )
                ),
            content = listOf(
                Column(
                    modifier = Modifier()
                        .fillMaxWidth(),
                    content = listOf(
                        Divider(
                            modifier = Modifier()
                                .marginBottom(Spacing.lg)
                                .backgroundColor("divider")
                        ),

                        Row(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .responsive(
                                    MediaQuery.mobile(
                                        Modifier(
                                            mapOf(
                                                "flex-direction" to "column",
                                                "align-items" to "center"
                                            )
                                        )
                                    )
                                ),
                            content = listOf(
                                Column(
                                    modifier = Modifier()
                                        .responsive(
                                            MediaQuery.mobile(
                                                Modifier()
                                                    .marginBottom(Spacing.lg)
                                                    .text().textAlign("center").toModifier()
                                            )
                                        ),
                                    content = listOf(
                                        h5Text(
                                            text = "SUMMON",
                                            modifier = Modifier()
                                                .textColor("primary")
                                                .marginBottom(Spacing.sm)
                                        ),

                                        bodyText(
                                            text = "A Kotlin Multiplatform UI Library",
                                            modifier = Modifier()
                                                .textColor("onSurfaceVariant")
                                        )
                                    )
                                ),

                                Box(
                                    modifier = Modifier()
                                        .fillMaxWidth(),
                                    content = listOf()
                                ),

                                Row(
                                    modifier = Modifier()
                                        .responsive(
                                            MediaQuery.mobile(
                                                Modifier(
                                                    mapOf(
                                                        "justify-content" to "center"
                                                    )
                                                )
                                            )
                                        ),
                                    content = listOf(
                                        Box(
                                            modifier = Modifier()
                                                .padding(Spacing.sm),
                                            content = listOf(
                                                Link(
                                                    text = "GitHub",
                                                    href = "#github",
                                                    modifier = Typography.body.applyTo(
                                                        Modifier().textColor("primary")
                                                    )
                                                )
                                            )
                                        ),

                                        Box(
                                            modifier = Modifier()
                                                .padding(Spacing.sm),
                                            content = listOf(
                                                Link(
                                                    text = "Documentation",
                                                    href = "#docs",
                                                    modifier = Typography.body.applyTo(
                                                        Modifier().textColor("primary")
                                                    )
                                                )
                                            )
                                        ),

                                        Box(
                                            modifier = Modifier()
                                                .padding(Spacing.sm),
                                            content = listOf(
                                                Link(
                                                    text = "Examples",
                                                    href = "#examples",
                                                    modifier = Typography.body.applyTo(
                                                        Modifier().textColor("primary")
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        ),

                        createSpacer(Spacing.lg),

                        captionText(
                            text = "© 2023 Summon. All rights reserved.",
                            modifier = Modifier()
                                .textColor("disabled")
                                .text().textAlign("center").toModifier()
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates a theme toggle button
     */
    private fun createThemeToggle(): Box {
        return Box(
            modifier = Modifier()
                .position("fixed")
                .right("20px")
                .bottom("20px")
                .backgroundColor("primary")
                .borderRadius("50%")
                .size("48px")
                .shadow(),
            content = listOf(
                Box(
                    modifier = Modifier()
                        .fillMaxSize()
                        .then(
                            Modifier(
                                mapOf(
                                    "display" to "flex",
                                    "justify-content" to "center",
                                    "align-items" to "center"
                                )
                            )
                        ),
                    content = listOf(
                        materialIcon(
                            name = "dark_mode",
                            size = "24px",
                            color = ColorSystem.getColor("onPrimary")
                        )
                    )
                )
            )
        )
    }

    /**
     * Creates the full integrated styling showcase
     */
    fun createShowcase(): Column {
        return Column(
            modifier = Modifier()
                .backgroundColor("background"),
            content = listOf(
                createHeader(),
                createFeaturesSection(),
                createTestimonialsSection(),
                createCallToAction(),
                createFooter(),
                createThemeToggle()
            )
        )
    }

    /**
     * Utility function to add position styling to a modifier
     */
    private fun Modifier.position(value: String): Modifier {
        return this.then(Modifier(mapOf("position" to value)))
    }

    /**
     * Utility function to add right positioning to a modifier
     */
    private fun Modifier.right(value: String): Modifier {
        return this.then(Modifier(mapOf("right" to value)))
    }

    /**
     * Utility function to add bottom positioning to a modifier
     */
    private fun Modifier.bottom(value: String): Modifier {
        return this.then(Modifier(mapOf("bottom" to value)))
    }

    /**
     * Utility function to add fill-max-size to a modifier
     */
    private fun Modifier.fillMaxSize(): Modifier {
        return this.then(
            Modifier(
                mapOf(
                    "width" to "100%",
                    "height" to "100%"
                )
            )
        )
    }

    /**
     * Creates a material icon
     */
    private fun materialIcon(name: String, size: String, color: String): Box {
        return Box(
            modifier = Modifier()
                .then(
                    Modifier(
                        mapOf(
                            "font-family" to "'Material Icons'",
                            "font-size" to size,
                            "color" to color,
                            "display" to "flex",
                            "align-items" to "center",
                            "justify-content" to "center",
                            "width" to size,
                            "height" to size
                        )
                    )
                ),
            content = listOf(
                Text(
                    text = name,
                    modifier = Modifier()
                )
            )
        )
    }
} 