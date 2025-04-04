package com.summon.examples

import com.summon.*

/**
 * This class serves as a style guide for using type-specific modifiers
 * with the appropriate component types. It demonstrates best practices
 * for creating, combining, and applying modifiers.
 */
class ModifierStyleGuide {

    /**
     * Demonstrates applying type-specific modifiers to text components.
     */
    fun textComponentStyles() {
        // Basic text component with text-specific modifiers
        val heading = Text(
            text = "Heading Text",
            modifier = Modifier()
                .fontSize("24px")
                .fontWeight("bold")
                .text() // Convert to TextModifier
                .textAlign("center")
                .lineHeight("1.2")
                .toModifier()
                .then(Modifier(mapOf("margin" to "0 0 16px 0")))
        )

        // Link component with text and clickable modifiers
        val link = Link(
            text = "Learn More",
            href = "/details",
            modifier = Modifier()
                .color("#0066cc")
                .text() // Convert to TextModifier
                .then(Modifier(mapOf("text-decoration" to "none")))
                .toModifier()
                .hover(mapOf("color" to "#004499", "text-decoration" to "underline"))
        )

        // Alert with text formatting
        val alert = Alert(
            title = "Warning",
            message = "Your session will expire soon.",
            type = AlertType.WARNING,
            modifier = Modifier()
                .then(Modifier(mapOf("margin" to "16px 0")))
                .text() // Convert to TextModifier
                .ellipsis() // Truncate if too long
                .toModifier()
        )
    }

    /**
     * Demonstrates applying type-specific modifiers to input components.
     */
    fun inputComponentStyles() {
        // Text field with input-specific modifiers
        val nameField = TextField(
            state = mutableStateOf(""),
            label = "Full Name",
            placeholder = "Enter your full name",
            modifier = Modifier()
                .width("100%")
                .then(Modifier(mapOf("margin" to "8px 0")))
                .input() // Convert to InputModifier
                .placeholderColor("#aaaaaa")
                .focusBorderColor("#4285f4")
                .toModifier()
        )

        // Disabled checkbox with input-specific styling
        val checkbox = Checkbox(
            state = mutableStateOf(false),
            label = "I agree to terms",
            modifier = Modifier()
                .then(Modifier(mapOf("margin" to "8px 0")))
                .input() // Convert to InputModifier
                .disabled()
                .toModifier()
        )

        // Select dropdown with validation styling
        val dropdown = Select(
            options = listOf(
                SelectOption("option1", "Option 1"),
                SelectOption("option2", "Option 2"),
                SelectOption("option3", "Option 3")
            ),
            selectedValue = mutableStateOf("option1" as String?),
            onSelectedChange = {},
            label = "Choose an option",
            modifier = Modifier()
                .width("300px")
                .input() // Convert to InputModifier
                .focusBorderColor("#4285f4")
                .toModifier()
                .border("1px", "solid", "#cccccc")
                .borderRadius("4px")
        )
    }

    /**
     * Demonstrates applying type-specific modifiers to layout components.
     */
    fun layoutComponentStyles() {
        // Row with layout-specific modifiers
        val row = Row(
            content = listOf(
                Text("Column 1"),
                Text("Column 2")
            ),
            modifier = Modifier()
                .width("100%")
                .then(Modifier(mapOf("padding" to "16px")))
                .layout() // Convert to LayoutModifier
                .flexGrow(1)
                .toModifier()
                .background("#f5f5f5")
        )

        // Grid with layout-specific modifiers
        val grid = Grid(
            content = listOf(
                Text("Item 1"),
                Text("Item 2"),
                Text("Item 3"),
                Text("Item 4")
            ),
            columns = "repeat(auto-fill, minmax(200px, 1fr))",
            gap = "16px",
            modifier = Modifier()
                .width("100%")
                .layout() // Convert to LayoutModifier
                .gridTemplateColumns("repeat(auto-fill, minmax(200px, 1fr))")
                .gridGap("16px")
                .toModifier()
        )

        // Box with scrollable content
        val scrollableBox = Box(
            content = listOf(
                Text("This is a scrollable container with a lot of content...")
            ),
            modifier = Modifier()
                .height("300px")
                .then(Modifier(mapOf("padding" to "16px")))
                .scrollable() // Convert to ScrollableModifier
                .verticalScroll()
                .toModifier()
                .border("1px", "solid", "#e0e0e0")
                .borderRadius("4px")
        )
    }

    /**
     * Demonstrates applying type-specific modifiers to media components.
     */
    fun mediaComponentStyles() {
        // Image with media-specific modifiers
        val profileImage = Image(
            src = "/images/profile.jpg",
            alt = "User profile",
            modifier = Modifier()
                .width("150px")
                .height("150px")
                .media() // Convert to MediaModifier
                .objectFit("cover")
                .objectPosition("center")
                .toModifier()
                .borderRadius("50%") // Circular image
                .border("3px", "solid", "#ffffff")
        )

        // Icon with media-specific filters
        val icon = Icon(
            name = "settings",
            size = "24px",
            modifier = Modifier()
                .color("#555555")
                .media() // Convert to MediaModifier
                .filter("drop-shadow(0 2px 3px rgba(0,0,0,0.2))")
                .toModifier()
        )

        // Progress bar with media filters
        val progress = Progress(
            value = 75,
            type = ProgressType.LINEAR,
            color = "#4CAF50",
            modifier = Modifier()
                .width("100%")
                .height("8px")
                .media() // Convert to MediaModifier
                .filter("brightness(1.05)")
                .toModifier()
                .borderRadius("4px")
        )
    }

    /**
     * Demonstrates combining different type-specific modifiers in complex components.
     */
    fun combinedModifiers() {
        // A card with mixed content requiring different modifier types
        val card = Card(
            content = listOf(
                // Heading with text modifiers
                Text(
                    text = "Card Title",
                    modifier = Modifier()
                        .fontSize("20px")
                        .fontWeight("bold")
                        .text() // Convert to TextModifier
                        .textAlign("center")
                        .toModifier()
                        .then(Modifier(mapOf("margin" to "0 0 16px 0")))
                ),

                // Image with media modifiers
                Image(
                    src = "/images/card-image.jpg",
                    alt = "Card image",
                    modifier = Modifier()
                        .width("100%")
                        .height("200px")
                        .media() // Convert to MediaModifier
                        .objectFit("cover")
                        .toModifier()
                        .then(Modifier(mapOf("margin" to "0 0 16px 0")))
                ),

                // Form field with input modifiers
                TextField(
                    state = mutableStateOf(""),
                    label = "Comment",
                    modifier = Modifier()
                        .width("100%")
                        .input() // Convert to InputModifier
                        .focusBorderColor("#4285f4")
                        .toModifier()
                        .then(Modifier(mapOf("margin" to "8px 0")))
                )
            ),
            modifier = Modifier()
                .width("350px")
                .borderRadius("8px")
                .then(Modifier(mapOf("padding" to "24px")))
                .shadow() // Add a subtle shadow
        )
    }

    /**
     * Best practices for creating and reusing modifiers
     */
    fun modifierBestPractices() {
        // Create reusable base modifiers
        val baseTextModifier = Modifier()
            .fontSize("16px")
            .then(Modifier(mapOf("font-family" to "'Roboto', sans-serif")))
            .color("#333333")

        val baseInputModifier = Modifier()
            .width("100%")
            .then(Modifier(mapOf("margin" to "8px 0")))
            .borderRadius("4px")

        val baseLayoutModifier = Modifier()
            .width("100%")
            .then(Modifier(mapOf("padding" to "16px")))

        // Apply them to components with specific modifiers
        val text = Text(
            text = "Styled Text",
            modifier = baseTextModifier
                .then(Modifier().fontWeight("bold"))
                .text() // Add text-specific modifiers
                .lineHeight("1.5")
                .toModifier()
        )

        val input = TextField(
            state = mutableStateOf(""),
            label = "Input Field",
            modifier = baseInputModifier
                .then(Modifier().border("1px", "solid", "#dddddd"))
                .input() // Add input-specific modifiers
                .focusBorderColor("#4285f4")
                .toModifier()
        )

        val container = Box(
            content = listOf(text, input),
            modifier = baseLayoutModifier
                .then(Modifier().background("#f9f9f9"))
                .layout() // Add layout-specific modifiers
                .flexGrow(1)
                .toModifier()
        )
    }
} 