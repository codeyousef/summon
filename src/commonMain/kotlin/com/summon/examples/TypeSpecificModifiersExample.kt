package com.summon.examples

import com.summon.*

/**
 * This example demonstrates the usage of type-specific modifiers
 * for different types of components.
 */
class TypeSpecificModifiersExample {

    /**
     * Example of using type-specific modifiers for a Text component.
     * Shows how to use TextModifier specific functions.
     */
    fun textExample() {
        val text = Text(
            text = "This is a text with specific text modifiers",
            modifier = Modifier()
                .then(Modifier(mapOf("padding" to "16px")))
                .background("#f0f0f0")
                .text() // Convert to TextModifier
                .ellipsis() // Text-specific modifier
                .lineHeight("1.5") // Text-specific modifier
                .textAlign("center") // Text-specific modifier
                .toModifier() // Convert back to base Modifier
                .border("1px", "solid", "#ccc")
        )

        // Usage with the text modifier directly in a chain
        val anotherText = Text(
            text = "Another text example",
            modifier = Modifier()
                .then(Modifier(mapOf("padding" to "8px")))
                .text()
                .textTransform("uppercase")
                .toModifier()
        )
    }

    /**
     * Example of using type-specific modifiers for a TextField component.
     * Shows how to use InputModifier specific functions.
     */
    fun textFieldExample() {
        val nameState = mutableStateOf("")

        val textField = TextField(
            state = nameState,
            label = "Name",
            placeholder = "Enter your name",
            modifier = Modifier()
                .then(Modifier(mapOf("padding" to "16px")))
                .width("300px")
                .input() // Convert to InputModifier
                .placeholderColor("#aaaaaa") // Input-specific modifier
                .focusBorderColor("#3f51b5") // Input-specific modifier
                .toModifier() // Convert back to base Modifier
                .then(Modifier(mapOf("margin" to "8px")))
        )

        // Disabled TextField example
        val disabledTextField = TextField(
            state = mutableStateOf("Read-only value"),
            modifier = Modifier()
                .then(Modifier(mapOf("padding" to "16px")))
                .input()
                .disabled() // Input-specific modifier
                .toModifier()
        )
    }

    /**
     * Example of using type-specific modifiers for a Grid component.
     * Shows how to use LayoutModifier and ScrollableModifier specific functions.
     */
    fun gridExample() {
        val grid = Grid(
            content = listOf(
                Text("Item 1"),
                Text("Item 2"),
                Text("Item 3"),
                Text("Item 4")
            ),
            columns = "repeat(2, 1fr)",
            gap = "16px",
            modifier = Modifier()
                .then(Modifier(mapOf("padding" to "16px")))
                .height("400px")
                .layout() // Convert to LayoutModifier
                .gridTemplateColumns("repeat(auto-fill, minmax(200px, 1fr))") // Layout-specific modifier
                .gridGap("20px") // Layout-specific modifier
                .toModifier() // Convert back to base Modifier
                .border("1px", "solid", "#eee")
                .scrollable() // Convert to ScrollableModifier
                .verticalScroll() // Scrollable-specific modifier
                .toModifier() // Convert back to base Modifier
        )
    }

    /**
     * Example of using type-specific modifiers for an Image component.
     * Shows how to use MediaModifier specific functions.
     */
    fun imageExample() {
        val image = Image(
            src = "https://example.com/profile.jpg",
            alt = "User profile image",
            modifier = Modifier()
                .width("200px")
                .height("200px")
                .media() // Convert to MediaModifier
                .preserveAspectRatio() // Media-specific modifier
                .objectPosition("center") // Media-specific modifier
                .toModifier() // Convert back to base Modifier
        )
    }

    /**
     * This example demonstrates combining different type-specific modifiers
     * by converting between them when needed.
     */
    fun combinedExample() {
        // First create a base modifier with common styles
        val baseModifier = Modifier()
            .then(Modifier(mapOf("padding" to "16px")))
            .background("#ffffff")
            .border("1px", "solid", "#dddddd")

        // Create a scrollable container with a maximum height
        val container = Box(
            modifier = baseModifier
                .height("500px")
                .scrollable() // Convert to ScrollableModifier
                .verticalScroll() // Scrollable-specific modifier
                .toModifier(), // Convert back to base Modifier
            content = listOf(
                // Text with text-specific modifiers
                Text(
                    text = "This is a heading",
                    modifier = Modifier()
                        .then(Modifier(mapOf("padding" to "16px")))
                        .text() // Convert to TextModifier
                        .textAlign("center") // Text-specific modifier
                        .lineHeight("1.4") // Text-specific modifier
                        .textTransform("uppercase") // Text-specific modifier
                        .toModifier() // Convert back to base Modifier
                        .fontSize("24px")
                ),

                // Form with input fields
                Form(
                    content = listOf(
                        TextField(
                            state = mutableStateOf(""),
                            label = "Username",
                            modifier = Modifier()
                                .then(Modifier(mapOf("margin" to "8px 0")))
                                .input() // Convert to InputModifier
                                .placeholderColor("#999999") // Input-specific modifier
                                .focusBorderColor("#4285f4") // Input-specific modifier
                                .toModifier() // Convert back to base Modifier
                        ),
                        TextField(
                            state = mutableStateOf(""),
                            label = "Password",
                            type = TextFieldType.Password,
                            modifier = Modifier()
                                .then(Modifier(mapOf("margin" to "8px 0")))
                                .input() // Convert to InputModifier
                                .placeholderColor("#999999") // Input-specific modifier
                                .focusBorderColor("#4285f4") // Input-specific modifier
                                .toModifier() // Convert back to base Modifier
                        )
                    ),
                    onSubmit = {},
                    modifier = Modifier().then(Modifier(mapOf("padding" to "16px")))
                ),

                // Grid with layout-specific modifiers
                Grid(
                    content = listOf(
                        Text("Item 1"),
                        Text("Item 2"),
                        Text("Item 3"),
                        Text("Item 4")
                    ),
                    columns = "1fr 1fr",
                    gap = "16px",
                    modifier = Modifier()
                        .then(Modifier(mapOf("margin" to "16px 0")))
                        .layout() // Convert to LayoutModifier
                        .gridTemplateColumns("repeat(auto-fill, minmax(150px, 1fr))") // Layout-specific modifier
                        .gridGap("16px") // Layout-specific modifier
                        .toModifier() // Convert back to base Modifier
                )
            )
        )
    }
} 