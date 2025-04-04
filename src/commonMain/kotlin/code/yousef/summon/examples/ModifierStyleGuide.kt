package code.yousef.summon.examples

import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.Image
import code.yousef.summon.components.feedback.Alert
import code.yousef.summon.components.feedback.AlertType
import code.yousef.summon.components.feedback.Progress
import code.yousef.summon.components.feedback.ProgressType
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.input.Select
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Grid
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.mutableStateOf

/**
 * This class serves as a style guide for using type-specific modifiers
 * with the appropriate component types. It demonstrates best practices
 * for creating, combining, and applying modifiers.
 */
class ModifierStyleGuide {

    /**
     * Demonstrates applying text-specific modifiers to text components.
     */
    fun textComponentStyles() {
        // Basic text component with text-specific modifiers
        val heading = code.yousef.summon.components.display.Text(
            text = "Heading Text",
            modifier = Modifier()
                .fontSize("24px")
                .fontWeight("bold")
                .style("text-align", "center")
                .style("line-height", "1.2")
                .then(Modifier(mapOf("margin" to "0 0 16px 0")))
        )

        // Link component with text and clickable modifiers
        val link = Link(
            text = "Learn More",
            href = "/details",
            modifier = Modifier()
                .color("#0066cc")
                .then(Modifier(mapOf("text-decoration" to "none")))
                .hover(mapOf("color" to "#004499", "text-decoration" to "underline"))
        )

        // Alert with text formatting
        val alert = Alert(
            title = "Warning",
            message = "Your session will expire soon.",
            type = AlertType.WARNING,
            modifier = Modifier()
                .then(Modifier(mapOf("margin" to "16px 0")))
                .style("white-space", "nowrap")
                .style("overflow", "hidden")
                .style("text-overflow", "ellipsis")
        )
    }

    /**
     * Demonstrates applying input-specific modifiers to input components.
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
                .style("placeholder-color", "#aaaaaa")
                .style("focus-border-color", "#4285f4")
        )

        // Disabled checkbox with input-specific styling
        val checkbox = Checkbox(
            state = mutableStateOf(false),
            label = "I agree to terms",
            modifier = Modifier()
                .then(Modifier(mapOf("margin" to "8px 0")))
                .style("pointer-events", "none")
                .style("opacity", "0.5")
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
                .style("focus-border-color", "#4285f4")
                .border("1px", "solid", "#cccccc")
                .borderRadius("4px")
        )
    }

    /**
     * Demonstrates applying layout-specific modifiers to layout components.
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
                .style("flex-grow", "1")
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
                .style("grid-template-columns", "repeat(auto-fill, minmax(200px, 1fr))")
                .style("grid-gap", "16px")
        )

        // Box with scrollable content
        val scrollableBox = Box(
            content = listOf(
                Text("This is a scrollable container with a lot of content...")
            ),
            modifier = Modifier()
                .height("300px")
                .then(Modifier(mapOf("padding" to "16px")))
                .style("overflow-y", "auto")
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
                .style("object-fit", "cover")
                .borderRadius("50%") // Circular image
                .border("3px", "solid", "#ffffff")
        )

        // Icon with media-specific filters
        val icon = Icon(
            name = "settings",
            size = "24px",
            modifier = Modifier()
                .color("#555555")
                .style("filter", "drop-shadow(0 2px 3px rgba(0,0,0,0.2))")
        )

        // Progress bar with media filters
        val progress = Progress(
            value = 75,
            type = ProgressType.LINEAR,
            color = "#4CAF50",
            modifier = Modifier()
                .width("100%")
                .height("8px")
                .style("filter", "brightness(1.05)")
                .borderRadius("4px")
        )
    }

    /**
     * Demonstrates combining different modifiers in complex components.
     */
    fun combinedModifiers() {
        // A card with mixed content requiring different modifier types
        val card = Card(
            content = listOf(
                // Heading with text modifiers
                code.yousef.summon.components.display.Text(
                    text = "Card Title",
                    modifier = Modifier()
                        .fontSize("20px")
                        .fontWeight("bold")
                        .style("text-align", "center")
                        .then(Modifier(mapOf("margin" to "0 0 16px 0")))
                ),

                // Image with media modifiers
                Image(
                    src = "/images/card-image.jpg",
                    alt = "Card image",
                    modifier = Modifier()
                        .width("100%")
                        .height("200px")
                        .style("object-fit", "cover")
                        .then(Modifier(mapOf("margin" to "0 0 16px 0")))
                ),

                // Form field with input modifiers
                TextField(
                    state = mutableStateOf(""),
                    label = "Comment",
                    modifier = Modifier()
                        .width("100%")
                        .style("focus-border-color", "#4285f4")
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
        val text = code.yousef.summon.components.display.Text(
            text = "Styled Text",
            modifier = baseTextModifier
                .then(Modifier().fontWeight("bold"))
                .style("line-height", "1.5")
        )

        val input = TextField(
            state = mutableStateOf(""),
            label = "Input Field",
            modifier = baseInputModifier
                .then(Modifier().border("1px", "solid", "#dddddd"))
                .style("focus-border-color", "#4285f4")
        )

        val container = Box(
            content = listOf(text, input),
            modifier = baseLayoutModifier
                .then(Modifier().background("#f9f9f9"))
                .style("flex-grow", "1")
        )
    }
} 