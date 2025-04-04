package com.summon.examples

import com.summon.*

/**
 * This example demonstrates how to use the Theme and StyleSheet components
 * to create consistent and reusable styles across a Summon application.
 */
object ThemeAndStyleSheetExample {
    /**
     * Shows how to set up and use Theme and StyleSheet
     */
    fun setupThemeAndStyles() {
        // 1. Define a custom theme
        val customTheme = Theme.createTheme {
            // Start with the base theme and customize it
            copy(
                colorPalette = ColorSystem.purple,
                customValues = mapOf(
                    "appHeaderHeight" to "64px",
                    "sidebarWidth" to "250px"
                )
            )
        }

        // 2. Set the theme as active
        Theme.setTheme(customTheme)

        // 3. Define reusable styles using StyleSheet
        createStyleSheet {
            // Define a card style
            style("card") {
                this
                    .themeBackgroundColor("surface")
                    .themeBorderRadius("md")
                    .themeElevation("sm")
                    .themePadding("md")
            }

            // Define a header style
            style("cardHeader") {
                this
                    .themeTextStyle("h5")
                    .themeColor("primary")
                    .themeMargin("sm")
            }

            // Define a primary button style
            style("primaryButton") {
                this
                    .themeBackgroundColor("primary")
                    .themeColor("onPrimary")
                    .themeBorderRadius("sm")
                    .themePadding("sm")
                    .cursor("pointer")
            }

            // Define a style that extends another style
            extendStyle("outlinedButton", "primaryButton") {
                this
                    .backgroundColor("transparent")
                    .themeColor("primary")
                    .themeStyleBorder("1px", "solid", "primary")
            }
        }
    }

    /**
     * Demonstrates how to apply styles from the StyleSheet
     */
    fun modifierExamples() {
        // Creating modifiers with theme values
        val cardModifier = Modifier()
            .applyStyle("card")

        val headerModifier = Modifier()
            .applyStyle("cardHeader")

        val buttonModifier = Modifier()
            .applyStyle("primaryButton")

        val outlinedButtonModifier = Modifier()
            .applyStyle("outlinedButton")

        // Directly applying theme values
        val customTextModifier = Modifier()
            .themeColor("secondary")
            .themeTextStyle("bodyLarge")
            .themePadding("md")

        // Using multiple styles
        val combinedModifier = Modifier()
            .applyStyles("primaryButton", "card")

        // Getting theme values for other properties
        val spacing = Theme.getSpacing("md")
        val borderRadius = Theme.getBorderRadius("sm")
        val elevation = Theme.getElevation("md")
        val textStyle = Theme.getTextStyle("body")
        val color = Theme.getColor("primary")

        // Custom theme values
        val headerHeight = Theme.getCustomValue("appHeaderHeight", "60px")
        val sidebarWidth = Theme.getCustomValue("sidebarWidth", "200px")

        // Output some examples to show the use
        println("Theme color value: $color")
        println("Theme spacing value: $spacing")
        println("Theme custom value: $headerHeight")
        println("Primary button modifier: ${buttonModifier.toStyleString()}")
    }
} 