package code.yousef.summon

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.modifier.Modifier
import kotlinx.html.*
import kotlinx.html.stream.appendHTML


/**
 * JVM example demonstrating the Summon library.
 */
fun main() {
    // Print the UI example HTML
    val uiExample = createUIExample()
    println("UI EXAMPLE:")
    println(uiExample)

    // Print the form example HTML
    val formExample = createContactForm()
    println("\nFORM EXAMPLE:")
    println(formExample)

    // Print the Text component example HTML
    val textExample = createTextExample()
    println("\nTEXT COMPONENT EXAMPLE:")
    println(textExample)

    // Print the Card component example HTML
    val cardExample = createCardExample()
    println("\nCARD COMPONENT EXAMPLE:")
    println(cardExample)

    // Print the Image component example HTML
    val imageExample = createImageExample()
    println("\nIMAGE COMPONENT EXAMPLE:")
    println(imageExample)

    // Print the Divider component example HTML
    val dividerExample = createDividerExample()
    println("\nDIVIDER COMPONENT EXAMPLE:")
    println(dividerExample)
}

/**
 * Creates a contact form example.
 */
private fun createContactForm(): String {
    // Create a form layout
    @Suppress("DEPRECATION")
    val form = object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#f9f9f9")
                    .padding("20px")
                    .borderRadius("8px")
                    .shadow(),
                content = {
                    Text(
                        "Contact Form",
                        Modifier()
                            .padding("10px")
                            .color("#333")
                            .fontSize("24px")
                            .fontWeight("bold")
                    )
                    Spacer(modifier = createSpacer("20px", true))
                    // Add form fields here if needed
                    Button(
                        onClick = { println("Form submitted!") },
                        label = "Submit",
                        modifier = Modifier()
                            .background("#4CAF50")
                            .color("white")
                            .padding("10px 20px")
                            .borderRadius("4px")
                    )
                }
            )
            return receiver
        }
    }

    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()

    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Contact Form Demo")
            style {
                unsafe {
                    raw(
                        """
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background-color: #f5f5f5;
                            padding: 20px;
                        }
                        """.trimIndent()
                    )
                }
            }
        }
        body {
            div {
                form.compose(this)
            }
        }
    }

    return output.toString()
}

/**
 * Creates a UI example with basic components.
 */
private fun createUIExample(): String {
    // Create a more advanced UI with enhanced styling
    val example = @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)")
                    .padding("20px")
                    .border("1px", "solid", "#ddd")
                    .borderRadius("8px")
                    .shadow(),
                content = {
                    Text(
                        "Hello from Summon UI!",
                        Modifier()
                            .padding("10px")
                            .color("#333")
                            .fontSize("24px")
                            .fontWeight("bold")
                            .hover(mapOf("color" to "#0066cc"))
                    )
                    Spacer(modifier = createSpacer("20px", true))
                    Text(
                        "This is a demo of the enhanced styling capabilities.",
                        Modifier()
                            .padding("10px")
                            .color("#666")
                            .fontSize("16px")
                    )
                    Spacer(modifier = createSpacer("20px", true))
                    Row(
                        modifier = Modifier()
                            .padding("10px")
                            .fillMaxWidth(),
                        content = {
                            Button(
                                onClick = { println("Primary button clicked!") },
                                label = "Primary Button",
                                modifier = Modifier()
                                    .background("#4CAF50")
                                    .color("white")
                                    .padding("10px 20px")
                                    .borderRadius("4px")
                                    .hover(mapOf("background-color" to "#45a049"))
                            )
                            Spacer(modifier = createSpacer("10px", false))
                            Button(
                                onClick = { println("Secondary button clicked!") },
                                label = "Secondary Button",
                                modifier = Modifier()
                                    .background("#f1f1f1")
                                    .color("#333")
                                    .padding("10px 20px")
                                    .border("1px", "solid", "#ccc")
                                    .borderRadius("4px")
                                    .hover(mapOf("background-color" to "#ddd"))
                            )
                        }
                    )
                }
            )
            return receiver
        }
    }

    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()

    // Render the UI to HTML with stylesheet support
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon UI Demo")
            // We'll include some CSS reset styles
            style {
                unsafe {
                    raw(
                        """
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        background-color: #f0f2f5;
                        padding: 20px;
                    }
                    """.trimIndent()
                    )
                }
            }

            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                // Render our UI
                example.compose(this)
            }
        }
    }

    return output.toString()
}

/**
 * Class for creating text component examples.
 */
class TextExample {
    @Suppress("DEPRECATION")
    fun createTextDemo(): code.yousef.summon.core.Composable =
        @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#ffffff")
                    .padding("20px")
                    .borderRadius("8px")
                    .shadow(),
                content = {
                    Text(
                        "Text Component Examples",
                        Modifier()
                            .padding("0 0 20px 0")
                            .color("#333")
                            .fontSize("28px")
                            .fontWeight("bold")
                    )
                    Text(
                        "Regular text with default styling",
                        Modifier()
                            .padding("10px 0")
                    )
                    Text(
                        "Styled text with custom properties",
                        Modifier()
                            .padding("10px 0")
                            .color("#0066cc")
                            .fontSize("18px")
                            .fontWeight("bold")
                    )
                    Text(
                        "Text with hover effect",
                        Modifier()
                            .padding("10px 0")
                            .color("#333")
                            .hover(mapOf("color" to "#e91e63", "text-decoration" to "underline"))
                    )
                }
            )
            return receiver
        }
    }
}

/**
 * Creates a Text component example showcasing the enhanced Text features.
 */
private fun createTextExample(): String {
    // Create a text example using our TextExample class
    val example = TextExample().createTextDemo()

    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()

    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Text Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw(
                        """
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f9f9f9;
                        padding: 20px;
                    }
                    """.trimIndent()
                    )
                }
            }

            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                // Render our example
                example.compose(this)
            }
        }
    }

    return output.toString()
}

/**
 * Card example showcase class.
 */
object CardExample {
    @Suppress("DEPRECATION")
    fun cardLayout(): code.yousef.summon.core.Composable =
        @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#ffffff")
                    .padding("20px")
                    .maxWidth("800px"),
                content = {
                    Text(
                        "Card Component Examples",
                        Modifier()
                            .padding("0 0 20px 0")
                            .color("#333")
                            .fontSize("28px")
                            .fontWeight("bold")
                    )
                    // Basic card examples would go here
                }
            )
            return receiver
        }
    }
}

/**
 * Creates a Card component example showcasing various Card styles and interactions.
 */
private fun createCardExample(): String {
    // Create a card example using our CardExample class
    val example = CardExample.cardLayout()

    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()

    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Card Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw(
                        """
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f9f9f9;
                        padding: 20px;
                    }
                    """.trimIndent()
                    )
                }
            }

            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                // Render our example
                example.compose(this)
            }
        }
    }

    return output.toString()
}

/**
 * Image example showcase object.
 */
object ImageExample {
    @Suppress("DEPRECATION")
    fun basicImage(): code.yousef.summon.core.Composable =
        @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#ffffff")
                    .padding("20px")
                    .borderRadius("8px"),
                content = {
                    Text(
                        "Basic Image Example",
                        Modifier()
                            .padding("0 0 20px 0")
                            .color("#333")
                            .fontSize("20px")
                            .fontWeight("bold")
                    )
                    // Image components would go here
                }
            )
            return receiver
        }
    }

    @Suppress("DEPRECATION")
    fun multipleImages(): code.yousef.summon.core.Composable =
        @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#ffffff")
                    .padding("20px")
                    .borderRadius("8px"),
                content = {
                    Text(
                        "Multiple Images Example",
                        Modifier()
                            .padding("0 0 20px 0")
                            .color("#333")
                            .fontSize("20px")
                            .fontWeight("bold")
                    )
                    // Multiple image examples would go here
                }
            )
            return receiver
        }
    }
}

/**
 * Creates an Image component example showcasing various Image features.
 */
private fun createImageExample(): String {
    // Create a basic image example
    val basicExample = ImageExample.basicImage()
    val multipleImagesExample = ImageExample.multipleImages()

    // Combine both examples in a column
    val example = @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#ffffff")
                    .padding("20px")
                    .borderRadius("8px")
                    .shadow(),
                content = {
                    Text(
                        "Image Component Examples",
                        Modifier()
                            .padding("0 0 20px 0")
                            .color("#333")
                            .fontSize("28px")
                            .fontWeight("bold")
                    )
                    // Manually render both image examples within this content
                    basicExample.compose(receiver)
                    Spacer(modifier = createSpacer("40px", true))
                    multipleImagesExample.compose(receiver)
                }
            )
            return receiver
        }
    }

    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()

    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Image Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw(
                        """
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f5f5f5;
                        padding: 20px;
                    }
                    """.trimIndent()
                    )
                }
            }

            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                style = "max-width: 1200px; margin: 0 auto;"
                // Render our example
                example.compose(this)
            }
        }
    }

    return output.toString()
}

/**
 * Divider example showcase object.
 */
object DividerExample {
    @Suppress("DEPRECATION")
    fun create(): code.yousef.summon.core.Composable =
        @Suppress("DEPRECATION") object : code.yousef.summon.core.Composable {
        override fun <T> compose(receiver: T): T {
            Column(
                modifier = Modifier()
                    .background("#ffffff")
                    .padding("20px")
                    .borderRadius("8px")
                    .shadow(),
                content = {
                    Text(
                        "Divider Component Examples",
                        Modifier()
                            .padding("0 0 20px 0")
                            .color("#333")
                            .fontSize("28px")
                            .fontWeight("bold")
                    )
                    // Divider examples would go here
                }
            )
            return receiver
        }
    }
}

/**
 * Creates a Divider component example showcasing horizontal and vertical dividers.
 */
private fun createDividerExample(): String {
    // Create a divider example using our DividerExample class
    val example = DividerExample.create()

    // Create a StringBuilder to capture the HTML output
    val output = StringBuilder()

    // Render the UI to HTML
    val consumer = output.appendHTML()
    consumer.html {
        head {
            title("Summon Divider Component Demo")
            // Include some CSS reset styles
            style {
                unsafe {
                    raw(
                        """
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background-color: #f5f5f5;
                        padding: 20px;
                    }
                    """.trimIndent()
                    )
                }
            }

            // Add the generated hover styles
            style {
                unsafe {
                    raw(CssClassStore.generateCss())
                }
            }
        }
        body {
            div {
                style = "max-width: 800px; margin: 0 auto;"
                // Render our example
                example.compose(this)
            }
        }
    }

    return output.toString()
}

/**
 * Helper function to create a Spacer with the specified size
 */
private fun createSpacer(size: String, isVertical: Boolean = true): Modifier {
    return if (isVertical) {
        Modifier().height(size)
    } else {
        Modifier().width(size)
    }
} 
